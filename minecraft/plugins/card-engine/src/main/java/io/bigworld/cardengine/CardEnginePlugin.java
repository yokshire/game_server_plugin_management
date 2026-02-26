package io.bigworld.cardengine;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class CardEnginePlugin extends JavaPlugin implements CommandExecutor, Listener {
  private static final String[] DEFAULT_SUBSYSTEM_KEYS = {
      "active_effects",
      "stalker",
      "raid",
      "aura",
      "score_decay",
      "border_wither"
  };

  private BukkitTask delegateTask;
  private boolean warnedMissingHook;
  private long lastDelegateErrorEpochSecond;
  private long driverTickCounter;
  private DelegateHooks delegateHooks;
  private final Map<String, Long> splitIntervals = new LinkedHashMap<>();
  private final Map<String, Long> splitOffsets = new LinkedHashMap<>();
  private boolean splitModeSupportedByTarget = true;

  private static final class DelegateHooks {
    private final Plugin target;
    private final Method isActiveMethod;
    private final Method coreTickMethod;
    private final Method subsystemTickMethod;

    private DelegateHooks(Plugin target, Method isActiveMethod, Method coreTickMethod, Method subsystemTickMethod) {
      this.target = target;
      this.isActiveMethod = isActiveMethod;
      this.coreTickMethod = coreTickMethod;
      this.subsystemTickMethod = subsystemTickMethod;
    }
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    PluginCommand command = getCommand("cardengine");
    if (command != null) {
      command.setExecutor(this);
    }
    Bukkit.getPluginManager().registerEvents(this, this);
    rebuildSplitSchedule();
    startDelegateTask();
    getLogger().info(
        "CardEngine enabled. delegate=" + delegateEnabled()
            + ", mode=" + delegateMode()
            + ", target=" + delegateTargetPluginName());
    String summary = fetchTargetImplementationSummary();
    if (summary != null && !summary.isBlank()) {
      getLogger().info("CardEngine target summary: " + summary);
    }
  }

  @Override
  public void onDisable() {
    stopDelegateTask();
    clearHooks();
  }

  @EventHandler
  public void onPluginDisable(PluginDisableEvent event) {
    if (event.getPlugin().getName().equalsIgnoreCase(delegateTargetPluginName())) {
      clearHooks();
    }
  }

  @EventHandler
  public void onPluginEnable(PluginEnableEvent event) {
    if (event.getPlugin().getName().equalsIgnoreCase(delegateTargetPluginName())) {
      clearHooks();
    }
  }

  private void startDelegateTask() {
    stopDelegateTask();
    if (!delegateEnabled()) {
      return;
    }
    driverTickCounter = 0L;
    this.delegateTask = Bukkit.getScheduler().runTaskTimer(this, this::tickDelegate, 1L, 1L);
  }

  private void stopDelegateTask() {
    if (delegateTask != null) {
      delegateTask.cancel();
      delegateTask = null;
    }
  }

  private void tickDelegate() {
    if (!delegateEnabled()) {
      return;
    }

    driverTickCounter++;
    DelegateHooks hooks = resolveHooks();
    if (hooks == null) {
      return;
    }
    if (!isDelegationActiveOnTarget(hooks)) {
      return;
    }

    String mode = delegateMode();
    if ("split".equals(mode) && hooks.subsystemTickMethod != null) {
      tickSplitMode(hooks);
      return;
    }
    tickCoreMode(hooks);
  }

  private void tickCoreMode(DelegateHooks hooks) {
    long interval = delegateIntervalTicks();
    if (!isDue(interval, 0L)) {
      return;
    }
    invokeCoreTick(hooks);
  }

  private void tickSplitMode(DelegateHooks hooks) {
    for (Map.Entry<String, Long> entry : splitIntervals.entrySet()) {
      String key = entry.getKey();
      long interval = entry.getValue();
      long offset = splitOffsets.getOrDefault(key, 0L);
      if (!isDue(interval, offset)) {
        continue;
      }
      invokeSubsystemTick(hooks, key);
    }
  }

  private boolean isDue(long interval, long offset) {
    if (interval <= 0L) {
      return false;
    }
    return ((driverTickCounter + offset) % interval) == 0L;
  }

  private DelegateHooks resolveHooks() {
    if (delegateHooks != null && delegateHooks.target.isEnabled()) {
      return delegateHooks;
    }

    Plugin target = Bukkit.getPluginManager().getPlugin(delegateTargetPluginName());
    if (target == null || !target.isEnabled()) {
      if (delegateStrictTargetRequired()) {
        warnRateLimited("Target plugin not ready: " + delegateTargetPluginName());
      }
      clearHooks();
      return null;
    }

    try {
      Method isActiveMethod = target.getClass().getMethod("isCardEngineDelegationActive");
      Method coreTickMethod = target.getClass().getMethod("runCardEngineCoreTickDelegated");
      Method subsystemTickMethod = null;
      try {
        subsystemTickMethod = target.getClass().getMethod("runCardEngineSubsystemTickDelegated", String.class);
      } catch (NoSuchMethodException ignored) {
        // Old target version: split-mode not available, will fall back to core mode.
      }

      delegateHooks = new DelegateHooks(target, isActiveMethod, coreTickMethod, subsystemTickMethod);
      warnedMissingHook = false;
      splitModeSupportedByTarget = subsystemTickMethod != null;
      return delegateHooks;
    } catch (NoSuchMethodException exception) {
      clearHooks();
      if (!warnedMissingHook) {
        warnedMissingHook = true;
        getLogger().warning(
            "Target plugin does not expose CardEngine hooks: " + delegateTargetPluginName()
                + " (expected isCardEngineDelegationActive/runCardEngineCoreTickDelegated)");
      }
      return null;
    }
  }

  private boolean isDelegationActiveOnTarget(DelegateHooks hooks) {
    try {
      Object activeObj = hooks.isActiveMethod.invoke(hooks.target);
      return (activeObj instanceof Boolean active) && active;
    } catch (ReflectiveOperationException exception) {
      warnRateLimited("CardEngine active check failed: " + exception.getMessage());
      clearHooks();
      return false;
    }
  }

  private void invokeCoreTick(DelegateHooks hooks) {
    try {
      hooks.coreTickMethod.invoke(hooks.target);
    } catch (ReflectiveOperationException exception) {
      warnRateLimited("CardEngine core delegate invocation failed: " + exception.getMessage());
      clearHooks();
    }
  }

  private void invokeSubsystemTick(DelegateHooks hooks, String subsystemKey) {
    if (hooks.subsystemTickMethod == null) {
      invokeCoreTick(hooks);
      return;
    }
    try {
      hooks.subsystemTickMethod.invoke(hooks.target, subsystemKey);
    } catch (ReflectiveOperationException exception) {
      warnRateLimited("CardEngine subsystem delegate invocation failed: key=" + subsystemKey + ", error=" + exception.getMessage());
      clearHooks();
    }
  }

  private void clearHooks() {
    delegateHooks = null;
    splitModeSupportedByTarget = true;
  }

  private void warnRateLimited(String message) {
    long now = nowEpochSecond();
    if ((now - lastDelegateErrorEpochSecond) >= 10L) {
      lastDelegateErrorEpochSecond = now;
      getLogger().warning(message);
    }
  }

  private void rebuildSplitSchedule() {
    splitIntervals.clear();
    splitOffsets.clear();

    int index = 0;
    for (String key : DEFAULT_SUBSYSTEM_KEYS) {
      long interval = Math.max(0L, getConfig().getLong("delegate.split_subsystems." + key, 20L));
      if (interval <= 0L) {
        continue;
      }
      splitIntervals.put(key, interval);
      splitOffsets.put(key, (long) index);
      index++;
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!"cardengine".equalsIgnoreCase(command.getName())) {
      return false;
    }

    String sub = args.length > 0 ? args[0].trim().toLowerCase(Locale.ROOT) : "status";
    if ("reload".equals(sub)) {
      reloadConfig();
      clearHooks();
      rebuildSplitSchedule();
      startDelegateTask();
      sender.sendMessage(
          "[CardEngine] config reloaded. delegate=" + delegateEnabled()
              + ", mode=" + delegateMode()
              + ", target=" + delegateTargetPluginName()
              + ", interval_ticks=" + delegateIntervalTicks());
      return true;
    }

    Plugin target = Bukkit.getPluginManager().getPlugin(delegateTargetPluginName());
    boolean ready = target != null && target.isEnabled();
    String summary = fetchTargetImplementationSummary();
    sender.sendMessage(
        "[CardEngine] delegate=" + delegateEnabled()
            + ", mode=" + delegateMode()
            + ", target=" + delegateTargetPluginName()
            + ", target_ready=" + ready
            + ", interval_ticks=" + delegateIntervalTicks()
            + ", split_supported=" + splitModeSupportedByTarget
            + ", split_schedule=" + splitIntervals
            + (summary == null || summary.isBlank() ? "" : ", target_summary={" + summary + "}"));
    return true;
  }

  private String fetchTargetImplementationSummary() {
    DelegateHooks hooks = resolveHooks();
    if (hooks == null) {
      return null;
    }
    try {
      Method summaryMethod = hooks.target.getClass().getMethod("getCardEngineImplementationSummary");
      Object value = summaryMethod.invoke(hooks.target);
      return value == null ? null : String.valueOf(value);
    } catch (ReflectiveOperationException ignored) {
      return null;
    }
  }

  private boolean delegateEnabled() {
    return getConfig().getBoolean("delegate.enabled", true);
  }

  private String delegateTargetPluginName() {
    String configured = getConfig().getString("delegate.target_plugin", "CardDraw");
    if (configured == null || configured.isBlank()) {
      return "CardDraw";
    }
    return configured.trim();
  }

  private String delegateMode() {
    String configured = getConfig().getString("delegate.mode", "split");
    if (configured == null || configured.isBlank()) {
      return "split";
    }
    String normalized = configured.trim().toLowerCase(Locale.ROOT);
    if (!normalized.equals("split") && !normalized.equals("core")) {
      return "split";
    }
    return normalized;
  }

  private long delegateIntervalTicks() {
    return Math.max(1L, getConfig().getLong("delegate.interval_ticks", 20L));
  }

  private boolean delegateStrictTargetRequired() {
    return getConfig().getBoolean("delegate.strict_target_required", false);
  }

  private long nowEpochSecond() {
    return System.currentTimeMillis() / 1000L;
  }
}
