package io.bigworld.season;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

@SuppressWarnings("deprecation")
public final class SeasonManagerPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
  private static final String ENDER_AURA_TICK_METADATA = "seasonmanager_ender_aura_tick";
  private static final String ENDER_AURA_TAG = "seasonmanager_ender_aura";
  private static final NamespacedKey ENDER_AURA_MAX_HEALTH_MODIFIER_KEY = new NamespacedKey("seasonmanager", "ender_aura_max_health");
  private static final NamespacedKey ENDER_AURA_ATTACK_DAMAGE_MODIFIER_KEY = new NamespacedKey("seasonmanager", "ender_aura_attack_damage");
  private static final String STALKER_TAG = "seasonmanager_stalker";
  private static final String RAID_TAG = "seasonmanager_raid";
  private static final int SLOT_GUI_REROLL_BUTTON_SLOT = 49;
  private static final int SLOT_GUI_PAGE_BUTTON_SLOT = 50;
  private static final int SLOT_GUI_UPGRADE_BUTTON_SLOT = 51;
  private static final int SLOT_GUI_DEBUG_OP_BUTTON_SLOT = 52;
  private static final int SLOT_GUI_PAGE_INFO_SLOT = 45;
  private static final int SLOT_GUI_RESIDUAL_PREV_PAGE_SLOT = 46;
  private static final int SLOT_GUI_RESIDUAL_FILTER_SLOT = 47;
  private static final int SLOT_GUI_RESIDUAL_NEXT_PAGE_SLOT = 48;
  private static final int SLOT_GUI_PAGE_PRIMARY = 1;
  private static final int SLOT_GUI_PAGE_RESIDUAL = 2;
  private static final int SLOT_GUI_RESIDUAL_FILTER_ALL = 0;
  private static final int SLOT_GUI_RESIDUAL_FILTER_TIER_3_PLUS = 1;
  private static final int SLOT_GUI_RESIDUAL_FILTER_EXPIRES_SOON = 2;
  private static final int SLOT_GUI_RESIDUAL_FILTER_MAX = SLOT_GUI_RESIDUAL_FILTER_EXPIRES_SOON;
  private static final int SLOT_GUI_RESIDUAL_EXPIRES_SOON_DAYS = 3;
  private static final int[] SLOT_GUI_BLESSING_DETAIL_BLOCK_STARTS = {9, 18};
  private static final int[] SLOT_GUI_CURSE_DETAIL_BLOCK_STARTS = {27, 36};
  private static final int[] SLOT_GUI_DETAIL_ROW_INFO_SLOTS = {14, 23, 32, 41};
  private static final Pattern DETAIL_ABSOLUTE_TIER_PATTERN = Pattern.compile(
      "^(?<label>.+?):\\s*T1\\s*(?<t1>[+-]?\\d+(?:\\.\\d+)?)%,\\s*"
          + "T2\\s*(?<t2>[+-]?\\d+(?:\\.\\d+)?)%,\\s*"
          + "T3\\s*(?<t3>[+-]?\\d+(?:\\.\\d+)?)%,\\s*"
          + "T4\\s*(?<t4>[+-]?\\d+(?:\\.\\d+)?)%\\s*"
          + "\\(발동 시작 T(?<min>[1-4])\\)\\s*$"
  );
  private static final Pattern DETAIL_GAMBLE_DOUBLE_PATTERN = Pattern.compile(
      "^T(?<tier>[1-4]):\\s*(?<success>\\d+)%\\s*2배\\s*/\\s*(?<fail>\\d+)%\\s*2배\\s*소모\\s*$"
  );
  private static final Pattern DETAIL_GAMBLE_RATIO_PATTERN = Pattern.compile(
      "^T(?<tier>[1-4]):\\s*(?<success>\\d+)\\s*/\\s*(?<fail>\\d+)\\s*$"
  );
  private static final Pattern DETAIL_TIER_GATE_PATTERN = Pattern.compile("T([1-4])\\+");
  private static final Pattern DETAIL_TIER_TOKEN_RUN_PATTERN = Pattern.compile(
      "(?:T[1-4]\\s*(?:(?!T[1-4]\\s)[^,])+\\s*,\\s*)+T[1-4]\\s*(?:(?!T[1-4]\\s)[^,])+"
  );
  private static final Pattern DETAIL_TIER_TOKEN_PATTERN = Pattern.compile(
      "T([1-4])\\s*((?:(?!T[1-4]\\s)[^,])+?)\\s*(?=,\\s*T[1-4]\\s|$)"
  );
  private static final Pattern DETAIL_SINGLE_TIER_PREFIX_PATTERN = Pattern.compile("(^|[:(\\s])T([2-4])\\s+");
  private static final Pattern DETAIL_SLASH_QUAD_PATTERN = Pattern.compile(
      "([+-]?\\d+(?:\\.\\d+)?(?:%p?|%|[A-Za-z가-힣]+)?)\\s*/\\s*"
          + "([+-]?\\d+(?:\\.\\d+)?(?:%p?|%|[A-Za-z가-힣]+)?)\\s*/\\s*"
          + "([+-]?\\d+(?:\\.\\d+)?(?:%p?|%|[A-Za-z가-힣]+)?)\\s*/\\s*"
          + "([+-]?\\d+(?:\\.\\d+)?(?:%p?|%|[A-Za-z가-힣]+)?)"
  );
  private static final Pattern EFFECT80_ID_PATTERN = Pattern.compile("^(?<group>[BCX])-(?<index>\\d{3})(?:-[BC])?$");
  private static final Pattern NUMERIC_POOL_ID_PATTERN = Pattern.compile("^(?<group>[BC])-(?<index>\\d{3})$");
  private static final Pattern EFFECT80_COOLDOWN_PATTERN = Pattern.compile("(?<!\\d)(\\d{1,4})s");
  private static final Pattern DETAIL_SLASH_SERIES_PATTERN = Pattern.compile(
      "[+-]?\\d+(?:\\.\\d+)?(?:[%\\p{L}]+)?(?:/[+-]?\\d+(?:\\.\\d+)?(?:[%\\p{L}]+)?)+"
  );
  private static final Pattern DETAIL_VALUE_WITH_UNIT_PATTERN = Pattern.compile(
      "(?<num>[+-]?\\d+(?:\\.\\d+)?)(?<unit>[%\\p{L}]+)?"
  );
  private static final Pattern SPECIAL_TIER_SECTION_PATTERN = Pattern.compile(
      "T(?<tier>[2-6])(?:\\([^)]*\\))?\\s*:\\s*(?<text>[^|]+)"
  );
  private static final long RHYTHM_COMBO_WINDOW_MILLIS = 2000L;
  private static final int RHYTHM_COMBO_MAX_STACKS = 5;
  private static final double BLESSING_POOL_AGGRESSIVE_SCALE = 1.00D;
  private static final double BLESSING_POOL_GENERAL_SCALE = 1.00D;
  private static final double CURSE_NEGATIVE_SOFTEN_SCALE = 1.00D;
  private static final double CURSE_PLAYER_STAT_REDUCTION_SOFTEN_SCALE = 0.85D;
  private static final double C_MOD_RUNTIME_RATIO_SCALE = 1.18D;
  private static final double X_MOD_CURSE_VARIANT_RUNTIME_RATIO_SCALE = 1.15D;
  private static final int B_MOD_TIER_BONUS = 1;
  private static final double B_MOD_RUNTIME_RATIO_SCALE = 1.15D;
  private static final double B_MOD_COOLDOWN_SCALE = 0.75D;
  private static final double B_MOD_CHANCE_SCALE = 1.20D;
  private static final double B_MOD_CHANCE_FLAT_BONUS = 0.05D;
  private static final double B_MOD_RANGE_SCALE = 1.20D;
  private static final double B_MOD_DURATION_SCALE = 1.20D;
  private static final String INFORMATION_REQUIRED_EFFECT_ID = "B-037";
  private static final Set<String> PROFILE_OVERRIDE_ROOTS = Set.of(
      "lives",
      "score",
      "cards",
      "slots",
      "player",
      "spawn_separation",
      "free_play",
      "climax",
      "border",
      "ender_aura",
      "stalker",
      "dragon_raid",
      "end"
  );

  private final Map<UUID, PlayerRoundData> players = new HashMap<>();
  private final Set<UUID> participants = new HashSet<>();
  private final Map<UUID, VaultSession> openVaultSessions = new HashMap<>();
  private final Map<UUID, Long> borderOutsideSinceEpochSecond = new HashMap<>();
  private final Set<UUID> auraAttributeModifiedMonsters = new HashSet<>();
  private final Set<UUID> stalkerEntities = new HashSet<>();
  private final Deque<UUID> raidEntityOrder = new ArrayDeque<>();
  private final Map<UUID, Double> dragonDamageByPlayer = new HashMap<>();
  private final Set<Particle> warnedParticleFallbacks = new HashSet<>();
  private final Map<String, EffectDefinition> effectDefinitionsById = new HashMap<>();
  private final Map<String, EffectGimmickProfile> effectGimmicksById = new HashMap<>();
  private final List<EffectDefinition> blessingEffectsCatalog = new ArrayList<>();
  private final List<EffectDefinition> curseEffectsCatalog = new ArrayList<>();
  private final Map<String, EffectRuntimeProfile> defaultRuntimeProfiles = new HashMap<>();
  private final Map<UUID, Long> slotRerollCooldownUntilEpochSecond = new HashMap<>();
  private final Map<UUID, Float> runtimeOriginalWalkSpeeds = new HashMap<>();
  private final Map<UUID, Map<Attribute, Double>> runtimeOriginalAttributeBases = new HashMap<>();
  private final Set<String> runtimePotionAdjustBypass = new HashSet<>();
  private final Map<UUID, Deque<PlayerRewindSnapshot>> rewindSnapshotsByPlayer = new HashMap<>();
  private final Map<String, Long> effectCooldownUntilEpochSecond = new HashMap<>();
  private final Map<String, Long> effectCooldownUntilEpochMilli = new HashMap<>();
  private final Map<UUID, Map<Integer, Long>> lockedHotbarSlotUntilEpochSecond = new HashMap<>();
  private final Map<UUID, Long> ghostUntilEpochSecond = new HashMap<>();
  private final Map<UUID, Long> noBuildUntilEpochSecond = new HashMap<>();
  private final Map<UUID, Long> noAttackUntilEpochSecond = new HashMap<>();
  private final Map<UUID, Long> lastCombatEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> buildBurstUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> buildBurstTierByPlayer = new HashMap<>();
  private final Map<UUID, Long> buildBurstFatiguePendingAtEpochSecondByPlayer = new HashMap<>();
  private final Map<String, Long> reinforcedBurstBlocksUntilEpochSecond = new HashMap<>();
  private final Map<UUID, String> bannedToolGroupByPlayer = new HashMap<>();
  private final Map<UUID, Long> bannedToolUntilEpochSecond = new HashMap<>();
  private final Map<UUID, Long> toolBanPendingEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> toolBanPendingTierByPlayer = new HashMap<>();
  private final Map<String, Long> collapsingPlacedBlocksUntilEpochSecond = new HashMap<>();
  private final Set<String> collapsingPlacedBlocksKeepDrop = new HashSet<>();
  private final Map<UUID, Location> anchorPrimaryLocationByPlayer = new HashMap<>();
  private final Map<UUID, Location> anchorSecondaryLocationByPlayer = new HashMap<>();
  private final Map<UUID, UUID> decoyEntityByPlayer = new HashMap<>();
  private final Map<UUID, Long> decoyExpireEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, List<ItemStack>> lootPortalItemsByPlayer = new HashMap<>();
  private final Map<UUID, Long> lootPortalExpireEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> lootPortalTierByPlayer = new HashMap<>();
  private final Map<UUID, Integer> lootPortalStoredXpByPlayer = new HashMap<>();
  private final Map<UUID, Boolean> lootPortalFilterEnabledByPlayer = new HashMap<>();
  private final Map<UUID, Integer> miningStreakCounterByPlayer = new HashMap<>();
  private final Map<UUID, Integer> excavationChargeByPlayer = new HashMap<>();
  private final Map<UUID, String> weaponMonopolyGroupByPlayer = new HashMap<>();
  private final Map<UUID, Integer> rockfallMiningStreakByPlayer = new HashMap<>();
  private final Map<UUID, Long> rockfallLastMineEpochMilliByPlayer = new HashMap<>();
  private final Map<UUID, Long> nextHotbarRotateEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> hotbarCyclePendingEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> hotbarCyclePendingTierByPlayer = new HashMap<>();
  private final Map<UUID, Long> nextSoundExposureEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> soundExposureUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> soundExposureTierByPlayer = new HashMap<>();
  private final Map<UUID, Long> nextMemoryWipeEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> nextTransmuteEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> nextScannerPingEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> enclosedSinceEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> nightmareLastCombatSeenEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> controlDistortionUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> controlDistortionTierByPlayer = new HashMap<>();
  private final Map<UUID, Integer> gripFractureHitCounterByPlayer = new HashMap<>();
  private final Map<UUID, Integer> weaponRebellionHitCounterByPlayer = new HashMap<>();
  private final Map<UUID, Long> cursedSprintLockUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> doomBreathHealLockUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> ominousBlinkReblinkUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> ominousBlinkNextPulseEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> c9InternalTeleportGuardUntilEpochMilliByPlayer = new HashMap<>();
  private final Map<UUID, Long> c9PortalHandledUntilEpochMilliByPlayer = new HashMap<>();
  private final Map<UUID, Long> alchemyContaminationGuardUntilEpochMilliByPlayer = new HashMap<>();
  private final Map<UUID, Long> frenzyUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> frenzyFatigueUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Boolean> frenzyCombatActiveByPlayer = new HashMap<>();
  private final Map<UUID, Integer> frenzyFatigueStacksByPlayer = new HashMap<>();
  private final Map<UUID, Long> frenzyHealLockUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> bloodContractLastKillEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> bloodContractKillStreakByPlayer = new HashMap<>();
  private final Map<UUID, Integer> ghostTierByPlayer = new HashMap<>();
  private final Map<UUID, Integer> ghostChargesByPlayer = new HashMap<>();
  private final Map<UUID, Long> ghostNextRechargeEpochSecondByPlayer = new HashMap<>();
  private final Set<UUID> silenceVowSilentPlayers = new HashSet<>();
  private final Map<UUID, Long> enderOrbitAfterimageUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> enderOrbitPearlGraceUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> enderOrbitRapidFollowupUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> enderOrbitRapidFollowupCooldownUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, List<UUID>> clonePactAuxDecoyEntityIdsByPlayer = new HashMap<>();
  private final Map<UUID, Long> clonePactSharedDamageGuardUntilEpochMilliByPlayer = new HashMap<>();
  private final Map<UUID, Long> reflectSkinNoReentryUntilEpochMilliByPlayer = new HashMap<>();
  private final Map<UUID, Long> reflectSkinVulnerableUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> timeTaxFieldUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> timeTaxFieldTierByPlayer = new HashMap<>();
  private final Map<UUID, String> spiritExchangeStateByPlayer = new HashMap<>();
  private final Map<UUID, Double> spiritExchangeScoreMultiplierByPlayer = new HashMap<>();
  private final Map<UUID, Long> borderGamblerNextScoreEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> fortressModeUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> fortressOverclockUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> antiGravityDashChargesByPlayer = new HashMap<>();
  private final Map<UUID, Long> antiGravityDashNextRechargeEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Boolean> antiGravityAirborneStateByPlayer = new HashMap<>();
  private final Map<UUID, Long> blackSanctuaryCalmUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> x7GuaranteedSuccessUsedEpochDayByPlayer = new HashMap<>();
  private final Set<UUID> x7GuaranteedFailurePendingPlayers = new HashSet<>();
  private final Map<UUID, Long> scoreInvestmentUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Double> scoreInvestmentBonusRatioByPlayer = new HashMap<>();
  private final Map<UUID, List<ScoreInvestmentLedgerEntry>> scoreInvestmentLedgerByPlayer = new HashMap<>();
  private final Map<UUID, Long> projectileAbsorbWindowUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> projectileAbsorbTierByPlayer = new HashMap<>();
  private final Map<UUID, Long> projectileAbsorbReflectUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> projectileAlignUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> projectileReflectUntilEpochSecondByPlayer = new HashMap<>();
  private final Set<UUID> projectileReflectPendingPlayers = new HashSet<>();
  private final Map<UUID, List<StoredProjectile>> storedProjectilesByPlayer = new HashMap<>();
  private final Map<UUID, Location> lastDamagerLocationByPlayer = new HashMap<>();
  private final Map<UUID, Double> shieldCoreOriginalMaxHealthBaseByPlayer = new HashMap<>();
  private final Map<UUID, Double> shieldCoreOriginalMaxAbsorptionBaseByPlayer = new HashMap<>();
  private final Map<UUID, Double> shieldCoreLastAbsorptionByPlayer = new HashMap<>();
  private final Map<UUID, Double> absorptionShieldOriginalMaxAbsorptionBaseByPlayer = new HashMap<>();
  private final Map<UUID, Double> absorptionShieldLastAbsorptionByPlayer = new HashMap<>();
  private final Map<UUID, Long> shieldCoreBreakBoostUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> rewindGuardUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Double> rewindGuardRatioByPlayer = new HashMap<>();
  private final Map<UUID, Long> scannerMarkedUntilEpochSecondByEntity = new HashMap<>();
  private final Map<UUID, Long> gravityLadderActiveUntilEpochMilliByPlayer = new HashMap<>();
  private final Map<String, Long> burstMineLockUntilEpochSecondByBlockKey = new HashMap<>();
  private final Map<UUID, Long> knockbackImmuneUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> blinkChargesByPlayer = new HashMap<>();
  private final Map<UUID, Long> blinkNextRechargeEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> blinkFallImmunityUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> phaseChargesByPlayer = new HashMap<>();
  private final Map<UUID, Long> phaseNextRechargeEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> decoyTierByPlayer = new HashMap<>();
  private final Map<UUID, Boolean> decoySwapUsedByPlayer = new HashMap<>();
  private final Map<UUID, Long> wallHangUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> wallHangRechargeEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> auraResonanceBacklashUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> auraInversionUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> auraInversionBacklashUntilEpochSecondByPlayer = new HashMap<>();
  private final Set<UUID> auraInversionBacklashAppliedByPlayer = new HashSet<>();
  private final Map<UUID, Long> anchorFatalBufferUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Integer> raidFatalBufferUsedPhaseByPlayer = new HashMap<>();
  private final Set<UUID> raidRaidwideSaveUsedByPlayer = new HashSet<>();
  private final Map<UUID, Long> stalkerSpawnWarnedForEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> stalkerNeutralizeUsedEpochDayByPlayer = new HashMap<>();
  private final Map<UUID, Long> instantCraftUsedEpochDayByPlayer = new HashMap<>();
  private final Map<UUID, Long> instantCraftBonusUntilEpochSecondByPlayer = new HashMap<>();
  private final Set<UUID> instantCraftRefundPendingPlayers = new HashSet<>();
  private final Map<UUID, ItemStack[]> rememberedRecipeMatrixByPlayer = new HashMap<>();
  private final Map<UUID, Long> dropRotUntilEpochSecondByPlayer = new HashMap<>();
  private final Map<UUID, Long> cursedDropExpireEpochSecondByItem = new HashMap<>();
  private final Map<UUID, UUID> cursedDropOwnerByItem = new HashMap<>();
  private final Map<UUID, Long> temporaryInvulnerableUntilEpochSecondByPlayer = new HashMap<>();
  private final Set<UUID> initialCardSetupActivePlayers = new HashSet<>();
  private final Map<UUID, Integer> rhythmComboStacksByPlayer = new HashMap<>();
  private final Map<UUID, Long> rhythmComboExpireEpochMilliByPlayer = new HashMap<>();
  private final Map<String, Location> strongholdLocateCacheByWorld = new HashMap<>();
  private final Map<String, Long> strongholdLocateCacheExpireEpochSecondByWorld = new HashMap<>();
  private final Map<UUID, Long> pendingScoreHudDeltaByPlayer = new HashMap<>();
  private final Map<UUID, Long> lastScoreHudEpochSecondByPlayer = new HashMap<>();

  private long roundId = 1L;
  private long roundStartedEpochSecond = 0L;
  private SeasonState state = SeasonState.HARDCORE;
  private SeasonState stateBeforeResetPending = SeasonState.HARDCORE;
  private long resetPendingUntilEpochSecond = 0L;
  private long lastAutoSaveEpochSecond = 0L;
  private long nextScorePersistenceEpochSecond = 0L;
  private long borderShrinkStartedEpochSecond = 0L;
  private long freePlayStartedEpochSecond = 0L;
  private long freePlayEscapeDeadlineEpochSecond = 0L;
  private boolean freePlayFinalized = false;
  private long tickCounter = 0L;
  private int auraPlayerCursor = 0;
  private int lastEnderAuraLevel = -1;
  private long nextSurvivalScoreEpochSecond = 0L;
  private long nextRuntimeScoreDecayEpochSecond = 0L;
  private UUID dragonEggBonusOwner = null;
  private long nextStalkerSpawnEpochSecond = 0L;
  private int stalkerEmpowerStacks = 0;
  private long nextRaidWaveEpochSecond = 0L;
  private int dragonRaidPhase = 0;
  private boolean dragonRaidPhase2BurstDone = false;
  private long lastCardDrawIngameDay = -1L;

  private File stateFile;
  private BukkitTask tickerTask;
  private BukkitTask borderParticleTask;
  private VaultJdbcRepository vaultRepository;
  private SeasonStateJdbcRepository seasonStateRepository;
  private ServerRole serverRole = ServerRole.SEASON_GAMEPLAY;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    this.serverRole = resolveServerRole();

    if (isSeasonGameplayServer()) {
      applySeasonProfileOverrides();
    }

    this.vaultRepository = null;
    this.seasonStateRepository = null;
    if (internalVaultEnabled() && databaseEnabled() && serverRole != ServerRole.DISABLED) {
      this.vaultRepository = new VaultJdbcRepository(
          this,
          databaseJdbcUrl(),
          databaseUsername(),
          databasePassword(),
          databaseConnectTimeoutSeconds()
      );
      this.vaultRepository.initialize();
    }

    if (seasonStateDatabaseEnabled() && isSeasonGameplayServer()) {
      this.seasonStateRepository = new SeasonStateJdbcRepository(
          this,
          databaseJdbcUrl(),
          databaseUsername(),
          databasePassword(),
          databaseConnectTimeoutSeconds()
      );
      this.seasonStateRepository.initialize();
    }

    this.stateFile = new File(getDataFolder(), "state.yml");
    if (isSeasonGameplayServer()) {
      reloadEffectCatalog();
      loadState();
      stripCardEffectDataIfDisabled();
      if (roundStartedEpochSecond <= 0L) {
        roundStartedEpochSecond = nowEpochSecond();
      }
      if (borderShrinkStartedEpochSecond <= 0L) {
        borderShrinkStartedEpochSecond = roundStartedEpochSecond;
      }
      if (nextSurvivalScoreEpochSecond <= 0L) {
        nextSurvivalScoreEpochSecond = nowEpochSecond() + scoreSurvivalIntervalSeconds();
      }
      if (nextRuntimeScoreDecayEpochSecond <= 0L) {
        nextRuntimeScoreDecayEpochSecond = nowEpochSecond() + runtimeScoreDecayIntervalSeconds();
      }
      if (nextStalkerSpawnEpochSecond <= 0L) {
        nextStalkerSpawnEpochSecond = roundStartedEpochSecond + stalkerFirstSpawnDelaySeconds();
      }
      syncAllParticipantSlots(false);
      saveState();
      bootstrapTaggedSeasonEntities();
      maybeAutoCenterBorderFromStronghold(false);
    }

    if (serverRole == ServerRole.DISABLED) {
      getLogger().info("SeasonManager enabled in DISABLED role on server=" + currentServerName() + ". No runtime hooks.");
      return;
    }

    if (isSeasonGameplayServer()) {
      registerCommand("season");
      registerCommand("slot");
      registerCommand("perk");
      registerCommand("life");
      registerCommand("score");
      registerCommand("border");
      registerCommand("information");
    }

    Bukkit.getPluginManager().registerEvents(this, this);

    if (isSeasonGameplayServer()) {
      for (World world : Bukkit.getWorlds()) {
        if (world.getEnvironment() == World.Environment.THE_END) {
          ensureEndSpawnPlatform(world);
        }
      }
      this.tickerTask = Bukkit.getScheduler().runTaskTimer(this, this::tick, 20L, 20L);
      long borderInterval = borderParticleIntervalTicks();
      this.borderParticleTask = Bukkit.getScheduler().runTaskTimer(
          this,
          this::tickBorderParticles,
          borderInterval,
          borderInterval
      );
    }

    if (isSeasonGameplayServer()) {
      getLogger().info("SeasonManager enabled. role=" + serverRole.name() + " roundId=" + roundId + " state=" + state.name());
    } else {
      getLogger().info("SeasonManager enabled. role=" + serverRole.name() + " server=" + currentServerName());
    }
    if (internalVaultEnabled()) {
      getLogger().info(
          "Vault policy: deposit_server=" + vaultDepositServer()
              + ", withdraw_servers=" + String.join(",", vaultWithdrawServers())
              + ", enabled_in_state=" + vaultEnabledState()
      );
    } else {
      getLogger().info("Vault subsystem disabled in SeasonManager (external plugin mode).");
    }
  }

  @Override
  public void onDisable() {
    if (tickerTask != null) {
      tickerTask.cancel();
    }
    if (borderParticleTask != null) {
      borderParticleTask.cancel();
    }
    restoreAllRuntimeWalkSpeeds();
    restoreAllRuntimeAttributes();
    for (Player player : Bukkit.getOnlinePlayers()) {
      restoreShieldCoreMaxHealth(player);
      restoreAbsorptionShieldCapacity(player);
    }
    clearAllAuraMonsterAttributeModifiers();
    closeAllVaultSessions();
    if (vaultRepository != null && vaultRepository.isReady()) {
      vaultRepository.releaseAllLocksByServer(currentServerName());
    }
    if (isSeasonGameplayServer()) {
      saveState();
    }
    pendingScoreHudDeltaByPlayer.clear();
    lastScoreHudEpochSecondByPlayer.clear();
  }

  private void applySeasonProfileOverrides() {
    String profileName = getConfig().getString("season.active_profile", "");
    if (profileName == null) {
      return;
    }
    profileName = profileName.trim();
    if (profileName.isEmpty()) {
      return;
    }

    File profilesDir = new File(getDataFolder(), "profiles");
    if (!profilesDir.exists() && !profilesDir.mkdirs()) {
      getLogger().warning("Could not create season profile directory: " + profilesDir.getAbsolutePath());
      return;
    }

    File profileFile = new File(profilesDir, profileName + ".yml");
    if (!profileFile.exists() && getConfig().getBoolean("season.auto_create_profile_template", true)) {
      String resourcePath = "profiles/" + profileName + ".yml";
      try {
        saveResource(resourcePath, false);
      } catch (IllegalArgumentException ignored) {
        // profile template not bundled; fall through to warning below
      }
    }

    if (!profileFile.exists()) {
      getLogger().warning("Season profile file not found: " + profileFile.getAbsolutePath());
      return;
    }

    YamlConfiguration profile = YamlConfiguration.loadConfiguration(profileFile);
    int applied = 0;
    for (Map.Entry<String, Object> entry : profile.getValues(true).entrySet()) {
      String path = entry.getKey();
      Object value = entry.getValue();
      if (path == null || path.isBlank() || value instanceof ConfigurationSection) {
        continue;
      }
      if (!isProfileOverridePathAllowed(path)) {
        continue;
      }
      getConfig().set(path, value);
      applied++;
    }

    getLogger().info("Applied season profile '" + profileName + "' overrides=" + applied);
  }

  private boolean isProfileOverridePathAllowed(String path) {
    if (path == null || path.isBlank()) {
      return false;
    }
    int dot = path.indexOf('.');
    String root = dot >= 0 ? path.substring(0, dot) : path;
    return PROFILE_OVERRIDE_ROOTS.contains(root);
  }

  private ServerRole resolveServerRole() {
    String configured = getConfig().getString("server.role", "AUTO");
    if (configured != null) {
      String normalized = configured.trim().toUpperCase(Locale.ROOT);
      if (!normalized.isEmpty() && !"AUTO".equals(normalized)) {
        ServerRole parsed = ServerRole.parse(normalized);
        if (parsed != null) {
          if (parsed == ServerRole.WITHDRAW_ONLY && !internalVaultEnabled()) {
            return ServerRole.DISABLED;
          }
          return parsed;
        }
        getLogger().warning("Unknown server.role='" + configured + "'. Falling back to AUTO.");
      }
    }

    String current = currentServerName();
    if (current.equalsIgnoreCase(vaultDepositServer())) {
      return ServerRole.SEASON_GAMEPLAY;
    }
    if (internalVaultEnabled()) {
      for (String withdrawServer : vaultWithdrawServers()) {
        if (current.equalsIgnoreCase(withdrawServer)) {
          return ServerRole.WITHDRAW_ONLY;
        }
      }
    }
    return ServerRole.DISABLED;
  }

  private enum ServerRole {
    SEASON_GAMEPLAY,
    WITHDRAW_ONLY,
    DISABLED;

    private static ServerRole parse(String raw) {
      if (raw == null || raw.isBlank()) {
        return null;
      }
      try {
        return ServerRole.valueOf(raw.trim().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException ignored) {
        return null;
      }
    }
  }

  private void registerCommand(String name) {
    PluginCommand pluginCommand = getCommand(name);
    if (pluginCommand == null) {
      getLogger().warning("Missing command in plugin.yml: " + name);
      return;
    }
    pluginCommand.setExecutor(this);
    pluginCommand.setTabCompleter(this);
  }

  @EventHandler
  public void onWorldLoad(WorldLoadEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (event.getWorld().getEnvironment() == World.Environment.THE_END) {
      ensureEndSpawnPlatform(event.getWorld());
    }
  }

  @EventHandler
  public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    handleSpiritExchangeTransitionOnWorldChange(event.getPlayer());
    if (state != SeasonState.HARDCORE || !climaxAutoOnFirstEndEntry()) {
      return;
    }
    if (event.getPlayer().getWorld().getEnvironment() != World.Environment.THE_END) {
      return;
    }

    state = SeasonState.CLIMAX;
    Bukkit.broadcastMessage("[Season] CLIMAX started: first player entered The End.");
    saveState();
  }

  @EventHandler
  public void onWeatherChange(WeatherChangeEvent event) {
    if (!isSeasonGameplayServer() || event == null || event.getWorld() == null) {
      return;
    }
    for (Player player : event.getWorld().getPlayers()) {
      if (player == null || !player.isOnline()) {
        continue;
      }
      UUID playerId = player.getUniqueId();
      spiritExchangeStateByPlayer.remove(playerId);
      spiritExchangeScoreMultiplierByPlayer.remove(playerId);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerPortal(PlayerPortalEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (state != SeasonState.FREE_PLAY || freePlayFinalized || !isWithinFreePlayEscapeWindow()) {
      return;
    }
    if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
      return;
    }
    if (event.getFrom().getWorld() == null || event.getFrom().getWorld().getEnvironment() != World.Environment.THE_END) {
      return;
    }

    Player player = event.getPlayer();
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut() || !data.hasScoreSnapshot() || data.isEscapedWithinWindow()) {
      return;
    }

    data.setEscapedWithinWindow(true);
    long remaining = Math.max(0L, freePlayEscapeDeadlineEpochSecond - nowEpochSecond());
    player.sendMessage("[Season] Escape recorded. remaining_window=" + remaining + "s");
    saveState();
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
      return;
    }
  }

  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    Entity entity = event.getEntity();
    UUID entityId = entity.getUniqueId();
    if (entity.getScoreboardTags().contains(STALKER_TAG)) {
      stalkerEntities.remove(entityId);
    }
    if (entity.getScoreboardTags().contains(RAID_TAG)) {
      raidEntityOrder.remove(entityId);
    }

    if (!isSeasonGameplayServer()) {
      return;
    }

    applyMobKillScore(event);

    if (!freePlayAutoOnDragonKill() || state == SeasonState.FREE_PLAY) {
      return;
    }
    if (event.getEntityType() != EntityType.ENDER_DRAGON) {
      return;
    }
    if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END) {
      return;
    }

    applyDragonJackpot(event.getEntity());
    clearRaidMobs();
    resetDragonRaidProgress();
    enterFreePlay("Ender Dragon defeated");
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();

    boolean isFirstParticipantJoin = !participants.contains(uuid);
    PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(uuid);
    syncPlayerSlots(uuid, data, true);
    recoverShieldCoreStateIfStuck(player, data);

    if (isFirstParticipantJoin) {
      long until = nowEpochSecond() + newbieProtectionSeconds();
      data.setNewbieProtectUntilEpochSecond(until);
      player.sendMessage("[Season] Newbie protection enabled for " + newbieProtectionSeconds() + " seconds.");
    }
    if (hasNewbieProtection(player.getUniqueId())) {
      applyNewbieProtectionEffects(player);
    }

    if (data.isOut() && enforceOutSpectator() && !(state == SeasonState.FREE_PLAY && allowOutInFreePlay())) {
      enforceSpectator(player);
      player.sendMessage("[Season] You are OUT for this round.");
    }

    boolean shouldRelocateOnFirstJoin = isFirstParticipantJoin && !data.isJoinSpawnAssigned();
    if (shouldRelocateOnFirstJoin) {
      data.setJoinSpawnAssigned(true);
      scheduleSafeSpawnOnJoin(player);
    }
    updateResetPendingState();
    beginInitialCardSetupIfNeeded(player, data);
    saveState();
  }

  private void beginInitialCardSetupIfNeeded(Player player, PlayerRoundData data) {
    if (player == null || !player.isOnline() || data == null) {
      return;
    }
    if (!cardsEnabled() || !cardsOneTimeRollEnabled() || !cardsEffectLogicEnabled()) {
      return;
    }
    if (data.isInitialCardRollCompleted()) {
      return;
    }

    UUID uuid = player.getUniqueId();
    syncPlayerSlots(uuid, data, false);
    int blessingSlots = Math.max(0, data.getBlessingSlotsUnlocked());
    int curseSlots = Math.max(0, data.getCurseSlotsUnlocked());
    if (blessingSlots <= 0 && curseSlots <= 0) {
      return;
    }

    long nowDay = currentIngameDay();
    data.getBlessingEffects().clear();
    data.getCurseEffects().clear();

    RollOutcome blessingOutcome = drawFreshEffectsForType(
        data,
        EffectKind.BLESSING,
        nowDay,
        blessingSlots
    );
    RollOutcome curseOutcome = drawFreshEffectsForType(
        data,
        EffectKind.CURSE,
        nowDay,
        curseSlots
    );

    data.setInitialCardRollCompleted(true);
    data.setInitialCardRerollsUsed(0);

    player.sendMessage("[Season] 최초 카드 추첨이 완료되었습니다.");
    if (!blessingOutcome.isEmpty()) {
      player.sendMessage("[Season] 축복 초기 추첨: " + blessingOutcome.summary());
    }
    if (!curseOutcome.isEmpty()) {
      player.sendMessage("[Season] 저주 초기 추첨: " + curseOutcome.summary());
    }
    player.sendMessage("[Season] 초기 리롤 가능 횟수: " + cardsInitialSetupFreeRerolls() + "회");

    Bukkit.getScheduler().runTaskLater(this, () -> {
      if (!player.isOnline()) {
        return;
      }
      PlayerRoundData latest = players.get(uuid);
      if (latest == null || latest.isOut()) {
        return;
      }
      if (cardsInitialSetupInvulnerableWhileGui()) {
        initialCardSetupActivePlayers.add(uuid);
        temporaryInvulnerableUntilEpochSecondByPlayer.put(uuid, nowEpochSecond() + 3600L);
      }
      openSeasonSlotOverview(player, Bukkit.getOfflinePlayer(uuid), latest);
    }, 20L);
  }

  private boolean isInitialCardSetupProtected(Player player) {
    if (player == null || !player.isOnline()) {
      return false;
    }
    UUID playerId = player.getUniqueId();
    if (!initialCardSetupActivePlayers.contains(playerId)) {
      return false;
    }
    InventoryView view = player.getOpenInventory();
    if (view == null || view.getTopInventory() == null) {
      return false;
    }
    return view.getTopInventory().getHolder() instanceof SeasonSlotInventoryHolder;
  }

  private void scheduleReleaseInitialCardSetupProtection(Player player) {
    if (player == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    Bukkit.getScheduler().runTaskLater(this, () -> {
      if (!initialCardSetupActivePlayers.contains(playerId)) {
        return;
      }
      Player online = Bukkit.getPlayer(playerId);
      if (online == null || !online.isOnline()) {
        initialCardSetupActivePlayers.remove(playerId);
        temporaryInvulnerableUntilEpochSecondByPlayer.remove(playerId);
        return;
      }
      InventoryView view = online.getOpenInventory();
      boolean stillInSlotGui = view != null
          && view.getTopInventory() != null
          && view.getTopInventory().getHolder() instanceof SeasonSlotInventoryHolder;
      if (stillInSlotGui) {
        return;
      }
      initialCardSetupActivePlayers.remove(playerId);
      temporaryInvulnerableUntilEpochSecondByPlayer.remove(playerId);
      online.sendMessage("[Season] 초기 카드 설정 보호가 종료되었습니다.");
    }, 2L);
  }

  private void scheduleSafeSpawnOnJoin(Player player) {
    if (player == null || !safeSpawnEnabled() || !safeSpawnEnabledInCurrentState()) {
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data != null && data.isOut()) {
      return;
    }
    if (player.getGameMode() == GameMode.SPECTATOR) {
      return;
    }

    Bukkit.getScheduler().runTask(this, () -> relocatePlayerToSafeSpawn(player));
  }

  private void relocatePlayerToSafeSpawn(Player player) {
    if (player == null || !player.isOnline() || !isSeasonGameplayServer()) {
      return;
    }
    if (!safeSpawnEnabled() || !safeSpawnEnabledInCurrentState()) {
      return;
    }

    PlayerRoundData data = players.get(player.getUniqueId());
    if (data != null && data.isOut()) {
      return;
    }
    if (player.getGameMode() == GameMode.SPECTATOR) {
      return;
    }

    World world = borderWorld();
    if (world == null) {
      return;
    }

    Location safeSpawn = findSafeJoinSpawn(player, world);
    if (safeSpawn == null) {
      getLogger().warning("Failed to find safe spawn for " + player.getName() + ". keeping current location.");
      return;
    }

    player.teleport(safeSpawn);
    player.sendMessage("[Season] You were moved to a safe outer spawn.");
  }

  private Location findSafeJoinSpawn(Player player, World world) {
    List<Location> anchors = playableSpawnAnchors(player, world);
    double minDistance = safeSpawnMinDistanceFromPlayers();
    int attempts = safeSpawnMaxAttempts();

    double centerX = borderCenterX();
    double centerZ = borderCenterZ();
    double borderLimit = Math.max(16.0D, borderCurrentRadius() - safeSpawnBorderMargin());
    double ringMin = Math.max(8.0D, Math.min(safeSpawnRingMinRadius(), borderLimit));
    double ringMax = Math.max(ringMin, Math.min(safeSpawnRingMaxRadius(), borderLimit));

    Location base = player.getLocation();
    for (int attempt = 0; attempt < attempts; attempt++) {
      double angle = ThreadLocalRandom.current().nextDouble(0.0D, Math.PI * 2.0D);
      double radius = randomDouble(ringMin, ringMax);
      double x = centerX + (Math.cos(angle) * radius);
      double z = centerZ + (Math.sin(angle) * radius);

      if (!isFarEnoughFromAnchors(x, z, anchors, minDistance)) {
        continue;
      }

      Location safe = safeSurfaceLocation(world, x, z, base.getYaw(), base.getPitch());
      if (safe != null) {
        return safe;
      }
    }

    return null;
  }

  private List<Location> playableSpawnAnchors(Player joiningPlayer, World world) {
    List<Location> anchors = new ArrayList<>();
    for (Player other : Bukkit.getOnlinePlayers()) {
      if (other.equals(joiningPlayer) || other.getWorld() != world || other.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }
      PlayerRoundData data = players.get(other.getUniqueId());
      if (data != null && data.isOut()) {
        continue;
      }
      anchors.add(other.getLocation());
    }
    return anchors;
  }

  private boolean isFarEnoughFromAnchors(double x, double z, List<Location> anchors, double minDistance) {
    if (minDistance <= 0.0D || anchors.isEmpty()) {
      return true;
    }
    for (Location anchor : anchors) {
      if (Math.hypot(x - anchor.getX(), z - anchor.getZ()) < minDistance) {
        return false;
      }
    }
    return true;
  }

  private Location safeSurfaceLocation(World world, double x, double z, float yaw, float pitch) {
    int blockX = (int) Math.floor(x);
    int blockZ = (int) Math.floor(z);
    int maxY = world.getMaxHeight() - 2;
    int startY = Math.min(maxY, world.getHighestBlockYAt(blockX, blockZ) + 1);
    int minY = Math.max(world.getMinHeight() + 1, safeSpawnMinGroundY(world));
    int searchDepth = safeSpawnVerticalSearchDepth();

    for (int y = startY, scanned = 0; y >= minY && scanned <= searchDepth; y--, scanned++) {
      if (isSafeStandingColumn(world, blockX, y, blockZ)) {
        return new Location(world, blockX + 0.5D, y, blockZ + 0.5D, yaw, pitch);
      }
    }
    return null;
  }

  private boolean isSafeStandingColumn(World world, int x, int y, int z) {
    int minY = world.getMinHeight() + 1;
    int maxY = world.getMaxHeight() - 2;
    if (y < minY || y > maxY) {
      return false;
    }

    Block below = world.getBlockAt(x, y - 1, z);
    Block feet = world.getBlockAt(x, y, z);
    Block head = world.getBlockAt(x, y + 1, z);

    if (!below.getType().isSolid() || isUnsafeSpawnBlock(below.getType())) {
      return false;
    }
    if (!feet.isPassable() || !head.isPassable()) {
      return false;
    }
    if (feet.isLiquid() || head.isLiquid()) {
      return false;
    }
    return !isUnsafeSpawnBlock(feet.getType()) && !isUnsafeSpawnBlock(head.getType());
  }

  private boolean isUnsafeSpawnBlock(Material material) {
    if (material == null) {
      return true;
    }
    if (material.name().endsWith("_LEAVES")) {
      return true;
    }
    return switch (material) {
      case WATER,
          LAVA,
          BUBBLE_COLUMN,
          MAGMA_BLOCK,
          CAMPFIRE,
          SOUL_CAMPFIRE,
          FIRE,
          SOUL_FIRE,
          POWDER_SNOW,
          CACTUS,
          SWEET_BERRY_BUSH -> true;
      default -> false;
    };
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player player = event.getPlayer();
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null) {
      return;
    }
    if (data.isOut() && enforceOutSpectator() && !(state == SeasonState.FREE_PLAY && allowOutInFreePlay())) {
      Bukkit.getScheduler().runTask(this, () -> enforceSpectator(player));
    }
    if (hasNewbieProtection(player.getUniqueId())) {
      Bukkit.getScheduler().runTask(this, () -> applyNewbieProtectionEffects(player));
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    restoreRuntimeAttributeStates(player);
    restoreShieldCoreMaxHealth(player);
    restoreAbsorptionShieldCapacity(player);
    UUID uuid = player.getUniqueId();
    initialCardSetupActivePlayers.remove(uuid);
    temporaryInvulnerableUntilEpochSecondByPlayer.remove(uuid);
    VaultSession session = openVaultSessions.remove(uuid);
    if (session != null && vaultRepository != null && vaultRepository.isReady() && vaultRequireDbLock()) {
      vaultRepository.releaseLock(uuid.toString(), currentServerName());
    }
    projectileAbsorbTierByPlayer.remove(uuid);
    blinkChargesByPlayer.remove(uuid);
    blinkNextRechargeEpochSecondByPlayer.remove(uuid);
    blinkFallImmunityUntilEpochSecondByPlayer.remove(uuid);
    phaseChargesByPlayer.remove(uuid);
    phaseNextRechargeEpochSecondByPlayer.remove(uuid);
    excavationChargeByPlayer.remove(uuid);
    decoyTierByPlayer.remove(uuid);
    decoySwapUsedByPlayer.remove(uuid);
    enclosedSinceEpochSecondByPlayer.remove(uuid);
    nightmareLastCombatSeenEpochSecondByPlayer.remove(uuid);
    wallHangUntilEpochSecondByPlayer.remove(uuid);
    wallHangRechargeEpochSecondByPlayer.remove(uuid);
    knockbackImmuneUntilEpochSecondByPlayer.remove(uuid);
    projectileAbsorbReflectUntilEpochSecondByPlayer.remove(uuid);
    lastDamagerLocationByPlayer.remove(uuid);
    shieldCoreBreakBoostUntilEpochSecondByPlayer.remove(uuid);
    pendingScoreHudDeltaByPlayer.remove(uuid);
    lastScoreHudEpochSecondByPlayer.remove(uuid);
    rewindGuardUntilEpochSecondByPlayer.remove(uuid);
    rewindGuardRatioByPlayer.remove(uuid);
    gravityLadderActiveUntilEpochMilliByPlayer.remove(uuid);
    buildBurstTierByPlayer.remove(uuid);
    buildBurstFatiguePendingAtEpochSecondByPlayer.remove(uuid);
    excavationChargeByPlayer.remove(uuid);
    lastCombatEpochSecondByPlayer.remove(uuid);
    lootPortalTierByPlayer.remove(uuid);
    lootPortalStoredXpByPlayer.remove(uuid);
    lootPortalFilterEnabledByPlayer.remove(uuid);
    soundExposureUntilEpochSecondByPlayer.remove(uuid);
    soundExposureTierByPlayer.remove(uuid);
    gripFractureHitCounterByPlayer.remove(uuid);
    weaponRebellionHitCounterByPlayer.remove(uuid);
    cursedSprintLockUntilEpochSecondByPlayer.remove(uuid);
    doomBreathHealLockUntilEpochSecondByPlayer.remove(uuid);
    frenzyCombatActiveByPlayer.remove(uuid);
    frenzyFatigueStacksByPlayer.remove(uuid);
    frenzyHealLockUntilEpochSecondByPlayer.remove(uuid);
    bloodContractLastKillEpochSecondByPlayer.remove(uuid);
    bloodContractKillStreakByPlayer.remove(uuid);
    ghostUntilEpochSecond.remove(uuid);
    ghostTierByPlayer.remove(uuid);
    ghostChargesByPlayer.remove(uuid);
    ghostNextRechargeEpochSecondByPlayer.remove(uuid);
    weaponMonopolyGroupByPlayer.remove(uuid);
    enderOrbitAfterimageUntilEpochSecondByPlayer.remove(uuid);
    enderOrbitPearlGraceUntilEpochSecondByPlayer.remove(uuid);
    enderOrbitRapidFollowupUntilEpochSecondByPlayer.remove(uuid);
    enderOrbitRapidFollowupCooldownUntilEpochSecondByPlayer.remove(uuid);
    clonePactSharedDamageGuardUntilEpochMilliByPlayer.remove(uuid);
    reflectSkinNoReentryUntilEpochMilliByPlayer.remove(uuid);
    reflectSkinVulnerableUntilEpochSecondByPlayer.remove(uuid);
    timeTaxFieldUntilEpochSecondByPlayer.remove(uuid);
    timeTaxFieldTierByPlayer.remove(uuid);
    spiritExchangeStateByPlayer.remove(uuid);
    spiritExchangeScoreMultiplierByPlayer.remove(uuid);
    borderGamblerNextScoreEpochSecondByPlayer.remove(uuid);
    fortressModeUntilEpochSecondByPlayer.remove(uuid);
    fortressOverclockUntilEpochSecondByPlayer.remove(uuid);
    antiGravityDashChargesByPlayer.remove(uuid);
    antiGravityDashNextRechargeEpochSecondByPlayer.remove(uuid);
    antiGravityAirborneStateByPlayer.remove(uuid);
    blackSanctuaryCalmUntilEpochSecondByPlayer.remove(uuid);
    auraInversionUntilEpochSecondByPlayer.remove(uuid);
    auraInversionBacklashUntilEpochSecondByPlayer.remove(uuid);
    auraInversionBacklashAppliedByPlayer.remove(uuid);
    anchorFatalBufferUntilEpochSecondByPlayer.remove(uuid);
    raidFatalBufferUsedPhaseByPlayer.remove(uuid);
    raidRaidwideSaveUsedByPlayer.remove(uuid);
    stalkerSpawnWarnedForEpochSecondByPlayer.remove(uuid);
    stalkerNeutralizeUsedEpochDayByPlayer.remove(uuid);
    instantCraftUsedEpochDayByPlayer.remove(uuid);
    instantCraftBonusUntilEpochSecondByPlayer.remove(uuid);
    instantCraftRefundPendingPlayers.remove(uuid);
    rememberedRecipeMatrixByPlayer.remove(uuid);
    rhythmComboStacksByPlayer.remove(uuid);
    rhythmComboExpireEpochMilliByPlayer.remove(uuid);
    clearClonePactAuxDecoys(uuid);
    if (silenceVowSilentPlayers.remove(uuid)) {
      player.setSilent(false);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Inventory inventory = event.getInventory();
    if (inventory.getHolder() instanceof SeasonSlotInventoryHolder) {
      if (event.getPlayer() instanceof Player player && initialCardSetupActivePlayers.contains(player.getUniqueId())) {
        scheduleReleaseInitialCardSetupProtection(player);
      }
      return;
    }
    if (!(inventory.getHolder() instanceof VaultInventoryHolder holder)) {
      return;
    }

    UUID owner = holder.owner();
    if (!(event.getPlayer() instanceof Player player) || !owner.equals(player.getUniqueId())) {
      return;
    }

    VaultSession session = openVaultSessions.remove(owner);
    if (session == null) {
      return;
    }

    if (vaultRepository == null || !vaultRepository.isReady()) {
      return;
    }

    try {
      vaultRepository.saveVaultItems(owner.toString(), inventory.getContents(), session.maxSlots());
    } catch (SQLException exception) {
      getLogger().warning("Failed to save vault for " + owner + ": " + exception.getMessage());
      player.sendMessage("[Season] Failed to save vault contents.");
    } finally {
      if (vaultRequireDbLock()) {
        vaultRepository.releaseLock(owner.toString(), currentServerName());
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getView().getTopInventory().getHolder() instanceof SeasonSlotInventoryHolder holder) {
      onSeasonSlotInventoryClick(event, holder);
      return;
    }

    if (!(event.getView().getTopInventory().getHolder() instanceof VaultInventoryHolder holder)) {
      return;
    }
    if (holder.mode() != VaultMode.WITHDRAW) {
      return;
    }

    Inventory top = event.getView().getTopInventory();
    Inventory clicked = event.getClickedInventory();
    if (clicked == null) {
      return;
    }

    if (clicked.equals(top)) {
      ItemStack cursor = event.getCursor();
      if ((cursor != null && cursor.getType() != Material.AIR) || event.getClick().isKeyboardClick()) {
        event.setCancelled(true);
      }
      return;
    }

    if (event.isShiftClick()) {
      event.setCancelled(true);
    }
  }

  private void onSeasonSlotInventoryClick(InventoryClickEvent event, SeasonSlotInventoryHolder holder) {
    if (event == null || holder == null) {
      return;
    }
    event.setCancelled(true);

    if (!(event.getWhoClicked() instanceof Player viewer)) {
      return;
    }

    int topSize = event.getView().getTopInventory().getSize();
    int rawSlot = event.getRawSlot();
    if (rawSlot < 0 || rawSlot >= topSize) {
      return;
    }
    if (rawSlot == SLOT_GUI_REROLL_BUTTON_SLOT) {
      trySeasonSlotReroll(viewer, holder);
      return;
    }
    if (rawSlot == SLOT_GUI_UPGRADE_BUTTON_SLOT) {
      trySeasonSlotTierUpgrade(viewer, holder);
      return;
    }
    if (rawSlot == SLOT_GUI_DEBUG_OP_BUTTON_SLOT && seasonDebugModeEnabled()) {
      trySeasonSlotDebugToggleOp(viewer, holder);
      return;
    }
  }

  private void switchSeasonSlotPage(Player viewer, SeasonSlotInventoryHolder holder) {
    if (viewer == null || holder == null) {
      return;
    }
    UUID targetUuid = holder.targetUuid();
    if (targetUuid == null) {
      viewer.sendMessage("[Season] 페이지 전환 대상 정보를 찾을 수 없습니다.");
      return;
    }
    PlayerRoundData data = players.get(targetUuid);
    if (data == null) {
      viewer.sendMessage("[Season] 라운드 데이터가 없어 페이지를 열 수 없습니다.");
      return;
    }
    syncPlayerSlots(targetUuid, data, false);
    int nextPage = holder.page() == SLOT_GUI_PAGE_RESIDUAL ? SLOT_GUI_PAGE_PRIMARY : SLOT_GUI_PAGE_RESIDUAL;
    openSeasonSlotOverview(
        viewer,
        Bukkit.getOfflinePlayer(targetUuid),
        data,
        nextPage,
        holder.residualPage(),
        holder.residualFilterMode()
    );
  }

  private void switchResidualPage(Player viewer, SeasonSlotInventoryHolder holder, int delta) {
    if (viewer == null || holder == null || delta == 0) {
      return;
    }
    UUID targetUuid = holder.targetUuid();
    if (targetUuid == null) {
      viewer.sendMessage("[Season] 페이지 전환 대상 정보를 찾을 수 없습니다.");
      return;
    }
    PlayerRoundData data = players.get(targetUuid);
    if (data == null) {
      viewer.sendMessage("[Season] 라운드 데이터가 없어 페이지를 열 수 없습니다.");
      return;
    }
    syncPlayerSlots(targetUuid, data, false);
    openSeasonSlotOverview(
        viewer,
        Bukkit.getOfflinePlayer(targetUuid),
        data,
        SLOT_GUI_PAGE_RESIDUAL,
        Math.max(0, holder.residualPage() + delta),
        holder.residualFilterMode()
    );
  }

  private void cycleResidualFilter(Player viewer, SeasonSlotInventoryHolder holder) {
    if (viewer == null || holder == null) {
      return;
    }
    UUID targetUuid = holder.targetUuid();
    if (targetUuid == null) {
      viewer.sendMessage("[Season] 필터 대상 정보를 찾을 수 없습니다.");
      return;
    }
    PlayerRoundData data = players.get(targetUuid);
    if (data == null) {
      viewer.sendMessage("[Season] 라운드 데이터가 없어 필터를 적용할 수 없습니다.");
      return;
    }
    syncPlayerSlots(targetUuid, data, false);
    int nextFilter = holder.residualFilterMode() + 1;
    if (nextFilter > SLOT_GUI_RESIDUAL_FILTER_MAX) {
      nextFilter = SLOT_GUI_RESIDUAL_FILTER_ALL;
    }
    openSeasonSlotOverview(
        viewer,
        Bukkit.getOfflinePlayer(targetUuid),
        data,
        SLOT_GUI_PAGE_RESIDUAL,
        0,
        nextFilter
    );
  }

  private void trySeasonSlotReroll(Player viewer, SeasonSlotInventoryHolder holder) {
    if (viewer == null || holder == null) {
      return;
    }

    UUID targetUuid = holder.targetUuid();
    if (targetUuid == null) {
      viewer.sendMessage("[Season] 리롤 대상 정보를 찾을 수 없습니다.");
      return;
    }

    PlayerRoundData data = players.get(targetUuid);
    if (data == null) {
      viewer.sendMessage("[Season] 라운드 데이터가 없어 리롤할 수 없습니다.");
      return;
    }

    syncPlayerSlots(targetUuid, data, false);
    boolean initialSetupMode = isInitialSetupRerollMode(viewer, targetUuid, data);
    long now = nowEpochSecond();
    String blockedReason = seasonSlotRerollBlockedReason(viewer, targetUuid, data, now, initialSetupMode);
    if (blockedReason != null) {
      viewer.sendMessage("[Season] " + blockedReason);
      return;
    }

    long cost = initialSetupMode ? 0L : cardsRerollCostScore();
    if (cost > 0L) {
      adjustPlayerScore(targetUuid, data, -cost, true);
    }

    int blessingSlots = data.getBlessingSlotsUnlocked();
    int curseSlots = data.getCurseSlotsUnlocked();
    long nowDay = currentIngameDay();

    data.getBlessingEffects().clear();
    data.getCurseEffects().clear();

    RollOutcome blessingOutcome = drawFreshEffectsForType(
        data,
        EffectKind.BLESSING,
        nowDay,
        blessingSlots
    );
    RollOutcome curseOutcome = drawFreshEffectsForType(
        data,
        EffectKind.CURSE,
        nowDay,
        curseSlots
    );

    long severeBonus = blessingOutcome.severeBonus() + curseOutcome.severeBonus();
    if (cardsRerollSevereBonusOnReroll() && severeBonus > 0L) {
      addFlatScore(targetUuid, severeBonus);
    }

    if (initialSetupMode) {
      data.setInitialCardRerollsUsed(data.getInitialCardRerollsUsed() + 1);
    } else {
      long cooldownSeconds = cardsRerollCooldownSeconds();
      if (cooldownSeconds > 0L) {
        slotRerollCooldownUntilEpochSecond.put(targetUuid, now + cooldownSeconds);
      } else {
        slotRerollCooldownUntilEpochSecond.remove(targetUuid);
      }
    }

    saveState();

    if (initialSetupMode) {
      int used = data.getInitialCardRerollsUsed();
      int max = cardsInitialSetupFreeRerolls();
      int remaining = Math.max(0, max - used);
      viewer.sendMessage("[Season] 초기 리롤 완료 (" + used + "/" + max + ", 남은 횟수=" + remaining + ")");
    } else {
      viewer.sendMessage("[Season] 슬롯 리롤 완료: 비용 -" + cost + " 점수.");
    }
    if (!blessingOutcome.isEmpty()) {
      viewer.sendMessage("[Season] 축복 리롤: " + blessingOutcome.summary());
    }
    if (!curseOutcome.isEmpty()) {
      viewer.sendMessage("[Season] 저주 리롤: " + curseOutcome.summary());
    }
    if (severeBonus > 0L) {
      viewer.sendMessage("[Season] 중대 저주 보너스 +" + severeBonus + " 점수.");
    }

    openSeasonSlotOverview(
        viewer,
        Bukkit.getOfflinePlayer(targetUuid),
        data,
        holder.page(),
        holder.residualPage(),
        holder.residualFilterMode()
    );
  }

  private String seasonSlotRerollBlockedReason(
      Player viewer,
      UUID targetUuid,
      PlayerRoundData data,
      long nowEpochSecond,
      boolean initialSetupMode
  ) {
    if (!isSeasonGameplayServer()) {
      return "이 서버에서는 슬롯 리롤을 사용할 수 없습니다.";
    }
    if (!cardsEnabled() || !cardsEnabledInCurrentState()) {
      return "현재 라운드 상태에서는 슬롯 리롤이 비활성화되어 있습니다.";
    }
    if (!cardsEffectLogicEnabled()) {
      return "카드 효과가 임시 비활성화되어 있습니다.";
    }
    if (!cardsRerollEnabled() && !initialSetupMode) {
      return "슬롯 리롤 기능이 비활성화되어 있습니다.";
    }
    if (viewer == null || targetUuid == null || !viewer.getUniqueId().equals(targetUuid)) {
      return "자신의 슬롯에서만 리롤할 수 있습니다.";
    }
    if (data == null) {
      return "플레이어 데이터가 없어 리롤할 수 없습니다.";
    }
    if (cardsRerollRequirePlayable() && data.isOut()) {
      return "OUT 상태에서는 슬롯 리롤을 사용할 수 없습니다.";
    }

    int availableSlots = Math.max(0, data.getBlessingSlotsUnlocked()) + Math.max(0, data.getCurseSlotsUnlocked());
    if (availableSlots <= 0) {
      return "해금된 슬롯이 없어 리롤할 수 없습니다.";
    }

    if (initialSetupMode) {
      int remaining = remainingInitialSetupRerolls(data);
      if (remaining <= 0) {
        return "초기 리롤 가능 횟수를 모두 사용했습니다.";
      }
      return null;
    }

    long cooldownUntil = slotRerollCooldownUntilEpochSecond.getOrDefault(targetUuid, 0L);
    if (cooldownUntil > nowEpochSecond) {
      return "리롤 대기시간이 남았습니다: " + (cooldownUntil - nowEpochSecond) + "초";
    }

    long cost = cardsRerollCostScore();
    if (cost > 0L && data.getScore() < cost) {
      return "점수가 부족합니다. 필요=" + cost + ", 보유=" + data.getScore();
    }
    return null;
  }

  private boolean isInitialSetupRerollMode(Player viewer, UUID targetUuid, PlayerRoundData data) {
    if (viewer == null || targetUuid == null || data == null) {
      return false;
    }
    if (!cardsOneTimeRollEnabled() || cardsInitialSetupFreeRerolls() <= 0) {
      return false;
    }
    if (!viewer.getUniqueId().equals(targetUuid)) {
      return false;
    }
    if (!data.isInitialCardRollCompleted()) {
      return false;
    }
    if (!isInitialCardSetupProtected(viewer)) {
      return false;
    }
    return data.getInitialCardRerollsUsed() < cardsInitialSetupFreeRerolls();
  }

  private int remainingInitialSetupRerolls(PlayerRoundData data) {
    if (data == null) {
      return 0;
    }
    int max = cardsInitialSetupFreeRerolls();
    return Math.max(0, max - data.getInitialCardRerollsUsed());
  }

  private void trySeasonSlotTierUpgrade(Player viewer, SeasonSlotInventoryHolder holder) {
    if (viewer == null || holder == null) {
      return;
    }
    UUID targetUuid = holder.targetUuid();
    if (targetUuid == null) {
      viewer.sendMessage("[Season] 강화 대상 정보를 찾을 수 없습니다.");
      return;
    }

    PlayerRoundData data = players.get(targetUuid);
    if (data == null) {
      viewer.sendMessage("[Season] 라운드 데이터가 없어 강화할 수 없습니다.");
      return;
    }

    syncPlayerSlots(targetUuid, data, false);
    String blockedReason = seasonSlotTierUpgradeBlockedReason(viewer, targetUuid, data);
    if (blockedReason != null) {
      viewer.sendMessage("[Season] " + blockedReason);
      return;
    }

    long cost = cardsTierUpgradeCostScore();
    if (cost > 0L) {
      adjustPlayerScore(targetUuid, data, -cost, true);
    }

    int blessingUpgraded = 0;
    int curseUpgraded = 0;
    long severeBonus = 0L;
    for (ActiveSeasonEffect effect : data.getBlessingEffects().values()) {
      if (effect == null || effect.getTier() >= cardsMaxTier()) {
        continue;
      }
      effect.increaseTier(cardsMaxTier());
      blessingUpgraded++;
    }
    for (ActiveSeasonEffect effect : data.getCurseEffects().values()) {
      if (effect == null || effect.getTier() >= cardsMaxTier()) {
        continue;
      }
      effect.increaseTier(cardsMaxTier());
      curseUpgraded++;
      if (effect.isSevereCurse() && cardsSevereBonusOnUpgrade()) {
        severeBonus += severeBonusFor(effect);
      }
    }

    if (severeBonus > 0L) {
      addFlatScore(targetUuid, severeBonus);
    }
    saveState();

    viewer.sendMessage(
        "[Season] 동시 티어 강화 완료: 비용 -" + cost
            + ", 축복 +" + blessingUpgraded
            + ", 저주 +" + curseUpgraded
    );
    if (severeBonus > 0L) {
      viewer.sendMessage("[Season] 중대 저주 보너스 +" + severeBonus + " 점수.");
    }
    openSeasonSlotOverview(
        viewer,
        Bukkit.getOfflinePlayer(targetUuid),
        data,
        holder.page(),
        holder.residualPage(),
        holder.residualFilterMode()
    );
  }

  private String seasonSlotTierUpgradeBlockedReason(Player viewer, UUID targetUuid, PlayerRoundData data) {
    if (!isSeasonGameplayServer()) {
      return "이 서버에서는 티어 강화를 사용할 수 없습니다.";
    }
    if (!cardsEnabled() || !cardsEnabledInCurrentState()) {
      return "현재 라운드 상태에서는 티어 강화가 비활성화되어 있습니다.";
    }
    if (!cardsEffectLogicEnabled()) {
      return "카드 효과가 임시 비활성화되어 있습니다.";
    }
    if (!cardsTierUpgradeEnabled()) {
      return "티어 강화 기능이 비활성화되어 있습니다.";
    }
    if (viewer == null || targetUuid == null || !viewer.getUniqueId().equals(targetUuid)) {
      return "자신의 슬롯에서만 티어 강화가 가능합니다.";
    }
    if (data == null) {
      return "플레이어 데이터가 없어 강화할 수 없습니다.";
    }
    if (data.isOut()) {
      return "OUT 상태에서는 티어 강화를 사용할 수 없습니다.";
    }
    boolean blessingUpgradable = false;
    for (ActiveSeasonEffect effect : data.getBlessingEffects().values()) {
      if (effect != null && effect.getTier() < cardsMaxTier()) {
        blessingUpgradable = true;
        break;
      }
    }
    boolean curseUpgradable = false;
    for (ActiveSeasonEffect effect : data.getCurseEffects().values()) {
      if (effect != null && effect.getTier() < cardsMaxTier()) {
        curseUpgradable = true;
        break;
      }
    }
    if (!blessingUpgradable || !curseUpgradable) {
      return "축복/저주 양쪽에 강화 가능한 카드가 있어야 합니다.";
    }

    long cost = cardsTierUpgradeCostScore();
    if (cost > 0L && data.getScore() < cost) {
      return "점수가 부족합니다. 필요=" + cost + ", 보유=" + data.getScore();
    }
    return null;
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryDrag(InventoryDragEvent event) {
    if (event.getView().getTopInventory().getHolder() instanceof SeasonSlotInventoryHolder) {
      event.setCancelled(true);
      return;
    }

    if (!(event.getView().getTopInventory().getHolder() instanceof VaultInventoryHolder holder)) {
      return;
    }
    if (holder.mode() != VaultMode.WITHDRAW) {
      return;
    }

    int topSize = event.getView().getTopInventory().getSize();
    for (int rawSlot : event.getRawSlots()) {
      if (rawSlot < topSize) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (event.getHand() != EquipmentSlot.HAND) {
      return;
    }
    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Player player = event.getPlayer();
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    if (isActionBlockedByTemporaryGimmick(player)) {
      event.setCancelled(true);
      return;
    }
    if (handleEffect80ActiveInteract(event, player, data)) {
      return;
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player player = event.getPlayer();
    if (isBuildBlockedByTemporaryGimmick(player)) {
      event.setCancelled(true);
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityExplode(EntityExplodeEvent event) {
    if (!isSeasonGameplayServer() || event == null) {
      return;
    }
    protectReinforcedBuildBurstBlocks(event.blockList());
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockExplode(BlockExplodeEvent event) {
    if (!isSeasonGameplayServer() || event == null) {
      return;
    }
    protectReinforcedBuildBurstBlocks(event.blockList());
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerItemHeld(PlayerItemHeldEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player player = event.getPlayer();
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    if (isHotbarSlotLocked(player.getUniqueId(), event.getNewSlot())) {
      event.setCancelled(true);
      player.sendActionBar(ChatColor.RED + "해당 슬롯은 잠겨 있습니다");
      return;
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player player = event.getPlayer();
    if (player == null || !player.isOnline()) {
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    if (cursedSprintLockUntilEpochSecondByPlayer.getOrDefault(player.getUniqueId(), 0L) > nowEpochSecond()
        && player.isSprinting()) {
      player.setSprinting(false);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player player = event.getPlayer();
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player player = event.getPlayer();
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onCraftItem(CraftItemEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    handleBlessingB035CraftingGimmick(event, player, data);
  }

  @EventHandler(ignoreCancelled = true)
  public void onLootGenerate(LootGenerateEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    int lootPortalTier = highestEffect80Tier(data, 'B', 36);
    if (lootPortalTier <= 0) {
      return;
    }
    if (event.getLoot() == null || event.getLoot().isEmpty()) {
      return;
    }
    List<ItemStack> stored = lootPortalItemsByPlayer.computeIfAbsent(player.getUniqueId(), ignored -> new ArrayList<>());
    int capacitySlots = switch (Math.max(1, Math.min(4, lootPortalTier))) {
      case 1 -> 9;
      case 2 -> 18;
      case 3 -> 27;
      default -> 36;
    };
    for (ItemStack drop : event.getLoot()) {
      if (drop == null || drop.getType() == Material.AIR || drop.getAmount() <= 0) {
        continue;
      }
      if (stored.size() >= capacitySlots && !canAutoStackIntoLootPortal(stored, drop, lootPortalTier)) {
        break;
      }
      addItemToLootPortalStorage(stored, drop.clone(), lootPortalTier, capacitySlots);
    }
    lootPortalExpireEpochSecondByPlayer.put(player.getUniqueId(), nowEpochSecond() + 3600L);
    lootPortalTierByPlayer.put(player.getUniqueId(), Math.max(1, Math.min(4, lootPortalTier)));
  }

  @EventHandler(ignoreCancelled = true)
  public void onProjectileLaunch(ProjectileLaunchEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Projectile projectile)) {
      return;
    }
    if (!(projectile.getShooter() instanceof Player player)) {
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerDamage(EntityDamageEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Player victim)) {
      return;
    }
    if (hasNewbieProtection(victim.getUniqueId())) {
      event.setCancelled(true);
      return;
    }
    if (isInitialCardSetupProtected(victim)) {
      event.setCancelled(true);
      return;
    }

    PlayerRoundData victimData = players.get(victim.getUniqueId());
    if (victimData == null || victimData.isOut()) {
      return;
    }
    long now = nowEpochSecond();
    if (ghostUntilEpochSecond.getOrDefault(victim.getUniqueId(), 0L) > now) {
      event.setCancelled(true);
      return;
    }
    if (event.getCause() == EntityDamageEvent.DamageCause.FALL
        && blinkFallImmunityUntilEpochSecondByPlayer.getOrDefault(victim.getUniqueId(), 0L) > now) {
      event.setCancelled(true);
      return;
    }
    EnumMap<RuntimeModifierType, Double> totals = computeRuntimeModifierTotals(victimData, victim.getWorld(), victim);
    double takenRatio = runtimeModifierValue(totals, RuntimeModifierType.DAMAGE_TAKEN_RATIO);
    double takenMultiplier = boundedMultiplier(1.0D + takenRatio, 0.15D, 4.0D);
    if (Math.abs(takenMultiplier - 1.0D) > 0.0001D) {
      event.setDamage(event.getDamage() * takenMultiplier);
    }
    applyAbsorptionShieldDamageMitigation(victim, victimData, event);

    EntityDamageEvent.DamageCause cause = event.getCause();
    if (cause == EntityDamageEvent.DamageCause.FALL) {
      double fallRatio = runtimeModifierValue(totals, RuntimeModifierType.FALL_DAMAGE_RATIO);
      double fallMultiplier = boundedMultiplier(1.0D + fallRatio, 0.0D, 4.0D);
      if (Math.abs(fallMultiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * fallMultiplier);
      }
    }

    if (cause == EntityDamageEvent.DamageCause.STARVATION) {
      double ratio = runtimeModifierValue(totals, RuntimeModifierType.STARVATION_DAMAGE_RATIO);
      double multiplier = boundedMultiplier(1.0D + ratio, 0.0D, 5.0D);
      if (Math.abs(multiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * multiplier);
      }
    }

    if (isProjectileDamageCause(cause)) {
      double ratio = runtimeModifierValue(totals, RuntimeModifierType.PROJECTILE_DAMAGE_TAKEN_RATIO);
      double multiplier = boundedMultiplier(1.0D + ratio, 0.05D, 5.0D);
      if (Math.abs(multiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * multiplier);
      }
    }
    if (isExplosionDamageCause(cause)) {
      double ratio = runtimeModifierValue(totals, RuntimeModifierType.EXPLOSION_DAMAGE_TAKEN_RATIO);
      double multiplier = boundedMultiplier(1.0D + ratio, 0.05D, 5.0D);
      if (Math.abs(multiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * multiplier);
      }
    }
    if (isFireDamageCause(cause)) {
      double ratio = runtimeModifierValue(totals, RuntimeModifierType.FIRE_DAMAGE_TAKEN_RATIO);
      double multiplier = boundedMultiplier(1.0D + ratio, 0.05D, 5.0D);
      if (Math.abs(multiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * multiplier);
      }
    }
    if (cause == EntityDamageEvent.DamageCause.WITHER) {
      double ratio = runtimeModifierValue(totals, RuntimeModifierType.WITHER_DAMAGE_TAKEN_RATIO);
      double multiplier = boundedMultiplier(1.0D + ratio, 0.05D, 5.0D);
      if (Math.abs(multiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * multiplier);
      }
    }
    if (victim.getWorld().getEnvironment() == World.Environment.THE_END) {
      double ratio = runtimeModifierValue(totals, RuntimeModifierType.END_DAMAGE_TAKEN_RATIO);
      double multiplier = boundedMultiplier(1.0D + ratio, 0.05D, 5.0D);
      if (Math.abs(multiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * multiplier);
      }
    }
    if (victim.getHealth() > 0.0D) {
      double healthRatio = victim.getHealth() / Math.max(1.0D, victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH) == null
          ? victim.getMaxHealth()
          : victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
      if (healthRatio <= 0.35D) {
        double ratio = runtimeModifierValue(totals, RuntimeModifierType.LOW_HEALTH_DAMAGE_TAKEN_RATIO);
        double multiplier = boundedMultiplier(1.0D + ratio, 0.05D, 5.0D);
        if (Math.abs(multiplier - 1.0D) > 0.0001D) {
          event.setDamage(event.getDamage() * multiplier);
        }
      }
    }

	    if (victim.isBlocking()) {
	      double shieldRatio = runtimeModifierValue(totals, RuntimeModifierType.SHIELD_BLOCK_RATIO);
	      double shieldMultiplier = boundedMultiplier(1.0D - shieldRatio, 0.0D, 5.0D);
	      if (Math.abs(shieldMultiplier - 1.0D) > 0.0001D) {
	        event.setDamage(event.getDamage() * shieldMultiplier);
	      }
	    }

	    if (event instanceof EntityDamageByEntityEvent byEntityEvent) {
	      Player attackingPlayer = resolveAttackingPlayer(byEntityEvent.getDamager());
	      if (attackingPlayer == null) {
	        applySingleHitDamageCap(event, victim, playerDamageCapPveRatio());
      } else if (event.getEntity() instanceof Player) {
        applySingleHitDamageCap(event, victim, playerDamageCapPvpRatio());
      }
      return;
    }

	    applySingleHitDamageCap(event, victim, playerDamageCapEnvironmentRatio());
	  }


	  @EventHandler(ignoreCancelled = true)
  public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
	    if (!isSeasonGameplayServer()) {
	      return;
	    }
    Player attacker = resolveAttackingPlayer(event.getDamager());
    if (attacker != null && hasNewbieProtection(attacker.getUniqueId())) {
      event.setCancelled(true);
      return;
    }
    if (attacker != null && isActionBlockedByTemporaryGimmick(attacker)) {
      event.setCancelled(true);
      return;
    }
    if (attacker != null && isHotbarSlotLocked(attacker.getUniqueId(), attacker.getInventory().getHeldItemSlot())) {
      event.setCancelled(true);
      return;
    }
    if (attacker != null && isToolBanned(attacker, attacker.getInventory().getItemInMainHand())) {
      event.setCancelled(true);
      attacker.sendActionBar(ChatColor.RED + "현재 도구군이 봉인되었습니다");
      return;
    }

    if (event.getEntity() instanceof Player victim && hasNewbieProtection(victim.getUniqueId())) {
      event.setCancelled(true);
      return;
    }
    long combatNow = nowEpochSecond();
    if (attacker != null) {
      lastCombatEpochSecondByPlayer.put(attacker.getUniqueId(), combatNow);
    }
    if (event.getEntity() instanceof Player victimPlayer) {
      lastCombatEpochSecondByPlayer.put(victimPlayer.getUniqueId(), combatNow);
    }
    if (attacker == null || event.isCancelled()) {
      return;
    }

    PlayerRoundData attackerData = players.get(attacker.getUniqueId());
    if (attackerData == null || attackerData.isOut()) {
      return;
    }
    EnumMap<RuntimeModifierType, Double> totals = computeRuntimeModifierTotals(attackerData, attacker.getWorld(), attacker);
    double dealtRatio = runtimeModifierValue(totals, RuntimeModifierType.DAMAGE_DEALT_RATIO);
    double dealtMultiplier = boundedMultiplier(1.0D + dealtRatio, 0.15D, 4.0D);
    if (Math.abs(dealtMultiplier - 1.0D) > 0.0001D) {
      event.setDamage(event.getDamage() * dealtMultiplier);
    }

    if (event.getEntity() instanceof Player) {
      double pvpRatio = runtimeModifierValue(totals, RuntimeModifierType.PVP_DAMAGE_DEALT_RATIO);
      double pvpMultiplier = boundedMultiplier(1.0D + pvpRatio, 0.05D, 5.0D);
      if (Math.abs(pvpMultiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * pvpMultiplier);
      }
    } else {
      double mobRatio = runtimeModifierValue(totals, RuntimeModifierType.MOB_DAMAGE_DEALT_RATIO);
      double mobMultiplier = boundedMultiplier(1.0D + mobRatio, 0.05D, 5.0D);
      if (Math.abs(mobMultiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * mobMultiplier);
      }
    }

    double attackerMaxHealth = attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH) == null
        ? attacker.getMaxHealth()
        : attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
    if (attackerMaxHealth > 0.0D && (attacker.getHealth() / attackerMaxHealth) <= 0.35D) {
      double lowHealthRatio = runtimeModifierValue(totals, RuntimeModifierType.LOW_HEALTH_DAMAGE_DEALT_RATIO);
      double lowHealthMultiplier = boundedMultiplier(1.0D + lowHealthRatio, 0.05D, 5.0D);
      if (Math.abs(lowHealthMultiplier - 1.0D) > 0.0001D) {
        event.setDamage(event.getDamage() * lowHealthMultiplier);
      }
    }

    if (event.getEntity() instanceof Player victimPlayer) {
      PlayerRoundData victimData = players.get(victimPlayer.getUniqueId());
      if (victimData != null && !victimData.isOut()) {
        EnumMap<RuntimeModifierType, Double> victimTotals = computeRuntimeModifierTotals(victimData, victimPlayer.getWorld(), victimPlayer);
        double takenContextRatio = runtimeModifierValue(victimTotals, RuntimeModifierType.PVP_DAMAGE_TAKEN_RATIO);
        double takenContextMultiplier = boundedMultiplier(1.0D + takenContextRatio, 0.05D, 5.0D);
        if (Math.abs(takenContextMultiplier - 1.0D) > 0.0001D) {
          event.setDamage(event.getDamage() * takenContextMultiplier);
        }

        if (event.getDamager().getScoreboardTags().contains(STALKER_TAG)) {
          double stalkerRatio = runtimeModifierValue(victimTotals, RuntimeModifierType.STALKER_DAMAGE_TAKEN_RATIO);
          double stalkerMultiplier = boundedMultiplier(1.0D + stalkerRatio, 0.05D, 5.0D);
          if (Math.abs(stalkerMultiplier - 1.0D) > 0.0001D) {
            event.setDamage(event.getDamage() * stalkerMultiplier);
          }
        }
      }
      applyPoisonInjectionOnHit(event, attacker, attackerData);
      applySingleHitDamageCap(event, victimPlayer, playerDamageCapPvpRatio());
      return;
    }

    applyPoisonInjectionOnHit(event, attacker, attackerData);
  }

  private void applyPoisonInjectionOnHit(
      EntityDamageByEntityEvent event,
      Player attacker,
      PlayerRoundData attackerData
  ) {
    if (event == null || attacker == null || attackerData == null || event.isCancelled()) {
      return;
    }
    if (event.getFinalDamage() <= 0.0D) {
      return;
    }
    if (!(event.getEntity() instanceof LivingEntity victim) || victim == attacker) {
      return;
    }

    ActiveSeasonEffect poisonInjection = attackerData.getBlessingEffect("B-017");
    if (poisonInjection == null) {
      return;
    }

    int tier = clampTier(poisonInjection.getTier());
    int durationTicks = 20 * 10; // 10s
    int poisonAmplifier = Math.max(0, tier - 1); // Lv1~Lv4
    victim.addPotionEffect(
        new PotionEffect(PotionEffectType.POISON, durationTicks, poisonAmplifier, true, false, true),
        true
    );

    if (tier >= 2) {
      int witherAmplifier = tier >= 4 ? 2 : 1; // T3 Lv2, T4 Lv3
      victim.addPotionEffect(
          new PotionEffect(PotionEffectType.WITHER, durationTicks, witherAmplifier, true, false, true),
          true
      );
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerNaturalRegainHealth(EntityRegainHealthEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
      return;
    }
    double ratio = runtimeModifierValue(
        computeRuntimeModifierTotals(data, player.getWorld(), player),
        RuntimeModifierType.NATURAL_REGEN_RATIO
    );
    double multiplier = boundedMultiplier(1.0D + ratio, 0.0D, 4.0D);
    if (Math.abs(multiplier - 1.0D) > 0.0001D) {
      event.setAmount(Math.max(0.0D, event.getAmount() * multiplier));
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    int current = player.getFoodLevel();
    int next = Math.max(0, Math.min(20, event.getFoodLevel()));
    if (next == current) {
      return;
    }

    int gravityLadderTier = highestEffect80Tier(data, 'B', 10);
    if (gravityLadderTier >= 2 && next < current && isTouchingSolidWall(player)) {
      event.setFoodLevel(current);
      return;
    }

    if (next < current) {
      int loss = current - next;
      double ratio = runtimeModifierValue(
          computeRuntimeModifierTotals(data, player.getWorld(), player),
          RuntimeModifierType.HUNGER_DRAIN_RATIO
      );
      double multiplier = boundedMultiplier(1.0D + ratio, 0.0D, 4.0D);
      int adjustedLoss = Math.max(0, (int) Math.ceil(loss * multiplier));
      int adjustedNext = Math.max(0, current - adjustedLoss);
      event.setFoodLevel(adjustedNext);
      return;
    }

    if (next > current) {
      int gain = next - current;
      double ratio = runtimeModifierValue(
          computeRuntimeModifierTotals(data, player.getWorld(), player),
          RuntimeModifierType.FOOD_GAIN_RATIO
      );
      double multiplier = boundedMultiplier(1.0D + ratio, 0.0D, 4.0D);
      int adjustedGain = Math.max(0, (int) Math.ceil(gain * multiplier));
      int adjustedNext = Math.min(20, current + adjustedGain);
      event.setFoodLevel(adjustedNext);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerItemDamage(PlayerItemDamageEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player player = event.getPlayer();
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    double ratio = runtimeModifierValue(
        computeRuntimeModifierTotals(data, player.getWorld(), player),
        RuntimeModifierType.ITEM_DURABILITY_LOSS_RATIO
    );
    double multiplier = boundedMultiplier(1.0D + ratio, 0.0D, 5.0D);
    int adjusted = (int) Math.floor(event.getDamage() * multiplier);
    event.setDamage(Math.max(0, adjusted));
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerKnockback(EntityKnockbackEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    if (knockbackImmuneUntilEpochSecondByPlayer.getOrDefault(player.getUniqueId(), 0L) > nowEpochSecond()) {
      event.setCancelled(true);
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }

    Vector knockback = event.getKnockback();
    if (knockback == null) {
      return;
    }
    int antiGravityTier = highestEffect80Tier(data, 'X', 17);
    if (antiGravityTier > 0) {
      double hybridMultiplier = 1.16D;
      if (antiGravityTier >= 2 && !player.isOnGround()) {
        hybridMultiplier -= antiGravityTier >= 4 ? 0.20D : 0.12D;
      }
      event.setKnockback(knockback.clone().multiply(Math.max(0.6D, hybridMultiplier)));
      knockback = event.getKnockback();
      if (knockback == null) {
        return;
      }
    }

    double ratio = runtimeModifierValue(
        computeRuntimeModifierTotals(data, player.getWorld(), player),
        RuntimeModifierType.KNOCKBACK_TAKEN_RATIO
    );
    double multiplier = boundedMultiplier(1.0D + ratio, 0.0D, 5.0D);
    if (Math.abs(multiplier - 1.0D) > 0.0001D) {
      event.setKnockback(knockback.clone().multiply(multiplier));
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerAirChange(EntityAirChangeEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }

    int current = player.getRemainingAir();
    int next = event.getAmount();
    if (next >= current) {
      return;
    }

    int loss = current - next;
    double ratio = runtimeModifierValue(
        computeRuntimeModifierTotals(data, player.getWorld(), player),
        RuntimeModifierType.OXYGEN_DRAIN_RATIO
    );
    double multiplier = boundedMultiplier(1.0D + ratio, 0.0D, 5.0D);
    int adjustedLoss = Math.max(0, (int) Math.ceil(loss * multiplier));
    int adjustedNext = current - adjustedLoss;
    event.setAmount(Math.max(-20, adjustedNext));
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerPotionEffect(EntityPotionEffectEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      return;
    }
    PotionEffect newEffect = event.getNewEffect();
    if (newEffect == null || newEffect.getType() == null || newEffect.getType().isInstant()) {
      return;
    }

    RuntimeModifierType modifierType = runtimeDurationModifierType(newEffect.getType());
    if (modifierType == null) {
      return;
    }

    String guardKey = potionGuardKey(player.getUniqueId(), newEffect.getType());
    if (runtimePotionAdjustBypass.remove(guardKey)) {
      return;
    }

    double ratio = runtimeModifierValue(
        computeRuntimeModifierTotals(data, player.getWorld(), player),
        modifierType
    );
    if (Math.abs(ratio) < 0.0001D) {
      return;
    }

    int originalDuration = Math.max(0, newEffect.getDuration());
    if (originalDuration <= 1) {
      return;
    }

    double multiplier = boundedMultiplier(1.0D + ratio, 0.0D, 4.0D);
    int adjustedDuration = Math.max(0, (int) Math.floor(originalDuration * multiplier));
    if (adjustedDuration == originalDuration) {
      return;
    }

    event.setCancelled(true);
    if (adjustedDuration <= 1) {
      return;
    }

    PotionEffect adjusted = new PotionEffect(
        newEffect.getType(),
        adjustedDuration,
        newEffect.getAmplifier(),
        newEffect.isAmbient(),
        newEffect.hasParticles(),
        newEffect.hasIcon()
    );
    runtimePotionAdjustBypass.add(guardKey);
    Bukkit.getScheduler().runTask(this, () -> {
      try {
        if (player.isOnline() && !player.isDead()) {
          player.addPotionEffect(adjusted, true);
        }
      } finally {
        runtimePotionAdjustBypass.remove(guardKey);
      }
    });
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDragonDamagedByPlayer(EntityDamageByEntityEvent event) {
    if (!isSeasonGameplayServer() || !scoreDragonJackpotEnabled()) {
      return;
    }
    if (!(event.getEntity() instanceof EnderDragon)) {
      return;
    }
    if (!scoreDragonDamageStateAllowed()) {
      return;
    }

    Player attacker = resolveAttackingPlayer(event.getDamager());
    if (attacker == null || !isPlayerEligibleForScoreGain(attacker)) {
      return;
    }

    double damage = Math.max(0.0D, event.getFinalDamage());
    if (damage <= 0.0D) {
      return;
    }

    UUID uuid = attacker.getUniqueId();
    PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(uuid);
    double ratio = runtimeModifierValue(
        computeRuntimeModifierTotals(data, attacker.getWorld(), attacker),
        RuntimeModifierType.DRAGON_DAMAGE_RATIO
    );
    double multiplier = boundedMultiplier(1.0D + ratio, 0.1D, 5.0D);
    dragonDamageByPlayer.merge(uuid, damage * multiplier, Double::sum);
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (!isSeasonGameplayServer() || !scoreMiningEnabled() || !scoreGainEnabledInCurrentState()) {
      return;
    }

    Player player = event.getPlayer();
    if (isHotbarSlotLocked(player.getUniqueId(), player.getInventory().getHeldItemSlot())) {
      event.setCancelled(true);
      player.sendActionBar(ChatColor.RED + "해당 슬롯은 잠겨 있습니다");
      return;
    }
    if (isToolBanned(player, player.getInventory().getItemInMainHand())) {
      event.setCancelled(true);
      player.sendActionBar(ChatColor.RED + "현재 도구군이 봉인되었습니다");
      return;
    }
    if (!isPlayerEligibleForScoreGain(player)) {
      return;
    }

    UUID uuid = player.getUniqueId();
    PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(uuid);
    long points = scoreMiningPoints(event.getBlock().getType());
    if (points <= 0L) {
      return;
    }

    long adjusted = applyRuntimeScoreModifier(points, data, player.getWorld(), RuntimeModifierType.MINING_SCORE_RATIO);
    addGeneratedScore(uuid, adjusted);
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityPickupItem(EntityPickupItemEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    UUID pickedItemId = event.getItem() == null ? null : event.getItem().getUniqueId();
    if (pickedItemId != null) {
      cursedDropOwnerByItem.remove(pickedItemId);
      cursedDropExpireEpochSecondByItem.remove(pickedItemId);
    }
    if (!scoreDragonJackpotEnabled()) {
      return;
    }
    if (dragonEggBonusOwner != null) {
      return;
    }
    if (event.getItem().getItemStack().getType() != Material.DRAGON_EGG) {
      return;
    }
    if (!(state == SeasonState.CLIMAX || state == SeasonState.FREE_PLAY)) {
      return;
    }
    if (!isPlayerEligibleForScoreGain(player)) {
      return;
    }

    UUID uuid = player.getUniqueId();
    data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(uuid);

    long bonus = scoreDragonFirstEggBonus();
    if (bonus <= 0L) {
      return;
    }

    dragonEggBonusOwner = uuid;
    long adjustedBonus = applyRuntimeScoreModifier(bonus, data, player.getWorld(), RuntimeModifierType.DRAGON_JACKPOT_RATIO);
    addFlatScore(uuid, adjustedBonus);
    Bukkit.broadcastMessage("[Season] Dragon Egg first pickup: " + player.getName() + " +" + adjustedBonus + " score.");
    saveState();
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (!isSeasonGameplayServer()) {
      return;
    }
    Player victim = event.getEntity();
    UUID victimId = victim.getUniqueId();

    PlayerRoundData victimData = players.computeIfAbsent(victimId, ignored -> new PlayerRoundData(baseLives()));
    participants.add(victimId);

    int lootPortalTier = highestEffect80Tier(victimData, 'B', 20);
    if (lootPortalTier >= 3 && event.getDrops() != null && !event.getDrops().isEmpty()) {
      UUID victimUuid = victim.getUniqueId();
      List<ItemStack> stored = lootPortalItemsByPlayer.computeIfAbsent(victimUuid, ignored -> new ArrayList<>());
      int capacitySlots = switch (Math.max(1, Math.min(4, lootPortalTier))) {
        case 1 -> 9;
        case 2 -> 18;
        case 3 -> 27;
        default -> 36;
      };
      for (ItemStack drop : new ArrayList<>(event.getDrops())) {
        if (drop == null || drop.getType() == Material.AIR || drop.getAmount() <= 0) {
          continue;
        }
        addItemToLootPortalStorage(stored, drop.clone(), lootPortalTier, capacitySlots);
      }
      event.getDrops().clear();
      lootPortalTierByPlayer.put(victimUuid, lootPortalTier);
      lootPortalExpireEpochSecondByPlayer.put(victimUuid, nowEpochSecond() + 3600L);
    }
    if (lootPortalTier >= 4
        && lootPortalItemsByPlayer.containsKey(victimId)
        && !lootPortalItemsByPlayer.getOrDefault(victimId, List.of()).isEmpty()) {
      long portalWindow = 180L;
      lootPortalExpireEpochSecondByPlayer.put(victimId, Math.max(
          lootPortalExpireEpochSecondByPlayer.getOrDefault(victimId, 0L),
          nowEpochSecond() + portalWindow
      ));
    }

    if (victimData.isOut()) {
      return;
    }
    if (state == SeasonState.FREE_PLAY && freePlayIgnoresLifeRules()) {
      return;
    }
    applyPvpSteal(victim, victimData);

    victimData.setDeathCount(victimData.getDeathCount() + 1);

    if (victimData.getLivesRemaining() > 0) {
      victimData.setLivesRemaining(victimData.getLivesRemaining() - 1);
      victim.sendMessage("[Season] You used one free life. remaining=" + victimData.getLivesRemaining());
    } else {
      int overDeaths = Math.max(0, victimData.getDeathCount() - baseLives());
      long respawnCost = (long) respawnCostBase() * (long) overDeaths;
      if (respawnCost > 0L) {
        double ratio = runtimeModifierValue(
            computeRuntimeModifierTotals(victimData, victim.getWorld(), victim),
            RuntimeModifierType.RESPAWN_COST_RATIO
        );
        double multiplier = boundedMultiplier(1.0D + ratio, 0.05D, 5.0D);
        respawnCost = Math.max(0L, (long) Math.floor(respawnCost * multiplier));
      }

      if (respawnCost <= 0L || victimData.getScore() >= respawnCost) {
        adjustPlayerScore(victimId, victimData, -respawnCost, false);
        victim.sendMessage("[Season] Respawn cost paid: " + respawnCost + " score.");
      } else {
        markOut(victim, victimData);
      }
    }

    updateResetPendingState();
    saveState();
  }

  private void applyPvpSteal(Player victim, PlayerRoundData victimData) {
    Player killer = victim.getKiller();
    if (killer == null || Objects.equals(killer.getUniqueId(), victim.getUniqueId())) {
      return;
    }

    UUID killerId = killer.getUniqueId();
    PlayerRoundData killerData = players.computeIfAbsent(killerId, ignored -> new PlayerRoundData(baseLives()));
    participants.add(killerId);

    double stealPercent = pvpStealPercent();
    if (stealPercent <= 0.0D) {
      return;
    }

    EnumMap<RuntimeModifierType, Double> killerTotals = computeRuntimeModifierTotals(killerData, killer.getWorld(), killer);
    EnumMap<RuntimeModifierType, Double> victimTotals = computeRuntimeModifierTotals(victimData, victim.getWorld(), victim);
    double killerRatio = runtimeModifierValue(killerTotals, RuntimeModifierType.PVP_STEAL_GAIN_RATIO);
    double victimRatio = runtimeModifierValue(victimTotals, RuntimeModifierType.PVP_STEAL_TAKEN_RATIO);
    double killerMultiplier = boundedMultiplier(1.0D + killerRatio, 0.05D, 4.0D);
    double victimMultiplier = boundedMultiplier(1.0D + victimRatio, 0.05D, 4.0D);
    double finalStealPercent = stealPercent * killerMultiplier * victimMultiplier;
    if (finalStealPercent <= 0.0D) {
      return;
    }

    long steal = (long) Math.floor(victimData.getScore() * finalStealPercent);
    steal = Math.min(steal, victimData.getScore());
    if (steal <= 0L) {
      return;
    }

    adjustPlayerScore(victim.getUniqueId(), victimData, -steal, false);
    adjustPlayerScore(killerId, killerData, steal, true);

    killer.sendMessage("[Season] PvP steal +" + steal + " score.");
    victim.sendMessage("[Season] PvP steal -" + steal + " score.");
  }

  private void markOut(Player player, PlayerRoundData data) {
    data.setOut(true);
    if (outSetsScoreZero()) {
      setPlayerScore(player.getUniqueId(), data, 0L, false);
    }

    player.sendMessage("[Season] You are OUT and cannot continue this round.");

    if (enforceOutSpectator()) {
      Bukkit.getScheduler().runTask(this, () -> enforceSpectator(player));
    }
  }

  private void enforceSpectator(Player player) {
    if (player.getGameMode() != GameMode.SPECTATOR) {
      player.setGameMode(GameMode.SPECTATOR);
    }
  }

  private Player resolveAttackingPlayer(Entity damager) {
    if (damager instanceof Player player) {
      return player;
    }
    if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
      return shooter;
    }
    return null;
  }

  private LivingEntity resolveDamageSourceLivingEntity(Entity damager) {
    if (damager instanceof LivingEntity living) {
      return living;
    }
    if (damager instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter) {
      return shooter;
    }
    if (damager instanceof TNTPrimed tnt && tnt.getSource() instanceof LivingEntity source) {
      return source;
    }
    return null;
  }

  private Player resolveExplosionSourcePlayer(Entity damager) {
    if (damager instanceof Player player) {
      return player;
    }
    if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
      return shooter;
    }
    if (damager instanceof TNTPrimed tnt && tnt.getSource() instanceof Player source) {
      return source;
    }
    return null;
  }

  private boolean hasNewbieProtection(UUID uuid) {
    PlayerRoundData data = players.get(uuid);
    return data != null && nowEpochSecond() < data.getNewbieProtectUntilEpochSecond();
  }

  private void tickNewbieProtectionEffects() {
    if (!newbieEffectsEnabled()) {
      return;
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!hasNewbieProtection(player.getUniqueId())) {
        continue;
      }
      applyNewbieProtectionEffects(player);
    }
  }

  private void applyNewbieProtectionEffects(Player player) {
    if (player == null || !player.isOnline() || !newbieEffectsEnabled()) {
      return;
    }
    int durationTicks = newbieEffectRefreshTicks();
    applyNewbieEffect(player, PotionEffectType.SPEED, newbieEffectSpeedAmplifier(), durationTicks);
    applyNewbieEffect(player, PotionEffectType.REGENERATION, newbieEffectRegenerationAmplifier(), durationTicks);
    applyNewbieEffect(player, PotionEffectType.RESISTANCE, newbieEffectResistanceAmplifier(), durationTicks);
  }

  private void applyNewbieEffect(Player player, PotionEffectType type, int amplifier, int durationTicks) {
    if (type == null || amplifier < 0 || durationTicks <= 0) {
      return;
    }
    player.addPotionEffect(new PotionEffect(
        type,
        durationTicks,
        amplifier,
        true,
        false,
        true
    ));
  }

  private void reloadEffectCatalog() {
    blessingEffectsCatalog.clear();
    curseEffectsCatalog.clear();
    effectDefinitionsById.clear();
    effectGimmicksById.clear();

    boolean loadedFromFile = loadEffectCatalogFromFile();
    if (!loadedFromFile && cardsCatalogFallbackEnabled()) {
      loadEffectCatalogFromConfigPools();
    } else if (!loadedFromFile) {
      getLogger().warning("No catalog files loaded. catalog fallback is disabled.");
    }
    if ((blessingEffectsCatalog.isEmpty() || curseEffectsCatalog.isEmpty()) && cardsCatalogFallbackEnabled()) {
      loadBuiltInSeasonOneCatalog();
    } else if (blessingEffectsCatalog.isEmpty() || curseEffectsCatalog.isEmpty()) {
      getLogger().warning(
          "Catalog loaded but blessing/curse pool is incomplete (blessings=" + blessingEffectsCatalog.size()
              + ", curses=" + curseEffectsCatalog.size() + "). catalog fallback is disabled."
      );
    }

    for (EffectDefinition definition : blessingEffectsCatalog) {
      effectDefinitionsById.put(definition.id().toUpperCase(Locale.ROOT), definition);
    }
    for (EffectDefinition definition : curseEffectsCatalog) {
      effectDefinitionsById.put(definition.id().toUpperCase(Locale.ROOT), definition);
    }
    if (cardsRuntimeEffectsEnabled()) {
      buildEffectGimmicksIndex();
    } else {
      effectGimmicksById.clear();
    }

    getLogger().info(
        "Loaded effects catalog: blessings=" + blessingEffectsCatalog.size()
            + ", curses=" + curseEffectsCatalog.size()
    );
  }

  private boolean loadEffectCatalogFromFile() {
    File catalogDir = new File(getDataFolder(), "catalogs");
    if (!catalogDir.exists() && !catalogDir.mkdirs()) {
      return false;
    }

    LinkedHashSet<String> filesToLoad = new LinkedHashSet<>();
    appendCatalogFileName(filesToLoad, cardsCatalogFileName());
    for (String appendFileName : cardsCatalogAppendFileNames()) {
      appendCatalogFileName(filesToLoad, appendFileName);
    }
    for (String patchFileName : resolveCatalogPatchFileNames(catalogDir)) {
      appendCatalogFileName(filesToLoad, patchFileName);
    }

    int count = 0;
    for (String fileName : filesToLoad) {
      count += loadEffectCatalogFile(catalogDir, fileName, cardsCatalogAutoCreate());
    }
    return count > 0;
  }

  private int loadEffectCatalogFile(File catalogDir, String fileName, boolean autoCreate) {
    if (catalogDir == null || fileName == null || fileName.isBlank()) {
      return 0;
    }

    String normalizedFileName = fileName.trim();
    if (normalizedFileName.isBlank()) {
      return 0;
    }

    File catalogFile = new File(catalogDir, normalizedFileName);
    String resourcePath = "catalogs/" + normalizedFileName;
    boolean syncedFromBundled = false;
    if (cardsCatalogSyncFromResource()) {
      try {
        saveResource(resourcePath, true);
        syncedFromBundled = true;
      } catch (IllegalArgumentException ignored) {
        // no bundled catalog file
      }
    }
    if (!catalogFile.exists() && autoCreate && !syncedFromBundled) {
      try {
        saveResource(resourcePath, false);
      } catch (IllegalArgumentException ignored) {
        // no bundled catalog file
      }
    }
    if (!catalogFile.exists()) {
      return 0;
    }

    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(catalogFile);
    int removed = removeEffectDefinitions(parseCatalogRemovalIds(yaml));
    int count = 0;
    count += loadEffectCatalogSection(yaml.getConfigurationSection("blessings"), EffectKind.BLESSING);
    count += loadEffectCatalogSection(yaml.getConfigurationSection("curses"), EffectKind.CURSE);
    if (count > 0 || removed > 0) {
      getLogger().info(
          "Loaded catalog file '" + normalizedFileName + "' entries=" + count + " removed=" + removed
      );
    }
    return count + removed;
  }

  private List<String> resolveCatalogPatchFileNames(File catalogDir) {
    LinkedHashSet<String> files = new LinkedHashSet<>();
    for (String configured : cardsCatalogPatchFileNames()) {
      appendCatalogFileName(files, configured);
    }
    if (cardsCatalogPatchAutoDiscover()) {
      for (String discovered : discoverCatalogPatchFileNames(catalogDir, cardsCatalogPatchDirName())) {
        appendCatalogFileName(files, discovered);
      }
    }
    if (files.isEmpty()) {
      return List.of();
    }
    return new ArrayList<>(files);
  }

  private void appendCatalogFileName(Set<String> files, String fileName) {
    if (files == null || fileName == null || fileName.isBlank()) {
      return;
    }
    String normalized = fileName.trim().replace('\\', '/');
    while (normalized.startsWith("./")) {
      normalized = normalized.substring(2);
    }
    if (normalized.isBlank()) {
      return;
    }
    if (normalized.startsWith("/") || normalized.contains("..")) {
      getLogger().warning("Ignored catalog file path outside catalogs/: " + normalized);
      return;
    }
    files.add(normalized);
  }

  private List<String> discoverCatalogPatchFileNames(File catalogDir, String patchDirName) {
    if (catalogDir == null || patchDirName == null || patchDirName.isBlank()) {
      return List.of();
    }
    File patchDir = new File(catalogDir, patchDirName);
    if (!patchDir.exists() && !patchDir.mkdirs()) {
      getLogger().warning("Could not create catalog patch directory: " + patchDir.getAbsolutePath());
      return List.of();
    }
    if (!patchDir.isDirectory()) {
      return List.of();
    }

    List<String> discovered = new ArrayList<>();
    collectCatalogPatchFileNames(catalogDir, patchDir, discovered);
    Collections.sort(discovered);
    return discovered;
  }

  private void collectCatalogPatchFileNames(File catalogDir, File currentDir, List<String> output) {
    if (catalogDir == null || currentDir == null || output == null) {
      return;
    }
    File[] entries = currentDir.listFiles();
    if (entries == null || entries.length == 0) {
      return;
    }
    Arrays.sort(entries, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
    for (File entry : entries) {
      if (entry == null) {
        continue;
      }
      if (entry.isDirectory()) {
        collectCatalogPatchFileNames(catalogDir, entry, output);
        continue;
      }
      String lowerName = entry.getName().toLowerCase(Locale.ROOT);
      if (!lowerName.endsWith(".yml") && !lowerName.endsWith(".yaml")) {
        continue;
      }
      String relativePath = toCatalogRelativePath(catalogDir, entry);
      if (!relativePath.isBlank()) {
        output.add(relativePath);
      }
    }
  }

  private String toCatalogRelativePath(File rootDir, File targetFile) {
    if (rootDir == null || targetFile == null) {
      return "";
    }
    try {
      String rootPath = rootDir.getCanonicalPath();
      String targetPath = targetFile.getCanonicalPath();
      String rootPrefix = rootPath.endsWith(File.separator) ? rootPath : rootPath + File.separator;
      if (!targetPath.startsWith(rootPrefix)) {
        return "";
      }
      String relative = targetPath.substring(rootPrefix.length()).replace(File.separatorChar, '/');
      if (relative.startsWith("/") || relative.contains("..")) {
        return "";
      }
      return relative;
    } catch (IOException exception) {
      getLogger().warning("Failed to resolve catalog relative path: " + exception.getMessage());
      return "";
    }
  }

  private Set<String> parseCatalogRemovalIds(YamlConfiguration yaml) {
    if (yaml == null) {
      return Set.of();
    }
    LinkedHashSet<String> removed = new LinkedHashSet<>();
    addCatalogRemovalIds(removed, yaml.get("remove"));
    addCatalogRemovalIds(removed, yaml.get("disable"));
    if (removed.isEmpty()) {
      return Set.of();
    }
    return removed;
  }

  private void addCatalogRemovalIds(Set<String> target, Object rawValue) {
    if (target == null || rawValue == null) {
      return;
    }

    if (rawValue instanceof ConfigurationSection section) {
      for (String key : section.getKeys(false)) {
        if (key == null || key.isBlank()) {
          continue;
        }
        Object value = section.get(key);
        if (value instanceof Boolean enabledFlag) {
          if (enabledFlag) {
            String normalized = normalizeEffectId(key);
            if (!normalized.isBlank()) {
              target.add(normalized);
            }
          }
          continue;
        }
        if ("ids".equalsIgnoreCase(key)
            || "effects".equalsIgnoreCase(key)
            || "all".equalsIgnoreCase(key)
            || "blessings".equalsIgnoreCase(key)
            || "curses".equalsIgnoreCase(key)) {
          addCatalogRemovalIds(target, value);
        }
      }
      return;
    }

    for (String token : parseMapStringTokens(rawValue)) {
      String normalized = normalizeEffectId(token);
      if (!normalized.isBlank()) {
        target.add(normalized);
      }
    }
  }

  private int removeEffectDefinitions(Set<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }
    int removed = 0;
    removed += removeEffectDefinitions(blessingEffectsCatalog, ids);
    removed += removeEffectDefinitions(curseEffectsCatalog, ids);
    return removed;
  }

  private int removeEffectDefinitions(List<EffectDefinition> target, Set<String> ids) {
    if (target == null || target.isEmpty() || ids == null || ids.isEmpty()) {
      return 0;
    }
    int before = target.size();
    target.removeIf(definition -> definition != null && ids.contains(normalizeEffectId(definition.id())));
    return Math.max(0, before - target.size());
  }

  private int loadEffectCatalogSection(ConfigurationSection section, EffectKind fallbackKind) {
    if (section == null || fallbackKind == null) {
      return 0;
    }

    int loaded = 0;
    for (String key : section.getKeys(false)) {
      EffectDefinition definition = parseEffectDefinition(section, key, fallbackKind);
      if (definition != null && upsertEffectDefinition(definition)) {
        loaded++;
      }
    }
    return loaded;
  }

  private boolean upsertEffectDefinition(EffectDefinition definition) {
    if (definition == null || definition.id() == null || definition.id().isBlank()) {
      return false;
    }

    List<EffectDefinition> target = definition.kind() == EffectKind.CURSE
        ? curseEffectsCatalog
        : blessingEffectsCatalog;
    String normalizedId = normalizeEffectId(definition.id());
    for (int i = 0; i < target.size(); i++) {
      EffectDefinition existing = target.get(i);
      if (existing != null && normalizedId.equals(normalizeEffectId(existing.id()))) {
        target.set(i, definition);
        return true;
      }
    }
    target.add(definition);
    return true;
  }

  private void loadEffectCatalogFromConfigPools() {
    ConfigurationSection blessingSection = getConfig().getConfigurationSection("cards.blessing_pool");
    if (blessingSection != null) {
      for (String key : blessingSection.getKeys(false)) {
        EffectDefinition definition = parseEffectDefinition(blessingSection, key, EffectKind.BLESSING);
        if (definition != null) {
          blessingEffectsCatalog.add(definition);
        }
      }
    }

    ConfigurationSection curseSection = getConfig().getConfigurationSection("cards.curse_pool");
    if (curseSection != null) {
      for (String key : curseSection.getKeys(false)) {
        EffectDefinition definition = parseEffectDefinition(curseSection, key, EffectKind.CURSE);
        if (definition != null) {
          curseEffectsCatalog.add(definition);
        }
      }
    }
  }

  private EffectDefinition parseEffectDefinition(ConfigurationSection section, String key, EffectKind fallbackKind) {
    if (section == null || key == null || key.isBlank()) {
      return null;
    }

    String id = normalizeEffectId(key);
    if (id.isBlank()) {
      return null;
    }

    ConfigurationSection entry = section.getConfigurationSection(key);
    if (entry == null) {
      Object raw = section.get(key);
      if (!(raw instanceof Number numeric)) {
        return null;
      }
      double weight = Math.max(0.0001D, numeric.doubleValue());
      EffectKind kind = inferEffectKind(id, fallbackKind);
      EffectArchetype archetype = inferArchetype(id);
      return new EffectDefinition(id, id, kind, weight, archetype, false, 0L, null);
    }

    String name = entry.getString("name", id);
    double weight = Math.max(0.0001D, entry.getDouble("weight", 1.0D));
    EffectKind kind = inferEffectKind(entry.getString("type"), fallbackKind, id);
    EffectArchetype archetype = parseEffectArchetype(entry.getString("archetype"), id);
    boolean severe = kind == EffectKind.CURSE && entry.getBoolean("severe", false);
    long severeBonus = Math.max(0L, entry.getLong("severe_bonus_points", 0L));
    EffectRuntimeProfile runtimeProfile = parseEffectRuntimeProfile(entry);

    return new EffectDefinition(id, name, kind, weight, archetype, severe, severeBonus, runtimeProfile);
  }

  private void loadBuiltInSeasonOneCatalog() {
    for (int i = 1; i <= 120; i++) {
      String id = String.format(Locale.ROOT, "B-%03d", i);
      blessingEffectsCatalog.add(new EffectDefinition(
          id,
          "Blessing " + id,
          EffectKind.BLESSING,
          1.0D,
          inferArchetype(id),
          false,
          0L,
          null
      ));
    }

    Set<Integer> severeCurseIds = Set.of(69, 76, 78, 90, 101, 106, 118, 119, 120);
    for (int i = 1; i <= 120; i++) {
      String id = String.format(Locale.ROOT, "C-%03d", i);
      boolean severe = severeCurseIds.contains(i);
      curseEffectsCatalog.add(new EffectDefinition(
          id,
          "Curse " + id,
          EffectKind.CURSE,
          1.0D,
          inferArchetype(id),
          severe,
          severe ? cardsSevereCurseDefaultBonusPoints() : 0L,
          null
      ));
    }
  }

  private EffectKind inferEffectKind(String configured, EffectKind fallback, String id) {
    if (configured != null) {
      String normalized = configured.trim().toUpperCase(Locale.ROOT);
      if ("BLESS".equals(normalized) || "BLESSING".equals(normalized)) {
        return EffectKind.BLESSING;
      }
      if ("CURSE".equals(normalized) || "CURSED".equals(normalized)) {
        return EffectKind.CURSE;
      }
    }
    return inferEffectKind(id, fallback);
  }

  private EffectKind inferEffectKind(String id, EffectKind fallback) {
    if (id != null) {
      String normalized = id.trim().toUpperCase(Locale.ROOT);
      if (normalized.startsWith("B-")) {
        return EffectKind.BLESSING;
      }
      if (normalized.startsWith("C-")) {
        return EffectKind.CURSE;
      }
    }
    return fallback == null ? EffectKind.BLESSING : fallback;
  }

  private EffectArchetype parseEffectArchetype(String configured, String id) {
    if (configured != null && !configured.isBlank()) {
      try {
        return EffectArchetype.valueOf(configured.trim().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException ignored) {
        // fallback below
      }
    }
    return inferArchetype(id);
  }

  private EffectRuntimeProfile parseEffectRuntimeProfile(ConfigurationSection entry) {
    if (entry == null) {
      return null;
    }
    ConfigurationSection runtime = entry.getConfigurationSection("runtime");
    if (runtime == null) {
      return null;
    }

    List<RuntimeModifierRule> modifierRules = new ArrayList<>();
    List<Map<?, ?>> rawModifierRules = runtime.getMapList("modifiers");
    if (rawModifierRules != null) {
      for (Map<?, ?> raw : rawModifierRules) {
        RuntimeModifierRule rule = parseRuntimeModifierRule(raw);
        if (rule != null) {
          modifierRules.add(rule);
        }
      }
    }

    Double scoreMultiplierPerTier = null;
    if (runtime.contains("score_multiplier_per_tier")) {
      scoreMultiplierPerTier = runtime.getDouble("score_multiplier_per_tier");
    }
    ConfigurationSection scoreSection = runtime.getConfigurationSection("score_multiplier");
    if (scoreSection != null && scoreSection.contains("per_tier")) {
      scoreMultiplierPerTier = scoreSection.getDouble("per_tier");
    }

    List<String> detailKo = runtime.getStringList("detail_ko");
    List<String> normalizedDetailKo = new ArrayList<>();
    if (detailKo != null) {
      for (String line : detailKo) {
        if (line != null && !line.isBlank()) {
          String normalizedLine = normalizeRuntimeDetailLine(line);
          if (!normalizedLine.isBlank()) {
            normalizedDetailKo.add(normalizedLine);
          }
        }
      }
    }

    if (modifierRules.isEmpty() && scoreMultiplierPerTier == null && normalizedDetailKo.isEmpty()) {
      return null;
    }
    return new EffectRuntimeProfile(
        List.copyOf(modifierRules),
        scoreMultiplierPerTier,
        List.copyOf(normalizedDetailKo)
    );
  }

  private String normalizeRuntimeDetailLine(String line) {
    if (line == null || line.isBlank()) {
      return "";
    }
    String normalized = line.trim();
    normalized = normalized.replace("카드 기믹", "특수 효과");
    if (normalized.startsWith("기믹:")) {
      normalized = "특수 효과:" + normalized.substring("기믹:".length());
    }
    return normalized.trim();
  }

  private RuntimeModifierRule parseRuntimeModifierRule(Map<?, ?> raw) {
    if (raw == null || raw.isEmpty()) {
      return null;
    }
    String typeRaw = parseMapString(raw, "type");
    RuntimeModifierType type = parseRuntimeModifierType(typeRaw);
    if (type == null) {
      getLogger().warning("Skipping runtime modifier rule due to invalid type: " + typeRaw);
      return null;
    }
    double valuePerTier = parseMapDouble(raw, "value_per_tier", Double.NaN);
    if (!Double.isFinite(valuePerTier)) {
      valuePerTier = parseMapDouble(raw, "per_tier", Double.NaN);
    }
    if (!Double.isFinite(valuePerTier)) {
      valuePerTier = parseMapDouble(raw, "value", Double.NaN);
    }
    if (!Double.isFinite(valuePerTier)) {
      getLogger().warning("Skipping runtime modifier rule due to missing value_per_tier: " + typeRaw);
      return null;
    }

    int minTotalTier = Math.max(1, parseMapInt(raw, "min_total_tier", 1));
    TierScalingMode scalingMode = parseTierScalingMode(parseMapString(raw, "scaling"));
    RuntimeWorldScope worldScope = parseRuntimeWorldScope(parseMapString(raw, "world"));
    Set<RuntimeCondition> conditions = parseRuntimeConditions(raw);
    String labelKo = parseMapString(raw, "label_ko");
    if (labelKo.isBlank()) {
      labelKo = runtimeModifierLabelKo(type);
    }

    return new RuntimeModifierRule(
        type,
        valuePerTier,
        minTotalTier,
        scalingMode,
        worldScope,
        conditions,
        labelKo
    );
  }

  private Set<RuntimeCondition> parseRuntimeConditions(Map<?, ?> raw) {
    if (raw == null || raw.isEmpty()) {
      return Set.of();
    }
    Set<String> tokens = new HashSet<>();
    tokens.addAll(parseMapStringTokens(raw.get("condition")));
    tokens.addAll(parseMapStringTokens(raw.get("conditions")));
    if (tokens.isEmpty()) {
      return Set.of();
    }
    EnumSet<RuntimeCondition> resolved = EnumSet.noneOf(RuntimeCondition.class);
    for (String token : tokens) {
      RuntimeCondition condition = parseRuntimeCondition(token);
      if (condition != null) {
        resolved.add(condition);
      } else {
        getLogger().warning("Unknown runtime condition token ignored: " + token);
      }
    }
    return resolved.isEmpty() ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(resolved));
  }

  private Set<String> parseMapStringTokens(Object rawValue) {
    if (rawValue == null) {
      return Set.of();
    }
    Set<String> tokens = new HashSet<>();
    if (rawValue instanceof Collection<?> collection) {
      for (Object value : collection) {
        if (value == null) {
          continue;
        }
        String text = String.valueOf(value).trim();
        if (!text.isBlank()) {
          tokens.add(text);
        }
      }
      return tokens;
    }
    String text = String.valueOf(rawValue).trim();
    if (text.isBlank()) {
      return Set.of();
    }
    for (String token : text.split(",")) {
      String normalized = token == null ? "" : token.trim();
      if (!normalized.isBlank()) {
        tokens.add(normalized);
      }
    }
    return tokens;
  }

  private RuntimeCondition parseRuntimeCondition(String configured) {
    if (configured == null || configured.isBlank()) {
      return null;
    }
    String normalized = configured.trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "SNEAK", "SNEAKING" -> RuntimeCondition.SNEAKING;
      case "SPRINT", "SPRINTING" -> RuntimeCondition.SPRINTING;
      case "BLOCK", "BLOCKING", "SHIELD_BLOCKING" -> RuntimeCondition.BLOCKING;
      case "ON_ICE", "ICE", "ICY_SURFACE" -> RuntimeCondition.ON_ICE;
      case "COLD_BIOME", "IN_COLD_BIOME", "SNOW_BIOME" -> RuntimeCondition.IN_COLD_BIOME;
      case "HOT_BIOME", "IN_HOT_BIOME", "DESERT_BIOME" -> RuntimeCondition.IN_HOT_BIOME;
      case "CAVE_BIOME", "IN_CAVE_BIOME" -> RuntimeCondition.IN_CAVE_BIOME;
      case "OUTSIDE_BORDER", "BORDER_OUTSIDE" -> RuntimeCondition.OUTSIDE_BORDER;
      case "INSIDE_BORDER", "BORDER_INSIDE" -> RuntimeCondition.INSIDE_BORDER;
      case "LOW_HEALTH_30", "LOW_HP_30" -> RuntimeCondition.LOW_HEALTH_30;
      case "LOW_HEALTH_35", "LOW_HP_35" -> RuntimeCondition.LOW_HEALTH_35;
      case "HAS_POSITIVE_EFFECT", "POSITIVE_EFFECT_ACTIVE", "POSITIVE_BUFF_ACTIVE" -> RuntimeCondition.HAS_POSITIVE_EFFECT;
      case "MAIN_HAND_EMPTY", "EMPTY_HAND", "BARE_HAND" -> RuntimeCondition.MAIN_HAND_EMPTY;
      case "MAIN_HAND_AXE", "HOLDING_AXE" -> RuntimeCondition.MAIN_HAND_AXE;
      case "MAIN_HAND_TRIDENT_LIKE", "HOLDING_TRIDENT", "HOLDING_SPEAR", "TRIDENT_OR_SPEAR" -> RuntimeCondition.MAIN_HAND_TRIDENT_LIKE;
      case "MAIN_HAND_ENDER_PEARL", "HOLDING_ENDER_PEARL" -> RuntimeCondition.MAIN_HAND_ENDER_PEARL;
      case "NEAR_STRONGHOLD_500", "STRONGHOLD_NEAR_500", "NEAR_STRONGHOLD" -> RuntimeCondition.NEAR_STRONGHOLD_500;
      default -> null;
    };
  }

  private String parseMapString(Map<?, ?> raw, String key) {
    if (raw == null || key == null) {
      return "";
    }
    Object value = raw.get(key);
    if (value == null) {
      return "";
    }
    String text = String.valueOf(value).trim();
    return text == null ? "" : text;
  }

  private int parseMapInt(Map<?, ?> raw, String key, int fallback) {
    if (raw == null || key == null) {
      return fallback;
    }
    Object value = raw.get(key);
    if (value instanceof Number number) {
      return number.intValue();
    }
    if (value instanceof String text) {
      Integer parsed = parseInt(text.trim());
      if (parsed != null) {
        return parsed;
      }
    }
    return fallback;
  }

  private double parseMapDouble(Map<?, ?> raw, String key, double fallback) {
    if (raw == null || key == null) {
      return fallback;
    }
    Object value = raw.get(key);
    if (value instanceof Number number) {
      return number.doubleValue();
    }
    if (value instanceof String text) {
      Double parsed = parseDouble(text.trim());
      if (parsed != null) {
        return parsed;
      }
    }
    return fallback;
  }

  private TierScalingMode parseTierScalingMode(String configured) {
    if (configured == null || configured.isBlank()) {
      return TierScalingMode.ARCHETYPE_TOTAL;
    }
    String normalized = configured.trim().toUpperCase(Locale.ROOT);
    if ("CARD".equals(normalized) || "CARD_TIER".equals(normalized)) {
      return TierScalingMode.CARD_TIER;
    }
    return TierScalingMode.ARCHETYPE_TOTAL;
  }

  private RuntimeWorldScope parseRuntimeWorldScope(String configured) {
    if (configured == null || configured.isBlank()) {
      return RuntimeWorldScope.ANY;
    }
    String normalized = configured.trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "OVERWORLD", "NORMAL" -> RuntimeWorldScope.OVERWORLD;
      case "NETHER" -> RuntimeWorldScope.NETHER;
      case "END", "THE_END" -> RuntimeWorldScope.END;
      default -> RuntimeWorldScope.ANY;
    };
  }

  private EffectRuntimeProfile resolveRuntimeProfile(
      EffectDefinition definition,
      EffectKind kind,
      EffectArchetype archetype
  ) {
    if (definition != null && definition.runtimeProfile() != null) {
      return definition.runtimeProfile();
    }
    return defaultRuntimeProfile(kind, archetype);
  }

  private EffectRuntimeProfile defaultRuntimeProfile(EffectKind kind, EffectArchetype archetype) {
    EffectKind resolvedKind = kind == null ? EffectKind.BLESSING : kind;
    EffectArchetype resolvedArchetype = archetype == null ? EffectArchetype.UTILITY : archetype;
    String key = resolvedKind.name() + ":" + resolvedArchetype.name();
    return defaultRuntimeProfiles.computeIfAbsent(key, ignored -> buildDefaultRuntimeProfile(resolvedKind, resolvedArchetype));
  }

  private EffectRuntimeProfile buildDefaultRuntimeProfile(EffectKind kind, EffectArchetype archetype) {
    if (kind == EffectKind.BLESSING) {
      return switch (archetype) {
        case MOBILITY -> new EffectRuntimeProfile(
            List.of(
                modifier(
                    RuntimeModifierType.WALK_SPEED_RATIO,
                    0.020D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "이동 속도"
                ),
                modifier(
                    RuntimeModifierType.FALL_DAMAGE_RATIO,
                    -0.030D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "낙하 피해"
                )
            ),
            null,
            List.of()
        );
        case COMBAT -> new EffectRuntimeProfile(
            List.of(
                modifier(
                    RuntimeModifierType.DAMAGE_DEALT_RATIO,
                    0.030D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "공격 피해"
                )
            ),
            null,
            List.of()
        );
        case DEFENSE -> new EffectRuntimeProfile(
            List.of(
                modifier(
                    RuntimeModifierType.DAMAGE_TAKEN_RATIO,
                    -0.025D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "받는 피해"
                )
            ),
            null,
            List.of()
        );
        case GATHERING -> new EffectRuntimeProfile(
            List.of(
                modifier(
                    RuntimeModifierType.MINING_SCORE_RATIO,
                    0.040D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "채굴 점수"
                ),
                modifier(
                    RuntimeModifierType.ITEM_DURABILITY_LOSS_RATIO,
                    -0.030D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "장비 내구도 소모"
                )
            ),
            null,
            List.of()
        );
        case SCORE -> new EffectRuntimeProfile(List.of(), null, List.of());
        case SURVIVAL -> new EffectRuntimeProfile(
            List.of(
                modifier(
                    RuntimeModifierType.SURVIVAL_SCORE_RATIO,
                    0.050D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "생존 점수"
                ),
                modifier(
                    RuntimeModifierType.RESPAWN_COST_RATIO,
                    -0.030D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "리스폰 비용"
                ),
                modifier(
                    RuntimeModifierType.NATURAL_REGEN_RATIO,
                    0.040D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "자연 회복량"
                ),
                modifier(
                    RuntimeModifierType.HUNGER_DRAIN_RATIO,
                    -0.030D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "포만감 소모"
                )
            ),
            null,
            List.of()
        );
        case UTILITY -> new EffectRuntimeProfile(
            List.of(
                modifier(
                    RuntimeModifierType.PVP_STEAL_GAIN_RATIO,
                    0.015D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "PvP 강탈량"
                ),
                modifier(
                    RuntimeModifierType.PVP_STEAL_TAKEN_RATIO,
                    -0.020D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.ANY,
                    "PvP 강탈 당함"
                )
            ),
            null,
            List.of()
        );
        case ENDGAME -> new EffectRuntimeProfile(
            List.of(
                modifier(
                    RuntimeModifierType.DRAGON_DAMAGE_RATIO,
                    0.040D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.END,
                    "드래곤 기여 피해"
                ),
                modifier(
                    RuntimeModifierType.DRAGON_JACKPOT_RATIO,
                    0.030D,
                    1,
                    TierScalingMode.ARCHETYPE_TOTAL,
                    RuntimeWorldScope.END,
                    "드래곤 보너스 점수"
                )
            ),
            null,
            List.of()
        );
      };
    }

    return switch (archetype) {
      case MOBILITY -> new EffectRuntimeProfile(
          List.of(
              modifier(
                  RuntimeModifierType.WALK_SPEED_RATIO,
                  -0.020D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "이동 속도"
              ),
              modifier(
                  RuntimeModifierType.FALL_DAMAGE_RATIO,
                  0.040D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "낙하 피해"
              )
          ),
          null,
          List.of()
      );
      case COMBAT -> new EffectRuntimeProfile(
          List.of(
              modifier(
                  RuntimeModifierType.DAMAGE_DEALT_RATIO,
                  -0.030D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "공격 피해"
              )
          ),
          null,
          List.of()
      );
      case DEFENSE -> new EffectRuntimeProfile(
          List.of(
              modifier(
                  RuntimeModifierType.DAMAGE_TAKEN_RATIO,
                  0.030D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "받는 피해"
              )
          ),
          null,
          List.of()
      );
      case GATHERING -> new EffectRuntimeProfile(
          List.of(
              modifier(
                  RuntimeModifierType.MINING_SCORE_RATIO,
                  -0.040D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "채굴 점수"
              ),
              modifier(
                  RuntimeModifierType.ITEM_DURABILITY_LOSS_RATIO,
                  0.050D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "장비 내구도 소모"
              )
          ),
          null,
          List.of()
      );
      case SCORE -> new EffectRuntimeProfile(List.of(), null, List.of());
      case SURVIVAL -> new EffectRuntimeProfile(
          List.of(
              modifier(
                  RuntimeModifierType.SURVIVAL_SCORE_RATIO,
                  -0.050D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "생존 점수"
              ),
              modifier(
                  RuntimeModifierType.RESPAWN_COST_RATIO,
                  0.040D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "리스폰 비용"
              ),
              modifier(
                  RuntimeModifierType.NATURAL_REGEN_RATIO,
                  -0.050D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "자연 회복량"
              ),
              modifier(
                  RuntimeModifierType.HUNGER_DRAIN_RATIO,
                  0.050D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "포만감 소모"
              ),
              modifier(
                  RuntimeModifierType.STARVATION_DAMAGE_RATIO,
                  0.080D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "아사 피해"
              )
          ),
          null,
          List.of()
      );
      case UTILITY -> new EffectRuntimeProfile(
          List.of(
              modifier(
                  RuntimeModifierType.PVP_STEAL_GAIN_RATIO,
                  -0.020D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "PvP 강탈량"
              ),
              modifier(
                  RuntimeModifierType.PVP_STEAL_TAKEN_RATIO,
                  0.030D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.ANY,
                  "PvP 강탈 당함"
              )
          ),
          null,
          List.of()
      );
      case ENDGAME -> new EffectRuntimeProfile(
          List.of(
              modifier(
                  RuntimeModifierType.DRAGON_DAMAGE_RATIO,
                  -0.050D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.END,
                  "드래곤 기여 피해"
              ),
              modifier(
                  RuntimeModifierType.DRAGON_JACKPOT_RATIO,
                  -0.050D,
                  1,
                  TierScalingMode.ARCHETYPE_TOTAL,
                  RuntimeWorldScope.END,
                  "드래곤 보너스 점수"
              )
          ),
          null,
          List.of()
      );
    };
  }

  private RuntimeModifierRule modifier(
      RuntimeModifierType type,
      double valuePerTier,
      int minTotalTier,
      TierScalingMode scalingMode,
      RuntimeWorldScope worldScope,
      String labelKo
  ) {
    return new RuntimeModifierRule(
        type,
        valuePerTier,
        Math.max(1, minTotalTier),
        scalingMode == null ? TierScalingMode.ARCHETYPE_TOTAL : scalingMode,
        worldScope == null ? RuntimeWorldScope.ANY : worldScope,
        Set.of(),
        labelKo == null || labelKo.isBlank() ? runtimeModifierLabelKo(type) : labelKo
    );
  }

  private RuntimeModifierType parseRuntimeModifierType(String configured) {
    if (configured == null || configured.isBlank()) {
      return null;
    }
    String normalized = configured.trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "DAMAGE_DEALT", "DAMAGE_DEALT_RATIO", "OUTGOING_DAMAGE" -> RuntimeModifierType.DAMAGE_DEALT_RATIO;
      case "DAMAGE_TAKEN", "DAMAGE_TAKEN_RATIO", "INCOMING_DAMAGE" -> RuntimeModifierType.DAMAGE_TAKEN_RATIO;
      case "FALL_DAMAGE", "FALL_DAMAGE_RATIO" -> RuntimeModifierType.FALL_DAMAGE_RATIO;
      case "WALK_SPEED", "WALK_SPEED_RATIO", "MOVE_SPEED" -> RuntimeModifierType.WALK_SPEED_RATIO;
      case "ATTACK_SPEED", "ATTACK_SPEED_RATIO" -> RuntimeModifierType.ATTACK_SPEED_RATIO;
      case "STEP_HEIGHT", "STEP_HEIGHT_RATIO", "STEP_UP_RATIO" -> RuntimeModifierType.STEP_HEIGHT_RATIO;
      case "SNEAK_SPEED", "SNEAK_SPEED_RATIO", "SNEAKING_SPEED_RATIO" -> RuntimeModifierType.SNEAK_SPEED_RATIO;
      case "BLOCK_BREAK_SPEED", "BLOCK_BREAK_SPEED_RATIO", "MINE_SPEED_RATIO" -> RuntimeModifierType.BLOCK_BREAK_SPEED_RATIO;
      case "WATER_MOVEMENT", "WATER_MOVEMENT_RATIO", "SWIM_SPEED_RATIO" -> RuntimeModifierType.WATER_MOVEMENT_RATIO;
      case "MAX_HEALTH", "MAX_HEALTH_RATIO" -> RuntimeModifierType.MAX_HEALTH_RATIO;
      case "MAX_ABSORPTION", "MAX_ABSORPTION_RATIO", "ABSORPTION_MAX_RATIO" -> RuntimeModifierType.MAX_ABSORPTION_RATIO;
      case "SAFE_FALL_DISTANCE", "SAFE_FALL_DISTANCE_RATIO" -> RuntimeModifierType.SAFE_FALL_DISTANCE_RATIO;
      case "BURNING_TIME", "BURNING_TIME_RATIO", "BURN_DURATION_RATIO" -> RuntimeModifierType.BURNING_TIME_RATIO;
      case "KNOCKBACK_TAKEN", "KNOCKBACK_TAKEN_RATIO" -> RuntimeModifierType.KNOCKBACK_TAKEN_RATIO;
      case "SHIELD_BLOCK", "SHIELD_BLOCK_RATIO", "SHIELD_EFFICIENCY", "SHIELD_EFFICIENCY_RATIO" -> RuntimeModifierType.SHIELD_BLOCK_RATIO;
      case "OXYGEN_DRAIN", "OXYGEN_DRAIN_RATIO", "AIR_DRAIN_RATIO" -> RuntimeModifierType.OXYGEN_DRAIN_RATIO;
      case "AURA_POWER", "AURA_POWER_RATIO", "AURA_MONSTER_POWER", "AURA_MONSTER_POWER_RATIO" -> RuntimeModifierType.AURA_MONSTER_POWER_RATIO;
      case "STALKER_SPAWN_INTERVAL", "STALKER_SPAWN_INTERVAL_RATIO", "STALKER_INTERVAL_RATIO" -> RuntimeModifierType.STALKER_SPAWN_INTERVAL_RATIO;
      case "STALKER_DAMAGE_TAKEN", "STALKER_DAMAGE_TAKEN_RATIO" -> RuntimeModifierType.STALKER_DAMAGE_TAKEN_RATIO;
      case "BORDER_WITHER", "BORDER_WITHER_RATIO", "BORDER_HAZARD_RATIO" -> RuntimeModifierType.BORDER_WITHER_RATIO;
      case "DARKNESS_DURATION", "DARKNESS_DURATION_RATIO" -> RuntimeModifierType.DARKNESS_DURATION_RATIO;
      case "BLINDNESS_DURATION", "BLINDNESS_DURATION_RATIO" -> RuntimeModifierType.BLINDNESS_DURATION_RATIO;
      case "GLOWING_DURATION", "GLOWING_DURATION_RATIO" -> RuntimeModifierType.GLOWING_DURATION_RATIO;
      case "WEAKNESS_DURATION", "WEAKNESS_DURATION_RATIO" -> RuntimeModifierType.WEAKNESS_DURATION_RATIO;
      case "WITHER_DURATION", "WITHER_DURATION_RATIO" -> RuntimeModifierType.WITHER_DURATION_RATIO;
      case "POISON_DURATION", "POISON_DURATION_RATIO" -> RuntimeModifierType.POISON_DURATION_RATIO;
      case "PROJECTILE_DAMAGE_TAKEN", "PROJECTILE_DAMAGE_TAKEN_RATIO" -> RuntimeModifierType.PROJECTILE_DAMAGE_TAKEN_RATIO;
      case "EXPLOSION_DAMAGE_TAKEN", "EXPLOSION_DAMAGE_TAKEN_RATIO" -> RuntimeModifierType.EXPLOSION_DAMAGE_TAKEN_RATIO;
      case "FIRE_DAMAGE_TAKEN", "FIRE_DAMAGE_TAKEN_RATIO" -> RuntimeModifierType.FIRE_DAMAGE_TAKEN_RATIO;
      case "WITHER_DAMAGE_TAKEN", "WITHER_DAMAGE_TAKEN_RATIO" -> RuntimeModifierType.WITHER_DAMAGE_TAKEN_RATIO;
      case "END_DAMAGE_TAKEN", "END_DAMAGE_TAKEN_RATIO" -> RuntimeModifierType.END_DAMAGE_TAKEN_RATIO;
      case "LOW_HEALTH_DAMAGE_DEALT", "LOW_HEALTH_DAMAGE_DEALT_RATIO" -> RuntimeModifierType.LOW_HEALTH_DAMAGE_DEALT_RATIO;
      case "LOW_HEALTH_DAMAGE_TAKEN", "LOW_HEALTH_DAMAGE_TAKEN_RATIO" -> RuntimeModifierType.LOW_HEALTH_DAMAGE_TAKEN_RATIO;
      case "PVP_DAMAGE_DEALT", "PVP_DAMAGE_DEALT_RATIO" -> RuntimeModifierType.PVP_DAMAGE_DEALT_RATIO;
      case "PVP_DAMAGE_TAKEN", "PVP_DAMAGE_TAKEN_RATIO" -> RuntimeModifierType.PVP_DAMAGE_TAKEN_RATIO;
      case "MOB_DAMAGE_DEALT", "MOB_DAMAGE_DEALT_RATIO" -> RuntimeModifierType.MOB_DAMAGE_DEALT_RATIO;
      case "NATURAL_REGEN", "NATURAL_REGEN_RATIO", "HEALTH_REGEN", "HEALTH_REGEN_RATIO" -> RuntimeModifierType.NATURAL_REGEN_RATIO;
      case "HUNGER_DRAIN", "HUNGER_DRAIN_RATIO", "FOOD_LOSS", "FOOD_LOSS_RATIO", "EXHAUSTION_GAIN_RATIO" -> RuntimeModifierType.HUNGER_DRAIN_RATIO;
      case "FOOD_GAIN", "FOOD_GAIN_RATIO", "SATIETY_GAIN", "SATIETY_GAIN_RATIO" -> RuntimeModifierType.FOOD_GAIN_RATIO;
      case "ITEM_DURABILITY_LOSS", "ITEM_DURABILITY_LOSS_RATIO", "DURABILITY_LOSS", "DURABILITY_LOSS_RATIO" -> RuntimeModifierType.ITEM_DURABILITY_LOSS_RATIO;
      case "STARVATION_DAMAGE", "STARVATION_DAMAGE_RATIO" -> RuntimeModifierType.STARVATION_DAMAGE_RATIO;
      case "SCORE_DECAY_PER_MINUTE", "SCORE_DECAY_PER_MINUTE_RATIO", "SCORE_DECAY_RATIO" -> RuntimeModifierType.SCORE_DECAY_PER_MINUTE_RATIO;
      case "MINING_SCORE", "MINING_SCORE_RATIO" -> RuntimeModifierType.MINING_SCORE_RATIO;
      case "MOB_SCORE", "MOB_SCORE_RATIO" -> RuntimeModifierType.MOB_SCORE_RATIO;
      case "SURVIVAL_SCORE", "SURVIVAL_SCORE_RATIO" -> RuntimeModifierType.SURVIVAL_SCORE_RATIO;
      case "PVP_STEAL_GAIN", "PVP_STEAL_GAIN_RATIO" -> RuntimeModifierType.PVP_STEAL_GAIN_RATIO;
      case "PVP_STEAL_TAKEN", "PVP_STEAL_TAKEN_RATIO" -> RuntimeModifierType.PVP_STEAL_TAKEN_RATIO;
      case "RESPAWN_COST", "RESPAWN_COST_RATIO" -> RuntimeModifierType.RESPAWN_COST_RATIO;
      case "DRAGON_DAMAGE", "DRAGON_DAMAGE_RATIO" -> RuntimeModifierType.DRAGON_DAMAGE_RATIO;
      case "DRAGON_JACKPOT", "DRAGON_JACKPOT_RATIO", "DRAGON_SCORE", "DRAGON_SCORE_RATIO" -> RuntimeModifierType.DRAGON_JACKPOT_RATIO;
      default -> null;
    };
  }

  private String runtimeModifierLabelKo(RuntimeModifierType type) {
    if (type == null) {
      return "효과";
    }
    return switch (type) {
      case DAMAGE_DEALT_RATIO -> "공격 피해";
      case DAMAGE_TAKEN_RATIO -> "받는 피해";
      case FALL_DAMAGE_RATIO -> "낙하 피해";
      case WALK_SPEED_RATIO -> "이동 속도";
      case ATTACK_SPEED_RATIO -> "공격 속도";
      case STEP_HEIGHT_RATIO -> "스텝 업 높이";
      case SNEAK_SPEED_RATIO -> "잠행 이동 속도";
      case BLOCK_BREAK_SPEED_RATIO -> "채굴 속도";
      case WATER_MOVEMENT_RATIO -> "수중 이동";
      case MAX_HEALTH_RATIO -> "최대 체력";
      case MAX_ABSORPTION_RATIO -> "최대 흡수 체력";
      case SAFE_FALL_DISTANCE_RATIO -> "안전 낙하 거리";
      case BURNING_TIME_RATIO -> "화상 지속 시간";
      case KNOCKBACK_TAKEN_RATIO -> "받는 넉백";
      case SHIELD_BLOCK_RATIO -> "방패 방어 효율";
      case OXYGEN_DRAIN_RATIO -> "산소 소모";
      case AURA_MONSTER_POWER_RATIO -> "엔더 오라 몹 강화도";
      case STALKER_SPAWN_INTERVAL_RATIO -> "스토커 스폰 간격";
      case STALKER_DAMAGE_TAKEN_RATIO -> "스토커 받는 피해";
      case BORDER_WITHER_RATIO -> "보더 위더 압박";
      case DARKNESS_DURATION_RATIO -> "다크니스 지속";
      case BLINDNESS_DURATION_RATIO -> "실명 지속";
      case GLOWING_DURATION_RATIO -> "발광 지속";
      case WEAKNESS_DURATION_RATIO -> "약화 지속";
      case WITHER_DURATION_RATIO -> "위더 지속";
      case POISON_DURATION_RATIO -> "독 지속";
      case PROJECTILE_DAMAGE_TAKEN_RATIO -> "투사체 피해";
      case EXPLOSION_DAMAGE_TAKEN_RATIO -> "폭발 피해";
      case FIRE_DAMAGE_TAKEN_RATIO -> "화염 피해";
      case WITHER_DAMAGE_TAKEN_RATIO -> "위더 피해";
      case END_DAMAGE_TAKEN_RATIO -> "엔드 받는 피해";
      case LOW_HEALTH_DAMAGE_DEALT_RATIO -> "저체력 공격 피해";
      case LOW_HEALTH_DAMAGE_TAKEN_RATIO -> "저체력 받는 피해";
      case PVP_DAMAGE_DEALT_RATIO -> "PvP 공격 피해";
      case PVP_DAMAGE_TAKEN_RATIO -> "PvP 받는 피해";
      case MOB_DAMAGE_DEALT_RATIO -> "몹 대상 피해";
      case NATURAL_REGEN_RATIO -> "자연 회복량";
      case HUNGER_DRAIN_RATIO -> "포만감 소모";
      case FOOD_GAIN_RATIO -> "포만감 회복";
      case ITEM_DURABILITY_LOSS_RATIO -> "장비 내구도 소모";
      case STARVATION_DAMAGE_RATIO -> "아사 피해";
      case SCORE_DECAY_PER_MINUTE_RATIO -> "분당 점수 감쇠";
      case MINING_SCORE_RATIO -> "채굴 점수";
      case MOB_SCORE_RATIO -> "몹 처치 점수";
      case SURVIVAL_SCORE_RATIO -> "생존 점수";
      case PVP_STEAL_GAIN_RATIO -> "PvP 강탈량";
      case PVP_STEAL_TAKEN_RATIO -> "PvP 강탈 당함";
      case RESPAWN_COST_RATIO -> "리스폰 비용";
      case DRAGON_DAMAGE_RATIO -> "드래곤 기여 피해";
      case DRAGON_JACKPOT_RATIO -> "드래곤 보너스 점수";
    };
  }

  private EffectArchetype inferArchetype(String effectId) {
    int number = parseEffectNumericId(effectId);
    if (number <= 0) {
      return EffectArchetype.UTILITY;
    }
    if (number <= 15) {
      return EffectArchetype.MOBILITY;
    }
    if (number <= 30) {
      return EffectArchetype.COMBAT;
    }
    if (number <= 45) {
      return EffectArchetype.DEFENSE;
    }
    if (number <= 60) {
      return EffectArchetype.GATHERING;
    }
    if (number <= 75) {
      return EffectArchetype.SCORE;
    }
    if (number <= 90) {
      return EffectArchetype.SURVIVAL;
    }
    if (number <= 105) {
      return EffectArchetype.UTILITY;
    }
    return EffectArchetype.ENDGAME;
  }

  private int parseEffectNumericId(String effectId) {
    if (effectId == null || effectId.length() < 3) {
      return -1;
    }
    int dash = effectId.indexOf('-');
    if (dash < 0 || dash + 1 >= effectId.length()) {
      return -1;
    }
    try {
      return Integer.parseInt(effectId.substring(dash + 1));
    } catch (NumberFormatException ignored) {
      return -1;
    }
  }

  private String normalizeEffectId(String id) {
    if (id == null || id.isBlank()) {
      return "";
    }
    return id.trim().toUpperCase(Locale.ROOT);
  }

  private void buildEffectGimmicksIndex() {
    effectGimmicksById.clear();
    if (!cardsRuntimeEffectsEnabled()) {
      return;
    }
    for (EffectDefinition definition : blessingEffectsCatalog) {
      EffectGimmickProfile profile = buildEffectGimmickProfile(definition);
      if (profile != null) {
        effectGimmicksById.put(normalizeEffectId(definition.id()), profile);
      }
    }
    for (EffectDefinition definition : curseEffectsCatalog) {
      EffectGimmickProfile profile = buildEffectGimmickProfile(definition);
      if (profile != null) {
        effectGimmicksById.put(normalizeEffectId(definition.id()), profile);
      }
    }
  }

  private EffectGimmickProfile buildEffectGimmickProfile(EffectDefinition definition) {
    if (definition == null || definition.id() == null || definition.id().isBlank()) {
      return null;
    }
    String normalizedId = normalizeEffectId(definition.id());
    Effect80Id effect80Id = parseEffect80Id(normalizedId);
    if (effect80Id == null) {
      return null;
    }

    StringBuilder sourceBuilder = new StringBuilder();
    sourceBuilder.append(normalizedId).append(' ');
    if (definition.displayName() != null) {
      sourceBuilder.append(definition.displayName()).append(' ');
    }
    EffectRuntimeProfile runtimeProfile = definition.runtimeProfile();
    if (runtimeProfile != null && runtimeProfile.detailKo() != null) {
      for (String line : runtimeProfile.detailKo()) {
        if (line == null || line.isBlank()) {
          continue;
        }
        sourceBuilder.append(line).append(' ');
      }
    }
    String sourceText = sourceBuilder.toString();
    EnumSet<GimmickTag> tags = EnumSet.noneOf(GimmickTag.class);

    if (containsTextTag(sourceText, "보호막", "흡수", "면역막")) {
      tags.add(GimmickTag.SHIELD_CORE);
    }
    if (containsTextTag(sourceText, "역행", "롤백", "시간")) {
      tags.add(GimmickTag.REWIND);
    }
    if (containsTextTag(sourceText, "점멸", "텔포", "전이", "귀환", "교환")) {
      tags.add(GimmickTag.TELEPORT);
    }
    if (containsTextTag(sourceText, "분신", "잔상")) {
      tags.add(GimmickTag.DECOY);
    }
    if (containsTextTag(sourceText, "벌목", "트리", "통나무")) {
      tags.add(GimmickTag.TREECAP);
    }
    if (containsTextTag(sourceText, "돌→광물", "광물→돌", "연금술", "광맥 붕괴", "쌍방향")) {
      tags.add(GimmickTag.BLOCK_TRANSMUTE);
    }
    if (containsTextTag(sourceText, "투사체", "탄도", "반사", "흡수막")) {
      tags.add(GimmickTag.PROJECTILE_CONTROL);
    }
    if (containsTextTag(sourceText, "핫바", "스왑", "윤회")) {
      tags.add(GimmickTag.HOTBAR_MANIPULATION);
    }
    if (containsTextTag(sourceText, "잠금", "손아귀")) {
      tags.add(GimmickTag.SLOT_LOCK);
    }
    if (containsTextTag(sourceText, "도구", "금단", "봉인")) {
      tags.add(GimmickTag.TOOL_BAN);
    }
    if (containsTextTag(sourceText, "건축 붕괴", "사라짐", "붕괴")) {
      tags.add(GimmickTag.BUILD_DECAY);
    }
    if (containsTextTag(sourceText, "포션", "물약", "오염", "양날")) {
      tags.add(GimmickTag.POTION_MUTATION);
    }
    if (containsTextTag(sourceText, "다크니스", "실명", "악몽", "밀실")) {
      tags.add(GimmickTag.NIGHTMARE);
    }
    if (containsTextTag(sourceText, "추격자", "스토커")) {
      tags.add(GimmickTag.STALKER_PRESSURE);
    }
    if (containsTextTag(sourceText, "오라")) {
      tags.add(GimmickTag.AURA_SHIFT);
    }
    if (containsTextTag(sourceText, "보더", "자기장")) {
      tags.add(GimmickTag.BORDER_PRESSURE);
    }
    if (containsTextTag(sourceText, "전리품", "드랍", "룰렛", "약탈")) {
      tags.add(GimmickTag.LOOT_CONTROL);
    }
    if (containsTextTag(sourceText, "반발", "충격", "검기", "연쇄", "폭발")) {
      tags.add(GimmickTag.AOE_COMBAT);
    }
    if (containsTextTag(sourceText, "점수", "세금", "투자", "방패")) {
      tags.add(GimmickTag.SCORE_TRADE);
    }
    if (containsTextTag(sourceText, "불꽃 치유", "물 취약", "물 치유", "불 취약")) {
      tags.add(GimmickTag.ELEMENT_SHIFT);
    }
    if (containsTextTag(sourceText, "허기", "굶주림", "포만감", "생명-허기")) {
      tags.add(GimmickTag.HUNGER_EXCHANGE);
    }
    if (containsTextTag(sourceText, "스캐너", "힌트", "나침반", "탐지")) {
      tags.add(GimmickTag.SCANNER);
    }
    if (containsTextTag(sourceText, "요새", "방어구 희생", "유리 대포", "반사 피부")) {
      tags.add(GimmickTag.TRADEOFF_DEFENSE);
    }
    if (containsTextTag(sourceText, "도박 제작", "제작")) {
      tags.add(GimmickTag.CRAFT_GAMBLE);
    }

    if (tags.isEmpty()) {
      tags.add(GimmickTag.GENERIC_RUNTIME);
    }

    AbilityToken token = inferAbilityToken(sourceText);
    AbilityMode mode = inferAbilityMode(sourceText);
    boolean hasActiveTrigger = containsTextTag(sourceText, "ACTIVE(", "TOKEN_");
    int baseCooldownSeconds = inferCooldownSecondsFromText(sourceText, 60);
    if (effect80Id.group() == 'X') {
      if (effect80Id.index() == 8) {
        // Tier 4 active cast is defined in runtime behavior.
        hasActiveTrigger = true;
        if (token == AbilityToken.NONE) {
          token = AbilityToken.OFFENSE;
        }
        mode = AbilityMode.NORMAL;
        baseCooldownSeconds = 60;
      } else if (effect80Id.index() == 11) {
        // Tier 4 active taunt.
        hasActiveTrigger = true;
        if (token == AbilityToken.NONE) {
          token = AbilityToken.UTILITY;
        }
        mode = AbilityMode.NORMAL;
        baseCooldownSeconds = 300;
      } else if (effect80Id.index() == 15) {
        // Optional active fortress mode.
        hasActiveTrigger = true;
        if (token == AbilityToken.NONE) {
          token = AbilityToken.BUILD;
        }
        mode = AbilityMode.NORMAL;
        baseCooldownSeconds = 120;
      } else if (effect80Id.index() == 20) {
        // Tier 4 active sacrifice cleanse.
        hasActiveTrigger = true;
        if (token == AbilityToken.NONE) {
          token = AbilityToken.UTILITY;
        }
        mode = AbilityMode.NORMAL;
        baseCooldownSeconds = 180;
      }
    } else if (effect80Id.group() == 'B') {
      switch (effect80Id.index()) {
        case 2 -> {
          hasActiveTrigger = true;
          token = AbilityToken.DEFENSE;
          mode = AbilityMode.SNEAK;
          baseCooldownSeconds = 120;
        }
        case 6 -> {
          // Passive shield-block logic.
          hasActiveTrigger = false;
          mode = null;
        }
        case 7 -> {
          hasActiveTrigger = true;
          token = AbilityToken.OFFENSE;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 20;
        }
        case 8 -> {
          hasActiveTrigger = true;
          token = AbilityToken.OFFENSE;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 60;
        }
        case 9 -> {
          hasActiveTrigger = true;
          token = AbilityToken.OFFENSE;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 20;
        }
        case 10 -> {
          hasActiveTrigger = true;
          token = AbilityToken.OFFENSE;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 60;
        }
        case 11 -> {
          // Passive-only: wooden pickaxe mining trigger.
          hasActiveTrigger = false;
          mode = null;
        }
        case 12 -> {
          hasActiveTrigger = true;
          token = AbilityToken.NONE;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 1;
        }
        case 13 -> {
          hasActiveTrigger = true;
          token = AbilityToken.OFFENSE;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 120;
        }
        case 15 -> {
          hasActiveTrigger = true;
          token = AbilityToken.NONE;
          mode = null;
          baseCooldownSeconds = 240;
        }
        case 16 -> {
          // Periodic-only behavior.
          hasActiveTrigger = false;
          mode = null;
        }
        case 17 -> {
          hasActiveTrigger = true;
          token = AbilityToken.DEFENSE;
          mode = AbilityMode.SPRINT;
          baseCooldownSeconds = 120;
        }
        case 35 -> {
          hasActiveTrigger = true;
          token = AbilityToken.NONE;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 1;
        }
        case 36 -> {
          hasActiveTrigger = true;
          token = AbilityToken.UTILITY;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 10;
        }
        case 39 -> {
          hasActiveTrigger = true;
          token = AbilityToken.UTILITY;
          mode = AbilityMode.NORMAL;
          baseCooldownSeconds = 120;
        }
        case 20 -> {
          hasActiveTrigger = true;
          token = AbilityToken.NONE;
          mode = null;
          baseCooldownSeconds = 10;
        }
        default -> {
        }
      }
    }

    return new EffectGimmickProfile(
        effect80Id,
        definition.kind(),
        tags,
        hasActiveTrigger,
        token,
        mode,
        baseCooldownSeconds
    );
  }

  private AbilityToken inferAbilityToken(String sourceText) {
    if (containsTextTag(sourceText, "TOKEN_MOBILITY")) {
      return AbilityToken.MOBILITY;
    }
    if (containsTextTag(sourceText, "TOKEN_DEFENSE")) {
      return AbilityToken.DEFENSE;
    }
    if (containsTextTag(sourceText, "TOKEN_OFFENSE")) {
      return AbilityToken.OFFENSE;
    }
    if (containsTextTag(sourceText, "TOKEN_UTILITY")) {
      return AbilityToken.UTILITY;
    }
    if (containsTextTag(sourceText, "TOKEN_BUILD")) {
      return AbilityToken.BUILD;
    }
    return AbilityToken.NONE;
  }

  private AbilityMode inferAbilityMode(String sourceText) {
    boolean hasNormal = containsTextTag(sourceText, "NORMAL");
    boolean hasSneak = containsTextTag(sourceText, "SNEAK");
    boolean hasSprint = containsTextTag(sourceText, "SPRINT");
    int modeMatches = (hasNormal ? 1 : 0) + (hasSneak ? 1 : 0) + (hasSprint ? 1 : 0);
    if (modeMatches != 1) {
      return null;
    }
    if (hasSneak) {
      return AbilityMode.SNEAK;
    }
    if (hasSprint) {
      return AbilityMode.SPRINT;
    }
    return AbilityMode.NORMAL;
  }

  private int inferCooldownSecondsFromText(String sourceText, int fallbackSeconds) {
    if (sourceText == null || sourceText.isBlank()) {
      return Math.max(1, fallbackSeconds);
    }
    Matcher matcher = EFFECT80_COOLDOWN_PATTERN.matcher(sourceText);
    int minPositive = Integer.MAX_VALUE;
    while (matcher.find()) {
      Integer parsed = parseInt(matcher.group(1));
      if (parsed == null || parsed <= 0) {
        continue;
      }
      minPositive = Math.min(minPositive, parsed);
    }
    if (minPositive == Integer.MAX_VALUE) {
      return Math.max(1, fallbackSeconds);
    }
    return Math.max(1, minPositive);
  }

  private boolean containsTextTag(String sourceText, String... tokens) {
    if (sourceText == null || sourceText.isBlank() || tokens == null || tokens.length == 0) {
      return false;
    }
    String normalized = sourceText.toLowerCase(Locale.ROOT);
    for (String token : tokens) {
      if (token == null || token.isBlank()) {
        continue;
      }
      if (normalized.contains(token.toLowerCase(Locale.ROOT))) {
        return true;
      }
    }
    return false;
  }

  private Effect80Id parseEffect80Id(String effectId) {
    if (effectId == null || effectId.isBlank()) {
      return null;
    }
    String normalized = effectId.trim().toUpperCase(Locale.ROOT);
    Matcher matcher = EFFECT80_ID_PATTERN.matcher(normalized);
    if (!matcher.matches()) {
      return null;
    }
    String groupRaw = matcher.group("group");
    String indexRaw = matcher.group("index");
    if (groupRaw == null || groupRaw.isBlank() || indexRaw == null || indexRaw.isBlank()) {
      return null;
    }
    Integer index = parseInt(indexRaw);
    if (index == null || index < 1 || index > 999) {
      return null;
    }
    return new Effect80Id(groupRaw.charAt(0), index);
  }

  private boolean isBModEffectId(String effectId) {
    return false;
  }

  private boolean isCModEffectId(String effectId) {
    return false;
  }

  private char xModVariantSuffix(String effectId) {
    return '\0';
  }

  private boolean isXModCurseVariant(String effectId) {
    return xModVariantSuffix(effectId) == 'C';
  }

  private int boostedBModTier(String effectId, int rawTier) {
    int clamped = Math.max(1, Math.min(4, rawTier));
    if (!isBModEffectId(effectId)) {
      return clamped;
    }
    if (clamped >= 4) {
      return 4;
    }
    return Math.min(4, clamped + B_MOD_TIER_BONUS);
  }

  private long scaledBModCooldownSeconds(String effectId, long cooldownSeconds) {
    long base = Math.max(1L, cooldownSeconds);
    if (!isBModEffectId(effectId)) {
      return base;
    }
    return Math.max(1L, Math.round(base * B_MOD_COOLDOWN_SCALE));
  }

  private long scaledBModCooldownMillis(String effectId, long cooldownMillis) {
    long base = Math.max(1L, cooldownMillis);
    if (!isBModEffectId(effectId)) {
      return base;
    }
    return Math.max(120L, Math.round(base * B_MOD_COOLDOWN_SCALE));
  }

  private double boostedBModChance(String effectId, double chance) {
    double clamped = Math.max(0.0D, Math.min(1.0D, chance));
    if (!isBModEffectId(effectId)) {
      return clamped;
    }
    double boosted = (clamped * B_MOD_CHANCE_SCALE) + B_MOD_CHANCE_FLAT_BONUS;
    return Math.max(0.0D, Math.min(0.98D, boosted));
  }

  private int scaledBModDurationTicks(String effectId, int ticks) {
    int base = Math.max(1, ticks);
    if (!isBModEffectId(effectId)) {
      return base;
    }
    return Math.max(1, (int) Math.round(base * B_MOD_DURATION_SCALE));
  }

  private long scaledBModDurationSeconds(String effectId, long seconds) {
    long base = Math.max(1L, seconds);
    if (!isBModEffectId(effectId)) {
      return base;
    }
    return Math.max(1L, Math.round(base * B_MOD_DURATION_SCALE));
  }

  private int scaledBModRangeBlocks(String effectId, int blocks) {
    int base = Math.max(1, blocks);
    if (!isBModEffectId(effectId)) {
      return base;
    }
    return Math.max(1, (int) Math.round(base * B_MOD_RANGE_SCALE));
  }

  private double scaledBModRangeBlocks(String effectId, double blocks) {
    double base = Math.max(0.1D, blocks);
    if (!isBModEffectId(effectId)) {
      return base;
    }
    return Math.max(0.1D, base * B_MOD_RANGE_SCALE);
  }

  private void emitBModProcFeedback(Player player, int tier, boolean burst) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    Location center = player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
    int rodCount = (burst ? 20 : 10) + (clampedTier * 5);
    int critCount = (burst ? 12 : 6) + (clampedTier * 3);
    player.getWorld().spawnParticle(
        Particle.END_ROD,
        center,
        rodCount,
        burst ? 0.55D : 0.40D,
        burst ? 0.85D : 0.55D,
        burst ? 0.55D : 0.40D,
        0.02D
    );
    player.getWorld().spawnParticle(
        Particle.CRIT,
        center,
        critCount,
        burst ? 0.42D : 0.30D,
        burst ? 0.55D : 0.35D,
        burst ? 0.42D : 0.30D,
        0.02D
    );
    Sound sound = burst ? Sound.BLOCK_BEACON_ACTIVATE : Sound.BLOCK_RESPAWN_ANCHOR_CHARGE;
    float volume = burst ? 0.95F : 0.60F;
    float pitch = burst ? 0.95F : 1.15F;
    player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
  }

  private NumericPoolEffectId parseNumericPoolEffectId(String effectId) {
    if (effectId == null || effectId.isBlank()) {
      return null;
    }
    String normalizedId = effectId.trim().toUpperCase(Locale.ROOT);
    Matcher matcher = NUMERIC_POOL_ID_PATTERN.matcher(normalizedId);
    if (!matcher.matches()) {
      return null;
    }
    String groupRaw = matcher.group("group");
    String indexRaw = matcher.group("index");
    if (groupRaw == null || groupRaw.isBlank() || indexRaw == null || indexRaw.isBlank()) {
      return null;
    }
    Integer index = parseInt(indexRaw);
    if (index == null || index < 1) {
      return null;
    }
    return new NumericPoolEffectId(groupRaw.charAt(0), index);
  }

  private int clampTier(int tier) {
    return Math.max(1, Math.min(4, tier));
  }

  private List<ActiveSeasonEffect> collectAllActiveEffects(PlayerRoundData data) {
    if (data == null || !cardsEffectLogicEnabled()) {
      return List.of();
    }
    List<ActiveSeasonEffect> all = new ArrayList<>();
    for (ActiveSeasonEffect effect : data.getBlessingEffects().values()) {
      if (effect != null && !effect.getId().isBlank()) {
        all.add(effect);
      }
    }
    for (ActiveSeasonEffect effect : data.getCurseEffects().values()) {
      if (effect != null && !effect.getId().isBlank()) {
        all.add(effect);
      }
    }
    if (all.size() <= 1) {
      return all;
    }
    all.sort((left, right) -> {
      int tierCmp = Integer.compare(right.getTier(), left.getTier());
      if (tierCmp != 0) {
        return tierCmp;
      }
      return normalizeEffectId(left.getId()).compareTo(normalizeEffectId(right.getId()));
    });
    return all;
  }

  private long effectCooldownRemaining(UUID playerId, String effectId, String actionKey) {
    if (playerId == null || effectId == null || effectId.isBlank() || actionKey == null || actionKey.isBlank()) {
      return 0L;
    }
    long until = effectCooldownUntilEpochSecond.getOrDefault(effectCooldownKey(playerId, effectId, actionKey), 0L);
    return Math.max(0L, until - nowEpochSecond());
  }

  private boolean useEffectCooldown(UUID playerId, String effectId, String actionKey, long cooldownSeconds) {
    if (playerId == null || effectId == null || effectId.isBlank() || actionKey == null || actionKey.isBlank()) {
      return false;
    }
    String key = effectCooldownKey(playerId, effectId, actionKey);
    long now = nowEpochSecond();
    long until = effectCooldownUntilEpochSecond.getOrDefault(key, 0L);
    if (until > now) {
      return false;
    }
    long applied = scaledBModCooldownSeconds(effectId, cooldownSeconds);
    effectCooldownUntilEpochSecond.put(key, now + applied);
    return true;
  }

  private boolean useEffectCooldownMillis(UUID playerId, String effectId, String actionKey, long cooldownMillis) {
    if (playerId == null || effectId == null || effectId.isBlank() || actionKey == null || actionKey.isBlank()) {
      return false;
    }
    String key = effectCooldownKey(playerId, effectId, actionKey);
    long now = System.currentTimeMillis();
    long until = effectCooldownUntilEpochMilli.getOrDefault(key, 0L);
    if (until > now) {
      return false;
    }
    long applied = scaledBModCooldownMillis(effectId, cooldownMillis);
    effectCooldownUntilEpochMilli.put(key, now + applied);
    return true;
  }

  private String effectCooldownKey(UUID playerId, String effectId, String actionKey) {
    return playerId + "|" + normalizeEffectId(effectId) + "|" + actionKey;
  }

  private boolean isActionBlockedByTemporaryGimmick(Player player) {
    if (player == null) {
      return false;
    }
    long now = nowEpochSecond();
    long noAttackUntil = noAttackUntilEpochSecond.getOrDefault(player.getUniqueId(), 0L);
    return noAttackUntil > now;
  }

  private boolean isBuildBlockedByTemporaryGimmick(Player player) {
    if (player == null) {
      return false;
    }
    long now = nowEpochSecond();
    long noBuildUntil = noBuildUntilEpochSecond.getOrDefault(player.getUniqueId(), 0L);
    return noBuildUntil > now;
  }

  private boolean isHotbarSlotLocked(UUID playerId, int slot) {
    if (playerId == null || slot < 0) {
      return false;
    }
    Map<Integer, Long> locked = lockedHotbarSlotUntilEpochSecond.get(playerId);
    if (locked == null || locked.isEmpty()) {
      return false;
    }
    long now = nowEpochSecond();
    locked.entrySet().removeIf(entry -> entry == null || entry.getValue() == null || entry.getValue() <= now);
    if (locked.isEmpty()) {
      lockedHotbarSlotUntilEpochSecond.remove(playerId);
      return false;
    }
    return locked.getOrDefault(slot, 0L) > now;
  }


  private void tickEffect80PerPlayer(Player player, PlayerRoundData data, long nowEpochSecond) {
    if (player == null || data == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    cleanupTransientGimmickState(playerId, nowEpochSecond);
    captureAbsorbableProjectiles(player, nowEpochSecond);
    settleScoreInvestmentLedger(player, data, nowEpochSecond);

    List<ActiveSeasonEffect> activeEffects = collectAllActiveEffects(data);
    if (activeEffects.isEmpty()) {
      restoreShieldCoreMaxHealth(player);
      shieldCoreLastAbsorptionByPlayer.remove(playerId);
      enclosedSinceEpochSecondByPlayer.remove(playerId);
      nightmareLastCombatSeenEpochSecondByPlayer.remove(playerId);
      soundExposureUntilEpochSecondByPlayer.remove(playerId);
      soundExposureTierByPlayer.remove(playerId);
      weaponRebellionHitCounterByPlayer.remove(playerId);
      doomBreathHealLockUntilEpochSecondByPlayer.remove(playerId);
      frenzyCombatActiveByPlayer.remove(playerId);
      frenzyFatigueStacksByPlayer.remove(playerId);
      frenzyHealLockUntilEpochSecondByPlayer.remove(playerId);
      bloodContractLastKillEpochSecondByPlayer.remove(playerId);
      bloodContractKillStreakByPlayer.remove(playerId);
      ghostUntilEpochSecond.remove(playerId);
      ghostTierByPlayer.remove(playerId);
      ghostChargesByPlayer.remove(playerId);
      ghostNextRechargeEpochSecondByPlayer.remove(playerId);
      weaponMonopolyGroupByPlayer.remove(playerId);
      enderOrbitAfterimageUntilEpochSecondByPlayer.remove(playerId);
      enderOrbitPearlGraceUntilEpochSecondByPlayer.remove(playerId);
      enderOrbitRapidFollowupUntilEpochSecondByPlayer.remove(playerId);
      enderOrbitRapidFollowupCooldownUntilEpochSecondByPlayer.remove(playerId);
      clonePactSharedDamageGuardUntilEpochMilliByPlayer.remove(playerId);
      reflectSkinNoReentryUntilEpochMilliByPlayer.remove(playerId);
      reflectSkinVulnerableUntilEpochSecondByPlayer.remove(playerId);
      timeTaxFieldUntilEpochSecondByPlayer.remove(playerId);
      timeTaxFieldTierByPlayer.remove(playerId);
      spiritExchangeStateByPlayer.remove(playerId);
      spiritExchangeScoreMultiplierByPlayer.remove(playerId);
      clearClonePactAuxDecoys(playerId);
      if (silenceVowSilentPlayers.remove(playerId)) {
        player.setSilent(false);
      }
      borderGamblerNextScoreEpochSecondByPlayer.remove(playerId);
      fortressModeUntilEpochSecondByPlayer.remove(playerId);
      fortressOverclockUntilEpochSecondByPlayer.remove(playerId);
      antiGravityDashChargesByPlayer.remove(playerId);
      antiGravityDashNextRechargeEpochSecondByPlayer.remove(playerId);
      antiGravityAirborneStateByPlayer.remove(playerId);
      blackSanctuaryCalmUntilEpochSecondByPlayer.remove(playerId);
      return;
    }

    if (highestEffect80Tier(data, 'C', 11) <= 0) {
      enclosedSinceEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'C', 12) <= 0) {
      nightmareLastCombatSeenEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'C', 16) <= 0) {
      soundExposureUntilEpochSecondByPlayer.remove(playerId);
      soundExposureTierByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'C', 18) <= 0) {
      weaponRebellionHitCounterByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'C', 19) <= 0) {
      doomBreathHealLockUntilEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 2) <= 0) {
      bloodContractLastKillEpochSecondByPlayer.remove(playerId);
      bloodContractKillStreakByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 9) <= 0) {
      ghostUntilEpochSecond.remove(playerId);
      ghostTierByPlayer.remove(playerId);
      ghostChargesByPlayer.remove(playerId);
      ghostNextRechargeEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 10) <= 0) {
      frenzyCombatActiveByPlayer.remove(playerId);
      frenzyFatigueStacksByPlayer.remove(playerId);
      frenzyHealLockUntilEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 13) <= 0) {
      borderGamblerNextScoreEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 15) <= 0) {
      fortressModeUntilEpochSecondByPlayer.remove(playerId);
      fortressOverclockUntilEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 17) <= 0) {
      antiGravityDashChargesByPlayer.remove(playerId);
      antiGravityDashNextRechargeEpochSecondByPlayer.remove(playerId);
      antiGravityAirborneStateByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 20) <= 0) {
      blackSanctuaryCalmUntilEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 31) <= 0) {
      weaponMonopolyGroupByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 33) <= 0) {
      enderOrbitAfterimageUntilEpochSecondByPlayer.remove(playerId);
      enderOrbitPearlGraceUntilEpochSecondByPlayer.remove(playerId);
      enderOrbitRapidFollowupUntilEpochSecondByPlayer.remove(playerId);
      enderOrbitRapidFollowupCooldownUntilEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 34) <= 0) {
      clonePactSharedDamageGuardUntilEpochMilliByPlayer.remove(playerId);
      clearClonePactAuxDecoys(playerId);
    }
    if (highestEffect80Tier(data, 'X', 35) <= 0) {
      reflectSkinNoReentryUntilEpochMilliByPlayer.remove(playerId);
      reflectSkinVulnerableUntilEpochSecondByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 37) <= 0 && silenceVowSilentPlayers.remove(playerId)) {
      player.setSilent(false);
    }
    if (highestEffect80Tier(data, 'X', 39) <= 0) {
      timeTaxFieldUntilEpochSecondByPlayer.remove(playerId);
      timeTaxFieldTierByPlayer.remove(playerId);
    }
    if (highestEffect80Tier(data, 'X', 40) <= 0) {
      spiritExchangeStateByPlayer.remove(playerId);
      spiritExchangeScoreMultiplierByPlayer.remove(playerId);
    }

    int shieldCoreTier = 0;
    for (ActiveSeasonEffect effect : activeEffects) {
      if (effect == null || effect.getId() == null || effect.getId().isBlank()) {
        continue;
      }
      String normalizedId = normalizeEffectId(effect.getId());
      if ("B-023".equals(normalizedId)) {
        shieldCoreTier = Math.max(shieldCoreTier, Math.max(1, Math.min(4, effect.getTier())));
      }
      EffectGimmickProfile profile = effectGimmicksById.get(normalizedId);
      if (profile == null) {
        continue;
      }

      if (profile.tags().contains(GimmickTag.REWIND)) {
        updateRewindSnapshot(player, effect.getTier());
      }
    }

    if (shieldCoreTier > 0) {
      maintainShieldCoreState(player, shieldCoreTier, nowEpochSecond);
    } else {
      restoreShieldCoreMaxHealth(player);
      recoverShieldCoreStateIfStuck(player, data);
      shieldCoreLastAbsorptionByPlayer.remove(playerId);
    }
  }

  private void cleanupTransientGimmickState(UUID playerId, long nowEpochSecond) {
    if (playerId == null) {
      return;
    }
    if (decoyExpireEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      int decoyTier = Math.max(0, decoyTierByPlayer.getOrDefault(playerId, 0));
      UUID entityId = decoyEntityByPlayer.remove(playerId);
      decoyExpireEpochSecondByPlayer.remove(playerId);
      decoyTierByPlayer.remove(playerId);
      decoySwapUsedByPlayer.remove(playerId);
      clearClonePactAuxDecoys(playerId);
      if (entityId != null) {
        Entity entity = Bukkit.getEntity(entityId);
        if (entity != null && entity.isValid()) {
          if (decoyTier >= 3) {
            emitDecoyCollapsePulse(entity.getLocation(), decoyTier);
          }
          entity.remove();
        }
      }
    }
    if (lootPortalExpireEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      lootPortalExpireEpochSecondByPlayer.remove(playerId);
      lootPortalItemsByPlayer.remove(playerId);
      lootPortalTierByPlayer.remove(playerId);
      lootPortalStoredXpByPlayer.remove(playerId);
    }
    if (bannedToolUntilEpochSecond.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      bannedToolUntilEpochSecond.remove(playerId);
      bannedToolGroupByPlayer.remove(playerId);
    }
    long toolBanPendingAt = toolBanPendingEpochSecondByPlayer.getOrDefault(playerId, 0L);
    if (toolBanPendingAt > 0L && toolBanPendingAt + 5L <= nowEpochSecond) {
      toolBanPendingEpochSecondByPlayer.remove(playerId);
      toolBanPendingTierByPlayer.remove(playerId);
    }
    if (instantCraftBonusUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      instantCraftBonusUntilEpochSecondByPlayer.remove(playerId);
    }
    if (controlDistortionUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      controlDistortionUntilEpochSecondByPlayer.remove(playerId);
      controlDistortionTierByPlayer.remove(playerId);
    }
    if (cursedSprintLockUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      cursedSprintLockUntilEpochSecondByPlayer.remove(playerId);
    }
    if (enderOrbitAfterimageUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      enderOrbitAfterimageUntilEpochSecondByPlayer.remove(playerId);
    }
    if (enderOrbitPearlGraceUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      enderOrbitPearlGraceUntilEpochSecondByPlayer.remove(playerId);
    }
    if (enderOrbitRapidFollowupUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      enderOrbitRapidFollowupUntilEpochSecondByPlayer.remove(playerId);
    }
    if (enderOrbitRapidFollowupCooldownUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      enderOrbitRapidFollowupCooldownUntilEpochSecondByPlayer.remove(playerId);
    }
    if (doomBreathHealLockUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      doomBreathHealLockUntilEpochSecondByPlayer.remove(playerId);
    }
    if (frenzyHealLockUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      frenzyHealLockUntilEpochSecondByPlayer.remove(playerId);
    }
    long ghostUntil = ghostUntilEpochSecond.getOrDefault(playerId, 0L);
    if (ghostUntil > 0L && ghostUntil <= nowEpochSecond) {
      ghostUntilEpochSecond.remove(playerId);
      int ghostTier = Math.max(0, ghostTierByPlayer.getOrDefault(playerId, 0));
      ghostTierByPlayer.remove(playerId);
      Player player = Bukkit.getPlayer(playerId);
      if (player != null && player.isOnline()) {
        completeGhostFormPenalty(player, ghostTier);
      }
    }
    if (temporaryInvulnerableUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      temporaryInvulnerableUntilEpochSecondByPlayer.remove(playerId);
    }
    if (blinkFallImmunityUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      blinkFallImmunityUntilEpochSecondByPlayer.remove(playerId);
    }
    if (fortressModeUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      fortressModeUntilEpochSecondByPlayer.remove(playerId);
      fortressOverclockUntilEpochSecondByPlayer.remove(playerId);
    }
    if (blackSanctuaryCalmUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      blackSanctuaryCalmUntilEpochSecondByPlayer.remove(playerId);
    }
    if (timeTaxFieldUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      timeTaxFieldUntilEpochSecondByPlayer.remove(playerId);
      timeTaxFieldTierByPlayer.remove(playerId);
    }
    if (reflectSkinVulnerableUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      reflectSkinVulnerableUntilEpochSecondByPlayer.remove(playerId);
    }
    long nowMillis = System.currentTimeMillis();
    if (reflectSkinNoReentryUntilEpochMilliByPlayer.getOrDefault(playerId, 0L) <= nowMillis) {
      reflectSkinNoReentryUntilEpochMilliByPlayer.remove(playerId);
    }
    if (clonePactSharedDamageGuardUntilEpochMilliByPlayer.getOrDefault(playerId, 0L) <= nowMillis) {
      clonePactSharedDamageGuardUntilEpochMilliByPlayer.remove(playerId);
    }
    if (ominousBlinkReblinkUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      ominousBlinkReblinkUntilEpochSecondByPlayer.remove(playerId);
      ominousBlinkNextPulseEpochSecondByPlayer.remove(playerId);
    }
    if (soundExposureUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      soundExposureUntilEpochSecondByPlayer.remove(playerId);
      soundExposureTierByPlayer.remove(playerId);
    }
    long hotbarPendingAt = hotbarCyclePendingEpochSecondByPlayer.getOrDefault(playerId, 0L);
    if (hotbarPendingAt > 0L && hotbarPendingAt + 5L <= nowEpochSecond) {
      hotbarCyclePendingEpochSecondByPlayer.remove(playerId);
      hotbarCyclePendingTierByPlayer.remove(playerId);
    }
    if (!isPlayerInRecentCombat(playerId, 12L)) {
      gripFractureHitCounterByPlayer.remove(playerId);
      weaponRebellionHitCounterByPlayer.remove(playerId);
    }
    if (buildBurstUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      Long fatigueAt = buildBurstFatiguePendingAtEpochSecondByPlayer.remove(playerId);
      if (fatigueAt != null && fatigueAt > 0L && fatigueAt <= nowEpochSecond) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null && player.isOnline()) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 60, 0, true, false, true));
        }
      }
      buildBurstUntilEpochSecondByPlayer.remove(playerId);
      buildBurstTierByPlayer.remove(playerId);
    }
    if (scoreInvestmentUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      scoreInvestmentUntilEpochSecondByPlayer.remove(playerId);
      scoreInvestmentBonusRatioByPlayer.remove(playerId);
    }
    if (projectileAlignUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      projectileAlignUntilEpochSecondByPlayer.remove(playerId);
    }
    if (projectileAbsorbWindowUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      projectileAbsorbWindowUntilEpochSecondByPlayer.remove(playerId);
      projectileAbsorbTierByPlayer.remove(playerId);
    }
    if (projectileAbsorbReflectUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      projectileAbsorbReflectUntilEpochSecondByPlayer.remove(playerId);
    }
    if (projectileReflectUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      projectileReflectUntilEpochSecondByPlayer.remove(playerId);
    }
    if (knockbackImmuneUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      knockbackImmuneUntilEpochSecondByPlayer.remove(playerId);
    }
    if (shieldCoreBreakBoostUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      shieldCoreBreakBoostUntilEpochSecondByPlayer.remove(playerId);
    }
    if (rewindGuardUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      rewindGuardUntilEpochSecondByPlayer.remove(playerId);
      rewindGuardRatioByPlayer.remove(playerId);
    }
    if (wallHangUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      wallHangUntilEpochSecondByPlayer.remove(playerId);
    }
    if (auraResonanceBacklashUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      auraResonanceBacklashUntilEpochSecondByPlayer.remove(playerId);
    }
    if (auraInversionUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      auraInversionUntilEpochSecondByPlayer.remove(playerId);
    }
    if (auraInversionBacklashUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      auraInversionBacklashUntilEpochSecondByPlayer.remove(playerId);
      auraInversionBacklashAppliedByPlayer.remove(playerId);
    }
    if (anchorFatalBufferUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond) {
      anchorFatalBufferUntilEpochSecondByPlayer.remove(playerId);
    }
  }


  private void applyBlessingEffect80Periodic(
      Player player,
      PlayerRoundData data,
      ActiveSeasonEffect effect,
      EffectGimmickProfile profile,
      int index,
      long nowEpochSecond
  ) {
    int tier = boostedBModTier(effect.getId(), effect.getTier());
    UUID uuid = player.getUniqueId();
    switch (index) {
      case 1 -> {
        int interval = switch (tier) {
          case 1, 2 -> 10;
          case 3 -> 8;
          default -> 5;
        };
        if (useEffectCooldown(uuid, effect.getId(), "shield_refill", interval)) {
          double refillAbsorption = switch (tier) {
            case 1 -> 20.0D;
            case 2 -> 30.0D;
            case 3 -> 40.0D;
            default -> 50.0D;
          };
          ensureShieldCoreAbsorptionCapacity(player, tier);
          player.setAbsorptionAmount(refillAbsorption);
          if (tier >= 2) {
            clearSingleNegativeEffect(player);
          }
          emitBModProcFeedback(player, tier, false);
        }
      }
      case 5 -> {
        // ON_BLOCK_BREAK (natural stone + pickaxe).
      }
      case 10 -> {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        boolean holdingHoe = mainHand != null && mainHand.getType().name().endsWith("_HOE");
        if (holdingHoe && isTouchingSolidWall(player) && player.getVelocity().getY() < 0.25D) {
          double climbVelocity = switch (tier) {
            case 1 -> 0.20D;
            case 2 -> 0.26D;
            case 3 -> 0.30D;
            default -> 0.34D;
          };
          climbVelocity = Math.min(0.48D, climbVelocity + 0.06D);
          player.setVelocity(player.getVelocity().setY(Math.min(0.48D, climbVelocity)));
          gravityLadderActiveUntilEpochMilliByPlayer.put(uuid, System.currentTimeMillis() + 300L);
          if (useEffectCooldown(uuid, effect.getId(), "wall_climb_fx", 2L)) {
            emitBModProcFeedback(player, tier, false);
          }
        }
      }
      case 13 -> {
        long burstUntil = buildBurstUntilEpochSecondByPlayer.getOrDefault(uuid, 0L);
        if (burstUntil > nowEpochSecond) {
          int hasteTicks = scaledBModDurationTicks(effect.getId(), 40);
          player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, hasteTicks, Math.max(0, tier - 1), true, false, true));
        }
      }
      case 16 -> {
        int interval = switch (tier) {
          case 1 -> 30;
          case 2 -> 20;
          case 3 -> 10;
          default -> 5;
        };
        if (useEffectCooldown(uuid, effect.getId(), "scanner_ping", interval)) {
          sendScannerHintByTier(player, tier, false);
          markResonanceScannerTargets(player, tier);
        }
      }
      case 17 -> {
        applyAuraInversionBlessingPeriodic(player, tier, nowEpochSecond);
      }
      case 18 -> {
        int radius = scaledBModRangeBlocks(effect.getId(), 12 + (tier * 2));
        int slowTicks = scaledBModDurationTicks(effect.getId(), 60);
        int slowAmp = switch (tier) {
          case 1 -> 0;
          case 2 -> 1;
          case 3 -> 3;
          default -> 4;
        };
        boolean affectedAny = false;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
          if (!(entity instanceof Enderman enderman)) {
            continue;
          }
          if (!enderman.getScoreboardTags().contains(STALKER_TAG)) {
            continue;
          }
          enderman.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowTicks, slowAmp, true, true, true));
          affectedAny = true;
        }
        if (affectedAny && useEffectCooldown(uuid, effect.getId(), "stalker_suppress_fx", 3L)) {
          emitBModProcFeedback(player, tier, false);
        }
      }
      case 19 -> {
        if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
          if (useEffectCooldown(uuid, effect.getId(), "raid_cleanse", 30L)) {
            clearSingleNegativeEffect(player);
          }
          if (tier >= 4) {
            markNearbyVexApproach(player);
          }
        }
      }
      default -> {
      }
    }
  }

  private void applyCurseEffect80Periodic(
      Player player,
      PlayerRoundData data,
      ActiveSeasonEffect effect,
      EffectGimmickProfile profile,
      int index,
      long nowEpochSecond
  ) {
    int tier = boostedBModTier(effect.getId(), effect.getTier());
    UUID uuid = player.getUniqueId();
    switch (index) {
      case 1 -> {
        int interval = 30;
        if (useEffectCooldown(uuid, effect.getId(), "ore_collapse", interval)) {
          int collapsed = collapseNearbyOreVeins(player, tier);
          if (tier >= 4 && collapsed > 0) {
            player.sendActionBar(ChatColor.DARK_RED + "광맥 붕괴: 주변 광물이 바스러졌습니다");
          }
        }
      }
      case 2 -> {
        int interval = tier <= 1 ? 90 : 60;
        boolean inCombat = tier >= 4 && isPlayerInRecentCombat(uuid, 10L);
        if (inCombat) {
          interval = 45;
        }
        if (useEffectCooldown(uuid, effect.getId(), "hotbar_rotate", interval)) {
          if (inCombat) {
            hotbarCyclePendingEpochSecondByPlayer.put(uuid, nowEpochSecond + 2L);
            hotbarCyclePendingTierByPlayer.put(uuid, tier);
            player.sendActionBar(ChatColor.RED + "핫바 윤회 예고: 2초 후 재배열");
          } else {
            executeHotbarCycle(player, tier);
          }
        }
        long pendingAt = hotbarCyclePendingEpochSecondByPlayer.getOrDefault(uuid, 0L);
        if (pendingAt > 0L && pendingAt <= nowEpochSecond) {
          int pendingTier = Math.max(1, hotbarCyclePendingTierByPlayer.getOrDefault(uuid, tier));
          executeHotbarCycle(player, pendingTier);
          hotbarCyclePendingEpochSecondByPlayer.remove(uuid);
          hotbarCyclePendingTierByPlayer.remove(uuid);
        }
      }
      case 4 -> {
        if (controlDistortionUntilEpochSecondByPlayer.getOrDefault(uuid, 0L) > nowEpochSecond) {
          int activeTier = Math.max(tier, controlDistortionTierByPlayer.getOrDefault(uuid, tier));
          if (activeTier >= 4 && useEffectCooldown(uuid, effect.getId(), "control_drift", 1L)) {
            randomizePlayerYaw(player, 16.0F);
          }
        }
      }
      case 5 -> {
        int interval = 90;
        if (useEffectCooldown(uuid, effect.getId(), "slot_lock", interval)) {
          int durationSeconds = switch (tier) {
            case 1 -> 20;
            case 2 -> 30;
            case 3 -> 30;
            default -> 35;
          };
          lockRandomHotbarSlots(uuid, tier >= 2 ? 2 : 1, durationSeconds);
        }
      }
      case 6 -> {
        int interval = 120;
        boolean inCombat = tier >= 4 && isPlayerInRecentCombat(uuid, 10L);
        if (inCombat) {
          interval = 90;
        }
        if (useEffectCooldown(uuid, effect.getId(), "tool_ban", interval)) {
          if (inCombat) {
            toolBanPendingEpochSecondByPlayer.put(uuid, nowEpochSecond + 1L);
            toolBanPendingTierByPlayer.put(uuid, tier);
            player.sendActionBar(ChatColor.RED + "도구 금단 예고: 1초 후 봉인");
          } else {
            applyToolBanByTier(uuid, tier);
          }
        }
        long pendingAt = toolBanPendingEpochSecondByPlayer.getOrDefault(uuid, 0L);
        if (pendingAt > 0L && pendingAt <= nowEpochSecond) {
          int pendingTier = Math.max(1, toolBanPendingTierByPlayer.getOrDefault(uuid, tier));
          applyToolBanByTier(uuid, pendingTier);
          toolBanPendingEpochSecondByPlayer.remove(uuid);
          toolBanPendingTierByPlayer.remove(uuid);
        }
        if (tier >= 2 && bannedToolUntilEpochSecond.getOrDefault(uuid, 0L) > nowEpochSecond) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
        }
      }
      case 9 -> {
        if (tier >= 4) {
          long reblinkUntil = ominousBlinkReblinkUntilEpochSecondByPlayer.getOrDefault(uuid, 0L);
          if (reblinkUntil > nowEpochSecond) {
            long nextPulse = ominousBlinkNextPulseEpochSecondByPlayer.getOrDefault(uuid, 0L);
            if (nextPulse <= nowEpochSecond) {
              ominousBlinkNextPulseEpochSecondByPlayer.put(uuid, nowEpochSecond + 1L);
              if (ThreadLocalRandom.current().nextDouble() < 0.45D) {
                performOminousReblink(player);
              }
            }
          }
        }
      }
      case 11 -> {
        if (!isLikelyEnclosed(player)) {
          enclosedSinceEpochSecondByPlayer.remove(uuid);
          break;
        }
        long enclosedSince = enclosedSinceEpochSecondByPlayer.computeIfAbsent(uuid, ignored -> nowEpochSecond);
        long thresholdSeconds = switch (tier) {
          case 1 -> 12L;
          case 2 -> 10L;
          case 3 -> 8L;
          default -> 6L;
        };
        if ((nowEpochSecond - enclosedSince) < thresholdSeconds) {
          break;
        }
        if (useEffectCooldown(uuid, effect.getId(), "claustro_tick", 3L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0, true, false, true));
          if (tier >= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0, true, false, true));
          }
          if (tier >= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 80, 0, true, false, true));
          }
          if (tier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 0, true, false, true));
          }
        }
      }
      case 12 -> {
        long time = player.getWorld().getTime();
        if (time >= 13000L && time <= 23000L) {
          if (useEffectCooldown(uuid, effect.getId(), "nightmare_tick", 10L)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0, true, false, true));
            if (tier >= 2) {
              player.sendActionBar(ChatColor.DARK_PURPLE + "야간 악몽: 뒤에서 시선이 느껴진다");
              player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 0.8F, 0.75F);
            }
            if (tier >= 2) {
              cursedSprintLockUntilEpochSecondByPlayer.put(uuid, nowEpochSecond + 2L);
              player.setSprinting(false);
            }
          }
          if (tier >= 4) {
            long combatStamp = lastCombatEpochSecondByPlayer.getOrDefault(uuid, 0L);
            long seenCombatStamp = nightmareLastCombatSeenEpochSecondByPlayer.getOrDefault(uuid, 0L);
            if (combatStamp > seenCombatStamp && (nowEpochSecond - combatStamp) <= 10L) {
              nightmareLastCombatSeenEpochSecondByPlayer.put(uuid, combatStamp);
              player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false, true));
            }
          }
        } else {
          nightmareLastCombatSeenEpochSecondByPlayer.remove(uuid);
        }
      }
      case 13 -> {
        if (useEffectCooldown(uuid, effect.getId(), "stalker_obsession_ping", 30L)) {
          player.sendActionBar(ChatColor.DARK_RED + "추격자 집착: 경고 없이 더 가까이 접근합니다");
        }
      }
      case 14 -> {
        // C14 applies through border hazard hook (tickBorderWitherHazard).
      }
      case 15 -> {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand != null && mainHand.getType() == Material.COMPASS) {
          if (useEffectCooldown(uuid, effect.getId(), "compass_distort", 6L)) {
            Vector toCenter = new Vector(-player.getLocation().getX(), 0.0D, -player.getLocation().getZ());
            player.sendActionBar(ChatColor.DARK_PURPLE + "나침반 왜곡: " + compassDirectionForPlayer(player, toCenter));
          }
        }
      }
      case 16 -> {
        int interval = switch (tier) {
          case 1 -> 90;
          case 2, 3 -> 75;
          default -> 60;
        };
        if (useEffectCooldown(uuid, effect.getId(), "sound_exposure_start", interval)) {
          int durationSeconds = tier >= 4 ? 8 : 4;
          soundExposureUntilEpochSecondByPlayer.put(uuid, nowEpochSecond + durationSeconds);
          soundExposureTierByPlayer.put(uuid, tier);
          player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, tier >= 4 ? 1.35F : 0.85F, tier >= 4 ? 0.70F : 0.92F);
          if (tier >= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, true, true, true));
          }
        }
        long exposureUntil = soundExposureUntilEpochSecondByPlayer.getOrDefault(uuid, 0L);
        if (exposureUntil > nowEpochSecond && useEffectCooldown(uuid, effect.getId(), "sound_exposure_tick", 1L)) {
          int activeTier = Math.max(tier, soundExposureTierByPlayer.getOrDefault(uuid, tier));
          float volume = activeTier >= 4 ? 1.20F : 0.70F;
          float pitch = activeTier >= 4 ? 0.75F : 1.15F;
          player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, volume, pitch);
          player.getWorld().spawnParticle(
              Particle.END_ROD,
              player.getLocation().clone().add(0.0D, 1.0D, 0.0D),
              activeTier >= 4 ? 28 : 14,
              0.55D,
              0.9D,
              0.55D,
              0.01D
          );
        }
      }
      case 17 -> {
        if (useEffectCooldown(uuid, effect.getId(), "drop_rot_tick", 5L)) {
          processCursedDropsForPlayer(player, tier, nowEpochSecond);
        }
      }
      case 20 -> {
        int interval = switch (tier) {
          case 1 -> 120;
          case 2, 3 -> 90;
          default -> 60;
        };
        if (useEffectCooldown(uuid, effect.getId(), "memory_wipe", interval)) {
          int removeCount = tier >= 2 ? 2 : 1;
          removePositivePotionEffects(player, removeCount);
          purgeCustomPositiveStates(uuid, removeCount);
          if (tier >= 4) {
            player.setAbsorptionAmount(0.0D);
          }
        }
      }
      case 19 -> {
        // Dragon breath curse handling is resolved via runtime modifiers.
      }
      default -> {
      }
    }
  }

  private void applyHybridEffect80Periodic(
      Player player,
      PlayerRoundData data,
      ActiveSeasonEffect effect,
      EffectGimmickProfile profile,
      int index,
      long nowEpochSecond
  ) {
    int tier = boostedBModTier(effect.getId(), effect.getTier());
    UUID uuid = player.getUniqueId();
    if (isXModCurseVariant(effect.getId())) {
      applyHybridXCurseVariantPeriodic(player, data, effect, index, tier, nowEpochSecond);
    }
    switch (index) {
      case 1 -> {
        if (useEffectCooldown(uuid, effect.getId(), "repulse", 12L)) {
          if (tier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, true, true, true));
            player.getWorld().spawnParticle(
                Particle.ELECTRIC_SPARK,
                player.getLocation().clone().add(0.0D, 1.0D, 0.0D),
                40,
                0.55D,
                0.75D,
                0.55D,
                0.02D
            );
            UUID playerId = player.getUniqueId();
            Bukkit.getScheduler().runTaskLater(this, () -> {
              Player online = Bukkit.getPlayer(playerId);
              if (online == null || !online.isOnline() || online.isDead()) {
                return;
              }
              PlayerRoundData onlineData = players.get(playerId);
              if (onlineData == null || onlineData.isOut()) {
                return;
              }
              int currentTier = highestEffect80Tier(onlineData, 'X', 1);
              if (currentTier <= 0) {
                return;
              }
              executeHybridRepulsePulse(online, currentTier);
            }, 40L);
          } else {
            executeHybridRepulsePulse(player, tier);
          }
        }
      }
      case 2 -> {
        applyBloodContractPeriodic(player, effect, tier, nowEpochSecond);
      }
      case 3 -> {
        if (useEffectCooldown(uuid, effect.getId(), "magnet", 1L)) {
          double radius = switch (tier) {
            case 1 -> 6.0D;
            case 2, 3 -> 10.0D;
            default -> 14.0D;
          };
          pullNearbyItems(player, radius, 1.0D + (tier * 0.25D));
          player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, tier >= 4 ? 40 : 20, 0, true, true, true));
          if (tier >= 2) {
            collectNearbyItemDrops(player, Math.min(3.0D, 1.5D + (tier * 0.5D)));
          }
          if (tier >= 2) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.95F, 0.7F);
            provokeNearbyMonsters(player, tier);
          }
        }
      }
      case 5 -> {
        if (useEffectCooldown(uuid, effect.getId(), "element_shift_fire", 2L)) {
          handleElementShiftFireWater(player, tier, true);
        }
      }
      case 6 -> {
        if (useEffectCooldown(uuid, effect.getId(), "element_shift_water", 2L)) {
          handleElementShiftFireWater(player, tier, false);
        }
      }
      case 10 -> {
        applyFrenzyEngineCombatState(player, effect, tier, nowEpochSecond);
        if (frenzyUntilEpochSecondByPlayer.getOrDefault(uuid, 0L) > nowEpochSecond) {
          int combatAmp = switch (tier) {
            case 1 -> 0;
            case 2, 3 -> 1;
            default -> 2;
          };
          player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, combatAmp, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, combatAmp, true, false, true));
          if (tier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, true, false, true));
          }
        } else if (frenzyFatigueUntilEpochSecondByPlayer.getOrDefault(uuid, 0L) > nowEpochSecond) {
          int stack = Math.max(1, frenzyFatigueStacksByPlayer.getOrDefault(uuid, 1));
          int fatigueAmp = tier >= 2 ? Math.min(2, stack - 1) : 0;
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, fatigueAmp, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, fatigueAmp, true, false, true));
          if (tier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, Math.min(1, fatigueAmp), true, false, true));
          }
        } else if (tier >= 2
            && frenzyFatigueStacksByPlayer.getOrDefault(uuid, 0) > 0
            && useEffectCooldown(uuid, effect.getId(), "frenzy_fatigue_stack_decay", 24L)) {
          int next = Math.max(0, frenzyFatigueStacksByPlayer.getOrDefault(uuid, 0) - 1);
          if (next <= 0) {
            frenzyFatigueStacksByPlayer.remove(uuid);
          } else {
            frenzyFatigueStacksByPlayer.put(uuid, next);
          }
        }
      }
      case 12 -> applyAuraResonancePeriodic(player, data, effect, tier, nowEpochSecond);
      case 13 -> {
        // X13 applies through border hazard hook (tickBorderWitherHazard).
      }
      case 17 -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 40, Math.max(0, tier - 1), true, false, true));
        if (tier >= 2) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0, true, false, true));
        }
        if (tier >= 2 && !player.isOnGround()) {
          Vector velocity = player.getVelocity();
          if (velocity != null && velocity.getY() < -0.18D) {
            player.setVelocity(velocity.clone().setY(Math.max(-0.18D, velocity.getY() * 0.65D)));
          }
        }
      }
      case 19 -> {
        int interval = switch (tier) {
          case 1 -> 20;
          case 2, 3 -> 18;
          default -> 16;
        };
        if (useEffectCooldown(uuid, effect.getId(), "raider_scent", interval)) {
          sendScannerHintByTier(player, tier, true);
          if (tier >= 2) {
            provokeNearbyMonsters(player, tier);
          }
          if (tier >= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 60, 0, true, false, true));
          }
          if (tier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 0, true, true, true));
            if (useEffectCooldown(uuid, effect.getId(), "raider_scent_precision", 90L)) {
              performPrecisionScannerPing(player);
            }
          }
        }
      }
      case 20 -> {
        long calmUntil = blackSanctuaryCalmUntilEpochSecondByPlayer.getOrDefault(uuid, 0L);
        if (calmUntil > nowEpochSecond && useEffectCooldown(uuid, effect.getId(), "sanctuary_calm_tick", 1L)) {
          weakenNearbyHostiles(player, tier, 10.0D);
          break;
        }
        int interval = switch (tier) {
          case 1 -> 30;
          case 2 -> 26;
          case 3 -> 22;
          default -> 20;
        };
        if (useEffectCooldown(uuid, effect.getId(), "sanctuary_spawn", interval)) {
          spawnHostileNearPlayer(player, tier);
        }
      }
      case 21 -> {
        if (player.getFoodLevel() <= 0 && useEffectCooldown(uuid, effect.getId(), "hunger_exchange_starvation_tick", 1L)) {
          applyHungerExchangeStarvationPenalty(player, tier);
        }
      }
      case 24 -> distortNearbyProjectiles(player, effect.getId(), tier);
      case 25 -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 80, 0, true, false, true));
        int fakeWarningInterval = switch (tier) {
          case 1 -> 120;
          case 2 -> 95;
          case 3 -> 80;
          default -> 65;
        };
        if (useEffectCooldown(uuid, effect.getId(), "nightmare_fake_warning", fakeWarningInterval)) {
          player.sendActionBar(ChatColor.DARK_PURPLE + "악몽 경보: 시야 왜곡 감지");
          player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 0.45F, 1.45F);
          player.getWorld().spawnParticle(Particle.SMOKE, player.getEyeLocation(), 10 + (tier * 2), 0.25D, 0.18D, 0.25D, 0.01D);
        }
        if (tier >= 2
            && (player.getWorld().getEnvironment() == World.Environment.NETHER
            || player.getWorld().getEnvironment() == World.Environment.THE_END)
            && useEffectCooldown(uuid, effect.getId(), "nightmare_darkness_pulse", 70L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 0, true, false, true));
        }
        if (tier >= 4 && useEffectCooldown(uuid, effect.getId(), "nightmare_outline_tradeoff", 90L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, true, true, true));
          UUID playerId = player.getUniqueId();
          Bukkit.getScheduler().runTaskLater(this, () -> {
            Player online = Bukkit.getPlayer(playerId);
            if (online == null || !online.isOnline() || online.isDead()) {
              return;
            }
            PlayerRoundData onlineData = players.get(playerId);
            if (onlineData == null || onlineData.isOut()) {
              return;
            }
            if (highestEffect80Tier(onlineData, 'X', 25) <= 0) {
              return;
            }
            online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true, false, true));
          }, 60L);
        }
      }
      case 26 -> {
        int interval = Math.max(18, 34 - (tier * 3));
        if (useEffectCooldown(uuid, effect.getId(), "bidirectional_transmute", interval)) {
          int sharedCap = tier >= 4 ? 2 : 1;
          int changed = 0;
          int remaining = sharedCap;
          changed += transmuteNearbyBlocks(player, false, true, tier, remaining);
          remaining = Math.max(0, sharedCap - changed);
          if (remaining > 0) {
            changed += transmuteNearbyBlocks(player, true, false, tier, remaining);
          }
          if (tier >= 4 && changed > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 120, 0, true, true, true));
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.55F, 0.95F);
          }
        }
      }
      case 27 -> {
        if (player.getWorld().getEnvironment() == World.Environment.NETHER
            && useEffectCooldown(uuid, effect.getId(), "nether_rift_cycle", 30L)) {
          int converted = transmuteNetherRiftBlocks(player, tier);
          if (converted > 0) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NETHER_GOLD_ORE_BREAK, 0.55F, 1.10F);
          }
          if (tier >= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 80, 0, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 80, 0, true, false, true));
          }
          if (tier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 0, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 0, true, false, true));
          }
        }
      }
      case 30 -> {
        if (useEffectCooldown(uuid, effect.getId(), "score_investment_cycle", 300L)) {
          processScoreInvestmentCycle(player, data, tier, nowEpochSecond);
        }
      }
      case 31 -> {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        String selectedGroup = weaponMonopolyGroupByPlayer.getOrDefault(uuid, "");
        if (selectedGroup.isBlank()) {
          String resolved = resolveWeaponMonopolyGroup(mainHand);
          if (!resolved.isBlank()) {
            selectedGroup = resolved;
            weaponMonopolyGroupByPlayer.put(uuid, resolved);
          }
        }
        if (!selectedGroup.isBlank()) {
          String currentGroup = resolveWeaponMonopolyGroup(mainHand);
          if (selectedGroup.equals(currentGroup)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, Math.max(0, tier - 2), true, false, true));
          } else if (!currentGroup.isBlank()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, tier >= 2 ? 1 : 0, true, false, true));
          }
        }
        if (tier >= 4 && useEffectCooldown(uuid, effect.getId(), "weapon_monopoly_fatigue", 8L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 0, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 0, true, false, true));
        }
      }
      case 33 -> {
        long afterimageUntil = enderOrbitAfterimageUntilEpochSecondByPlayer.getOrDefault(uuid, 0L);
        if (afterimageUntil > nowEpochSecond) {
          Location loc = player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
          player.getWorld().spawnParticle(Particle.PORTAL, loc, 28 + (tier * 4), 0.45D, 0.6D, 0.45D, 0.03D);
          if (tier >= 2) {
            player.getWorld().spawnParticle(Particle.SMOKE, loc, 10 + (tier * 2), 0.35D, 0.45D, 0.35D, 0.01D);
          }
          if (tier >= 2) {
            deflectNearbyProjectilesSlight(player, 2.5D + (tier * 0.7D));
          }
        } else {
          enderOrbitAfterimageUntilEpochSecondByPlayer.remove(uuid);
        }
        if (tier >= 4 && enderOrbitRapidFollowupUntilEpochSecondByPlayer.getOrDefault(uuid, 0L) > nowEpochSecond) {
          player.sendActionBar(ChatColor.DARK_AQUA + "엔더 궤도: 2연속 가능");
        }
      }
      case 37 -> {
        if (silenceVowSilentPlayers.add(uuid)) {
          player.setSilent(true);
        }
        if (tier >= 2 && !isPlayerInRecentCombat(uuid, 8L) && player.isSneaking()) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0, true, false, true));
          healPlayer(player, 0.4D);
        }
      }
      case 39 -> {
        long fieldUntil = timeTaxFieldUntilEpochSecondByPlayer.getOrDefault(uuid, 0L);
        if (fieldUntil > nowEpochSecond) {
          int fieldTier = Math.max(tier, Math.max(1, timeTaxFieldTierByPlayer.getOrDefault(uuid, tier)));
          double radius = switch (fieldTier) {
            case 1 -> 6.0D;
            case 2 -> 8.0D;
            case 3 -> 8.5D;
            default -> 9.5D;
          };
          int slownessAmp = switch (fieldTier) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            default -> 6;
          };
          for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living) || entity.getUniqueId().equals(uuid)) {
              continue;
            }
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, slownessAmp, true, false, true));
            if (fieldTier >= 4) {
              living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 0, true, false, true));
            }
          }
          if (fieldTier >= 2) {
            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
              if (!(entity instanceof Projectile projectile) || !projectile.isValid()) {
                continue;
              }
              projectile.setVelocity(projectile.getVelocity().multiply(fieldTier >= 4 ? 0.40D : 0.55D));
            }
          }
          player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().clone().add(0.0D, 1.0D, 0.0D), 10, 0.45D, 0.12D, 0.45D, 0.01D);
        } else {
          timeTaxFieldUntilEpochSecondByPlayer.remove(uuid);
          timeTaxFieldTierByPlayer.remove(uuid);
        }
      }
      case 40 -> {
        applySpiritExchangeBuff(player, tier);
      }
      default -> {
      }
    }
  }

  private void applyHybridXCurseVariantPeriodic(
      Player player,
      PlayerRoundData data,
      ActiveSeasonEffect effect,
      int index,
      int tier,
      long nowEpochSecond
  ) {
    if (player == null || data == null || effect == null || !isXModCurseVariant(effect.getId()) || tier <= 0) {
      return;
    }
    UUID playerId = player.getUniqueId();
    int clampedTier = Math.max(1, Math.min(4, tier));
    boolean inCombat = isPlayerInRecentCombat(playerId, 10L);
    boolean lowHealth = player.getHealth() <= Math.max(2.0D, playerMaxHealth(player) * 0.35D);

    switch (index) {
      case 1 -> {
        long interval = switch (clampedTier) {
          case 1 -> 18L;
          case 2 -> 16L;
          case 3 -> 14L;
          default -> 12L;
        };
        if (inCombat && useEffectCooldown(playerId, effect.getId(), "x_c01_aftershock", interval)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30 + (clampedTier * 10), clampedTier >= 3 ? 1 : 0, true, false, true));
        }
      }
      case 2 -> {
        long interval = switch (clampedTier) {
          case 1 -> 10L;
          case 2 -> 9L;
          case 3 -> 8L;
          default -> 7L;
        };
        if (!inCombat && useEffectCooldown(playerId, effect.getId(), "x_c02_drought", interval)) {
          player.damage(0.5D + (clampedTier * 0.2D), player);
          player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 50 + (clampedTier * 15), 0, true, false, true));
        }
      }
      case 3 -> {
        if (hasNearbyItemEntities(player, 5.0D + (clampedTier * 2.0D))
            && useEffectCooldown(playerId, effect.getId(), "x_c03_overload", 6L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60 + (clampedTier * 20), 0, true, false, true));
          if (clampedTier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40 + (clampedTier * 20), 0, true, true, true));
          }
        }
      }
      case 4 -> {
        long reserve = switch (clampedTier) {
          case 1 -> 240L;
          case 2 -> 300L;
          case 3 -> 360L;
          default -> 420L;
        };
        if (data.getScore() < reserve && useEffectCooldown(playerId, effect.getId(), "x_c04_leak", 10L)) {
          adjustPlayerScore(playerId, data, -(4L + clampedTier), false);
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 50, 0, true, false, true));
        }
      }
      case 5 -> {
        boolean wet = player.getLocation().getBlock().isLiquid();
        if (wet && useEffectCooldown(playerId, effect.getId(), "x_c05_wet", 5L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40 + (clampedTier * 10), clampedTier >= 3 ? 1 : 0, true, false, true));
          if (clampedTier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
          }
        }
      }
      case 6 -> {
        if (player.getFireTicks() > 0 && useEffectCooldown(playerId, effect.getId(), "x_c06_burn", 6L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60 + (clampedTier * 10), 0, true, false, true));
          if (clampedTier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
          }
        }
      }
      case 7 -> {
        String topType = String.valueOf(player.getOpenInventory().getTopInventory().getType());
        if (topType.toUpperCase(Locale.ROOT).contains("CRAFT")
            && useEffectCooldown(playerId, effect.getId(), "x_c07_craft_tax", 12L)) {
          int foodLoss = clampedTier >= 3 ? 2 : 1;
          player.setFoodLevel(Math.max(0, player.getFoodLevel() - foodLoss));
          if (clampedTier >= 3) {
            applyRandomNegativeEffect(player, 40 + (clampedTier * 20), 0);
          }
        }
      }
      case 8 -> {
        if (inCombat && useEffectCooldown(playerId, effect.getId(), "x_c08_vertigo", 14L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40 + (clampedTier * 20), 0, true, false, true));
        }
      }
      case 9 -> {
        if (ghostUntilEpochSecond.getOrDefault(playerId, 0L) > nowEpochSecond
            && useEffectCooldown(playerId, effect.getId(), "x_c09_toll", 2L)) {
          int foodLoss = clampedTier >= 3 ? 2 : 1;
          player.setFoodLevel(Math.max(0, player.getFoodLevel() - foodLoss));
          if (clampedTier >= 4) {
            adjustPlayerScore(playerId, data, -8L, false);
          }
        }
      }
      case 10 -> {
        if (frenzyUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > nowEpochSecond
            && useEffectCooldown(playerId, effect.getId(), "x_c10_overheat", 7L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 0, true, false, true));
          if (clampedTier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
          }
        }
      }
      case 11 -> {
        long time = player.getWorld().getTime();
        boolean night = time >= 13000L && time <= 23000L;
        if (night && useEffectCooldown(playerId, effect.getId(), "x_c11_beacon", 15L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40 + (clampedTier * 10), 0, true, true, true));
        }
      }
      case 12 -> {
        double auraRadius = enderAuraRadius() + (clampedTier * 1.5D);
        if (isInsideEnderAura(player, auraRadius)
            && useEffectCooldown(playerId, effect.getId(), "x_c12_backlash", 6L)) {
          player.damage(0.4D + (clampedTier * 0.25D), player);
          if (clampedTier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
          }
        }
      }
      case 13 -> {
        if (isOutsideCurrentBorder(player.getLocation())
            && useEffectCooldown(playerId, effect.getId(), "x_c13_border", 4L)) {
          adjustPlayerScore(playerId, data, -(4L + clampedTier), false);
          if (clampedTier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 0, true, false, true));
          }
        }
      }
      case 14 -> {
        if (player.getLocation().getY() < 48.0D
            && useEffectCooldown(playerId, effect.getId(), "x_c14_depth_noise", 10L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0, true, false, true));
        }
      }
      case 15 -> {
        if (fortressModeUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > nowEpochSecond
            && useEffectCooldown(playerId, effect.getId(), "x_c15_fortress_load", 8L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 80, 0, true, false, true));
          if (clampedTier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
          }
        }
      }
      case 16 -> {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand != null
            && isSwordMaterial(mainHand.getType())
            && useEffectCooldown(playerId, effect.getId(), "x_c16_recoil", 9L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40 + (clampedTier * 10), 0, true, false, true));
        }
      }
      case 17 -> {
        if (!player.isOnGround() && useEffectCooldown(playerId, effect.getId(), "x_c17_air_sickness", 5L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40, 0, true, false, true));
          if (clampedTier >= 3) {
            noAttackUntilEpochSecond.put(playerId, Math.max(noAttackUntilEpochSecond.getOrDefault(playerId, 0L), nowEpochSecond + 1L));
          }
        }
      }
      case 18 -> {
        if (countEmptyInventorySlots(player) <= Math.max(1, 4 - clampedTier)
            && useEffectCooldown(playerId, effect.getId(), "x_c18_overpack", 12L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, clampedTier >= 3 ? 1 : 0, true, false, true));
        }
      }
      case 19 -> {
        if (hasNearbyHostileEntity(player, 8.0D + (clampedTier * 2.5D))
            && useEffectCooldown(playerId, effect.getId(), "x_c19_marked", 9L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, true, true, true));
        }
      }
      case 20 -> {
        long interval = switch (clampedTier) {
          case 1 -> 14L;
          case 2 -> 12L;
          case 3 -> 10L;
          default -> 8L;
        };
        if (useEffectCooldown(playerId, effect.getId(), "x_c20_tribute", interval)) {
          adjustPlayerScore(playerId, data, -(3L + clampedTier), false);
          if (inCombat) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
          }
        }
      }
      case 21 -> {
        if (player.getFoodLevel() >= 16 && useEffectCooldown(playerId, effect.getId(), "x_c21_leak", 6L)) {
          player.setFoodLevel(Math.max(0, player.getFoodLevel() - 1));
          if (clampedTier >= 4 && inCombat) {
            player.damage(0.5D, player);
          }
        }
      }
      case 22 -> {
        if (hasAnyBeneficialPotionEffect(player)
            && useEffectCooldown(playerId, effect.getId(), "x_c22_backlash", 8L)) {
          applyRandomNegativeEffect(player, 40 + (clampedTier * 20), clampedTier >= 3 ? 1 : 0);
        }
      }
      case 23 -> {
        if (useEffectCooldown(playerId, effect.getId(), "x_c23_erosion", 12L)) {
          applyArmorDurabilitySacrifice(player, 0.7D + (clampedTier * 0.3D), Math.max(1, clampedTier - 1));
          if (clampedTier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
          }
        }
      }
      case 24 -> {
        if (player.isBlocking() && useEffectCooldown(playerId, effect.getId(), "x_c24_jam", 4L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30 + (clampedTier * 10), 1, true, false, true));
          if (clampedTier >= 3) {
            player.setSprinting(false);
          }
        }
      }
      case 25 -> {
        if (!inCombat && useEffectCooldown(playerId, effect.getId(), "x_c25_dread", 16L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40 + (clampedTier * 10), 0, true, false, true));
        }
      }
      case 26 -> {
        World.Environment environment = player.getWorld().getEnvironment();
        if ((environment == World.Environment.NETHER || environment == World.Environment.THE_END)
            && useEffectCooldown(playerId, effect.getId(), "x_c26_contam", 12L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60 + (clampedTier * 20), clampedTier >= 3 ? 1 : 0, true, false, true));
        }
      }
      case 27 -> {
        if (player.getWorld().getEnvironment() == World.Environment.NETHER
            && useEffectCooldown(playerId, effect.getId(), "x_c27_rift_fever", 6L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40 + (clampedTier * 20), 0, true, false, true));
        }
      }
      case 28 -> {
        if (inCombat && useEffectCooldown(playerId, effect.getId(), "x_c28_feedback", 8L)) {
          player.damage(0.4D + (clampedTier * 0.2D), player);
        }
      }
      case 29 -> {
        if (!lowHealth && inCombat && useEffectCooldown(playerId, effect.getId(), "x_c29_fragile", 10L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, clampedTier >= 3 ? 1 : 0, true, false, true));
        }
      }
      case 30 -> {
        if (scoreInvestmentUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > nowEpochSecond
            && useEffectCooldown(playerId, effect.getId(), "x_c30_tax", 3L)) {
          adjustPlayerScore(playerId, data, -(2L + clampedTier), false);
        }
      }
      case 31 -> {
        String selectedGroup = weaponMonopolyGroupByPlayer.getOrDefault(playerId, "");
        if (selectedGroup.isBlank()) {
          break;
        }
        String currentGroup = resolveWeaponMonopolyGroup(player.getInventory().getItemInMainHand());
        if (!selectedGroup.equals(currentGroup)
            && useEffectCooldown(playerId, effect.getId(), "x_c31_lock", 6L)) {
          long lockUntil = nowEpochSecond + 1L;
          noAttackUntilEpochSecond.put(playerId, Math.max(noAttackUntilEpochSecond.getOrDefault(playerId, 0L), lockUntil));
          noBuildUntilEpochSecond.put(playerId, Math.max(noBuildUntilEpochSecond.getOrDefault(playerId, 0L), lockUntil));
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
        }
      }
      case 32 -> {
        long interval = clampedTier >= 3 ? 8L : 12L;
        if (useEffectCooldown(playerId, effect.getId(), "x_c32_reveal", interval)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40 + (clampedTier * 20), 0, true, true, true));
        }
      }
      case 33 -> {
        if (enderOrbitAfterimageUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > nowEpochSecond
            && useEffectCooldown(playerId, effect.getId(), "x_c33_dizzy", 3L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40, 0, true, false, true));
        }
      }
      case 34 -> {
        if (decoyExpireEpochSecondByPlayer.getOrDefault(playerId, 0L) > nowEpochSecond
            && useEffectCooldown(playerId, effect.getId(), "x_c34_drain", 6L)) {
          player.setFoodLevel(Math.max(0, player.getFoodLevel() - 1));
          if (clampedTier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
          }
        }
      }
      case 35 -> {
        if (reflectSkinVulnerableUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > nowEpochSecond
            && useEffectCooldown(playerId, effect.getId(), "x_c35_crack", 5L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
        }
      }
      case 36 -> {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        boolean holdingExplosive = mainHand != null
            && (mainHand.getType() == Material.TNT || mainHand.getType() == Material.END_CRYSTAL);
        if (holdingExplosive && useEffectCooldown(playerId, effect.getId(), "x_c36_instability", 8L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, true, true, true));
          if (clampedTier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
          }
        }
      }
      case 37 -> {
        if (inCombat && useEffectCooldown(playerId, effect.getId(), "x_c37_mute", 9L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
          if (clampedTier >= 4) {
            noAttackUntilEpochSecond.put(playerId, Math.max(noAttackUntilEpochSecond.getOrDefault(playerId, 0L), nowEpochSecond + 1L));
          }
        }
      }
      case 38 -> {
        if (player.getFoodLevel() <= 6 && useEffectCooldown(playerId, effect.getId(), "x_c38_collapse", 5L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40 + (clampedTier * 10), 0, true, false, true));
        }
      }
      case 39 -> {
        if (timeTaxFieldUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) <= nowEpochSecond
            && inCombat
            && useEffectCooldown(playerId, effect.getId(), "x_c39_lag", 12L)) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, clampedTier >= 3 ? 1 : 0, true, false, true));
        }
      }
      case 40 -> {
        if (useEffectCooldown(playerId, effect.getId(), "x_c40_sidefx", 10L)) {
          String state = spiritExchangeStateByPlayer.getOrDefault(playerId, resolveSpiritExchangeState(player));
          switch (state) {
            case "END" -> player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 80, 0, true, false, true));
            case "NETHER" -> player.setFireTicks(Math.max(player.getFireTicks(), 40 + (clampedTier * 10)));
            case "STORM" -> player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
            case "NIGHT" -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, true, false, true));
            default -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
          }
        }
      }
      default -> {
      }
    }
  }

  private boolean hasNearbyItemEntities(Player player, double radius) {
    if (player == null || radius <= 0.0D) {
      return false;
    }
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (entity instanceof Item item && item.isValid() && !item.isDead()) {
        return true;
      }
    }
    return false;
  }

  private boolean hasNearbyHostileEntity(Player player, double radius) {
    if (player == null || radius <= 0.0D) {
      return false;
    }
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (entity instanceof Monster monster && monster.isValid() && !monster.isDead()) {
        return true;
      }
    }
    return false;
  }

  private boolean hasAnyBeneficialPotionEffect(Player player) {
    if (player == null) {
      return false;
    }
    for (PotionEffect active : player.getActivePotionEffects()) {
      if (active == null || active.getType() == null) {
        continue;
      }
      if (isBeneficialPotionType(active.getType())) {
        return true;
      }
    }
    return false;
  }

  private int countEmptyInventorySlots(Player player) {
    if (player == null || player.getInventory() == null) {
      return 0;
    }
    int empty = 0;
    for (ItemStack stack : player.getInventory().getStorageContents()) {
      if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) {
        empty++;
      }
    }
    return empty;
  }

  private void tickPlacedBlockDecay() {
    if (collapsingPlacedBlocksUntilEpochSecond.isEmpty()) {
      collapsingPlacedBlocksKeepDrop.clear();
      long now = nowEpochSecond();
      reinforcedBurstBlocksUntilEpochSecond.entrySet().removeIf(entry -> entry == null
          || entry.getKey() == null
          || entry.getValue() == null
          || entry.getValue() <= now);
      return;
    }
    long now = nowEpochSecond();
    List<String> expiredKeys = new ArrayList<>();
    for (Map.Entry<String, Long> entry : collapsingPlacedBlocksUntilEpochSecond.entrySet()) {
      if (entry == null || entry.getKey() == null) {
        continue;
      }
      if (entry.getValue() == null || entry.getValue() > now) {
        continue;
      }
      Block block = resolveBlockKey(entry.getKey());
      if (block != null && block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
        if (collapsingPlacedBlocksKeepDrop.contains(entry.getKey())) {
          dropCollapsedPlacedBlock(block);
        } else {
          block.setType(Material.AIR, false);
        }
      }
      expiredKeys.add(entry.getKey());
    }
    for (String key : expiredKeys) {
      collapsingPlacedBlocksUntilEpochSecond.remove(key);
      collapsingPlacedBlocksKeepDrop.remove(key);
    }
    reinforcedBurstBlocksUntilEpochSecond.entrySet().removeIf(entry -> entry == null
        || entry.getKey() == null
        || entry.getValue() == null
        || entry.getValue() <= now);
  }



































  private boolean isInsideCobweb(Player player) {
    if (player == null) {
      return false;
    }
    Material feet = player.getLocation().getBlock().getType();
    Material body = player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock().getType();
    return feet == Material.COBWEB || body == Material.COBWEB;
  }

  private void clearCobwebCrossAtPlayer(Player player) {
    if (player == null) {
      return;
    }
    Block center = player.getLocation().getBlock();
    clearCobwebBlock(center);
    clearCobwebBlock(center.getRelative(BlockFace.NORTH));
    clearCobwebBlock(center.getRelative(BlockFace.SOUTH));
    clearCobwebBlock(center.getRelative(BlockFace.EAST));
    clearCobwebBlock(center.getRelative(BlockFace.WEST));
  }

  private void clearCobwebBlock(Block block) {
    if (block == null) {
      return;
    }
    if (block.getType() == Material.COBWEB) {
      block.setType(Material.AIR, false);
    }
  }

  private boolean isOnIcySurface(Player player) {
    if (player == null) {
      return false;
    }
    Material below = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
    return below == Material.ICE
        || below == Material.PACKED_ICE
        || below == Material.BLUE_ICE
        || below == Material.FROSTED_ICE;
  }

  private boolean isInHotBiome(Player player) {
    if (player == null || player.getWorld() == null) {
      return false;
    }
    String biome = String.valueOf(player.getLocation().getBlock().getBiome()).toUpperCase(Locale.ROOT);
    return biome.contains("DESERT")
        || biome.contains("BADLANDS")
        || biome.contains("SAVANNA");
  }

  private boolean isInColdBiome(Player player) {
    if (player == null || player.getWorld() == null) {
      return false;
    }
    String biome = String.valueOf(player.getLocation().getBlock().getBiome()).toUpperCase(Locale.ROOT);
    return biome.contains("SNOW")
        || biome.contains("FROZEN")
        || biome.contains("ICE")
        || biome.contains("TAIGA")
        || biome.contains("GROVE")
        || biome.contains("PEAK");
  }

  private boolean isInCaveBiome(Player player) {
    if (player == null || player.getWorld() == null) {
      return false;
    }
    String biome = String.valueOf(player.getLocation().getBlock().getBiome()).toUpperCase(Locale.ROOT);
    return biome.contains("CAVE")
        || biome.contains("DEEP_DARK")
        || biome.contains("DRIPSTONE");
  }

  private boolean isMainHandEmpty(Player player) {
    if (player == null || player.getInventory() == null) {
      return false;
    }
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    return mainHand == null || mainHand.getType() == Material.AIR;
  }

  private boolean isMainHandAxe(Player player) {
    if (player == null || player.getInventory() == null) {
      return false;
    }
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    return mainHand != null && isAxeMaterial(mainHand.getType());
  }

  private boolean isMainHandTridentLike(Player player) {
    if (player == null || player.getInventory() == null) {
      return false;
    }
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    if (mainHand == null) {
      return false;
    }
    Material material = mainHand.getType();
    if (material == Material.TRIDENT) {
      return true;
    }
    String name = material.name();
    return name.endsWith("_SPEAR");
  }

  private boolean isMainHandEnderPearl(Player player) {
    if (player == null || player.getInventory() == null) {
      return false;
    }
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    return mainHand != null && mainHand.getType() == Material.ENDER_PEARL;
  }

  private boolean hasPositivePotionEffect(Player player) {
    if (player == null) {
      return false;
    }
    for (PotionEffect effect : player.getActivePotionEffects()) {
      if (effect == null || effect.getType() == null) {
        continue;
      }
      PotionEffectType type = effect.getType();
      if (type.equals(PotionEffectType.SPEED)
          || type.equals(PotionEffectType.HASTE)
          || type.equals(PotionEffectType.STRENGTH)
          || type.equals(PotionEffectType.JUMP_BOOST)
          || type.equals(PotionEffectType.REGENERATION)
          || type.equals(PotionEffectType.RESISTANCE)
          || type.equals(PotionEffectType.FIRE_RESISTANCE)
          || type.equals(PotionEffectType.WATER_BREATHING)
          || type.equals(PotionEffectType.NIGHT_VISION)
          || type.equals(PotionEffectType.ABSORPTION)
          || type.equals(PotionEffectType.HEALTH_BOOST)
          || type.equals(PotionEffectType.DOLPHINS_GRACE)
          || type.equals(PotionEffectType.HERO_OF_THE_VILLAGE)) {
        return true;
      }
    }
    return false;
  }

  private double playerHealthRatio(Player player) {
    if (player == null) {
      return 1.0D;
    }
    double max = player.getAttribute(Attribute.MAX_HEALTH) == null
        ? player.getMaxHealth()
        : player.getAttribute(Attribute.MAX_HEALTH).getValue();
    if (max <= 0.0D) {
      return 1.0D;
    }
    return Math.max(0.0D, Math.min(1.0D, player.getHealth() / max));
  }

  private boolean isNearStronghold(Player player, double radiusBlocks) {
    if (player == null || player.getWorld() == null || radiusBlocks <= 0.0D) {
      return false;
    }
    World world = player.getWorld();
    if (world.getEnvironment() != World.Environment.NORMAL || !world.canGenerateStructures()) {
      return false;
    }
    Location stronghold = cachedNearestStronghold(world, player.getLocation());
    if (stronghold == null || stronghold.getWorld() != world) {
      return false;
    }
    return stronghold.distanceSquared(player.getLocation()) <= (radiusBlocks * radiusBlocks);
  }

  private Location cachedNearestStronghold(World world, Location origin) {
    if (world == null || origin == null) {
      return null;
    }
    String worldKey = world.getName().toLowerCase(Locale.ROOT);
    long now = nowEpochSecond();
    long cachedUntil = strongholdLocateCacheExpireEpochSecondByWorld.getOrDefault(worldKey, 0L);
    if (cachedUntil > now && strongholdLocateCacheByWorld.containsKey(worldKey)) {
      return strongholdLocateCacheByWorld.get(worldKey);
    }
    Location located = world.locateNearestStructure(
        origin,
        StructureType.STRONGHOLD,
        256,
        false
    );
    strongholdLocateCacheByWorld.put(worldKey, located);
    strongholdLocateCacheExpireEpochSecondByWorld.put(worldKey, now + 600L);
    return located;
  }











  private AbilityToken detectAbilityToken(ItemStack item) {
    if (item == null || item.getType() == Material.AIR) {
      return AbilityToken.NONE;
    }
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      if (meta.hasCustomModelData()) {
        int data = meta.getCustomModelData();
        if (data == 7001) {
          return AbilityToken.MOBILITY;
        }
        if (data == 7002) {
          return AbilityToken.DEFENSE;
        }
        if (data == 7003) {
          return AbilityToken.OFFENSE;
        }
        if (data == 7004) {
          return AbilityToken.UTILITY;
        }
        if (data == 7005) {
          return AbilityToken.BUILD;
        }
      }
      String name = meta.hasDisplayName() ? ChatColor.stripColor(meta.getDisplayName()) : "";
      if (name != null && !name.isBlank()) {
        String normalized = name.toUpperCase(Locale.ROOT);
        if (normalized.contains("TOKEN_MOBILITY")) {
          return AbilityToken.MOBILITY;
        }
        if (normalized.contains("TOKEN_DEFENSE")) {
          return AbilityToken.DEFENSE;
        }
        if (normalized.contains("TOKEN_OFFENSE")) {
          return AbilityToken.OFFENSE;
        }
        if (normalized.contains("TOKEN_UTILITY")) {
          return AbilityToken.UTILITY;
        }
        if (normalized.contains("TOKEN_BUILD")) {
          return AbilityToken.BUILD;
        }
      }
    }
    Material type = item.getType();
    if (isSwordMaterial(type) || type == Material.BLAZE_ROD) {
      return AbilityToken.OFFENSE;
    }
    return switch (type) {
      case FEATHER, ENDER_PEARL -> AbilityToken.MOBILITY;
      case SHIELD, TOTEM_OF_UNDYING -> AbilityToken.DEFENSE;
      case COMPASS, CLOCK, BOOK -> AbilityToken.UTILITY;
      case BRICKS, COBBLESTONE, STONE -> AbilityToken.BUILD;
      default -> AbilityToken.NONE;
    };
  }

  private AbilityMode detectAbilityMode(Player player) {
    if (player == null) {
      return AbilityMode.NORMAL;
    }
    if (player.isSprinting()) {
      return AbilityMode.SPRINT;
    }
    if (player.isSneaking()) {
      return AbilityMode.SNEAK;
    }
    return AbilityMode.NORMAL;
  }

  private boolean handleEffect80ActiveInteract(PlayerInteractEvent event, Player player, PlayerRoundData data) {
    if (event == null || player == null || data == null) {
      return false;
    }
    Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
      return false;
    }
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    AbilityToken heldToken = detectAbilityToken(mainHand);
    AbilityMode heldMode = detectAbilityMode(player);
    long nowEpochSecond = nowEpochSecond();

    if (tryActivateBlessingB010GhostWalk(event, player, data, mainHand, nowEpochSecond)) {
      return true;
    }
    if (tryActivateBlessingB009Blink(event, player, data, mainHand, nowEpochSecond)) {
      return true;
    }
    if (tryActivateBlessingB036LootPortal(event, player, data, heldToken, heldMode, nowEpochSecond)) {
      return true;
    }
    if (tryActivateBlessingB035InstantCraftWorkbench(event, player, data, mainHand)) {
      return true;
    }
    return tryActivateBlessingB039AuraCommand(event, player, data, heldToken, heldMode, nowEpochSecond);
  }

  private boolean tryActivateBlessingB009Blink(
      PlayerInteractEvent event,
      Player player,
      PlayerRoundData data,
      ItemStack mainHand,
      long nowEpochSecond
  ) {
    ActiveSeasonEffect effect = data.getBlessingEffect("B-009");
    if (effect == null || mainHand == null || !isSwordMaterial(mainHand.getType())) {
      return false;
    }
    ItemStack offHand = player.getInventory().getItemInOffHand();
    if (offHand != null && offHand.getType() == Material.SHIELD) {
      return false;
    }
    int tier = clampTier(effect.getTier());
    long cooldownSeconds = switch (tier) {
      case 1 -> 20L;
      case 2 -> 15L;
      case 3 -> 10L;
      default -> 5L;
    };
    if (!useEffectCooldown(player.getUniqueId(), effect.getId(), "b009_blink_cast", cooldownSeconds)) {
      return false;
    }
    double distance = switch (tier) {
      case 1 -> 10.0D;
      case 2 -> 15.0D;
      case 3 -> 20.0D;
      default -> 25.0D;
    };
    executePhaseWalk(player, distance, tier);
    long noFallSeconds = switch (tier) {
      case 3 -> 1L;
      case 4 -> 2L;
      default -> 0L;
    };
    if (noFallSeconds > 0L) {
      blinkFallImmunityUntilEpochSecondByPlayer.put(
          player.getUniqueId(),
          Math.max(blinkFallImmunityUntilEpochSecondByPlayer.getOrDefault(player.getUniqueId(), 0L), nowEpochSecond + noFallSeconds)
      );
    }
    event.setCancelled(true);
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8F, 1.2F);
    return true;
  }

  private boolean tryActivateBlessingB010GhostWalk(
      PlayerInteractEvent event,
      Player player,
      PlayerRoundData data,
      ItemStack mainHand,
      long nowEpochSecond
  ) {
    ActiveSeasonEffect effect = data.getBlessingEffect("B-010");
    if (effect == null || mainHand == null || !isSwordMaterial(mainHand.getType())) {
      return false;
    }
    ItemStack offHand = player.getInventory().getItemInOffHand();
    if (offHand != null && offHand.getType() == Material.SHIELD) {
      return false;
    }
    int tier = clampTier(effect.getTier());
    long cooldownSeconds = switch (tier) {
      case 1 -> 60L;
      case 2 -> 50L;
      case 3 -> 40L;
      default -> 30L;
    };
    if (!useEffectCooldown(player.getUniqueId(), effect.getId(), "b010_ghost_walk_cast", cooldownSeconds)) {
      return false;
    }
    long durationSeconds = switch (tier) {
      case 1 -> 7L;
      case 2 -> 8L;
      case 3 -> 9L;
      default -> 10L;
    };
    UUID playerId = player.getUniqueId();
    long until = nowEpochSecond + durationSeconds;
    ghostUntilEpochSecond.put(playerId, Math.max(ghostUntilEpochSecond.getOrDefault(playerId, 0L), until));
    ghostTierByPlayer.put(playerId, tier);
    noAttackUntilEpochSecond.put(playerId, Math.max(noAttackUntilEpochSecond.getOrDefault(playerId, 0L), until));
    knockbackImmuneUntilEpochSecondByPlayer.put(
        playerId,
        Math.max(knockbackImmuneUntilEpochSecondByPlayer.getOrDefault(playerId, 0L), until)
    );
    player.getWorld().spawnParticle(
        Particle.PORTAL,
        player.getLocation().clone().add(0.0D, 1.0D, 0.0D),
        30 + (tier * 8),
        0.4D,
        0.7D,
        0.4D,
        0.05D
    );
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 0.8F, 0.75F);
    event.setCancelled(true);
    return true;
  }

  private boolean tryActivateBlessingB035InstantCraftWorkbench(
      PlayerInteractEvent event,
      Player player,
      PlayerRoundData data,
      ItemStack mainHand
  ) {
    ActiveSeasonEffect effect = data.getBlessingEffect("B-035");
    if (effect == null || mainHand == null || mainHand.getType() != Material.CRAFTING_TABLE) {
      return false;
    }
    event.setCancelled(true);
    player.openWorkbench(null, true);
    UUID playerId = player.getUniqueId();
    Bukkit.getScheduler().runTaskLater(this, () -> {
      Player online = Bukkit.getPlayer(playerId);
      if (online == null || !online.isOnline()) {
        return;
      }
      autoFillRememberedRecipe(online);
    }, 1L);
    player.sendActionBar(ChatColor.GREEN + "제작 최적화: 개인 제작 UI를 열었습니다");
    return true;
  }

  private boolean tryActivateBlessingB036LootPortal(
      PlayerInteractEvent event,
      Player player,
      PlayerRoundData data,
      AbilityToken heldToken,
      AbilityMode heldMode,
      long nowEpochSecond
  ) {
    ActiveSeasonEffect effect = data.getBlessingEffect("B-036");
    if (effect == null || heldToken != AbilityToken.UTILITY || heldMode != AbilityMode.NORMAL) {
      return false;
    }
    int tier = clampTier(effect.getTier());
    long cooldownSeconds = switch (tier) {
      case 1 -> 10L;
      case 2 -> 8L;
      case 3 -> 6L;
      default -> 4L;
    };
    if (!useEffectCooldown(player.getUniqueId(), effect.getId(), "b036_loot_portal", cooldownSeconds)) {
      return false;
    }
    if (player.isSneaking()) {
      openLootPortalInventory(player);
    } else {
      absorbNearbyDropsToLootPortal(player, tier);
    }
    event.setCancelled(true);
    lootPortalExpireEpochSecondByPlayer.put(player.getUniqueId(), nowEpochSecond + 3600L);
    return true;
  }

  private boolean tryActivateBlessingB039AuraCommand(
      PlayerInteractEvent event,
      Player player,
      PlayerRoundData data,
      AbilityToken heldToken,
      AbilityMode heldMode,
      long nowEpochSecond
  ) {
    ActiveSeasonEffect effect = data.getBlessingEffect("B-039");
    if (effect == null || heldToken != AbilityToken.UTILITY || heldMode != AbilityMode.NORMAL) {
      return false;
    }
    int tier = clampTier(effect.getTier());
    if (tier < 2) {
      return false;
    }
    long cooldownSeconds = tier >= 4 ? 60L : 120L;
    if (!useEffectCooldown(player.getUniqueId(), effect.getId(), "b039_aura_active", cooldownSeconds)) {
      return false;
    }
    long durationSeconds = tier >= 4 ? 30L : 20L;
    auraInversionUntilEpochSecondByPlayer.put(
        player.getUniqueId(),
        Math.max(auraInversionUntilEpochSecondByPlayer.getOrDefault(player.getUniqueId(), 0L), nowEpochSecond + durationSeconds)
    );
    auraInversionBacklashAppliedByPlayer.remove(player.getUniqueId());
    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.65F, 1.0F);
    player.sendActionBar(ChatColor.LIGHT_PURPLE + "오라 지휘 발동 (" + durationSeconds + "s)");
    event.setCancelled(true);
    return true;
  }

  private void handleBlessingB035CraftingGimmick(CraftItemEvent event, Player player, PlayerRoundData data) {
    if (event == null || player == null || data == null) {
      return;
    }
    ActiveSeasonEffect effect = data.getBlessingEffect("B-035");
    if (effect == null || !(event.getInventory() instanceof CraftingInventory crafting)) {
      return;
    }
    ItemStack result = event.getCurrentItem();
    if (result == null || result.getType() == Material.AIR) {
      return;
    }
    ItemStack[] matrix = crafting.getMatrix();
    if (matrix == null || matrix.length == 0) {
      return;
    }
    rememberCraftingMatrix(player.getUniqueId(), matrix);

    int tier = clampTier(effect.getTier());
    int ingredientSlots = 0;
    for (ItemStack ingredient : matrix) {
      if (ingredient != null && ingredient.getType() != Material.AIR && ingredient.getAmount() > 0) {
        ingredientSlots++;
      }
    }
    if (ingredientSlots <= 0) {
      return;
    }

    double noConsumeChance = switch (tier) {
      case 1 -> 0.00D;
      case 2 -> 0.10D;
      case 3 -> 0.20D;
      default -> 0.30D;
    };
    int highCostThreshold = switch (tier) {
      case 1 -> 9;
      case 2 -> 8;
      case 3 -> 7;
      default -> 6;
    };
    if (ingredientSlots >= highCostThreshold) {
      noConsumeChance *= 1.5D;
    }

    long nowEpochSecond = nowEpochSecond();
    long bonusUntil = instantCraftBonusUntilEpochSecondByPlayer.getOrDefault(player.getUniqueId(), 0L);
    if (bonusUntil > nowEpochSecond) {
      double chainBonus = switch (tier) {
        case 1 -> 0.05D;
        case 2 -> 0.10D;
        case 3 -> 0.15D;
        default -> 0.20D;
      };
      noConsumeChance += chainBonus;
      instantCraftBonusUntilEpochSecondByPlayer.remove(player.getUniqueId());
    }
    noConsumeChance = Math.max(0.0D, Math.min(0.95D, noConsumeChance));
    if (ThreadLocalRandom.current().nextDouble() >= noConsumeChance) {
      return;
    }

    ItemStack[] snapshot = new ItemStack[matrix.length];
    for (int i = 0; i < matrix.length; i++) {
      snapshot[i] = matrix[i] == null ? null : matrix[i].clone();
    }
    Bukkit.getScheduler().runTask(this, () -> refundCraftingIngredientsOnce(player, snapshot));
    instantCraftBonusUntilEpochSecondByPlayer.put(player.getUniqueId(), nowEpochSecond + 20L);
    player.sendActionBar(ChatColor.GREEN + "제작 최적화 발동: 이번 제작은 재료를 소모하지 않습니다");
  }

  private void updateRewindSnapshot(Player player, int tier) {
    if (player == null || !player.isOnline()) {
      return;
    }
    UUID uuid = player.getUniqueId();
    Deque<PlayerRewindSnapshot> snapshots = rewindSnapshotsByPlayer.computeIfAbsent(uuid, ignored -> new ArrayDeque<>());
    snapshots.addLast(new PlayerRewindSnapshot(
        player.getLocation().clone(),
        player.getHealth(),
        player.getFoodLevel(),
        player.getSaturation(),
        player.getFireTicks(),
        player.getFallDistance(),
        nowEpochSecond()
    ));
    int maxSize = Math.max(10, 12 + tier);
    while (snapshots.size() > maxSize) {
      snapshots.removeFirst();
    }
  }

  private void performTimeRewind(Player player, int tier) {
    int secondsBack = Math.max(4, Math.min(10, 5 + tier));
    performTimeRewind(player, tier, secondsBack);
  }

  private void performTimeRewind(Player player, int tier, int secondsBack) {
    if (player == null || !player.isOnline()) {
      return;
    }
    Deque<PlayerRewindSnapshot> snapshots = rewindSnapshotsByPlayer.get(player.getUniqueId());
    if (snapshots == null || snapshots.isEmpty()) {
      return;
    }
    int rewindSeconds = Math.max(2, Math.min(60, secondsBack));
    long targetEpoch = nowEpochSecond() - rewindSeconds;
    PlayerRewindSnapshot chosen = null;
    for (PlayerRewindSnapshot snapshot : snapshots) {
      if (snapshot == null) {
        continue;
      }
      if (snapshot.epochSecond() <= targetEpoch) {
        chosen = snapshot;
      }
    }
    if (chosen == null) {
      chosen = snapshots.peekFirst();
    }
    if (chosen == null || chosen.location() == null) {
      return;
    }
    Location safe = findNearestSafeLocation(chosen.location(), 6);
    if (safe != null) {
      player.teleport(safe, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    double maxHealth = player.getAttribute(Attribute.MAX_HEALTH) == null
        ? player.getMaxHealth()
        : player.getAttribute(Attribute.MAX_HEALTH).getValue();
    player.setHealth(Math.max(1.0D, Math.min(maxHealth, chosen.health())));
    if (tier >= 2) {
      player.setFoodLevel(Math.max(0, Math.min(20, chosen.foodLevel())));
      player.setSaturation(Math.max(0.0F, chosen.saturation()));
    }
    player.setFireTicks(Math.max(0, chosen.fireTicks()));
    player.setFallDistance(Math.max(0.0F, chosen.fallDistance()));
    player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40 + (tier * 10), 0, true, false, true));
  }

  private void maintainShieldCoreState(Player player, int tier, long nowEpochSecond) {
    if (player == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    long breakBoostUntil = shieldCoreBreakBoostUntilEpochSecondByPlayer.getOrDefault(playerId, 0L);
    boolean breakBoostActive = breakBoostUntil > nowEpochSecond;
    AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
    if (maxHealthAttribute != null) {
      shieldCoreOriginalMaxHealthBaseByPlayer.putIfAbsent(playerId, maxHealthAttribute.getBaseValue());
      double targetBase = breakBoostActive ? 10.0D : 1.0D;
      maxHealthAttribute.setBaseValue(targetBase);
      if (player.getHealth() > targetBase) {
        player.setHealth(targetBase);
      }
    }

    ensureShieldCoreAbsorptionCapacity(player, tier);

    double currentAbsorption = Math.max(0.0D, player.getAbsorptionAmount());
    double lastAbsorption = Math.max(0.0D, shieldCoreLastAbsorptionByPlayer.getOrDefault(playerId, currentAbsorption));
    if (tier >= 4 && lastAbsorption > 0.0D && currentAbsorption <= 0.0D) {
      long boostUntil = nowEpochSecond + 10L;
      shieldCoreBreakBoostUntilEpochSecondByPlayer.put(playerId, boostUntil);
      knockbackImmuneUntilEpochSecondByPlayer.put(playerId, boostUntil);
    }
    shieldCoreLastAbsorptionByPlayer.put(playerId, currentAbsorption);
  }

  private void ensureShieldCoreAbsorptionCapacity(Player player, int tier) {
    if (player == null) {
      return;
    }
    AttributeInstance maxAbsorptionAttribute = player.getAttribute(Attribute.MAX_ABSORPTION);
    if (maxAbsorptionAttribute == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    shieldCoreOriginalMaxAbsorptionBaseByPlayer.putIfAbsent(playerId, maxAbsorptionAttribute.getBaseValue());
    double targetCapacity = switch (Math.max(1, Math.min(4, tier))) {
      case 1 -> 20.0D;
      case 2 -> 30.0D;
      case 3 -> 40.0D;
      default -> 50.0D;
    };
    if (Math.abs(maxAbsorptionAttribute.getBaseValue() - targetCapacity) > 0.0001D) {
      maxAbsorptionAttribute.setBaseValue(targetCapacity);
    }
  }

  private void restoreShieldCoreMaxHealth(Player player) {
    if (player == null) {
      return;
    }
    UUID uuid = player.getUniqueId();
    shieldCoreBreakBoostUntilEpochSecondByPlayer.remove(uuid);
    AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
    Double original = shieldCoreOriginalMaxHealthBaseByPlayer.remove(uuid);
    if (maxHealthAttribute != null && original != null && original > 0.0D) {
      maxHealthAttribute.setBaseValue(original);
      double maxHealth = maxHealthAttribute.getValue();
      if (player.getHealth() > maxHealth) {
        player.setHealth(maxHealth);
      }
    }

    AttributeInstance maxAbsorptionAttribute = player.getAttribute(Attribute.MAX_ABSORPTION);
    Double originalAbsorption = shieldCoreOriginalMaxAbsorptionBaseByPlayer.remove(uuid);
    if (maxAbsorptionAttribute != null && originalAbsorption != null) {
      maxAbsorptionAttribute.setBaseValue(Math.max(0.0D, originalAbsorption));
      double maxAbsorption = Math.max(0.0D, maxAbsorptionAttribute.getValue());
      if (player.getAbsorptionAmount() > maxAbsorption) {
        player.setAbsorptionAmount(maxAbsorption);
      }
    }
  }

  private void recoverShieldCoreStateIfStuck(Player player, PlayerRoundData data) {
    if (player == null) {
      return;
    }
    if (data != null && hasForcedAbsorptionCapacityGimmick(data)) {
      return;
    }
    EnumMap<RuntimeModifierType, Double> totals = computeRuntimeModifierTotals(data, player.getWorld(), player);
    if (Math.abs(runtimeModifierValue(totals, RuntimeModifierType.MAX_HEALTH_RATIO)) > 0.0001D
        || Math.abs(runtimeModifierValue(totals, RuntimeModifierType.MAX_ABSORPTION_RATIO)) > 0.0001D) {
      return;
    }

    UUID playerId = player.getUniqueId();

    AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
    if (maxHealthAttribute != null && !shieldCoreOriginalMaxHealthBaseByPlayer.containsKey(playerId)) {
      double currentBase = maxHealthAttribute.getBaseValue();
      double defaultBase = Math.max(1.0D, maxHealthAttribute.getDefaultValue());
      if (currentBase <= 2.05D && defaultBase > 2.05D) {
        maxHealthAttribute.setBaseValue(defaultBase);
        if (player.getHealth() > defaultBase) {
          player.setHealth(defaultBase);
        }
      }
    }

    AttributeInstance maxAbsorptionAttribute = player.getAttribute(Attribute.MAX_ABSORPTION);
    if (maxAbsorptionAttribute != null && !shieldCoreOriginalMaxAbsorptionBaseByPlayer.containsKey(playerId)) {
      double currentBase = maxAbsorptionAttribute.getBaseValue();
      double defaultBase = Math.max(0.0D, maxAbsorptionAttribute.getDefaultValue());
      if (currentBase >= 39.5D) {
        maxAbsorptionAttribute.setBaseValue(defaultBase);
      }
      double maxAbsorption = Math.max(0.0D, maxAbsorptionAttribute.getValue());
      if (player.getAbsorptionAmount() > maxAbsorption) {
        player.setAbsorptionAmount(maxAbsorption);
      }
    }
  }

  private boolean consumeRechargeCharge(
      UUID playerId,
      Map<UUID, Integer> chargeMap,
      Map<UUID, Long> rechargeAtMap,
      int maxCharges,
      long rechargeSeconds,
      long nowEpochSecond
  ) {
    if (playerId == null || maxCharges <= 0 || rechargeSeconds <= 0L) {
      return false;
    }
    int charges = Math.max(0, Math.min(maxCharges, chargeMap.getOrDefault(playerId, maxCharges)));
    long rechargeAt = rechargeAtMap.getOrDefault(playerId, 0L);
    while (charges < maxCharges && rechargeAt > 0L && rechargeAt <= nowEpochSecond) {
      charges++;
      rechargeAt += rechargeSeconds;
    }
    if (charges <= 0) {
      chargeMap.put(playerId, 0);
      rechargeAtMap.put(playerId, rechargeAt);
      return false;
    }
    charges--;
    if (charges < maxCharges && rechargeAt <= nowEpochSecond) {
      rechargeAt = nowEpochSecond + rechargeSeconds;
    }
    chargeMap.put(playerId, charges);
    rechargeAtMap.put(playerId, rechargeAt);
    return true;
  }

  private void teleportForwardSafely(Player player, double distance, int verticalAssist) {
    if (player == null || !player.isOnline()) {
      return;
    }
    Location origin = player.getLocation().clone();
    Vector direction = origin.getDirection().normalize();
    Location target = origin.clone().add(direction.multiply(Math.max(2.0D, distance)));
    target.setYaw(origin.getYaw());
    target.setPitch(origin.getPitch());
    Location safe = findNearestSafeLocation(target, Math.max(4, verticalAssist + 2));
    if (safe == null) {
      safe = findNearestSafeLocation(origin.clone().add(0.0D, 1.0D, 0.0D), 4);
    }
    if (safe != null) {
      player.teleport(safe, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
  }

  private void teleportBackwardSafely(Player player, double distance, int verticalAssist) {
    if (player == null || !player.isOnline()) {
      return;
    }
    Location origin = player.getLocation().clone();
    Vector backward = origin.getDirection().normalize().multiply(-1.0D);
    Location target = origin.clone().add(backward.multiply(Math.max(2.0D, distance)));
    target.setYaw(origin.getYaw());
    target.setPitch(origin.getPitch());
    Location safe = findNearestSafeLocation(target, Math.max(4, verticalAssist + 2));
    if (safe == null) {
      safe = findNearestSafeLocation(origin.clone().add(0.0D, 1.0D, 0.0D), 4);
    }
    if (safe != null) {
      player.teleport(safe, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
  }

  private Location findNearestSafeLocation(Location center, int verticalAssist) {
    if (center == null || center.getWorld() == null) {
      return null;
    }
    World world = center.getWorld();
    int baseX = center.getBlockX();
    int baseY = center.getBlockY();
    int baseZ = center.getBlockZ();
    int maxY = world.getMaxHeight() - 2;
    int minY = world.getMinHeight() + 1;
    int range = Math.max(2, verticalAssist);
    for (int dy = 0; dy <= range; dy++) {
      for (int sign : new int[] {1, -1}) {
        int y = baseY + (dy * sign);
        if (y < minY || y > maxY) {
          continue;
        }
        Block feet = world.getBlockAt(baseX, y, baseZ);
        Block head = world.getBlockAt(baseX, y + 1, baseZ);
        Block below = world.getBlockAt(baseX, y - 1, baseZ);
        if (feet.isPassable() && head.isPassable() && !below.isPassable() && below.getType() != Material.LAVA) {
          return new Location(world, baseX + 0.5D, y, baseZ + 0.5D, center.getYaw(), center.getPitch());
        }
      }
    }
    return null;
  }

  private void spawnOrRefreshDecoy(Player player, int tier) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    UUID uuid = player.getUniqueId();
    int clampedTier = Math.max(1, Math.min(4, tier));
    long durationSeconds = clampedTier <= 1 ? 6L : 8L;
    UUID existingId = decoyEntityByPlayer.get(uuid);
    Entity existing = existingId == null ? null : Bukkit.getEntity(existingId);
    if (existing instanceof ArmorStand stand && stand.isValid()) {
      stand.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
      decoyExpireEpochSecondByPlayer.put(uuid, nowEpochSecond() + durationSeconds);
      decoyTierByPlayer.put(uuid, clampedTier);
      decoySwapUsedByPlayer.put(uuid, false);
      return;
    }

    Location spawn = player.getLocation().clone();
    ArmorStand stand = player.getWorld().spawn(spawn, ArmorStand.class, spawned -> {
      spawned.setInvisible(false);
      spawned.setSilent(true);
      spawned.setGravity(false);
      spawned.setInvulnerable(true);
      spawned.setCustomName(ChatColor.GRAY + player.getName() + "의 분신");
      spawned.setCustomNameVisible(true);
      spawned.getEquipment().setHelmet(new ItemStack(Material.PLAYER_HEAD));
    });
    decoyEntityByPlayer.put(uuid, stand.getUniqueId());
    decoyExpireEpochSecondByPlayer.put(uuid, nowEpochSecond() + durationSeconds);
    decoyTierByPlayer.put(uuid, clampedTier);
    decoySwapUsedByPlayer.put(uuid, false);
  }

  private void spawnClonePactAuxDecoys(Player player, int tier) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    clearClonePactAuxDecoys(playerId);
    if (tier < 4) {
      return;
    }
    List<UUID> spawnedIds = new ArrayList<>();
    World world = player.getWorld();
    Location origin = player.getLocation();

    ArmorStand stand = world.spawn(origin.clone().add(1.2D, 0.0D, 0.0D), ArmorStand.class, spawned -> {
      spawned.setInvisible(false);
      spawned.setSilent(true);
      spawned.setGravity(false);
      spawned.setInvulnerable(true);
      spawned.setCustomName(ChatColor.DARK_GRAY + player.getName() + "의 분신");
      spawned.setCustomNameVisible(false);
      spawned.getEquipment().setHelmet(new ItemStack(Material.PLAYER_HEAD));
    });
    spawnedIds.add(stand.getUniqueId());
    clonePactAuxDecoyEntityIdsByPlayer.put(playerId, spawnedIds);
  }

  private void clearClonePactAuxDecoys(UUID playerId) {
    if (playerId == null) {
      return;
    }
    List<UUID> entityIds = clonePactAuxDecoyEntityIdsByPlayer.remove(playerId);
    if (entityIds == null || entityIds.isEmpty()) {
      return;
    }
    for (UUID entityId : entityIds) {
      if (entityId == null) {
        continue;
      }
      Entity entity = Bukkit.getEntity(entityId);
      if (entity != null && entity.isValid()) {
        entity.remove();
      }
    }
  }

  private void emitDecoyCollapsePulse(Location location, int tier) {
    if (location == null || location.getWorld() == null) {
      return;
    }
    World world = location.getWorld();
    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.clone().add(0.0D, 1.0D, 0.0D), 18, 0.35D, 0.45D, 0.35D, 0.01D);
    int radius = 4 + Math.max(0, tier);
    for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
      if (!(entity instanceof Monster monster)) {
        continue;
      }
      monster.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false, true));
      if (tier >= 4) {
        monster.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20, 0, true, false, true));
      }
    }
  }

  private boolean handleAnchorAbility(PlayerInteractEvent event, Player player, int tier) {
    if (event == null || player == null) {
      return false;
    }
    UUID uuid = player.getUniqueId();
    if (player.isSneaking()
        && event.getAction() == Action.RIGHT_CLICK_BLOCK
        && event.getClickedBlock() != null) {
      Location anchorLocation = event.getClickedBlock().getLocation().clone().add(0.5D, 1.0D, 0.5D);
      Location previous = anchorPrimaryLocationByPlayer.put(uuid, anchorLocation);
      if (tier >= 2 && previous != null) {
        anchorSecondaryLocationByPlayer.put(uuid, previous);
      }
      player.sendActionBar(ChatColor.AQUA + "귀환 앵커가 설정되었습니다");
      return true;
    }

    if (!player.isSprinting()) {
      return false;
    }
    Location target = anchorPrimaryLocationByPlayer.get(uuid);
    if (target == null) {
      target = anchorSecondaryLocationByPlayer.get(uuid);
    }
    if (target == null) {
      player.sendActionBar(ChatColor.RED + "귀환 앵커가 설정되지 않았습니다");
      return false;
    }
    if (target.getWorld() == null || target.getWorld() != player.getWorld()) {
      player.sendActionBar(ChatColor.RED + "귀환 앵커: 동일 차원에서만 귀환할 수 있습니다");
      return false;
    }
    Location safe = findNearestSafeLocation(target, 6);
    if (safe == null) {
      player.sendActionBar(ChatColor.RED + "귀환 앵커: 안전 지점을 찾지 못했습니다");
      return false;
    }
    player.teleport(safe, PlayerTeleportEvent.TeleportCause.PLUGIN);
    int regenAmplifier = switch (Math.max(1, Math.min(4, tier))) {
      case 1 -> 1; // regen II
      case 2, 3 -> 2; // regen III
      default -> 3; // regen IV
    };
    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, regenAmplifier, true, false, true));
    return true;
  }

  private void absorbNearbyDropsToLootPortal(Player player, int tier) {
    if (player == null) {
      return;
    }
    UUID uuid = player.getUniqueId();
    List<ItemStack> stored = lootPortalItemsByPlayer.computeIfAbsent(uuid, ignored -> new ArrayList<>());
    int clampedTier = Math.max(1, Math.min(4, tier));
    double radius = switch (clampedTier) {
      case 1 -> 10.0D;
      case 2 -> 15.0D;
      case 3 -> 20.0D;
      default -> 25.0D;
    };
    int capacitySlots = switch (clampedTier) {
      case 1 -> 9;
      case 2 -> 18;
      case 3 -> 27;
      default -> 36;
    };
    boolean filterEnabled = lootPortalFilterEnabledByPlayer.getOrDefault(uuid, false);
    int collected = 0;
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Item itemEntity) || !itemEntity.isValid()) {
        continue;
      }
      ItemStack stack = itemEntity.getItemStack();
      if (stack == null || stack.getType() == Material.AIR) {
        continue;
      }
      if (filterEnabled && isLootPortalFilteredItem(stack.getType())) {
        continue;
      }
      if (stored.size() >= capacitySlots && !canAutoStackIntoLootPortal(stored, stack, clampedTier)) {
        break;
      }
      addItemToLootPortalStorage(stored, stack.clone(), clampedTier, capacitySlots);
      itemEntity.remove();
      collected++;
    }
    if (clampedTier >= 2) {
      int absorbedXp = 0;
      for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
        if (!(entity instanceof ExperienceOrb orb) || !orb.isValid()) {
          continue;
        }
        absorbedXp += Math.max(0, orb.getExperience());
        orb.remove();
      }
      if (absorbedXp > 0) {
        int total = Math.max(0, lootPortalStoredXpByPlayer.getOrDefault(uuid, 0));
        lootPortalStoredXpByPlayer.put(uuid, total + absorbedXp);
      }
    }
    lootPortalExpireEpochSecondByPlayer.put(uuid, nowEpochSecond() + 3600L);
    lootPortalTierByPlayer.put(uuid, clampedTier);
    player.sendActionBar(ChatColor.GOLD + "전리품 포탈 흡수: " + collected + "개");
  }

  private void openLootPortalInventory(Player player) {
    if (player == null) {
      return;
    }
    UUID uuid = player.getUniqueId();
    List<ItemStack> stored = lootPortalItemsByPlayer.get(uuid);
    int storedXp = Math.max(0, lootPortalStoredXpByPlayer.getOrDefault(uuid, 0));
    int tier = Math.max(1, lootPortalTierByPlayer.getOrDefault(uuid, 1));
    if ((stored == null || stored.isEmpty()) && storedXp <= 0) {
      player.sendActionBar(ChatColor.GRAY + "임시창고가 비어 있습니다");
      return;
    }
    int size = switch (Math.max(1, Math.min(4, tier))) {
      case 1 -> 9;
      case 2 -> 18;
      case 3 -> 27;
      default -> 36;
    };
    Inventory inventory = Bukkit.createInventory(null, size, "Loot Portal");
    int index = 0;
    if (stored != null) {
      for (ItemStack stack : stored) {
        if (stack == null || stack.getType() == Material.AIR) {
          continue;
        }
        if (index >= inventory.getSize()) {
          break;
        }
        inventory.setItem(index++, stack.clone());
      }
    }
    player.openInventory(inventory);
    if (stored != null) {
      stored.clear();
      lootPortalItemsByPlayer.remove(uuid);
    }
    if (storedXp > 0) {
      player.giveExp(storedXp);
      player.sendActionBar(ChatColor.AQUA + "전리품 포탈 XP 회수: " + storedXp);
    }
    lootPortalStoredXpByPlayer.remove(uuid);
    lootPortalExpireEpochSecondByPlayer.remove(uuid);
    lootPortalTierByPlayer.remove(uuid);
    if (tier >= 4) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, true, true, true));
    }
  }

  private void addItemToLootPortalStorage(List<ItemStack> stored, ItemStack incoming, int tier, int maxSlots) {
    if (stored == null || incoming == null || incoming.getType() == Material.AIR) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    int capacity = Math.max(1, maxSlots);
    if (clampedTier >= 2) {
      int remaining = Math.max(1, incoming.getAmount());
      for (ItemStack existing : stored) {
        if (existing == null || existing.getType() != incoming.getType()) {
          continue;
        }
        if (existing.getAmount() >= existing.getMaxStackSize()) {
          continue;
        }
        int moved = Math.min(remaining, existing.getMaxStackSize() - existing.getAmount());
        existing.setAmount(existing.getAmount() + moved);
        remaining -= moved;
        if (remaining <= 0) {
          return;
        }
      }
      while (remaining > 0 && stored.size() < capacity) {
        ItemStack stack = incoming.clone();
        int amount = Math.min(stack.getMaxStackSize(), remaining);
        stack.setAmount(amount);
        stored.add(stack);
        remaining -= amount;
      }
      return;
    }
    if (stored.size() < capacity) {
      stored.add(incoming.clone());
    }
  }

  private boolean canAutoStackIntoLootPortal(List<ItemStack> stored, ItemStack incoming, int tier) {
    if (stored == null || incoming == null || incoming.getType() == Material.AIR) {
      return false;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    if (clampedTier < 2) {
      return false;
    }
    for (ItemStack existing : stored) {
      if (existing == null || existing.getType() != incoming.getType()) {
        continue;
      }
      if (existing.getAmount() < existing.getMaxStackSize()) {
        return true;
      }
    }
    return false;
  }

  private boolean isLootPortalFilteredItem(Material material) {
    if (material == null) {
      return false;
    }
    return material == Material.DIRT
        || material == Material.COBBLESTONE
        || material == Material.GRAVEL
        || material == Material.NETHERRACK
        || material == Material.COBBLED_DEEPSLATE;
  }

  private void toggleProjectileAbsorb(Player player, String effectId, int tier) {
    if (player == null) {
      return;
    }
    UUID uuid = player.getUniqueId();
    long now = nowEpochSecond();
    long windowUntil = projectileAbsorbWindowUntilEpochSecondByPlayer.getOrDefault(uuid, 0L);
    List<StoredProjectile> stored = storedProjectilesByPlayer.computeIfAbsent(uuid, ignored -> new ArrayList<>());
    if (windowUntil > now) {
      releaseStoredProjectiles(player, tier);
      projectileAbsorbWindowUntilEpochSecondByPlayer.remove(uuid);
      projectileAbsorbTierByPlayer.remove(uuid);
      projectileAbsorbReflectUntilEpochSecondByPlayer.remove(uuid);
      return;
    }
    projectileAbsorbWindowUntilEpochSecondByPlayer.put(uuid, now + 1L);
    projectileAbsorbTierByPlayer.put(uuid, Math.max(1, Math.min(4, tier)));
    stored.clear();
    if (tier >= 4 && effectId != null && !effectId.isBlank()
        && useEffectCooldown(uuid, effectId, "projectile_absorb_reflect", 180L)) {
      projectileAbsorbReflectUntilEpochSecondByPlayer.put(uuid, now + 1L);
      player.sendActionBar(ChatColor.AQUA + "탄도 흡수막: 흡수 + 약화 반사 준비");
      return;
    }
    player.sendActionBar(ChatColor.AQUA + "탄도 흡수막: 투사체를 흡수합니다");
  }

  private void captureAbsorbableProjectiles(Player player, long nowEpochSecond) {
    if (player == null) {
      return;
    }
    UUID uuid = player.getUniqueId();
    long windowUntil = projectileAbsorbWindowUntilEpochSecondByPlayer.getOrDefault(uuid, 0L);
    if (windowUntil <= nowEpochSecond) {
      return;
    }
    List<StoredProjectile> stored = storedProjectilesByPlayer.computeIfAbsent(uuid, ignored -> new ArrayList<>());
    int tier = Math.max(1, projectileAbsorbTierByPlayer.getOrDefault(uuid, 1));
    int cap = switch (tier) {
      case 1 -> 4;
      case 2 -> 6;
      case 3 -> 7;
      default -> 8;
    };
    double radius = switch (tier) {
      case 1 -> 4.0D;
      case 2 -> 5.0D;
      case 3 -> 5.5D;
      default -> 6.0D;
    };
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Projectile projectile) || !projectile.isValid()) {
        continue;
      }
      Object shooter = projectile.getShooter();
      if (shooter instanceof Player shooterPlayer && shooterPlayer.getUniqueId().equals(uuid)) {
        continue;
      }
      if (tier >= 4
          && projectileAbsorbReflectUntilEpochSecondByPlayer.getOrDefault(uuid, 0L) > nowEpochSecond
          && isReflectableProjectile(projectile)) {
        reflectIncomingProjectileWeak(projectile, player);
        projectileAbsorbReflectUntilEpochSecondByPlayer.remove(uuid);
        continue;
      }
      if (stored.size() >= cap) {
        break;
      }
      Vector velocity = projectile.getVelocity().clone();
      stored.add(new StoredProjectile(projectile.getType(), velocity));
      projectile.remove();
    }
  }

  private void releaseStoredProjectiles(Player player, int tier) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    UUID uuid = player.getUniqueId();
    List<StoredProjectile> stored = storedProjectilesByPlayer.get(uuid);
    if (stored == null || stored.isEmpty()) {
      return;
    }
    int released = 0;
    Location lastDamager = lastDamagerLocationByPlayer.get(uuid);
    Vector homingDirection = null;
    if (tier >= 2 && lastDamager != null && lastDamager.getWorld() == player.getWorld()) {
      Vector toDamager = lastDamager.toVector().subtract(player.getEyeLocation().toVector());
      if (toDamager.lengthSquared() > 0.001D) {
        homingDirection = toDamager.normalize();
      }
    }
    for (StoredProjectile shot : stored) {
      if (shot == null || shot.entityType() == null) {
        continue;
      }
      Entity spawnedEntity = player.getWorld().spawnEntity(
          player.getEyeLocation().clone().add(player.getLocation().getDirection().multiply(0.6D)),
          shot.entityType()
      );
      if (!(spawnedEntity instanceof Projectile spawnedProjectile)) {
        spawnedEntity.remove();
        continue;
      }
      Vector base = shot.velocity() == null ? player.getLocation().getDirection() : shot.velocity();
      double releaseScale = switch (Math.max(1, Math.min(4, tier))) {
        case 1 -> 0.70D;
        case 2 -> 0.85D;
        case 3 -> 0.95D;
        default -> 1.00D;
      };
      Vector fired = base.clone().normalize().multiply(Math.max(0.3D, base.length() * releaseScale));
      double yawSpread = switch (Math.max(1, Math.min(4, tier))) {
        case 1 -> 10.0D;
        case 2 -> 6.0D;
        case 3 -> 4.0D;
        default -> 3.0D;
      };
      fired = applyDirectionalSpread(fired, yawSpread, yawSpread * 0.45D);
      if (homingDirection != null) {
        double speed = Math.max(0.3D, fired.length());
        Vector blended = fired.clone().normalize().multiply(0.65D).add(homingDirection.clone().multiply(0.35D));
        if (blended.lengthSquared() > 0.0001D) {
          fired = blended.normalize().multiply(speed);
        }
      }
      spawnedProjectile.setVelocity(fired);
      spawnedProjectile.setShooter(player);
      released++;
    }
    stored.clear();
    projectileAbsorbTierByPlayer.remove(uuid);
    projectileAbsorbReflectUntilEpochSecondByPlayer.remove(uuid);
    player.sendActionBar(ChatColor.AQUA + "탄도 방출: " + released + "개");
  }

  private Vector applyDirectionalSpread(Vector vector, double yawDegrees, double pitchDegrees) {
    if (vector == null || vector.lengthSquared() <= 0.0001D) {
      return vector == null ? new Vector(0.0D, 0.0D, 0.0D) : vector.clone();
    }
    double yawJitter = Math.toRadians(ThreadLocalRandom.current().nextDouble(-yawDegrees, yawDegrees));
    double pitchJitter = Math.toRadians(ThreadLocalRandom.current().nextDouble(-pitchDegrees, pitchDegrees));
    return vector.clone().rotateAroundY(yawJitter).rotateAroundX(pitchJitter);
  }

  private boolean isReflectableProjectile(Projectile projectile) {
    if (projectile == null || projectile.getType() == null) {
      return false;
    }
    return switch (projectile.getType()) {
      case FIREBALL, SMALL_FIREBALL, DRAGON_FIREBALL, WITHER_SKULL -> true;
      default -> false;
    };
  }

  private void reflectIncomingProjectileWeak(Projectile projectile, Player defender) {
    if (projectile == null || defender == null || !projectile.isValid()) {
      return;
    }
    Vector incoming = projectile.getVelocity();
    Vector direction;
    if (incoming != null && incoming.lengthSquared() > 0.0001D) {
      direction = incoming.clone().multiply(-1.0D).normalize();
    } else {
      direction = defender.getEyeLocation().getDirection().normalize();
    }
    double speed = incoming == null ? 0.6D : Math.max(0.45D, incoming.length() * 0.70D);
    projectile.setVelocity(direction.multiply(speed));
    projectile.setShooter(defender);
    defender.getWorld().playSound(defender.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.7F, 0.95F);
    defender.getWorld().spawnParticle(Particle.CRIT, defender.getEyeLocation(), 8, 0.2D, 0.18D, 0.2D, 0.02D);
  }

  private void executePhaseWalk(Player player, double distance, int tier) {
    if (player == null || !player.isOnline()) {
      return;
    }
    double boundedDistance = Math.max(2.0D, distance);
    if (tier <= 1) {
      teleportForwardSafely(player, boundedDistance, 4);
      return;
    }
    Location origin = player.getLocation().clone();
    Vector direction = origin.getDirection().normalize();
    int solidThickness = consecutiveSolidThicknessAlongRay(origin, direction, boundedDistance);
    if (solidThickness > 2) {
      teleportForwardSafely(player, boundedDistance, 4);
      return;
    }
    Location target = origin.clone().add(direction.multiply(boundedDistance));
    target.setYaw(origin.getYaw());
    target.setPitch(origin.getPitch());
    Location safe = findNearestSafeLocation(target, 4);
    if (safe != null) {
      player.teleport(safe, PlayerTeleportEvent.TeleportCause.PLUGIN);
      return;
    }
    teleportForwardSafely(player, boundedDistance, 4);
  }

  private int consecutiveSolidThicknessAlongRay(Location origin, Vector direction, double distance) {
    if (origin == null || direction == null || direction.lengthSquared() <= 0.0001D || distance <= 0.0D) {
      return 0;
    }
    if (origin.getWorld() == null) {
      return 0;
    }
    Vector dir = direction.clone().normalize();
    int consecutive = 0;
    int maxConsecutive = 0;
    int steps = Math.max(1, (int) Math.ceil(distance / 0.35D));
    for (int i = 1; i <= steps; i++) {
      double stepDistance = Math.min(distance, i * 0.35D);
      Location sample = origin.clone().add(dir.clone().multiply(stepDistance));
      Block feet = sample.getBlock();
      Block head = feet.getRelative(BlockFace.UP);
      boolean blocked = !feet.isPassable() || !head.isPassable();
      if (blocked) {
        consecutive++;
        maxConsecutive = Math.max(maxConsecutive, consecutive);
      } else {
        consecutive = 0;
      }
    }
    return maxConsecutive;
  }

  private List<LivingEntity> executeSwordWave(Player player, int tier) {
    List<LivingEntity> hits = new ArrayList<>();
    if (player == null) {
      return hits;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    Location eye = player.getEyeLocation();
    Vector direction = eye.getDirection().normalize();
    double range = switch (clampedTier) {
      case 1 -> 8.0D;
      case 2, 3 -> 10.0D;
      default -> 12.0D;
    };
    int pierceCap = switch (clampedTier) {
      case 1 -> 1;
      case 2 -> 2;
      case 3 -> 3;
      default -> 4;
    };
    double damage = switch (clampedTier) {
      case 1 -> 5.0D;
      case 2 -> 6.0D;
      case 3 -> 7.0D;
      default -> 8.5D;
    };
    int pierced = 0;
    for (Entity entity : player.getNearbyEntities(range + 2, 3.5D, range + 2)) {
      if (!(entity instanceof LivingEntity living) || entity == player) {
        continue;
      }
      Vector toTarget = entity.getLocation().toVector().subtract(eye.toVector());
      double distance = toTarget.length();
      if (distance <= 0.01D || distance > range) {
        continue;
      }
      double alignment = direction.dot(toTarget.normalize());
      if (alignment < 0.86D) {
        continue;
      }
      living.damage(damage, player);
      hits.add(living);
      pierced++;
      if (pierced >= pierceCap) {
        break;
      }
    }
    int particleCount = 12 + (clampedTier * 4);
    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, eye.clone().add(direction.multiply(1.5D)), particleCount, 0.25, 0.25, 0.25, 0.0);
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.75F, clampedTier >= 4 ? 0.8F : 1.0F);
    return hits;
  }

  private void consumeHybridSwordWaveCost(Player player, int tier) {
    if (player == null || tier <= 0) {
      return;
    }
    int foodCost = tier >= 4 ? 2 : 1;
    player.setFoodLevel(Math.max(0, player.getFoodLevel() - foodCost));
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    if (mainHand == null || mainHand.getType() == Material.AIR) {
      return;
    }
    ItemMeta meta = mainHand.getItemMeta();
    if (!(meta instanceof Damageable damageable)) {
      return;
    }
    int durabilityCost = tier >= 4 ? 2 : 1;
    damageable.setDamage(Math.max(0, damageable.getDamage() + durabilityCost));
    mainHand.setItemMeta(meta);
  }

  private void executeHybridRepulsePulse(Player player, int tier) {
    if (player == null) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    double radius = switch (clampedTier) {
      case 1 -> 6.0D;
      case 2, 3 -> 8.0D;
      default -> 10.0D;
    };
    double strength = switch (clampedTier) {
      case 1 -> 0.62D;
      case 2 -> 0.72D;
      case 3 -> 0.84D;
      default -> 0.98D;
    };
    int cap = 18 + (clampedTier * 3);
    int affected = 0;
    Location center = player.getLocation();
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof LivingEntity living) || entity == player) {
        continue;
      }
      Vector delta = living.getLocation().toVector().subtract(center.toVector());
      if (delta.lengthSquared() <= 0.0001D) {
        continue;
      }
      Vector push = delta.normalize().multiply(strength).setY(0.24D);
      living.setVelocity(living.getVelocity().add(push));
      if (clampedTier >= 2) {
        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 0, true, false, true));
      }
      affected++;
      if (affected >= cap) {
        break;
      }
    }
    if (clampedTier >= 3) {
      deflectNearbyProjectilesSlight(player, radius + 1.0D);
    }
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.55F + (clampedTier * 0.08F), 1.35F);
    player.getWorld().spawnParticle(
        Particle.CLOUD,
        player.getLocation().clone().add(0.0D, 1.0D, 0.0D),
        16 + (clampedTier * 6),
        0.6D,
        0.5D,
        0.6D,
        0.02D
    );
  }

  private void deflectNearbyProjectilesSlight(Player player, double radius) {
    if (player == null || radius <= 0.0D) {
      return;
    }
    int cap = 24;
    int changed = 0;
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Projectile projectile) || !projectile.isValid()) {
        continue;
      }
      Vector velocity = projectile.getVelocity();
      if (velocity == null || velocity.lengthSquared() <= 0.0001D) {
        continue;
      }
      Vector redirected = velocity.clone().multiply(-0.35D)
          .add(player.getLocation().getDirection().normalize().multiply(0.22D));
      if (redirected.lengthSquared() <= 0.0001D) {
        redirected = player.getLocation().getDirection().normalize().multiply(0.22D);
      }
      projectile.setVelocity(redirected);
      changed++;
      if (changed >= cap) {
        break;
      }
    }
  }

  private void applyBloodContractPeriodic(Player player, ActiveSeasonEffect effect, int tier, long nowEpochSecond) {
    if (player == null || effect == null || tier <= 0) {
      return;
    }
    UUID playerId = player.getUniqueId();
    if (!bloodContractLastKillEpochSecondByPlayer.containsKey(playerId)) {
      bloodContractLastKillEpochSecondByPlayer.put(playerId, nowEpochSecond);
      return;
    }

    long lastKillAt = bloodContractLastKillEpochSecondByPlayer.getOrDefault(playerId, nowEpochSecond);
    if (tier >= 4
        && bloodContractKillStreakByPlayer.getOrDefault(playerId, 0) > 0
        && (nowEpochSecond - lastKillAt) > 18L) {
      bloodContractKillStreakByPlayer.remove(playerId);
    }

    if (tier < 2 || (nowEpochSecond - lastKillAt) < 30L) {
      return;
    }

    long penaltyInterval = tier >= 4 ? 20L : 30L;
    if (!useEffectCooldown(playerId, effect.getId(), "blood_contract_drought", penaltyInterval)) {
      return;
    }
    if (tier >= 4) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1, true, false, true));
      player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 0, true, false, true));
      player.damage(1.0D, player);
      player.sendActionBar(ChatColor.RED + "피의 계약: 무처치 페널티 강화");
      return;
    }
    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
    player.sendActionBar(ChatColor.RED + "피의 계약: 무처치 약화");
  }

  private void applyFrenzyEngineCombatState(
      Player player,
      ActiveSeasonEffect effect,
      int tier,
      long nowEpochSecond
  ) {
    if (player == null || effect == null || tier <= 0) {
      return;
    }
    UUID playerId = player.getUniqueId();
    boolean inCombat = isPlayerInRecentCombat(playerId, 8L);
    boolean wasInCombat = frenzyCombatActiveByPlayer.getOrDefault(playerId, false);
    if (inCombat) {
      frenzyCombatActiveByPlayer.put(playerId, true);
      frenzyFatigueUntilEpochSecondByPlayer.remove(playerId);
      long frenzyDuration = switch (tier) {
        case 1 -> 8L;
        case 2 -> 9L;
        case 3 -> 10L;
        default -> 12L;
      };
      long nextUntil = Math.max(frenzyUntilEpochSecondByPlayer.getOrDefault(playerId, 0L), nowEpochSecond + frenzyDuration);
      frenzyUntilEpochSecondByPlayer.put(playerId, nextUntil);
      return;
    }
    if (!wasInCombat) {
      return;
    }

    frenzyCombatActiveByPlayer.remove(playerId);
    frenzyUntilEpochSecondByPlayer.remove(playerId);
    long fatigueDuration = switch (tier) {
      case 1 -> 4L;
      case 2 -> 5L;
      case 3 -> 6L;
      default -> 7L;
    };
    int stacks = Math.max(0, frenzyFatigueStacksByPlayer.getOrDefault(playerId, 0));
    if (tier >= 2) {
      stacks = Math.min(4, stacks + 1);
    } else {
      stacks = Math.max(1, stacks);
    }
    frenzyFatigueStacksByPlayer.put(playerId, stacks);
    long fatigueUntil = nowEpochSecond + fatigueDuration + Math.max(0, (tier >= 2 ? stacks - 1 : 0));
    frenzyFatigueUntilEpochSecondByPlayer.put(playerId, fatigueUntil);
    if (tier >= 4) {
      frenzyHealLockUntilEpochSecondByPlayer.put(playerId, nowEpochSecond + 5L);
    }
  }

  private void applyScoreShieldDamageResponse(
      EntityDamageEvent event,
      Player victim,
      PlayerRoundData victimData,
      ActiveSeasonEffect effect,
      int tier,
      long nowEpochSecond
  ) {
    if (event == null || victim == null || victimData == null || effect == null || tier <= 0) {
      return;
    }
    UUID victimId = victim.getUniqueId();
    double heavyThreshold = tier >= 2 ? 3.5D : 4.0D;
    if (event.getFinalDamage() >= heavyThreshold) {
      double reductionRatio = tier <= 1 ? 0.20D : 0.30D;
      double prevented = Math.max(0.0D, event.getDamage() * reductionRatio);
      long scoreCost = Math.max(40L, Math.round(prevented * (tier >= 2 ? 90.0D : 80.0D)));
      long available = Math.max(0L, victimData.getScore());
      if (available >= scoreCost) {
        adjustPlayerScore(victimId, victimData, -scoreCost, false);
        event.setDamage(Math.max(0.2D, event.getDamage() - prevented));
      } else if (tier >= 2) {
        double backfireMultiplier = tier >= 4 ? 1.20D : 1.12D;
        event.setDamage(event.getDamage() * backfireMultiplier);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, tier >= 4 ? 100 : 60, 0, true, false, true));
        if (tier >= 4) {
          victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 0, true, false, true));
        }
      }
    }

    if (tier >= 4
        && event.getFinalDamage() >= victim.getHealth()
        && useEffectCooldown(victimId, effect.getId(), "score_shield_fatal_save", 180L)) {
      event.setCancelled(true);
      victim.setHealth(Math.max(2.0D, victim.getHealth()));
      temporaryInvulnerableUntilEpochSecondByPlayer.put(victimId, nowEpochSecond + 1L);
      victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0, true, false, true));
      victim.sendActionBar(ChatColor.GOLD + "점수 방패: 치명타 1회 상쇄");
    }
  }

  private LivingEntity findSwapStrikeTarget(Player player, double range) {
    if (player == null || range <= 0.0D) {
      return null;
    }
    Location eye = player.getEyeLocation();
    Vector facing = eye.getDirection().normalize();
    double bestDistanceSquared = (range * range) + 0.01D;
    LivingEntity best = null;
    for (Entity entity : player.getNearbyEntities(range, range * 0.75D, range)) {
      if (!(entity instanceof LivingEntity living) || living == player) {
        continue;
      }
      Vector rel = living.getLocation().toVector().subtract(eye.toVector());
      double distanceSquared = rel.lengthSquared();
      if (distanceSquared <= 0.01D || distanceSquared >= bestDistanceSquared) {
        continue;
      }
      if (facing.dot(rel.normalize()) < 0.65D) {
        continue;
      }
      if (!player.hasLineOfSight(living)) {
        continue;
      }
      best = living;
      bestDistanceSquared = distanceSquared;
    }
    return best;
  }

  private boolean performSwapStrike(Player attacker, LivingEntity target, int tier, boolean activeCast) {
    if (attacker == null || target == null || !attacker.isOnline() || !target.isValid()) {
      return false;
    }
    Location attackerFrom = attacker.getLocation().clone();
    Location targetFrom = target.getLocation().clone();
    Location safeAttackerDest = findNearestSafeLocation(targetFrom, 6);
    if (safeAttackerDest == null) {
      safeAttackerDest = targetFrom;
    }
    attacker.teleport(safeAttackerDest, PlayerTeleportEvent.TeleportCause.PLUGIN);

    Location targetDest = attackerFrom;
    if (target instanceof Player targetPlayer) {
      Location safeTargetDest = findNearestSafeLocation(attackerFrom, 6);
      if (safeTargetDest != null) {
        targetDest = safeTargetDest;
      }
      targetPlayer.teleport(targetDest, PlayerTeleportEvent.TeleportCause.PLUGIN);
    } else {
      target.teleport(targetDest, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    if (tier >= 2) {
      attacker.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 0, true, false, true));
      target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 0, true, false, true));
    }
    if (tier >= 2) {
      target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
      attacker.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 0, true, true, true));
    }
    if (activeCast) {
      attacker.getWorld().spawnParticle(Particle.PORTAL, attacker.getLocation(), 24, 0.4D, 0.7D, 0.4D, 0.02D);
    }
    attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8F, 1.08F);
    return true;
  }

  private void provokeNearbyMonsters(Player player, int tier) {
    if (player == null) {
      return;
    }
    double radius = tier >= 4 ? 18.0D : 14.0D;
    int cap = tier >= 4 ? 6 : 4;
    int setTargetCount = 0;
    UUID playerId = player.getUniqueId();
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Mob mob) || !(entity instanceof Monster)) {
        continue;
      }
      LivingEntity currentTarget = mob.getTarget();
      if (currentTarget != null
          && currentTarget.getUniqueId().equals(playerId)
          && ThreadLocalRandom.current().nextDouble() < 0.65D) {
        continue;
      }
      mob.setTarget(player);
      setTargetCount++;
      if (setTargetCount >= cap) {
        break;
      }
    }
  }

  private void weakenNearbyHostiles(Player player, int tier, double radius) {
    if (player == null || radius <= 0.0D) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    int cap = clampedTier >= 4 ? 12 : 8;
    int affected = 0;
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Monster monster)) {
        continue;
      }
      monster.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, clampedTier >= 4 ? 1 : 0, true, false, true));
      monster.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0, true, false, true));
      if (clampedTier >= 4) {
        monster.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, true, true));
      }
      affected++;
      if (affected >= cap) {
        break;
      }
    }
  }

  private void reinforceFortressCluster(Block center, long untilEpochSecond) {
    if (center == null || untilEpochSecond <= 0L) {
      return;
    }
    reinforcedBurstBlocksUntilEpochSecond.put(blockKey(center.getLocation()), untilEpochSecond);
    for (BlockFace face : List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP)) {
      Block adjacent = center.getRelative(face);
      if (adjacent == null || adjacent.getType() == Material.AIR) {
        continue;
      }
      reinforcedBurstBlocksUntilEpochSecond.put(blockKey(adjacent.getLocation()), untilEpochSecond);
    }
  }

  private void completeGhostFormPenalty(Player player, int tier) {
    if (player == null || tier <= 0) {
      return;
    }
    if (tier >= 2) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
    }
    if (tier >= 4) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, true, true));
      player.sendActionBar(ChatColor.RED + "유령화 종료: 위치 노출");
    }
  }

  private void consumeExtraCraftingIngredients(CraftingInventory inventory, int extraSets) {
    if (inventory == null || extraSets <= 0) {
      return;
    }
    int sets = Math.max(1, extraSets);
    for (int set = 0; set < sets; set++) {
      ItemStack[] matrix = inventory.getMatrix();
      if (matrix == null || matrix.length == 0) {
        return;
      }
      boolean changed = false;
      for (int i = 0; i < matrix.length; i++) {
        ItemStack stack = matrix[i];
        if (stack == null || stack.getType() == Material.AIR) {
          continue;
        }
        int nextAmount = stack.getAmount() - 1;
        if (nextAmount <= 0) {
          matrix[i] = null;
        } else {
          ItemStack reduced = stack.clone();
          reduced.setAmount(nextAmount);
          matrix[i] = reduced;
        }
        changed = true;
      }
      if (!changed) {
        return;
      }
      inventory.setMatrix(matrix);
    }
  }

  private void refundCraftingIngredientsOnce(Player player, ItemStack[] matrix) {
    if (player == null || matrix == null || matrix.length == 0) {
      return;
    }
    List<ItemStack> refunds = new ArrayList<>();
    for (ItemStack ingredient : matrix) {
      if (ingredient == null || ingredient.getType() == Material.AIR || ingredient.getAmount() <= 0) {
        continue;
      }
      ItemStack one = ingredient.clone();
      one.setAmount(1);
      refunds.add(one);
    }
    if (refunds.isEmpty()) {
      return;
    }
    Bukkit.getScheduler().runTask(this, () -> {
      if (!player.isOnline()) {
        return;
      }
      Location dropLocation = player.getLocation().clone().add(0.0D, 0.8D, 0.0D);
      PlayerInventory inventory = player.getInventory();
      for (ItemStack refund : refunds) {
        Map<Integer, ItemStack> overflow = inventory.addItem(refund);
        if (overflow == null || overflow.isEmpty()) {
          continue;
        }
        for (ItemStack overflowStack : overflow.values()) {
          if (overflowStack == null || overflowStack.getType() == Material.AIR || overflowStack.getAmount() <= 0) {
            continue;
          }
          player.getWorld().dropItemNaturally(dropLocation, overflowStack);
        }
      }
      player.updateInventory();
    });
  }

  private void flareReveal(Player player, int tier) {
    if (player == null) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    int exposureSeconds = switch (clampedTier) {
      case 1 -> 6;
      case 2 -> 8;
      case 3 -> 10;
      default -> 15;
    };
    int exposureTicks = exposureSeconds * 20;
    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, exposureTicks, 0, true, true, true));

    Player target = pickFlareRevealTarget(player, clampedTier);
    if (target == null) {
      player.sendActionBar(ChatColor.GRAY + "신호탄: 추적 대상 없음");
      player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 18, 0.45D, 0.85D, 0.45D, 0.02D);
      return;
    }

    boolean sameWorld = target.getWorld() == player.getWorld();
    double distance = sameWorld ? player.getLocation().distance(target.getLocation()) : -1.0D;
    if (sameWorld) {
      Location from = player.getEyeLocation();
      Location to = target.getLocation().clone().add(0.0D, 1.0D, 0.0D);
      Vector delta = to.toVector().subtract(from.toVector());
      double length = Math.max(0.1D, delta.length());
      Vector step = delta.normalize().multiply(0.7D);
      Location cursor = from.clone();
      int points = Math.max(10, Math.min(30, (int) Math.round(length / 0.7D)));
      for (int i = 0; i < points; i++) {
        player.getWorld().spawnParticle(Particle.END_ROD, cursor, 1, 0.02D, 0.02D, 0.02D, 0.0D);
        cursor.add(step);
      }
      target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Math.max(exposureTicks, clampedTier >= 4 ? 300 : exposureTicks), 0, true, true, true));
    }

    String targetInfo = sameWorld
        ? (target.getName() + " / " + String.format(Locale.ROOT, "%.1fm", distance))
        : (target.getName() + " / " + target.getWorld().getEnvironment().name());
    player.sendActionBar(ChatColor.GOLD + "신호탄 고정: " + targetInfo);
    player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 40, 0.8D, 1.2D, 0.8D, 0.02D);
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.55F, 1.05F);
  }

  private Player pickFlareRevealTarget(Player caster, int tier) {
    if (caster == null) {
      return null;
    }
    List<Player> candidates = new ArrayList<>();
    for (Player online : Bukkit.getOnlinePlayers()) {
      if (online == null || !online.isOnline() || online.isDead()) {
        continue;
      }
      if (online.getUniqueId().equals(caster.getUniqueId())) {
        continue;
      }
      if (online.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }
      PlayerRoundData data = players.get(online.getUniqueId());
      if (data != null && data.isOut()) {
        continue;
      }
      if (tier < 2 && online.getWorld() != caster.getWorld()) {
        continue;
      }
      candidates.add(online);
    }
    if (candidates.isEmpty()) {
      return null;
    }

    Player best = null;
    double bestDistance = Double.MAX_VALUE;
    for (Player candidate : candidates) {
      double distance;
      if (candidate.getWorld() == caster.getWorld()) {
        distance = candidate.getLocation().distanceSquared(caster.getLocation());
      } else {
        distance = 1_000_000.0D + ThreadLocalRandom.current().nextDouble(0.0D, 1000.0D);
      }
      if (distance < bestDistance) {
        bestDistance = distance;
        best = candidate;
      }
    }
    if (best == null) {
      return null;
    }

    double lockChance = switch (Math.max(1, Math.min(4, tier))) {
      case 1 -> 0.80D;
      case 2 -> 0.92D;
      default -> 1.00D;
    };
    if (ThreadLocalRandom.current().nextDouble() > lockChance) {
      return null;
    }
    return best;
  }

  private void randomizePlayerYaw(Player player, float maxDegrees) {
    if (player == null) {
      return;
    }
    float delta = (float) ThreadLocalRandom.current().nextDouble(-maxDegrees, maxDegrees);
    Location location = player.getLocation();
    location.setYaw(location.getYaw() + delta);
    c9InternalTeleportGuardUntilEpochMilliByPlayer.put(player.getUniqueId(), System.currentTimeMillis() + 500L);
    player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
  }

  private void pullEntitiesInFront(Player player, double range, double pullStrength, int cap) {
    pullEntitiesInFrontDetailed(player, range, pullStrength, cap);
  }

  private List<LivingEntity> pullEntitiesInFrontDetailed(Player player, double range, double pullStrength, int cap) {
    List<LivingEntity> affected = new ArrayList<>();
    if (player == null) {
      return affected;
    }
    Location eye = player.getEyeLocation();
    Vector facing = eye.getDirection().normalize();
    int affectedCount = 0;
    for (Entity entity : player.getNearbyEntities(range + 1.5D, 2.5D, range + 1.5D)) {
      if (!(entity instanceof LivingEntity living) || entity == player) {
        continue;
      }
      Vector rel = living.getLocation().toVector().subtract(eye.toVector());
      double distance = rel.length();
      if (distance <= 0.01D || distance > range) {
        continue;
      }
      double align = facing.dot(rel.normalize());
      if (align < 0.65D) {
        continue;
      }
      Vector pull = player.getLocation().toVector().subtract(living.getLocation().toVector()).normalize().multiply(Math.max(0.2D, pullStrength / 6.0D));
      pull.setY(Math.max(0.08D, pull.getY()));
      living.setVelocity(living.getVelocity().add(pull));
      affected.add(living);
      affectedCount++;
      if (affectedCount >= cap) {
        break;
      }
    }
    return affected;
  }

  private List<LivingEntity> pullEntitiesInLineZone(
      Player player,
      double range,
      double halfWidth,
      double pullDistance,
      int cap
  ) {
    List<LivingEntity> affected = new ArrayList<>();
    if (player == null || range <= 0.0D || halfWidth <= 0.0D || cap <= 0) {
      return affected;
    }
    Location eye = player.getEyeLocation();
    Vector facing = eye.getDirection();
    if (facing.lengthSquared() <= 0.0001D) {
      return affected;
    }
    facing.normalize();
    double pullStrength = Math.max(0.2D, pullDistance / 8.0D);
    for (Entity entity : player.getNearbyEntities(range + halfWidth + 1.0D, 4.0D, range + halfWidth + 1.0D)) {
      if (!(entity instanceof LivingEntity living) || entity == player) {
        continue;
      }
      Vector rel = living.getLocation().toVector().subtract(eye.toVector());
      double forward = rel.dot(facing);
      if (forward <= 0.0D || forward > range) {
        continue;
      }
      Vector lateral = rel.clone().subtract(facing.clone().multiply(forward));
      if (lateral.length() > halfWidth) {
        continue;
      }
      Vector pull = player.getLocation().toVector().subtract(living.getLocation().toVector());
      if (pull.lengthSquared() <= 0.0001D) {
        continue;
      }
      living.setVelocity(living.getVelocity().add(pull.normalize().multiply(pullStrength).setY(0.12D)));
      affected.add(living);
      if (affected.size() >= cap) {
        break;
      }
    }
    return affected;
  }

  private void applyLinePullDot(Player attacker, LivingEntity target, double damage, int ticks) {
    if (attacker == null || target == null || damage <= 0.0D || ticks <= 0) {
      return;
    }
    final UUID attackerId = attacker.getUniqueId();
    final UUID targetId = target.getUniqueId();
    for (int i = 1; i <= ticks; i++) {
      long delay = i * 10L;
      Bukkit.getScheduler().runTaskLater(this, () -> {
        Player source = Bukkit.getPlayer(attackerId);
        Entity entity = Bukkit.getEntity(targetId);
        if (!(entity instanceof LivingEntity living) || !living.isValid() || living.isDead()) {
          return;
        }
        if (source != null && source.isOnline() && !source.isDead()) {
          living.damage(damage, source);
        } else {
          living.damage(damage);
        }
      }, delay);
    }
  }

  private void pulseKnockback(Player player, double radius, double strength, boolean playersOnly) {
    if (player == null) {
      return;
    }
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof LivingEntity living) || entity == player) {
        continue;
      }
      if (playersOnly && !(living instanceof Player)) {
        continue;
      }
      Vector delta = living.getLocation().toVector().subtract(player.getLocation().toVector());
      if (delta.lengthSquared() <= 0.001D) {
        continue;
      }
      Vector velocity = delta.normalize().multiply(strength).setY(0.22D);
      living.setVelocity(living.getVelocity().add(velocity));
    }
  }

  private void pullNearbyItems(Player player, double radius, double speedMultiplier) {
    if (player == null) {
      return;
    }
    Location center = player.getLocation().clone().add(0.0D, 0.9D, 0.0D);
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Item item) || !item.isValid()) {
        continue;
      }
      Vector toPlayer = center.toVector().subtract(item.getLocation().toVector());
      if (toPlayer.lengthSquared() <= 0.0001D) {
        continue;
      }
      item.setVelocity(item.getVelocity().multiply(0.35D).add(toPlayer.normalize().multiply(Math.max(0.2D, speedMultiplier / 12.0D))));
    }
  }

  private void collectNearbyItemDrops(Player player, double radius) {
    if (player == null || radius <= 0.0D) {
      return;
    }
    Location center = player.getLocation().clone().add(0.0D, 0.8D, 0.0D);
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Item item) || !item.isValid()) {
        continue;
      }
      ItemStack stack = item.getItemStack();
      if (stack == null || stack.getType() == Material.AIR) {
        continue;
      }
      item.teleport(center, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
  }

  private void handleElementShiftFireWater(Player player, int tier, boolean fireHealWaterHurt) {
    if (player == null) {
      return;
    }
    boolean inWater = player.isInWater() || player.isInRain() || player.getEyeLocation().getBlock().isLiquid();
    boolean onFire = player.getFireTicks() > 0 || player.getLocation().getBlock().getType() == Material.LAVA;
    if (fireHealWaterHurt) {
      if (onFire) {
        double heal = switch (tier) {
          case 1 -> 0.6D;
          case 2 -> 1.0D;
          case 3 -> 1.1D;
          default -> 1.4D;
        };
        healPlayer(player, heal);
        if (tier >= 2) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, tier >= 4 ? 80 : 60, tier >= 4 ? 1 : 0, true, false, true));
        }
      }
      if (inWater) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, tier >= 2 ? 60 : 40, 0, true, false, true));
        if (tier >= 2) {
          player.damage(0.7D + (tier * 0.45D), player);
        }
        if (tier >= 2) {
          cursedSprintLockUntilEpochSecondByPlayer.put(player.getUniqueId(), nowEpochSecond() + 3L);
          player.setSprinting(false);
        }
        if (tier >= 4) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0, true, false, true));
        }
      }
    } else {
      if (inWater) {
        double heal = switch (tier) {
          case 1 -> 0.6D;
          case 2 -> 1.0D;
          case 3 -> 1.3D;
          default -> 1.7D;
        };
        healPlayer(player, heal);
        if (tier >= 2) {
          healPlayer(player, 0.4D);
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 0, true, false, true));
        }
        if (tier >= 4) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 80, 1, true, false, true));
        }
      }
      if (onFire) {
        player.damage(0.7D + (tier * 0.45D), player);
        if (tier >= 2) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
        }
        if (tier >= 4) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 80, 0, true, false, true));
        }
      }
    }
  }

  private void healPlayer(Player player, double amount) {
    if (player == null || amount <= 0.0D) {
      return;
    }
    double maxHealth = player.getAttribute(Attribute.MAX_HEALTH) == null
        ? player.getMaxHealth()
        : player.getAttribute(Attribute.MAX_HEALTH).getValue();
    player.setHealth(Math.min(maxHealth, Math.max(1.0D, player.getHealth() + amount)));
  }

  private void applyHungerExchangeStarvationPenalty(Player player, int tier) {
    if (player == null || tier <= 0) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
    if (clampedTier >= 2) {
      int witherAmp = clampedTier >= 4 ? 1 : 0;
      player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, witherAmp, true, false, true));
    }
  }

  private void processScoreInvestmentCycle(Player player, PlayerRoundData data, int tier, long nowEpochSecond) {
    if (player == null || data == null || tier <= 0) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    long currentScore = Math.max(0L, data.getScore());
    if (currentScore <= 0L) {
      return;
    }

    double lockRatio = switch (clampedTier) {
      case 1 -> 0.05D;
      case 2 -> 0.08D;
      case 3 -> 0.10D;
      default -> 0.12D;
    };
    long principal = Math.max(1L, (long) Math.floor(currentScore * lockRatio));
    principal = Math.min(principal, currentScore);
    if (principal <= 0L) {
      return;
    }

    adjustPlayerScore(player.getUniqueId(), data, -principal, false);
    long maturityEpochSecond = nowEpochSecond + 300L;
    scoreInvestmentLedgerByPlayer.computeIfAbsent(player.getUniqueId(), ignored -> new ArrayList<>())
        .add(new ScoreInvestmentLedgerEntry(principal, clampedTier, maturityEpochSecond));

    long expected = Math.max(0L, Math.round(principal * scoreInvestmentReturnMultiplier(clampedTier)));
    player.sendActionBar(ChatColor.AQUA + "점수 투자: -" + principal + " (5m 후 예상 +" + expected + ")");
  }

  private void settleScoreInvestmentLedger(Player player, PlayerRoundData data, long nowEpochSecond) {
    if (player == null || data == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    List<ScoreInvestmentLedgerEntry> ledger = scoreInvestmentLedgerByPlayer.get(playerId);
    if (ledger == null || ledger.isEmpty()) {
      return;
    }

    List<ScoreInvestmentLedgerEntry> matured = new ArrayList<>();
    for (ScoreInvestmentLedgerEntry entry : ledger) {
      if (entry == null) {
        continue;
      }
      if (entry.maturityEpochSecond() <= nowEpochSecond) {
        matured.add(entry);
      }
    }
    if (matured.isEmpty()) {
      return;
    }

    long totalPayout = 0L;
    boolean hadBust = false;
    int highestMaturedTier = 0;
    for (ScoreInvestmentLedgerEntry entry : matured) {
      int entryTier = Math.max(1, Math.min(4, entry.tier()));
      highestMaturedTier = Math.max(highestMaturedTier, entryTier);
      long principal = Math.max(0L, entry.principal());
      if (principal <= 0L) {
        continue;
      }
      long payout = Math.max(0L, Math.round(principal * scoreInvestmentReturnMultiplier(entryTier)));
      if (entryTier >= 4 && ThreadLocalRandom.current().nextDouble() < 0.20D) {
        payout = 0L;
        hadBust = true;
      }
      totalPayout += payout;
    }

    ledger.removeAll(matured);
    if (ledger.isEmpty()) {
      scoreInvestmentLedgerByPlayer.remove(playerId);
    }

    if (totalPayout > 0L) {
      addGeneratedScore(playerId, totalPayout);
    }
    if (highestMaturedTier >= 3) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 120, 0, true, true, true));
    }

    if (totalPayout > 0L) {
      player.sendActionBar(ChatColor.GOLD + "점수 투자 만기: +" + totalPayout);
    } else if (hadBust) {
      player.sendActionBar(ChatColor.RED + "점수 투자 만기: 0 반환");
    }
  }

  private double scoreInvestmentReturnMultiplier(int tier) {
    return switch (Math.max(1, Math.min(4, tier))) {
      case 1 -> 1.40D;   // 5% -> 7%
      case 2 -> 1.375D;  // 8% -> 11%
      case 3 -> 1.40D;   // 10% -> 14%
      default -> 1.50D;  // 12% -> 18%
    };
  }

  private void spawnHostileNearPlayer(Player player) {
    spawnHostileNearPlayer(player, 1);
  }

  private void spawnHostileNearPlayer(Player player, int tier) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    World world = player.getWorld();
    double angle = ThreadLocalRandom.current().nextDouble(0.0D, Math.PI * 2.0D);
    double minDistance = clampedTier >= 3 ? 5.0D : 6.0D;
    double maxDistance = clampedTier >= 3 ? 9.5D : 11.0D;
    double distance = ThreadLocalRandom.current().nextDouble(minDistance, maxDistance);
    Location spawn = player.getLocation().clone().add(Math.cos(angle) * distance, 0.0D, Math.sin(angle) * distance);
    Location safe = findNearestSafeLocation(spawn, 6);
    if (safe == null) {
      return;
    }
    EntityType spawnType;
    if (world.getEnvironment() == World.Environment.THE_END) {
      if (clampedTier >= 3 && ThreadLocalRandom.current().nextDouble() < 0.35D) {
        spawnType = ThreadLocalRandom.current().nextBoolean() ? EntityType.SHULKER : EntityType.ENDERMITE;
      } else {
        spawnType = EntityType.ENDERMAN;
      }
    } else if (world.getEnvironment() == World.Environment.NETHER) {
      if (clampedTier >= 3 && ThreadLocalRandom.current().nextDouble() < 0.45D) {
        spawnType = switch (ThreadLocalRandom.current().nextInt(3)) {
          case 0 -> EntityType.WITHER_SKELETON;
          case 1 -> EntityType.HOGLIN;
          default -> EntityType.MAGMA_CUBE;
        };
      } else {
        spawnType = EntityType.ZOMBIFIED_PIGLIN;
      }
    } else if (clampedTier >= 3 && ThreadLocalRandom.current().nextDouble() < 0.45D) {
      spawnType = switch (ThreadLocalRandom.current().nextInt(4)) {
        case 0 -> EntityType.SKELETON;
        case 1 -> EntityType.SPIDER;
        case 2 -> EntityType.HUSK;
        default -> EntityType.CREEPER;
      };
    } else {
      spawnType = EntityType.ZOMBIE;
    }
    Entity spawned = world.spawnEntity(safe, spawnType);
    if (spawned instanceof Monster monster) {
      if (clampedTier >= 2) {
        monster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0, true, false, true));
      }
      if (clampedTier >= 3) {
        monster.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 0, true, false, true));
      }
      if (clampedTier >= 4) {
        monster.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 0, true, false, true));
      }
    }
  }

  private void applySpiritExchangeBuff(Player player, int tier) {
    if (player == null) {
      return;
    }
    World world = player.getWorld();
    if (world == null) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    UUID playerId = player.getUniqueId();
    String state = resolveSpiritExchangeState(player);
    String previousState = spiritExchangeStateByPlayer.put(playerId, state);
    boolean transitioned = previousState != null && !previousState.equals(state);

    if (transitioned) {
      if (clampedTier >= 3) {
        pulseKnockback(player, 4.0D + clampedTier, 0.35D + (clampedTier * 0.05D), false);
      }
      player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().clone().add(0.0D, 1.0D, 0.0D), 22, 0.45D, 0.7D, 0.45D, 0.02D);
      player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.45F, 1.15F);
    }

    switch (state) {
      case "END" -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, Math.max(0, clampedTier - 1), true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
        if (clampedTier >= 2) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 0, true, false, true));
        }
      }
      case "NETHER" -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, Math.max(0, clampedTier - 2), true, false, true));
        if (clampedTier >= 2) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, 0, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
        }
      }
      case "STORM" -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 0, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 0, true, false, true));
        if (clampedTier >= 2) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60, 0, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
        }
      }
      case "NIGHT" -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60, 0, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
        if (clampedTier >= 2) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 0, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 0, true, false, true));
        }
      }
      default -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, Math.max(0, clampedTier - 1), true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
        if (clampedTier >= 2) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 60, 0, true, false, true));
          player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 0, true, false, true));
        }
      }
    }

    if (clampedTier >= 4) {
      spiritExchangeScoreMultiplierByPlayer.put(playerId, spiritExchangeScoreMultiplier(state));
    } else {
      spiritExchangeScoreMultiplierByPlayer.remove(playerId);
    }
  }

  private void handleSpiritExchangeTransitionOnWorldChange(Player player) {
    if (player == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    PlayerRoundData data = players.get(playerId);
    if (data == null || data.isOut()) {
      spiritExchangeStateByPlayer.remove(playerId);
      spiritExchangeScoreMultiplierByPlayer.remove(playerId);
      return;
    }
    int tier = highestEffect80Tier(data, 'X', 40);
    if (tier <= 0) {
      spiritExchangeStateByPlayer.remove(playerId);
      spiritExchangeScoreMultiplierByPlayer.remove(playerId);
      return;
    }
    spiritExchangeStateByPlayer.remove(playerId);
    applySpiritExchangeBuff(player, tier);
  }

  private String resolveSpiritExchangeState(Player player) {
    if (player == null || player.getWorld() == null) {
      return "DAY";
    }
    World world = player.getWorld();
    if (world.getEnvironment() == World.Environment.THE_END) {
      return "END";
    }
    if (world.getEnvironment() == World.Environment.NETHER) {
      return "NETHER";
    }
    if (world.hasStorm() || world.isThundering()) {
      return "STORM";
    }
    long time = world.getTime();
    if (time >= 13000L && time <= 23000L) {
      return "NIGHT";
    }
    return "DAY";
  }

  private double spiritExchangeScoreMultiplier(String state) {
    if (state == null || state.isBlank()) {
      return 1.0D;
    }
    return switch (state) {
      case "END" -> 1.25D;
      case "NETHER" -> 1.18D;
      case "DAY" -> 1.12D;
      case "NIGHT" -> 0.88D;
      case "STORM" -> 0.78D;
      default -> 1.0D;
    };
  }

  private void applyAuraResonancePeriodic(
      Player player,
      PlayerRoundData data,
      ActiveSeasonEffect effect,
      int tier,
      long nowEpochSecond
  ) {
    if (player == null || data == null || effect == null || tier <= 0) {
      return;
    }
    double auraRadius = enderAuraRadius() + (tier >= 2 ? (3.0D + tier) : 0.0D);
    boolean inAura = isInsideEnderAura(player, auraRadius);
    if (!inAura) {
      return;
    }
    if (useEffectCooldown(player.getUniqueId(), effect.getId(), "aura_resonance_score", Math.max(4, 10 - tier))) {
      addGeneratedScore(player.getUniqueId(), 4L * tier);
    }
    if (tier >= 2) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 0, true, false, true));
    }
    if (tier >= 4
        && player.getHealth() <= Math.max(2.0D, player.getMaxHealth() * 0.40D)
        && useEffectCooldown(player.getUniqueId(), effect.getId(), "aura_resonance_invert", 240L)) {
      healPlayer(player, 4.0D);
      clearSingleNegativeEffect(player);
      player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, true, false, true));
      auraResonanceBacklashUntilEpochSecondByPlayer.put(player.getUniqueId(), nowEpochSecond + 120L);
      player.sendActionBar(ChatColor.DARK_PURPLE + "오라 공명 반전 발동");
    }
    if (auraResonanceBacklashUntilEpochSecondByPlayer.getOrDefault(player.getUniqueId(), 0L) > nowEpochSecond) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false, true));
    }
  }

  private void distortNearbyProjectiles(Player player, String effectId, int tier) {
    if (player == null || tier <= 0) {
      return;
    }
    UUID playerId = player.getUniqueId();
    long now = nowEpochSecond();
    long alignUntil = projectileAlignUntilEpochSecondByPlayer.getOrDefault(playerId, 0L);
    long reflectUntil = projectileReflectUntilEpochSecondByPlayer.getOrDefault(playerId, 0L);

    if (projectileReflectPendingPlayers.contains(playerId) && reflectUntil <= now) {
      projectileReflectPendingPlayers.remove(playerId);
      player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 120, 0, true, true, true));
      player.sendActionBar(ChatColor.RED + "탄도 반사 실패: 위치 노출");
    }

    double radius = 3.5D + (tier * 1.5D);
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Projectile projectile) || !projectile.isValid()) {
        continue;
      }
      Vector velocity = projectile.getVelocity();
      if (velocity == null || velocity.lengthSquared() <= 0.0001D) {
        continue;
      }
      Object shooter = projectile.getShooter();
      boolean ownShot = shooter instanceof Player shooterPlayer && shooterPlayer.getUniqueId().equals(playerId);

      if (tier >= 4 && reflectUntil > now && !ownShot) {
        Vector toPlayer = player.getEyeLocation().toVector().subtract(projectile.getLocation().toVector());
        if (toPlayer.lengthSquared() > 0.01D && velocity.normalize().dot(toPlayer.normalize()) > 0.35D) {
          reflectProjectileToAttacker(projectile, player);
          projectileReflectPendingPlayers.remove(playerId);
          continue;
        }
      }
      if (tier >= 2 && alignUntil > now && ownShot) {
        double speed = Math.max(0.6D, velocity.length());
        Vector aligned = player.getEyeLocation().getDirection().normalize().multiply(speed * (1.04D + (tier * 0.03D)));
        projectile.setVelocity(aligned);
        continue;
      }

      double jitterChance = 0.14D + (tier * 0.05D);
      if (ThreadLocalRandom.current().nextDouble() >= jitterChance) {
        continue;
      }
      double speed = Math.max(0.35D, velocity.length());
      double yawJitter = Math.toRadians(ThreadLocalRandom.current().nextDouble(-(5.0D + (tier * 2.0D)), 5.0D + (tier * 2.0D)));
      double pitchJitter = Math.toRadians(ThreadLocalRandom.current().nextDouble(-3.0D, 3.0D));
      Vector jittered = velocity.normalize().rotateAroundY(yawJitter).rotateAroundX(pitchJitter).multiply(speed);
      projectile.setVelocity(jittered);
    }
  }

  private void redirectNearbyProjectilesTowardDefender(Player defender, double radius) {
    if (defender == null || radius <= 0.0D) {
      return;
    }
    Location center = defender.getEyeLocation();
    int changed = 0;
    int cap = 32;
    for (Entity entity : defender.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Projectile projectile) || !projectile.isValid()) {
        continue;
      }
      Object shooter = projectile.getShooter();
      if (shooter instanceof Player shooterPlayer && shooterPlayer.getUniqueId().equals(defender.getUniqueId())) {
        continue;
      }
      Vector toShield = center.toVector().subtract(projectile.getLocation().toVector());
      if (toShield.lengthSquared() <= 0.0001D) {
        continue;
      }
      double speed = Math.max(0.45D, projectile.getVelocity() == null ? 0.6D : projectile.getVelocity().length());
      projectile.setVelocity(toShield.normalize().multiply(speed));
      changed++;
      if (changed >= cap) {
        break;
      }
    }
  }

  private void reflectProjectileToAttacker(Projectile projectile, Player defender) {
    if (projectile == null || defender == null) {
      return;
    }
    Vector incoming = projectile.getVelocity();
    if (incoming == null || incoming.lengthSquared() <= 0.0001D) {
      incoming = defender.getEyeLocation().getDirection().normalize();
    }
    Vector rebound = incoming.clone().multiply(-1.0D).normalize().multiply(Math.max(0.7D, incoming.length() * 1.08D));
    projectile.setVelocity(rebound);
    projectile.setShooter(defender);
    defender.getWorld().playSound(defender.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.85F, 1.15F);
    defender.getWorld().spawnParticle(Particle.CRIT, defender.getEyeLocation(), 12, 0.25D, 0.2D, 0.25D, 0.03D);
  }

  private boolean isRaidImmunityTrackedMob(Entity entity) {
    if (entity == null) {
      return false;
    }
    EntityType type = entity.getType();
    return type == EntityType.ENDER_DRAGON
        || type == EntityType.BLAZE
        || type == EntityType.VEX
        || type == EntityType.ENDERMAN
        || type == EntityType.DRAGON_FIREBALL;
  }

  private void repairPlayerEquipmentDurability(Player player, int amount) {
    if (player == null || amount <= 0) {
      return;
    }
    List<ItemStack> allItems = new ArrayList<>();
    PlayerInventory inventory = player.getInventory();
    if (inventory != null) {
      allItems.add(inventory.getItemInMainHand());
      allItems.add(inventory.getItemInOffHand());
      allItems.addAll(Arrays.asList(inventory.getArmorContents()));
      allItems.addAll(Arrays.asList(inventory.getContents()));
    }
    for (ItemStack item : allItems) {
      if (item == null || item.getType() == Material.AIR) {
        continue;
      }
      ItemMeta meta = item.getItemMeta();
      if (!(meta instanceof Damageable damageable)) {
        continue;
      }
      int current = Math.max(0, damageable.getDamage());
      if (current <= 0) {
        continue;
      }
      damageable.setDamage(Math.max(0, current - amount));
      item.setItemMeta(meta);
    }
  }

  private int applyArmorDurabilitySacrifice(Player player, double preventedDamage, int tier) {
    if (player == null || preventedDamage <= 0.0D || tier <= 0) {
      return 0;
    }
    PlayerInventory inventory = player.getInventory();
    ItemStack[] armor = inventory.getArmorContents();
    if (armor == null || armor.length == 0) {
      return 0;
    }
    int equipped = 0;
    for (ItemStack piece : armor) {
      if (isArmorItem(piece)) {
        equipped++;
      }
    }
    if (equipped <= 0) {
      return 0;
    }

    int totalDurabilityCost = Math.max(1, (int) Math.ceil(preventedDamage * (1.5D + (tier * 0.5D))));
    int perPieceCost = Math.max(1, totalDurabilityCost / equipped);
    int broken = 0;

    for (int i = 0; i < armor.length; i++) {
      ItemStack piece = armor[i];
      if (!isArmorItem(piece)) {
        continue;
      }
      int maxDurability = piece.getType().getMaxDurability();
      if (maxDurability <= 0) {
        continue;
      }
      ItemMeta meta = piece.getItemMeta();
      if (!(meta instanceof Damageable damageableMeta) || meta.isUnbreakable()) {
        continue;
      }
      int current = Math.max(0, damageableMeta.getDamage());
      int next = current + perPieceCost;
      if (next >= maxDurability) {
        armor[i] = null;
        broken++;
        continue;
      }
      damageableMeta.setDamage(next);
      piece.setItemMeta((ItemMeta) damageableMeta);
      armor[i] = piece;
    }

    inventory.setArmorContents(armor);
    return broken;
  }

  private boolean shatterOneArmorPiece(Player player) {
    if (player == null) {
      return false;
    }
    PlayerInventory inventory = player.getInventory();
    ItemStack[] armor = inventory.getArmorContents();
    if (armor == null || armor.length == 0) {
      return false;
    }
    int chosenSlot = -1;
    int highestRemainingDurability = -1;
    for (int i = 0; i < armor.length; i++) {
      ItemStack piece = armor[i];
      if (!isArmorItem(piece)) {
        continue;
      }
      int maxDurability = piece.getType().getMaxDurability();
      if (maxDurability <= 0) {
        continue;
      }
      ItemMeta meta = piece.getItemMeta();
      int currentDamage = 0;
      if (meta instanceof Damageable damageableMeta) {
        currentDamage = Math.max(0, damageableMeta.getDamage());
      }
      int remaining = Math.max(0, maxDurability - currentDamage);
      if (remaining > highestRemainingDurability) {
        highestRemainingDurability = remaining;
        chosenSlot = i;
      }
    }
    if (chosenSlot < 0) {
      return false;
    }
    armor[chosenSlot] = null;
    inventory.setArmorContents(armor);
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.9F, 0.9F);
    return true;
  }

  private boolean isArmorItem(ItemStack item) {
    if (item == null || item.getType() == Material.AIR) {
      return false;
    }
    String name = item.getType().name();
    return name.endsWith("_HELMET")
        || name.endsWith("_CHESTPLATE")
        || name.endsWith("_LEGGINGS")
        || name.endsWith("_BOOTS")
        || item.getType() == Material.ELYTRA;
  }

  private boolean isInsideEnderAura(Player player, double radius) {
    if (player == null || radius <= 0.0D) {
      return false;
    }
    for (Entity nearby : player.getNearbyEntities(radius, radius, radius)) {
      if (nearby instanceof Enderman) {
        return true;
      }
      if (nearby instanceof Monster monster && hasFreshAuraMark(monster)) {
        return true;
      }
    }
    return false;
  }

  private boolean isAuraInfusedMob(Entity entity) {
    if (!(entity instanceof Monster monster)) {
      return false;
    }
    return monster instanceof Enderman || hasFreshAuraMark(monster);
  }

  private boolean hasFreshAuraMark(Monster monster) {
    if (monster == null || !monster.hasMetadata(ENDER_AURA_TICK_METADATA)) {
      return false;
    }
    long latestAuraTick = Long.MIN_VALUE;
    for (MetadataValue value : monster.getMetadata(ENDER_AURA_TICK_METADATA)) {
      if (value.getOwningPlugin() != this) {
        continue;
      }
      latestAuraTick = Math.max(latestAuraTick, value.asLong());
    }
    if (latestAuraTick == Long.MIN_VALUE) {
      return false;
    }

    // tickCounter increases once per second in tick(); convert aura duration ticks to seconds.
    long freshnessWindowSeconds = Math.max(1L, (long) Math.ceil(enderAuraEffectDurationTicks() / 20.0D) + 2L);
    long nowTick = tickCounter;
    boolean stale = latestAuraTick > nowTick || (nowTick - latestAuraTick) > freshnessWindowSeconds;
    if (stale) {
      monster.removeMetadata(ENDER_AURA_TICK_METADATA, this);
      return false;
    }
    return true;
  }

  private void cleanupStaleAuraMonsterAttributeModifiers() {
    if (auraAttributeModifiedMonsters.isEmpty()) {
      return;
    }
    auraAttributeModifiedMonsters.removeIf(entityId -> {
      Entity entity = Bukkit.getEntity(entityId);
      if (!(entity instanceof Monster monster) || !monster.isValid() || monster.isDead()) {
        return true;
      }
      if (hasFreshAuraMark(monster)) {
        return false;
      }
      removeAuraMonsterAttributeModifiers(monster);
      return true;
    });
  }

  private void clearAllAuraMonsterAttributeModifiers() {
    if (auraAttributeModifiedMonsters.isEmpty()) {
      return;
    }
    for (UUID entityId : new HashSet<>(auraAttributeModifiedMonsters)) {
      Entity entity = Bukkit.getEntity(entityId);
      if (entity instanceof Monster monster && monster.isValid() && !monster.isDead()) {
        removeAuraMonsterAttributeModifiers(monster);
      }
    }
    auraAttributeModifiedMonsters.clear();
  }

  private void applyAuraMonsterAttributeModifiers(Monster monster, double multiplier) {
    if (monster == null || !monster.isValid() || monster.isDead()) {
      return;
    }
    UUID entityId = monster.getUniqueId();
    boolean shouldApply = Math.abs(multiplier - 1.0D) > 0.0001D;
    if (!shouldApply) {
      removeAuraMonsterAttributeModifiers(monster);
      auraAttributeModifiedMonsters.remove(entityId);
      return;
    }

    AttributeInstance maxHealth = monster.getAttribute(Attribute.MAX_HEALTH);
    if (maxHealth != null) {
      upsertAuraAttributeModifier(maxHealth, ENDER_AURA_MAX_HEALTH_MODIFIER_KEY, multiplier);
      double adjustedMax = Math.max(0.5D, maxHealth.getValue());
      if (monster.getHealth() > adjustedMax) {
        monster.setHealth(adjustedMax);
      }
    }
    AttributeInstance attackDamage = monster.getAttribute(Attribute.ATTACK_DAMAGE);
    if (attackDamage != null) {
      upsertAuraAttributeModifier(attackDamage, ENDER_AURA_ATTACK_DAMAGE_MODIFIER_KEY, multiplier);
    }

    auraAttributeModifiedMonsters.add(entityId);
    monster.addScoreboardTag(ENDER_AURA_TAG);
  }

  private void removeAuraMonsterAttributeModifiers(Monster monster) {
    if (monster == null) {
      return;
    }
    AttributeInstance maxHealth = monster.getAttribute(Attribute.MAX_HEALTH);
    if (maxHealth != null) {
      removeAuraAttributeModifier(maxHealth, ENDER_AURA_MAX_HEALTH_MODIFIER_KEY);
    }
    AttributeInstance attackDamage = monster.getAttribute(Attribute.ATTACK_DAMAGE);
    if (attackDamage != null) {
      removeAuraAttributeModifier(attackDamage, ENDER_AURA_ATTACK_DAMAGE_MODIFIER_KEY);
    }
    monster.removeScoreboardTag(ENDER_AURA_TAG);
  }

  private void upsertAuraAttributeModifier(
      AttributeInstance instance,
      NamespacedKey modifierKey,
      double multiplier
  ) {
    if (instance == null || modifierKey == null) {
      return;
    }
    removeAuraAttributeModifier(instance, modifierKey);
    double amount = multiplier - 1.0D;
    if (Math.abs(amount) < 0.0001D) {
      return;
    }
    instance.addModifier(new AttributeModifier(modifierKey, amount, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
  }

  private void removeAuraAttributeModifier(AttributeInstance instance, NamespacedKey modifierKey) {
    if (instance == null || modifierKey == null) {
      return;
    }
    instance.removeModifier(modifierKey);
  }

  private int highestEffect80Tier(PlayerRoundData data, char group, int index) {
    if (!cardsEffectLogicEnabled() || data == null || index <= 0) {
      return 0;
    }
    int best = 0;
    for (ActiveSeasonEffect effect : collectAllActiveEffects(data)) {
      if (effect == null || effect.getId() == null || effect.getId().isBlank()) {
        continue;
      }
      EffectGimmickProfile profile = effectGimmicksById.get(normalizeEffectId(effect.getId()));
      Effect80Id id = profile == null ? parseEffect80Id(effect.getId()) : profile.effect80Id();
      if (id == null || id.group() != group || id.index() != index) {
        continue;
      }
      int resolvedTier = id.group() == 'B'
          ? boostedBModTier(effect.getId(), effect.getTier())
          : Math.max(1, Math.min(4, effect.getTier()));
      best = Math.max(best, resolvedTier);
    }
    return best;
  }

  private double auraResonanceGlobalAuraPowerBonus() {
    long now = nowEpochSecond();
    double maxBonus = 0.0D;
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player == null || !player.isOnline() || player.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }
      PlayerRoundData data = players.get(player.getUniqueId());
      if (data == null || data.isOut()) {
        continue;
      }
      int tier = highestEffect80Tier(data, 'X', 12);
      if (tier <= 0) {
        continue;
      }
      double bonus = tier <= 1 ? 0.30D : 0.50D;
      if (tier >= 4 && auraResonanceBacklashUntilEpochSecondByPlayer.getOrDefault(player.getUniqueId(), 0L) > now) {
        bonus += 0.25D;
      }
      maxBonus = Math.max(maxBonus, bonus);
    }
    return maxBonus;
  }

  private void sendScannerHint(Player player, boolean noisy) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    Location origin = player.getLocation();
    Entity targetEntity = null;
    double nearest = Double.MAX_VALUE;
    for (Entity entity : player.getNearbyEntities(48.0D, 48.0D, 48.0D)) {
      if (!(entity instanceof Monster) && !(entity instanceof Player)) {
        continue;
      }
      if (entity == player) {
        continue;
      }
      double dist = entity.getLocation().distanceSquared(origin);
      if (dist < nearest) {
        nearest = dist;
        targetEntity = entity;
      }
    }
    if (targetEntity == null) {
      player.sendActionBar(ChatColor.GRAY + "스캐너: 주변 반응 없음");
      return;
    }
    Vector dir = targetEntity.getLocation().toVector().subtract(origin.toVector());
    String hint = String.format(
        Locale.ROOT,
        "스캐너: %s %.0fm",
        targetEntity.getType().name(),
        Math.sqrt(nearest)
    );
    player.sendActionBar(ChatColor.AQUA + hint);
    if (noisy) {
      player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.6F, 1.5F);
      player.getWorld().spawnParticle(Particle.END_ROD, origin.clone().add(dir.normalize().multiply(1.2D)), 8, 0.3, 0.2, 0.3, 0.01);
    }
  }

  private void sendScannerHintByTier(Player player, int tier, boolean noisy) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    Location origin = player.getLocation();
    World world = player.getWorld();

    if (clampedTier >= 3) {
      if (world.getEnvironment() == World.Environment.NETHER) {
        Block netherOre = findNearestOreBlock(origin, 24, true);
        if (netherOre != null) {
          Vector dir = netherOre.getLocation().toVector().subtract(origin.toVector());
          player.sendActionBar(ChatColor.AQUA + "스캐너: 네더 광물 " + compassDirectionForPlayer(player, dir) + " / " + distanceBandLabel(Math.sqrt(dir.lengthSquared())));
          if (noisy) {
            player.spawnParticle(Particle.END_ROD, netherOre.getLocation().clone().add(0.5D, 0.7D, 0.5D), 12, 0.2D, 0.2D, 0.2D, 0.0D);
          }
          return;
        }
      } else if (world.getEnvironment() == World.Environment.THE_END) {
        Vector toCenter = new Vector(-origin.getX(), 0.0D, -origin.getZ());
        player.sendActionBar(ChatColor.AQUA + "스캐너: 엔드 출구 " + compassDirectionForPlayer(player, toCenter) + " / " + distanceBandLabel(toCenter.length()));
        if (noisy) {
          Vector pointer = toCenter.lengthSquared() <= 0.0001D ? new Vector(1.0D, 0.0D, 0.0D) : toCenter.normalize();
          player.spawnParticle(Particle.END_ROD, origin.clone().add(pointer.multiply(1.5D)).add(0.0D, 1.0D, 0.0D), 12, 0.25D, 0.2D, 0.25D, 0.01D);
        }
        return;
      }
    }

    Entity target = null;
    double nearest = Double.MAX_VALUE;
    for (Entity entity : player.getNearbyEntities(64.0D, 64.0D, 64.0D)) {
      if (!(entity instanceof Monster) && !(entity instanceof Player)) {
        continue;
      }
      if (entity == player) {
        continue;
      }
      double distSq = entity.getLocation().distanceSquared(origin);
      if (distSq < nearest) {
        nearest = distSq;
        target = entity;
      }
    }
    if (target == null) {
      Block ore = findNearestOreBlock(origin, 18, false);
      if (ore != null) {
        Vector dir = ore.getLocation().toVector().subtract(origin.toVector());
        player.sendActionBar(ChatColor.GRAY + "스캐너: 광물 반응 " + compassDirectionForPlayer(player, dir));
        return;
      }
      player.sendActionBar(ChatColor.GRAY + "스캐너: 주변 반응 없음");
      return;
    }
    Vector dir = target.getLocation().toVector().subtract(origin.toVector());
    double distance = Math.sqrt(nearest);
    if (clampedTier <= 1) {
      player.sendActionBar(ChatColor.AQUA + "스캐너: 근접 반응 감지");
    } else {
      player.sendActionBar(ChatColor.AQUA + "스캐너: " + target.getType().name() + " " + compassDirectionForPlayer(player, dir) + " / " + distanceBandLabel(distance));
    }
    if (noisy) {
      player.getWorld().playSound(origin, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.6F, 1.35F);
      player.getWorld().spawnParticle(Particle.END_ROD, origin.clone().add(dir.normalize().multiply(1.2D)), 10, 0.25D, 0.2D, 0.25D, 0.01D);
    }
  }

  private void performPrecisionScannerPing(Player player) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    Location origin = player.getLocation();
    List<Entity> targets = new ArrayList<>();
    for (Entity entity : player.getNearbyEntities(72.0D, 72.0D, 72.0D)) {
      if (entity == player) {
        continue;
      }
      if (!(entity instanceof Monster) && !(entity instanceof Player)) {
        continue;
      }
      targets.add(entity);
    }
    if (targets.isEmpty()) {
      player.sendActionBar(ChatColor.GRAY + "정밀 스캔: 반응 없음");
      return;
    }
    targets.sort(Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(origin)));
    int pinged = 0;
    for (Entity entity : targets) {
      if (pinged >= 3) {
        break;
      }
      Location targetLoc = entity.getLocation();
      Vector dir = targetLoc.toVector().subtract(origin.toVector());
      double distance = Math.sqrt(targetLoc.distanceSquared(origin));
      player.sendMessage(ChatColor.AQUA + "[스캐너] " + entity.getType().name()
          + " / " + compassDirectionForPlayer(player, dir)
          + " / " + String.format(Locale.ROOT, "%.0fm", distance));
      player.spawnParticle(Particle.END_ROD, targetLoc.clone().add(0.0D, 1.0D, 0.0D), 16, 0.35D, 0.5D, 0.35D, 0.01D);
      if (entity instanceof LivingEntity living) {
        living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 160, 0, true, true, true));
      }
      pinged++;
    }
  }

  private void markResonanceScannerTargets(Player player, int tier) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    double radius = switch (clampedTier) {
      case 1 -> 18.0D;
      case 2 -> 28.0D;
      case 3 -> 40.0D;
      default -> 52.0D;
    };
    long markUntil = nowEpochSecond() + 5L;
    int marked = 0;
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (entity == player) {
        continue;
      }
      if (!(entity instanceof LivingEntity living)) {
        continue;
      }
      living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, true, true));
      scannerMarkedUntilEpochSecondByEntity.put(living.getUniqueId(), markUntil);
      marked++;
      if (marked >= 32) {
        break;
      }
    }
    Block ore = findNearestOreBlock(player.getLocation(), Math.max(10, (int) radius / 2), false);
    if (ore != null) {
      player.spawnParticle(Particle.END_ROD, ore.getLocation().clone().add(0.5D, 0.8D, 0.5D), 14, 0.3D, 0.3D, 0.3D, 0.01D);
    }
  }

  private Block findNearestOreBlock(Location origin, int radius, boolean netherPriority) {
    if (origin == null || origin.getWorld() == null || radius <= 0) {
      return null;
    }
    World world = origin.getWorld();
    int ox = origin.getBlockX();
    int oy = origin.getBlockY();
    int oz = origin.getBlockZ();
    double bestDistanceSq = Double.MAX_VALUE;
    Block best = null;
    for (int dx = -radius; dx <= radius; dx++) {
      for (int dy = -Math.min(16, radius); dy <= Math.min(16, radius); dy++) {
        for (int dz = -radius; dz <= radius; dz++) {
          Block block = world.getBlockAt(ox + dx, oy + dy, oz + dz);
          Material type = block.getType();
          if (!isOreMaterial(type)) {
            continue;
          }
          if (netherPriority && world.getEnvironment() == World.Environment.NETHER
              && type != Material.ANCIENT_DEBRIS
              && type != Material.NETHER_QUARTZ_ORE
              && type != Material.NETHER_GOLD_ORE) {
            continue;
          }
          double distSq = block.getLocation().distanceSquared(origin);
          if (distSq < bestDistanceSq) {
            bestDistanceSq = distSq;
            best = block;
          }
        }
      }
    }
    return best;
  }

  private String compassDirectionForPlayer(Player player, Vector vector) {
    if (player == null || vector == null || vector.lengthSquared() <= 0.0001D) {
      return compassDirectionFromVector(vector);
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    int tier = highestEffect80Tier(data, 'C', 15);
    if (tier <= 0) {
      return compassDirectionFromVector(vector);
    }

    Vector distorted = vector.clone();
    if (tier >= 4) {
      if (ThreadLocalRandom.current().nextDouble() < 0.18D) {
        return compassDirectionFromVector(distorted);
      }
      if (ThreadLocalRandom.current().nextDouble() < 0.70D) {
        distorted.multiply(-1.0D);
      }
      distorted = rotateHorizontalVector(distorted, ThreadLocalRandom.current().nextDouble(-90.0D, 90.0D));
      return compassDirectionFromVector(distorted);
    }
    if (tier == 3 && ThreadLocalRandom.current().nextDouble() < 0.30D) {
      distorted.multiply(-1.0D);
      return compassDirectionFromVector(distorted);
    }
    double maxOffset = tier >= 2 ? 60.0D : 30.0D;
    distorted = rotateHorizontalVector(distorted, ThreadLocalRandom.current().nextDouble(-maxOffset, maxOffset));
    return compassDirectionFromVector(distorted);
  }

  private Vector rotateHorizontalVector(Vector source, double yawDegrees) {
    if (source == null) {
      return new Vector(0.0D, 0.0D, 0.0D);
    }
    double radians = Math.toRadians(yawDegrees);
    double cos = Math.cos(radians);
    double sin = Math.sin(radians);
    double x = (source.getX() * cos) - (source.getZ() * sin);
    double z = (source.getX() * sin) + (source.getZ() * cos);
    return new Vector(x, source.getY(), z);
  }

  private String compassDirectionFromVector(Vector vector) {
    if (vector == null || vector.lengthSquared() <= 0.0001D) {
      return "근접";
    }
    double angle = Math.atan2(vector.getZ(), vector.getX());
    double normalized = (Math.toDegrees(angle) + 360.0D) % 360.0D;
    if (normalized < 22.5D || normalized >= 337.5D) {
      return "E";
    }
    if (normalized < 67.5D) {
      return "SE";
    }
    if (normalized < 112.5D) {
      return "S";
    }
    if (normalized < 157.5D) {
      return "SW";
    }
    if (normalized < 202.5D) {
      return "W";
    }
    if (normalized < 247.5D) {
      return "NW";
    }
    if (normalized < 292.5D) {
      return "N";
    }
    return "NE";
  }

  private String distanceBandLabel(double distance) {
    if (distance < 12.0D) {
      return "근거리";
    }
    if (distance < 28.0D) {
      return "중거리";
    }
    if (distance < 48.0D) {
      return "원거리";
    }
    return "초원거리";
  }

  private void emitExcavationOreHint(Player player, Location center, int radius) {
    if (player == null || center == null) {
      return;
    }
    Block ore = findNearestOreBlock(center, Math.max(4, radius), false);
    if (ore == null) {
      return;
    }
    Location target = ore.getLocation().clone().add(0.5D, 0.7D, 0.5D);
    player.spawnParticle(Particle.END_ROD, target, 14, 0.25D, 0.25D, 0.25D, 0.01D);
    player.spawnParticle(Particle.CRIT, target, 8, 0.18D, 0.18D, 0.18D, 0.01D);
  }

  private void applyAuraInversionBlessingPeriodic(Player player, int tier, long nowEpochSecond) {
    if (player == null || tier <= 0) {
      return;
    }
    int radius = switch (tier) {
      case 1 -> 12;
      case 2 -> 16;
      case 3 -> 20;
      default -> 24;
    };
    int weaknessAmplifier = switch (tier) {
      case 1 -> 0;
      case 2 -> 1;
      case 3 -> 2;
      default -> 3;
    };
    double periodicDamage = tier >= 4 ? 6.0D : (tier >= 2 ? 3.0D : 0.0D);
    long inversionUntil = auraInversionUntilEpochSecondByPlayer.getOrDefault(player.getUniqueId(), 0L);
    boolean inversionActive = inversionUntil > nowEpochSecond;
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!isAuraInfusedMob(entity) || !(entity instanceof Monster monster)) {
        continue;
      }
      monster.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, weaknessAmplifier, true, true, true));
      if (tier >= 2) {
        monster.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, true, true));
        player.spawnParticle(Particle.END_ROD, monster.getLocation().clone().add(0.0D, 1.2D, 0.0D), 4, 0.2D, 0.3D, 0.2D, 0.0D);
      }
      if (inversionActive && periodicDamage > 0.0D) {
        monster.damage(periodicDamage, player);
      }
    }
    if (inversionActive && useEffectCooldown(player.getUniqueId(), "B-039", "aura_inverse_fx", 2L)) {
      emitBModProcFeedback(player, tier, false);
    }
  }

  private void markNearbyVexApproach(Player player) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    for (Entity entity : player.getNearbyEntities(24.0D, 16.0D, 24.0D)) {
      if (!(entity instanceof Vex vex)) {
        continue;
      }
      player.spawnParticle(Particle.END_ROD, vex.getLocation().clone().add(0.0D, 0.8D, 0.0D), 8, 0.2D, 0.2D, 0.2D, 0.0D);
    }
  }

  private void neutralizeNearbyStalkers(Player player, int durationTicks) {
    if (player == null || durationTicks <= 0) {
      return;
    }
    for (Entity entity : player.getNearbyEntities(28.0D, 16.0D, 28.0D)) {
      if (!(entity instanceof Enderman enderman) || !enderman.getScoreboardTags().contains(STALKER_TAG)) {
        continue;
      }
      enderman.setTarget(null);
      enderman.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 6, true, true, true));
      enderman.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, durationTicks, 1, true, true, true));
      enderman.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Math.min(durationTicks, 120), 0, true, true, true));
    }
  }

  private boolean confuseOneNearbyStalker(Player player, int durationTicks) {
    if (player == null || durationTicks <= 0) {
      return false;
    }
    Enderman chosen = null;
    double nearest = Double.MAX_VALUE;
    for (Entity entity : player.getNearbyEntities(24.0D, 12.0D, 24.0D)) {
      if (!(entity instanceof Enderman enderman) || !enderman.getScoreboardTags().contains(STALKER_TAG)) {
        continue;
      }
      double distSq = enderman.getLocation().distanceSquared(player.getLocation());
      if (distSq < nearest) {
        nearest = distSq;
        chosen = enderman;
      }
    }
    if (chosen == null) {
      return false;
    }
    chosen.setTarget(null);
    chosen.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, durationTicks, 0, true, false, true));
    chosen.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Math.min(durationTicks, 120), 0, true, true, true));
    return true;
  }

  private boolean isPlayerInRecentCombat(UUID playerId, long windowSeconds) {
    if (playerId == null || windowSeconds <= 0L) {
      return false;
    }
    long until = lastCombatEpochSecondByPlayer.getOrDefault(playerId, 0L) + windowSeconds;
    return until > nowEpochSecond();
  }

  private long currentEpochDayUtc() {
    return Math.floorDiv(nowEpochSecond(), 86400L);
  }

  private void protectReinforcedBuildBurstBlocks(List<Block> blocks) {
    if (blocks == null || blocks.isEmpty()) {
      return;
    }
    long now = nowEpochSecond();
    blocks.removeIf(block -> {
      if (block == null || block.getWorld() == null) {
        return false;
      }
      long until = reinforcedBurstBlocksUntilEpochSecond.getOrDefault(blockKey(block.getLocation()), 0L);
      return until > now;
    });
  }

  private void createInstantBurstWall(Player player, int tier) {
    if (player == null || player.getWorld() == null) {
      return;
    }
    Material wallMaterial = resolveBurstWallMaterial(player);
    Location base = player.getLocation().clone();
    Vector forward = base.getDirection().setY(0.0D);
    if (forward.lengthSquared() <= 0.0001D) {
      forward = new Vector(0.0D, 0.0D, 1.0D);
    }
    forward.normalize();
    Vector right = new Vector(-forward.getZ(), 0.0D, forward.getX());
    Location center = base.clone().add(forward.clone().multiply(1.5D));
    long reinforceUntil = nowEpochSecond() + 10L;
    for (int side = -1; side <= 1; side++) {
      for (int y = 0; y <= 1; y++) {
        Location placeLoc = center.clone().add(right.clone().multiply(side)).add(0.0D, y, 0.0D);
        Block block = placeLoc.getBlock();
        if (!block.isPassable()) {
          continue;
        }
        block.setType(wallMaterial, false);
        if (tier >= 2) {
          reinforcedBurstBlocksUntilEpochSecond.put(blockKey(block.getLocation()), reinforceUntil);
        }
      }
    }
  }

  private Material resolveBurstWallMaterial(Player player) {
    if (player == null) {
      return Material.COBBLESTONE;
    }
    ItemStack held = player.getInventory().getItemInMainHand();
    if (held != null && held.getType().isBlock() && held.getType().isSolid() && held.getType() != Material.BEDROCK) {
      return held.getType();
    }
    return Material.COBBLESTONE;
  }

  private void rememberCraftingMatrix(UUID playerId, ItemStack[] matrix) {
    if (playerId == null || matrix == null || matrix.length == 0) {
      return;
    }
    ItemStack[] remembered = new ItemStack[9];
    boolean hasIngredient = false;
    for (int i = 0; i < remembered.length && i < matrix.length; i++) {
      ItemStack ingredient = matrix[i];
      if (ingredient == null || ingredient.getType() == Material.AIR) {
        remembered[i] = null;
        continue;
      }
      ItemStack one = ingredient.clone();
      one.setAmount(1);
      remembered[i] = one;
      hasIngredient = true;
    }
    if (hasIngredient) {
      rememberedRecipeMatrixByPlayer.put(playerId, remembered);
    }
  }

  private ItemStack randomSingleIngredientFromMatrix(ItemStack[] matrix) {
    if (matrix == null || matrix.length == 0) {
      return null;
    }
    List<ItemStack> candidates = new ArrayList<>();
    for (ItemStack ingredient : matrix) {
      if (ingredient == null || ingredient.getType() == Material.AIR) {
        continue;
      }
      ItemStack one = ingredient.clone();
      one.setAmount(1);
      candidates.add(one);
    }
    if (candidates.isEmpty()) {
      return null;
    }
    return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
  }

  private void autoFillRememberedRecipe(Player player) {
    if (player == null || !player.isOnline()) {
      return;
    }
    ItemStack[] remembered = rememberedRecipeMatrixByPlayer.get(player.getUniqueId());
    if (remembered == null || remembered.length == 0) {
      return;
    }
    Inventory top = player.getOpenInventory().getTopInventory();
    if (!(top instanceof CraftingInventory crafting)) {
      return;
    }
    ItemStack[] matrix = new ItemStack[9];
    for (int i = 0; i < matrix.length && i < remembered.length; i++) {
      ItemStack ingredient = remembered[i];
      if (ingredient == null || ingredient.getType() == Material.AIR) {
        matrix[i] = null;
        continue;
      }
      ItemStack consumed = consumeSingleMatchingItem(player, ingredient);
      if (consumed == null) {
        matrix[i] = null;
        continue;
      }
      matrix[i] = consumed;
    }
    crafting.setMatrix(matrix);
  }

  private ItemStack consumeSingleMatchingItem(Player player, ItemStack template) {
    if (player == null || template == null || template.getType() == Material.AIR) {
      return null;
    }
    PlayerInventory inventory = player.getInventory();
    for (int slot = 0; slot < inventory.getSize(); slot++) {
      ItemStack stack = inventory.getItem(slot);
      if (stack == null || stack.getType() == Material.AIR) {
        continue;
      }
      if (stack.getType() != template.getType()) {
        continue;
      }
      ItemStack result = stack.clone();
      result.setAmount(1);
      int remain = stack.getAmount() - 1;
      if (remain <= 0) {
        inventory.setItem(slot, null);
      } else {
        stack.setAmount(remain);
        inventory.setItem(slot, stack);
      }
      return result;
    }
    return null;
  }

  private void grantRaidImmunityPhaseTransitionBonus(World world, int phase) {
    if (world == null || phase <= 0) {
      return;
    }
    for (Player player : endRaidTargets(world)) {
      if (player == null || !player.isOnline()) {
        continue;
      }
      PlayerRoundData data = players.get(player.getUniqueId());
      int tier = highestEffect80Tier(data, 'B', 19);
      if (tier < 1) {
        continue;
      }
      int durabilityRecover = switch (tier) {
        case 1 -> 0;
        case 2 -> 10;
        case 3 -> 20;
        default -> 30;
      };
      if (durabilityRecover > 0) {
        repairPlayerEquipmentDurability(player, durabilityRecover);
      }
      int regenAmplifier = switch (tier) {
        case 1 -> 1; // regen II
        case 2 -> 2; // regen III
        case 3 -> 3; // regen IV
        default -> 4; // regen V
      };
      player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, regenAmplifier, true, false, true));
      emitBModProcFeedback(player, tier, false);
    }
  }

  private void weakenNearbyMonsters(Player player, int radius, int durationTicks, int amplifier) {
    if (player == null) {
      return;
    }
    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof Monster monster)) {
        continue;
      }
      monster.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, durationTicks, amplifier, true, true, true));
    }
  }

  private void clearSingleNegativeEffect(Player player) {
    if (player == null) {
      return;
    }
    for (PotionEffect active : player.getActivePotionEffects()) {
      if (active == null || active.getType() == null) {
        continue;
      }
      if (isHarmfulPotionType(active.getType())) {
        player.removePotionEffect(active.getType());
        return;
      }
    }
  }

  private void removePositivePotionEffects(Player player, int maxCount) {
    if (player == null || maxCount <= 0) {
      return;
    }
    int removed = 0;
    for (PotionEffect active : player.getActivePotionEffects()) {
      if (active == null || active.getType() == null) {
        continue;
      }
      if (!isBeneficialPotionType(active.getType())) {
        continue;
      }
      player.removePotionEffect(active.getType());
      removed++;
      if (removed >= maxCount) {
        return;
      }
    }
  }

  private void purgeCustomPositiveStates(UUID playerId, int maxCount) {
    if (playerId == null || maxCount <= 0) {
      return;
    }
    int removed = 0;
    long now = nowEpochSecond();

    if (scoreInvestmentUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > now) {
      scoreInvestmentUntilEpochSecondByPlayer.remove(playerId);
      scoreInvestmentBonusRatioByPlayer.remove(playerId);
      removed++;
    }
    if (removed >= maxCount) {
      return;
    }
    if (frenzyUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > now) {
      frenzyUntilEpochSecondByPlayer.remove(playerId);
      removed++;
    }
    if (removed >= maxCount) {
      return;
    }
    if (projectileAlignUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > now) {
      projectileAlignUntilEpochSecondByPlayer.remove(playerId);
      removed++;
    }
    if (removed >= maxCount) {
      return;
    }
    if (projectileAbsorbWindowUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > now) {
      projectileAbsorbWindowUntilEpochSecondByPlayer.remove(playerId);
      projectileAbsorbTierByPlayer.remove(playerId);
      removed++;
    }
    if (removed >= maxCount) {
      return;
    }
    if (projectileAbsorbReflectUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > now) {
      projectileAbsorbReflectUntilEpochSecondByPlayer.remove(playerId);
      removed++;
    }
    if (removed >= maxCount) {
      return;
    }
    if (knockbackImmuneUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > now) {
      knockbackImmuneUntilEpochSecondByPlayer.remove(playerId);
      removed++;
    }
    if (removed >= maxCount) {
      return;
    }
    if (wallHangUntilEpochSecondByPlayer.getOrDefault(playerId, 0L) > now) {
      wallHangUntilEpochSecondByPlayer.remove(playerId);
      wallHangRechargeEpochSecondByPlayer.remove(playerId);
      removed++;
    }
  }

  private void processCursedDropsForPlayer(Player player, int tier, long nowEpochSecond) {
    if (player == null || tier <= 0 || cursedDropOwnerByItem.isEmpty()) {
      return;
    }
    UUID ownerId = player.getUniqueId();
    boolean inCombat = tier >= 4 && isPlayerInRecentCombat(ownerId, 10L);

    java.util.Iterator<Map.Entry<UUID, UUID>> iterator = cursedDropOwnerByItem.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<UUID, UUID> entry = iterator.next();
      if (entry == null || entry.getKey() == null || entry.getValue() == null) {
        if (entry != null && entry.getKey() != null) {
          cursedDropExpireEpochSecondByItem.remove(entry.getKey());
        }
        iterator.remove();
        continue;
      }
      if (!ownerId.equals(entry.getValue())) {
        continue;
      }
      UUID itemId = entry.getKey();
      Entity entity = Bukkit.getEntity(itemId);
      if (!(entity instanceof Item item) || !item.isValid() || item.isDead()) {
        iterator.remove();
        cursedDropExpireEpochSecondByItem.remove(itemId);
        continue;
      }

      long expireAt = cursedDropExpireEpochSecondByItem.getOrDefault(itemId, nowEpochSecond + 5L);
      if (inCombat) {
        long tightened = nowEpochSecond + 45L;
        if (tightened < expireAt) {
          expireAt = tightened;
          cursedDropExpireEpochSecondByItem.put(itemId, expireAt);
          int targetTicksLived = Math.max(0, (int) (6000L - (45L * 20L)));
          item.setTicksLived(Math.max(item.getTicksLived(), targetTicksLived));
        }
      }

      if (expireAt <= nowEpochSecond) {
        item.remove();
        iterator.remove();
        cursedDropExpireEpochSecondByItem.remove(itemId);
      }
    }

    if (!inCombat && dropRotUntilEpochSecondByPlayer.getOrDefault(ownerId, 0L) <= nowEpochSecond) {
      dropRotUntilEpochSecondByPlayer.remove(ownerId);
    }
  }

  private void cleanupCursedDropRecords(long nowEpochSecond) {
    if (cursedDropOwnerByItem.isEmpty()) {
      return;
    }
    java.util.Iterator<Map.Entry<UUID, UUID>> iterator = cursedDropOwnerByItem.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<UUID, UUID> entry = iterator.next();
      if (entry == null || entry.getKey() == null) {
        iterator.remove();
        continue;
      }
      UUID itemId = entry.getKey();
      Entity entity = Bukkit.getEntity(itemId);
      long expireAt = cursedDropExpireEpochSecondByItem.getOrDefault(itemId, nowEpochSecond + 30L);
      if (!(entity instanceof Item item) || !item.isValid() || item.isDead() || expireAt <= nowEpochSecond) {
        if (entity instanceof Item item && item.isValid() && !item.isDead() && expireAt <= nowEpochSecond) {
          item.remove();
        }
        iterator.remove();
        cursedDropExpireEpochSecondByItem.remove(itemId);
      }
    }
  }

  private void applyRandomNegativeEffect(Player player, int durationTicks, int amplifier) {
    if (player == null) {
      return;
    }
    List<PotionEffectType> pool = List.of(
        PotionEffectType.WEAKNESS,
        PotionEffectType.SLOWNESS,
        PotionEffectType.BLINDNESS,
        PotionEffectType.POISON,
        PotionEffectType.NAUSEA
    );
    PotionEffectType chosen = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    player.addPotionEffect(new PotionEffect(chosen, Math.max(20, durationTicks), Math.max(0, amplifier), true, false, true));
  }

  private boolean isHealingPotionType(PotionEffectType type) {
    if (type == null) {
      return false;
    }
    if (type.equals(PotionEffectType.REGENERATION)
        || type.equals(PotionEffectType.INSTANT_HEALTH)
        || type.equals(PotionEffectType.ABSORPTION)) {
      return true;
    }
    if (type.getKey() == null) {
      return false;
    }
    String key = type.getKey().getKey().toUpperCase(Locale.ROOT);
    return key.contains("HEAL") || key.contains("REGEN");
  }

  private boolean isHealingConsumable(ItemStack item) {
    if (item == null) {
      return false;
    }
    Material type = item.getType();
    return type == Material.GOLDEN_APPLE || type == Material.ENCHANTED_GOLDEN_APPLE;
  }

  private void applyHealingInversionAttemptSideEffects(Player player, int tier) {
    if (player == null) {
      return;
    }
    if (tier >= 2) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, true, false, true));
    }
    if (tier >= 4) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 80, 0, true, false, true));
    }
  }

  private boolean isSplashOrLingeringPotionCause(EntityPotionEffectEvent.Cause cause) {
    if (cause == null) {
      return false;
    }
    String causeName = cause.name().toUpperCase(Locale.ROOT);
    return causeName.contains("SPLASH")
        || causeName.contains("AREA_EFFECT_CLOUD")
        || causeName.contains("LINGERING");
  }

  private void applyAlchemyContamination(Player player, int tier) {
    if (player == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    long nowMillis = System.currentTimeMillis();
    long guardUntil = alchemyContaminationGuardUntilEpochMilliByPlayer.getOrDefault(playerId, 0L);
    if (guardUntil > nowMillis) {
      return;
    }
    alchemyContaminationGuardUntilEpochMilliByPlayer.put(playerId, nowMillis + 350L);
    double chance = switch (Math.max(1, Math.min(4, tier))) {
      case 1 -> 0.15D;
      case 2, 3 -> 0.25D;
      default -> 1.00D;
    };
    if (ThreadLocalRandom.current().nextDouble() < chance) {
      applyRandomNegativeEffect(player, 40 + (tier * 20), 0);
    }
  }

  private boolean isBeneficialPotionType(PotionEffectType type) {
    if (type == null) {
      return false;
    }
    String category = String.valueOf(type.getCategory()).toUpperCase(Locale.ROOT);
    return category.contains("BENEFICIAL");
  }

  private boolean isHarmfulPotionType(PotionEffectType type) {
    if (type == null) {
      return false;
    }
    String category = String.valueOf(type.getCategory()).toUpperCase(Locale.ROOT);
    return category.contains("HARMFUL");
  }

  private void lockRandomHotbarSlots(UUID playerId, int slots, int durationSeconds) {
    if (playerId == null || slots <= 0 || durationSeconds <= 0) {
      return;
    }
    Map<Integer, Long> locked = lockedHotbarSlotUntilEpochSecond.computeIfAbsent(playerId, ignored -> new HashMap<>());
    long until = nowEpochSecond() + durationSeconds;
    for (int i = 0; i < slots; i++) {
      int slot = ThreadLocalRandom.current().nextInt(9);
      locked.put(slot, until);
    }
  }

  private void applyRandomToolBan(UUID playerId, int durationSeconds) {
    if (playerId == null || durationSeconds <= 0) {
      return;
    }
    List<String> groups = List.of("PICKAXE", "AXE", "SHOVEL", "HOE", "SWORD", "BOW", "CROSSBOW", "TRIDENT");
    String chosen = groups.get(ThreadLocalRandom.current().nextInt(groups.size()));
    bannedToolGroupByPlayer.put(playerId, chosen);
    bannedToolUntilEpochSecond.put(playerId, nowEpochSecond() + durationSeconds);
  }

  private void applyToolBanByTier(UUID playerId, int tier) {
    if (playerId == null) {
      return;
    }
    int durationSeconds = switch (Math.max(1, Math.min(4, tier))) {
      case 1 -> 20;
      case 2 -> 30;
      case 3 -> 30;
      default -> 35;
    };
    applyRandomToolBan(playerId, durationSeconds);
  }

  private boolean isToolBanned(Player player, ItemStack item) {
    if (player == null || item == null || item.getType() == Material.AIR) {
      return false;
    }
    UUID uuid = player.getUniqueId();
    long until = bannedToolUntilEpochSecond.getOrDefault(uuid, 0L);
    if (until <= nowEpochSecond()) {
      bannedToolUntilEpochSecond.remove(uuid);
      bannedToolGroupByPlayer.remove(uuid);
      return false;
    }
    String group = bannedToolGroupByPlayer.get(uuid);
    if (group == null || group.isBlank()) {
      return false;
    }
    String material = item.getType().name();
    return material.contains(group);
  }

  private void rotateHotbar(Player player, boolean randomSwap) {
    if (player == null) {
      return;
    }
    PlayerInventory inv = player.getInventory();
    if (randomSwap) {
      int a = ThreadLocalRandom.current().nextInt(9);
      int b = ThreadLocalRandom.current().nextInt(9);
      ItemStack ia = inv.getItem(a);
      ItemStack ib = inv.getItem(b);
      inv.setItem(a, ib);
      inv.setItem(b, ia);
      return;
    }
    ItemStack last = inv.getItem(8);
    for (int i = 8; i > 0; i--) {
      inv.setItem(i, inv.getItem(i - 1));
    }
    inv.setItem(0, last);
  }

  private void executeHotbarCycle(Player player, int tier) {
    if (player == null) {
      return;
    }
    if (tier >= 2 && ThreadLocalRandom.current().nextBoolean()) {
      rotateHotbar(player, true);
      return;
    }
    rotateHotbar(player, false);
  }

  private void forceSwapToRandomHotbar(Player player) {
    if (player == null) {
      return;
    }
    int randomSlot = ThreadLocalRandom.current().nextInt(9);
    player.getInventory().setHeldItemSlot(randomSlot);
  }

  private void flipPlayerYaw(Player player) {
    if (player == null) {
      return;
    }
    Location location = player.getLocation().clone();
    location.setYaw(location.getYaw() + 180.0F);
    c9InternalTeleportGuardUntilEpochMilliByPlayer.put(player.getUniqueId(), System.currentTimeMillis() + 600L);
    player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
  }

  private void performOminousReblink(Player player) {
    if (player == null || !player.isOnline()) {
      return;
    }
    Location base = player.getLocation().clone();
    Location shifted = base.clone().add(
        ThreadLocalRandom.current().nextDouble(-1.0D, 1.0D),
        0.0D,
        ThreadLocalRandom.current().nextDouble(-1.0D, 1.0D)
    );
    Location safe = findNearestSafeLocation(shifted, 2);
    if (safe == null) {
      return;
    }
    safe.setYaw(base.getYaw());
    safe.setPitch(base.getPitch());
    c9InternalTeleportGuardUntilEpochMilliByPlayer.put(player.getUniqueId(), System.currentTimeMillis() + 800L);
    player.teleport(safe, PlayerTeleportEvent.TeleportCause.PLUGIN);
  }

  private boolean isPortalTeleportCause(PlayerTeleportEvent.TeleportCause cause) {
    if (cause == null) {
      return false;
    }
    return cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
        || cause == PlayerTeleportEvent.TeleportCause.END_PORTAL;
  }

  private void chainShockNearby(Player attacker, LivingEntity primary, int tier) {
    if (attacker == null || primary == null) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    double radius = clampedTier <= 1 ? 3.0D : 4.0D;
    double knockbackStrength = switch (clampedTier) {
      case 1 -> 0.60D;
      case 2 -> 0.78D;
      case 3 -> 0.90D;
      default -> 1.18D;
    };
    int cap = switch (clampedTier) {
      case 1 -> 6;
      case 2 -> 8;
      case 3 -> 10;
      default -> 12;
    };
    int affected = 0;
    Location origin = primary.getLocation();
    for (Entity entity : primary.getNearbyEntities(radius, radius, radius)) {
      if (!(entity instanceof LivingEntity living) || living == attacker) {
        continue;
      }
      Vector push = living.getLocation().toVector().subtract(origin.toVector());
      if (push.lengthSquared() <= 0.0001D) {
        push = new Vector(
            ThreadLocalRandom.current().nextDouble(-0.2D, 0.2D),
            0.0D,
            ThreadLocalRandom.current().nextDouble(-0.2D, 0.2D)
        );
      }
      Vector velocity = push.normalize().multiply(knockbackStrength).setY(0.24D + (clampedTier >= 4 ? 0.06D : 0.0D));
      living.setVelocity(living.getVelocity().add(velocity));
      if (clampedTier >= 3) {
        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, true, false, true));
      }
      affected++;
      if (affected >= cap) {
        break;
      }
    }
    if (clampedTier >= 3) {
      attacker.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 0, true, false, true));
    }
    if (clampedTier >= 4) {
      attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 4, true, false, true));
      long until = nowEpochSecond() + 1L;
      UUID attackerId = attacker.getUniqueId();
      noAttackUntilEpochSecond.put(attackerId, Math.max(noAttackUntilEpochSecond.getOrDefault(attackerId, 0L), until));
      noBuildUntilEpochSecond.put(attackerId, Math.max(noBuildUntilEpochSecond.getOrDefault(attackerId, 0L), until));
    }
  }

  private boolean isLikelyEnclosed(Player player) {
    if (player == null || player.getWorld() == null) {
      return false;
    }
    Location loc = player.getLocation();
    Block block = loc.getBlock();
    int openSides = 0;
    for (BlockFace face : List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP)) {
      Block adjacent = block.getRelative(face);
      if (adjacent.isPassable()) {
        openSides++;
      }
    }
    return openSides <= 1;
  }

  private boolean isTouchingSolidWall(Player player) {
    if (player == null || player.getWorld() == null) {
      return false;
    }
    Block feet = player.getLocation().getBlock();
    Block body = feet.getRelative(BlockFace.UP);
    for (BlockFace face : List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
      if (!feet.getRelative(face).isPassable() || !body.getRelative(face).isPassable()) {
        return true;
      }
    }
    return false;
  }

  private boolean isOutsideCurrentBorder(Location location) {
    if (location == null) {
      return false;
    }
    double dx = location.getX() - borderCenterX();
    double dz = location.getZ() - borderCenterZ();
    double radius = borderCurrentRadius();
    return (dx * dx) + (dz * dz) > (radius * radius);
  }

  private int collapseNearbyOreVeins(Player player, int tier) {
    if (player == null || player.getWorld() == null) {
      return 0;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    int samples = switch (clampedTier) {
      case 1 -> 24;
      case 2 -> 30;
      case 3 -> 36;
      default -> 42;
    };
    double baseChance = switch (clampedTier) {
      case 1 -> 0.0016D;
      case 2 -> 0.0026D;
      case 3 -> 0.0038D;
      default -> 0.0055D;
    };
    int cap = clampedTier >= 3 ? 2 : 1;
    int radius = 8 + (clampedTier * 2);
    int changed = 0;
    Location origin = player.getLocation();
    for (int i = 0; i < samples && changed < cap; i++) {
      int x = origin.getBlockX() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
      int y = origin.getBlockY() + ThreadLocalRandom.current().nextInt(-12, 13);
      int z = origin.getBlockZ() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
      Block block = player.getWorld().getBlockAt(x, y, z);
      if (block == null) {
        continue;
      }
      Material oreType = block.getType();
      if (!isOreMaterial(oreType)) {
        continue;
      }
      double chance = baseChance * oreCollapseChanceMultiplier(oreType);
      if (ThreadLocalRandom.current().nextDouble() >= chance) {
        continue;
      }
      block.setType(collapseReplacementForOre(oreType), false);
      changed++;
    }
    return changed;
  }

  private double oreCollapseChanceMultiplier(Material material) {
    if (material == null) {
      return 0.5D;
    }
    return switch (material) {
      case ANCIENT_DEBRIS -> 0.03D;
      case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> 0.10D;
      case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> 0.12D;
      case GOLD_ORE, DEEPSLATE_GOLD_ORE, NETHER_GOLD_ORE -> 0.45D;
      case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE, LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> 0.55D;
      case IRON_ORE, DEEPSLATE_IRON_ORE, COPPER_ORE, DEEPSLATE_COPPER_ORE -> 0.75D;
      case COAL_ORE, DEEPSLATE_COAL_ORE, NETHER_QUARTZ_ORE -> 1.00D;
      default -> 0.65D;
    };
  }

  private Material collapseReplacementForOre(Material material) {
    if (material == null) {
      return Material.STONE;
    }
    String name = material.name();
    if (name.contains("NETHER") || material == Material.ANCIENT_DEBRIS) {
      return Material.NETHERRACK;
    }
    if (name.contains("DEEPSLATE")) {
      return Material.DEEPSLATE;
    }
    return Material.STONE;
  }

  private int transmuteNearbyStoneByTierTable(Player player, Location origin, int tier, String effectId) {
    if (player == null || origin == null || origin.getWorld() == null) {
      return 0;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    World world = origin.getWorld();

    int radius = switch (clampedTier) {
      case 1 -> 4;
      case 2 -> 6;
      case 3 -> 8;
      default -> 10;
    };
    int samples = switch (clampedTier) {
      case 1 -> 48;
      case 2 -> 72;
      case 3 -> 108;
      default -> 144;
    };
    int maxChanges = switch (clampedTier) {
      case 1 -> 6;
      case 2 -> 10;
      case 3 -> 14;
      default -> 18;
    };
    double convertChance = switch (clampedTier) {
      case 1 -> 0.10D;
      case 2 -> 0.15D;
      case 3 -> 0.20D;
      default -> 0.25D;
    };

    radius = scaledBModRangeBlocks(effectId, radius);
    samples = Math.max(samples, scaledBModRangeBlocks(effectId, samples));
    maxChanges = Math.max(1, scaledBModRangeBlocks(effectId, maxChanges));

    boolean endTableEnabled = world.getEnvironment() == World.Environment.THE_END && clampedTier >= 3;
    double endActivationChance = clampedTier >= 4 ? 0.75D : 0.50D;

    int changed = 0;
    int ox = origin.getBlockX();
    int oy = origin.getBlockY();
    int oz = origin.getBlockZ();
    int minY = world.getMinHeight();
    int maxY = world.getMaxHeight() - 1;

    for (int i = 0; i < samples && changed < maxChanges; i++) {
      int x = ox + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
      int y = Math.max(minY, Math.min(maxY, oy + ThreadLocalRandom.current().nextInt(-radius, radius + 1)));
      int z = oz + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
      Block target = world.getBlockAt(x, y, z);
      if (target == null || !isNaturalStone(target.getType())) {
        continue;
      }
      if (ThreadLocalRandom.current().nextDouble() >= convertChance) {
        continue;
      }
      if (world.getEnvironment() == World.Environment.THE_END) {
        if (!endTableEnabled) {
          continue;
        }
        if (ThreadLocalRandom.current().nextDouble() >= endActivationChance) {
          continue;
        }
      }
      Material replacement = selectStoneAlchemyTarget(world.getEnvironment(), clampedTier, target.getType());
      if (replacement == null || replacement == target.getType()) {
        continue;
      }
      target.setType(replacement, false);
      changed++;
      if (clampedTier >= 3) {
        world.spawnParticle(Particle.END_ROD, target.getLocation().clone().add(0.5D, 0.6D, 0.5D), 5 + clampedTier, 0.15D, 0.2D, 0.15D, 0.0D);
      }
    }
    return changed;
  }

  private Material selectStoneAlchemyTarget(World.Environment environment, int tier, Material sourceType) {
    if (environment == World.Environment.NETHER) {
      return selectNetherStoneAlchemyTarget(tier);
    }
    if (environment == World.Environment.THE_END) {
      return selectEndStoneAlchemyTarget(tier);
    }
    return mapToDeepslateVariant(selectOverworldStoneAlchemyTarget(tier), sourceType);
  }

  private Material selectOverworldStoneAlchemyTarget(int tier) {
    int clampedTier = Math.max(1, Math.min(4, tier));
    int roll = ThreadLocalRandom.current().nextInt(100) + 1;
    if (clampedTier == 1) {
      if (roll <= 40) return Material.COAL_ORE;
      if (roll <= 65) return Material.IRON_ORE;
      if (roll <= 85) return Material.COPPER_ORE;
      return Material.GOLD_ORE;
    }
    if (clampedTier == 2) {
      if (roll <= 28) return Material.COAL_ORE;
      if (roll <= 50) return Material.IRON_ORE;
      if (roll <= 68) return Material.COPPER_ORE;
      if (roll <= 81) return Material.GOLD_ORE;
      if (roll <= 92) return Material.REDSTONE_ORE;
      return Material.LAPIS_ORE;
    }
    if (clampedTier == 3) {
      if (roll <= 20) return Material.COAL_ORE;
      if (roll <= 38) return Material.IRON_ORE;
      if (roll <= 53) return Material.COPPER_ORE;
      if (roll <= 65) return Material.GOLD_ORE;
      if (roll <= 79) return Material.REDSTONE_ORE;
      if (roll <= 90) return Material.LAPIS_ORE;
      return Material.DIAMOND_ORE;
    }
    if (roll <= 14) return Material.COAL_ORE;
    if (roll <= 31) return Material.IRON_ORE;
    if (roll <= 43) return Material.COPPER_ORE;
    if (roll <= 54) return Material.GOLD_ORE;
    if (roll <= 70) return Material.REDSTONE_ORE;
    if (roll <= 83) return Material.LAPIS_ORE;
    return Material.DIAMOND_ORE;
  }

  private Material selectNetherStoneAlchemyTarget(int tier) {
    int clampedTier = Math.max(1, Math.min(4, tier));
    if (clampedTier <= 2) {
      return Material.NETHER_QUARTZ_ORE;
    }
    int roll = ThreadLocalRandom.current().nextInt(100) + 1;
    if (clampedTier == 3) {
      if (roll <= 10) return Material.NETHER_QUARTZ_ORE;
      if (roll <= 12) return Material.ANCIENT_DEBRIS;
      return null;
    }
    if (roll <= 15) return Material.NETHER_QUARTZ_ORE;
    if (roll <= 19) return Material.ANCIENT_DEBRIS;
    return null;
  }

  private Material selectEndStoneAlchemyTarget(int tier) {
    int clampedTier = Math.max(1, Math.min(4, tier));
    if (clampedTier < 3) {
      return null;
    }
    int roll = ThreadLocalRandom.current().nextInt(100) + 1;
    if (clampedTier == 3) {
      if (roll <= 14) return Material.COAL_ORE;
      if (roll <= 28) return Material.IRON_ORE;
      if (roll <= 39) return Material.COPPER_ORE;
      if (roll <= 47) return Material.GOLD_ORE;
      if (roll <= 57) return Material.REDSTONE_ORE;
      if (roll <= 65) return Material.LAPIS_ORE;
      if (roll <= 74) return Material.DIAMOND_ORE;
      if (roll <= 84) return Material.NETHER_QUARTZ_ORE; // quartz 10%
      if (roll <= 86) return Material.ANCIENT_DEBRIS;
      return null;
    }
    if (roll <= 10) return Material.COAL_ORE;
    if (roll <= 22) return Material.IRON_ORE;
    if (roll <= 31) return Material.COPPER_ORE;
    if (roll <= 38) return Material.GOLD_ORE;
    if (roll <= 50) return Material.REDSTONE_ORE;
    if (roll <= 61) return Material.LAPIS_ORE;
    if (roll <= 82) return Material.DIAMOND_ORE;
    if (roll <= 97) return Material.NETHER_QUARTZ_ORE; // quartz 15%
    return Material.ANCIENT_DEBRIS;
  }

  private Material mapToDeepslateVariant(Material ore, Material sourceType) {
    if (ore == null || sourceType != Material.DEEPSLATE) {
      return ore;
    }
    return switch (ore) {
      case COAL_ORE -> Material.DEEPSLATE_COAL_ORE;
      case IRON_ORE -> Material.DEEPSLATE_IRON_ORE;
      case COPPER_ORE -> Material.DEEPSLATE_COPPER_ORE;
      case GOLD_ORE -> Material.DEEPSLATE_GOLD_ORE;
      case REDSTONE_ORE -> Material.DEEPSLATE_REDSTONE_ORE;
      case LAPIS_ORE -> Material.DEEPSLATE_LAPIS_ORE;
      case DIAMOND_ORE -> Material.DEEPSLATE_DIAMOND_ORE;
      default -> ore;
    };
  }

  private int transmuteNearbyBlocks(Player player, boolean oreToStone, boolean stoneToOre, int tier) {
    int defaultCap = tier >= 4 ? 2 : 1;
    return transmuteNearbyBlocks(player, oreToStone, stoneToOre, tier, defaultCap, null);
  }

  private int transmuteNearbyBlocks(Player player, boolean oreToStone, boolean stoneToOre, int tier, int maxChanges) {
    return transmuteNearbyBlocks(player, oreToStone, stoneToOre, tier, maxChanges, null);
  }

  private int transmuteNearbyBlocks(Player player, boolean oreToStone, boolean stoneToOre, int tier, int maxChanges, String effectId) {
    if (player == null || player.getWorld() == null) {
      return 0;
    }
    if (maxChanges <= 0) {
      return 0;
    }
    World world = player.getWorld();
    int clampedTier = Math.max(1, Math.min(4, tier));
    boolean boostedBMod = isBModEffectId(effectId);
    int samples = switch (clampedTier) {
      case 1 -> 24;
      case 2 -> 30;
      case 3 -> 36;
      default -> 42;
    };
    if (boostedBMod) {
      samples = Math.max(samples, (int) Math.round(samples * B_MOD_RANGE_SCALE));
      maxChanges = Math.max(1, scaledBModRangeBlocks(effectId, maxChanges));
    }
    int changed = 0;
    int radius = boostedBMod
        ? scaledBModRangeBlocks(effectId, 8 + (clampedTier * 2))
        : (8 + (clampedTier * 2));
    Location origin = player.getLocation();
    for (int i = 0; i < samples && changed < maxChanges; i++) {
      int x = origin.getBlockX() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
      int y = origin.getBlockY() + ThreadLocalRandom.current().nextInt(-12, 13);
      int z = origin.getBlockZ() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
      Block block = world.getBlockAt(x, y, z);
      if (block == null) {
        continue;
      }
      Material type = block.getType();
      if (oreToStone && isOreMaterial(type)) {
        block.setType(collapseReplacementForOre(type), false);
        changed++;
        if (boostedBMod) {
          world.spawnParticle(Particle.END_ROD, block.getLocation().clone().add(0.5D, 0.6D, 0.5D), 8 + (clampedTier * 2), 0.25D, 0.25D, 0.25D, 0.01D);
        }
      } else if (stoneToOre && isNaturalStone(type)) {
        block.setType(randomOreMaterialForTier(clampedTier), false);
        changed++;
        if (boostedBMod) {
          world.spawnParticle(Particle.CRIT, block.getLocation().clone().add(0.5D, 0.6D, 0.5D), 7 + (clampedTier * 2), 0.22D, 0.22D, 0.22D, 0.01D);
        }
      }
    }
    if (boostedBMod && changed > 0) {
      world.playSound(origin, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.55F + (clampedTier * 0.08F), 1.10F);
    }
    return changed;
  }

  private int transmuteNetherRiftBlocks(Player player, int tier) {
    if (player == null || player.getWorld() == null) {
      return 0;
    }
    if (player.getWorld().getEnvironment() != World.Environment.NETHER) {
      return 0;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    int samples = switch (clampedTier) {
      case 1 -> 24;
      case 2 -> 30;
      case 3 -> 36;
      default -> 44;
    };
    int cap = switch (clampedTier) {
      case 1 -> 1;
      case 2 -> 2;
      case 3 -> 2;
      default -> 3;
    };
    int radius = 8 + (clampedTier * 2);
    double convertChance = switch (clampedTier) {
      case 1 -> 0.10D;
      case 2 -> 0.14D;
      case 3 -> 0.18D;
      default -> 0.24D;
    };
    int changed = 0;
    World world = player.getWorld();
    Location origin = player.getLocation();
    for (int i = 0; i < samples && changed < cap; i++) {
      int x = origin.getBlockX() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
      int y = origin.getBlockY() + ThreadLocalRandom.current().nextInt(-12, 13);
      int z = origin.getBlockZ() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
      Block block = world.getBlockAt(x, y, z);
      if (block == null || block.getType() != Material.NETHERRACK) {
        continue;
      }
      if (ThreadLocalRandom.current().nextDouble() >= convertChance) {
        continue;
      }
      block.setType(randomNetherRiftOreForTier(clampedTier), false);
      changed++;
    }
    return changed;
  }

  private Material randomNetherRiftOreForTier(int tier) {
    int clampedTier = Math.max(1, Math.min(4, tier));
    double roll = ThreadLocalRandom.current().nextDouble();
    if (clampedTier >= 3) {
      double debrisChance = clampedTier >= 4 ? 0.03D : 0.015D;
      if (roll < debrisChance) {
        return Material.ANCIENT_DEBRIS;
      }
    }
    if (roll < (clampedTier >= 2 ? 0.60D : 0.70D)) {
      return Material.NETHER_QUARTZ_ORE;
    }
    return Material.NETHER_GOLD_ORE;
  }

  private Material randomOreMaterialForTier(int tier) {
    List<Material> ores;
    int clampedTier = Math.max(1, Math.min(4, tier));
    if (clampedTier <= 1) {
      ores = List.of(
          Material.COAL_ORE,
          Material.COPPER_ORE,
          Material.IRON_ORE,
          Material.COAL_ORE,
          Material.COPPER_ORE
      );
    } else if (clampedTier == 2) {
      ores = List.of(
          Material.COAL_ORE,
          Material.COPPER_ORE,
          Material.IRON_ORE,
          Material.REDSTONE_ORE,
          Material.LAPIS_ORE,
          Material.GOLD_ORE
      );
    } else if (clampedTier == 3) {
      ores = List.of(
          Material.IRON_ORE,
          Material.REDSTONE_ORE,
          Material.LAPIS_ORE,
          Material.GOLD_ORE,
          Material.DIAMOND_ORE
      );
    } else {
      ores = List.of(
          Material.IRON_ORE,
          Material.REDSTONE_ORE,
          Material.LAPIS_ORE,
          Material.GOLD_ORE,
          Material.DIAMOND_ORE,
          Material.DIAMOND_ORE,
          Material.EMERALD_ORE
      );
    }
    return ores.get(ThreadLocalRandom.current().nextInt(ores.size()));
  }

  private boolean isOreMaterial(Material material) {
    if (material == null) {
      return false;
    }
    String name = material.name();
    return name.endsWith("_ORE") || material == Material.ANCIENT_DEBRIS;
  }

  private void highlightNearbyTrapCircuitBlocks(Player player, String effectId, int tier) {
    if (player == null || player.getWorld() == null || effectId == null || effectId.isBlank()) {
      return;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    int radius = switch (clampedTier) {
      case 1 -> 8;
      case 2 -> 12;
      case 3 -> 16;
      default -> 24;
    };
    int verticalRange = switch (clampedTier) {
      case 1 -> 4;
      case 2 -> 5;
      case 3 -> 6;
      default -> 8;
    };
    int maxMarkers = switch (clampedTier) {
      case 1 -> 6;
      case 2 -> 10;
      case 3 -> 14;
      default -> 20;
    };
    long scanCooldownSeconds = switch (clampedTier) {
      case 1 -> 4L;
      case 2 -> 3L;
      case 3 -> 2L;
      default -> 1L;
    };
    if (!useEffectCooldown(player.getUniqueId(), effectId, "b015_trap_scan", scanCooldownSeconds)) {
      return;
    }

    List<Block> traps = findNearbyTrapCircuitBlocks(player.getLocation(), radius, verticalRange, maxMarkers);
    if (traps.isEmpty()) {
      if (clampedTier >= 2
          && useEffectCooldown(player.getUniqueId(), effectId, "b015_trap_scan_clear", 8L)) {
        player.sendActionBar(ChatColor.GRAY + "함정 감지: 반응 없음");
      }
      return;
    }

    Location origin = player.getLocation();
    for (Block trap : traps) {
      Location marker = trap.getLocation().clone().add(0.5D, 0.6D, 0.5D);
      int glowCount = switch (clampedTier) {
        case 1 -> 8;
        case 2 -> 10;
        case 3 -> 12;
        default -> 16;
      };
      player.spawnParticle(Particle.GLOW, marker, glowCount, 0.16D, 0.22D, 0.16D, 0.0D);
      if (clampedTier >= 3) {
        player.spawnParticle(Particle.END_ROD, marker, 3 + clampedTier, 0.12D, 0.18D, 0.12D, 0.0D);
      }
    }

    Block nearest = traps.get(0);
    Vector direction = nearest.getLocation().toVector().subtract(origin.toVector());
    double distance = Math.max(0.0D, Math.sqrt(nearest.getLocation().distanceSquared(origin)));
    String summary = "함정 감지: " + traps.size() + "개 / "
        + compassDirectionForPlayer(player, direction) + " / "
        + distanceBandLabel(distance);

    if (clampedTier >= 2) {
      player.sendActionBar(ChatColor.YELLOW + summary);
    } else {
      player.sendActionBar(ChatColor.GRAY + "함정 감지: 근접 반응");
    }
  }

  private List<Block> findNearbyTrapCircuitBlocks(Location origin, int radius, int verticalRange, int limit) {
    if (origin == null || origin.getWorld() == null || radius <= 0 || verticalRange < 0 || limit <= 0) {
      return List.of();
    }
    World world = origin.getWorld();
    int ox = origin.getBlockX();
    int oy = origin.getBlockY();
    int oz = origin.getBlockZ();
    int minY = Math.max(world.getMinHeight(), oy - verticalRange);
    int maxY = Math.min(world.getMaxHeight() - 1, oy + verticalRange);
    int radiusSquared = radius * radius;

    Map<Long, Boolean> chunkLoadedCache = new HashMap<>();
    List<Block> found = new ArrayList<>();

    for (int dx = -radius; dx <= radius; dx++) {
      for (int dz = -radius; dz <= radius; dz++) {
        if ((dx * dx) + (dz * dz) > radiusSquared) {
          continue;
        }
        int x = ox + dx;
        int z = oz + dz;
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        long chunkKey = (((long) chunkX) << 32) ^ (chunkZ & 0xffffffffL);
        boolean loaded = chunkLoadedCache.computeIfAbsent(chunkKey, key -> world.isChunkLoaded(chunkX, chunkZ));
        if (!loaded) {
          continue;
        }
        for (int y = minY; y <= maxY; y++) {
          Block block = world.getBlockAt(x, y, z);
          if (!isTrapCircuitMaterial(block.getType())) {
            continue;
          }
          found.add(block);
        }
      }
    }

    if (found.isEmpty()) {
      return List.of();
    }
    found.sort(Comparator.comparingDouble(block -> block.getLocation().distanceSquared(origin)));
    if (found.size() <= limit) {
      return found;
    }
    return new ArrayList<>(found.subList(0, limit));
  }

  private boolean isTrapCircuitMaterial(Material material) {
    if (material == null || material == Material.AIR) {
      return false;
    }
    String name = material.name();
    if (name.endsWith("_BUTTON") || name.endsWith("_PRESSURE_PLATE")) {
      return true;
    }
    return name.equals("LEVER")
        || name.equals("TRIPWIRE")
        || name.equals("TRIPWIRE_HOOK")
        || name.equals("REDSTONE_WIRE")
        || name.equals("REDSTONE_TORCH")
        || name.equals("REDSTONE_WALL_TORCH")
        || name.equals("REPEATER")
        || name.equals("COMPARATOR")
        || name.equals("OBSERVER")
        || name.equals("TARGET")
        || name.equals("TNT")
        || name.equals("DISPENSER")
        || name.equals("DROPPER")
        || name.equals("DAYLIGHT_DETECTOR")
        || name.equals("SCULK_SENSOR")
        || name.equals("CALIBRATED_SCULK_SENSOR");
  }

  private boolean isNaturalStone(Material material) {
    return material == Material.STONE
        || material == Material.DEEPSLATE
        || material == Material.ANDESITE
        || material == Material.GRANITE
        || material == Material.DIORITE
        || material == Material.NETHERRACK
        || material == Material.BLACKSTONE;
  }

  private boolean isLogMaterial(Material material) {
    if (material == null) {
      return false;
    }
    String name = material.name();
    return name.endsWith("_LOG") || name.endsWith("_STEM");
  }

  private boolean isMushroomCoreMaterial(Material material) {
    if (material == null) {
      return false;
    }
    return material == Material.BROWN_MUSHROOM_BLOCK
        || material == Material.RED_MUSHROOM_BLOCK
        || material == Material.MUSHROOM_STEM;
  }

  private boolean isTreeCapCoreMaterial(Material material, boolean includeMushroomCore) {
    if (isLogMaterial(material)) {
      return true;
    }
    return includeMushroomCore && isMushroomCoreMaterial(material);
  }

  private boolean isTreeCanopyMaterial(Material material) {
    if (material == null) {
      return false;
    }
    String name = material.name();
    return name.endsWith("_LEAVES")
        || material == Material.NETHER_WART_BLOCK
        || material == Material.WARPED_WART_BLOCK
        || material == Material.SHROOMLIGHT;
  }

  private boolean isAxeMaterial(Material material) {
    if (material == null) {
      return false;
    }
    return material.name().endsWith("_AXE");
  }

  private boolean isPickaxeMaterial(Material material) {
    if (material == null) {
      return false;
    }
    return material.name().endsWith("_PICKAXE");
  }

  private boolean isGatheringTool(Material material) {
    if (material == null || material == Material.AIR) {
      return false;
    }
    String name = material.name();
    return name.endsWith("_PICKAXE")
        || name.endsWith("_AXE")
        || name.endsWith("_SHOVEL")
        || name.endsWith("_HOE");
  }

  private boolean isSwordMaterial(Material material) {
    if (material == null) {
      return false;
    }
    return material.name().endsWith("_SWORD");
  }

  private String resolveWeaponMonopolyGroup(ItemStack item) {
    if (item == null || item.getType() == Material.AIR) {
      return "";
    }
    Material material = item.getType();
    if (isSwordMaterial(material)) {
      return "SWORD";
    }
    if (isAxeMaterial(material)) {
      return "AXE";
    }
    if (material == Material.BOW || material == Material.CROSSBOW) {
      return "RANGED";
    }
    if (material == Material.TRIDENT) {
      return "TRIDENT";
    }
    if (isPickaxeMaterial(material) || material.name().endsWith("_SHOVEL") || material.name().endsWith("_HOE")) {
      return "TOOL";
    }
    return "";
  }

  private String weaponMonopolyGroupNameKo(String group) {
    if (group == null || group.isBlank()) {
      return "미지정";
    }
    return switch (group) {
      case "SWORD" -> "검";
      case "AXE" -> "도끼";
      case "RANGED" -> "원거리";
      case "TRIDENT" -> "삼지창";
      case "TOOL" -> "도구";
      default -> group;
    };
  }

  private void breakNearbyLogs(Block origin, int cap, boolean includeMushroomCore, boolean trimCanopy) {
    if (origin == null || origin.getWorld() == null || cap <= 0) {
      return;
    }
    Deque<Block> queue = new ArrayDeque<>();
    Set<String> visited = new HashSet<>();
    List<Block> brokenCoreBlocks = new ArrayList<>();
    queue.add(origin);
    int broken = 0;
    while (!queue.isEmpty() && broken < cap) {
      Block block = queue.removeFirst();
      String key = blockKey(block.getLocation());
      if (!visited.add(key)) {
        continue;
      }
      if (!isTreeCapCoreMaterial(block.getType(), includeMushroomCore)) {
        continue;
      }
      block.breakNaturally();
      brokenCoreBlocks.add(block);
      broken++;
      for (BlockFace face : BlockFace.values()) {
        if (face == BlockFace.SELF) {
          continue;
        }
        Block neighbor = block.getRelative(face);
        if (neighbor != null && isTreeCapCoreMaterial(neighbor.getType(), includeMushroomCore)) {
          queue.addLast(neighbor);
        }
      }
    }
    if (!trimCanopy || brokenCoreBlocks.isEmpty()) {
      return;
    }
    int cleaned = 0;
    int cleanupCap = 32;
    for (Block brokenCore : brokenCoreBlocks) {
      if (cleaned >= cleanupCap) {
        break;
      }
      for (int dx = -2; dx <= 2 && cleaned < cleanupCap; dx++) {
        for (int dy = -1; dy <= 3 && cleaned < cleanupCap; dy++) {
          for (int dz = -2; dz <= 2 && cleaned < cleanupCap; dz++) {
            Block target = brokenCore.getRelative(dx, dy, dz);
            if (target == null || !isTreeCanopyMaterial(target.getType())) {
              continue;
            }
            target.breakNaturally();
            cleaned++;
          }
        }
      }
    }
  }

  private void breakNearbyNaturalStone(Block origin, int cap) {
    if (origin == null || origin.getWorld() == null || cap <= 0) {
      return;
    }
    int broken = 0;
    for (int dx = -2; dx <= 2 && broken < cap; dx++) {
      for (int dy = -2; dy <= 2 && broken < cap; dy++) {
        for (int dz = -2; dz <= 2 && broken < cap; dz++) {
          Block block = origin.getRelative(dx, dy, dz);
          if (block == null || !isNaturalStone(block.getType())) {
            continue;
          }
          block.breakNaturally();
          broken++;
        }
      }
    }
  }

  private int createRockfall(Block origin, int tier) {
    if (origin == null || origin.getWorld() == null) {
      return 0;
    }
    int clampedTier = Math.max(1, Math.min(4, tier));
    int cap = switch (clampedTier) {
      case 1 -> 3;
      case 2 -> 5;
      case 3 -> 6;
      default -> 8;
    };
    int changed = 0;
    List<Block> candidates = new ArrayList<>();
    for (int dx = -1; dx <= 1; dx++) {
      for (int dy = -1; dy <= 1; dy++) {
        for (int dz = -1; dz <= 1; dz++) {
          Block candidate = origin.getRelative(dx, dy, dz);
          if (candidate == null || candidate.equals(origin)) {
            continue;
          }
          if (isNaturalStone(candidate.getType())) {
            candidates.add(candidate);
          }
        }
      }
    }
    Collections.shuffle(candidates, ThreadLocalRandom.current());
    for (Block candidate : candidates) {
      if (changed >= cap) {
        break;
      }
      candidate.setType(Material.GRAVEL, false);
      changed++;
    }
    Block above = origin.getRelative(BlockFace.UP);
    if (changed < (cap + 1) && above.isPassable()) {
      above.setType(Material.GRAVEL, false);
      changed++;
    }
    return changed;
  }

  private void spreadCollapseToAdjacentPlacedBlocks(Block origin, long collapseAtEpochSecond) {
    if (origin == null) {
      return;
    }
    int spreadCount = ThreadLocalRandom.current().nextInt(1, 3);
    List<String> candidates = new ArrayList<>();
    for (BlockFace face : List.of(
        BlockFace.NORTH,
        BlockFace.SOUTH,
        BlockFace.EAST,
        BlockFace.WEST,
        BlockFace.UP,
        BlockFace.DOWN
    )) {
      Block adjacent = origin.getRelative(face);
      if (adjacent == null || adjacent.getType() == Material.AIR || adjacent.getType() == Material.BEDROCK) {
        continue;
      }
      String key = blockKey(adjacent.getLocation());
      if (!collapsingPlacedBlocksUntilEpochSecond.containsKey(key)) {
        continue;
      }
      candidates.add(key);
    }
    Collections.shuffle(candidates, ThreadLocalRandom.current());
    for (int i = 0; i < spreadCount && i < candidates.size(); i++) {
      String key = candidates.get(i);
      long existing = collapsingPlacedBlocksUntilEpochSecond.getOrDefault(key, collapseAtEpochSecond);
      collapsingPlacedBlocksUntilEpochSecond.put(key, Math.min(existing, collapseAtEpochSecond));
      collapsingPlacedBlocksKeepDrop.add(key);
    }
  }

  private void dropCollapsedPlacedBlock(Block block) {
    if (block == null || block.getWorld() == null) {
      return;
    }
    Material type = block.getType();
    if (type == Material.AIR || type == Material.BEDROCK) {
      return;
    }
    if (type.isItem()) {
      block.getWorld().dropItemNaturally(
          block.getLocation().clone().add(0.5D, 0.4D, 0.5D),
          new ItemStack(type)
      );
    }
    block.setType(Material.AIR, false);
  }

  private String blockKey(Location location) {
    if (location == null || location.getWorld() == null) {
      return "";
    }
    return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
  }

  private Block resolveBlockKey(String key) {
    if (key == null || key.isBlank()) {
      return null;
    }
    String[] parts = key.split(":");
    if (parts.length != 4) {
      return null;
    }
    World world = Bukkit.getWorld(parts[0]);
    if (world == null) {
      return null;
    }
    Integer x = parseInt(parts[1]);
    Integer y = parseInt(parts[2]);
    Integer z = parseInt(parts[3]);
    if (x == null || y == null || z == null) {
      return null;
    }
    return world.getBlockAt(x, y, z);
  }

  private void tickDailyCardDraw() {
    if (!cardsEnabled() || !cardsEnabledInCurrentState() || !cardsEffectLogicEnabled()) {
      return;
    }
    if (cardsOneTimeRollEnabled()) {
      return;
    }

    World world = cardsWorld();
    if (world == null) {
      return;
    }
    if (effectDefinitionsById.isEmpty()) {
      reloadEffectCatalog();
    }
    if (blessingEffectsCatalog.isEmpty() || curseEffectsCatalog.isEmpty()) {
      return;
    }

    int nowTick = (int) world.getTime();
    long currentDay = Math.max(0L, world.getFullTime() / 24000L);
    boolean inWindow = isWithinCardDrawWindow(nowTick);
    long maxProcessDay = inWindow ? currentDay : Math.max(-1L, currentDay - 1L);

    long nextDay = lastCardDrawIngameDay + 1L;
    if (nextDay > maxProcessDay) {
      return;
    }

    int processed = 0;
    int maxCatchup = cardsCatchupMaxDaysPerTick();
    while (nextDay <= maxProcessDay && processed < maxCatchup) {
      runDailyCardDraw(nextDay);
      lastCardDrawIngameDay = nextDay;
      nextDay++;
      processed++;
    }

    if (processed > 0) {
      saveState();
    }
  }

  private boolean isWithinCardDrawWindow(int nowTick) {
    int midnightTick = cardsMidnightTick();
    int triggerWindow = cardsTriggerWindowTicks();
    return nowTick >= midnightTick && nowTick <= (midnightTick + triggerWindow);
  }

  private void runDailyCardDraw(long day) {
    if (!cardsEffectLogicEnabled()) {
      return;
    }
    int targetedPlayers = 0;
    long totalSevereBonus = 0L;

    for (UUID participant : participants) {
      PlayerRoundData data = players.get(participant);
      if (data == null) {
        continue;
      }
      if (data.isOut() && !cardsDrawForOutPlayers()) {
        continue;
      }

      syncPlayerSlots(participant, data, false);
      int blessingSlots = data.getBlessingSlotsUnlocked();
      int curseSlots = data.getCurseSlotsUnlocked();
      int bonusDraws = cardsBonusDrawCount(data);
      if (blessingSlots <= 0 && curseSlots <= 0) {
        continue;
      }

      RollOutcome blessingOutcome = processDailyEffectsForType(
          participant,
          data,
          EffectKind.BLESSING,
          day,
          blessingSlots,
          bonusDraws
      );
      RollOutcome curseOutcome = processDailyEffectsForType(
          participant,
          data,
          EffectKind.CURSE,
          day,
          curseSlots,
          bonusDraws
      );

      long severeBonusForPlayer = blessingOutcome.severeBonus() + curseOutcome.severeBonus();
      if (severeBonusForPlayer > 0L) {
        addFlatScore(participant, severeBonusForPlayer);
        totalSevereBonus += severeBonusForPlayer;
      }

      targetedPlayers++;
      Player online = Bukkit.getPlayer(participant);
      if (online != null && online.isOnline()) {
        if (!blessingOutcome.isEmpty()) {
          online.sendMessage("[Season] 축복 추첨: " + blessingOutcome.summary());
        }
        if (!curseOutcome.isEmpty()) {
          online.sendMessage("[Season] 저주 추첨: " + curseOutcome.summary());
        }
        if (severeBonusForPlayer > 0L) {
          online.sendMessage("[Season] 중대 저주 보너스 +" + severeBonusForPlayer + " 점수.");
        }
      }
    }

    if (targetedPlayers > 0) {
      Bukkit.broadcastMessage(
          "[Season] 일일 효과 추첨 완료: 일수=" + day
              + ", 대상=" + targetedPlayers
              + ", 중대보너스=" + totalSevereBonus
      );
    }
  }

  private RollOutcome drawFreshEffectsForType(
      PlayerRoundData data,
      EffectKind kind,
      long day,
      int slotCount
  ) {
    if (data == null || slotCount <= 0) {
      return new RollOutcome(0, 0, 0, 0, 0L, "추첨 대상 슬롯이 없습니다");
    }
    if (!cardsEffectLogicEnabled()) {
      return new RollOutcome(0, 0, 0, 0, 0L, "카드 효과 비활성화");
    }
    if (effectDefinitionsById.isEmpty()) {
      reloadEffectCatalog();
    }

    Map<String, ActiveSeasonEffect> active = kind == EffectKind.BLESSING
        ? data.getBlessingEffects()
        : data.getCurseEffects();
    List<EffectDefinition> pool = kind == EffectKind.BLESSING
        ? blessingEffectsCatalog
        : curseEffectsCatalog;
    if (pool.isEmpty()) {
      return new RollOutcome(0, 0, 0, 0, 0L, "카드 풀 비어 있음");
    }

    int targetSlots = Math.max(0, slotCount);
    List<EffectDefinition> available = new ArrayList<>(pool);
    Map<String, Integer> duplicateDamping = new HashMap<>();
    int added = 0;
    long severeBonus = 0L;
    long expireDay = resolveEffectExpireIngameDay(day);

    while (active.size() < targetSlots && !available.isEmpty()) {
      EffectDefinition drawn = weightedRandomEffectDefinition(
          available,
          duplicateDamping,
          cardsDuplicateWeightScale()
      );
      if (drawn == null) {
        break;
      }
      ActiveSeasonEffect effect = new ActiveSeasonEffect(
          drawn.id(),
          drawn.displayName(),
          1,
          day,
          expireDay,
          drawn.severeCurse(),
          drawn.severeBonusPoints()
      );
      active.put(effect.getId(), effect);
      available.removeIf(definition -> definition.id().equals(effect.getId()));
      duplicateDamping.merge(effect.getId(), 1, Integer::sum);
      added++;
      if (kind == EffectKind.CURSE && effect.isSevereCurse()) {
        severeBonus += severeBonusFor(effect);
      }
    }

    String summary = "신규=" + added + "/" + targetSlots
        + (added < targetSlots ? ", 카드 풀 부족" : "");
    return new RollOutcome(0, added, 0, 0, severeBonus, summary);
  }

  private long resolveEffectExpireIngameDay(long startDay) {
    if (cardsOneTimeRollEnabled() && cardsOneTimeRollNonExpiring()) {
      return Long.MAX_VALUE - 1L;
    }
    return safeAddIngameDays(startDay, cardsEffectDurationDays());
  }

  private long safeAddIngameDays(long baseDay, long addDays) {
    long normalizedBase = Math.max(0L, baseDay);
    long normalizedAdd = Math.max(1L, addDays);
    long maxSafeAdd = (Long.MAX_VALUE - 1L) - normalizedBase;
    if (maxSafeAdd <= 0L) {
      return Long.MAX_VALUE - 1L;
    }
    return normalizedBase + Math.min(normalizedAdd, maxSafeAdd);
  }

  private RollOutcome processDailyEffectsForType(
      UUID playerId,
      PlayerRoundData data,
      EffectKind kind,
      long day,
      int slotCount,
      int bonusDrawCount
  ) {
    Map<String, ActiveSeasonEffect> active = kind == EffectKind.BLESSING
        ? data.getBlessingEffects()
        : data.getCurseEffects();
    if (!cardsEffectLogicEnabled()) {
      int removed = active.size();
      active.clear();
      return new RollOutcome(removed, 0, 0, 0, 0L, "카드 효과 비활성화");
    }
    List<EffectDefinition> pool = kind == EffectKind.BLESSING
        ? blessingEffectsCatalog
        : curseEffectsCatalog;
    if (pool.isEmpty() || slotCount <= 0) {
      int removedExpired = removeExpiredActiveEffects(active, day);
      return new RollOutcome(removedExpired, 0, 0, 0, 0L, "적용 가능한 카드가 없습니다");
    }

    int expired = removeExpiredActiveEffects(active, day);
    int trimmed = cardsTrimToSlotsOnDailyDraw() ? trimToSlotCount(active, slotCount) : 0;

    int added = 0;
    long severeBonus = 0L;
    Set<String> currentIds = new HashSet<>(active.keySet());
    List<EffectDefinition> availableNew = new ArrayList<>();
    for (EffectDefinition definition : pool) {
      if (!currentIds.contains(definition.id())) {
        availableNew.add(definition);
      }
    }

    while (active.size() < slotCount && !availableNew.isEmpty()) {
      EffectDefinition drawn = weightedRandomEffectDefinition(availableNew, Collections.emptyMap(), 1.0D);
      if (drawn == null) {
        break;
      }
      ActiveSeasonEffect effect = new ActiveSeasonEffect(
          drawn.id(),
          drawn.displayName(),
          1,
          day,
          resolveEffectExpireIngameDay(day),
          drawn.severeCurse(),
          drawn.severeBonusPoints()
      );
      active.put(effect.getId(), effect);
      availableNew.removeIf(def -> def.id().equals(effect.getId()));
      added++;
      if (kind == EffectKind.CURSE && effect.isSevereCurse()) {
        severeBonus += severeBonusFor(effect);
      }
    }

    int upgraded = 0;
    int overflowConverted = 0;
    int attempts = Math.max(0, slotCount + bonusDrawCount);
    Map<String, Integer> duplicateDamping = new HashMap<>();

    for (int i = 0; i < attempts; i++) {
      List<ActiveSeasonEffect> candidates = new ArrayList<>();
      for (ActiveSeasonEffect effect : active.values()) {
        if (effect != null && effect.getTier() < cardsMaxTier()) {
          candidates.add(effect);
        }
      }

      if (candidates.isEmpty()) {
        long overflowScore = cardsUpgradeOverflowBonusPoints();
        if (overflowScore > 0L) {
          addFlatScore(playerId, overflowScore);
          overflowConverted++;
        }
        continue;
      }

      ActiveSeasonEffect selected = weightedRandomUpgradeableEffect(candidates, duplicateDamping);
      if (selected == null) {
        continue;
      }

      selected.increaseTier(cardsMaxTier());
      upgraded++;
      duplicateDamping.merge(selected.getId(), 1, Integer::sum);

      if (kind == EffectKind.CURSE && selected.isSevereCurse() && cardsSevereBonusOnUpgrade()) {
        severeBonus += severeBonusFor(selected);
      }
    }

    int totalExpired = expired + trimmed;
    String summary = "만료=" + totalExpired
        + ", 신규=" + added
        + ", 강화=" + upgraded
        + (overflowConverted > 0 ? ", 점수전환=" + overflowConverted : "");

    return new RollOutcome(totalExpired, added, upgraded, overflowConverted, severeBonus, summary);
  }

  private int removeExpiredActiveEffects(Map<String, ActiveSeasonEffect> active, long day) {
    if (active == null || active.isEmpty()) {
      return 0;
    }
    int before = active.size();
    active.entrySet().removeIf(entry -> {
      ActiveSeasonEffect effect = entry.getValue();
      return effect == null || effect.isExpired(day);
    });
    return Math.max(0, before - active.size());
  }

  private int trimToSlotCount(Map<String, ActiveSeasonEffect> active, int slotCount) {
    if (active == null || slotCount < 0 || active.size() <= slotCount) {
      return 0;
    }

    List<ActiveSeasonEffect> ordered = new ArrayList<>();
    for (ActiveSeasonEffect effect : active.values()) {
      if (effect != null) {
        ordered.add(effect);
      }
    }
    ordered.sort((left, right) -> {
      int tierCompare = Integer.compare(left.getTier(), right.getTier());
      if (tierCompare != 0) {
        return tierCompare;
      }
      int expireCompare = Long.compare(left.getExpireIngameDay(), right.getExpireIngameDay());
      if (expireCompare != 0) {
        return expireCompare;
      }
      return left.getId().compareTo(right.getId());
    });

    int removeCount = Math.max(0, active.size() - slotCount);
    int removed = 0;
    for (int i = 0; i < ordered.size() && removed < removeCount; i++) {
      ActiveSeasonEffect target = ordered.get(i);
      if (target == null) {
        continue;
      }
      if (active.remove(target.getId()) != null) {
        removed++;
      }
    }
    return removed;
  }

  private EffectDefinition weightedRandomEffectDefinition(
      List<EffectDefinition> pool,
      Map<String, Integer> duplicateCounts,
      double duplicateScale
  ) {
    if (pool == null || pool.isEmpty()) {
      return null;
    }

    double total = 0.0D;
    for (EffectDefinition definition : pool) {
      int duplicate = Math.max(0, duplicateCounts.getOrDefault(definition.id(), 0));
      double weight = definition.weight() * Math.pow(duplicateScale, duplicate);
      if (weight > 0.0D) {
        total += weight;
      }
    }
    if (total <= 0.0D) {
      return null;
    }

    double needle = ThreadLocalRandom.current().nextDouble(total);
    double cumulative = 0.0D;
    EffectDefinition fallback = null;
    for (EffectDefinition definition : pool) {
      int duplicate = Math.max(0, duplicateCounts.getOrDefault(definition.id(), 0));
      double weight = definition.weight() * Math.pow(duplicateScale, duplicate);
      if (weight <= 0.0D) {
        continue;
      }
      fallback = definition;
      cumulative += weight;
      if (needle <= cumulative) {
        return definition;
      }
    }
    return fallback;
  }

  private ActiveSeasonEffect weightedRandomUpgradeableEffect(
      List<ActiveSeasonEffect> candidates,
      Map<String, Integer> duplicateCounts
  ) {
    if (candidates == null || candidates.isEmpty()) {
      return null;
    }

    double total = 0.0D;
    for (ActiveSeasonEffect effect : candidates) {
      int duplicate = Math.max(0, duplicateCounts.getOrDefault(effect.getId(), 0));
      double tierWeight = Math.max(0.01D, cardsUpgradeTierBiasBase() - effect.getTier());
      double weight = tierWeight * Math.pow(cardsDuplicateWeightScale(), duplicate);
      if (weight > 0.0D) {
        total += weight;
      }
    }
    if (total <= 0.0D) {
      return null;
    }

    double needle = ThreadLocalRandom.current().nextDouble(total);
    double cumulative = 0.0D;
    ActiveSeasonEffect fallback = null;
    for (ActiveSeasonEffect effect : candidates) {
      int duplicate = Math.max(0, duplicateCounts.getOrDefault(effect.getId(), 0));
      double tierWeight = Math.max(0.01D, cardsUpgradeTierBiasBase() - effect.getTier());
      double weight = tierWeight * Math.pow(cardsDuplicateWeightScale(), duplicate);
      if (weight <= 0.0D) {
        continue;
      }
      fallback = effect;
      cumulative += weight;
      if (needle <= cumulative) {
        return effect;
      }
    }
    return fallback;
  }

  private long severeBonusFor(ActiveSeasonEffect effect) {
    if (effect == null) {
      return 0L;
    }
    if (effect.getSevereBonusPoints() > 0L) {
      return effect.getSevereBonusPoints();
    }
    return cardsSevereCurseDefaultBonusPoints();
  }

  private void tickActiveSeasonEffects() {
    if (!cardsEnabled() || !cardsRuntimeEffectsEnabled()) {
      return;
    }
    int everySeconds = cardsRuntimeEffectIntervalSeconds();
    if (everySeconds > 1 && (tickCounter % everySeconds) != 0L) {
      return;
    }

    World world = cardsWorld();
    long currentDay = world == null ? 0L : Math.max(0L, world.getFullTime() / 24000L);
    boolean stateDirty = false;

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!player.isOnline() || player.getGameMode() == GameMode.SPECTATOR) {
        restoreRuntimeAttributeStates(player);
        restoreAbsorptionShieldCapacity(player);
        continue;
      }
      PlayerRoundData data = players.get(player.getUniqueId());
      if (data == null || data.isOut()) {
        restoreRuntimeAttributeStates(player);
        restoreAbsorptionShieldCapacity(player);
        continue;
      }

      if (!cardsOneTimeRollEnabled()) {
        int removed = data.removeExpiredBlessingEffects(currentDay) + data.removeExpiredCurseEffects(currentDay);
        if (removed > 0) {
          stateDirty = true;
        }
      }

      applyRuntimePassiveStates(player, data);
      applyRuntimeGimmicks(player, data);
    }

    if (stateDirty) {
      saveState();
    }
  }

  private void applyRuntimePassiveStates(Player player, PlayerRoundData data) {
    if (player == null || data == null) {
      return;
    }
    EnumMap<RuntimeModifierType, Double> totals = computeRuntimeModifierTotals(data, player.getWorld(), player);
    boolean forceAbsorptionCapacity = hasForcedAbsorptionCapacityGimmick(data);
    double walkRatio = runtimeModifierValue(totals, RuntimeModifierType.WALK_SPEED_RATIO);
    applyRuntimeWalkSpeed(player, walkRatio);
    applyRuntimeAttributeRatio(
        player,
        totals,
        RuntimeModifierType.ATTACK_SPEED_RATIO,
        Attribute.ATTACK_SPEED,
        0.25D,
        40.0D
    );
    applyRuntimeAttributeRatio(
        player,
        totals,
        RuntimeModifierType.STEP_HEIGHT_RATIO,
        Attribute.STEP_HEIGHT,
        0.0D,
        8.0D
    );
    applyRuntimeAttributeRatio(
        player,
        totals,
        RuntimeModifierType.SNEAK_SPEED_RATIO,
        Attribute.SNEAKING_SPEED,
        0.01D,
        4.0D
    );
    applyRuntimeAttributeRatio(
        player,
        totals,
        RuntimeModifierType.BLOCK_BREAK_SPEED_RATIO,
        Attribute.BLOCK_BREAK_SPEED,
        0.05D,
        16.0D
    );
    applyRuntimeAttributeRatio(
        player,
        totals,
        RuntimeModifierType.WATER_MOVEMENT_RATIO,
        Attribute.WATER_MOVEMENT_EFFICIENCY,
        0.0D,
        16.0D
    );
    applyRuntimeAttributeRatio(
        player,
        totals,
        RuntimeModifierType.MAX_HEALTH_RATIO,
        Attribute.MAX_HEALTH,
        2.0D,
        80.0D
    );
    if (!forceAbsorptionCapacity) {
      applyRuntimeAttributeRatio(
          player,
          totals,
          RuntimeModifierType.MAX_ABSORPTION_RATIO,
          Attribute.MAX_ABSORPTION,
          0.0D,
          40.0D
      );
    }
    applyRuntimeAttributeRatio(
        player,
        totals,
        RuntimeModifierType.BURNING_TIME_RATIO,
        Attribute.BURNING_TIME,
        0.0D,
        8.0D
    );
    applyRuntimeAttributeRatio(
        player,
        totals,
        RuntimeModifierType.SAFE_FALL_DISTANCE_RATIO,
        Attribute.SAFE_FALL_DISTANCE,
        0.0D,
        64.0D
    );
  }

  private boolean hasForcedAbsorptionCapacityGimmick(PlayerRoundData data) {
    if (data == null) {
      return false;
    }
    ActiveSeasonEffect absorptionShield = data.getBlessingEffect("B-023");
    return absorptionShield != null && clampTier(absorptionShield.getTier()) > 0;
  }

  private void applyRuntimeGimmicks(Player player, PlayerRoundData data) {
    if (player == null || data == null) {
      return;
    }
    long nowEpochSecond = nowEpochSecond();
    tickEffect80PerPlayer(player, data, nowEpochSecond);
    applyRuntimeEffect80PeriodicGimmicks(player, data, nowEpochSecond);
    applyRuntimeBlessingB023AbsorptionShield(player, data, nowEpochSecond);
    applyRuntimeBlessingB039AuraDetect(player, data);
  }

  private void applyRuntimeEffect80PeriodicGimmicks(Player player, PlayerRoundData data, long nowEpochSecond) {
    if (player == null || data == null) {
      return;
    }
    for (ActiveSeasonEffect effect : collectAllActiveEffects(data)) {
      if (effect == null || effect.getId() == null || effect.getId().isBlank()) {
        continue;
      }
      String normalizedId = normalizeEffectId(effect.getId());
      Effect80Id id = parseEffect80Id(normalizedId);
      if (id == null) {
        continue;
      }
      EffectGimmickProfile profile = effectGimmicksById.get(normalizedId);
      if (id.group() == 'B') {
        applyBlessingEffect80Periodic(player, data, effect, profile, id.index(), nowEpochSecond);
        continue;
      }
      if (id.group() == 'C') {
        applyCurseEffect80Periodic(player, data, effect, profile, id.index(), nowEpochSecond);
        continue;
      }
      if (id.group() == 'X') {
        applyHybridEffect80Periodic(player, data, effect, profile, id.index(), nowEpochSecond);
      }
    }
  }

  private void applyRuntimeBlessingB023AbsorptionShield(Player player, PlayerRoundData data, long nowEpochSecond) {
    if (player == null || data == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    ActiveSeasonEffect effect = data.getBlessingEffect("B-023");
    if (effect == null) {
      restoreAbsorptionShieldCapacity(player);
      return;
    }

    int tier = clampTier(effect.getTier());
    double refillAmount = switch (tier) {
      case 1 -> 5.0D;
      case 2 -> 10.0D;
      case 3 -> 15.0D;
      default -> 20.0D;
    };
    long refillInterval = 10L;
    // Keep a large technical cap so periodic absorption gain works reliably without exposing tiered max-cap options.
    double technicalCapacity = 200.0D;

    ensureAbsorptionShieldCapacity(player, technicalCapacity);
    if (useEffectCooldown(playerId, effect.getId(), "b023_refill", refillInterval)) {
      double currentAbsorption = Math.max(0.0D, player.getAbsorptionAmount());
      if (currentAbsorption + 0.0001D < technicalCapacity) {
        player.setAbsorptionAmount(Math.min(technicalCapacity, currentAbsorption + refillAmount));
      }
    }

    double currentAbsorption = Math.max(0.0D, player.getAbsorptionAmount());
    double lastAbsorption = Math.max(
        0.0D,
        absorptionShieldLastAbsorptionByPlayer.getOrDefault(playerId, currentAbsorption)
    );
    if (tier >= 4 && lastAbsorption > 0.0D && currentAbsorption <= 0.0D) {
      long immuneUntil = nowEpochSecond + 10L;
      knockbackImmuneUntilEpochSecondByPlayer.put(
          playerId,
          Math.max(knockbackImmuneUntilEpochSecondByPlayer.getOrDefault(playerId, 0L), immuneUntil)
      );
    }
    absorptionShieldLastAbsorptionByPlayer.put(playerId, currentAbsorption);
  }

  private void ensureAbsorptionShieldCapacity(Player player, double targetCapacity) {
    if (player == null) {
      return;
    }
    AttributeInstance maxAbsorptionAttribute = player.getAttribute(Attribute.MAX_ABSORPTION);
    if (maxAbsorptionAttribute == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    absorptionShieldOriginalMaxAbsorptionBaseByPlayer.putIfAbsent(playerId, maxAbsorptionAttribute.getBaseValue());
    double clampedTarget = Math.max(0.0D, targetCapacity);
    if (Math.abs(maxAbsorptionAttribute.getBaseValue() - clampedTarget) > 0.0001D) {
      maxAbsorptionAttribute.setBaseValue(clampedTarget);
    }
    if (player.getAbsorptionAmount() > clampedTarget) {
      player.setAbsorptionAmount(clampedTarget);
    }
  }

  private void restoreAbsorptionShieldCapacity(Player player) {
    if (player == null) {
      return;
    }
    UUID playerId = player.getUniqueId();
    absorptionShieldLastAbsorptionByPlayer.remove(playerId);
    AttributeInstance maxAbsorptionAttribute = player.getAttribute(Attribute.MAX_ABSORPTION);
    Double original = absorptionShieldOriginalMaxAbsorptionBaseByPlayer.remove(playerId);
    if (maxAbsorptionAttribute != null && original != null) {
      maxAbsorptionAttribute.setBaseValue(Math.max(0.0D, original));
      double maxAbsorption = Math.max(0.0D, maxAbsorptionAttribute.getValue());
      if (player.getAbsorptionAmount() > maxAbsorption) {
        player.setAbsorptionAmount(maxAbsorption);
      }
    }
  }

  private void applyAbsorptionShieldDamageMitigation(Player victim, PlayerRoundData victimData, EntityDamageEvent event) {
    if (victim == null || victimData == null || event == null) {
      return;
    }
    ActiveSeasonEffect effect = victimData.getBlessingEffect("B-023");
    if (effect == null || victim.getAbsorptionAmount() <= 0.0D) {
      return;
    }
    int tier = clampTier(effect.getTier());
    double extraReduction = switch (tier) {
      case 1 -> 0.10D;
      case 2 -> 0.15D;
      case 3 -> 0.20D;
      default -> 0.25D;
    };
    double multiplier = boundedMultiplier(1.0D - extraReduction, 0.05D, 4.0D);
    if (Math.abs(multiplier - 1.0D) > 0.0001D) {
      event.setDamage(event.getDamage() * multiplier);
    }
  }

  private void applyRuntimeBlessingB039AuraDetect(Player player, PlayerRoundData data) {
    ActiveSeasonEffect effect = data.getBlessingEffect("B-039");
    if (effect == null) {
      return;
    }
    int tier = clampTier(effect.getTier());
    long intervalSeconds = switch (tier) {
      case 1 -> 60L;
      case 2 -> 40L;
      case 3 -> 20L;
      default -> 10L;
    };
    if (!useEffectCooldown(player.getUniqueId(), effect.getId(), "b039_aura_detect", intervalSeconds)) {
      return;
    }
    double radius = 16.0D + (tier * 2.0D);
    int maxTargets = 12 + (tier * 2);
    List<Monster> targets = new ArrayList<>();
    Location origin = player.getLocation();
    for (Entity nearby : player.getNearbyEntities(radius, radius, radius)) {
      if (!(nearby instanceof Monster monster) || monster.isDead() || !monster.isValid()) {
        continue;
      }
      if (!isAuraInfusedMob(monster)) {
        continue;
      }
      targets.add(monster);
    }
    if (targets.isEmpty()) {
      return;
    }
    targets.sort(Comparator.comparingDouble(monster -> monster.getLocation().distanceSquared(origin)));
    if (targets.size() > maxTargets) {
      targets = targets.subList(0, maxTargets);
    }
    for (Monster monster : targets) {
      monster.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, true, true));
    }
  }

  private void applyRuntimeWalkSpeed(Player player, double ratio) {
    if (player == null) {
      return;
    }
    UUID uuid = player.getUniqueId();
    float current = player.getWalkSpeed();
    if (Math.abs(ratio) < 0.0001D) {
      restoreRuntimeWalkSpeed(player);
      return;
    }

    float base = runtimeOriginalWalkSpeeds.computeIfAbsent(uuid, ignored -> current);
    double target = base * (1.0D + ratio);
    target = Math.max(0.05D, Math.min(1.0D, target));
    if (Math.abs(current - target) > 0.0005D) {
      player.setWalkSpeed((float) target);
    }
  }

  private void restoreRuntimeWalkSpeed(Player player) {
    if (player == null) {
      return;
    }
    Float base = runtimeOriginalWalkSpeeds.remove(player.getUniqueId());
    if (base == null) {
      return;
    }
    float restored = Math.max(0.05F, Math.min(1.0F, base));
    if (Math.abs(player.getWalkSpeed() - restored) > 0.0005F) {
      player.setWalkSpeed(restored);
    }
  }

  private void restoreAllRuntimeWalkSpeeds() {
    if (runtimeOriginalWalkSpeeds.isEmpty()) {
      return;
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
      restoreRuntimeWalkSpeed(player);
    }
    runtimeOriginalWalkSpeeds.clear();
  }

  private void applyRuntimeAttributeRatio(
      Player player,
      EnumMap<RuntimeModifierType, Double> totals,
      RuntimeModifierType modifierType,
      Attribute attribute,
      double minValue,
      double maxValue
  ) {
    if (player == null || modifierType == null || attribute == null) {
      return;
    }

    double ratio = runtimeModifierValue(totals, modifierType);
    if (Math.abs(ratio) < 0.0001D) {
      restoreRuntimeAttribute(player, attribute);
      return;
    }

    AttributeInstance instance = player.getAttribute(attribute);
    if (instance == null) {
      return;
    }

    UUID uuid = player.getUniqueId();
    Map<Attribute, Double> originalBases = runtimeOriginalAttributeBases.computeIfAbsent(
        uuid,
        ignored -> new HashMap<>()
    );
    originalBases.putIfAbsent(attribute, instance.getBaseValue());

    double baseline = originalBases.getOrDefault(attribute, instance.getBaseValue());
    if (Math.abs(baseline) < 1.0E-6D) {
      baseline = runtimeAttributeBaseline(instance);
    }
    double target = baseline * boundedMultiplier(1.0D + ratio, 0.0D, 8.0D);
    target = Math.max(minValue, Math.min(maxValue, target));
    if (Math.abs(instance.getBaseValue() - target) > 0.0001D) {
      instance.setBaseValue(target);
      if (attribute == Attribute.MAX_HEALTH) {
        double currentHealth = player.getHealth();
        if (currentHealth > target) {
          player.setHealth(target);
        }
      }
    }
  }

  private double runtimeAttributeBaseline(AttributeInstance instance) {
    if (instance == null) {
      return 1.0D;
    }
    double fallback = instance.getDefaultValue();
    if (Math.abs(fallback) < 1.0E-6D) {
      fallback = 1.0D;
    }
    return fallback;
  }

  private void restoreRuntimeAttribute(Player player, Attribute attribute) {
    if (player == null || attribute == null) {
      return;
    }
    Map<Attribute, Double> originalBases = runtimeOriginalAttributeBases.get(player.getUniqueId());
    if (originalBases == null) {
      return;
    }
    Double original = originalBases.remove(attribute);
    if (original == null) {
      if (originalBases.isEmpty()) {
        runtimeOriginalAttributeBases.remove(player.getUniqueId());
      }
      return;
    }
    AttributeInstance instance = player.getAttribute(attribute);
    if (instance != null && Math.abs(instance.getBaseValue() - original) > 0.0001D) {
      instance.setBaseValue(original);
      if (attribute == Attribute.MAX_HEALTH) {
        double maxHealth = Math.max(1.0D, instance.getValue());
        if (player.getHealth() > maxHealth) {
          player.setHealth(maxHealth);
        }
      }
    }
    if (originalBases.isEmpty()) {
      runtimeOriginalAttributeBases.remove(player.getUniqueId());
    }
  }

  private void restoreRuntimeAttributes(Player player) {
    if (player == null) {
      return;
    }
    Map<Attribute, Double> originalBases = runtimeOriginalAttributeBases.remove(player.getUniqueId());
    if (originalBases == null || originalBases.isEmpty()) {
      return;
    }
    for (Map.Entry<Attribute, Double> entry : originalBases.entrySet()) {
      Attribute attribute = entry.getKey();
      Double value = entry.getValue();
      if (attribute == null || value == null) {
        continue;
      }
      AttributeInstance instance = player.getAttribute(attribute);
      if (instance != null && Math.abs(instance.getBaseValue() - value) > 0.0001D) {
        instance.setBaseValue(value);
      }
    }
    AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
    if (maxHealthAttr != null) {
      double maxHealth = Math.max(1.0D, maxHealthAttr.getValue());
      if (player.getHealth() > maxHealth) {
        player.setHealth(maxHealth);
      }
    }
  }

  private void restoreRuntimeAttributeStates(Player player) {
    restoreRuntimeWalkSpeed(player);
    restoreRuntimeAttributes(player);
  }

  private void restoreAllRuntimeAttributes() {
    if (runtimeOriginalAttributeBases.isEmpty()) {
      return;
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
      restoreRuntimeAttributes(player);
    }
    runtimeOriginalAttributeBases.clear();
  }

  private EnumMap<RuntimeModifierType, Double> computeRuntimeModifierTotals(PlayerRoundData data, World world) {
    return computeRuntimeModifierTotals(data, world, null);
  }

  private EnumMap<RuntimeModifierType, Double> computeRuntimeModifierTotals(
      PlayerRoundData data,
      World world,
      Player player
  ) {
    EnumMap<RuntimeModifierType, Double> totals = new EnumMap<>(RuntimeModifierType.class);
    if (data == null || !cardsEnabled() || !cardsRuntimeEffectsEnabled()) {
      return totals;
    }
    RuntimeArchetypeTotals archetypeTotalsByKind = computeRuntimeArchetypeTotals(data);
    applyRuntimeModifiersForKind(data.getBlessingEffects(), EffectKind.BLESSING, archetypeTotalsByKind, world, player, totals);
    applyRuntimeModifiersForKind(data.getCurseEffects(), EffectKind.CURSE, archetypeTotalsByKind, world, player, totals);
    applyCatalogConditionalRuntimeAdjustments(data, world, player, totals);
    applyAdaptiveCurseRuntimeAdjustments(data, world, player, archetypeTotalsByKind, totals);
    return totals;
  }

  private RuntimeArchetypeTotals computeRuntimeArchetypeTotals(PlayerRoundData data) {
    EnumMap<EffectArchetype, Integer> blessingTotals = new EnumMap<>(EffectArchetype.class);
    EnumMap<EffectArchetype, Integer> curseTotals = new EnumMap<>(EffectArchetype.class);
    if (data == null) {
      return new RuntimeArchetypeTotals(blessingTotals, curseTotals);
    }
    for (ActiveSeasonEffect active : collectAllActiveEffects(data)) {
      if (active == null || active.getId() == null || active.getId().isBlank()) {
        continue;
      }
      EffectDefinition definition = effectDefinitionsById.get(active.getId());
      EffectArchetype archetype = definition == null ? inferArchetype(active.getId()) : definition.archetype();
      EffectKind resolvedKind = resolveRuntimeEffectKind(active, definition, null);
      if (archetype == null || resolvedKind == null) {
        continue;
      }
      EnumMap<EffectArchetype, Integer> target = resolvedKind == EffectKind.CURSE ? curseTotals : blessingTotals;
      target.merge(archetype, clampTier(active.getTier()), Integer::sum);
    }
    return new RuntimeArchetypeTotals(blessingTotals, curseTotals);
  }

  private void applyRuntimeModifiersForKind(
      Map<String, ActiveSeasonEffect> effects,
      EffectKind kind,
      RuntimeArchetypeTotals archetypeTotalsByKind,
      World world,
      Player player,
      EnumMap<RuntimeModifierType, Double> totals
  ) {
    if (effects == null || effects.isEmpty() || archetypeTotalsByKind == null || totals == null) {
      return;
    }

    for (ActiveSeasonEffect active : effects.values()) {
      if (active == null) {
        continue;
      }

      EffectDefinition definition = effectDefinitionsById.get(active.getId());
      EffectArchetype archetype = definition == null ? inferArchetype(active.getId()) : definition.archetype();
      EffectKind resolvedKind = resolveRuntimeEffectKind(active, definition, kind);
      EffectRuntimeProfile runtimeProfile = resolveRuntimeProfile(definition, resolvedKind, archetype);
      if (runtimeProfile == null || runtimeProfile.modifierRules().isEmpty()) {
        continue;
      }

      EnumMap<EffectArchetype, Integer> resolvedArchetypeTotals = resolvedKind == EffectKind.CURSE
          ? archetypeTotalsByKind.curseTotals()
          : archetypeTotalsByKind.blessingTotals();
      int archetypeTotalTier = Math.max(0, resolvedArchetypeTotals.getOrDefault(archetype, 0));
      int cardTier = Math.max(1, active.getTier());

      for (RuntimeModifierRule rule : runtimeProfile.modifierRules()) {
        if (rule == null || rule.type() == null) {
          continue;
        }
        if (!matchesWorldScope(rule.worldScope(), world)) {
          continue;
        }
        if (!matchesRuntimeConditions(rule.conditions(), world, player)) {
          continue;
        }

        int sourceTier = rule.scalingMode() == TierScalingMode.CARD_TIER ? cardTier : archetypeTotalTier;
        Double value = runtimeModifierRuleValue(rule, sourceTier);
        if (value == null || value == 0.0D) {
          continue;
        }
        if (resolvedKind == EffectKind.CURSE && isCursePoolEffectInRange(active.getId())) {
          value *= curseBaselineScale(rule.type());
        }
        if (resolvedKind == EffectKind.BLESSING && isBModEffectId(active.getId())) {
          value *= B_MOD_RUNTIME_RATIO_SCALE;
        }
        double adjusted = applyCatalogBalanceScaling(active.getId(), resolvedKind, rule.type(), value);
        if (resolvedKind == EffectKind.CURSE) {
          adjusted = softenCursePlayerStatReduction(rule.type(), adjusted);
        }
        if (adjusted == 0.0D) {
          continue;
        }
        totals.merge(rule.type(), adjusted, Double::sum);
      }
    }
  }

  private EffectKind resolveRuntimeEffectKind(ActiveSeasonEffect active, EffectDefinition definition, EffectKind fallbackKind) {
    if (definition != null && definition.kind() != null) {
      return definition.kind();
    }
    NumericPoolEffectId poolId = active == null ? null : parseNumericPoolEffectId(active.getId());
    if (poolId != null) {
      return poolId.group() == 'C' ? EffectKind.CURSE : EffectKind.BLESSING;
    }
    return fallbackKind;
  }

  private boolean matchesWorldScope(RuntimeWorldScope scope, World world) {
    if (scope == null || scope == RuntimeWorldScope.ANY || world == null) {
      return true;
    }
    World.Environment environment = world.getEnvironment();
    return switch (scope) {
      case ANY -> true;
      case OVERWORLD -> environment == World.Environment.NORMAL;
      case NETHER -> environment == World.Environment.NETHER;
      case END -> environment == World.Environment.THE_END;
    };
  }

  private boolean matchesRuntimeConditions(
      Set<RuntimeCondition> conditions,
      World world,
      Player player
  ) {
    if (conditions == null || conditions.isEmpty()) {
      return true;
    }
    for (RuntimeCondition condition : conditions) {
      if (!matchesRuntimeCondition(condition, world, player)) {
        return false;
      }
    }
    return true;
  }

  private boolean matchesRuntimeCondition(
      RuntimeCondition condition,
      World world,
      Player player
  ) {
    if (condition == null) {
      return true;
    }
    if (player == null || !player.isOnline()) {
      return false;
    }
    return switch (condition) {
      case SNEAKING -> player.isSneaking();
      case SPRINTING -> player.isSprinting();
      case BLOCKING -> player.isBlocking();
      case ON_ICE -> isOnIcySurface(player);
      case IN_COLD_BIOME -> isInColdBiome(player);
      case IN_HOT_BIOME -> isInHotBiome(player);
      case IN_CAVE_BIOME -> isInCaveBiome(player);
      case OUTSIDE_BORDER -> isOutsideCurrentBorder(player.getLocation());
      case INSIDE_BORDER -> !isOutsideCurrentBorder(player.getLocation());
      case LOW_HEALTH_30 -> playerHealthRatio(player) <= 0.30D;
      case LOW_HEALTH_35 -> playerHealthRatio(player) <= 0.35D;
      case HAS_POSITIVE_EFFECT -> hasPositivePotionEffect(player);
      case MAIN_HAND_EMPTY -> isMainHandEmpty(player);
      case MAIN_HAND_AXE -> isMainHandAxe(player);
      case MAIN_HAND_TRIDENT_LIKE -> isMainHandTridentLike(player);
      case MAIN_HAND_ENDER_PEARL -> isMainHandEnderPearl(player);
      case NEAR_STRONGHOLD_500 -> isNearStronghold(player, 500.0D);
    };
  }

  private void applyCatalogConditionalRuntimeAdjustments(
      PlayerRoundData data,
      World world,
      Player player,
      EnumMap<RuntimeModifierType, Double> totals
  ) {
    if (data == null || player == null || totals == null) {
      return;
    }
    applyRhythmComboRuntimeAdjustment(data, player, totals);
    applyScoreEngineRankRuntimeAdjustment(data, player, totals);
  }

  private void applyAdaptiveCurseRuntimeAdjustments(
      PlayerRoundData data,
      World world,
      Player player,
      RuntimeArchetypeTotals archetypeTotalsByKind,
      EnumMap<RuntimeModifierType, Double> totals
  ) {
    if (data == null || world == null || player == null || archetypeTotalsByKind == null || totals == null) {
      return;
    }
    EnumMap<EffectArchetype, Integer> curseTotals = archetypeTotalsByKind.curseTotals();
    if (curseTotals == null || curseTotals.isEmpty()) {
      return;
    }

    int totalCurseTier = 0;
    for (Integer tier : curseTotals.values()) {
      if (tier != null && tier > 0) {
        totalCurseTier += tier;
      }
    }
    if (totalCurseTier <= 0) {
      return;
    }

    UUID playerId = player.getUniqueId();
    boolean inCombat = isPlayerInRecentCombat(playerId, 12L);
    double healthRatio = playerHealthRatio(player);
    Material mainHandType = player.getInventory().getItemInMainHand() == null
        ? Material.AIR
        : player.getInventory().getItemInMainHand().getType();

    for (Map.Entry<EffectArchetype, Integer> entry : curseTotals.entrySet()) {
      EffectArchetype archetype = entry.getKey();
      int tierSum = entry.getValue() == null ? 0 : entry.getValue();
      if (archetype == null || tierSum <= 0) {
        continue;
      }
      double scale = Math.min(1.0D, tierSum / 8.0D);
      switch (archetype) {
        case MOBILITY -> {
          if (player.isSprinting() || isOnIcySurface(player) || player.getFallDistance() > 2.0F) {
            totals.merge(RuntimeModifierType.WALK_SPEED_RATIO, -0.010D * scale, Double::sum);
          }
          if (player.getFallDistance() > 2.0F) {
            totals.merge(RuntimeModifierType.FALL_DAMAGE_RATIO, 0.015D * scale, Double::sum);
          }
          if (player.isInWater() && player.getRemainingAir() < (int) (player.getMaximumAir() * 0.6D)) {
            totals.merge(RuntimeModifierType.OXYGEN_DRAIN_RATIO, 0.010D * scale, Double::sum);
          }
        }
        case COMBAT -> {
          if (inCombat) {
            totals.merge(RuntimeModifierType.DAMAGE_DEALT_RATIO, -0.012D * scale, Double::sum);
            totals.merge(RuntimeModifierType.PVP_DAMAGE_DEALT_RATIO, -0.010D * scale, Double::sum);
            totals.merge(RuntimeModifierType.MOB_DAMAGE_DEALT_RATIO, -0.010D * scale, Double::sum);
          }
          if (healthRatio <= 0.50D) {
            totals.merge(RuntimeModifierType.DAMAGE_TAKEN_RATIO, 0.010D * scale, Double::sum);
          }
        }
        case DEFENSE -> {
          if (healthRatio <= 0.35D) {
            totals.merge(RuntimeModifierType.DAMAGE_TAKEN_RATIO, 0.018D * scale, Double::sum);
          }
          if (player.isBlocking()) {
            totals.merge(RuntimeModifierType.SHIELD_BLOCK_RATIO, -0.012D * scale, Double::sum);
          }
        }
        case GATHERING -> {
          if (isGatheringTool(mainHandType)) {
            totals.merge(RuntimeModifierType.BLOCK_BREAK_SPEED_RATIO, -0.015D * scale, Double::sum);
            totals.merge(RuntimeModifierType.ITEM_DURABILITY_LOSS_RATIO, 0.012D * scale, Double::sum);
          }
          if (inCombat) {
            totals.merge(RuntimeModifierType.BLOCK_BREAK_SPEED_RATIO, -0.008D * scale, Double::sum);
          }
        }
        case SCORE -> {
          long score = Math.max(0L, data.getScore());
          if (score > 0L) {
            double scorePressure = Math.min(1.0D, score / 30000.0D);
            totals.merge(RuntimeModifierType.SCORE_DECAY_PER_MINUTE_RATIO, 0.010D * scale * scorePressure, Double::sum);
            if (scorePressure >= 0.40D) {
              totals.merge(RuntimeModifierType.SURVIVAL_SCORE_RATIO, -0.008D * scale, Double::sum);
            }
          }
        }
        case SURVIVAL -> {
          if (player.isSprinting() || player.getFoodLevel() <= 10) {
            totals.merge(RuntimeModifierType.HUNGER_DRAIN_RATIO, 0.016D * scale, Double::sum);
          }
          if (player.getFoodLevel() <= 8) {
            totals.merge(RuntimeModifierType.NATURAL_REGEN_RATIO, -0.010D * scale, Double::sum);
          }
        }
        case UTILITY -> {
          if (hasPositivePotionEffect(player)) {
            totals.merge(RuntimeModifierType.WEAKNESS_DURATION_RATIO, 0.010D * scale, Double::sum);
            totals.merge(RuntimeModifierType.GLOWING_DURATION_RATIO, 0.008D * scale, Double::sum);
          }
          if (isMainHandEmpty(player)) {
            totals.merge(RuntimeModifierType.PVP_STEAL_TAKEN_RATIO, 0.008D * scale, Double::sum);
          }
        }
        case ENDGAME -> {
          if (world.getEnvironment() == World.Environment.THE_END) {
            totals.merge(RuntimeModifierType.END_DAMAGE_TAKEN_RATIO, 0.018D * scale, Double::sum);
            totals.merge(RuntimeModifierType.DRAGON_DAMAGE_RATIO, -0.012D * scale, Double::sum);
            totals.merge(RuntimeModifierType.WITHER_DAMAGE_TAKEN_RATIO, 0.010D * scale, Double::sum);
          } else if (world.getEnvironment() == World.Environment.NETHER) {
            totals.merge(RuntimeModifierType.BORDER_WITHER_RATIO, 0.010D * scale, Double::sum);
          }
          if (isOutsideCurrentBorder(player.getLocation())) {
            totals.merge(RuntimeModifierType.BORDER_WITHER_RATIO, 0.012D * scale, Double::sum);
          }
        }
      }
    }

    if (totalCurseTier >= 12 && inCombat) {
      double pressure = Math.min(0.015D, (totalCurseTier - 11) * 0.0015D);
      totals.merge(RuntimeModifierType.DAMAGE_TAKEN_RATIO, pressure, Double::sum);
    }
  }

  private boolean isCursePoolEffectInRange(String effectId) {
    NumericPoolEffectId poolId = parseNumericPoolEffectId(effectId);
    return poolId != null && poolId.group() == 'C' && poolId.index() >= 1 && poolId.index() <= 120;
  }

  private double curseBaselineScale(RuntimeModifierType type) {
    if (type == null) {
      return 0.75D;
    }
    return switch (type) {
      case DAMAGE_TAKEN_RATIO,
           FALL_DAMAGE_RATIO,
           PROJECTILE_DAMAGE_TAKEN_RATIO,
           EXPLOSION_DAMAGE_TAKEN_RATIO,
           FIRE_DAMAGE_TAKEN_RATIO,
           WITHER_DAMAGE_TAKEN_RATIO,
           END_DAMAGE_TAKEN_RATIO,
           PVP_DAMAGE_TAKEN_RATIO,
           LOW_HEALTH_DAMAGE_TAKEN_RATIO -> 0.65D;
      case DAMAGE_DEALT_RATIO,
           PVP_DAMAGE_DEALT_RATIO,
           MOB_DAMAGE_DEALT_RATIO,
           WALK_SPEED_RATIO,
           ATTACK_SPEED_RATIO,
           BLOCK_BREAK_SPEED_RATIO,
           NATURAL_REGEN_RATIO,
           HUNGER_DRAIN_RATIO,
           ITEM_DURABILITY_LOSS_RATIO,
           STARVATION_DAMAGE_RATIO -> 0.72D;
      case SCORE_DECAY_PER_MINUTE_RATIO,
           SURVIVAL_SCORE_RATIO,
           MINING_SCORE_RATIO,
           MOB_SCORE_RATIO,
           DRAGON_JACKPOT_RATIO -> 0.78D;
      default -> 0.80D;
    };
  }

  private void applyRhythmComboRuntimeAdjustment(
      PlayerRoundData data,
      Player player,
      EnumMap<RuntimeModifierType, Double> totals
  ) {
    if (data == null || player == null || totals == null) {
      return;
    }
    ActiveSeasonEffect rhythm = data.getBlessingEffect("B-017");
    if (rhythm == null) {
      return;
    }
    int tier = clampTier(rhythm.getTier());
    int stacks = currentRhythmComboStacks(player.getUniqueId());
    double stackFactorOffset = stacks - 1.0D;
    totals.merge(
        RuntimeModifierType.ATTACK_SPEED_RATIO,
        b017AttackSpeedBaseRatio(tier) * stackFactorOffset,
        Double::sum
    );
    totals.merge(
        RuntimeModifierType.PVP_DAMAGE_DEALT_RATIO,
        b017PvpBaseRatio(tier) * stackFactorOffset,
        Double::sum
    );
    totals.merge(
        RuntimeModifierType.MOB_DAMAGE_DEALT_RATIO,
        b017MobBaseRatio(tier) * stackFactorOffset,
        Double::sum
    );
  }

  private void applyScoreEngineRankRuntimeAdjustment(
      PlayerRoundData data,
      Player player,
      EnumMap<RuntimeModifierType, Double> totals
  ) {
    if (data == null || player == null || totals == null) {
      return;
    }
    ActiveSeasonEffect scoreEngine = data.getBlessingEffect("B-061");
    if (scoreEngine == null) {
      return;
    }
    int tier = clampTier(scoreEngine.getTier());
    double rankMultiplier = survivalScoreRankMultiplier(player.getUniqueId());
    if (rankMultiplier <= 1.0001D) {
      return;
    }
    double basePerTier = 0.10D;
    double baseContribution = basePerTier * tier;
    double bonus = baseContribution * (rankMultiplier - 1.0D);
    if (bonus == 0.0D) {
      return;
    }
    totals.merge(RuntimeModifierType.ATTACK_SPEED_RATIO, bonus, Double::sum);
    totals.merge(RuntimeModifierType.WALK_SPEED_RATIO, bonus, Double::sum);
  }

  private double survivalScoreRankMultiplier(UUID playerId) {
    if (playerId == null) {
      return 1.0D;
    }
    List<Map.Entry<UUID, PlayerRoundData>> ranking = new ArrayList<>();
    for (Map.Entry<UUID, PlayerRoundData> entry : players.entrySet()) {
      UUID uuid = entry.getKey();
      PlayerRoundData data = entry.getValue();
      Player online = Bukkit.getPlayer(uuid);
      if (online == null || !online.isOnline() || online.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }
      if (data != null && data.isOut()) {
        continue;
      }
      ranking.add(entry);
    }
    if (ranking.size() <= 1) {
      return 1.0D;
    }
    ranking.sort((left, right) -> {
      long leftScore = left.getValue() == null ? 0L : left.getValue().getScore();
      long rightScore = right.getValue() == null ? 0L : right.getValue().getScore();
      return Long.compare(rightScore, leftScore);
    });
    int index = -1;
    for (int i = 0; i < ranking.size(); i++) {
      if (playerId.equals(ranking.get(i).getKey())) {
        index = i;
        break;
      }
    }
    if (index < 0) {
      return 1.0D;
    }
    double normalized = 1.0D - (index / (double) (ranking.size() - 1));
    normalized = Math.max(0.0D, Math.min(1.0D, normalized));
    return 1.0D + (normalized * 2.0D);
  }

  private int currentRhythmComboStacks(UUID playerId) {
    if (playerId == null) {
      return 0;
    }
    long until = rhythmComboExpireEpochMilliByPlayer.getOrDefault(playerId, 0L);
    if (until <= 0L || until < System.currentTimeMillis()) {
      rhythmComboExpireEpochMilliByPlayer.remove(playerId);
      rhythmComboStacksByPlayer.remove(playerId);
      return 0;
    }
    return Math.max(0, Math.min(RHYTHM_COMBO_MAX_STACKS, rhythmComboStacksByPlayer.getOrDefault(playerId, 0)));
  }

  private void registerRhythmComboHit(Player attacker) {
    if (attacker == null) {
      return;
    }
    UUID playerId = attacker.getUniqueId();
    int current = currentRhythmComboStacks(playerId);
    int next = Math.max(1, Math.min(RHYTHM_COMBO_MAX_STACKS, current + 1));
    rhythmComboStacksByPlayer.put(playerId, next);
    rhythmComboExpireEpochMilliByPlayer.put(playerId, System.currentTimeMillis() + RHYTHM_COMBO_WINDOW_MILLIS);
  }

  private double b017AttackSpeedBaseRatio(int tier) {
    int clamped = Math.max(1, Math.min(4, tier));
    return 0.20D * clamped;
  }

  private double b017PvpBaseRatio(int tier) {
    int clamped = Math.max(1, Math.min(4, tier));
    return 0.03D * clamped;
  }

  private double b017MobBaseRatio(int tier) {
    int clamped = Math.max(1, Math.min(4, tier));
    return 0.06D * clamped;
  }




  private Double runtimeModifierRuleValue(RuntimeModifierRule rule, int sourceTier) {
    if (rule == null || sourceTier <= 0) {
      return null;
    }
    int minTier = Math.max(1, rule.minTotalTier());
    if (sourceTier < minTier) {
      return null;
    }
    double scaledTier = sourceTier;
    if (rule.scalingMode() == TierScalingMode.CARD_TIER) {
      scaledTier = switch (Math.max(1, Math.min(4, sourceTier))) {
        case 1 -> 1.0D;
        case 2 -> 2.0D;
        case 3 -> 3.6D;
        default -> 5.2D;
      };
    }
    return rule.valuePerTier() * scaledTier;
  }

  private double applyCatalogBalanceScaling(
      String effectId,
      EffectKind kind,
      RuntimeModifierType type,
      double value
  ) {
    if (type == null || value == 0.0D) {
      return value;
    }
    if (kind == EffectKind.CURSE) {
      double scaled = value * CURSE_NEGATIVE_SOFTEN_SCALE;
      if (isCModEffectId(effectId)) {
        scaled *= C_MOD_RUNTIME_RATIO_SCALE;
      } else if (isXModCurseVariant(effectId)) {
        scaled *= X_MOD_CURSE_VARIANT_RUNTIME_RATIO_SCALE;
      }
      return scaled;
    }
    if (kind != EffectKind.BLESSING) {
      return value;
    }
    if (!isBlessingPoolEffectInRange(effectId, 1, 120)) {
      return value;
    }

    if (value > 0.0D) {
      if (isAggressiveGrowthModifierType(type)) {
        return value * BLESSING_POOL_AGGRESSIVE_SCALE;
      }
      return value * BLESSING_POOL_GENERAL_SCALE;
    }
    if (value < 0.0D && isReductionStyleModifierType(type)) {
      return value * BLESSING_POOL_GENERAL_SCALE;
    }
    return value;
  }

  private double softenCursePlayerStatReduction(RuntimeModifierType type, double value) {
    if (value >= 0.0D || !isCursePlayerStatReductionType(type)) {
      return value;
    }
    return value * CURSE_PLAYER_STAT_REDUCTION_SOFTEN_SCALE;
  }

  private boolean isCursePlayerStatReductionType(RuntimeModifierType type) {
    if (type == null) {
      return false;
    }
    return switch (type) {
      case DAMAGE_DEALT_RATIO,
          PVP_DAMAGE_DEALT_RATIO,
          MOB_DAMAGE_DEALT_RATIO,
          LOW_HEALTH_DAMAGE_DEALT_RATIO,
          WALK_SPEED_RATIO,
          ATTACK_SPEED_RATIO,
          STEP_HEIGHT_RATIO,
          SNEAK_SPEED_RATIO,
          BLOCK_BREAK_SPEED_RATIO,
          WATER_MOVEMENT_RATIO,
          MAX_HEALTH_RATIO,
          MAX_ABSORPTION_RATIO,
          NATURAL_REGEN_RATIO,
          FOOD_GAIN_RATIO -> true;
      default -> false;
    };
  }

  private boolean isBlessingPoolEffectInRange(String effectId, int min, int max) {
    NumericPoolEffectId poolId = parseNumericPoolEffectId(effectId);
    if (poolId == null || poolId.group() != 'B') {
      return false;
    }
    int index = poolId.index();
    return index >= Math.max(1, min) && index <= Math.max(min, max);
  }

  private boolean isBlessingPoolRuntimeRange(int index) {
    return index >= 1 && index <= 120;
  }

  private boolean isCursePoolRuntimeRange(int index) {
    return index >= 1 && index <= 120;
  }

  private boolean isAggressiveGrowthModifierType(RuntimeModifierType type) {
    if (type == null) {
      return false;
    }
    return switch (type) {
      case WALK_SPEED_RATIO,
          ATTACK_SPEED_RATIO,
          SNEAK_SPEED_RATIO,
          STEP_HEIGHT_RATIO,
          WATER_MOVEMENT_RATIO,
          BLOCK_BREAK_SPEED_RATIO,
          DAMAGE_DEALT_RATIO,
          PVP_DAMAGE_DEALT_RATIO,
          MOB_DAMAGE_DEALT_RATIO,
          LOW_HEALTH_DAMAGE_DEALT_RATIO -> true;
      default -> false;
    };
  }

  private boolean isReductionStyleModifierType(RuntimeModifierType type) {
    if (type == null) {
      return false;
    }
    return switch (type) {
      case DAMAGE_TAKEN_RATIO,
          FALL_DAMAGE_RATIO,
          PROJECTILE_DAMAGE_TAKEN_RATIO,
          EXPLOSION_DAMAGE_TAKEN_RATIO,
          FIRE_DAMAGE_TAKEN_RATIO,
          WITHER_DAMAGE_TAKEN_RATIO,
          END_DAMAGE_TAKEN_RATIO,
          LOW_HEALTH_DAMAGE_TAKEN_RATIO,
          KNOCKBACK_TAKEN_RATIO,
          BURNING_TIME_RATIO,
          OXYGEN_DRAIN_RATIO,
          HUNGER_DRAIN_RATIO,
          ITEM_DURABILITY_LOSS_RATIO,
          DARKNESS_DURATION_RATIO,
          BLINDNESS_DURATION_RATIO,
          GLOWING_DURATION_RATIO,
          WEAKNESS_DURATION_RATIO,
          WITHER_DURATION_RATIO,
          POISON_DURATION_RATIO -> true;
      default -> false;
    };
  }

  private double runtimeModifierValue(EnumMap<RuntimeModifierType, Double> totals, RuntimeModifierType type) {
    if (totals == null || type == null) {
      return 0.0D;
    }
    return totals.getOrDefault(type, 0.0D);
  }

  private double boundedMultiplier(double value, double min, double max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }

  private void applySingleHitDamageCap(EntityDamageEvent event, Player victim, double ratio) {
    if (event == null || victim == null) {
      return;
    }
    double cappedRatio = Math.max(0.05D, Math.min(1.0D, ratio));
    double maxHealth = playerMaxHealth(victim);
    double maxDamage = Math.max(1.0D, maxHealth * cappedRatio);
    double current = event.getDamage();
    if (Double.isFinite(current) && current > maxDamage) {
      event.setDamage(maxDamage);
    }
  }

  private double playerMaxHealth(Player player) {
    if (player == null) {
      return 20.0D;
    }
    AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
    if (maxHealth == null) {
      return Math.max(1.0D, player.getMaxHealth());
    }
    return Math.max(1.0D, maxHealth.getValue());
  }

  private boolean isProjectileDamageCause(EntityDamageEvent.DamageCause cause) {
    return cause == EntityDamageEvent.DamageCause.PROJECTILE;
  }

  private boolean isExplosionDamageCause(EntityDamageEvent.DamageCause cause) {
    return cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
        || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;
  }

  private boolean isFireDamageCause(EntityDamageEvent.DamageCause cause) {
    return cause == EntityDamageEvent.DamageCause.FIRE
        || cause == EntityDamageEvent.DamageCause.FIRE_TICK
        || cause == EntityDamageEvent.DamageCause.HOT_FLOOR
        || cause == EntityDamageEvent.DamageCause.LAVA;
  }

  private RuntimeModifierType runtimeDurationModifierType(PotionEffectType potionEffectType) {
    if (potionEffectType == null) {
      return null;
    }
    if (potionEffectType.equals(PotionEffectType.DARKNESS)) {
      return RuntimeModifierType.DARKNESS_DURATION_RATIO;
    }
    if (potionEffectType.equals(PotionEffectType.BLINDNESS)) {
      return RuntimeModifierType.BLINDNESS_DURATION_RATIO;
    }
    if (potionEffectType.equals(PotionEffectType.GLOWING)) {
      return RuntimeModifierType.GLOWING_DURATION_RATIO;
    }
    if (potionEffectType.equals(PotionEffectType.WEAKNESS)) {
      return RuntimeModifierType.WEAKNESS_DURATION_RATIO;
    }
    if (potionEffectType.equals(PotionEffectType.WITHER)) {
      return RuntimeModifierType.WITHER_DURATION_RATIO;
    }
    if (potionEffectType.equals(PotionEffectType.POISON)) {
      return RuntimeModifierType.POISON_DURATION_RATIO;
    }
    return null;
  }

  private String potionGuardKey(UUID playerUuid, PotionEffectType potionEffectType) {
    if (playerUuid == null || potionEffectType == null || potionEffectType.getKey() == null) {
      return "";
    }
    return playerUuid + "|" + potionEffectType.getKey().toString();
  }

  private double averageRuntimeModifierAcrossParticipants(RuntimeModifierType modifierType) {
    if (modifierType == null) {
      return 0.0D;
    }
    double total = 0.0D;
    int count = 0;
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }
      PlayerRoundData data = players.get(player.getUniqueId());
      if (data == null || data.isOut()) {
        continue;
      }
      total += runtimeModifierValue(computeRuntimeModifierTotals(data, player.getWorld(), player), modifierType);
      count++;
    }
    return count <= 0 ? 0.0D : (total / count);
  }

  private int highestHybridTierAcrossParticipants(int index) {
    if (index <= 0) {
      return 0;
    }
    int best = 0;
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player == null || !player.isOnline() || player.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }
      PlayerRoundData data = players.get(player.getUniqueId());
      if (data == null || data.isOut()) {
        continue;
      }
      best = Math.max(best, highestEffect80Tier(data, 'X', index));
    }
    return best;
  }

  private long applyRuntimeScoreModifier(
      long points,
      PlayerRoundData data,
      World world,
      RuntimeModifierType modifierType
  ) {
    if (!cardsEffectLogicEnabled() || points <= 0L || data == null || modifierType == null) {
      return points;
    }
    Player contextPlayer = resolveOnlinePlayerByRoundData(data);
    double ratio = runtimeModifierValue(computeRuntimeModifierTotals(data, world, contextPlayer), modifierType);
    double multiplier = boundedMultiplier(1.0D + ratio, 0.1D, 5.0D);
    long adjusted = (long) Math.floor(points * multiplier);
    return Math.max(1L, adjusted);
  }

  private Player resolveOnlinePlayerByRoundData(PlayerRoundData data) {
    if (data == null) {
      return null;
    }
    for (Map.Entry<UUID, PlayerRoundData> entry : players.entrySet()) {
      if (entry.getValue() != data) {
        continue;
      }
      Player player = Bukkit.getPlayer(entry.getKey());
      if (player != null && player.isOnline()) {
        return player;
      }
      return null;
    }
    return null;
  }

  private void tickBorderWitherHazard() {
    if (!borderEnabled()) {
      borderOutsideSinceEpochSecond.clear();
      return;
    }
    World borderWorld = borderWorld();
    if (borderWorld == null) {
      borderOutsideSinceEpochSecond.clear();
      return;
    }

    long now = nowEpochSecond();
    double centerX = borderCenterX();
    double centerZ = borderCenterZ();
    double radius = borderCurrentRadius();
    int witherMaxLevel = borderWitherMaxLevel();
    int stepSeconds = borderWitherStepSeconds();

    Set<UUID> online = new HashSet<>();

    for (Player player : Bukkit.getOnlinePlayers()) {
      UUID uuid = player.getUniqueId();
      online.add(uuid);

      if (player.getWorld() != borderWorld) {
        borderOutsideSinceEpochSecond.remove(uuid);
        continue;
      }

      if (player.getGameMode() == GameMode.SPECTATOR) {
        borderOutsideSinceEpochSecond.remove(uuid);
        continue;
      }

      double dx = player.getLocation().getX() - centerX;
      double dz = player.getLocation().getZ() - centerZ;
      double distance = Math.hypot(dx, dz);
      PlayerRoundData data = players.get(uuid);
      EnumMap<RuntimeModifierType, Double> runtimeTotals = null;
      if (data != null && !data.isOut()) {
        runtimeTotals = computeRuntimeModifierTotals(data, player.getWorld(), player);
      }
      int c14Tier = highestEffect80Tier(data, 'C', 14);
      int x13Tier = highestEffect80Tier(data, 'X', 13);
      int curseExtraLevel = switch (Math.max(0, c14Tier)) {
        case 0 -> 0;
        case 1 -> 2;
        default -> 4;
      };
      int hybridExtraLevel = switch (Math.max(0, x13Tier)) {
        case 0 -> 0;
        case 1 -> 1;
        case 2 -> 2;
        case 3 -> 3;
        default -> 4;
      };

      if (distance <= radius) {
        borderOutsideSinceEpochSecond.remove(uuid);
        borderGamblerNextScoreEpochSecondByPlayer.remove(uuid);
        if (c14Tier >= 3 && distance >= Math.max(0.0D, radius - 5.0D)) {
          player.addPotionEffect(new PotionEffect(
              PotionEffectType.WITHER,
              40,
              0,
              true,
              false,
              true
          ));
        }
        continue;
      }

      long outsideSince = borderOutsideSinceEpochSecond.computeIfAbsent(uuid, ignored -> now);
      long elapsed = Math.max(0L, now - outsideSince);
      if (c14Tier >= 4) {
        elapsed = (long) Math.floor(elapsed * 1.5D);
      }
      if (x13Tier > 0) {
        long nextScoreAt = borderGamblerNextScoreEpochSecondByPlayer.getOrDefault(uuid, 0L);
        if (nextScoreAt <= now) {
          double bonusRatio = switch (x13Tier) {
            case 1 -> 0.25D;
            case 2 -> 0.40D;
            case 3 -> 0.60D;
            default -> 0.90D;
          };
          long basePoints = 6L;
          long bonusPoints = Math.max(1L, Math.round(basePoints * bonusRatio));
          addGeneratedScore(uuid, basePoints + bonusPoints);
          borderGamblerNextScoreEpochSecondByPlayer.put(uuid, now + 2L);
          if (x13Tier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, true, true, true));
          }
          if (x13Tier >= 4) {
            player.setAbsorptionAmount(Math.max(0.0D, player.getAbsorptionAmount() * 0.80D));
          }
        }
      }
      int level = Math.min(witherMaxLevel, (int) (elapsed / stepSeconds) + 1);
      level = Math.min(witherMaxLevel, level + curseExtraLevel + hybridExtraLevel);
      if (runtimeTotals != null) {
        double borderRatio = runtimeModifierValue(runtimeTotals, RuntimeModifierType.BORDER_WITHER_RATIO);
        double borderMultiplier = boundedMultiplier(1.0D + borderRatio, 0.1D, 4.0D);
        level = Math.max(1, Math.min(witherMaxLevel, (int) Math.round(level * borderMultiplier)));
      }

      player.addPotionEffect(new PotionEffect(
          PotionEffectType.WITHER,
          40,
          Math.max(0, level - 1),
          true,
          false,
          true
      ));
      applyBorderWitherExtraDamage(player, level, runtimeTotals);
    }

    borderOutsideSinceEpochSecond.keySet().removeIf(uuid -> !online.contains(uuid));
  }

  private void applyBorderWitherExtraDamage(
      Player player,
      int witherLevel,
      EnumMap<RuntimeModifierType, Double> runtimeTotals
  ) {
    if (player == null || witherLevel <= 0 || player.isDead() || player.getHealth() <= 0.0D) {
      return;
    }
    double perLevelDamage = borderWitherExtraDamagePerLevelPerSecond();
    if (perLevelDamage <= 0.0D) {
      return;
    }

    double extraDamage = Math.max(0.1D, witherLevel * perLevelDamage);
    if (runtimeTotals != null) {
      double witherTakenRatio = runtimeModifierValue(runtimeTotals, RuntimeModifierType.WITHER_DAMAGE_TAKEN_RATIO);
      double witherTakenMultiplier = boundedMultiplier(1.0D + witherTakenRatio, 0.05D, 5.0D);
      extraDamage *= witherTakenMultiplier;
    }

    if (extraDamage <= 0.0D || !Double.isFinite(extraDamage)) {
      return;
    }

    // Border hazard extra tick should always apply once per second regardless of recent hit i-frames.
    if (player.getNoDamageTicks() > 0) {
      player.setNoDamageTicks(0);
    }
    player.damage(extraDamage);
  }

  private void tickBorderParticles() {
    if (!borderParticlesEnabled()) {
      return;
    }

    World world = borderWorld();
    if (world == null) {
      return;
    }

    double centerX = borderCenterX();
    double centerZ = borderCenterZ();
    double radius = borderCurrentRadius();
    double insideRenderDistance = borderParticleRenderDistance();
    double outsideRenderDistance = borderParticleOutsideRenderDistance();
    double insideArcRadians = Math.toRadians(borderParticleArcDegrees());
    double outsideArcRadians = Math.toRadians(borderParticleOutsideArcDegrees());
    double stepRadians = Math.toRadians(borderParticleAngleStepDegrees());
    double wallHeight = borderParticleWallHeight();
    double verticalStep = borderParticleVerticalStep();
    int primaryCount = borderParticleCountPerPoint();
    boolean primaryDustEnabled = borderParticlePrimaryDustEnabled();
    boolean accentEnabled = borderParticleAccentEnabled();
    int accentCount = borderParticleAccentCountPerPoint();
    int accentAngleStride = borderParticleAccentAngleStride();
    int accentVerticalStride = borderParticleAccentVerticalStride();
    boolean outsideIgnoreFacing = borderParticleOutsideIgnoreFacing();
    boolean forceParticles = borderParticleForce();
    Particle.DustOptions primaryDust = new Particle.DustOptions(
        Color.fromRGB(borderParticlePrimaryRed(), borderParticlePrimaryGreen(), borderParticlePrimaryBlue()),
        (float) borderParticlePrimarySize()
    );
    Particle primaryParticle = primaryDustEnabled ? Particle.DUST : Particle.ELECTRIC_SPARK;
    double primaryOffsetX = primaryDustEnabled ? 0.055D : 0.03D;
    double primaryOffsetY = primaryDustEnabled ? 0.085D : 0.06D;
    double primaryOffsetZ = primaryDustEnabled ? 0.055D : 0.03D;
    Object primaryParticleData = primaryDustEnabled ? primaryDust : null;

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getWorld() != world || player.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }

      double px = player.getLocation().getX();
      double pz = player.getLocation().getZ();
      double distanceFromCenter = Math.hypot(px - centerX, pz - centerZ);
      boolean outside = distanceFromCenter > radius;
      double activeRenderDistance = outside ? outsideRenderDistance : insideRenderDistance;
      if (Math.abs(distanceFromCenter - radius) > activeRenderDistance) {
        continue;
      }

      Vector view = player.getEyeLocation().getDirection();
      double viewX = view.getX();
      double viewZ = view.getZ();
      double viewLength = Math.hypot(viewX, viewZ);
      if (viewLength < 1.0E-6D) {
        continue;
      }
      viewX /= viewLength;
      viewZ /= viewLength;

      double facing = Math.atan2(viewZ, viewX);
      double activeArc = outside ? outsideArcRadians : insideArcRadians;
      double start = facing - (activeArc / 2.0D);
      double end = facing + (activeArc / 2.0D);

      double yStart = Math.max(world.getMinHeight() + 1.0D, player.getLocation().getY() - (wallHeight / 2.0D));
      double yEnd = Math.min(world.getMaxHeight() - 1.0D, yStart + wallHeight);

      int angleIndex = 0;
      for (double angle = start; angle <= end; angle += stepRadians) {
        double borderX = centerX + (Math.cos(angle) * radius);
        double borderZ = centerZ + (Math.sin(angle) * radius);
        double dx = borderX - px;
        double dz = borderZ - pz;

        if (Math.hypot(dx, dz) > activeRenderDistance) {
          continue;
        }
        if ((!outside || !outsideIgnoreFacing) && (dx * viewX) + (dz * viewZ) <= 0.0D) {
          continue;
        }

        int verticalIndex = 0;
        for (double y = yStart; y <= yEnd; y += verticalStep) {
          spawnParticleSafe(
              world,
              primaryParticle,
              new Location(world, borderX, y, borderZ),
              primaryCount,
              primaryOffsetX,
              primaryOffsetY,
              primaryOffsetZ,
              0.0D,
              primaryParticleData,
              forceParticles
          );

          if (accentEnabled
              && (angleIndex % accentAngleStride == 0)
              && (verticalIndex % accentVerticalStride == 0)) {
            spawnParticleSafe(
                world,
                Particle.END_ROD,
                new Location(world, borderX, y, borderZ),
                accentCount,
                0.02D,
                0.045D,
                0.02D,
                0.0D,
                null,
                forceParticles
            );
          }
          verticalIndex++;
        }
        angleIndex++;
      }
    }
  }

  private void tickEnderAura() {
    boolean enabled = enderAuraEnabled() && enderAuraEnabledInCurrentState();
    if (!enabled) {
      clearAllAuraMonsterAttributeModifiers();
      return;
    }
    cleanupStaleAuraMonsterAttributeModifiers();

    List<Player> candidates = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getGameMode() != GameMode.SPECTATOR) {
        candidates.add(player);
      }
    }
    if (candidates.isEmpty()) {
      auraPlayerCursor = 0;
      return;
    }

    int auraLevel = enderAuraLevel();
    if (auraLevel != lastEnderAuraLevel) {
      getLogger().info("Ender aura level changed -> " + auraLevel + " (totalScore=" + totalRoundScore() + ")");
      lastEnderAuraLevel = auraLevel;
    }

    int playersPerTick = Math.min(candidates.size(), enderAuraPlayersPerTick());
    if (auraPlayerCursor >= candidates.size()) {
      auraPlayerCursor = 0;
    }

    Set<UUID> scannedEndermen = new HashSet<>();
    int maxEndermen = enderAuraMaxEndermenPerTick();
    double scanRadius = enderAuraPlayerScanRadius();

    for (int i = 0; i < playersPerTick; i++) {
      int idx = (auraPlayerCursor + i) % candidates.size();
      Player player = candidates.get(idx);
      for (Entity nearby : player.getNearbyEntities(scanRadius, scanRadius, scanRadius)) {
        if (!(nearby instanceof Enderman enderman) || !enderman.isValid() || enderman.isDead()) {
          continue;
        }
        scannedEndermen.add(enderman.getUniqueId());
        if (scannedEndermen.size() >= maxEndermen) {
          break;
        }
      }
      if (scannedEndermen.size() >= maxEndermen) {
        break;
      }
    }

    auraPlayerCursor = (auraPlayerCursor + playersPerTick) % candidates.size();
    if (scannedEndermen.isEmpty()) {
      return;
    }

    int speedAmplifier = enderAuraSpeedAmplifier(auraLevel);
    int strengthAmplifier = enderAuraStrengthAmplifier(auraLevel);
    double auraPowerRatio = averageRuntimeModifierAcrossParticipants(RuntimeModifierType.AURA_MONSTER_POWER_RATIO)
        + auraResonanceGlobalAuraPowerBonus();
    double auraPowerMultiplier = boundedMultiplier(1.0D + auraPowerRatio, 0.1D, 4.0D);
    if (speedAmplifier >= 0) {
      speedAmplifier = Math.max(0, (int) Math.round(speedAmplifier * auraPowerMultiplier));
    }
    if (strengthAmplifier >= 0) {
      strengthAmplifier = Math.max(0, (int) Math.round(strengthAmplifier * auraPowerMultiplier));
    }
    if (speedAmplifier < 0 && strengthAmplifier < 0) {
      return;
    }

    long auraTick = tickCounter;
    int durationTicks = enderAuraEffectDurationTicks();
    double auraRadius = enderAuraRadius();

    for (UUID endermanId : scannedEndermen) {
      Entity source = Bukkit.getEntity(endermanId);
      if (!(source instanceof Enderman enderman) || !enderman.isValid() || enderman.isDead()) {
        continue;
      }

      for (Entity nearby : enderman.getNearbyEntities(auraRadius, auraRadius, auraRadius)) {
        if (!(nearby instanceof Monster monster) || monster instanceof Enderman || monster.isDead() || !monster.isValid()) {
          continue;
        }
        if (!markAuraAppliedThisTick(monster, auraTick)) {
          continue;
        }

        if (speedAmplifier >= 0) {
          monster.addPotionEffect(new PotionEffect(
              PotionEffectType.SPEED,
              durationTicks,
              speedAmplifier,
              true,
              false,
              true
          ));
        }
        if (strengthAmplifier >= 0) {
          monster.addPotionEffect(new PotionEffect(
              PotionEffectType.STRENGTH,
              durationTicks,
              strengthAmplifier,
              true,
              false,
              true
          ));
        }
        applyAuraMonsterAttributeModifiers(monster, auraPowerMultiplier);
      }
    }
  }

  private boolean markAuraAppliedThisTick(Monster monster, long auraTick) {
    if (monster.hasMetadata(ENDER_AURA_TICK_METADATA)) {
      for (MetadataValue value : monster.getMetadata(ENDER_AURA_TICK_METADATA)) {
        if (value.getOwningPlugin() == this && value.asLong() == auraTick) {
          return false;
        }
      }
    }
    monster.setMetadata(ENDER_AURA_TICK_METADATA, new FixedMetadataValue(this, auraTick));
    return true;
  }

  private void bootstrapTaggedSeasonEntities() {
    stalkerEntities.clear();
    raidEntityOrder.clear();
    for (World world : Bukkit.getWorlds()) {
      for (Entity entity : world.getEntities()) {
        if (entity.getScoreboardTags().contains(STALKER_TAG)) {
          stalkerEntities.add(entity.getUniqueId());
        }
        if (entity.getScoreboardTags().contains(RAID_TAG)) {
          raidEntityOrder.addLast(entity.getUniqueId());
        }
      }
    }
  }

  private void cleanupStalkerEntities() {
    stalkerEntities.removeIf(uuid -> {
      Entity entity = Bukkit.getEntity(uuid);
      return entity == null
          || !entity.isValid()
          || entity.isDead()
          || !entity.getScoreboardTags().contains(STALKER_TAG);
    });
  }

  private void tickStalkerSystem() {
    if (!stalkerEnabled() || !stalkerEnabledInCurrentState()) {
      return;
    }

    cleanupStalkerEntities();
    long now = nowEpochSecond();
    if (nextStalkerSpawnEpochSecond <= 0L) {
      nextStalkerSpawnEpochSecond = now + stalkerFirstSpawnDelaySeconds();
      return;
    }
    List<Player> targets = playableOnlineParticipants();
    if (targets.isEmpty()) {
      nextStalkerSpawnEpochSecond = now + 30L;
      return;
    }

    if (now < nextStalkerSpawnEpochSecond) {
      long secondsUntil = Math.max(0L, nextStalkerSpawnEpochSecond - now);
      for (Player target : targets) {
        if (target == null || !target.isOnline()) {
          continue;
        }
        UUID playerId = target.getUniqueId();
        PlayerRoundData data = players.get(playerId);
        int obsessionTier = highestEffect80Tier(data, 'C', 13);
        if (obsessionTier >= 1) {
          // C13-T1: stalker warning removed.
          continue;
        }
        int tier = highestEffect80Tier(data, 'B', 18);
        if (tier <= 0) {
          continue;
        }
        long warningSeconds = switch (tier) {
          case 1 -> 10L;
          case 2 -> 12L;
          case 3 -> 14L;
          default -> 16L;
        };
        if (secondsUntil > warningSeconds) {
          continue;
        }
        if (stalkerSpawnWarnedForEpochSecondByPlayer.getOrDefault(playerId, -1L) == nextStalkerSpawnEpochSecond) {
          continue;
        }
        stalkerSpawnWarnedForEpochSecondByPlayer.put(playerId, nextStalkerSpawnEpochSecond);
        int slowAmp = switch (tier) {
          case 1 -> 0;
          case 2 -> 1;
          case 3 -> 3;
          default -> 4;
        };
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, slowAmp, true, false, true));
        emitBModProcFeedback(target, tier, false);
        target.sendActionBar(ChatColor.DARK_RED + "추격자 출현 예고: " + secondsUntil + "s");
      }
      return;
    }

    int effectiveMaxActive = effectiveStalkerMaxActive(targets);
    if (stalkerEntities.size() >= effectiveMaxActive) {
      int maxObsessionTier = 0;
      int maxHybridBountyTier = 0;
      int maxRaiderScentTier = 0;
      for (Player candidate : targets) {
        if (candidate == null || !candidate.isOnline()) {
          continue;
        }
        PlayerRoundData candidateData = players.get(candidate.getUniqueId());
        maxObsessionTier = Math.max(maxObsessionTier, highestEffect80Tier(candidateData, 'C', 13));
        maxHybridBountyTier = Math.max(maxHybridBountyTier, highestEffect80Tier(candidateData, 'X', 11));
        maxRaiderScentTier = Math.max(maxRaiderScentTier, highestEffect80Tier(candidateData, 'X', 19));
      }
      int empowerGain = maxObsessionTier >= 4 ? 2 : 1;
      if (maxHybridBountyTier >= 3) {
        empowerGain++;
      }
      if (maxRaiderScentTier >= 3) {
        empowerGain++;
      }
      stalkerEmpowerStacks = Math.min(stalkerMaxEmpowerStacks(), stalkerEmpowerStacks + empowerGain);
      for (UUID stalkerId : stalkerEntities) {
        Entity entity = Bukkit.getEntity(stalkerId);
        if (entity instanceof Enderman stalker && stalker.isValid() && !stalker.isDead()) {
          applyStalkerBuffs(stalker, stalkerEmpowerStacks, null);
        }
      }
      Bukkit.broadcastMessage("[Season] Stalkers are empowered. stack=" + stalkerEmpowerStacks);
      scheduleNextStalkerSpawn(now);
      saveState();
      playStalkerEmpowerVisuals(stalkerEmpowerStacks);
      return;
    }

    Player target = selectStalkerTarget(targets);
    if (target == null) {
      scheduleNextStalkerSpawn(now);
      return;
    }
    Enderman spawned = spawnStalkerNear(target);
    if (spawned != null) {
      stalkerEntities.add(spawned.getUniqueId());
      Bukkit.broadcastMessage("[Season] A stalker has appeared near " + target.getName() + ".");
    }
    scheduleNextStalkerSpawn(now);
    saveState();
    if (spawned != null) {
      playStalkerSpawnVisuals(target, spawned.getLocation());
    }
  }

  private Player selectStalkerTarget(List<Player> targets) {
    if (targets == null || targets.isEmpty()) {
      return null;
    }
    double totalWeight = 0.0D;
    List<Double> weights = new ArrayList<>(targets.size());
    for (Player target : targets) {
      if (target == null || !target.isOnline()) {
        weights.add(0.0D);
        continue;
      }
      PlayerRoundData data = players.get(target.getUniqueId());
      int c13Tier = highestEffect80Tier(data, 'C', 13);
      int x11Tier = highestEffect80Tier(data, 'X', 11);
      int x19Tier = highestEffect80Tier(data, 'X', 19);
      double weight = 1.0D;
      if (c13Tier >= 3) {
        weight = c13Tier >= 4 ? 3.0D : 2.2D;
      }
      if (x11Tier >= 1) {
        weight *= switch (x11Tier) {
          case 1 -> 1.15D;
          case 2 -> 1.30D;
          case 3 -> 1.55D;
          default -> 1.90D;
        };
      }
      if (x19Tier >= 3) {
        weight *= x19Tier >= 4 ? 1.65D : 1.35D;
      }
      weights.add(weight);
      totalWeight += weight;
    }
    if (totalWeight <= 0.0D) {
      return targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
    }
    double needle = ThreadLocalRandom.current().nextDouble(totalWeight);
    double cumulative = 0.0D;
    for (int i = 0; i < targets.size(); i++) {
      double weight = weights.get(i);
      if (weight <= 0.0D) {
        continue;
      }
      cumulative += weight;
      if (needle <= cumulative) {
        return targets.get(i);
      }
    }
    return targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
  }

  private int effectiveStalkerMaxActive(List<Player> targets) {
    int base = stalkerMaxActive();
    int maxTier = 0;
    if (targets != null) {
      for (Player target : targets) {
        if (target == null || !target.isOnline()) {
          continue;
        }
        PlayerRoundData data = players.get(target.getUniqueId());
        maxTier = Math.max(maxTier, highestEffect80Tier(data, 'B', 18));
      }
    }
    int reduction = switch (maxTier) {
      case 2 -> 1;
      case 3, 4 -> 2;
      default -> 0;
    };
    return Math.max(1, base - reduction);
  }

  private Enderman spawnStalkerNear(Player target) {
    int c13Tier = highestEffect80Tier(players.get(target.getUniqueId()), 'C', 13);
    Location spawn = stalkerSpawnLocation(target, c13Tier);
    if (spawn == null) {
      return null;
    }

    Entity created = target.getWorld().spawnEntity(spawn, EntityType.ENDERMAN);
    if (!(created instanceof Enderman stalker)) {
      created.remove();
      return null;
    }

    stalker.addScoreboardTag(STALKER_TAG);
    stalker.setPersistent(true);
    stalker.setRemoveWhenFarAway(false);
    stalker.setTarget(target);
    int effectiveEmpowerStacks = stalkerEmpowerStacks + (c13Tier >= 4 ? 1 : 0);
    applyStalkerBuffs(stalker, effectiveEmpowerStacks, target);
    int surrenderTier = highestEffect80Tier(players.get(target.getUniqueId()), 'B', 18);
    if (surrenderTier > 0) {
      int slowAmp = switch (Math.max(1, Math.min(4, surrenderTier))) {
        case 1 -> 0;
        case 2 -> 1;
        case 3 -> 3;
        default -> 4;
      };
      stalker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, slowAmp, true, true, true));
    }
    return stalker;
  }

  private Location stalkerSpawnLocation(Player target, int curseTier) {
    World world = target.getWorld();
    Location base = target.getLocation();

    Vector facing = base.getDirection().setY(0.0D);
    if (facing.lengthSquared() < 1.0E-6D) {
      double random = ThreadLocalRandom.current().nextDouble(0.0D, Math.PI * 2.0D);
      facing = new Vector(Math.cos(random), 0.0D, Math.sin(random));
    } else {
      facing.normalize();
    }

    double behindAngle = Math.atan2(-facing.getZ(), -facing.getX());
    double spread = Math.toRadians(stalkerSpawnAngleSpreadDegrees());
    double angle = behindAngle + ThreadLocalRandom.current().nextDouble(-spread, spread);
    double minDistance = stalkerSpawnDistanceMin();
    double maxDistance = stalkerSpawnDistanceMax();
    if (curseTier >= 1) {
      minDistance = Math.max(3.0D, minDistance * 0.72D);
      maxDistance = Math.max(minDistance + 1.0D, maxDistance * 0.72D);
    }
    double distance = randomDouble(minDistance, maxDistance);

    double x = base.getX() + (Math.cos(angle) * distance);
    double z = base.getZ() + (Math.sin(angle) * distance);
    int highestY = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z));
    double y = Math.max(base.getY(), highestY + 1.0D);
    y = Math.max(world.getMinHeight() + 1.0D, Math.min(world.getMaxHeight() - 2.0D, y));
    return new Location(world, x, y, z, base.getYaw(), base.getPitch());
  }

  private void applyStalkerBuffs(Enderman stalker, int empowerStacks, Player preferredTarget) {
    int speedAmp = stalkerBaseSpeedAmplifier() + (empowerStacks * stalkerEmpowerAmplifierPerStack());
    int strengthAmp = stalkerBaseStrengthAmplifier() + (empowerStacks * stalkerEmpowerAmplifierPerStack());
    int duration = stalkerBuffDurationTicks();

    if (speedAmp >= 0) {
      stalker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, speedAmp, true, false, true));
    }
    if (strengthAmp >= 0) {
      stalker.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, strengthAmp, true, false, true));
    }
    if (preferredTarget != null) {
      stalker.setTarget(preferredTarget);
    }
  }

  private void playStalkerSpawnVisuals(Player target, Location spawnLocation) {
    if (!stalkerVisualEnabled() || target == null || !target.isOnline()) {
      return;
    }

    World world = spawnLocation == null ? null : spawnLocation.getWorld();
    if (world != null) {
      Location center = spawnLocation.clone().add(0.0D, 1.0D, 0.0D);
      int spawnCount = stalkerVisualSpawnParticleCount();
      spawnParticleSafe(
          world,
          Particle.PORTAL,
          center,
          spawnCount,
          0.8D,
          1.0D,
          0.8D,
          0.2D
      );
      spawnParticleSafe(
          world,
          Particle.DRAGON_BREATH,
          center,
          Math.max(8, spawnCount / 4),
          0.4D,
          0.8D,
          0.4D,
          0.01D
      );
    }

    target.sendTitle(
        "Stalker Incoming",
        "Watch your back",
        stalkerVisualTitleFadeInTicks(),
        stalkerVisualTitleStayTicks(),
        stalkerVisualTitleFadeOutTicks()
    );
    target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1.0F, 0.9F);
    target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 0.7F, 1.15F);
  }

  private void playStalkerEmpowerVisuals(int stack) {
    if (!stalkerVisualEnabled()) {
      return;
    }

    for (UUID stalkerId : stalkerEntities) {
      Entity entity = Bukkit.getEntity(stalkerId);
      if (!(entity instanceof Enderman stalker) || !stalker.isValid() || stalker.isDead()) {
        continue;
      }

      Location center = stalker.getLocation().clone().add(0.0D, 1.2D, 0.0D);
      World world = center.getWorld();
      if (world == null) {
        continue;
      }

      int empowerCount = stalkerVisualEmpowerParticleCount();
      spawnParticleSafe(
          world,
          Particle.DRAGON_BREATH,
          center,
          empowerCount,
          0.6D,
          0.9D,
          0.6D,
          0.02D
      );
      spawnParticleSafe(
          world,
          Particle.END_ROD,
          center,
          Math.max(8, empowerCount / 3),
          0.4D,
          0.6D,
          0.4D,
          0.0D
      );
    }

    for (Player player : playableOnlineParticipants()) {
      player.sendTitle(
          "Stalkers Empowered",
          "Stack " + Math.max(0, stack),
          stalkerVisualTitleFadeInTicks(),
          stalkerVisualTitleStayTicks(),
          stalkerVisualTitleFadeOutTicks()
      );
      player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 0.7F, 0.75F);
    }
  }

  private void playDragonPhaseTransitionVisuals(EnderDragon dragon, int phase) {
    if (!dragonRaidVisualEnabled() || dragon == null || dragon.isDead() || !dragon.isValid()) {
      return;
    }

    World world = dragon.getWorld();
    Location center = dragon.getLocation().clone().add(0.0D, 2.0D, 0.0D);
    int particleCount = dragonRaidVisualPhaseParticleCount(phase);
    Particle particle = switch (phase) {
      case 1 -> Particle.END_ROD;
      case 2 -> Particle.FLAME;
      case 3 -> Particle.DRAGON_BREATH;
      default -> Particle.END_ROD;
    };
    Sound phaseSound = switch (phase) {
      case 1 -> Sound.ENTITY_ENDER_DRAGON_AMBIENT;
      case 2 -> Sound.ENTITY_WITHER_SPAWN;
      case 3 -> Sound.ENTITY_ENDER_DRAGON_GROWL;
      default -> Sound.ENTITY_ENDER_DRAGON_AMBIENT;
    };
    String subtitle = switch (phase) {
      case 1 -> "Raid mobs begin to appear";
      case 2 -> "Blaze pressure increased";
      case 3 -> "Vex assault started";
      default -> "Unknown phase";
    };

    spawnParticleSafe(
        world,
        particle,
        center,
        particleCount,
        2.0D,
        1.5D,
        2.0D,
        phase == 3 ? 0.02D : 0.0D
    );
    world.playSound(center, phaseSound, 1.2F, 1.0F);

    for (Player player : endRaidTargets(world)) {
      player.sendTitle(
          "Dragon Phase " + phase,
          subtitle,
          dragonRaidVisualTitleFadeInTicks(),
          dragonRaidVisualTitleStayTicks(),
          dragonRaidVisualTitleFadeOutTicks()
      );
      player.playSound(player.getLocation(), phaseSound, 0.9F, 1.0F);
    }
  }

  private void scheduleNextStalkerSpawn(long now) {
    int min = stalkerRespawnIntervalMinSeconds();
    int max = stalkerRespawnIntervalMaxSeconds();
    int delay = randomIntInclusive(min, max);
    double intervalRatio = averageRuntimeModifierAcrossParticipants(RuntimeModifierType.STALKER_SPAWN_INTERVAL_RATIO);
    double intervalMultiplier = boundedMultiplier(1.0D + intervalRatio, 0.2D, 4.0D);
    delay = Math.max(1, (int) Math.round(delay * intervalMultiplier));
    int bountyTier = highestHybridTierAcrossParticipants(11);
    if (bountyTier > 0) {
      double bountyMultiplier = switch (bountyTier) {
        case 1 -> 0.90D;
        case 2 -> 0.80D;
        case 3 -> 0.68D;
        default -> 0.55D;
      };
      delay = Math.max(1, (int) Math.round(delay * bountyMultiplier));
    }
    nextStalkerSpawnEpochSecond = now + delay;
  }

  private void spawnParticleSafe(
      World world,
      Particle particle,
      Location center,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ,
      double extra
  ) {
    spawnParticleSafe(world, particle, center, count, offsetX, offsetY, offsetZ, extra, null, false);
  }

  private void spawnParticleSafe(
      World world,
      Particle particle,
      Location center,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ,
      double extra,
      Object data,
      boolean force
  ) {
    if (world == null || particle == null || center == null || count <= 0) {
      return;
    }
    try {
      world.spawnParticle(particle, center, count, offsetX, offsetY, offsetZ, extra, data, force);
      return;
    } catch (IllegalArgumentException exception) {
      if (warnedParticleFallbacks.add(particle)) {
        getLogger().warning(
            "Particle '" + particle.name() + "' requires extra data on this build; using fallback particle."
        );
      }
    }

    try {
      world.spawnParticle(
          Particle.PORTAL,
          center,
          count,
          offsetX,
          offsetY,
          offsetZ,
          Math.max(0.0D, extra),
          null,
          force
      );
    } catch (IllegalArgumentException ignored) {
      // Ignore: visual-only path should never break gameplay scheduling.
    }
  }

  private void tickDragonRaid() {
    if (!dragonRaidEnabled() || !dragonRaidEnabledInCurrentState()) {
      return;
    }

    cleanupRaidEntityOrder();
    EnderDragon dragon = findActiveEnderDragon();
    if (dragon == null || dragon.isDead() || !dragon.isValid()) {
      return;
    }

    int computedPhase = dragonRaidPhaseByHealth(dragon);
    if (computedPhase <= 0) {
      return;
    }

    if (computedPhase > dragonRaidPhase) {
      dragonRaidPhase = computedPhase;
      Bukkit.broadcastMessage("[Season] Dragon raid phase " + dragonRaidPhase + " started.");
      playDragonPhaseTransitionVisuals(dragon, dragonRaidPhase);
      grantRaidImmunityPhaseTransitionBonus(dragon.getWorld(), dragonRaidPhase);
      if (dragonRaidPhase >= 2 && !dragonRaidPhase2BurstDone) {
        spawnDragonRaidPhase2EntryBurst(dragon);
        dragonRaidPhase2BurstDone = true;
      }
      saveState();
    }

    long now = nowEpochSecond();
    if (nextRaidWaveEpochSecond <= 0L) {
      nextRaidWaveEpochSecond = now;
    }
    if (now < nextRaidWaveEpochSecond) {
      return;
    }

    spawnDragonRaidWave(dragon, dragonRaidPhase);
    nextRaidWaveEpochSecond = now + dragonRaidWaveIntervalSeconds();
    saveState();
  }

  private EnderDragon findActiveEnderDragon() {
    for (World world : Bukkit.getWorlds()) {
      if (world.getEnvironment() != World.Environment.THE_END) {
        continue;
      }
      for (Entity entity : world.getEntities()) {
        if (entity instanceof EnderDragon dragon && dragon.isValid() && !dragon.isDead()) {
          return dragon;
        }
      }
    }
    return null;
  }

  private int dragonRaidPhaseByHealth(EnderDragon dragon) {
    double maxHealth = dragon.getMaxHealth();
    if (maxHealth <= 0.0D) {
      return 0;
    }
    double percent = (dragon.getHealth() / maxHealth) * 100.0D;
    if (percent <= 20.0D) {
      return 3;
    }
    if (percent <= 50.0D) {
      return 2;
    }
    if (percent <= 70.0D) {
      return 1;
    }
    return 0;
  }

  private void spawnDragonRaidPhase2EntryBurst(EnderDragon dragon) {
    List<Player> targets = endRaidTargets(dragon.getWorld());
    if (targets.isEmpty()) {
      return;
    }

    int spawnCount = randomIntInclusive(
        dragonRaidPhase2EntryBlazeMin(),
        dragonRaidPhase2EntryBlazeMax()
    );
    int cap = dragonRaidMobCap(targets.size());
    for (int i = 0; i < spawnCount; i++) {
      Player target = targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
      spawnDragonRaidMobNear(target, EntityType.BLAZE, cap);
    }
  }

  private void spawnDragonRaidWave(EnderDragon dragon, int phase) {
    if (phase <= 0) {
      return;
    }

    List<Player> targets = endRaidTargets(dragon.getWorld());
    if (targets.isEmpty()) {
      return;
    }

    int cap = dragonRaidMobCap(targets.size());
    int endermanCount;
    int blazeCount = 0;
    int vexCount = 0;

    if (phase >= 3) {
      endermanCount = randomIntInclusive(dragonRaidPhase3EndermanMin(), dragonRaidPhase3EndermanMax());
      blazeCount = randomIntInclusive(dragonRaidPhase3BlazeMin(), dragonRaidPhase3BlazeMax());
      vexCount = randomIntInclusive(dragonRaidPhase3VexMin(), dragonRaidPhase3VexMax());
    } else if (phase == 2) {
      endermanCount = randomIntInclusive(dragonRaidPhase2EndermanMin(), dragonRaidPhase2EndermanMax());
      blazeCount = randomIntInclusive(dragonRaidPhase2BlazeMin(), dragonRaidPhase2BlazeMax());
    } else {
      endermanCount = randomIntInclusive(dragonRaidPhase1EndermanMin(), dragonRaidPhase1EndermanMax());
    }

    spawnDragonRaidMobBatch(targets, EntityType.ENDERMAN, endermanCount, cap);
    if (blazeCount > 0) {
      spawnDragonRaidMobBatch(targets, EntityType.BLAZE, blazeCount, cap);
    }
    if (vexCount > 0) {
      spawnDragonRaidMobBatch(targets, EntityType.VEX, vexCount, cap);
    }
  }

  private void spawnDragonRaidMobBatch(List<Player> targets, EntityType type, int count, int cap) {
    for (int i = 0; i < count; i++) {
      Player target = targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
      spawnDragonRaidMobNear(target, type, cap);
    }
  }

  private void spawnDragonRaidMobNear(Player target, EntityType type, int cap) {
    Location spawn = randomSpawnNear(target, dragonRaidSpawnDistanceMin(), dragonRaidSpawnDistanceMax());
    Entity entity = target.getWorld().spawnEntity(spawn, type);
    if (!(entity instanceof Mob mob)) {
      entity.remove();
      return;
    }

    mob.addScoreboardTag(RAID_TAG);
    mob.setPersistent(true);
    mob.setRemoveWhenFarAway(false);
    if (mob instanceof Monster monster) {
      monster.setTarget(target);
    }

    registerRaidEntity(entity.getUniqueId(), cap);
  }

  private void registerRaidEntity(UUID entityId, int cap) {
    cleanupRaidEntityOrder();
    int boundedCap = Math.max(1, cap);
    while (raidEntityOrder.size() >= boundedCap) {
      UUID oldest = raidEntityOrder.pollFirst();
      if (oldest == null) {
        break;
      }
      Entity oldEntity = Bukkit.getEntity(oldest);
      if (oldEntity != null && oldEntity.isValid() && !oldEntity.isDead()) {
        oldEntity.remove();
      }
    }
    raidEntityOrder.addLast(entityId);
  }

  private void cleanupRaidEntityOrder() {
    raidEntityOrder.removeIf(uuid -> {
      Entity entity = Bukkit.getEntity(uuid);
      return entity == null
          || !entity.isValid()
          || entity.isDead()
          || !entity.getScoreboardTags().contains(RAID_TAG);
    });
  }

  private List<Player> endRaidTargets(World endWorld) {
    List<Player> targets = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getWorld() != endWorld || player.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }
      PlayerRoundData data = players.get(player.getUniqueId());
      if (data == null || data.isOut()) {
        continue;
      }
      targets.add(player);
    }
    return targets;
  }

  private List<Player> playableOnlineParticipants() {
    List<Player> targets = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getGameMode() == GameMode.SPECTATOR) {
        continue;
      }
      PlayerRoundData data = players.get(player.getUniqueId());
      if (data == null || data.isOut()) {
        continue;
      }
      targets.add(player);
    }
    return targets;
  }

  private Location randomSpawnNear(Player target, double minDistance, double maxDistance) {
    Location base = target.getLocation();
    double angle = ThreadLocalRandom.current().nextDouble(0.0D, Math.PI * 2.0D);
    double distance = randomDouble(minDistance, maxDistance);
    double x = base.getX() + (Math.cos(angle) * distance);
    double z = base.getZ() + (Math.sin(angle) * distance);

    World world = target.getWorld();
    int highestY = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z));
    double y = Math.max(base.getY(), highestY + 1.0D);
    y = Math.max(world.getMinHeight() + 1.0D, Math.min(world.getMaxHeight() - 2.0D, y));
    return new Location(world, x, y, z, base.getYaw(), base.getPitch());
  }

  private void clearStalkers() {
    for (World world : Bukkit.getWorlds()) {
      for (Entity entity : world.getEntities()) {
        if (entity.getScoreboardTags().contains(STALKER_TAG)) {
          entity.remove();
        }
      }
    }
    stalkerEntities.clear();
  }

  private void clearRaidMobs() {
    for (World world : Bukkit.getWorlds()) {
      for (Entity entity : world.getEntities()) {
        if (entity.getScoreboardTags().contains(RAID_TAG)) {
          entity.remove();
        }
      }
    }
    raidEntityOrder.clear();
  }

  private void applyMobKillScore(EntityDeathEvent event) {
    if (!scoreMobEnabled() || !scoreGainEnabledInCurrentState()) {
      return;
    }
    if (event.getEntityType() == EntityType.ENDER_DRAGON || event.getEntity() instanceof Player) {
      return;
    }

    Player killer = event.getEntity().getKiller();
    if (killer == null || !isPlayerEligibleForScoreGain(killer)) {
      return;
    }
    UUID killerUuid = killer.getUniqueId();
    PlayerRoundData killerData = players.computeIfAbsent(killerUuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(killerUuid);

    long points = scoreMobPoints(event.getEntityType());
    if (points <= 0L) {
      return;
    }
    boolean auraInfusedMob = isAuraInfusedMob(event.getEntity());
    ActiveSeasonEffect auraDetect = killerData.getBlessingEffect("B-039");
    if (auraDetect != null && auraInfusedMob) {
      int tier = clampTier(auraDetect.getTier());
      double bonusRatio = switch (tier) {
        case 1 -> 0.15D;
        case 2 -> 0.30D;
        case 3 -> 0.45D;
        default -> 0.60D;
      };
      points += Math.max(1L, Math.round(points * bonusRatio));
    }
    if (auraDetect != null
        && auraInversionUntilEpochSecondByPlayer.getOrDefault(killerUuid, 0L) > nowEpochSecond()) {
      int tier = clampTier(auraDetect.getTier());
      if (tier >= 2 && isAuraInfusedMob(event.getEntity())) {
        double activeBonusRatio = tier >= 4 ? 0.35D : 0.20D;
        points += Math.max(1L, Math.round(points * activeBonusRatio));
      }
    }
    int auraTier = highestEffect80Tier(killerData, 'X', 12);
    if (auraTier > 0 && auraInfusedMob) {
      double bonusRatio = auraTier <= 1 ? 0.30D : 0.50D;
      if (auraResonanceBacklashUntilEpochSecondByPlayer.getOrDefault(killerUuid, 0L) > nowEpochSecond()) {
        bonusRatio += 0.25D;
      }
      points += Math.max(1L, Math.round(points * bonusRatio));
    }
    int raiderScentTier = highestEffect80Tier(killerData, 'X', 19);
    if (raiderScentTier >= 3) {
      double bonusRatio = raiderScentTier >= 4 ? 0.25D : 0.15D;
      points += Math.max(1L, Math.round(points * bonusRatio));
    }
    int explosionDesignerTier = highestEffect80Tier(killerData, 'X', 36);
    if (explosionDesignerTier >= 3) {
      EntityDamageEvent lastDamage = event.getEntity().getLastDamageCause();
      if (lastDamage != null && isExplosionDamageCause(lastDamage.getCause())) {
        double bonusRatio = explosionDesignerTier >= 4 ? 0.45D : 0.25D;
        points += Math.max(1L, Math.round(points * bonusRatio));
      }
    }

    long adjusted = applyRuntimeScoreModifier(points, killerData, killer.getWorld(), RuntimeModifierType.MOB_SCORE_RATIO);
    addGeneratedScore(killerUuid, adjusted);
  }

  private boolean isWeakenedMobForAuraBounty(LivingEntity entity) {
    if (entity == null) {
      return false;
    }
    if (entity.hasPotionEffect(PotionEffectType.WEAKNESS) || entity.hasPotionEffect(PotionEffectType.SLOWNESS)) {
      return true;
    }
    if (entity instanceof Monster monster && isAuraInfusedMob(monster)) {
      return true;
    }
    return false;
  }

  private void applyDragonJackpot(Entity dragonEntity) {
    if (!(dragonEntity instanceof EnderDragon dragon) || !scoreDragonJackpotEnabled()) {
      dragonDamageByPlayer.clear();
      return;
    }

    long assistPool = scoreDragonAssistTotalPoints();
    if (assistPool > 0L) {
      Map<UUID, Double> validDamage = new HashMap<>();
      double totalDamage = 0.0D;

      for (Map.Entry<UUID, Double> entry : dragonDamageByPlayer.entrySet()) {
        UUID uuid = entry.getKey();
        double value = Math.max(0.0D, entry.getValue());
        if (value <= 0.0D) {
          continue;
        }

        PlayerRoundData data = players.get(uuid);
        if (data != null && data.isOut()) {
          continue;
        }
        validDamage.put(uuid, value);
        totalDamage += value;
      }

      if (totalDamage > 0.0D && !validDamage.isEmpty()) {
        Map<UUID, Long> grants = new HashMap<>();
        long assigned = 0L;
        UUID topDamager = null;
        double topValue = -1.0D;

        for (Map.Entry<UUID, Double> entry : validDamage.entrySet()) {
          UUID uuid = entry.getKey();
          double damage = entry.getValue();
          long gain = (long) Math.floor((assistPool * damage) / totalDamage);
          if (gain > 0L) {
            grants.put(uuid, gain);
            assigned += gain;
          }
          if (damage > topValue) {
            topValue = damage;
            topDamager = uuid;
          }
        }

        long remain = assistPool - assigned;
        if (remain > 0L && topDamager != null) {
          grants.merge(topDamager, remain, Long::sum);
        }

        for (Map.Entry<UUID, Long> grant : grants.entrySet()) {
          long gain = grant.getValue();
          if (gain <= 0L) {
            continue;
          }
          PlayerRoundData data = players.get(grant.getKey());
          World world = dragon.getWorld();
          long adjusted = applyRuntimeScoreModifier(gain, data, world, RuntimeModifierType.DRAGON_JACKPOT_RATIO);
          addFlatScore(grant.getKey(), adjusted);
          Player online = Bukkit.getPlayer(grant.getKey());
          if (online != null) {
            online.sendMessage("[Season] Dragon assist +" + adjusted + " score.");
          }
        }
      }
    }

    Player killer = dragon.getKiller();
    long finalHitBonus = scoreDragonFinalHitBonus();
    if (killer != null && finalHitBonus > 0L && isPlayerEligibleForScoreGain(killer)) {
      PlayerRoundData killerData = players.get(killer.getUniqueId());
      long adjusted = applyRuntimeScoreModifier(
          finalHitBonus,
          killerData,
          killer.getWorld(),
          RuntimeModifierType.DRAGON_JACKPOT_RATIO
      );
      addFlatScore(killer.getUniqueId(), adjusted);
      Bukkit.broadcastMessage("[Season] Dragon final hit: " + killer.getName() + " +" + adjusted + " score.");
    }

    dragonDamageByPlayer.clear();
    saveState();
  }

  private void tickSurvivalScoreGain() {
    if (!scoreSurvivalEnabled() || !scoreGainEnabledInCurrentState()) {
      return;
    }

    long now = nowEpochSecond();
    int interval = scoreSurvivalIntervalSeconds();
    if (nextSurvivalScoreEpochSecond <= 0L) {
      nextSurvivalScoreEpochSecond = now + interval;
      return;
    }
    if (now < nextSurvivalScoreEpochSecond) {
      return;
    }

    long points = scoreSurvivalPointsPerInterval();
    if (points > 0L) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (!isPlayerEligibleForScoreGain(player)) {
          continue;
        }
        UUID uuid = player.getUniqueId();
        PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
        participants.add(uuid);
        long adjusted = applyRuntimeScoreModifier(points, data, player.getWorld(), RuntimeModifierType.SURVIVAL_SCORE_RATIO);
        addGeneratedScore(uuid, adjusted);
      }
    }

    do {
      nextSurvivalScoreEpochSecond += interval;
    } while (nextSurvivalScoreEpochSecond <= now);
  }

  private void tickRuntimeScoreDecay() {
    if (!cardsEnabled() || !cardsRuntimeEffectsEnabled() || !scoreGainEnabledInCurrentState()) {
      return;
    }
    int interval = runtimeScoreDecayIntervalSeconds();
    long now = nowEpochSecond();
    if (nextRuntimeScoreDecayEpochSecond <= 0L) {
      nextRuntimeScoreDecayEpochSecond = now + interval;
      return;
    }
    if (now < nextRuntimeScoreDecayEpochSecond) {
      return;
    }

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!isPlayerEligibleForScoreGain(player)) {
        continue;
      }
      UUID uuid = player.getUniqueId();
      PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
      participants.add(uuid);

      EnumMap<RuntimeModifierType, Double> totals = computeRuntimeModifierTotals(data, player.getWorld(), player);
      double decayRatio = runtimeModifierValue(totals, RuntimeModifierType.SCORE_DECAY_PER_MINUTE_RATIO);
      if (decayRatio <= 0.0D || data.getScore() <= 0L) {
        continue;
      }

      double clamped = Math.max(0.0D, Math.min(0.5D, decayRatio));
      long loss = (long) Math.floor(data.getScore() * clamped);
      if (loss <= 0L) {
        loss = 1L;
      }
      adjustPlayerScore(uuid, data, -loss, false);
    }

    do {
      nextRuntimeScoreDecayEpochSecond += interval;
    } while (nextRuntimeScoreDecayEpochSecond <= now);
  }

  private boolean isPlayerEligibleForScoreGain(Player player) {
    if (player == null || !player.isOnline() || player.getGameMode() == GameMode.SPECTATOR) {
      return false;
    }
    PlayerRoundData data = players.get(player.getUniqueId());
    return data == null || !data.isOut();
  }

  private void addGeneratedScore(UUID uuid, long points) {
    addScoreInternal(uuid, points, true);
  }

  private void addFlatScore(UUID uuid, long points) {
    addScoreInternal(uuid, points, false);
  }

  private void addScoreInternal(UUID uuid, long points, boolean generated) {
    if (points == 0L) {
      return;
    }

    PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(uuid);

    long adjusted = points;
    if (generated && points > 0L) {
      adjusted = applyGeneratedScoreMultiplier(points, uuid, data);
      if (adjusted <= 0L) {
        adjusted = 1L;
      }
    }
    adjustPlayerScore(uuid, data, adjusted, true);
  }

  private long applyGeneratedScoreMultiplier(long points, UUID playerId, PlayerRoundData data) {
    if (points <= 0L) {
      return points;
    }
    double multiplier = generatedScoreMultiplier(playerId, data);
    return (long) Math.floor(points * multiplier);
  }

  private double generatedScoreMultiplier(UUID playerId, PlayerRoundData data) {
    if (data == null || !cardsEnabled() || !cardsMultiplierEnabled()) {
      double spirit = playerId == null ? 1.0D : spiritExchangeScoreMultiplierByPlayer.getOrDefault(playerId, 1.0D);
      return Math.max(0.05D, Math.min(5.0D, spirit));
    }
    double bonusRatio =
        cardsMultiplierContribution(data.getBlessingEffects(), EffectKind.BLESSING)
            + cardsMultiplierContribution(data.getCurseEffects(), EffectKind.CURSE);

    if (bonusRatio <= 0.0D) {
      // Backward-compatibility fallback for deprecated stack-only state.
      int blessingStacks = data.getTotalBlessingCardStacks();
      int curseStacks = data.getTotalCurseCardStacks();
      bonusRatio += blessingStacks * cardsMultiplierBlessingPerStack();
      bonusRatio += curseStacks * cardsMultiplierCursePerStack();
    }

    double value = 1.0D + bonusRatio;
    double min = cardsMultiplierMin();
    double max = cardsMultiplierMax();
    if (value < min) {
      return min;
    }
    if (value > max) {
      value = max;
    }
    double spirit = playerId == null ? 1.0D : spiritExchangeScoreMultiplierByPlayer.getOrDefault(playerId, 1.0D);
    return Math.max(0.05D, Math.min(5.0D, value * spirit));
  }

  private double cardsMultiplierContribution(Map<String, ActiveSeasonEffect> activeEffects, EffectKind kind) {
    if (activeEffects == null || activeEffects.isEmpty() || kind == null) {
      return 0.0D;
    }
    double sum = 0.0D;
    for (ActiveSeasonEffect effect : activeEffects.values()) {
      if (effect == null) {
        continue;
      }
      int tier = Math.max(0, effect.getTier());
      if (tier <= 0) {
        continue;
      }
      EffectDefinition definition = effectDefinitionsById.get(effect.getId());
      double perTier = effectScoreMultiplierPerTier(definition, kind);
      if (perTier <= 0.0D) {
        continue;
      }
      sum += tier * perTier;
    }
    return Math.max(0.0D, sum);
  }

  private double effectScoreMultiplierPerTier(EffectDefinition definition, EffectKind kind) {
    EffectKind resolvedKind = kind;
    if (resolvedKind == null && definition != null) {
      resolvedKind = definition.kind();
    }
    if (resolvedKind == null) {
      resolvedKind = EffectKind.BLESSING;
    }

    EffectRuntimeProfile runtimeProfile = definition == null
        ? null
        : resolveRuntimeProfile(definition, resolvedKind, definition.archetype());
    if (runtimeProfile != null && runtimeProfile.scoreMultiplierPerTier() != null) {
      return Math.max(0.0D, runtimeProfile.scoreMultiplierPerTier());
    }
    return resolvedKind == EffectKind.BLESSING
        ? cardsMultiplierBlessingPerStack()
        : cardsMultiplierCursePerStack();
  }

  private void adjustPlayerScore(UUID uuid, PlayerRoundData data, long delta, boolean notifySlotUnlock) {
    if (data == null || delta == 0L) {
      return;
    }
    data.addScore(delta);
    queueScoreHudDelta(uuid, delta);
    syncPlayerSlots(uuid, data, notifySlotUnlock);
    maybePersistScoreChange();
  }

  private void setPlayerScore(UUID uuid, PlayerRoundData data, long newScore, boolean notifySlotUnlock) {
    if (data == null) {
      return;
    }
    long previous = data.getScore();
    data.setScore(newScore);
    queueScoreHudDelta(uuid, newScore - previous);
    syncPlayerSlots(uuid, data, notifySlotUnlock);
    maybePersistScoreChange();
  }

  private void maybePersistScoreChange() {
    if (!seasonStateDatabaseEnabled() || seasonStateRepository == null || !seasonStateRepository.isReady()) {
      return;
    }
    long now = nowEpochSecond();
    int minIntervalSeconds = scorePersistenceDbMinIntervalSeconds();
    if (minIntervalSeconds <= 0) {
      saveState();
      return;
    }
    if (now < nextScorePersistenceEpochSecond) {
      return;
    }
    nextScorePersistenceEpochSecond = now + minIntervalSeconds;
    saveState();
  }

  private void queueScoreHudDelta(UUID uuid, long delta) {
    if (!scoreHudEnabled() || uuid == null || delta == 0L) {
      return;
    }
    pendingScoreHudDeltaByPlayer.merge(uuid, delta, Long::sum);
  }

  private void tickScoreHudActionBar() {
    if (!scoreHudEnabled() || !scoreHudActionbarEnabled()) {
      return;
    }

    long now = nowEpochSecond();
    int refreshSeconds = scoreHudActionbarRefreshSeconds();
    boolean showWhenNoChange = scoreHudActionbarShowWhenNoChange();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player == null || !player.isOnline()) {
        continue;
      }
      UUID uuid = player.getUniqueId();
      PlayerRoundData data = players.get(uuid);
      if (data == null) {
        continue;
      }

      long delta = pendingScoreHudDeltaByPlayer.getOrDefault(uuid, 0L);
      long lastSent = lastScoreHudEpochSecondByPlayer.getOrDefault(uuid, 0L);
      boolean refreshDue = refreshSeconds > 0 && (now - lastSent) >= refreshSeconds;
      if (delta == 0L && (!showWhenNoChange || !refreshDue)) {
        continue;
      }

      sendScoreHudActionBar(player, data.getScore(), delta);
      lastScoreHudEpochSecondByPlayer.put(uuid, now);
      pendingScoreHudDeltaByPlayer.remove(uuid);
    }

    pendingScoreHudDeltaByPlayer.keySet().removeIf(playerId -> Bukkit.getPlayer(playerId) == null);
    lastScoreHudEpochSecondByPlayer.keySet().removeIf(playerId -> Bukkit.getPlayer(playerId) == null);
  }

  private void sendScoreHudActionBar(Player player, long totalScore, long delta) {
    if (player == null || !player.isOnline()) {
      return;
    }
    String total = ChatColor.GOLD + "총 " + formatScoreAmount(totalScore);
    if (delta > 0L) {
      player.sendActionBar(ChatColor.GREEN + "+" + formatScoreAmount(delta) + ChatColor.DARK_GRAY + " | " + total);
      return;
    }
    if (delta < 0L) {
      player.sendActionBar(ChatColor.RED + "-" + formatScoreAmount(Math.abs(delta)) + ChatColor.DARK_GRAY + " | " + total);
      return;
    }
    player.sendActionBar(total);
  }

  private String formatScoreAmount(long value) {
    return String.format(Locale.ROOT, "%,d", value);
  }

  private void syncAllParticipantSlots(boolean notifySlotUnlock) {
    for (Map.Entry<UUID, PlayerRoundData> entry : players.entrySet()) {
      syncPlayerSlots(entry.getKey(), entry.getValue(), notifySlotUnlock);
    }
  }

  private void syncPlayerSlots(UUID uuid, PlayerRoundData data, boolean notifySlotUnlock) {
    if (data == null) {
      return;
    }
    if (!slotsEnabled()) {
      data.setBlessingSlotsUnlocked(0);
      data.setCurseSlotsUnlocked(0);
      return;
    }
    int unlocked = computeUnlockedSlotCount(data);
    int oldBlessing = data.getBlessingSlotsUnlocked();
    int oldCurse = data.getCurseSlotsUnlocked();
    int newBlessing = slotBlessingEnabled() ? Math.min(unlocked, slotBlessingMax()) : 0;
    int newCurse = slotCurseEnabled() ? Math.min(unlocked, slotCurseMax()) : 0;

    data.setBlessingSlotsUnlocked(newBlessing);
    data.setCurseSlotsUnlocked(newCurse);

    // Keep active effects bounded by currently unlocked slots.
    trimToSlotCount(data.getBlessingEffects(), newBlessing);
    trimToSlotCount(data.getCurseEffects(), newCurse);

    if (!notifySlotUnlock || !slotAnnounceUnlock()) {
      return;
    }
    if (uuid == null) {
      return;
    }
    Player online = Bukkit.getPlayer(uuid);
    if (online == null || !online.isOnline()) {
      return;
    }
    if (newBlessing > oldBlessing) {
      online.sendMessage("[Season] Blessing slots unlocked: " + newBlessing + "/" + slotBlessingMax());
    }
    if (newCurse > oldCurse) {
      online.sendMessage("[Season] Curse slots unlocked: " + newCurse + "/" + slotCurseMax());
    }
  }

  private int computeUnlockedSlotCount(PlayerRoundData data) {
    if (data == null) {
      return 0;
    }
    int start = slotStartCount();
    int max = slotBaseMax();
    long perUnlock = slotProgressPerUnlock();
    long progress = slotProgressScore(data);
    long unlockedByProgress = perUnlock <= 0L ? 0L : (progress / perUnlock);
    long total = start + unlockedByProgress;
    if (total < 0L) {
      return 0;
    }
    return (int) Math.min(max, Math.min(Integer.MAX_VALUE, total));
  }

  private long slotProgressScore(PlayerRoundData data) {
    String source = slotProgressScoreSource();
    if (source != null && source.equalsIgnoreCase("CURRENT_SCORE")) {
      return Math.max(0L, data.getScore());
    }
    return Math.max(0L, data.getPeakScore());
  }

  private void resetDragonRaidProgress() {
    nextRaidWaveEpochSecond = 0L;
    dragonRaidPhase = 0;
    dragonRaidPhase2BurstDone = false;
    raidFatalBufferUsedPhaseByPlayer.clear();
    raidRaidwideSaveUsedByPlayer.clear();
  }

  private double randomDouble(double min, double max) {
    if (max <= min) {
      return min;
    }
    return ThreadLocalRandom.current().nextDouble(min, max);
  }

  private int randomIntInclusive(int min, int max) {
    if (max <= min) {
      return min;
    }
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  private void tick() {
    if (!isSeasonGameplayServer()) {
      return;
    }
    tickCounter++;

    updateResetPendingState();
    tickNewbieProtectionEffects();
    tickDailyCardDraw();
    tickActiveSeasonEffects();
    tickPlacedBlockDecay();
    tickStalkerSystem();
    tickDragonRaid();
    tickEnderAura();
    tickSurvivalScoreGain();
    tickRuntimeScoreDecay();
    tickBorderWitherHazard();
    tickFreePlayFinalization();
    tickScoreHudActionBar();

    if (state == SeasonState.RESET_PENDING && resetPendingUntilEpochSecond > 0L) {
      long remaining = resetPendingUntilEpochSecond - nowEpochSecond();
      if (remaining <= 0L) {
        executeRoundReset();
        return;
      }

      if (remaining <= 10L || remaining % 60L == 0L) {
        Bukkit.broadcastMessage("[Season] Reset in " + remaining + "s.");
      }
    }

    long now = nowEpochSecond();
    if (now - lastAutoSaveEpochSecond >= autoSaveSeconds()) {
      saveState();
      lastAutoSaveEpochSecond = now;
    }
  }

  private void updateResetPendingState() {
    if (state == SeasonState.FREE_PLAY) {
      return;
    }
    if (participants.isEmpty()) {
      return;
    }

    boolean allOut = true;
    for (UUID participant : participants) {
      PlayerRoundData data = players.get(participant);
      if (data == null || !data.isOut()) {
        allOut = false;
        break;
      }
    }

    if (allOut) {
      if (state != SeasonState.RESET_PENDING) {
        stateBeforeResetPending = state;
        state = SeasonState.RESET_PENDING;
        resetPendingUntilEpochSecond = nowEpochSecond() + resetCountdownSeconds();
        Bukkit.broadcastMessage("[Season] All participants are OUT. Reset pending for " + resetCountdownSeconds() + " seconds.");
        saveState();
      }
      return;
    }

    if (state == SeasonState.RESET_PENDING && cancelResetIfPlayableAgain()) {
      state = stateBeforeResetPending;
      resetPendingUntilEpochSecond = 0L;
      Bukkit.broadcastMessage("[Season] Reset pending cancelled: at least one participant can play.");
      saveState();
    }
  }

  private boolean isWithinFreePlayEscapeWindow() {
    if (state != SeasonState.FREE_PLAY || freePlayEscapeDeadlineEpochSecond <= 0L) {
      return false;
    }
    return nowEpochSecond() <= freePlayEscapeDeadlineEpochSecond;
  }

  private void enterFreePlay(String reason) {
    long now = nowEpochSecond();

    state = SeasonState.FREE_PLAY;
    resetPendingUntilEpochSecond = 0L;
    freePlayStartedEpochSecond = now;
    freePlayEscapeDeadlineEpochSecond = now + freePlayEscapeWindowSeconds();
    freePlayFinalized = false;
    clearStalkers();
    resetDragonRaidProgress();
    nextStalkerSpawnEpochSecond = 0L;
    dragonDamageByPlayer.clear();

    int eligible = 0;
    for (UUID participant : participants) {
      PlayerRoundData data = players.get(participant);
      if (data == null) {
        continue;
      }
      data.clearFreePlayResult();
      if (!data.isOut()) {
        data.setScoreSnapshot(data.getScore());
        eligible++;
      }
    }

    Bukkit.broadcastMessage("[Season] FREE_PLAY started: " + reason + ".");
    Bukkit.broadcastMessage("[Season] Escape window is " + freePlayEscapeWindowSeconds() + " seconds. eligible=" + eligible + ".");
    saveState();
  }

  private void tickFreePlayFinalization() {
    if (state != SeasonState.FREE_PLAY || freePlayFinalized || freePlayEscapeDeadlineEpochSecond <= 0L) {
      return;
    }

    if (nowEpochSecond() >= freePlayEscapeDeadlineEpochSecond) {
      finalizeFreePlayRanking();
    }
  }

  private void finalizeFreePlayRanking() {
    List<Map.Entry<UUID, PlayerRoundData>> ranking = new ArrayList<>();
    for (UUID participant : participants) {
      PlayerRoundData data = players.get(participant);
      if (data == null || !data.hasScoreSnapshot()) {
        continue;
      }
      ranking.add(Map.entry(participant, data));
    }

    ranking.sort((left, right) -> {
      double leftScore = finalRankingScore(left.getValue());
      double rightScore = finalRankingScore(right.getValue());
      int cmp = Double.compare(rightScore, leftScore);
      if (cmp != 0) {
        return cmp;
      }

      long leftSnapshot = left.getValue().getScoreSnapshot() == null ? 0L : left.getValue().getScoreSnapshot();
      long rightSnapshot = right.getValue().getScoreSnapshot() == null ? 0L : right.getValue().getScoreSnapshot();
      cmp = Long.compare(rightSnapshot, leftSnapshot);
      if (cmp != 0) {
        return cmp;
      }
      return left.getKey().toString().compareTo(right.getKey().toString());
    });

    int topN = Math.max(0, freePlayWinnerBonusTopN());
    long winnerBonus = Math.max(0L, freePlayWinnerBonusScore());

    int rank = 0;
    for (Map.Entry<UUID, PlayerRoundData> entry : ranking) {
      rank++;
      UUID uuid = entry.getKey();
      PlayerRoundData data = entry.getValue();
      data.setWinnerRank(rank);

      if (rank <= topN && winnerBonus > 0L) {
        adjustPlayerScore(uuid, data, winnerBonus, true);
        data.setWinnerBonusGranted(true);

        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
          online.sendMessage("[Season] Winner bonus +" + winnerBonus + " score. rank=#" + rank);
        }
      }
    }

    freePlayFinalized = true;
    persistFreePlayRankingToDatabase(ranking);

    if (ranking.isEmpty()) {
      Bukkit.broadcastMessage("[Season] FREE_PLAY finalized: no eligible participants.");
    } else {
      int show = Math.min(Math.max(1, topN), ranking.size());
      StringBuilder summary = new StringBuilder("[Season] FREE_PLAY finalized top" + show + ": ");
      for (int i = 0; i < show; i++) {
        Map.Entry<UUID, PlayerRoundData> entry = ranking.get(i);
        PlayerRoundData data = entry.getValue();
        if (i > 0) {
          summary.append(", ");
        }
        summary
            .append("#").append(i + 1)
            .append(" ").append(playerDisplayName(entry.getKey()))
            .append(data.isEscapedWithinWindow() ? "(escape)" : "(survival)");
      }
      Bukkit.broadcastMessage(summary.toString());
    }

    saveState();
  }

  private void persistFreePlayRankingToDatabase(List<Map.Entry<UUID, PlayerRoundData>> ranking) {
    if (!seasonStateDatabaseEnabled() || seasonStateRepository == null || !seasonStateRepository.isReady()) {
      return;
    }

    List<SeasonStateJdbcRepository.RoundLeaderboardEntry> entries = new ArrayList<>();
    long savedAt = nowEpochSecond();
    int rank = 0;
    if (ranking != null) {
      for (Map.Entry<UUID, PlayerRoundData> entry : ranking) {
        if (entry == null || entry.getKey() == null || entry.getValue() == null) {
          continue;
        }
        rank++;
        UUID uuid = entry.getKey();
        PlayerRoundData data = entry.getValue();
        long snapshot = data.getScoreSnapshot() == null ? 0L : Math.max(0L, data.getScoreSnapshot());
        entries.add(new SeasonStateJdbcRepository.RoundLeaderboardEntry(
            rank,
            uuid.toString(),
            playerDisplayName(uuid),
            snapshot,
            finalRankingScore(data),
            data.isEscapedWithinWindow(),
            data.isWinnerBonusGranted(),
            savedAt
        ));
      }
    }

    try {
      seasonStateRepository.replaceRoundLeaderboard(seasonId(), currentServerName(), roundId, entries);
    } catch (SQLException exception) {
      getLogger().warning("Failed to persist FREE_PLAY leaderboard to DB: " + exception.getMessage());
    }
  }

  private double finalRankingScore(PlayerRoundData data) {
    if (data == null || !data.hasScoreSnapshot()) {
      return 0.0D;
    }

    long snapshot = data.getScoreSnapshot() == null ? 0L : data.getScoreSnapshot();
    double weight = data.isEscapedWithinWindow() ? freePlayEscapeWeight() : freePlaySurvivalWeight();
    return snapshot * weight;
  }

  private String playerDisplayName(UUID uuid) {
    Player online = Bukkit.getPlayer(uuid);
    if (online != null) {
      return online.getName();
    }
    OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
    if (offline.getName() != null && !offline.getName().isBlank()) {
      return offline.getName();
    }
    return uuid.toString();
  }

  private void executeRoundReset() {
    long currentRound = roundId;
    long nextRound = roundId + 1L;
    boolean worldWipeRequested = false;

    List<String> commands = getConfig().getStringList("round.reset_commands");
    for (String command : commands) {
      if (command == null || command.isBlank()) {
        continue;
      }

      String resolved = command
          .replace("{round_id}", String.valueOf(currentRound))
          .replace("{next_round_id}", String.valueOf(nextRound))
          .replace("{server_name}", currentServerName());

      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resolved);
    }

    if (roundWorldWipeEnabled()) {
      worldWipeRequested = requestRoundWorldWipe(currentRound, nextRound);
    }

    players.clear();
    participants.clear();
    borderOutsideSinceEpochSecond.clear();
    clearStalkers();
    clearRaidMobs();
    roundId = nextRound;
    roundStartedEpochSecond = nowEpochSecond();
    borderShrinkStartedEpochSecond = roundStartedEpochSecond;
    freePlayStartedEpochSecond = 0L;
    freePlayEscapeDeadlineEpochSecond = 0L;
    freePlayFinalized = false;
    tickCounter = 0L;
    auraPlayerCursor = 0;
    lastEnderAuraLevel = -1;
    nextSurvivalScoreEpochSecond = roundStartedEpochSecond + scoreSurvivalIntervalSeconds();
    dragonEggBonusOwner = null;
    dragonDamageByPlayer.clear();
    nextStalkerSpawnEpochSecond = roundStartedEpochSecond + stalkerFirstSpawnDelaySeconds();
    stalkerEmpowerStacks = 0;
    resetDragonRaidProgress();
    lastCardDrawIngameDay = -1L;
    state = SeasonState.HARDCORE;
    stateBeforeResetPending = SeasonState.HARDCORE;
    resetPendingUntilEpochSecond = 0L;
    maybeAutoCenterBorderFromStronghold(true);

    Bukkit.broadcastMessage("[Season] Round reset complete. New round=" + roundId + ".");
    saveState();

    if (worldWipeRequested && roundWorldWipeAutoShutdown()) {
      Bukkit.broadcastMessage("[Season] Restarting server to apply world wipe...");
      Bukkit.getScheduler().runTask(this, Bukkit::shutdown);
    }
  }

  private boolean requestRoundWorldWipe(long currentRound, long nextRound) {
    List<String> worlds = roundWorldWipeWorlds();
    if (worlds.isEmpty()) {
      getLogger().warning("World wipe requested but no world list is configured.");
      return false;
    }

    File marker = roundWorldWipeMarkerFile();
    File parent = marker.getParentFile();
    if (parent != null && !parent.exists() && !parent.mkdirs()) {
      getLogger().warning("Could not create world wipe marker parent directory: " + parent.getAbsolutePath());
      return false;
    }

    YamlConfiguration markerYaml = new YamlConfiguration();
    markerYaml.set("current_round", currentRound);
    markerYaml.set("next_round", nextRound);
    markerYaml.set("server_name", currentServerName());
    markerYaml.set("requested_epoch_second", nowEpochSecond());
    markerYaml.set("worlds", worlds);

    try {
      markerYaml.save(marker);
    } catch (IOException exception) {
      getLogger().warning("Failed to write world wipe marker: " + exception.getMessage());
      return false;
    }

    kickAllPlayersForWorldWipe(roundWorldWipeKickMessage());
    getLogger().info(
        "Round world wipe marker created: " + marker.getAbsolutePath()
            + " worlds=" + String.join(",", worlds)
    );
    return true;
  }

  private void kickAllPlayersForWorldWipe(String message) {
    String reason = message == null || message.isBlank()
        ? "Round reset in progress. Please reconnect shortly."
        : message;

    for (Player player : new ArrayList<>(Bukkit.getOnlinePlayers())) {
      player.kickPlayer(reason);
    }
  }

  private void ensureEndSpawnPlatform(World endWorld) {
    if (!getConfig().getBoolean("end.spawn_platform.enabled", true)) {
      return;
    }

    int radius = Math.max(1, getConfig().getInt("end.spawn_platform.radius", 2));
    int baseY = endWorld.getSpawnLocation().getBlockY() - 1;
    int baseX = endWorld.getSpawnLocation().getBlockX();
    int baseZ = endWorld.getSpawnLocation().getBlockZ();

    for (int x = baseX - radius; x <= baseX + radius; x++) {
      for (int z = baseZ - radius; z <= baseZ + radius; z++) {
        Material current = endWorld.getBlockAt(x, baseY, z).getType();
        if (current == Material.AIR || current == Material.CAVE_AIR || current == Material.VOID_AIR) {
          endWorld.getBlockAt(x, baseY, z).setType(Material.BEDROCK, false);
        }
      }
    }
  }

  private void maybeAutoCenterBorderFromStronghold(boolean force) {
    if (!borderAutoCenterEnabled()) {
      return;
    }

    World world = borderWorld();
    if (world == null) {
      return;
    }
    if (world.getEnvironment() != World.Environment.NORMAL) {
      return;
    }
    if (!world.canGenerateStructures()) {
      return;
    }

    if (!force && borderAutoCenterPreserveExisting()) {
      if (Math.abs(borderCenterX()) > 1.0E-6D || Math.abs(borderCenterZ()) > 1.0E-6D) {
        return;
      }
    }

    Location origin = world.getSpawnLocation();
    int radiusChunks = borderAutoCenterSearchRadiusChunks();
    boolean findUnexplored = borderAutoCenterPreferUnexplored();

    Location located = world.locateNearestStructure(
        origin,
        StructureType.STRONGHOLD,
        radiusChunks,
        findUnexplored
    );
    if (located == null) {
      getLogger().warning("Could not locate stronghold for border auto-center.");
      return;
    }

    getConfig().set("border.center.x", located.getX());
    getConfig().set("border.center.z", located.getZ());
    saveConfig();
    getLogger().info(
        "Border center auto-set from stronghold: x="
            + String.format(Locale.ROOT, "%.1f", located.getX())
            + ", z="
            + String.format(Locale.ROOT, "%.1f", located.getZ())
            + ", force="
            + force
    );
  }

  private void loadState() {
    players.clear();
    participants.clear();
    dragonDamageByPlayer.clear();

    YamlConfiguration yaml = loadStateFromDatabaseIfAvailable();
    if (yaml == null) {
      if (!stateFile.exists()) {
        saveState();
        return;
      }
      yaml = YamlConfiguration.loadConfiguration(stateFile);
    }

    roundId = Math.max(1L, yaml.getLong("round.id", 1L));
    roundStartedEpochSecond = Math.max(1L, yaml.getLong("round.started_epoch_second", nowEpochSecond()));
    borderShrinkStartedEpochSecond = Math.max(
        1L,
        yaml.getLong("round.border_shrink_started_epoch_second", roundStartedEpochSecond)
    );
    state = SeasonState.parse(yaml.getString("round.state")).orElse(SeasonState.HARDCORE);
    stateBeforeResetPending = SeasonState.parse(yaml.getString("round.state_before_reset_pending"))
        .orElse(SeasonState.HARDCORE);
    resetPendingUntilEpochSecond = Math.max(0L, yaml.getLong("round.reset_pending_until_epoch_second", 0L));
    freePlayStartedEpochSecond = Math.max(0L, yaml.getLong("round.free_play_started_epoch_second", 0L));
    freePlayEscapeDeadlineEpochSecond = Math.max(0L, yaml.getLong("round.free_play_escape_deadline_epoch_second", 0L));
    freePlayFinalized = yaml.getBoolean("round.free_play_finalized", false);
    nextStalkerSpawnEpochSecond = Math.max(0L, yaml.getLong("round.next_stalker_spawn_epoch_second", 0L));
    stalkerEmpowerStacks = Math.max(0, yaml.getInt("round.stalker_empower_stacks", 0));
    nextRaidWaveEpochSecond = Math.max(0L, yaml.getLong("round.next_raid_wave_epoch_second", 0L));
    dragonRaidPhase = Math.max(0, yaml.getInt("round.dragon_raid_phase", 0));
    dragonRaidPhase2BurstDone = yaml.getBoolean("round.dragon_raid_phase2_burst_done", false);
    nextSurvivalScoreEpochSecond = Math.max(0L, yaml.getLong("round.next_survival_score_epoch_second", 0L));
    lastCardDrawIngameDay = Math.max(-1L, yaml.getLong("round.last_card_draw_ingame_day", -1L));

    dragonEggBonusOwner = null;
    String eggOwnerRaw = yaml.getString("round.dragon_egg_bonus_owner");
    if (eggOwnerRaw != null && !eggOwnerRaw.isBlank()) {
      try {
        dragonEggBonusOwner = UUID.fromString(eggOwnerRaw);
      } catch (IllegalArgumentException ignored) {
        dragonEggBonusOwner = null;
      }
    }

    ConfigurationSection dragonDamageSection = yaml.getConfigurationSection("round.dragon_damage");
    if (dragonDamageSection != null) {
      for (String key : dragonDamageSection.getKeys(false)) {
        try {
          UUID uuid = UUID.fromString(key);
          double damage = Math.max(0.0D, dragonDamageSection.getDouble(key, 0.0D));
          if (damage > 0.0D) {
            dragonDamageByPlayer.put(uuid, damage);
          }
        } catch (IllegalArgumentException ignored) {
          // ignore invalid uuid key
        }
      }
    }

    ConfigurationSection playersSection = yaml.getConfigurationSection("players");
    if (playersSection == null) {
      return;
    }

    for (String key : playersSection.getKeys(false)) {
      UUID uuid;
      try {
        uuid = UUID.fromString(key);
      } catch (IllegalArgumentException ignored) {
        getLogger().warning("Skipping invalid UUID in state file: " + key);
        continue;
      }

      ConfigurationSection section = playersSection.getConfigurationSection(key);
      if (section == null) {
        continue;
      }

      PlayerRoundData data = new PlayerRoundData(baseLives());
      data.setScore(section.getLong("score", 0L));
      data.setPeakScore(section.getLong("peak_score", data.getScore()));
      data.setLivesRemaining(section.getInt("lives_remaining", baseLives()));
      data.setDeathCount(section.getInt("death_count", 0));
      data.setOut(section.getBoolean("out", false));
      data.setNewbieProtectUntilEpochSecond(section.getLong("newbie_protect_until_epoch_second", 0L));
      if (section.contains("score_snapshot")) {
        data.setScoreSnapshot(section.getLong("score_snapshot", 0L));
      } else {
        data.setScoreSnapshot(null);
      }
      data.setEscapedWithinWindow(section.getBoolean("escaped_within_window", false));
      data.setWinnerRank(section.getInt("winner_rank", 0));
      data.setWinnerBonusGranted(section.getBoolean("winner_bonus_granted", false));
      data.setJoinSpawnAssigned(section.getBoolean("join_spawn_assigned", section.getBoolean("participant", true)));
      data.setBlessingSlotsUnlocked(section.getInt("blessing_slots_unlocked", 0));
      data.setCurseSlotsUnlocked(section.getInt("curse_slots_unlocked", 0));
      data.setInitialCardRollCompleted(section.getBoolean("initial_card_roll_completed", false));
      data.setInitialCardRerollsUsed(section.getInt("initial_card_rerolls_used", 0));

      ConfigurationSection blessingEffects = section.getConfigurationSection("blessing_effects");
      if (blessingEffects != null) {
        for (String effectId : blessingEffects.getKeys(false)) {
          ConfigurationSection effectSection = blessingEffects.getConfigurationSection(effectId);
          if (effectSection == null) {
            continue;
          }
          ActiveSeasonEffect effect = parseActiveEffect(effectId, effectSection, EffectKind.BLESSING);
          if (effect != null) {
            data.putBlessingEffect(effect);
          }
        }
      }

      ConfigurationSection curseEffects = section.getConfigurationSection("curse_effects");
      if (curseEffects != null) {
        for (String effectId : curseEffects.getKeys(false)) {
          ConfigurationSection effectSection = curseEffects.getConfigurationSection(effectId);
          if (effectSection == null) {
            continue;
          }
          ActiveSeasonEffect effect = parseActiveEffect(effectId, effectSection, EffectKind.CURSE);
          if (effect != null) {
            data.putCurseEffect(effect);
          }
        }
      }

      ConfigurationSection blessingStacks = section.getConfigurationSection("blessing_card_stacks");
      if (blessingStacks != null) {
        for (String cardId : blessingStacks.getKeys(false)) {
          int value = Math.max(0, blessingStacks.getInt(cardId, 0));
          if (value > 0) {
            data.addBlessingCardStack(cardId, value);
          }
        }
      }
      ConfigurationSection curseStacks = section.getConfigurationSection("curse_card_stacks");
      if (curseStacks != null) {
        for (String cardId : curseStacks.getKeys(false)) {
          int value = Math.max(0, curseStacks.getInt(cardId, 0));
          if (value > 0) {
            data.addCurseCardStack(cardId, value);
          }
        }
      }

      migrateStackDataToEffects(data);

      if (!section.contains("initial_card_roll_completed")) {
        boolean inferredCompleted = !data.getBlessingEffects().isEmpty()
            || !data.getCurseEffects().isEmpty()
            || !data.getBlessingCardStacks().isEmpty()
            || !data.getCurseCardStacks().isEmpty();
        data.setInitialCardRollCompleted(inferredCompleted);
      }
      if (!data.isInitialCardRollCompleted()) {
        data.setInitialCardRerollsUsed(0);
      }

      players.put(uuid, data);
      if (section.getBoolean("participant", true)) {
        participants.add(uuid);
      }
    }
  }

  private void stripCardEffectDataIfDisabled() {
    if (cardsEffectLogicEnabled()) {
      return;
    }

    boolean changed = false;
    for (PlayerRoundData data : players.values()) {
      if (data == null) {
        continue;
      }
      if (!data.getBlessingEffects().isEmpty()) {
        data.getBlessingEffects().clear();
        changed = true;
      }
      if (!data.getCurseEffects().isEmpty()) {
        data.getCurseEffects().clear();
        changed = true;
      }
      if (!data.getBlessingCardStacks().isEmpty()) {
        data.getBlessingCardStacks().clear();
        changed = true;
      }
      if (!data.getCurseCardStacks().isEmpty()) {
        data.getCurseCardStacks().clear();
        changed = true;
      }
    }

    if (!silenceVowSilentPlayers.isEmpty()) {
      Set<UUID> muted = new HashSet<>(silenceVowSilentPlayers);
      silenceVowSilentPlayers.clear();
      for (UUID uuid : muted) {
        Player online = Bukkit.getPlayer(uuid);
        if (online != null && online.isOnline()) {
          online.setSilent(false);
        }
      }
      changed = true;
    }

    if (!initialCardSetupActivePlayers.isEmpty()
        || !temporaryInvulnerableUntilEpochSecondByPlayer.isEmpty()
        || !slotRerollCooldownUntilEpochSecond.isEmpty()
        || !effectCooldownUntilEpochSecond.isEmpty()
        || !effectCooldownUntilEpochMilli.isEmpty()
        || !noBuildUntilEpochSecond.isEmpty()
        || !noAttackUntilEpochSecond.isEmpty()
        || !lockedHotbarSlotUntilEpochSecond.isEmpty()
        || !bannedToolGroupByPlayer.isEmpty()
        || !bannedToolUntilEpochSecond.isEmpty()
        || !toolBanPendingEpochSecondByPlayer.isEmpty()
        || !toolBanPendingTierByPlayer.isEmpty()) {
      changed = true;
    }

    initialCardSetupActivePlayers.clear();
    temporaryInvulnerableUntilEpochSecondByPlayer.clear();
    slotRerollCooldownUntilEpochSecond.clear();
    effectCooldownUntilEpochSecond.clear();
    effectCooldownUntilEpochMilli.clear();
    noBuildUntilEpochSecond.clear();
    noAttackUntilEpochSecond.clear();
    lockedHotbarSlotUntilEpochSecond.clear();
    bannedToolGroupByPlayer.clear();
    bannedToolUntilEpochSecond.clear();
    toolBanPendingEpochSecondByPlayer.clear();
    toolBanPendingTierByPlayer.clear();

    if (changed) {
      getLogger().info("cards.effect_logic_enabled=false: cleared stored card effects/stacks.");
    }
  }

  private YamlConfiguration loadStateFromDatabaseIfAvailable() {
    if (!seasonStateDatabaseEnabled() || seasonStateRepository == null || !seasonStateRepository.isReady()) {
      return null;
    }

    try {
      String blob = seasonStateRepository.loadStateBlob(seasonId(), currentServerName());
      if (blob == null || blob.isBlank()) {
        return null;
      }
      YamlConfiguration yaml = new YamlConfiguration();
      yaml.loadFromString(blob);
      getLogger().info("Loaded season state from DB.");
      return yaml;
    } catch (SQLException | InvalidConfigurationException exception) {
      getLogger().warning("Failed to load state from DB, falling back to state.yml: " + exception.getMessage());
      return null;
    }
  }

  private ActiveSeasonEffect parseActiveEffect(String effectId, ConfigurationSection section, EffectKind fallbackKind) {
    if (section == null) {
      return null;
    }

    String normalized = normalizeEffectId(effectId);
    if (normalized.isBlank()) {
      return null;
    }

    EffectDefinition definition = effectDefinitionsById.get(normalized);
    EffectKind kind = definition == null ? inferEffectKind(normalized, fallbackKind) : definition.kind();
    if (kind != fallbackKind) {
      return null;
    }

    String displayName;
    if (definition != null && definition.displayName() != null && !definition.displayName().isBlank()) {
      displayName = definition.displayName();
    } else {
      String storedName = section.getString("name", normalized);
      displayName = (storedName == null || storedName.isBlank()) ? normalized : storedName.trim();
    }
    int tier = Math.max(1, section.getInt("tier", 1));
    long startDay = Math.max(0L, section.getLong("start_ingame_day", 0L));
    long expireDay = Math.max(
        startDay + 1L,
        section.getLong("expire_ingame_day", resolveEffectExpireIngameDay(startDay))
    );
    boolean severe = definition != null
        ? definition.severeCurse()
        : section.getBoolean("severe", fallbackKind == EffectKind.CURSE);
    long severeBonus = definition != null
        ? Math.max(0L, definition.severeBonusPoints())
        : Math.max(0L, section.getLong("severe_bonus_points", 0L));

    return new ActiveSeasonEffect(
        normalized,
        displayName,
        Math.min(cardsMaxTier(), tier),
        startDay,
        expireDay,
        severe,
        severeBonus
    );
  }

  private void writeActiveEffect(YamlConfiguration yaml, String path, ActiveSeasonEffect effect) {
    if (yaml == null || path == null || path.isBlank() || effect == null) {
      return;
    }
    yaml.set(path + ".name", effect.getDisplayName());
    yaml.set(path + ".tier", Math.max(1, effect.getTier()));
    yaml.set(path + ".start_ingame_day", Math.max(0L, effect.getStartIngameDay()));
    yaml.set(path + ".expire_ingame_day", Math.max(effect.getStartIngameDay() + 1L, effect.getExpireIngameDay()));
    yaml.set(path + ".severe", effect.isSevereCurse());
    yaml.set(path + ".severe_bonus_points", Math.max(0L, effect.getSevereBonusPoints()));
  }

  private void migrateStackDataToEffects(PlayerRoundData data) {
    if (!cardsStackMigrationEnabled()) {
      return;
    }
    if (data == null) {
      return;
    }

    long baseDay = Math.max(0L, lastCardDrawIngameDay);
    long expireDay = resolveEffectExpireIngameDay(baseDay);

    if (data.getBlessingEffects().isEmpty() && !data.getBlessingCardStacks().isEmpty()) {
      for (Map.Entry<String, Integer> entry : data.getBlessingCardStacks().entrySet()) {
        String id = normalizeEffectId(entry.getKey());
        int value = Math.max(0, entry.getValue() == null ? 0 : entry.getValue());
        if (id.isBlank() || value <= 0) {
          continue;
        }
        EffectDefinition definition = effectDefinitionsById.get(id);
        String name = definition == null ? id : definition.displayName();
        int tier = Math.min(cardsMaxTier(), value);
        data.putBlessingEffect(new ActiveSeasonEffect(id, name, tier, baseDay, expireDay, false, 0L));
      }
    }

    if (data.getCurseEffects().isEmpty() && !data.getCurseCardStacks().isEmpty()) {
      for (Map.Entry<String, Integer> entry : data.getCurseCardStacks().entrySet()) {
        String id = normalizeEffectId(entry.getKey());
        int value = Math.max(0, entry.getValue() == null ? 0 : entry.getValue());
        if (id.isBlank() || value <= 0) {
          continue;
        }
        EffectDefinition definition = effectDefinitionsById.get(id);
        String name = definition == null ? id : definition.displayName();
        int tier = Math.min(cardsMaxTier(), value);
        boolean severe = definition != null && definition.severeCurse();
        long severeBonus = definition == null ? 0L : definition.severeBonusPoints();
        data.putCurseEffect(new ActiveSeasonEffect(id, name, tier, baseDay, expireDay, severe, severeBonus));
      }
    }
  }

  private void saveState() {
    if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
      getLogger().warning("Could not create plugin data folder: " + getDataFolder());
      return;
    }

    YamlConfiguration yaml = new YamlConfiguration();
    yaml.set("round.id", roundId);
    yaml.set("round.started_epoch_second", roundStartedEpochSecond);
    yaml.set("round.border_shrink_started_epoch_second", borderShrinkStartedEpochSecond);
    yaml.set("round.state", state.name());
    yaml.set("round.state_before_reset_pending", stateBeforeResetPending.name());
    yaml.set("round.reset_pending_until_epoch_second", resetPendingUntilEpochSecond);
    yaml.set("round.free_play_started_epoch_second", freePlayStartedEpochSecond);
    yaml.set("round.free_play_escape_deadline_epoch_second", freePlayEscapeDeadlineEpochSecond);
    yaml.set("round.free_play_finalized", freePlayFinalized);
    yaml.set("round.next_stalker_spawn_epoch_second", nextStalkerSpawnEpochSecond);
    yaml.set("round.stalker_empower_stacks", stalkerEmpowerStacks);
    yaml.set("round.next_raid_wave_epoch_second", nextRaidWaveEpochSecond);
    yaml.set("round.dragon_raid_phase", dragonRaidPhase);
    yaml.set("round.dragon_raid_phase2_burst_done", dragonRaidPhase2BurstDone);
    yaml.set("round.next_survival_score_epoch_second", nextSurvivalScoreEpochSecond);
    yaml.set("round.last_card_draw_ingame_day", lastCardDrawIngameDay);
    yaml.set("round.dragon_egg_bonus_owner", dragonEggBonusOwner == null ? null : dragonEggBonusOwner.toString());
    yaml.set("round.dragon_damage", null);
    for (Map.Entry<UUID, Double> entry : dragonDamageByPlayer.entrySet()) {
      double value = Math.max(0.0D, entry.getValue());
      if (value <= 0.0D) {
        continue;
      }
      yaml.set("round.dragon_damage." + entry.getKey(), value);
    }

    for (Map.Entry<UUID, PlayerRoundData> entry : players.entrySet()) {
      UUID uuid = entry.getKey();
      PlayerRoundData data = entry.getValue();
      String path = "players." + uuid;
      yaml.set(path + ".participant", participants.contains(uuid));
      yaml.set(path + ".score", data.getScore());
      yaml.set(path + ".peak_score", data.getPeakScore());
      yaml.set(path + ".lives_remaining", data.getLivesRemaining());
      yaml.set(path + ".death_count", data.getDeathCount());
      yaml.set(path + ".out", data.isOut());
      yaml.set(path + ".newbie_protect_until_epoch_second", data.getNewbieProtectUntilEpochSecond());
      yaml.set(path + ".score_snapshot", data.getScoreSnapshot());
      yaml.set(path + ".escaped_within_window", data.isEscapedWithinWindow());
      yaml.set(path + ".winner_rank", data.getWinnerRank());
      yaml.set(path + ".winner_bonus_granted", data.isWinnerBonusGranted());
      yaml.set(path + ".join_spawn_assigned", data.isJoinSpawnAssigned());
      yaml.set(path + ".blessing_slots_unlocked", data.getBlessingSlotsUnlocked());
      yaml.set(path + ".curse_slots_unlocked", data.getCurseSlotsUnlocked());
      yaml.set(path + ".initial_card_roll_completed", data.isInitialCardRollCompleted());
      yaml.set(path + ".initial_card_rerolls_used", data.getInitialCardRerollsUsed());

      yaml.set(path + ".blessing_effects", null);
      for (Map.Entry<String, ActiveSeasonEffect> effectEntry : data.getBlessingEffects().entrySet()) {
        String effectPath = path + ".blessing_effects." + effectEntry.getKey();
        writeActiveEffect(yaml, effectPath, effectEntry.getValue());
      }

      yaml.set(path + ".curse_effects", null);
      for (Map.Entry<String, ActiveSeasonEffect> effectEntry : data.getCurseEffects().entrySet()) {
        String effectPath = path + ".curse_effects." + effectEntry.getKey();
        writeActiveEffect(yaml, effectPath, effectEntry.getValue());
      }

      yaml.set(path + ".blessing_card_stacks", null);
      for (Map.Entry<String, Integer> cardEntry : data.getBlessingCardStacks().entrySet()) {
        int value = Math.max(0, cardEntry.getValue() == null ? 0 : cardEntry.getValue());
        if (value <= 0) {
          continue;
        }
        yaml.set(path + ".blessing_card_stacks." + cardEntry.getKey(), value);
      }
      yaml.set(path + ".curse_card_stacks", null);
      for (Map.Entry<String, Integer> cardEntry : data.getCurseCardStacks().entrySet()) {
        int value = Math.max(0, cardEntry.getValue() == null ? 0 : cardEntry.getValue());
        if (value <= 0) {
          continue;
        }
        yaml.set(path + ".curse_card_stacks." + cardEntry.getKey(), value);
      }
    }

    try {
      yaml.save(stateFile);
    } catch (IOException exception) {
      getLogger().severe("Failed to save state.yml: " + exception.getMessage());
    }

    if (seasonStateDatabaseEnabled() && seasonStateRepository != null && seasonStateRepository.isReady()) {
      try {
        seasonStateRepository.saveStateBlob(seasonId(), currentServerName(), yaml.saveToString());
      } catch (SQLException exception) {
        getLogger().warning("Failed to save state to DB: " + exception.getMessage());
      }
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    String cmd = command.getName().toLowerCase(Locale.ROOT);

    if ("season".equals(cmd)) {
      return onSeasonCommand(sender, args);
    }
    if ("slot".equals(cmd) || "perk".equals(cmd)) {
      return onSlotCommand(sender, args);
    }
    if ("life".equals(cmd)) {
      return onLifeCommand(sender, args);
    }
    if ("score".equals(cmd)) {
      return onScoreCommand(sender, args);
    }
    if ("vault".equals(cmd)) {
      return onVaultCommand(sender, args);
    }
    if ("border".equals(cmd)) {
      return onBorderCommand(sender, args);
    }
    if ("information".equals(cmd)) {
      return onInformationCommand(sender, args);
    }
    return false;
  }

  private boolean onSeasonCommand(CommandSender sender, String[] args) {
    if (!isSeasonGameplayServer()) {
      sender.sendMessage("[Season] This command is only available on " + vaultDepositServer() + ".");
      return true;
    }

    if (args.length == 0) {
      sender.sendMessage("[Season] /season state [HARDCORE|CLIMAX|FREE_PLAY|RESET_PENDING]");
      sender.sendMessage("[Season] /season round info");
      sender.sendMessage("[Season] /season slot [player]");
      sender.sendMessage("[Season] /perk [player]");
      sender.sendMessage("[Season] /season debug [on|off]");
      sender.sendMessage("[Season] /season effect give <player> <B-xxx|C-xxx> [tier]");
      sender.sendMessage("[Season] /season catalog [show|reload]");
      sender.sendMessage("[Season] /season profile [show|reload|set <name>]");
      sender.sendMessage("[Season] /season reset now");
      return true;
    }

    String sub = args[0].toLowerCase(Locale.ROOT);

    if ("state".equals(sub)) {
      if (args.length == 1) {
        sender.sendMessage("[Season] state=" + state.name());
        return true;
      }

      SeasonState targetState = SeasonState.parse(args[1]).orElse(null);
      if (targetState == null) {
        sender.sendMessage("[Season] Invalid state: " + args[1]);
        return true;
      }

      if (targetState == SeasonState.RESET_PENDING) {
        if (state != SeasonState.RESET_PENDING) {
          stateBeforeResetPending = state;
        }
        state = SeasonState.RESET_PENDING;
        resetPendingUntilEpochSecond = nowEpochSecond() + resetCountdownSeconds();
      } else if (targetState == SeasonState.FREE_PLAY) {
        enterFreePlay("manual state change");
        sender.sendMessage("[Season] state changed to " + state.name());
        return true;
      } else {
        state = targetState;
        resetPendingUntilEpochSecond = 0L;
      }

      saveState();
      sender.sendMessage("[Season] state changed to " + state.name());
      return true;
    }

    if ("round".equals(sub) && args.length >= 2 && "info".equalsIgnoreCase(args[1])) {
      int outCount = 0;
      for (UUID participant : participants) {
        PlayerRoundData data = players.get(participant);
        if (data != null && data.isOut()) {
          outCount++;
        }
      }

      sender.sendMessage("[Season] round.id=" + roundId);
      sender.sendMessage("[Season] round.state=" + state.name());
      sender.sendMessage("[Season] participants=" + participants.size() + " out=" + outCount);
      sender.sendMessage(
          "[Season] border.center=" + (int) borderCenterX() + "," + (int) borderCenterZ()
              + " radius=" + String.format(Locale.ROOT, "%.1f", borderCurrentRadius())
      );
      sender.sendMessage("[Season] vault.deposit_server=" + vaultDepositServer());
      sender.sendMessage("[Season] vault.withdraw_servers=" + String.join(",", vaultWithdrawServers()));
      sender.sendMessage("[Season] vault.enabled_in_state=" + vaultEnabledState());
      sender.sendMessage("[Season] season.id=" + seasonId());
      sender.sendMessage("[Season] season.active_profile=" + activeSeasonProfile());
      sender.sendMessage("[Season] slots.enabled=" + slotsEnabled() + " source=" + slotProgressScoreSource());
      sender.sendMessage("[Season] slots.start=" + slotStartCount() + " max=" + slotBaseMax() + " per_unlock=" + slotProgressPerUnlock());

      if (state == SeasonState.RESET_PENDING) {
        long remaining = Math.max(0L, resetPendingUntilEpochSecond - nowEpochSecond());
        sender.sendMessage("[Season] reset_pending_remaining=" + remaining + "s");
      }
      if (state == SeasonState.FREE_PLAY) {
        long remaining = Math.max(0L, freePlayEscapeDeadlineEpochSecond - nowEpochSecond());
        int escaped = 0;
        int eligible = 0;
        for (UUID participant : participants) {
          PlayerRoundData data = players.get(participant);
          if (data == null || !data.hasScoreSnapshot()) {
            continue;
          }
          eligible++;
          if (data.isEscapedWithinWindow()) {
            escaped++;
          }
        }
        sender.sendMessage("[Season] free_play.escape_remaining=" + remaining + "s");
        sender.sendMessage("[Season] free_play.finalized=" + freePlayFinalized);
        sender.sendMessage("[Season] free_play.escaped=" + escaped + "/" + eligible);
      }
      return true;
    }

    if ("profile".equals(sub)) {
      return onSeasonProfileCommand(sender, args);
    }

    if ("catalog".equals(sub)) {
      return onSeasonCatalogCommand(sender, args);
    }

    if ("debug".equals(sub)) {
      return onSeasonDebugCommand(sender, args);
    }

    if ("slot".equals(sub)) {
      return onSeasonSlotCommand(sender, args);
    }

    if ("effect".equals(sub)) {
      return onSeasonEffectCommand(sender, args);
    }

    if ("reset".equals(sub) && args.length >= 2 && "now".equalsIgnoreCase(args[1])) {
      executeRoundReset();
      sender.sendMessage("[Season] round reset executed.");
      return true;
    }

    sender.sendMessage("[Season] Unknown subcommand.");
    return true;
  }

  private boolean onSlotCommand(CommandSender sender, String[] args) {
    if (!isSeasonGameplayServer()) {
      sender.sendMessage("[Season] This command is only available on " + vaultDepositServer() + ".");
      return true;
    }
    String[] forwarded = new String[(args == null ? 0 : args.length) + 1];
    forwarded[0] = "slot";
    if (args != null && args.length > 0) {
      System.arraycopy(args, 0, forwarded, 1, args.length);
    }
    return onSeasonSlotCommand(sender, forwarded);
  }

  private boolean onSeasonDebugCommand(CommandSender sender, String[] args) {
    if (args.length <= 1) {
      sender.sendMessage("[Season] debug=" + (seasonDebugModeEnabled() ? "ON" : "OFF"));
      sender.sendMessage("[Season] one_time_roll.enabled=" + cardsOneTimeRollEnabled());
      sender.sendMessage("[Season] one_time_roll.initial_free_rerolls=" + cardsInitialSetupFreeRerolls());
      sender.sendMessage("[Season] reroll.cost_score=" + cardsRerollCostScore());
      sender.sendMessage("[Season] reroll.cooldown_seconds=" + cardsRerollCooldownSeconds());
      sender.sendMessage("[Season] tier_upgrade.cost_score=" + cardsTierUpgradeCostScore());
      sender.sendMessage("[Season] /season debug [on|off]");
      return true;
    }
    String sub = args[1].toLowerCase(Locale.ROOT);
    if (!"on".equals(sub) && !"off".equals(sub)) {
      sender.sendMessage("[Season] /season debug [on|off]");
      return true;
    }
    boolean enabled = "on".equals(sub);
    getConfig().set("season.debug_mode", enabled);
    saveConfig();
    sender.sendMessage("[Season] debug mode set to " + (enabled ? "ON" : "OFF"));
    sender.sendMessage("[Season] one_time_roll.enabled=" + cardsOneTimeRollEnabled());
    sender.sendMessage("[Season] one_time_roll.initial_free_rerolls=" + cardsInitialSetupFreeRerolls());
    sender.sendMessage("[Season] reroll.cost_score=" + cardsRerollCostScore());
    sender.sendMessage("[Season] reroll.cooldown_seconds=" + cardsRerollCooldownSeconds());
    sender.sendMessage("[Season] tier_upgrade.cost_score=" + cardsTierUpgradeCostScore());
    return true;
  }

  private boolean onSeasonCatalogCommand(CommandSender sender, String[] args) {
    String action = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "show";
    if ("show".equals(action)) {
      File catalogDir = new File(getDataFolder(), "catalogs");
      List<String> appendFiles = cardsCatalogAppendFileNames();
      List<String> patchFiles = resolveCatalogPatchFileNames(catalogDir);
      sender.sendMessage("[Season] catalog.file=" + cardsCatalogFileName());
      sender.sendMessage(
          "[Season] catalog.append_files=" + (appendFiles.isEmpty() ? "(none)" : String.join(", ", appendFiles))
      );
      sender.sendMessage(
          "[Season] catalog.patch_dir=" + cardsCatalogPatchDirName()
              + " auto_discover=" + cardsCatalogPatchAutoDiscover()
      );
      sender.sendMessage(
          "[Season] catalog.patch_files=" + (patchFiles.isEmpty() ? "(none)" : String.join(", ", patchFiles))
      );
      sender.sendMessage(
          "[Season] catalog.loaded blessings=" + blessingEffectsCatalog.size()
              + " curses=" + curseEffectsCatalog.size()
      );
      sender.sendMessage("[Season] /season catalog reload");
      return true;
    }

    if ("reload".equals(action)) {
      reloadEffectCatalog();
      syncAllParticipantSlots(false);
      saveState();
      sender.sendMessage(
          "[Season] catalog reloaded. blessings=" + blessingEffectsCatalog.size()
              + ", curses=" + curseEffectsCatalog.size()
      );
      return true;
    }

    sender.sendMessage("[Season] /season catalog [show|reload]");
    return true;
  }

  private boolean onSeasonSlotCommand(CommandSender sender, String[] args) {
    OfflinePlayer target;
    boolean admin = sender.hasPermission("seasonmanager.admin");
    UUID senderUuid = sender instanceof Player viewer ? viewer.getUniqueId() : null;
    if (args.length >= 2) {
      target = resolvePlayer(args[1]);
      if (target == null) {
        sender.sendMessage("[Season] Unknown player: " + args[1]);
        return true;
      }
      if (!admin) {
        if (senderUuid == null || !senderUuid.equals(target.getUniqueId())) {
          sender.sendMessage("[Season] You can only open your own slot. (관리자 권한 필요)");
          return true;
        }
      }
    } else if (sender instanceof Player player) {
      target = player;
    } else {
      sender.sendMessage("[Season] /perk <player> (or /season slot <player>)");
      return true;
    }

    UUID uuid = target.getUniqueId();
    PlayerRoundData data = players.get(uuid);
    if (data == null) {
      sender.sendMessage("[Season] No round data for " + target.getName() + ".");
      return true;
    }

    syncPlayerSlots(uuid, data, false);
    if (sender instanceof Player viewer) {
      openSeasonSlotOverview(viewer, target, data);
    } else {
      sendSeasonSlotSummary(sender, uuid, data);
    }
    return true;
  }

  private void sendSeasonSlotSummary(CommandSender sender, UUID uuid, PlayerRoundData data) {
    sender.sendMessage("[Season] 슬롯 대상=" + playerDisplayName(uuid));
    sender.sendMessage("[Season] 진행 점수=" + slotProgressScore(data) + " (기준=" + slotProgressScoreSource() + ")");
    sender.sendMessage("[Season] 축복 슬롯=" + data.getBlessingSlotsUnlocked() + "/" + slotBlessingMax());
    sender.sendMessage("[Season] 저주 슬롯=" + data.getCurseSlotsUnlocked() + "/" + slotCurseMax());
    sender.sendMessage(
        "[Season] 축복 활성 카드=" + data.getActiveBlessingEffectCount()
            + " (티어합=" + data.getTotalBlessingEffectTier() + ")"
    );
    sender.sendMessage(
        "[Season] 저주 활성 카드=" + data.getActiveCurseEffectCount()
            + " (티어합=" + data.getTotalCurseEffectTier() + ")"
    );
    if (cardsOneTimeRollEnabled()) {
      sender.sendMessage("[Season] 추첨 모드=최초 1회 + 점수 리롤");
      sender.sendMessage(
          "[Season] 초기 무료 리롤 사용="
              + data.getInitialCardRerollsUsed()
              + "/" + cardsInitialSetupFreeRerolls()
      );
      sender.sendMessage("[Season] 리롤 비용=" + cardsRerollCostScore() + ", 티어강화 비용=" + cardsTierUpgradeCostScore());
    } else {
      sender.sendMessage(
          "[Season] 추가 추첨 횟수=" + cardsBonusDrawCount(data)
              + " (기준=" + cardsDrawBonusScoreSource() + ")"
      );
    }
    sender.sendMessage(
        "[Season] 생성 점수 배율=x"
            + String.format(Locale.ROOT, "%.2f", generatedScoreMultiplier(uuid, data))
    );
  }

  private void openSeasonSlotOverview(Player viewer, OfflinePlayer target, PlayerRoundData data) {
    openSeasonSlotOverview(
        viewer,
        target,
        data,
        SLOT_GUI_PAGE_PRIMARY,
        0,
        SLOT_GUI_RESIDUAL_FILTER_ALL
    );
  }

  private void openSeasonSlotOverview(Player viewer, OfflinePlayer target, PlayerRoundData data, int page) {
    openSeasonSlotOverview(
        viewer,
        target,
        data,
        page,
        0,
        SLOT_GUI_RESIDUAL_FILTER_ALL
    );
  }

  private void openSeasonSlotOverview(
      Player viewer,
      OfflinePlayer target,
      PlayerRoundData data,
      int page,
      int residualPage,
      int residualFilterMode
  ) {
    if (viewer == null || !viewer.isOnline() || target == null || data == null) {
      return;
    }

    UUID targetUuid = target.getUniqueId();
    String targetName = playerDisplayName(targetUuid);
    int currentPage = SLOT_GUI_PAGE_PRIMARY;
    int resolvedFilterMode = normalizeResidualFilterMode(residualFilterMode);
    int resolvedResidualPage = Math.max(0, residualPage);
    long currentDay = currentIngameDay();

    List<ActiveSeasonEffect> blessingResidualRaw = List.of();
    List<ActiveSeasonEffect> curseResidualRaw = List.of();
    List<ActiveSeasonEffect> blessingResidualFiltered = List.of();
    List<ActiveSeasonEffect> curseResidualFiltered = List.of();
    int residualMaxPage = 0;
    int residualSlotsPerSide = 17;
    if (currentPage == SLOT_GUI_PAGE_RESIDUAL) {
      blessingResidualRaw = residualActiveEffects(
          sortedActiveEffects(data.getBlessingEffects()),
          data.getBlessingSlotsUnlocked()
      );
      curseResidualRaw = residualActiveEffects(
          sortedActiveEffects(data.getCurseEffects()),
          data.getCurseSlotsUnlocked()
      );
      blessingResidualFiltered = filterResidualEffects(blessingResidualRaw, currentDay, resolvedFilterMode);
      curseResidualFiltered = filterResidualEffects(curseResidualRaw, currentDay, resolvedFilterMode);
      int filteredMax = Math.max(blessingResidualFiltered.size(), curseResidualFiltered.size());
      residualMaxPage = filteredMax <= 0 ? 0 : (int) Math.ceil(filteredMax / (double) residualSlotsPerSide) - 1;
      resolvedResidualPage = Math.max(0, Math.min(resolvedResidualPage, residualMaxPage));
    }

    String titlePrefix = currentPage == SLOT_GUI_PAGE_RESIDUAL ? "잔여 " : "슬롯 ";
    String title = titlePrefix + slotTitleName(targetName);
    SeasonSlotInventoryHolder holder = new SeasonSlotInventoryHolder(
        targetUuid,
        targetName,
        currentPage,
        resolvedResidualPage,
        resolvedFilterMode
    );
    Inventory inventory = Bukkit.createInventory(holder, 54, title);
    holder.bindInventory(inventory);

    EnumMap<EffectArchetype, Integer> blessingTierTotals = aggregateArchetypeTierTotals(data.getBlessingEffects());
    EnumMap<EffectArchetype, Integer> curseTierTotals = aggregateArchetypeTierTotals(data.getCurseEffects());

    if (currentPage == SLOT_GUI_PAGE_RESIDUAL) {
      populateSlotOverviewResidualPage(
          inventory,
          targetUuid,
          targetName,
          data,
          blessingTierTotals,
          curseTierTotals,
          currentDay,
          blessingResidualRaw,
          curseResidualRaw,
          blessingResidualFiltered,
          curseResidualFiltered,
          resolvedResidualPage,
          residualMaxPage,
          resolvedFilterMode
      );
    } else {
      populateSlotOverviewPrimaryPage(
          inventory,
          targetUuid,
          targetName,
          data,
          blessingTierTotals,
          curseTierTotals,
          currentDay
      );
    }

    inventory.setItem(SLOT_GUI_PAGE_INFO_SLOT, seasonSlotPageInfoItem(currentPage, resolvedResidualPage, residualMaxPage, resolvedFilterMode));
    inventory.setItem(SLOT_GUI_REROLL_BUTTON_SLOT, seasonSlotRerollButtonItem(viewer, targetUuid, data));
    inventory.setItem(SLOT_GUI_UPGRADE_BUTTON_SLOT, seasonSlotTierUpgradeButtonItem(viewer, targetUuid, data));
    if (seasonDebugModeEnabled()) {
      inventory.setItem(SLOT_GUI_DEBUG_OP_BUTTON_SLOT, seasonSlotDebugOpButtonItem(viewer, targetUuid));
    }
    if (currentPage == SLOT_GUI_PAGE_RESIDUAL) {
      inventory.setItem(SLOT_GUI_RESIDUAL_PREV_PAGE_SLOT, seasonSlotResidualPrevPageItem(resolvedResidualPage));
      inventory.setItem(SLOT_GUI_RESIDUAL_FILTER_SLOT, seasonSlotResidualFilterItem(resolvedFilterMode));
      inventory.setItem(SLOT_GUI_RESIDUAL_NEXT_PAGE_SLOT, seasonSlotResidualNextPageItem(resolvedResidualPage, residualMaxPage));
    }

    for (int slot = 45; slot <= 53; slot++) {
      if (inventory.getItem(slot) == null) {
        inventory.setItem(slot, makeGuiItem(
            Material.GRAY_STAINED_GLASS_PANE,
            ChatColor.DARK_GRAY + " ",
            List.of()
        ));
      }
    }

    viewer.openInventory(inventory);
    viewer.sendMessage("[Season] " + targetName + "의 슬롯 현황을 열었습니다.");
  }

  private void populateSlotOverviewPrimaryPage(
      Inventory inventory,
      UUID targetUuid,
      String targetName,
      PlayerRoundData data,
      EnumMap<EffectArchetype, Integer> blessingTierTotals,
      EnumMap<EffectArchetype, Integer> curseTierTotals,
      long currentDay
  ) {
    if (inventory == null || targetUuid == null || data == null) {
      return;
    }
    List<ActiveSeasonEffect> blessingEffects = sortedActiveEffects(data.getBlessingEffects());
    List<ActiveSeasonEffect> curseEffects = sortedActiveEffects(data.getCurseEffects());
    int blessingDisplayCap = SLOT_GUI_BLESSING_DETAIL_BLOCK_STARTS.length;
    int curseDisplayCap = SLOT_GUI_CURSE_DETAIL_BLOCK_STARTS.length;
    int blessingHidden = Math.max(0, blessingEffects.size() - blessingDisplayCap);
    int curseHidden = Math.max(0, curseEffects.size() - curseDisplayCap);

    inventory.setItem(0, makeGuiItem(
        Material.NAME_TAG,
        ChatColor.AQUA + "플레이어: " + ChatColor.WHITE + targetName,
        List.of(
            ChatColor.GRAY + "라운드 " + roundId + " / " + state.name(),
            ChatColor.GRAY + "진행 점수 " + slotProgressScore(data) + " (" + slotProgressScoreSource() + ")"
        )
    ));
    inventory.setItem(1, makeGuiItem(
        Material.LIME_STAINED_GLASS_PANE,
        ChatColor.GREEN + "축복 슬롯(좌측)",
        List.of(
            ChatColor.GRAY + "해금 " + data.getBlessingSlotsUnlocked() + " / " + slotBlessingMax(),
            ChatColor.GRAY + "표시 최대 " + blessingDisplayCap + "개"
        )
    ));
    inventory.setItem(2, makeGuiItem(
        Material.RED_STAINED_GLASS_PANE,
        ChatColor.RED + "저주 슬롯(우측)",
        List.of(
            ChatColor.GRAY + "해금 " + data.getCurseSlotsUnlocked() + " / " + slotCurseMax(),
            ChatColor.GRAY + "표시 최대 " + curseDisplayCap + "개"
        )
    ));
    inventory.setItem(3, seasonSlotDrawModeItem(data));
    inventory.setItem(4, makeGuiItem(
        Material.NETHER_STAR,
        ChatColor.YELLOW + "점수 배율",
        List.of(
            ChatColor.GRAY + "생성 점수 x" + String.format(Locale.ROOT, "%.2f", generatedScoreMultiplier(targetUuid, data))
        )
    ));
    inventory.setItem(5, makeGuiItem(
        Material.LIME_DYE,
        ChatColor.GREEN + "축복 카드 5칸 구성",
        List.of(
            ChatColor.GRAY + "스탯 / 특수 효과 / T2 / T4 / T6",
            ChatColor.DARK_GRAY + "좌측 행에 순서대로 배치"
        )
    ));
    inventory.setItem(6, makeGuiItem(
        Material.RED_DYE,
        ChatColor.RED + "저주 카드 5칸 구성",
        List.of(
            ChatColor.GRAY + "스탯 / 특수 효과 / T2 / T4 / T6",
            ChatColor.DARK_GRAY + "하단 행에 순서대로 배치"
        )
    ));
    inventory.setItem(7, makeGuiItem(
        Material.PAPER,
        ChatColor.YELLOW + "표시 범위",
        List.of(
            ChatColor.GRAY + "활성 축복 " + blessingEffects.size() + "개, 저주 " + curseEffects.size() + "개",
            ChatColor.GRAY + "행당 1개 · 축복 2행 / 저주 2행",
            ChatColor.DARK_GRAY + "미표시: 축복 " + blessingHidden + " / 저주 " + curseHidden
        )
    ));
    inventory.setItem(8, makeGuiItem(
        Material.BOOK,
        ChatColor.AQUA + "읽는 법",
        List.of(
            ChatColor.GRAY + "/perk 로 슬롯 UI를 엽니다",
            ChatColor.GRAY + "카드당 가로 5칸(스탯/특수/T2/T4/T6)",
            ChatColor.GRAY + "로어에는 현재 티어 기준 수치만 표시됩니다"
        )
    ));

    for (int i = 0; i < SLOT_GUI_DETAIL_ROW_INFO_SLOTS.length; i++) {
      int slot = SLOT_GUI_DETAIL_ROW_INFO_SLOTS[i];
      int rowNo = i + 1;
      inventory.setItem(slot, makeGuiItem(
          Material.GRAY_STAINED_GLASS_PANE,
          ChatColor.GOLD + "행 #" + rowNo,
          List.of(
              ChatColor.GRAY + "구성: 스탯 / 특수 / T2 / T4 / T6",
              ChatColor.DARK_GRAY + "T4가 비어 있으면 차기 패치 예정"
          )
      ));
    }

    populateEffectDetailBlocks(
        inventory,
        SLOT_GUI_BLESSING_DETAIL_BLOCK_STARTS,
        blessingEffects,
        data.getBlessingSlotsUnlocked(),
        slotBlessingMax(),
        EffectKind.BLESSING,
        blessingTierTotals,
        currentDay
    );
    populateEffectDetailBlocks(
        inventory,
        SLOT_GUI_CURSE_DETAIL_BLOCK_STARTS,
        curseEffects,
        data.getCurseSlotsUnlocked(),
        slotCurseMax(),
        EffectKind.CURSE,
        curseTierTotals,
        currentDay
    );
  }

  private void populateSlotOverviewResidualPage(
      Inventory inventory,
      UUID targetUuid,
      String targetName,
      PlayerRoundData data,
      EnumMap<EffectArchetype, Integer> blessingTierTotals,
      EnumMap<EffectArchetype, Integer> curseTierTotals,
      long currentDay,
      List<ActiveSeasonEffect> blessingResidualRaw,
      List<ActiveSeasonEffect> curseResidualRaw,
      List<ActiveSeasonEffect> blessingResidualFiltered,
      List<ActiveSeasonEffect> curseResidualFiltered,
      int residualPage,
      int residualMaxPage,
      int residualFilterMode
  ) {
    if (inventory == null || targetUuid == null || data == null) {
      return;
    }
    List<ActiveSeasonEffect> blessingRaw = blessingResidualRaw == null ? List.of() : blessingResidualRaw;
    List<ActiveSeasonEffect> curseRaw = curseResidualRaw == null ? List.of() : curseResidualRaw;
    List<ActiveSeasonEffect> blessingFiltered = blessingResidualFiltered == null ? List.of() : blessingResidualFiltered;
    List<ActiveSeasonEffect> curseFiltered = curseResidualFiltered == null ? List.of() : curseResidualFiltered;
    List<ActiveSeasonEffect> blessingPage = paginateResidualEffects(blessingFiltered, residualPage, 17);
    List<ActiveSeasonEffect> cursePage = paginateResidualEffects(curseFiltered, residualPage, 17);
    String filterLabel = residualFilterLabel(residualFilterMode);
    String pageLabel = (residualPage + 1) + "/" + (residualMaxPage + 1);

    inventory.setItem(0, makeGuiItem(
        Material.NAME_TAG,
        ChatColor.AQUA + "플레이어: " + ChatColor.WHITE + targetName,
        List.of(
            ChatColor.GRAY + "라운드 " + roundId + " / " + state.name(),
            ChatColor.GRAY + "슬롯 외 잔여 효과 페이지",
            ChatColor.GRAY + "필터: " + filterLabel + " / 페이지: " + pageLabel
        )
    ));
    inventory.setItem(1, makeGuiItem(
        Material.LIME_STAINED_GLASS_PANE,
        ChatColor.GREEN + "잔여 축복 효과",
        List.of(
            ChatColor.GRAY + "전체: " + blessingRaw.size() + "개",
            ChatColor.GRAY + "필터 적용: " + blessingFiltered.size() + "개",
            ChatColor.GRAY + "현재 페이지: " + blessingPage.size() + "개"
        )
    ));
    inventory.setItem(2, makeGuiItem(
        Material.RED_STAINED_GLASS_PANE,
        ChatColor.RED + "잔여 저주 효과",
        List.of(
            ChatColor.GRAY + "전체: " + curseRaw.size() + "개",
            ChatColor.GRAY + "필터 적용: " + curseFiltered.size() + "개",
            ChatColor.GRAY + "현재 페이지: " + cursePage.size() + "개"
        )
    ));
    inventory.setItem(3, seasonSlotDrawModeItem(data));
    inventory.setItem(4, makeGuiItem(
        Material.NETHER_STAR,
        ChatColor.YELLOW + "점수 배율",
        List.of(
            ChatColor.GRAY + "생성 점수 x" + String.format(Locale.ROOT, "%.2f", generatedScoreMultiplier(targetUuid, data))
        )
    ));
    inventory.setItem(8, makeGuiItem(
        Material.BOOK,
        ChatColor.AQUA + "읽는 법",
        List.of(
            ChatColor.GRAY + "상단: 축복 잔여 효과",
            ChatColor.GRAY + "하단: 저주 잔여 효과",
            ChatColor.GRAY + "슬롯 외 카드의 실제 수치도 확인 가능합니다",
            ChatColor.YELLOW + "필터 버튼으로 조건을 바꿀 수 있습니다"
        )
    ));

    inventory.setItem(9, makeGuiItem(
        Material.LIME_DYE,
        ChatColor.GREEN + "축복 잔여 효과",
        List.of(ChatColor.GRAY + "활성 슬롯 외에 보관 중인 축복 카드")
    ));
    inventory.setItem(27, makeGuiItem(
        Material.RED_DYE,
        ChatColor.RED + "저주 잔여 효과",
        List.of(ChatColor.GRAY + "활성 슬롯 외에 보관 중인 저주 카드")
    ));

    populateResidualEffectSection(
        inventory,
        10,
        26,
        blessingPage,
        !blessingRaw.isEmpty(),
        EffectKind.BLESSING,
        blessingTierTotals,
        currentDay
    );
    populateResidualEffectSection(
        inventory,
        28,
        44,
        cursePage,
        !curseRaw.isEmpty(),
        EffectKind.CURSE,
        curseTierTotals,
        currentDay
    );
  }

  private ItemStack seasonSlotPageInfoItem(int page, int residualPage, int residualMaxPage, int residualFilterMode) {
    return makeGuiItem(
        Material.BOOK,
        ChatColor.AQUA + "페이지 1/1: 슬롯 현황",
        List.of(
            ChatColor.GRAY + "카드당 5칸: 스탯 / 특수 / T2 / T4 / T6",
            ChatColor.GRAY + "T4 전용 효과가 비면 후속 패치 예정",
            ChatColor.DARK_GRAY + "잠금 슬롯 포함"
        )
    );
  }

  private ItemStack seasonSlotResidualPrevPageItem(int residualPage) {
    if (residualPage <= 0) {
      return makeGuiItem(
          Material.GRAY_DYE,
          ChatColor.DARK_GRAY + "이전 페이지 없음",
          List.of(ChatColor.GRAY + "현재 첫 페이지입니다")
      );
    }
    return makeGuiItem(
        Material.ARROW,
        ChatColor.YELLOW + "이전 페이지",
        List.of(
            ChatColor.GRAY + "좌클릭: 이전 잔여 페이지",
            ChatColor.DARK_GRAY + "현재: " + (residualPage + 1)
        )
    );
  }

  private ItemStack seasonSlotResidualNextPageItem(int residualPage, int residualMaxPage) {
    if (residualPage >= residualMaxPage) {
      return makeGuiItem(
          Material.GRAY_DYE,
          ChatColor.DARK_GRAY + "다음 페이지 없음",
          List.of(ChatColor.GRAY + "현재 마지막 페이지입니다")
      );
    }
    return makeGuiItem(
        Material.ARROW,
        ChatColor.YELLOW + "다음 페이지",
        List.of(
            ChatColor.GRAY + "좌클릭: 다음 잔여 페이지",
            ChatColor.DARK_GRAY + "현재: " + (residualPage + 1) + "/" + (residualMaxPage + 1)
        )
    );
  }

  private ItemStack seasonSlotResidualFilterItem(int residualFilterMode) {
    String label = residualFilterLabel(residualFilterMode);
    return makeGuiItem(
        Material.HOPPER,
        ChatColor.AQUA + "잔여 필터",
        List.of(
            ChatColor.GRAY + "현재: " + label,
            ChatColor.GRAY + "좌클릭: 필터 순환",
            ChatColor.DARK_GRAY + "전체 -> T3+ -> 만료임박"
        )
    );
  }

  private ItemStack seasonSlotPageSwitchItem(int currentPage) {
    if (currentPage == SLOT_GUI_PAGE_RESIDUAL) {
      return makeGuiItem(
          Material.ARROW,
          ChatColor.YELLOW + "슬롯 페이지로 이동",
          List.of(
              ChatColor.GRAY + "좌클릭: 페이지 1/2 열기",
              ChatColor.DARK_GRAY + "활성/잠금 슬롯 화면"
          )
      );
    }
    return makeGuiItem(
        Material.ARROW,
        ChatColor.YELLOW + "잔여 효과 페이지로 이동",
        List.of(
            ChatColor.GRAY + "좌클릭: 페이지 2/2 열기",
            ChatColor.DARK_GRAY + "슬롯 외 잔여 효과 화면"
        )
    );
  }

  private ItemStack seasonSlotDrawModeItem(PlayerRoundData data) {
    if (cardsOneTimeRollEnabled()) {
      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.GRAY + "최초 접속 시 1회 자동 추첨");
      lore.add(ChatColor.YELLOW + "카드 옵션은 리롤 전까지 유지됩니다");
      lore.add(ChatColor.GRAY + "초기 무료 리롤: " + cardsInitialSetupFreeRerolls() + "회");
      lore.add(ChatColor.GRAY + "일반 리롤 비용: " + cardsRerollCostScore() + " 점수");
      if (cardsTierUpgradeEnabled()) {
        lore.add(ChatColor.GRAY + "동시 티어 강화 비용: " + cardsTierUpgradeCostScore() + " 점수");
      } else {
        lore.add(ChatColor.DARK_GRAY + "동시 티어 강화: 비활성화");
      }
      if (data != null && data.isInitialCardRollCompleted()) {
        int used = data.getInitialCardRerollsUsed();
        int remaining = remainingInitialSetupRerolls(data);
        lore.add(ChatColor.DARK_GRAY + "초기 리롤 사용: " + used + ", 남은 횟수: " + remaining);
      }
      return makeGuiItem(
          Material.CLOCK,
          ChatColor.GOLD + "일회성 추첨 모드",
          lore
      );
    }
    return makeGuiItem(
        Material.EXPERIENCE_BOTTLE,
        ChatColor.GOLD + "추가 추첨",
        List.of(
            ChatColor.YELLOW + "카드 옵션은 리롤 전까지 유지됩니다",
            ChatColor.GRAY + "일일 추가 추첨: " + cardsBonusDrawCount(data),
            ChatColor.GRAY + "기준: " + cardsDrawBonusScoreSource()
        )
    );
  }

  private int normalizeResidualFilterMode(int mode) {
    if (mode < SLOT_GUI_RESIDUAL_FILTER_ALL || mode > SLOT_GUI_RESIDUAL_FILTER_MAX) {
      return SLOT_GUI_RESIDUAL_FILTER_ALL;
    }
    return mode;
  }

  private String residualFilterLabel(int mode) {
    int normalized = normalizeResidualFilterMode(mode);
    return switch (normalized) {
      case SLOT_GUI_RESIDUAL_FILTER_TIER_3_PLUS -> "티어 3+";
      case SLOT_GUI_RESIDUAL_FILTER_EXPIRES_SOON -> "만료 " + SLOT_GUI_RESIDUAL_EXPIRES_SOON_DAYS + "일 이내";
      default -> "전체";
    };
  }

  private String effectRemainingLabel(ActiveSeasonEffect effect, long currentDay) {
    if (effect == null) {
      return "0일";
    }
    if (cardsOneTimeRollEnabled()
        && cardsOneTimeRollNonExpiring()
        && effect.getExpireIngameDay() >= (Long.MAX_VALUE - 2L)) {
      return "상시";
    }
    long remainingDays = Math.max(0L, effect.getExpireIngameDay() - currentDay);
    return remainingDays + "일";
  }

  private List<ActiveSeasonEffect> residualActiveEffects(List<ActiveSeasonEffect> activeEffects, int unlockedSlots) {
    if (activeEffects == null || activeEffects.isEmpty()) {
      return List.of();
    }
    int startIndex = Math.max(0, unlockedSlots);
    if (startIndex >= activeEffects.size()) {
      return List.of();
    }
    List<ActiveSeasonEffect> residual = new ArrayList<>();
    for (int i = startIndex; i < activeEffects.size(); i++) {
      ActiveSeasonEffect effect = activeEffects.get(i);
      if (effect != null) {
        residual.add(effect);
      }
    }
    return residual;
  }

  private List<ActiveSeasonEffect> filterResidualEffects(
      List<ActiveSeasonEffect> residualEffects,
      long currentDay,
      int filterMode
  ) {
    if (residualEffects == null || residualEffects.isEmpty()) {
      return List.of();
    }
    int normalized = normalizeResidualFilterMode(filterMode);
    if (normalized == SLOT_GUI_RESIDUAL_FILTER_ALL) {
      return new ArrayList<>(residualEffects);
    }
    List<ActiveSeasonEffect> filtered = new ArrayList<>();
    for (ActiveSeasonEffect effect : residualEffects) {
      if (effect == null) {
        continue;
      }
      if (normalized == SLOT_GUI_RESIDUAL_FILTER_TIER_3_PLUS) {
        if (effect.getTier() >= 3) {
          filtered.add(effect);
        }
        continue;
      }
      long remainingDays = Math.max(0L, effect.getExpireIngameDay() - currentDay);
      if (remainingDays <= SLOT_GUI_RESIDUAL_EXPIRES_SOON_DAYS) {
        filtered.add(effect);
      }
    }
    return filtered;
  }

  private List<ActiveSeasonEffect> paginateResidualEffects(List<ActiveSeasonEffect> effects, int page, int pageSize) {
    if (effects == null || effects.isEmpty()) {
      return List.of();
    }
    int normalizedPageSize = Math.max(1, pageSize);
    int normalizedPage = Math.max(0, page);
    int from = Math.min(effects.size(), normalizedPage * normalizedPageSize);
    int to = Math.min(effects.size(), from + normalizedPageSize);
    if (from >= to) {
      return List.of();
    }
    List<ActiveSeasonEffect> sliced = new ArrayList<>();
    for (int i = from; i < to; i++) {
      ActiveSeasonEffect effect = effects.get(i);
      if (effect != null) {
        sliced.add(effect);
      }
    }
    return sliced;
  }

  private void populateResidualEffectSection(
      Inventory inventory,
      int fromSlot,
      int toSlot,
      List<ActiveSeasonEffect> residualEffects,
      boolean hasAnyResidual,
      EffectKind kind,
      EnumMap<EffectArchetype, Integer> archetypeTierTotals,
      long currentDay
  ) {
    if (inventory == null || residualEffects == null) {
      return;
    }
    int index = 0;
    for (int slot = fromSlot; slot <= toSlot; slot++) {
      if (index >= residualEffects.size()) {
        Material emptyType = kind == EffectKind.BLESSING ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        String emptyTitle = kind == EffectKind.BLESSING
            ? ChatColor.GREEN + "축복 잔여 없음"
            : ChatColor.RED + "저주 잔여 없음";
        String emptyLore = hasAnyResidual
            ? ChatColor.DARK_GRAY + "필터 조건에 해당하는 카드가 없습니다"
            : ChatColor.DARK_GRAY + "현재 슬롯 외 카드가 없습니다";
        inventory.setItem(slot, makeGuiItem(
            emptyType,
            emptyTitle,
            List.of(emptyLore)
        ));
        continue;
      }

      ActiveSeasonEffect effect = residualEffects.get(index++);
      EffectDefinition definition = effectDefinitionsById.get(effect.getId());
      EffectArchetype archetype = definition == null ? inferArchetype(effect.getId()) : definition.archetype();
      String displayName = (definition != null && definition.displayName() != null && !definition.displayName().isBlank())
          ? definition.displayName()
          : ((effect.getDisplayName() == null || effect.getDisplayName().isBlank()) ? effect.getId() : effect.getDisplayName());
      String remainingLabel = effectRemainingLabel(effect, currentDay);
      int archetypeTotalTier = Math.max(0, archetypeTierTotals == null
          ? 0
          : archetypeTierTotals.getOrDefault(archetype, 0));

      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.DARK_GRAY + effect.getId() + " · " + effectArchetypeLabel(archetype));
      lore.add(ChatColor.GRAY + "티어 T" + Math.max(1, effect.getTier()) + " · 만료 " + remainingLabel);
      lore.add(ChatColor.YELLOW + "상태: 슬롯 외");
      lore.addAll(effectDetailLines(definition, kind, archetype, archetypeTotalTier, effect.getTier()));
      if (kind == EffectKind.CURSE && effect.isSevereCurse()) {
        lore.add(ChatColor.DARK_RED + "중대 저주 보너스 +" + severeBonusFor(effect) + " 점수");
      }

      inventory.setItem(slot, makeGuiItem(
          effectIcon(kind, archetype),
          (kind == EffectKind.BLESSING ? ChatColor.GREEN : ChatColor.RED) + displayName,
          lore
      ));
    }
  }

  private void populateEffectSection(
      Inventory inventory,
      int fromSlot,
      int toSlot,
      List<ActiveSeasonEffect> activeEffects,
      int unlockedSlots,
      int maxSlots,
      EffectKind kind,
      EnumMap<EffectArchetype, Integer> archetypeTierTotals,
      long currentDay
  ) {
    if (inventory == null || activeEffects == null) {
      return;
    }
    int unlocked = Math.max(0, unlockedSlots);
    int maximum = Math.max(0, maxSlots);

    int visibleIndex = 0;
    for (int slot = fromSlot; slot <= toSlot; slot++) {
      if (visibleIndex >= maximum) {
        inventory.setItem(slot, makeGuiItem(
            Material.BLACK_STAINED_GLASS_PANE,
            ChatColor.DARK_GRAY + "미사용 슬롯",
            List.of(ChatColor.GRAY + "이번 시즌 최대 슬롯: " + maximum + "칸")
        ));
        continue;
      }

      if (visibleIndex >= unlocked) {
        inventory.setItem(slot, makeGuiItem(
            Material.GRAY_STAINED_GLASS_PANE,
            ChatColor.GRAY + "잠금 슬롯 #" + (visibleIndex + 1),
            List.of(ChatColor.DARK_GRAY + "점수를 더 쌓아 해금하세요")
        ));
        visibleIndex++;
        continue;
      }

      ActiveSeasonEffect effect = visibleIndex < activeEffects.size() ? activeEffects.get(visibleIndex) : null;
      if (effect == null) {
        Material emptyType = kind == EffectKind.BLESSING ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        String emptyHint = cardsOneTimeRollEnabled()
            ? "리롤로 새 카드를 다시 추첨할 수 있습니다"
            : "매일 자정 추첨에서 카드가 채워집니다";
        inventory.setItem(slot, makeGuiItem(
            emptyType,
            (kind == EffectKind.BLESSING ? ChatColor.GREEN : ChatColor.RED) + "빈 슬롯 #" + (visibleIndex + 1),
            List.of(ChatColor.GRAY + emptyHint)
        ));
        visibleIndex++;
        continue;
      }

      EffectDefinition definition = effectDefinitionsById.get(effect.getId());
      EffectArchetype archetype = definition == null ? inferArchetype(effect.getId()) : definition.archetype();
      String displayName = (definition != null && definition.displayName() != null && !definition.displayName().isBlank())
          ? definition.displayName()
          : ((effect.getDisplayName() == null || effect.getDisplayName().isBlank()) ? effect.getId() : effect.getDisplayName());
      String remainingLabel = effectRemainingLabel(effect, currentDay);
      int archetypeTotalTier = Math.max(0, archetypeTierTotals == null
          ? 0
          : archetypeTierTotals.getOrDefault(archetype, 0));

      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.DARK_GRAY + effect.getId() + " · " + effectArchetypeLabel(archetype));
      lore.add(ChatColor.GRAY + "티어 T" + Math.max(1, effect.getTier()) + " · 만료 " + remainingLabel);
      lore.addAll(effectDetailLines(definition, kind, archetype, archetypeTotalTier, effect.getTier()));
      if (kind == EffectKind.CURSE && effect.isSevereCurse()) {
        lore.add(ChatColor.DARK_RED + "중대 저주 보너스 +" + severeBonusFor(effect) + " 점수");
      }

      inventory.setItem(slot, makeGuiItem(
          effectIcon(kind, archetype),
          (kind == EffectKind.BLESSING ? ChatColor.GREEN : ChatColor.RED) + displayName,
          lore
      ));
      visibleIndex++;
    }
  }

  private void populateEffectDetailBlocks(
      Inventory inventory,
      int[] blockStarts,
      List<ActiveSeasonEffect> activeEffects,
      int unlockedSlots,
      int maxSlots,
      EffectKind kind,
      EnumMap<EffectArchetype, Integer> archetypeTierTotals,
      long currentDay
  ) {
    if (inventory == null || blockStarts == null || activeEffects == null) {
      return;
    }
    int unlocked = Math.max(0, unlockedSlots);
    int maximum = Math.max(0, maxSlots);

    for (int index = 0; index < blockStarts.length; index++) {
      int startSlot = blockStarts[index];
      if (startSlot < 0 || startSlot + 4 >= inventory.getSize()) {
        continue;
      }

      int slotNo = index + 1;
      if (index >= maximum) {
        setEffectDetailBlockPlaceholders(
            inventory,
            startSlot,
            Material.BLACK_STAINED_GLASS_PANE,
            ChatColor.DARK_GRAY + "미사용 슬롯 #" + slotNo,
            ChatColor.DARK_GRAY + "이번 시즌 최대 슬롯: " + maximum + "칸"
        );
        continue;
      }

      if (index >= unlocked) {
        setEffectDetailBlockPlaceholders(
            inventory,
            startSlot,
            Material.GRAY_STAINED_GLASS_PANE,
            ChatColor.GRAY + "잠금 슬롯 #" + slotNo,
            ChatColor.DARK_GRAY + "점수를 더 쌓아 해금하세요"
        );
        continue;
      }

      ActiveSeasonEffect effect = index < activeEffects.size() ? activeEffects.get(index) : null;
      if (effect == null) {
        String emptyHint = cardsOneTimeRollEnabled()
            ? "리롤로 새 카드를 다시 추첨할 수 있습니다"
            : "매일 자정 추첨에서 카드가 채워집니다";
        Material emptyType = kind == EffectKind.BLESSING ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        ChatColor color = kind == EffectKind.BLESSING ? ChatColor.GREEN : ChatColor.RED;
        setEffectDetailBlockPlaceholders(
            inventory,
            startSlot,
            emptyType,
            color + "빈 슬롯 #" + slotNo,
            ChatColor.GRAY + emptyHint
        );
        continue;
      }

      renderEffectDetailBlock(
          inventory,
          startSlot,
          effect,
          kind,
          archetypeTierTotals,
          currentDay
      );
    }
  }

  private void setEffectDetailBlockPlaceholders(
      Inventory inventory,
      int startSlot,
      Material baseMaterial,
      String title,
      String line
  ) {
    if (inventory == null) {
      return;
    }
    String message = line == null ? "" : line;
    inventory.setItem(startSlot, makeGuiItem(
        baseMaterial,
        title,
        List.of(message)
    ));
    inventory.setItem(startSlot + 1, makeGuiItem(
        baseMaterial,
        ChatColor.DARK_GRAY + "특수 효과",
        List.of(message)
    ));
    inventory.setItem(startSlot + 2, makeGuiItem(
        baseMaterial,
        ChatColor.DARK_GRAY + "T2 해금 능력",
        List.of(message)
    ));
    inventory.setItem(startSlot + 3, makeGuiItem(
        baseMaterial,
        ChatColor.DARK_GRAY + "T4 해금 능력",
        List.of(message)
    ));
    inventory.setItem(startSlot + 4, makeGuiItem(
        baseMaterial,
        ChatColor.DARK_GRAY + "T6 해금 능력",
        List.of(message)
    ));
  }

  private void renderEffectDetailBlock(
      Inventory inventory,
      int startSlot,
      ActiveSeasonEffect effect,
      EffectKind kind,
      EnumMap<EffectArchetype, Integer> archetypeTierTotals,
      long currentDay
  ) {
    if (inventory == null || effect == null) {
      return;
    }

    EffectDefinition definition = effectDefinitionsById.get(effect.getId());
    EffectArchetype archetype = definition == null ? inferArchetype(effect.getId()) : definition.archetype();
    String displayName = (definition != null && definition.displayName() != null && !definition.displayName().isBlank())
        ? definition.displayName()
        : ((effect.getDisplayName() == null || effect.getDisplayName().isBlank()) ? effect.getId() : effect.getDisplayName());
    int ownTier = Math.max(1, effect.getTier());
    int archetypeTotalTier = Math.max(0, archetypeTierTotals == null
        ? 0
        : archetypeTierTotals.getOrDefault(archetype, 0));

    EffectLoreSections sections = effectLoreSections(definition, kind, archetype, archetypeTotalTier, ownTier);
    String remainingLabel = effectRemainingLabel(effect, currentDay);

    ChatColor mainColor = kind == EffectKind.BLESSING ? ChatColor.GREEN : ChatColor.RED;
    ChatColor accentColor = kind == EffectKind.BLESSING ? ChatColor.AQUA : ChatColor.GOLD;
    Material specialMaterial = kind == EffectKind.BLESSING ? Material.LIGHT_BLUE_DYE : Material.GRAY_DYE;

    List<String> titleLore = new ArrayList<>();
    titleLore.add(ChatColor.DARK_GRAY + effect.getId() + " · " + effectArchetypeLabel(archetype));
    titleLore.add(ChatColor.GRAY + "티어 T" + ownTier + " · 만료 " + remainingLabel);
    titleLore.add(accentColor + "스탯: " + truncateLoreDetail(sections.stat(), 72));
    if (kind == EffectKind.CURSE && effect.isSevereCurse()) {
      titleLore.add(ChatColor.DARK_RED + "중대 저주 보너스 +" + severeBonusFor(effect) + " 점수");
    }

    inventory.setItem(startSlot, makeGuiItem(
        effectIcon(kind, archetype),
        mainColor + displayName,
        titleLore
    ));
    inventory.setItem(startSlot + 1, makeGuiItem(
        specialMaterial,
        accentColor + "특수 효과",
        List.of(ChatColor.GRAY + truncateLoreDetail(sections.special(), 74))
    ));
    inventory.setItem(startSlot + 2, makeGuiItem(
        Material.BLAZE_POWDER,
        ChatColor.YELLOW + "T2 해금 능력",
        buildTierAbilityLore(sections.t2(), ownTier, 2)
    ));
    inventory.setItem(startSlot + 3, makeGuiItem(
        Material.GLOWSTONE_DUST,
        ChatColor.GOLD + "T4 해금 능력",
        buildTierAbilityLore(sections.t4(), ownTier, 4)
    ));
    inventory.setItem(startSlot + 4, makeGuiItem(
        Material.NETHER_STAR,
        ChatColor.LIGHT_PURPLE + "T6 해금 능력",
        buildTierAbilityLore(sections.t6(), ownTier, 6)
    ));
  }

  private List<String> buildTierAbilityLore(String detail, int ownTier, int unlockTier) {
    List<String> lore = new ArrayList<>();
    String safeDetail = (detail == null || detail.isBlank()) ? "능력 정보 없음" : detail;
    if (ownTier >= unlockTier) {
      lore.add(ChatColor.GREEN + "해금됨");
    } else {
      lore.add(ChatColor.DARK_GRAY + "잠김 (T" + unlockTier + " 해금)");
    }
    lore.add(ChatColor.GRAY + truncateLoreDetail(safeDetail, 74));
    return lore;
  }

  private EffectLoreSections effectLoreSections(
      EffectDefinition definition,
      EffectKind kind,
      EffectArchetype archetype,
      int totalTier,
      int ownTier
  ) {
    int tier = Math.max(1, ownTier);
    EffectRuntimeProfile runtimeProfile = resolveRuntimeProfile(definition, kind, archetype);
    List<String> runtimeDetails = runtimeProfile == null ? List.of() : runtimeProfile.detailKo();

    String statRaw = findRuntimeDetailLine(runtimeDetails, "스탯:");
    String specialRaw = findRuntimeDetailLine(runtimeDetails, "카드 기믹");
    if (specialRaw.isBlank()) {
      specialRaw = findRuntimeDetailLine(runtimeDetails, "특수 효과");
    }
    String abilityRaw = findRuntimeDetailLine(runtimeDetails, "특수 능력:");

    String stat = normalizeTierDetailLine(stripRuntimeDetailPrefix(statRaw), tier);
    String special = normalizeTierDetailLine(stripRuntimeDetailPrefix(specialRaw), tier);
    String tier2Ability = resolveTierUnlockAbilityDetail(abilityRaw, tier, 2);
    String tier4Ability = resolveTierUnlockAbilityDetail(abilityRaw, tier, 4);
    String tier6Ability = resolveTierUnlockAbilityDetail(abilityRaw, tier, 6);

    if (stat.isBlank() || special.isBlank()) {
      List<String> fallbackLines = effectDetailLines(definition, kind, archetype, totalTier, tier);
      List<String> plain = new ArrayList<>();
      for (String fallback : fallbackLines) {
        String stripped = ChatColor.stripColor(fallback);
        if (stripped == null || stripped.isBlank()) {
          continue;
        }
        stripped = stripped.replaceFirst("^·\\s*", "").trim();
        if (!stripped.isBlank()) {
          plain.add(stripped);
        }
      }
      if (stat.isBlank() && !plain.isEmpty()) {
        stat = plain.get(0);
      }
      if (special.isBlank() && plain.size() >= 2) {
        special = plain.get(1);
      }
    }

    if (stat.isBlank()) {
      stat = "수치 정보 없음";
    }
    if (special.isBlank()) {
      special = "특수 효과 정보 없음";
    }
    if (tier2Ability.isBlank()) {
      tier2Ability = "T2 해금 능력 정보 없음";
    }
    if (tier4Ability.isBlank()) {
      tier4Ability = "T4 전용 능력 없음 (차기 패치 예정)";
    }
    if (tier6Ability.isBlank()) {
      tier6Ability = "T6 해금 능력 정보 없음";
    }
    return new EffectLoreSections(stat, special, tier2Ability, tier4Ability, tier6Ability);
  }

  private String findRuntimeDetailLine(List<String> runtimeDetails, String prefix) {
    if (runtimeDetails == null || runtimeDetails.isEmpty() || prefix == null || prefix.isBlank()) {
      return "";
    }
    String normalizedPrefix = prefix.trim();
    for (String detail : runtimeDetails) {
      if (detail == null || detail.isBlank()) {
        continue;
      }
      String trimmed = detail.trim();
      if (trimmed.startsWith(normalizedPrefix)) {
        return trimmed;
      }
      if ("카드 기믹".equals(normalizedPrefix) && trimmed.startsWith("카드 기믹(상시):")) {
        return trimmed;
      }
    }
    return "";
  }

  private String stripRuntimeDetailPrefix(String raw) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    String trimmed = raw.trim();
    String[] prefixes = {
      "스탯:",
      "카드 기믹(상시):",
      "카드 기믹:",
      "특수 효과:",
      "특수 능력:"
    };
    for (String prefix : prefixes) {
      if (trimmed.startsWith(prefix)) {
        return trimmed.substring(prefix.length()).trim();
      }
    }
    return trimmed;
  }

  private String normalizeTierDetailLine(String raw, int tier) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    String normalized = raw.replace("**", "").trim();
    normalized = collapseSlashSeriesByTier(normalized, tier);
    normalized = normalized.replaceAll("\\s+", " ").trim();
    return normalized;
  }

  private String collapseSlashSeriesByTier(String raw, int tier) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    int targetTier = Math.max(1, tier);
    Matcher matcher = DETAIL_SLASH_SERIES_PATTERN.matcher(raw);
    StringBuffer buffer = new StringBuffer();
    while (matcher.find()) {
      String series = matcher.group();
      String selected = selectSlashSeriesValue(series, targetTier);
      matcher.appendReplacement(buffer, Matcher.quoteReplacement(selected));
    }
    matcher.appendTail(buffer);
    return buffer.toString();
  }

  private String selectSlashSeriesValue(String series, int tier) {
    if (series == null || series.isBlank()) {
      return "";
    }
    String[] tokens = series.split("/");
    if (tokens.length == 0) {
      return series;
    }
    int index = Math.max(0, Math.min(tokens.length - 1, Math.max(1, tier) - 1));
    String selected = tokens[index].trim();
    Matcher selectedMatcher = DETAIL_VALUE_WITH_UNIT_PATTERN.matcher(selected);
    if (!selectedMatcher.matches()) {
      return selected;
    }

    String number = selectedMatcher.group("num");
    String unit = selectedMatcher.group("unit");
    if (number == null || number.isBlank()) {
      return selected;
    }
    if (unit == null || unit.isBlank()) {
      unit = detectSlashSeriesUnit(tokens);
    }
    return number + (unit == null ? "" : unit);
  }

  private String detectSlashSeriesUnit(String[] tokens) {
    if (tokens == null || tokens.length == 0) {
      return "";
    }
    for (int i = tokens.length - 1; i >= 0; i--) {
      String token = tokens[i];
      if (token == null || token.isBlank()) {
        continue;
      }
      Matcher matcher = DETAIL_VALUE_WITH_UNIT_PATTERN.matcher(token.trim());
      if (!matcher.matches()) {
        continue;
      }
      String unit = matcher.group("unit");
      if (unit != null && !unit.isBlank()) {
        return unit;
      }
    }
    return "";
  }

  private String resolveTierUnlockAbilityDetail(String abilityRaw, int ownTier, int targetTier) {
    if (abilityRaw == null || abilityRaw.isBlank()) {
      return "";
    }
    String abilityName = extractAbilityName(abilityRaw);
    int detailTier = Math.max(1, targetTier);
    Map<Integer, String> sections = parseSpecialTierSections(abilityRaw, detailTier);
    String detail = sections.getOrDefault(targetTier, "");
    if (detail.isBlank()) {
      return switch (targetTier) {
        case 2 -> abilityName.isBlank() ? "T2 해금 능력 데이터 없음" : abilityName + " · T2 데이터 없음";
        case 4 -> abilityName.isBlank()
            ? "T4 전용 능력 없음 (차기 패치 예정)"
            : abilityName + " · T4 전용 능력 없음 (차기 패치 예정)";
        case 6 -> abilityName.isBlank() ? "T6 해금 능력 데이터 없음" : abilityName + " · T6 데이터 없음";
        default -> abilityName.isBlank() ? "능력 데이터 없음" : abilityName + " · 데이터 없음";
      };
    }
    String status = ownTier >= targetTier ? "해금" : ("T" + targetTier + " 해금");
    String merged = status + " · " + detail;
    return abilityName.isBlank() ? merged : abilityName + " · " + merged;
  }

  private String extractAbilityName(String abilityRaw) {
    if (abilityRaw == null || abilityRaw.isBlank()) {
      return "";
    }
    String normalized = normalizeTierDetailLine(stripRuntimeDetailPrefix(abilityRaw), 1);
    int splitAt = normalized.indexOf('—');
    if (splitAt < 0) {
      splitAt = normalized.indexOf('-');
    }
    String head = splitAt < 0 ? normalized : normalized.substring(0, splitAt).trim();
    int triggerAt = head.indexOf("(발동:");
    if (triggerAt >= 0) {
      head = head.substring(0, triggerAt).trim();
    }
    return head.replace("**", "").trim();
  }

  private Map<Integer, String> parseSpecialTierSections(String abilityRaw, int tier) {
    Map<Integer, String> sections = new LinkedHashMap<>();
    if (abilityRaw == null || abilityRaw.isBlank()) {
      return sections;
    }
    String normalized = stripRuntimeDetailPrefix(abilityRaw);
    Matcher matcher = SPECIAL_TIER_SECTION_PATTERN.matcher(normalized);
    while (matcher.find()) {
      Integer parsedTier = parseInt(matcher.group("tier"));
      if (parsedTier == null) {
        continue;
      }
      String detail = matcher.group("text");
      if (detail == null || detail.isBlank()) {
        continue;
      }
      detail = normalizeTierDetailLine(detail, tier);
      if (!detail.isBlank()) {
        sections.put(parsedTier, detail);
      }
    }
    return sections;
  }

  private ItemStack seasonSlotRerollButtonItem(Player viewer, UUID targetUuid, PlayerRoundData data) {
    long now = nowEpochSecond();
    boolean initialSetupMode = isInitialSetupRerollMode(viewer, targetUuid, data);
    long cost = initialSetupMode ? 0L : cardsRerollCostScore();
    String blockedReason = seasonSlotRerollBlockedReason(viewer, targetUuid, data, now, initialSetupMode);
    long cooldownLeft = initialSetupMode
        ? 0L
        : Math.max(0L, slotRerollCooldownUntilEpochSecond.getOrDefault(targetUuid, 0L) - now);

    List<String> lore = new ArrayList<>();
    if (initialSetupMode) {
      int used = data == null ? 0 : data.getInitialCardRerollsUsed();
      int max = cardsInitialSetupFreeRerolls();
      int remaining = data == null ? 0 : remainingInitialSetupRerolls(data);
      lore.add(ChatColor.GRAY + "초기 무료 리롤");
      lore.add(ChatColor.GRAY + "사용: " + used + "/" + max + "회, 남은 횟수: " + remaining);
    } else {
      lore.add(ChatColor.GRAY + "비용: " + cost + " 점수");
    }
    lore.add(ChatColor.GRAY + "현재 점수: " + (data == null ? 0L : data.getScore()));
    lore.add(ChatColor.DARK_GRAY + "리롤 전까지 현재 카드 옵션/티어가 유지됩니다");
    lore.add(ChatColor.YELLOW + "주의: 기존 축복/저주 카드 효과가 모두 제거됩니다");
    lore.add(ChatColor.YELLOW + "제거 직후 새 카드로 다시 추첨됩니다");
    if (cooldownLeft > 0L) {
      lore.add(ChatColor.GRAY + "대기시간: " + cooldownLeft + "초");
    }

    if (blockedReason == null) {
      if (initialSetupMode) {
        lore.add(ChatColor.GREEN + "좌클릭: 무료 리롤");
      } else {
        lore.add(ChatColor.GREEN + "좌클릭: 점수 차감 후 즉시 리롤");
      }
      lore.add(ChatColor.DARK_GRAY + "현재 축복/저주 카드를 다시 추첨합니다");
      return makeGuiItem(Material.EMERALD, ChatColor.GREEN + "슬롯 리롤", lore);
    }

    lore.add(ChatColor.RED + blockedReason);
    return makeGuiItem(Material.BARRIER, ChatColor.RED + "슬롯 리롤 불가", lore);
  }

  private ItemStack seasonSlotTierUpgradeButtonItem(Player viewer, UUID targetUuid, PlayerRoundData data) {
    String blockedReason = seasonSlotTierUpgradeBlockedReason(viewer, targetUuid, data);
    long cost = cardsTierUpgradeCostScore();

    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.GRAY + "비용: " + cost + " 점수");
    lore.add(ChatColor.GRAY + "현재 점수: " + (data == null ? 0L : data.getScore()));
    lore.add(ChatColor.YELLOW + "축복/저주 카드를 동시에 +1 티어 강화");
    lore.add(ChatColor.AQUA + "특수 효과: T2 개방 · T3 강화 · T4 신규");
    lore.add(ChatColor.DARK_GRAY + "양쪽 모두 강화 가능한 카드가 있어야 합니다");
    lore.add(ChatColor.DARK_GRAY + "강화한 옵션은 리롤 전까지 유지됩니다");

    if (blockedReason == null) {
      lore.add(ChatColor.GREEN + "좌클릭: 동시 티어 강화");
      return makeGuiItem(Material.ENCHANTED_BOOK, ChatColor.AQUA + "동시 티어 강화", lore);
    }

    lore.add(ChatColor.RED + blockedReason);
    return makeGuiItem(Material.BARRIER, ChatColor.RED + "티어 강화 불가", lore);
  }

  private ItemStack seasonSlotDebugOpButtonItem(Player viewer, UUID targetUuid) {
    OfflinePlayer target = targetUuid == null ? null : Bukkit.getOfflinePlayer(targetUuid);
    boolean targetOp = target != null && target.isOp();
    String targetName = targetUuid == null ? "unknown" : playerDisplayName(targetUuid);

    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.DARK_RED + "DEBUG MODE 전용");
    lore.add(ChatColor.GRAY + "서버 단위 OP 지정/해제");
    lore.add(ChatColor.GRAY + "대상: " + ChatColor.WHITE + targetName);
    lore.add(ChatColor.GRAY + "현재 OP 상태: " + (targetOp ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
    lore.add(ChatColor.YELLOW + "좌클릭: OP 상태 토글");

    Material type = targetOp ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK;
    String title = targetOp
        ? ChatColor.RED + "DEBUG: 서버 OP 해제"
        : ChatColor.GREEN + "DEBUG: 서버 OP 지정";
    return makeGuiItem(type, title, lore);
  }

  private void trySeasonSlotDebugToggleOp(Player viewer, SeasonSlotInventoryHolder holder) {
    if (viewer == null || holder == null) {
      return;
    }
    if (!seasonDebugModeEnabled()) {
      viewer.sendMessage("[Season][DEBUG] debug_mode가 비활성화되어 있습니다.");
      return;
    }

    UUID targetUuid = holder.targetUuid();
    if (targetUuid == null) {
      viewer.sendMessage("[Season][DEBUG] 대상 플레이어 정보를 찾을 수 없습니다.");
      return;
    }

    OfflinePlayer target = Bukkit.getOfflinePlayer(targetUuid);
    boolean nextOp = !target.isOp();
    target.setOp(nextOp);
    viewer.sendMessage(
        "[Season][DEBUG] OP "
            + (nextOp ? "부여" : "해제")
            + ": "
            + playerDisplayName(targetUuid)
            + " -> "
            + (nextOp ? "ON" : "OFF")
    );

    PlayerRoundData data = players.get(targetUuid);
    if (data != null) {
      openSeasonSlotOverview(
          viewer,
          target,
          data,
          holder.page(),
          holder.residualPage(),
          holder.residualFilterMode()
      );
    }
  }

  private List<ActiveSeasonEffect> sortedActiveEffects(Map<String, ActiveSeasonEffect> effects) {
    List<ActiveSeasonEffect> sorted = new ArrayList<>();
    if (effects == null || effects.isEmpty()) {
      return sorted;
    }
    for (ActiveSeasonEffect effect : effects.values()) {
      if (effect != null) {
        sorted.add(effect);
      }
    }
    sorted.sort((left, right) -> {
      int byTier = Integer.compare(right.getTier(), left.getTier());
      if (byTier != 0) {
        return byTier;
      }
      int byExpire = Long.compare(left.getExpireIngameDay(), right.getExpireIngameDay());
      if (byExpire != 0) {
        return byExpire;
      }
      return left.getId().compareTo(right.getId());
    });
    return sorted;
  }

  private EnumMap<EffectArchetype, Integer> aggregateArchetypeTierTotals(Map<String, ActiveSeasonEffect> effects) {
    EnumMap<EffectArchetype, Integer> totals = new EnumMap<>(EffectArchetype.class);
    if (effects == null || effects.isEmpty()) {
      return totals;
    }

    for (ActiveSeasonEffect effect : effects.values()) {
      if (effect == null) {
        continue;
      }
      EffectDefinition definition = effectDefinitionsById.get(effect.getId());
      EffectArchetype archetype = definition == null ? inferArchetype(effect.getId()) : definition.archetype();
      int tier = Math.max(1, effect.getTier());
      totals.merge(archetype, tier, Integer::sum);
    }
    return totals;
  }

  private ItemStack makeGuiItem(Material material, String name, List<String> lore) {
    ItemStack item = new ItemStack(material == null ? Material.STONE : material);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(name == null ? "" : name);
      if (lore != null) {
        meta.setLore(lore);
      }
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
      item.setItemMeta(meta);
    }
    return item;
  }

  private String slotTitleName(String name) {
    if (name == null || name.isBlank()) {
      return "unknown";
    }
    return name.length() <= 20 ? name : name.substring(0, 20);
  }

  private long currentIngameDay() {
    World world = cardsWorld();
    if (world == null) {
      return 0L;
    }
    return Math.max(0L, world.getFullTime() / 24000L);
  }

  private Material effectIcon(EffectKind kind, EffectArchetype archetype) {
    if (kind == EffectKind.CURSE) {
      return switch (archetype) {
        case MOBILITY -> Material.CHAINMAIL_CHESTPLATE;
        case COMBAT -> Material.IRON_AXE;
        case DEFENSE -> Material.BONE;
        case GATHERING -> Material.WOODEN_PICKAXE;
        case SCORE -> Material.PAPER;
        case SURVIVAL -> Material.ROTTEN_FLESH;
        case UTILITY -> Material.INK_SAC;
        case ENDGAME -> Material.WITHER_ROSE;
      };
    }

    return switch (archetype) {
      case MOBILITY -> Material.FEATHER;
      case COMBAT -> Material.IRON_SWORD;
      case DEFENSE -> Material.SHIELD;
      case GATHERING -> Material.IRON_PICKAXE;
      case SCORE -> Material.SUNFLOWER;
      case SURVIVAL -> Material.GOLDEN_APPLE;
      case UTILITY -> Material.SPYGLASS;
      case ENDGAME -> Material.ENDER_EYE;
    };
  }

  private String effectArchetypeLabel(EffectArchetype archetype) {
    return switch (archetype) {
      case MOBILITY -> "기동";
      case COMBAT -> "전투";
      case DEFENSE -> "방어";
      case GATHERING -> "채집";
      case SCORE -> "점수";
      case SURVIVAL -> "생존";
      case UTILITY -> "유틸";
      case ENDGAME -> "엔드";
    };
  }

  private List<String> effectDetailLines(
      EffectDefinition definition,
      EffectKind kind,
      EffectArchetype archetype,
      int totalTier,
      int ownTier
  ) {
    List<String> lines = new ArrayList<>();
    ChatColor mainColor = kind == EffectKind.BLESSING ? ChatColor.AQUA : ChatColor.RED;
    EffectRuntimeProfile runtimeProfile = resolveRuntimeProfile(definition, kind, archetype);
    List<RuntimeModifierRule> rules = runtimeProfile == null ? List.of() : runtimeProfile.modifierRules();
    List<String> runtimeDetails = runtimeProfile == null ? List.of() : runtimeProfile.detailKo();
    int detailLinesAdded = 0;
    List<String> tierDetails = compactTierDetailLines(runtimeDetailTierLines(runtimeDetails, ownTier));
    if (!tierDetails.isEmpty()) {
      for (String detail : tierDetails) {
        lines.add(mainColor + "· " + detail);
        detailLinesAdded++;
      }
    }

    if (detailLinesAdded <= 0) {
      if (!rules.isEmpty()) {
        String effectId = definition == null ? null : definition.id();
        List<String> ruleLines = runtimeModifierSummaryLines(rules, ownTier, totalTier, effectId, kind);
        for (String line : ruleLines) {
          lines.add(mainColor + "· " + line);
        }
      } else {
        lines.add(mainColor + "· 상시 수치 보정 없음 (전용 특수 효과 중심)");
      }
    }

    if (cardsMultiplierEnabled()) {
      double perTier = effectScoreMultiplierPerTier(definition, kind);
      double contribution = perTier * Math.max(0, ownTier);
      if (Math.abs(contribution) > 0.0001D) {
        lines.add(ChatColor.DARK_GRAY + "부가 점수 배율: " + formatSignedPercent(contribution) + "%");
      }
    }

    return lines;
  }

  private List<String> compactTierDetailLines(List<String> tierDetails) {
    if (tierDetails == null || tierDetails.isEmpty()) {
      return List.of();
    }
    List<String> regular = new ArrayList<>();
    List<String> special = new ArrayList<>();
    for (String detail : tierDetails) {
      if (detail == null || detail.isBlank()) {
        continue;
      }
      String normalized = detail.replaceAll("\\s+", " ").trim();
      if (normalized.startsWith("특수 효과")) {
        special.add(normalized);
      } else {
        regular.add(normalized);
      }
    }

    List<String> compact = new ArrayList<>();
    int regularLimit = special.isEmpty() ? 3 : 2;
    for (String line : regular) {
      if (compact.size() >= regularLimit) {
        break;
      }
      compact.add(truncateLoreDetail(line, 72));
    }
    if (!special.isEmpty()) {
      compact.add(truncateLoreDetail(special.get(0), 72));
    }
    return compact.isEmpty() ? List.of() : compact;
  }

  private String truncateLoreDetail(String raw, int maxLength) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    int limit = Math.max(24, maxLength);
    String normalized = raw.replaceAll("\\s+", " ").trim();
    if (normalized.length() <= limit) {
      return normalized;
    }
    return normalized.substring(0, limit - 3) + "...";
  }

  private String deriveTriggerSummary(List<RuntimeModifierRule> rules, EffectGimmickProfile gimmickProfile) {
    LinkedHashSet<String> parts = new LinkedHashSet<>();
    if (rules != null && !rules.isEmpty()) {
      parts.add("상시");
    }
    if (gimmickProfile != null && gimmickProfile.hasActiveTrigger()) {
      parts.add("능동 입력");
    }

    LinkedHashSet<String> worlds = new LinkedHashSet<>();
    LinkedHashSet<String> conditions = new LinkedHashSet<>();
    boolean hasTierGate = false;
    if (rules != null) {
      for (RuntimeModifierRule rule : rules) {
        if (rule == null) {
          continue;
        }
        if (rule.worldScope() != null && rule.worldScope() != RuntimeWorldScope.ANY) {
          String label = runtimeWorldScopeLabel(rule.worldScope());
          if (label != null && !label.isBlank()) {
            worlds.add(label);
          }
        }
        String condition = runtimeConditionLabel(rule.conditions());
        if (condition != null && !condition.isBlank()) {
          conditions.add(condition);
        }
        if (rule.minTotalTier() > 1) {
          hasTierGate = true;
        }
      }
    }

    if (!worlds.isEmpty()) {
      parts.add("세계: " + String.join(", ", worlds));
    }
    if (!conditions.isEmpty()) {
      parts.add("조건: " + String.join(", ", conditions));
    }
    if (hasTierGate) {
      parts.add("일부 효과는 티어 조건 충족 시 발동");
    }

    if (parts.isEmpty()) {
      return "상시";
    }
    return String.join(" / ", parts);
  }

  private List<String> runtimeDetailTierLines(List<String> runtimeDetails, int ownTier) {
    if (runtimeDetails == null || runtimeDetails.isEmpty()) {
      return List.of();
    }
    int tier = clampTier(ownTier);
    LinkedHashSet<String> normalized = new LinkedHashSet<>();
    for (String line : runtimeDetails) {
      if (line == null || line.isBlank()) {
        continue;
      }
      String trimmed = line.trim();
      if (isRuntimeTriggerLine(trimmed)) {
        continue;
      }
      if (trimmed.startsWith("-")) {
        trimmed = trimmed.substring(1).trim();
      }
      trimmed = trimmed.replace("카드 기믹", "특수 효과");
      if (trimmed.startsWith("기믹:")) {
        trimmed = "특수 효과:" + trimmed.substring("기믹:".length()).trim();
      }
      if (trimmed.startsWith("카드 기믹:")) {
        trimmed = "특수 효과:" + trimmed.substring("카드 기믹:".length()).trim();
      }
      Matcher tierGateMatcher = DETAIL_TIER_GATE_PATTERN.matcher(trimmed);
      boolean gatedOut = false;
      while (tierGateMatcher.find()) {
        Integer parsedRequiredTier = parseInt(tierGateMatcher.group(1));
        int requiredTier = parsedRequiredTier == null ? 1 : parsedRequiredTier;
        if (tier < requiredTier) {
          gatedOut = true;
          break;
        }
      }
      if (gatedOut) {
        continue;
      }
      trimmed = trimmed.replaceAll("T([1-4])\\+\\s*", "");
      trimmed = trimmed.replaceAll("\\(\\s*발동\\s*시작\\s*T[1-4]\\s*\\)", "");
      trimmed = trimmed.replace("T1~T4 공통", "공통");
      trimmed = trimmed.replace("T1~T4", "공통");
      // Normalize " / T2 ..." style separators so tier token runs are parsed consistently.
      trimmed = trimmed.replaceAll("\\s*/\\s*(?=T[1-4]\\s)", ", ");
      trimmed = collapseSlashQuadTokens(trimmed, tier);
      trimmed = collapseTierTokenRuns(trimmed, tier);
      trimmed = collapseSingleTierPrefix(trimmed, tier);
      trimmed = normalizeCollapsedTierDetailLine(trimmed);
      if (trimmed == null || trimmed.isBlank()) {
        continue;
      }
      trimmed = trimmed.replaceAll("\\s+", " ").trim();
      trimmed = trimmed.replaceAll("\\(\\s*\\)", "");
      if (trimmed.endsWith(":")) {
        trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
      }
      if (trimmed.isBlank() || isRuntimeTierLineInactive(trimmed)) {
        continue;
      }
      normalized.add(trimmed);
    }
    return normalized.isEmpty() ? List.of() : new ArrayList<>(normalized);
  }

  private String collapseTierTokenRuns(String raw, int tier) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    String collapsed = raw;
    for (int i = 0; i < 16; i++) {
      Matcher runMatcher = DETAIL_TIER_TOKEN_RUN_PATTERN.matcher(collapsed);
      if (!runMatcher.find()) {
        break;
      }
      String run = runMatcher.group();
      String selected = selectTierTokenValue(run, tier);
      if (selected == null || selected.isBlank()) {
        return null;
      }
      collapsed = collapsed.substring(0, runMatcher.start())
          + selected.trim()
          + collapsed.substring(runMatcher.end());
    }
    return collapsed;
  }

  private String selectTierTokenValue(String run, int tier) {
    if (run == null || run.isBlank()) {
      return "";
    }
    Map<Integer, String> values = new LinkedHashMap<>();
    Matcher tokenMatcher = DETAIL_TIER_TOKEN_PATTERN.matcher(run);
    while (tokenMatcher.find()) {
      Integer parsedTier = parseInt(tokenMatcher.group(1));
      if (parsedTier == null || parsedTier < 1 || parsedTier > 4) {
        continue;
      }
      String value = tokenMatcher.group(2);
      if (value == null || value.isBlank()) {
        continue;
      }
      values.put(parsedTier, value.trim());
    }
    if (values.isEmpty()) {
      return run;
    }

    int selectedTier = -1;
    for (Integer key : values.keySet()) {
      if (key != null && key <= tier) {
        selectedTier = Math.max(selectedTier, key);
      }
    }
    if (selectedTier < 0) {
      return null;
    }
    String selected = values.get(selectedTier);
    return selected == null ? null : selected;
  }

  private String collapseSingleTierPrefix(String raw, int tier) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    Matcher matcher = DETAIL_SINGLE_TIER_PREFIX_PATTERN.matcher(raw);
    if (!matcher.find()) {
      return raw;
    }
    String boundary = matcher.group(1);
    Integer requiredTier = parseInt(matcher.group(2));
    if (requiredTier != null && tier < requiredTier) {
      return "";
    }
    String replacementBoundary = boundary == null ? "" : boundary;
    return matcher.replaceFirst(Matcher.quoteReplacement(replacementBoundary));
  }

  private boolean isRuntimeTriggerLine(String line) {
    if (line == null || line.isBlank()) {
      return false;
    }
    String trimmed = line.trim();
    return trimmed.regionMatches(true, 0, "Trigger:", 0, "Trigger:".length())
        || trimmed.startsWith("능동 트리거:")
        || trimmed.startsWith("발동 조건:");
  }

  private String collapseSlashQuadTokens(String raw, int tier) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    String collapsed = raw;
    for (int i = 0; i < 12; i++) {
      Matcher matcher = DETAIL_SLASH_QUAD_PATTERN.matcher(collapsed);
      if (!matcher.find()) {
        break;
      }
      String selected = switch (tier) {
        case 1 -> matcher.group(1);
        case 2 -> matcher.group(2);
        case 3 -> matcher.group(3);
        default -> matcher.group(4);
      };
      if (selected == null) {
        selected = "";
      }
      collapsed = matcher.replaceFirst(Matcher.quoteReplacement(selected.trim()));
    }
    return collapsed;
  }

  private String normalizeCollapsedTierDetailLine(String raw) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    String normalized = raw;
    // Prevent malformed output such as "20%60%" when tier token collapse leaves adjacent value fragments.
    normalized = normalized.replaceAll("(?<=%)(?=[+-]?\\d)", " ");
    return normalized.trim();
  }

  private boolean isRuntimeTierLineInactive(String line) {
    if (line == null || line.isBlank()) {
      return true;
    }
    String normalized = line.replace(" ", "");
    return normalized.contains("없음") || normalized.contains("비활성");
  }

  private List<String> runtimeModifierSummaryLines(
      List<RuntimeModifierRule> rules,
      int ownTier,
      int totalTier,
      String effectId,
      EffectKind kind
  ) {
    if (rules == null || rules.isEmpty()) {
      return List.of();
    }

    Map<String, RuntimeModifierSummaryBucket> buckets = new LinkedHashMap<>();
    for (RuntimeModifierRule rule : rules) {
      if (rule == null) {
        continue;
      }
      int sourceTier = rule.scalingMode() == TierScalingMode.CARD_TIER
          ? Math.max(0, ownTier)
          : Math.max(0, totalTier);
      String key = rule.type().name()
          + "|" + rule.scalingMode().name()
          + "|" + rule.worldScope().name()
          + "|" + runtimeConditionLabel(rule.conditions());
      RuntimeModifierSummaryBucket bucket = buckets.computeIfAbsent(
          key,
          ignored -> new RuntimeModifierSummaryBucket(rule, sourceTier)
      );
      bucket.accept(rule, sourceTier);
    }

    List<String> lines = new ArrayList<>();
    for (RuntimeModifierSummaryBucket bucket : buckets.values()) {
      Double value = bucket.hasActive() ? bucket.valueSum() : null;
      if (value != null) {
        value = applyDisplayRuntimeRuleScaling(effectId, kind, bucket.type(), value);
      }
      lines.add(
          modifierRuleSummaryLine(
              bucket.labelKo(),
              bucket.scalingMode(),
              bucket.sourceTier(),
              bucket.minRequiredTier(),
              bucket.worldScope(),
              bucket.conditions(),
              value
          )
      );
    }
    return lines;
  }

  private String modifierRuleSummaryLine(
      String label,
      TierScalingMode scalingMode,
      int sourceTier,
      int minRequiredTier,
      RuntimeWorldScope worldScope,
      Set<RuntimeCondition> conditions,
      Double value
  ) {
    String safeLabel = (label == null || label.isBlank()) ? "효과" : label;
    String scaling = scalingMode == TierScalingMode.CARD_TIER ? "카드티어" : "계열누적";
    String scope = runtimeWorldScopeLabel(worldScope);
    String condition = runtimeConditionLabel(conditions);
    String scopeSuffix = scope.isBlank() ? "" : " [" + scope + "]";
    String conditionSuffix = condition.isBlank() ? "" : " {" + condition + "}";

    if (value == null) {
      return safeLabel
          + " 미발동 (필요 "
          + scaling
          + " "
          + minRequiredTier
          + ", 현재 "
          + sourceTier
          + ")"
          + scopeSuffix
          + conditionSuffix;
    }

    String direction = value > 0.0D ? "증가" : (value < 0.0D ? "감소" : "변화 없음");
    return safeLabel
        + " "
        + formatSignedPercent(value)
        + "% ("
        + direction
        + ", 기준 "
        + scaling
        + " "
        + sourceTier
        + ")"
        + scopeSuffix
        + conditionSuffix;
  }

  private String extractRuntimeTriggerLine(List<String> runtimeDetails) {
    if (runtimeDetails == null || runtimeDetails.isEmpty()) {
      return null;
    }
    for (String line : runtimeDetails) {
      if (line == null || line.isBlank()) {
        continue;
      }
      String trimmed = line.trim();
      if (trimmed.regionMatches(true, 0, "Trigger:", 0, "Trigger:".length())) {
        return trimmed;
      }
      if (trimmed.startsWith("능동 트리거:")) {
        return "Trigger: " + trimmed.substring("능동 트리거:".length()).trim();
      }
      if (trimmed.startsWith("발동 조건:")) {
        return "Trigger: " + trimmed.substring("발동 조건:".length()).trim();
      }
    }
    return null;
  }

  private String humanReadableTriggerLine(String rawTriggerLine) {
    if (rawTriggerLine == null || rawTriggerLine.isBlank()) {
      return "";
    }
    String text = rawTriggerLine.trim();
    if (text.regionMatches(true, 0, "Trigger:", 0, "Trigger:".length())) {
      text = text.substring("Trigger:".length()).trim();
    }
    if (text.isBlank()) {
      return "";
    }

    text = text.replace("PASSIVE", "상시");
    text = text.replace("PERIODIC(", "주기(");
    text = text.replace("ACTIVE(", "우클릭 능동(");
    text = text.replace("EVENT(", "행동/상황(");
    text = text.replace("(+선택 ACTIVE)", "(+선택 우클릭 능동)");

    text = text.replace("TOKEN_MOBILITY", "기동 토큰(깃털/엔더진주)");
    text = text.replace("TOKEN_DEFENSE", "방어 토큰(방패/불사의 토템)");
    text = text.replace("TOKEN_OFFENSE", "공격 토큰(블레이즈 막대/철·네더라이트 검)");
    text = text.replace("TOKEN_UTILITY", "유틸 토큰(나침반/시계/책)");
    text = text.replace("TOKEN_BUILD", "건축 토큰(벽돌/조약돌/돌)");

    text = text.replace("SNEAK", "웅크리기(Shift)");
    text = text.replace("SPRINT", "질주");
    text = text.replace("NORMAL", "기본 자세");

    text = text.replace("ON_MELEE_HIT", "근접 공격 적중");
    text = text.replace("ON_BLOCK_BREAK", "블록 파괴");
    text = text.replace("ON_BLOCK_PLACE", "블록 설치");
    text = text.replace("ON_DAMAGE_TAKEN", "피격");
    text = text.replace("ON_FATAL_DAMAGE", "치명 피해 직전");
    text = text.replace("ON_ITEM_PICKUP", "아이템 획득");
    text = text.replace("ON_ITEM_CONSUME", "아이템 섭취");
    text = text.replace("ON_POTION_APPLY", "포션 효과 적용");
    text = text.replace("ON_CRAFT", "제작 완료");
    text = text.replace("ON_ITEM_HELD_CHANGE", "핫바 슬롯 변경");
    text = text.replace("ON_LOOT_GENERATE", "전리품 생성");
    text = text.replace("ON_TELEPORT", "텔레포트");
    text = text.replace("ON_KILL", "처치");
    text = text.replace("ON_BORDER_OUTSIDE_TICK", "경계 밖 체류");
    text = text.replace("ON_TIME_CHECK", "시간 변화");
    text = text.replace("ON_WEATHER_CHANGE", "날씨 변화");
    text = text.replace("ON_DIMENSION_CHANGE", "차원 이동");
    text = text.replace("Projectile near", "주변 투사체 접근");
    text = text.replace("Explosion damage", "폭발 피해");
    text = text.replace("food change", "허기 변화");
    text = text.replace("Enter/Exit", "진입/이탈");
    text = text.replace("내부쿨", "내부 쿨다운");

    return text.replaceAll("\\s{2,}", " ").trim();
  }

  private String activeInputGuideLine(EffectGimmickProfile profile) {
    if (profile == null || !profile.hasActiveTrigger()) {
      return "";
    }
    StringBuilder builder = new StringBuilder("능동 입력: 주손 우클릭");
    String tokenGuide = abilityTokenGuideLabel(profile.token());
    if (!tokenGuide.isBlank()) {
      builder.append(" + ").append(tokenGuide);
    }
    String modeGuide = abilityModeGuideLabel(profile.mode());
    if (!modeGuide.isBlank()) {
      builder.append(" + ").append(modeGuide);
    }
    builder.append(" (기본 쿨다운 ").append(Math.max(1, profile.baseCooldownSeconds())).append("초, 카드 티어 상승 시 단축)");
    return builder.toString();
  }

  private String abilityTokenGuideLabel(AbilityToken token) {
    if (token == null) {
      return "";
    }
    return switch (token) {
      case MOBILITY -> "기동 토큰(깃털/엔더진주)";
      case DEFENSE -> "방어 토큰(방패/불사의 토템)";
      case OFFENSE -> "공격 토큰(블레이즈 막대/철·네더라이트 검)";
      case UTILITY -> "유틸 토큰(나침반/시계/책)";
      case BUILD -> "건축 토큰(벽돌/조약돌/돌)";
      case NONE -> "";
    };
  }

  private String abilityModeGuideLabel(AbilityMode mode) {
    if (mode == null) {
      return "";
    }
    return switch (mode) {
      case SNEAK -> "웅크리기(Shift)";
      case SPRINT -> "질주";
      case NORMAL -> "기본 자세";
    };
  }

  private double applyDisplayRuntimeRuleScaling(
      String effectId,
      EffectKind kind,
      RuntimeModifierType type,
      double value
  ) {
    double adjusted = value;
    if (kind == EffectKind.BLESSING && isBModEffectId(effectId)) {
      adjusted *= B_MOD_RUNTIME_RATIO_SCALE;
    }
    return applyCatalogBalanceScaling(effectId, kind, type, adjusted);
  }

  private String humanReadableRuntimeDetailLine(EffectDefinition definition, String line) {
    if (line == null || line.isBlank() || definition == null || definition.id() == null) {
      return line == null ? "" : line;
    }
    String id = normalizeEffectId(definition.id());
    if (!"X-007-B".equals(id) && !"X-007-C".equals(id)) {
      return line;
    }

    Matcher detailed = DETAIL_GAMBLE_DOUBLE_PATTERN.matcher(line);
    if (detailed.matches()) {
      String tier = detailed.group("tier");
      String success = detailed.group("success");
      String fail = detailed.group("fail");
      return "T" + tier + ": 성공 " + success + "% (결과 +100%), 실패 " + fail + "% (재료 +100% 소모)";
    }

    Matcher compact = DETAIL_GAMBLE_RATIO_PATTERN.matcher(line);
    if (compact.matches()) {
      String tier = compact.group("tier");
      String success = compact.group("success");
      String fail = compact.group("fail");
      return "T" + tier + ": 성공 " + success + "% / 실패 " + fail + "%";
    }
    return line;
  }

  private String detailLineForCurrentTier(
      String detail,
      int ownTier,
      String effectId,
      EffectKind kind,
      RuntimeModifierRule pairedRule
  ) {
    if (detail == null || detail.isBlank()) {
      return "";
    }
    String trimmed = detail.trim();
    Matcher matcher = DETAIL_ABSOLUTE_TIER_PATTERN.matcher(trimmed);
    if (!matcher.matches()) {
      return trimmed;
    }

    int tier = Math.max(1, Math.min(4, ownTier));
    Integer parsedMinTier = parseInt(matcher.group("min"));
    int minTier = parsedMinTier == null ? 1 : parsedMinTier;
    String label = matcher.group("label").trim();
    String value = switch (tier) {
      case 1 -> matcher.group("t1");
      case 2 -> matcher.group("t2");
      case 3 -> matcher.group("t3");
      default -> matcher.group("t4");
    };

    if (tier < minTier) {
      return label + ": 현재 T" + tier + " 미발동 (발동 T" + minTier + "부터)";
    }
    String adjustedValue = value;
    Double parsedPercent = parseDouble(value);
    if (parsedPercent != null && pairedRule != null && pairedRule.type() != null) {
      double adjustedRatio = applyCatalogBalanceScaling(
          effectId,
          kind,
          pairedRule.type(),
          parsedPercent / 100.0D
      );
      adjustedValue = String.format(Locale.ROOT, "%+.1f", adjustedRatio * 100.0D);
    }
    return label + ": 현재 T" + tier + " " + adjustedValue + "%";
  }

  private String modifierRuleDetailLine(RuntimeModifierRule rule, int sourceTier, Double value) {
    if (rule == null) {
      return "실제 적용: 설정 없음";
    }
    String scope = runtimeWorldScopeLabel(rule.worldScope());
    String condition = runtimeConditionLabel(rule.conditions());
    String scaling = rule.scalingMode() == TierScalingMode.CARD_TIER ? "카드티어" : "계열누적";
    String scopeSuffix = scope.isBlank() ? "" : " [" + scope + "]";
    String conditionSuffix = condition.isBlank() ? "" : " {" + condition + "}";
    if (value == null) {
      return rule.labelKo()
          + " 미발동 (조건 미충족: 필요 "
          + scaling
          + " "
          + rule.minTotalTier()
          + ", 현재 "
          + sourceTier
          + ")"
          + scopeSuffix
          + conditionSuffix;
    }
    String direction = value > 0.0D ? "증가" : (value < 0.0D ? "감소" : "변화 없음");
    return rule.labelKo()
        + " "
        + formatSignedPercent(value)
        + "% (" + direction + ", 기준 "
        + scaling
        + " "
        + sourceTier
        + ", 티어당 "
        + formatSignedPercent(rule.valuePerTier())
        + "%"
        + ")"
        + scopeSuffix
        + conditionSuffix;
  }

  private String runtimeWorldScopeLabel(RuntimeWorldScope scope) {
    if (scope == null) {
      return "";
    }
    return switch (scope) {
      case ANY -> "";
      case OVERWORLD -> "오버월드";
      case NETHER -> "네더";
      case END -> "엔드";
    };
  }

  private String runtimeConditionLabel(Set<RuntimeCondition> conditions) {
    if (conditions == null || conditions.isEmpty()) {
      return "";
    }
    List<String> labels = new ArrayList<>();
    for (RuntimeCondition condition : conditions) {
      String label = runtimeConditionLabel(condition);
      if (label != null && !label.isBlank()) {
        labels.add(label);
      }
    }
    return String.join(", ", labels);
  }

  private String runtimeConditionLabel(RuntimeCondition condition) {
    if (condition == null) {
      return "";
    }
    return switch (condition) {
      case SNEAKING -> "잠행 중";
      case SPRINTING -> "달리기 중";
      case BLOCKING -> "방패 방어 중";
      case ON_ICE -> "얼음 위";
      case IN_COLD_BIOME -> "설원 바이옴";
      case IN_HOT_BIOME -> "고온 바이옴";
      case IN_CAVE_BIOME -> "동굴 바이옴";
      case OUTSIDE_BORDER -> "보더 밖";
      case INSIDE_BORDER -> "보더 안";
      case LOW_HEALTH_30 -> "체력 30% 이하";
      case LOW_HEALTH_35 -> "체력 35% 이하";
      case HAS_POSITIVE_EFFECT -> "긍정 버프 적용 중";
      case MAIN_HAND_EMPTY -> "맨손";
      case MAIN_HAND_AXE -> "도끼 장착";
      case MAIN_HAND_TRIDENT_LIKE -> "삼지창/창 장착";
      case MAIN_HAND_ENDER_PEARL -> "엔더 진주 장착";
      case NEAR_STRONGHOLD_500 -> "스트롱홀드 500b 이내";
    };
  }

  private String formatPercent(double ratio) {
    double value = Math.max(0.0D, ratio) * 100.0D;
    return String.format(Locale.ROOT, "%.1f", value);
  }

  private String formatSignedPercent(double ratio) {
    return String.format(Locale.ROOT, "%+.1f", ratio * 100.0D);
  }

  private boolean onSeasonEffectCommand(CommandSender sender, String[] args) {
    if (effectDefinitionsById.isEmpty()) {
      reloadEffectCatalog();
    }
    if (!cardsEffectLogicEnabled()) {
      sender.sendMessage("[Season] 카드 효과 로직이 비활성화되어 effect give를 사용할 수 없습니다.");
      return true;
    }
    if (args.length < 4 || !"give".equalsIgnoreCase(args[1])) {
      sender.sendMessage("[Season] /season effect give <player> <B-xxx|C-xxx> [tier]");
      return true;
    }

    OfflinePlayer target = resolvePlayer(args[2]);
    if (target == null) {
      sender.sendMessage("[Season] Unknown player: " + args[2]);
      return true;
    }

    String requestedId = normalizeEffectId(args[3]);
    if (requestedId.isBlank()) {
      sender.sendMessage("[Season] Invalid effect id.");
      return true;
    }
    EffectDefinition definition = effectDefinitionsById.get(requestedId);
    if (definition == null) {
      EffectKind inferredKind = inferEffectKind(requestedId, null);
      definition = new EffectDefinition(
          requestedId,
          requestedId,
          inferredKind,
          1.0D,
          inferArchetype(requestedId),
          inferredKind == EffectKind.CURSE,
          inferredKind == EffectKind.CURSE ? cardsSevereCurseDefaultBonusPoints() : 0L,
          null
      );
    }

    int tier = 1;
    if (args.length >= 5) {
      Integer parsed = parseInt(args[4]);
      if (parsed == null) {
        sender.sendMessage("[Season] Invalid tier: " + args[4]);
        return true;
      }
      tier = Math.max(1, Math.min(cardsMaxTier(), parsed));
    }

    UUID uuid = target.getUniqueId();
    PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(uuid);

    World world = cardsWorld();
    long nowDay = world == null ? 0L : Math.max(0L, world.getFullTime() / 24000L);
    ActiveSeasonEffect applied = new ActiveSeasonEffect(
        definition.id(),
        definition.displayName(),
        tier,
        nowDay,
        resolveEffectExpireIngameDay(nowDay),
        definition.severeCurse(),
        definition.severeBonusPoints()
    );

    if (definition.kind() == EffectKind.BLESSING) {
      data.putBlessingEffect(applied);
    } else {
      data.putCurseEffect(applied);
    }

    syncPlayerSlots(uuid, data, false);
    saveState();

    sender.sendMessage(
        "[Season] effect applied: player=" + playerDisplayName(uuid)
            + " id=" + applied.getId()
            + " tier=" + applied.getTier()
            + " expire_day=" + applied.getExpireIngameDay()
    );
    Player online = Bukkit.getPlayer(uuid);
    if (online != null && online.isOnline()) {
      online.sendMessage(
          "[Season] Effect granted: " + applied.getDisplayName()
              + " (tier " + applied.getTier() + ", expires day " + applied.getExpireIngameDay() + ")"
      );
    }
    return true;
  }

  private boolean onSeasonProfileCommand(CommandSender sender, String[] args) {
    if (args.length <= 1 || "show".equalsIgnoreCase(args[1])) {
      sender.sendMessage("[Season] season.id=" + seasonId());
      sender.sendMessage("[Season] season.active_profile=" + activeSeasonProfile());
      sender.sendMessage("[Season] server.role=" + serverRole.name());
      return true;
    }

    String action = args[1].toLowerCase(Locale.ROOT);
    if ("reload".equals(action)) {
      reloadConfig();
      if (isSeasonGameplayServer()) {
        applySeasonProfileOverrides();
        reloadEffectCatalog();
        syncAllParticipantSlots(false);
        stripCardEffectDataIfDisabled();
        saveState();
      }
      sender.sendMessage("[Season] Profile reloaded. active_profile=" + activeSeasonProfile());
      return true;
    }

    if ("set".equals(action)) {
      if (args.length < 3) {
        sender.sendMessage("[Season] /season profile set <name>");
        return true;
      }
      String profileName = args[2].trim();
      if (profileName.isEmpty() || !profileName.matches("[A-Za-z0-9_-]+")) {
        sender.sendMessage("[Season] Invalid profile name. allowed=[A-Za-z0-9_-]");
        return true;
      }

      getConfig().set("season.active_profile", profileName);
      saveConfig();
      reloadConfig();
      if (isSeasonGameplayServer()) {
        applySeasonProfileOverrides();
        reloadEffectCatalog();
        syncAllParticipantSlots(false);
        stripCardEffectDataIfDisabled();
        saveState();
      }
      sender.sendMessage("[Season] Active profile set to " + activeSeasonProfile());
      return true;
    }

    sender.sendMessage("[Season] /season profile [show|reload|set <name>]");
    return true;
  }

  private boolean onLifeCommand(CommandSender sender, String[] args) {
    if (!isSeasonGameplayServer()) {
      sender.sendMessage("[Season] This command is only available on " + vaultDepositServer() + ".");
      return true;
    }

    if (args.length < 3) {
      sender.sendMessage("[Season] /life set <player> <amount>");
      sender.sendMessage("[Season] /life out <player> [true|false]");
      return true;
    }

    String sub = args[0].toLowerCase(Locale.ROOT);
    OfflinePlayer target = resolvePlayer(args[1]);
    if (target == null) {
      sender.sendMessage("[Season] Unknown player: " + args[1]);
      return true;
    }

    UUID uuid = target.getUniqueId();
    PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(uuid);

    if ("set".equals(sub)) {
      Integer value = parseInt(args[2]);
      if (value == null || value < 0) {
        sender.sendMessage("[Season] Invalid life amount: " + args[2]);
        return true;
      }

      data.setLivesRemaining(value);
      saveState();
      sender.sendMessage("[Season] life(" + target.getName() + ")=" + value);
      return true;
    }

    if ("out".equals(sub)) {
      boolean out = parseBoolean(args[2], true);
      data.setOut(out);

      if (out && outSetsScoreZero()) {
        setPlayerScore(uuid, data, 0L, false);
      }

      Player online = target.getPlayer();
      if (online != null && enforceOutSpectator() && out && !(state == SeasonState.FREE_PLAY && allowOutInFreePlay())) {
        enforceSpectator(online);
      }
      if (online != null && !out && online.getGameMode() == GameMode.SPECTATOR) {
        online.setGameMode(GameMode.SURVIVAL);
      }

      updateResetPendingState();
      saveState();
      sender.sendMessage("[Season] out(" + target.getName() + ")=" + out);
      return true;
    }

    sender.sendMessage("[Season] Unknown life subcommand: " + sub);
    return true;
  }

  private boolean onScoreCommand(CommandSender sender, String[] args) {
    if (!isSeasonGameplayServer()) {
      sender.sendMessage("[Season] This command is only available on " + vaultDepositServer() + ".");
      return true;
    }

    if (args.length < 1) {
      sender.sendMessage("[Season] /score add <player> <amount>");
      sender.sendMessage("[Season] /score set <player> <amount>");
      sender.sendMessage("[Season] /score top [count]");
      sender.sendMessage("[Season] /score history <round_id|latest> [count]");
      return true;
    }

    String sub = args[0].toLowerCase(Locale.ROOT);
    if ("top".equals(sub) || "leaderboard".equals(sub)) {
      int count = resolveScoreTopCount(args.length >= 2 ? args[1] : null);
      showCurrentScoreTop(sender, count);
      return true;
    }
    if ("history".equals(sub)) {
      showPersistedLeaderboard(sender, args);
      return true;
    }

    if (args.length < 3) {
      sender.sendMessage("[Season] /score add <player> <amount>");
      sender.sendMessage("[Season] /score set <player> <amount>");
      sender.sendMessage("[Season] /score top [count]");
      sender.sendMessage("[Season] /score history <round_id|latest> [count]");
      return true;
    }

    OfflinePlayer target = resolvePlayer(args[1]);
    if (target == null) {
      sender.sendMessage("[Season] Unknown player: " + args[1]);
      return true;
    }

    Long amount = parseLong(args[2]);
    if (amount == null) {
      sender.sendMessage("[Season] Invalid score amount: " + args[2]);
      return true;
    }

    UUID uuid = target.getUniqueId();
    PlayerRoundData data = players.computeIfAbsent(uuid, ignored -> new PlayerRoundData(baseLives()));
    participants.add(uuid);

    if ("add".equals(sub)) {
      adjustPlayerScore(uuid, data, amount, true);
      saveState();
      sender.sendMessage("[Season] score(" + target.getName() + ")=" + data.getScore());
      return true;
    }

    if ("set".equals(sub)) {
      setPlayerScore(uuid, data, amount, true);
      saveState();
      sender.sendMessage("[Season] score(" + target.getName() + ")=" + data.getScore());
      return true;
    }

    sender.sendMessage("[Season] Unknown score subcommand: " + sub);
    return true;
  }

  private int resolveScoreTopCount(String raw) {
    if (raw == null || raw.isBlank()) {
      return 10;
    }
    Long parsed = parseLong(raw);
    if (parsed == null) {
      return 10;
    }
    return Math.max(1, Math.min(50, parsed.intValue()));
  }

  private void showCurrentScoreTop(CommandSender sender, int limit) {
    int top = Math.max(1, Math.min(50, limit));
    List<Map.Entry<UUID, PlayerRoundData>> ranking = new ArrayList<>();
    for (Map.Entry<UUID, PlayerRoundData> entry : players.entrySet()) {
      if (entry == null || entry.getKey() == null || entry.getValue() == null) {
        continue;
      }
      ranking.add(entry);
    }
    ranking.sort((left, right) -> {
      int cmp = Long.compare(right.getValue().getScore(), left.getValue().getScore());
      if (cmp != 0) {
        return cmp;
      }
      cmp = Long.compare(right.getValue().getPeakScore(), left.getValue().getPeakScore());
      if (cmp != 0) {
        return cmp;
      }
      return left.getKey().toString().compareTo(right.getKey().toString());
    });

    if (ranking.isEmpty()) {
      sender.sendMessage("[Season] score leaderboard is empty.");
      return;
    }

    int show = Math.min(top, ranking.size());
    sender.sendMessage("[Season] score top " + show + " (round=" + roundId + ")");
    for (int i = 0; i < show; i++) {
      Map.Entry<UUID, PlayerRoundData> entry = ranking.get(i);
      PlayerRoundData data = entry.getValue();
      sender.sendMessage(
          "[Season] #"
              + (i + 1)
              + " "
              + playerDisplayName(entry.getKey())
              + " score="
              + formatScoreAmount(data.getScore())
              + " peak="
              + formatScoreAmount(data.getPeakScore())
              + (data.isOut() ? " OUT" : "")
      );
    }
  }

  private void showPersistedLeaderboard(CommandSender sender, String[] args) {
    if (!seasonStateDatabaseEnabled() || seasonStateRepository == null || !seasonStateRepository.isReady()) {
      sender.sendMessage("[Season] DB leaderboard is unavailable. check database.enabled/use_for_season_state.");
      return;
    }

    String roundToken = args.length >= 2 ? args[1] : "latest";
    int limit = resolveScoreTopCount(args.length >= 3 ? args[2] : null);
    Long targetRoundId;
    try {
      if ("latest".equalsIgnoreCase(roundToken)) {
        targetRoundId = seasonStateRepository.findLatestLeaderboardRoundId(seasonId(), currentServerName());
      } else {
        targetRoundId = parseLong(roundToken);
      }
    } catch (SQLException exception) {
      sender.sendMessage("[Season] Failed to read DB leaderboard round id: " + exception.getMessage());
      return;
    }

    if (targetRoundId == null || targetRoundId <= 0L) {
      sender.sendMessage("[Season] No persisted leaderboard round found.");
      return;
    }

    List<SeasonStateJdbcRepository.RoundLeaderboardEntry> entries;
    try {
      entries = seasonStateRepository.loadRoundLeaderboard(
          seasonId(),
          currentServerName(),
          targetRoundId,
          limit
      );
    } catch (SQLException exception) {
      sender.sendMessage("[Season] Failed to load DB leaderboard: " + exception.getMessage());
      return;
    }

    if (entries == null || entries.isEmpty()) {
      sender.sendMessage("[Season] No persisted leaderboard entries for round " + targetRoundId + ".");
      return;
    }

    sender.sendMessage("[Season] persisted leaderboard round=" + targetRoundId + " top=" + entries.size());
    for (SeasonStateJdbcRepository.RoundLeaderboardEntry entry : entries) {
      if (entry == null) {
        continue;
      }
      String name = (entry.playerName() == null || entry.playerName().isBlank())
          ? entry.playerUuid()
          : entry.playerName();
      sender.sendMessage(
          "[Season] #"
              + entry.rankNo()
              + " "
              + name
              + " snapshot="
              + formatScoreAmount(entry.snapshotScore())
              + " weighted="
              + String.format(Locale.ROOT, "%.1f", entry.weightedScore())
              + (entry.escapedWithinWindow() ? " escape" : " survival")
              + (entry.winnerBonusGranted() ? " bonus" : "")
      );
    }
  }

  private boolean onBorderCommand(CommandSender sender, String[] args) {
    if (!isSeasonGameplayServer()) {
      sender.sendMessage("[Season] This command is only available on " + vaultDepositServer() + ".");
      return true;
    }

    if (args.length < 1) {
      sender.sendMessage("[Season] /border setCenter <x> <z>");
      sender.sendMessage("[Season] /border setRadius <start> <end> <duration_seconds>");
      return true;
    }

    String sub = args[0].toLowerCase(Locale.ROOT);
    if ("setcenter".equals(sub)) {
      if (args.length < 3) {
        sender.sendMessage("[Season] /border setCenter <x> <z>");
        return true;
      }

      Double x = parseDouble(args[1]);
      Double z = parseDouble(args[2]);
      if (x == null || z == null) {
        sender.sendMessage("[Season] Invalid coordinates.");
        return true;
      }

      getConfig().set("border.center.x", x);
      getConfig().set("border.center.z", z);
      saveConfig();
      sender.sendMessage("[Season] border.center set to " + x + "," + z);
      return true;
    }

    if ("setradius".equals(sub)) {
      if (args.length < 4) {
        sender.sendMessage("[Season] /border setRadius <start> <end> <duration_seconds>");
        return true;
      }

      Double start = parseDouble(args[1]);
      Double end = parseDouble(args[2]);
      Long duration = parseLong(args[3]);
      if (start == null || end == null || duration == null) {
        sender.sendMessage("[Season] Invalid number.");
        return true;
      }
      if (start <= 0.0D || end <= 0.0D || end > start || duration <= 0L) {
        sender.sendMessage("[Season] Require start>0, end>0, end<=start, duration>0.");
        return true;
      }

      getConfig().set("border.radius.start", start);
      getConfig().set("border.radius.end", end);
      getConfig().set("border.shrink_seconds", duration);
      borderShrinkStartedEpochSecond = nowEpochSecond();
      saveConfig();
      saveState();
      sender.sendMessage(
          "[Season] border.radius start=" + start + " end=" + end + " duration=" + duration + "s"
      );
      return true;
    }

    sender.sendMessage("[Season] Unknown border subcommand.");
    return true;
  }

  private boolean onInformationCommand(CommandSender sender, String[] args) {
    if (!isSeasonGameplayServer()) {
      sender.sendMessage("[Season] This command is only available on " + vaultDepositServer() + ".");
      return true;
    }
    if (!(sender instanceof Player player)) {
      sender.sendMessage("[Season] This command can only be used by players.");
      return true;
    }

    PlayerRoundData data = players.get(player.getUniqueId());
    if (data == null || data.isOut()) {
      player.sendMessage("[Season] Round data unavailable.");
      return true;
    }
    String requiredEffectId = INFORMATION_REQUIRED_EFFECT_ID;
    int tier = highestActiveEffectTier(data, requiredEffectId);
    if (tier <= 0) {
      player.sendMessage("[Season] " + effectDisplayName(requiredEffectId) + " 활성화 상태에서만 사용할 수 있습니다.");
      return true;
    }

    if (args.length < 1) {
      player.sendMessage("[Season] /information <structure>");
      player.sendMessage("[Season] 예시: /information stronghold");
      return true;
    }
    if (player.getWorld() == null || !player.getWorld().canGenerateStructures()) {
      player.sendMessage("[Season] 현재 월드에서는 구조물 탐색을 지원하지 않습니다.");
      return true;
    }

    StructureType structureType = resolveStructureType(args[0]);
    if (structureType == null) {
      player.sendMessage("[Season] 알 수 없는 구조물: " + args[0]);
      player.sendMessage("[Season] 예시: stronghold, village, bastion_remnant, end_city");
      return true;
    }

    UUID playerId = player.getUniqueId();
    String effectId = requiredEffectId;
    if (!useEffectCooldown(playerId, effectId, "information_command", 120L)) {
      long remain = effectCooldownRemaining(playerId, effectId, "information_command");
      player.sendMessage("[Season] /information 대기시간: " + remain + "s");
      return true;
    }

    Location located = player.getWorld().locateNearestStructure(
        player.getLocation(),
        structureType,
        256,
        false
    );
    if (located == null) {
      player.sendMessage("[Season] 구조물을 찾지 못했습니다: " + structureDisplayName(structureType));
      return true;
    }

    Location current = player.getLocation();
    Vector delta = located.toVector().subtract(current.toVector());
    double distance = Math.sqrt(Math.max(0.0D, delta.lengthSquared()));
    String direction = compassDirectionForPlayer(player, delta);
    String message = String.format(
        Locale.ROOT,
        "[Season] %s 좌표: x=%d y=%d z=%d | %s %.0fm",
        structureDisplayName(structureType),
        located.getBlockX(),
        located.getBlockY(),
        located.getBlockZ(),
        direction,
        distance
    );
    player.sendMessage(message);
    player.sendActionBar(ChatColor.AQUA + "정보 획득: " + structureDisplayName(structureType) + " / " + direction);
    player.spawnParticle(
        Particle.END_ROD,
        located.clone().add(0.5D, 1.0D, 0.5D),
        Math.max(6, 4 + tier),
        0.25D,
        0.35D,
        0.25D,
        0.0D
    );
    return true;
  }

  private int highestActiveEffectTier(PlayerRoundData data, String effectId) {
    if (data == null || effectId == null || effectId.isBlank()) {
      return 0;
    }
    String normalized = normalizeEffectId(effectId);
    int best = 0;
    for (ActiveSeasonEffect effect : collectAllActiveEffects(data)) {
      if (effect == null || effect.getId() == null || effect.getId().isBlank()) {
        continue;
      }
      if (!normalized.equals(normalizeEffectId(effect.getId()))) {
        continue;
      }
      best = Math.max(best, Math.max(1, Math.min(4, effect.getTier())));
    }
    return best;
  }

  private String effectDisplayName(String effectId) {
    String normalized = normalizeEffectId(effectId);
    EffectDefinition definition = effectDefinitionsById.get(normalized);
    if (definition == null || definition.displayName() == null || definition.displayName().isBlank()) {
      return normalized;
    }
    return normalized + " (" + definition.displayName() + ")";
  }

  private StructureType resolveStructureType(String token) {
    if (token == null || token.isBlank()) {
      return null;
    }
    Map<String, StructureType> types = StructureType.getStructureTypes();
    if (types == null || types.isEmpty()) {
      return null;
    }
    String normalized = token.trim().toLowerCase(Locale.ROOT).replace('-', '_');

    StructureType direct = types.get(normalized);
    if (direct != null) {
      return direct;
    }
    if (!normalized.contains(":")) {
      direct = types.get("minecraft:" + normalized);
      if (direct != null) {
        return direct;
      }
    }

    String alias = switch (normalized) {
      case "fortress" -> "nether_fortress";
      case "outpost" -> "pillager_outpost";
      case "mansion" -> "woodland_mansion";
      case "bastion" -> "bastion_remnant";
      case "desert_temple" -> "desert_pyramid";
      case "jungle_temple" -> "jungle_pyramid";
      case "witch_hut" -> "swamp_hut";
      case "treasure" -> "buried_treasure";
      default -> normalized;
    };
    direct = types.get(alias);
    if (direct != null) {
      return direct;
    }
    direct = types.get("minecraft:" + alias);
    if (direct != null) {
      return direct;
    }

    for (Map.Entry<String, StructureType> entry : types.entrySet()) {
      String key = entry.getKey();
      StructureType type = entry.getValue();
      if (key != null) {
        String loweredKey = key.toLowerCase(Locale.ROOT);
        if (loweredKey.equals(normalized) || loweredKey.equals("minecraft:" + normalized)) {
          return type;
        }
        if (loweredKey.endsWith(":" + normalized)) {
          return type;
        }
      }
      String name = type == null ? "" : type.getName();
      if (name != null && !name.isBlank() && name.equalsIgnoreCase(normalized)) {
        return type;
      }
    }
    return null;
  }

  private List<String> availableStructureTypeTokens() {
    Map<String, StructureType> types = StructureType.getStructureTypes();
    if (types == null || types.isEmpty()) {
      return List.of();
    }
    Set<String> tokens = new HashSet<>();
    for (String key : types.keySet()) {
      if (key == null || key.isBlank()) {
        continue;
      }
      String lowered = key.toLowerCase(Locale.ROOT);
      if (lowered.startsWith("minecraft:")) {
        tokens.add(lowered.substring("minecraft:".length()));
      } else {
        tokens.add(lowered);
      }
    }
    tokens.addAll(List.of(
        "fortress",
        "outpost",
        "mansion",
        "bastion",
        "desert_temple",
        "jungle_temple",
        "witch_hut",
        "treasure"
    ));
    List<String> sorted = new ArrayList<>(tokens);
    Collections.sort(sorted);
    return sorted;
  }

  private String structureDisplayName(StructureType structureType) {
    if (structureType == null) {
      return "unknown";
    }
    String name = structureType.getName();
    if (name == null || name.isBlank()) {
      return "unknown";
    }
    return name.toLowerCase(Locale.ROOT);
  }

  private boolean onVaultCommand(CommandSender sender, String[] args) {
    if (args.length >= 1 && "unlock".equalsIgnoreCase(args[0])) {
      return onVaultUnlockCommand(sender, args);
    }
    if (args.length > 0) {
      sender.sendMessage("[Season] /vault");
      sender.sendMessage("[Season] /vault unlock <player|uuid>");
      return true;
    }

    if (!(sender instanceof Player player)) {
      sender.sendMessage("[Season] This command can only be used by players.");
      return true;
    }

    if (vaultRepository == null || !vaultRepository.isReady()) {
      player.sendMessage("[Season] Vault is unavailable (DB not ready).");
      return true;
    }

    VaultMode mode = resolveVaultMode();
    if (mode == null) {
      if (currentServerName().equalsIgnoreCase(vaultDepositServer())) {
        player.sendMessage("[Season] Vault deposit is only available in state " + vaultEnabledState() + ".");
      } else {
        player.sendMessage("[Season] Vault is not available on this server.");
      }
      return true;
    }

    UUID owner = player.getUniqueId();
    int rows = vaultRowsFor(owner);
    int slots = rows * 9;

    if (openVaultSessions.containsKey(owner)) {
      player.closeInventory();
    }

    if (vaultRequireDbLock()) {
      try {
        boolean locked = vaultRepository.tryAcquireLock(owner.toString(), currentServerName(), vaultLockStaleSeconds());
        if (!locked) {
          player.sendMessage("[Season] Your vault is currently open on another server.");
          return true;
        }
      } catch (SQLException exception) {
        getLogger().warning("Failed to acquire vault lock for " + owner + ": " + exception.getMessage());
        player.sendMessage("[Season] Vault lock error. Try again shortly.");
        return true;
      }
    }

    try {
      ItemStack[] contents = vaultRepository.loadVaultItems(owner.toString(), slots);
      String title = mode == VaultMode.DEPOSIT ? "Vault Deposit" : "Vault Withdraw";
      VaultInventoryHolder holder = new VaultInventoryHolder(owner, mode);
      Inventory inventory = Bukkit.createInventory(holder, slots, title);
      holder.bindInventory(inventory);
      inventory.setContents(contents);

      openVaultSessions.put(owner, new VaultSession(owner, slots, mode));
      player.openInventory(inventory);
      return true;
    } catch (SQLException exception) {
      getLogger().warning("Failed to open vault for " + owner + ": " + exception.getMessage());
      player.sendMessage("[Season] Failed to open vault.");
      if (vaultRequireDbLock()) {
        vaultRepository.releaseLock(owner.toString(), currentServerName());
      }
      return true;
    }
  }

  private boolean onVaultUnlockCommand(CommandSender sender, String[] args) {
    if (!sender.hasPermission("seasonmanager.admin")) {
      sender.sendMessage("[Season] You do not have permission.");
      return true;
    }

    if (args.length < 2) {
      sender.sendMessage("[Season] /vault unlock <player|uuid>");
      return true;
    }

    if (vaultRepository == null || !vaultRepository.isReady()) {
      sender.sendMessage("[Season] Vault is unavailable (DB not ready).");
      return true;
    }

    UUID owner = resolvePlayerUuid(args[1]);
    if (owner == null) {
      sender.sendMessage("[Season] Unknown player or UUID: " + args[1]);
      return true;
    }

    openVaultSessions.remove(owner);
    Player online = Bukkit.getPlayer(owner);
    if (online != null) {
      online.closeInventory();
    }

    vaultRepository.forceReleaseLock(owner.toString());
    sender.sendMessage("[Season] Vault lock force-unlocked for " + owner + ".");
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    String cmd = command.getName().toLowerCase(Locale.ROOT);

    if ("season".equals(cmd)) {
      if (args.length == 1) {
        return complete(args[0], List.of("state", "round", "slot", "effect", "catalog", "profile", "debug", "reset"));
      }
      if (args.length == 2 && "state".equalsIgnoreCase(args[0])) {
        List<String> options = new ArrayList<>();
        for (SeasonState value : SeasonState.values()) {
          options.add(value.name());
        }
        return complete(args[1], options);
      }
      if (args.length == 2 && "round".equalsIgnoreCase(args[0])) {
        return complete(args[1], List.of("info"));
      }
      if (args.length == 2 && "reset".equalsIgnoreCase(args[0])) {
        return complete(args[1], List.of("now"));
      }
      if (args.length == 2 && "profile".equalsIgnoreCase(args[0])) {
        return complete(args[1], List.of("show", "reload", "set"));
      }
      if (args.length == 2 && "catalog".equalsIgnoreCase(args[0])) {
        return complete(args[1], List.of("show", "reload"));
      }
      if (args.length == 2 && "debug".equalsIgnoreCase(args[0])) {
        return complete(args[1], List.of("on", "off"));
      }
      if (args.length == 3 && "profile".equalsIgnoreCase(args[0]) && "set".equalsIgnoreCase(args[1])) {
        return complete(args[2], availableSeasonProfiles());
      }
      if (args.length == 2 && "slot".equalsIgnoreCase(args[0])) {
        return completePlayerNames(args[1]);
      }
      if (args.length == 2 && "effect".equalsIgnoreCase(args[0])) {
        return complete(args[1], List.of("give"));
      }
      if (args.length == 3 && "effect".equalsIgnoreCase(args[0]) && "give".equalsIgnoreCase(args[1])) {
        return completePlayerNames(args[2]);
      }
      if (args.length == 4 && "effect".equalsIgnoreCase(args[0]) && "give".equalsIgnoreCase(args[1])) {
        List<String> ids = new ArrayList<>(effectDefinitionsById.keySet());
        Collections.sort(ids);
        return complete(args[3], ids);
      }
      if (args.length == 5 && "effect".equalsIgnoreCase(args[0]) && "give".equalsIgnoreCase(args[1])) {
        return complete(args[4], List.of("1", "2", "3", "4"));
      }
    }

    if (("slot".equals(cmd) || "perk".equals(cmd)) && args.length == 1) {
      return completePlayerNames(args[0]);
    }

    if ("life".equals(cmd)) {
      if (args.length == 1) {
        return complete(args[0], List.of("set", "out"));
      }
      if (args.length == 2) {
        return completePlayerNames(args[1]);
      }
      if (args.length == 3 && "out".equalsIgnoreCase(args[0])) {
        return complete(args[2], List.of("true", "false"));
      }
    }

    if ("score".equals(cmd)) {
      if (args.length == 1) {
        return complete(args[0], List.of("add", "set", "top", "leaderboard", "history"));
      }
      if (args.length == 2 && ("add".equalsIgnoreCase(args[0]) || "set".equalsIgnoreCase(args[0]))) {
        return completePlayerNames(args[1]);
      }
      if (args.length == 2 && ("top".equalsIgnoreCase(args[0]) || "leaderboard".equalsIgnoreCase(args[0]))) {
        return complete(args[1], List.of("5", "10", "20"));
      }
      if (args.length == 2 && "history".equalsIgnoreCase(args[0])) {
        return complete(args[1], List.of("latest", String.valueOf(roundId)));
      }
      if (args.length == 3 && "history".equalsIgnoreCase(args[0])) {
        return complete(args[2], List.of("5", "10", "20"));
      }
    }

    if ("vault".equals(cmd)) {
      if (args.length == 1) {
        return complete(args[0], List.of("unlock"));
      }
      if (args.length == 2 && "unlock".equalsIgnoreCase(args[0])) {
        return completePlayerNames(args[1]);
      }
    }

    if ("border".equals(cmd)) {
      if (args.length == 1) {
        return complete(args[0], List.of("setCenter", "setRadius"));
      }
    }

    if ("information".equals(cmd) && args.length == 1) {
      return complete(args[0], availableStructureTypeTokens());
    }

    return Collections.emptyList();
  }

  private List<String> completePlayerNames(String token) {
    List<String> names = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      names.add(player.getName());
    }
    return complete(token, names);
  }

  private List<String> complete(String token, Collection<String> options) {
    String lower = token.toLowerCase(Locale.ROOT);
    List<String> out = new ArrayList<>();
    for (String option : options) {
      if (option.toLowerCase(Locale.ROOT).startsWith(lower)) {
        out.add(option);
      }
    }
    return out;
  }

  private List<String> availableSeasonProfiles() {
    File profilesDir = new File(getDataFolder(), "profiles");
    File[] files = profilesDir.listFiles((dir, name) -> name != null && name.endsWith(".yml"));
    if (files == null || files.length == 0) {
      return Collections.emptyList();
    }

    List<String> profiles = new ArrayList<>();
    for (File file : files) {
      String name = file.getName();
      if (name.length() > 4) {
        profiles.add(name.substring(0, name.length() - 4));
      }
    }
    Collections.sort(profiles);
    return profiles;
  }

  private OfflinePlayer resolvePlayer(String token) {
    Player online = Bukkit.getPlayerExact(token);
    if (online != null) {
      return online;
    }

    try {
      UUID uuid = UUID.fromString(token);
      return Bukkit.getOfflinePlayer(uuid);
    } catch (IllegalArgumentException ignored) {
      // not UUID
    }

    OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached(token);
    if (cached != null) {
      return cached;
    }

    return null;
  }

  private UUID resolvePlayerUuid(String token) {
    try {
      return UUID.fromString(token);
    } catch (IllegalArgumentException ignored) {
      // not UUID
    }

    OfflinePlayer target = resolvePlayer(token);
    if (target != null) {
      return target.getUniqueId();
    }
    return null;
  }

  private Integer parseInt(String raw) {
    try {
      return Integer.parseInt(raw);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private Long parseLong(String raw) {
    try {
      return Long.parseLong(raw);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private Double parseDouble(String raw) {
    try {
      return Double.parseDouble(raw);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private boolean parseBoolean(String raw, boolean fallback) {
    if (raw == null) {
      return fallback;
    }
    if ("true".equalsIgnoreCase(raw) || "yes".equalsIgnoreCase(raw) || "1".equalsIgnoreCase(raw)) {
      return true;
    }
    if ("false".equalsIgnoreCase(raw) || "no".equalsIgnoreCase(raw) || "0".equalsIgnoreCase(raw)) {
      return false;
    }
    return fallback;
  }

  private long nowEpochSecond() {
    return Instant.now().getEpochSecond();
  }

  private int baseLives() {
    return Math.max(0, getConfig().getInt("lives.base", 3));
  }

  private int respawnCostBase() {
    return Math.max(1, getConfig().getInt("lives.respawn_cost_base", 1000));
  }

  private double pvpStealPercent() {
    double configured = getConfig().getDouble("score.pvp_steal_percent", 0.5D);
    if (configured < 0.0D) {
      return 0.0D;
    }
    if (configured > 1.0D) {
      return 1.0D;
    }
    return configured;
  }

  private boolean scoreGainEnabledInCurrentState() {
    List<String> states = getConfig().getStringList("score.gain_states");
    if (states == null || states.isEmpty()) {
      return state == SeasonState.HARDCORE || state == SeasonState.CLIMAX;
    }
    for (String configured : states) {
      if (configured != null && state.name().equalsIgnoreCase(configured.trim())) {
        return true;
      }
    }
    return false;
  }

  private boolean scoreSurvivalEnabled() {
    return getConfig().getBoolean("score.survival.enabled", true);
  }

  private int scoreSurvivalIntervalSeconds() {
    return Math.max(1, getConfig().getInt("score.survival.interval_seconds", 30));
  }

  private long scoreSurvivalPointsPerInterval() {
    return Math.max(0L, getConfig().getLong("score.survival.points_per_interval", 5L));
  }

  private boolean scoreMobEnabled() {
    return getConfig().getBoolean("score.mob.enabled", true);
  }

  private long scoreMobDefaultPoints() {
    return Math.max(0L, getConfig().getLong("score.mob.default_points", 8L));
  }

  private long scoreMobPoints(EntityType type) {
    if (type == null) {
      return 0L;
    }
    String path = "score.mob.per_type." + type.name();
    if (getConfig().contains(path)) {
      return Math.max(0L, getConfig().getLong(path, 0L));
    }
    return scoreMobDefaultPoints();
  }

  private boolean scoreMiningEnabled() {
    return getConfig().getBoolean("score.mining.enabled", true);
  }

  private long scoreMiningPoints(Material blockType) {
    if (blockType == null) {
      return 0L;
    }
    String path = "score.mining.per_block." + blockType.name();
    if (!getConfig().contains(path)) {
      return 0L;
    }
    return Math.max(0L, getConfig().getLong(path, 0L));
  }

  private boolean scoreDragonJackpotEnabled() {
    return getConfig().getBoolean("score.dragon_jackpot.enabled", true);
  }

  private boolean scoreDragonDamageStateAllowed() {
    return state == SeasonState.CLIMAX || state == SeasonState.HARDCORE;
  }

  private long scoreDragonAssistTotalPoints() {
    return Math.max(0L, getConfig().getLong("score.dragon_jackpot.assist_total_points", 5000L));
  }

  private long scoreDragonFinalHitBonus() {
    return Math.max(0L, getConfig().getLong("score.dragon_jackpot.final_hit_bonus", 1000L));
  }

  private long scoreDragonFirstEggBonus() {
    return Math.max(0L, getConfig().getLong("score.dragon_jackpot.first_egg_bonus", 3000L));
  }

  private boolean scoreHudEnabled() {
    return getConfig().getBoolean("score.hud.enabled", true);
  }

  private boolean scoreHudActionbarEnabled() {
    return getConfig().getBoolean("score.hud.actionbar.enabled", true);
  }

  private int scoreHudActionbarRefreshSeconds() {
    return Math.max(0, getConfig().getInt("score.hud.actionbar.refresh_seconds", 8));
  }

  private boolean scoreHudActionbarShowWhenNoChange() {
    return getConfig().getBoolean("score.hud.actionbar.show_when_no_change", true);
  }

  private boolean outSetsScoreZero() {
    return getConfig().getBoolean("score.out_sets_zero", true);
  }

  private int newbieProtectionSeconds() {
    return Math.max(0, getConfig().getInt("player.newbie_protection_seconds", 600));
  }

  private boolean newbieEffectsEnabled() {
    return getConfig().getBoolean("player.newbie_effects.enabled", true);
  }

  private int newbieEffectRefreshTicks() {
    int seconds = Math.max(1, getConfig().getInt("player.newbie_effects.refresh_duration_seconds", 6));
    return seconds * 20;
  }

  private int newbieEffectSpeedAmplifier() {
    return Math.max(-1, getConfig().getInt("player.newbie_effects.speed", 1));
  }

  private int newbieEffectRegenerationAmplifier() {
    return Math.max(-1, getConfig().getInt("player.newbie_effects.regeneration", 0));
  }

  private int newbieEffectResistanceAmplifier() {
    return Math.max(-1, getConfig().getInt("player.newbie_effects.resistance", 0));
  }

  private double playerDamageCapEnvironmentRatio() {
    double value = getConfig().getDouble("player.damage_caps.environment_health_ratio", 0.80D);
    return Math.max(0.05D, Math.min(1.0D, value));
  }

  private double playerDamageCapPveRatio() {
    double value = getConfig().getDouble("player.damage_caps.pve_health_ratio", 0.72D);
    return Math.max(0.05D, Math.min(1.0D, value));
  }

  private double playerDamageCapPvpRatio() {
    double value = getConfig().getDouble("player.damage_caps.pvp_health_ratio", 0.60D);
    return Math.max(0.05D, Math.min(1.0D, value));
  }

  private boolean slotsEnabled() {
    return getConfig().getBoolean("slots.enabled", true);
  }

  private String slotProgressScoreSource() {
    return getConfig().getString("slots.progress_score_source", "PEAK_SCORE");
  }

  private int slotStartCount() {
    return Math.max(0, getConfig().getInt("slots.start", 1));
  }

  private int slotBaseMax() {
    int start = slotStartCount();
    return Math.max(start, getConfig().getInt("slots.max", 5));
  }

  private long slotProgressPerUnlock() {
    return Math.max(1L, getConfig().getLong("slots.progress_per_unlock", 10000L));
  }

  private boolean slotBlessingEnabled() {
    return getConfig().getBoolean("slots.blessing.enabled", true);
  }

  private int slotBlessingMax() {
    int base = slotBaseMax();
    return Math.max(0, Math.min(base, getConfig().getInt("slots.blessing.max", base)));
  }

  private boolean slotCurseEnabled() {
    return getConfig().getBoolean("slots.curse.enabled", true);
  }

  private int slotCurseMax() {
    int base = slotBaseMax();
    return Math.max(0, Math.min(base, getConfig().getInt("slots.curse.max", base)));
  }

  private boolean slotAnnounceUnlock() {
    return getConfig().getBoolean("slots.announce_unlock", true);
  }

  private boolean cardsEnabled() {
    return getConfig().getBoolean("cards.enabled", true);
  }

  private boolean cardsEffectLogicEnabled() {
    return getConfig().getBoolean("cards.effect_logic_enabled", true);
  }

  private boolean cardsEnabledInCurrentState() {
    List<String> states = getConfig().getStringList("cards.states");
    if (states == null || states.isEmpty()) {
      return state == SeasonState.HARDCORE || state == SeasonState.CLIMAX;
    }
    for (String configured : states) {
      if (configured != null && state.name().equalsIgnoreCase(configured.trim())) {
        return true;
      }
    }
    return false;
  }

  private World cardsWorld() {
    String configured = getConfig().getString("cards.world_name", "world");
    if (configured != null && !configured.isBlank()) {
      World named = Bukkit.getWorld(configured);
      if (named != null) {
        return named;
      }
    }
    return borderWorld();
  }

  private int cardsMidnightTick() {
    int configured = getConfig().getInt("cards.midnight_tick", 18000);
    if (configured < 0) {
      return 0;
    }
    if (configured > 23999) {
      return 23999;
    }
    return configured;
  }

  private int cardsTriggerWindowTicks() {
    return Math.max(1, getConfig().getInt("cards.trigger_window_ticks", 120));
  }

  private boolean cardsDrawForOutPlayers() {
    return getConfig().getBoolean("cards.draw_for_out_players", false);
  }

  private double cardsDuplicateWeightScale() {
    double configured = getConfig().getDouble("cards.duplicate_weight_scale", 0.35D);
    if (configured < 0.01D) {
      return 0.01D;
    }
    if (configured > 1.0D) {
      return 1.0D;
    }
    return configured;
  }

  private boolean cardsBonusDrawEnabled() {
    return getConfig().getBoolean("cards.draws.bonus_enabled", true);
  }

  private String cardsDrawBonusScoreSource() {
    return getConfig().getString("cards.draws.score_source", "CURRENT_SCORE");
  }

  private long cardsDrawBonusScorePerDraw() {
    return Math.max(1L, getConfig().getLong("cards.draws.score_per_extra_draw", 20000L));
  }

  private int cardsDrawBonusMaxExtraDraws() {
    return Math.max(0, getConfig().getInt("cards.draws.max_extra_draws", 3));
  }

  private int cardsBonusDrawCount(PlayerRoundData data) {
    // Deprecated by season rule: score-based bonus draw mechanic is not used.
    return 0;
  }

  private boolean cardsRerollEnabled() {
    return getConfig().getBoolean("cards.reroll.enabled", true);
  }

  private long cardsRerollCostScore() {
    if (seasonDebugModeEnabled()) {
      return 0L;
    }
    return Math.max(0L, getConfig().getLong("cards.reroll.cost_score", 12000L));
  }

  private long cardsRerollCooldownSeconds() {
    if (seasonDebugModeEnabled()) {
      return 1L;
    }
    return Math.max(0L, getConfig().getLong("cards.reroll.cooldown_seconds", 45L));
  }

  private boolean cardsRerollRequirePlayable() {
    return getConfig().getBoolean("cards.reroll.require_playable", true);
  }

  private boolean cardsRerollSevereBonusOnReroll() {
    return getConfig().getBoolean("cards.reroll.severe_bonus_on_reroll", false);
  }

  private boolean cardsOneTimeRollEnabled() {
    return getConfig().getBoolean("cards.one_time_roll.enabled", true);
  }

  private int cardsInitialSetupFreeRerolls() {
    return Math.max(0, getConfig().getInt("cards.one_time_roll.initial_free_rerolls", 5));
  }

  private boolean cardsInitialSetupInvulnerableWhileGui() {
    return getConfig().getBoolean("cards.one_time_roll.invulnerable_while_gui", true);
  }

  private boolean cardsOneTimeRollNonExpiring() {
    return getConfig().getBoolean("cards.one_time_roll.non_expiring", true);
  }

  private boolean cardsTierUpgradeEnabled() {
    return getConfig().getBoolean("cards.tier_upgrade.enabled", true);
  }

  private long cardsTierUpgradeCostScore() {
    if (seasonDebugModeEnabled()) {
      return 0L;
    }
    return Math.max(0L, getConfig().getLong("cards.tier_upgrade.cost_score", 60000L));
  }

  private long cardsDrawBonusProgressScore(PlayerRoundData data) {
    String source = cardsDrawBonusScoreSource();
    if (source != null && source.equalsIgnoreCase("PEAK_SCORE")) {
      return Math.max(0L, data.getPeakScore());
    }
    return Math.max(0L, data.getScore());
  }

  private boolean cardsMultiplierEnabled() {
    return cardsEffectLogicEnabled() && getConfig().getBoolean("cards.score_multiplier.enabled", true);
  }

  private double cardsMultiplierBlessingPerStack() {
    if (getConfig().contains("cards.score_multiplier.blessing_per_tier")) {
      return getConfig().getDouble("cards.score_multiplier.blessing_per_tier", 0.03D);
    }
    return getConfig().getDouble("cards.score_multiplier.blessing_per_stack", 0.03D);
  }

  private double cardsMultiplierCursePerStack() {
    if (getConfig().contains("cards.score_multiplier.curse_per_tier")) {
      return getConfig().getDouble("cards.score_multiplier.curse_per_tier", 0.06D);
    }
    return getConfig().getDouble("cards.score_multiplier.curse_per_stack", 0.06D);
  }

  private double cardsMultiplierMin() {
    return getConfig().getDouble("cards.score_multiplier.min", 1.0D);
  }

  private double cardsMultiplierMax() {
    double min = cardsMultiplierMin();
    return Math.max(min, getConfig().getDouble("cards.score_multiplier.max", 3.0D));
  }

  private long cardsSevereCurseDefaultBonusPoints() {
    return Math.max(0L, getConfig().getLong("cards.severe_curse.default_bonus_points", 250L));
  }

  private boolean cardsSevereBonusOnUpgrade() {
    return getConfig().getBoolean("cards.severe_curse.bonus_on_upgrade", true);
  }

  private int cardsCatchupMaxDaysPerTick() {
    return Math.max(1, getConfig().getInt("cards.catchup.max_days_per_tick", 7));
  }

  private boolean cardsTrimToSlotsOnDailyDraw() {
    return getConfig().getBoolean("cards.trim_to_slots_on_daily_draw", false);
  }

  private String cardsCatalogFileName() {
    return getConfig().getString("cards.catalog.file", "ender_shadow_s1.yml");
  }

  private List<String> cardsCatalogAppendFileNames() {
    List<String> configured = getConfig().getStringList("cards.catalog.append_files");
    if (configured == null || configured.isEmpty()) {
      return List.of();
    }

    List<String> files = new ArrayList<>();
    Set<String> seen = new HashSet<>();
    for (String entry : configured) {
      if (entry == null || entry.isBlank()) {
        continue;
      }
      String normalized = entry.trim();
      if (normalized.isEmpty()) {
        continue;
      }
      String dedupeKey = normalized.toLowerCase(Locale.ROOT);
      if (seen.add(dedupeKey)) {
        files.add(normalized);
      }
    }
    return files;
  }

  private boolean cardsCatalogAutoCreate() {
    return getConfig().getBoolean("cards.catalog.auto_create", true);
  }

  private boolean cardsStackMigrationEnabled() {
    return false;
  }

  private boolean cardsCatalogSyncFromResource() {
    return getConfig().getBoolean("cards.catalog.sync_from_resource", true);
  }

  private boolean cardsCatalogFallbackEnabled() {
    // Locked off: catalog fallback to deprecated pools is disabled for this server profile.
    return false;
  }

  private String cardsCatalogPatchDirName() {
    String configured = getConfig().getString("cards.catalog.patch_dir", "patches");
    if (configured == null || configured.isBlank()) {
      return "patches";
    }
    return configured.trim();
  }

  private boolean cardsCatalogPatchAutoDiscover() {
    return getConfig().getBoolean("cards.catalog.patch_auto_discover", true);
  }

  private List<String> cardsCatalogPatchFileNames() {
    List<String> configured = getConfig().getStringList("cards.catalog.patch_files");
    if (configured == null || configured.isEmpty()) {
      return List.of();
    }

    List<String> files = new ArrayList<>();
    Set<String> seen = new HashSet<>();
    for (String entry : configured) {
      if (entry == null || entry.isBlank()) {
        continue;
      }
      String normalized = entry.trim();
      if (normalized.isEmpty()) {
        continue;
      }
      String dedupeKey = normalized.toLowerCase(Locale.ROOT);
      if (seen.add(dedupeKey)) {
        files.add(normalized);
      }
    }
    return files;
  }

  private int cardsEffectDurationDays() {
    return Math.max(1, getConfig().getInt("cards.effect_duration_ingame_days", 7));
  }

  private int cardsMaxTier() {
    return Math.max(1, getConfig().getInt("cards.max_tier", 4));
  }

  private long cardsUpgradeOverflowBonusPoints() {
    return Math.max(0L, getConfig().getLong("cards.upgrade_overflow_bonus_points", 100L));
  }

  private double cardsUpgradeTierBiasBase() {
    return Math.max(1.0D, getConfig().getDouble("cards.upgrade_tier_bias_base", 5.0D));
  }

  private boolean cardsRuntimeEffectsEnabled() {
    return cardsEffectLogicEnabled() && getConfig().getBoolean("cards.runtime_effects.enabled", true);
  }

  private int cardsRuntimeEffectIntervalSeconds() {
    return Math.max(1, getConfig().getInt("cards.runtime_effects.interval_seconds", 1));
  }

  private int cardsRuntimeEffectDurationTicks() {
    int seconds = Math.max(1, getConfig().getInt("cards.runtime_effects.refresh_duration_seconds", 3));
    return seconds * 20;
  }

  private int runtimeScoreDecayIntervalSeconds() {
    return Math.max(10, getConfig().getInt("cards.runtime_effects.score_decay_interval_seconds", 60));
  }

  private boolean seasonStateDatabaseEnabled() {
    return databaseEnabled() && getConfig().getBoolean("database.use_for_season_state", true);
  }

  private boolean borderAutoCenterEnabled() {
    return getConfig().getBoolean("border.center.auto_from_stronghold_on_round_start", true);
  }

  private boolean borderAutoCenterPreserveExisting() {
    return getConfig().getBoolean("border.center.auto_preserve_existing", true);
  }

  private int borderAutoCenterSearchRadiusChunks() {
    return Math.max(64, getConfig().getInt("border.center.auto_search_radius_chunks", 1536));
  }

  private boolean borderAutoCenterPreferUnexplored() {
    return getConfig().getBoolean("border.center.auto_find_unexplored", false);
  }

  private boolean enforceOutSpectator() {
    return getConfig().getBoolean("player.enforce_out_spectator", true);
  }

  private boolean allowOutInFreePlay() {
    return getConfig().getBoolean("player.allow_out_in_free_play", true);
  }

  private boolean safeSpawnEnabled() {
    return getConfig().getBoolean("spawn_separation.enabled", true);
  }

  private boolean safeSpawnEnabledInCurrentState() {
    List<String> states = getConfig().getStringList("spawn_separation.states");
    if (states == null || states.isEmpty()) {
      return state == SeasonState.HARDCORE || state == SeasonState.CLIMAX;
    }
    for (String configured : states) {
      if (configured != null && state.name().equalsIgnoreCase(configured.trim())) {
        return true;
      }
    }
    return false;
  }

  private double safeSpawnMinDistanceFromPlayers() {
    return Math.max(0.0D, getConfig().getDouble("spawn_separation.min_distance_from_players", 800.0D));
  }

  private double safeSpawnRingMinRadius() {
    return Math.max(8.0D, getConfig().getDouble("spawn_separation.ring.min_radius", 1200.0D));
  }

  private double safeSpawnRingMaxRadius() {
    double min = safeSpawnRingMinRadius();
    return Math.max(min, getConfig().getDouble("spawn_separation.ring.max_radius", 2600.0D));
  }

  private double safeSpawnBorderMargin() {
    return Math.max(0.0D, getConfig().getDouble("spawn_separation.ring.border_margin", 64.0D));
  }

  private int safeSpawnMaxAttempts() {
    return Math.max(1, getConfig().getInt("spawn_separation.search.max_attempts", 120));
  }

  private int safeSpawnVerticalSearchDepth() {
    return Math.max(1, getConfig().getInt("spawn_separation.search.vertical_search_depth", 24));
  }

  private int safeSpawnMinGroundY(World world) {
    int worldMin = world.getMinHeight() + 1;
    return Math.max(worldMin, getConfig().getInt("spawn_separation.safe.min_y", worldMin));
  }

  private boolean freePlayIgnoresLifeRules() {
    return getConfig().getBoolean("free_play.ignore_life_rules", true);
  }

  private boolean climaxAutoOnFirstEndEntry() {
    return getConfig().getBoolean("climax.auto_on_first_end_entry", true);
  }

  private boolean freePlayAutoOnDragonKill() {
    return getConfig().getBoolean("free_play.auto_on_dragon_kill", true);
  }

  private int freePlayEscapeWindowSeconds() {
    return Math.max(1, getConfig().getInt("free_play.escape_window_seconds", 900));
  }

  private double freePlaySurvivalWeight() {
    return Math.max(0.0D, getConfig().getDouble("free_play.survival_weight", 1.0D));
  }

  private double freePlayEscapeWeight() {
    return Math.max(0.0D, getConfig().getDouble("free_play.escape_weight", 1.2D));
  }

  private int freePlayWinnerBonusTopN() {
    return Math.max(0, getConfig().getInt("free_play.winner_bonus_top_n", 3));
  }

  private long freePlayWinnerBonusScore() {
    return Math.max(0L, getConfig().getLong("free_play.winner_bonus_score", 2000L));
  }

  private long totalRoundScore() {
    long sum = 0L;
    for (UUID participant : participants) {
      PlayerRoundData data = players.get(participant);
      if (data == null) {
        continue;
      }
      sum += Math.max(0L, data.getScore());
    }
    return sum;
  }

  private boolean enderAuraEnabled() {
    return getConfig().getBoolean("ender_aura.enabled", true);
  }

  private boolean enderAuraEnabledInCurrentState() {
    List<String> states = getConfig().getStringList("ender_aura.states");
    if (states == null || states.isEmpty()) {
      return state == SeasonState.HARDCORE || state == SeasonState.CLIMAX;
    }
    for (String value : states) {
      if (value != null && state.name().equalsIgnoreCase(value.trim())) {
        return true;
      }
    }
    return false;
  }

  private double enderAuraPlayerScanRadius() {
    return Math.max(8.0D, getConfig().getDouble("ender_aura.scan.player_radius", 112.0D));
  }

  private int enderAuraPlayersPerTick() {
    return Math.max(1, getConfig().getInt("ender_aura.scan.players_per_tick", 4));
  }

  private int enderAuraMaxEndermenPerTick() {
    return Math.max(1, getConfig().getInt("ender_aura.scan.max_endermen_per_tick", 24));
  }

  private double enderAuraRadius() {
    return Math.max(1.0D, getConfig().getDouble("ender_aura.aura_radius", 16.0D));
  }

  private int enderAuraEffectDurationTicks() {
    return Math.max(20, getConfig().getInt("ender_aura.effect_duration_ticks", 60));
  }

  private int enderAuraLevel() {
    long score = totalRoundScore();
    long level3 = Math.max(0L, getConfig().getLong("ender_aura.level3_total_score", 90000L));
    long level2 = Math.max(0L, getConfig().getLong("ender_aura.level2_total_score", 30000L));
    if (score >= level3) {
      return 3;
    }
    if (score >= level2) {
      return 2;
    }
    return 1;
  }

  private int enderAuraSpeedAmplifier(int level) {
    int value = getConfig().getInt("ender_aura.effects.level" + level + ".speed", Math.max(0, level - 1));
    return value < 0 ? -1 : value;
  }

  private int enderAuraStrengthAmplifier(int level) {
    int value = getConfig().getInt("ender_aura.effects.level" + level + ".strength", Math.max(0, level - 1));
    return value < 0 ? -1 : value;
  }

  private boolean stalkerEnabled() {
    return getConfig().getBoolean("stalker.enabled", true);
  }

  private boolean stalkerEnabledInCurrentState() {
    List<String> states = getConfig().getStringList("stalker.states");
    if (states == null || states.isEmpty()) {
      return state == SeasonState.HARDCORE || state == SeasonState.CLIMAX;
    }
    for (String configured : states) {
      if (configured != null && state.name().equalsIgnoreCase(configured.trim())) {
        return true;
      }
    }
    return false;
  }

  private long stalkerFirstSpawnDelaySeconds() {
    return Math.max(1L, getConfig().getLong("stalker.first_spawn_delay_seconds", 1200L));
  }

  private int stalkerRespawnIntervalMinSeconds() {
    return Math.max(1, getConfig().getInt("stalker.respawn_interval_seconds.min", 600));
  }

  private int stalkerRespawnIntervalMaxSeconds() {
    int min = stalkerRespawnIntervalMinSeconds();
    return Math.max(min, getConfig().getInt("stalker.respawn_interval_seconds.max", 900));
  }

  private int stalkerMaxActive() {
    return Math.max(1, getConfig().getInt("stalker.max_active", 3));
  }

  private double stalkerSpawnDistanceMin() {
    return Math.max(2.0D, getConfig().getDouble("stalker.spawn.distance_min", 8.0D));
  }

  private double stalkerSpawnDistanceMax() {
    double min = stalkerSpawnDistanceMin();
    return Math.max(min, getConfig().getDouble("stalker.spawn.distance_max", 16.0D));
  }

  private double stalkerSpawnAngleSpreadDegrees() {
    double value = getConfig().getDouble("stalker.spawn.angle_spread_degrees", 35.0D);
    return Math.max(0.0D, Math.min(180.0D, value));
  }

  private int stalkerBaseSpeedAmplifier() {
    return Math.max(0, getConfig().getInt("stalker.buffs.base_speed", 1));
  }

  private int stalkerBaseStrengthAmplifier() {
    return Math.max(0, getConfig().getInt("stalker.buffs.base_strength", 1));
  }

  private int stalkerEmpowerAmplifierPerStack() {
    return Math.max(0, getConfig().getInt("stalker.buffs.empower_per_overcap", 1));
  }

  private int stalkerMaxEmpowerStacks() {
    return Math.max(0, getConfig().getInt("stalker.buffs.max_empower_stacks", 6));
  }

  private int stalkerBuffDurationTicks() {
    return Math.max(20, getConfig().getInt("stalker.buffs.duration_ticks", 6000));
  }

  private boolean stalkerVisualEnabled() {
    return getConfig().getBoolean("stalker.visual.enabled", true);
  }

  private int stalkerVisualTitleFadeInTicks() {
    return Math.max(0, getConfig().getInt("stalker.visual.title.fade_in_ticks", 6));
  }

  private int stalkerVisualTitleStayTicks() {
    return Math.max(1, getConfig().getInt("stalker.visual.title.stay_ticks", 36));
  }

  private int stalkerVisualTitleFadeOutTicks() {
    return Math.max(0, getConfig().getInt("stalker.visual.title.fade_out_ticks", 10));
  }

  private int stalkerVisualSpawnParticleCount() {
    return Math.max(1, getConfig().getInt("stalker.visual.particles.spawn_count", 70));
  }

  private int stalkerVisualEmpowerParticleCount() {
    return Math.max(1, getConfig().getInt("stalker.visual.particles.empower_count", 56));
  }

  private boolean dragonRaidEnabled() {
    return getConfig().getBoolean("dragon_raid.enabled", true);
  }

  private boolean dragonRaidVisualEnabled() {
    return getConfig().getBoolean("dragon_raid.visual.enabled", true);
  }

  private int dragonRaidVisualTitleFadeInTicks() {
    return Math.max(0, getConfig().getInt("dragon_raid.visual.title.fade_in_ticks", 6));
  }

  private int dragonRaidVisualTitleStayTicks() {
    return Math.max(1, getConfig().getInt("dragon_raid.visual.title.stay_ticks", 40));
  }

  private int dragonRaidVisualTitleFadeOutTicks() {
    return Math.max(0, getConfig().getInt("dragon_raid.visual.title.fade_out_ticks", 12));
  }

  private int dragonRaidVisualPhaseParticleCount(int phase) {
    return switch (phase) {
      case 1 -> Math.max(1, getConfig().getInt("dragon_raid.visual.particles.phase1_count", 120));
      case 2 -> Math.max(1, getConfig().getInt("dragon_raid.visual.particles.phase2_count", 160));
      case 3 -> Math.max(1, getConfig().getInt("dragon_raid.visual.particles.phase3_count", 220));
      default -> Math.max(1, getConfig().getInt("dragon_raid.visual.particles.default_count", 96));
    };
  }

  private boolean dragonRaidEnabledInCurrentState() {
    List<String> states = getConfig().getStringList("dragon_raid.states");
    if (states == null || states.isEmpty()) {
      return state == SeasonState.CLIMAX;
    }
    for (String configured : states) {
      if (configured != null && state.name().equalsIgnoreCase(configured.trim())) {
        return true;
      }
    }
    return false;
  }

  private int dragonRaidWaveIntervalSeconds() {
    return Math.max(1, getConfig().getInt("dragon_raid.wave_interval_seconds", 20));
  }

  private double dragonRaidSpawnDistanceMin() {
    return Math.max(2.0D, getConfig().getDouble("dragon_raid.spawn.distance_min", 8.0D));
  }

  private double dragonRaidSpawnDistanceMax() {
    double min = dragonRaidSpawnDistanceMin();
    return Math.max(min, getConfig().getDouble("dragon_raid.spawn.distance_max", 18.0D));
  }

  private int dragonRaidMobCap(int endPlayers) {
    int base = Math.max(1, getConfig().getInt("dragon_raid.cap.base", 10));
    int perPlayer = Math.max(0, getConfig().getInt("dragon_raid.cap.per_player", 6));
    return base + (Math.max(0, endPlayers) * perPlayer);
  }

  private int dragonRaidPhase1EndermanMin() {
    return Math.max(0, getConfig().getInt("dragon_raid.phase1.enderman_min", 2));
  }

  private int dragonRaidPhase1EndermanMax() {
    int min = dragonRaidPhase1EndermanMin();
    return Math.max(min, getConfig().getInt("dragon_raid.phase1.enderman_max", 3));
  }

  private int dragonRaidPhase2EntryBlazeMin() {
    return Math.max(0, getConfig().getInt("dragon_raid.phase2.entry_blaze_min", 8));
  }

  private int dragonRaidPhase2EntryBlazeMax() {
    int min = dragonRaidPhase2EntryBlazeMin();
    return Math.max(min, getConfig().getInt("dragon_raid.phase2.entry_blaze_max", 12));
  }

  private int dragonRaidPhase2EndermanMin() {
    return Math.max(0, getConfig().getInt("dragon_raid.phase2.enderman_min", 2));
  }

  private int dragonRaidPhase2EndermanMax() {
    int min = dragonRaidPhase2EndermanMin();
    return Math.max(min, getConfig().getInt("dragon_raid.phase2.enderman_max", 3));
  }

  private int dragonRaidPhase2BlazeMin() {
    return Math.max(0, getConfig().getInt("dragon_raid.phase2.blaze_min", 2));
  }

  private int dragonRaidPhase2BlazeMax() {
    int min = dragonRaidPhase2BlazeMin();
    return Math.max(min, getConfig().getInt("dragon_raid.phase2.blaze_max", 3));
  }

  private int dragonRaidPhase3EndermanMin() {
    return Math.max(0, getConfig().getInt("dragon_raid.phase3.enderman_min", 2));
  }

  private int dragonRaidPhase3EndermanMax() {
    int min = dragonRaidPhase3EndermanMin();
    return Math.max(min, getConfig().getInt("dragon_raid.phase3.enderman_max", 3));
  }

  private int dragonRaidPhase3BlazeMin() {
    return Math.max(0, getConfig().getInt("dragon_raid.phase3.blaze_min", 2));
  }

  private int dragonRaidPhase3BlazeMax() {
    int min = dragonRaidPhase3BlazeMin();
    return Math.max(min, getConfig().getInt("dragon_raid.phase3.blaze_max", 3));
  }

  private int dragonRaidPhase3VexMin() {
    return Math.max(0, getConfig().getInt("dragon_raid.phase3.vex_min", 2));
  }

  private int dragonRaidPhase3VexMax() {
    int min = dragonRaidPhase3VexMin();
    return Math.max(min, getConfig().getInt("dragon_raid.phase3.vex_max", 3));
  }

  private int resetCountdownSeconds() {
    return Math.max(1, getConfig().getInt("round.reset_countdown_seconds", 600));
  }

  private boolean roundWorldWipeEnabled() {
    return getConfig().getBoolean("round.world_wipe.enabled", true);
  }

  private boolean roundWorldWipeAutoShutdown() {
    return getConfig().getBoolean("round.world_wipe.auto_shutdown", true);
  }

  private String roundWorldWipeKickMessage() {
    return getConfig().getString(
        "round.world_wipe.kick_message",
        "Round reset in progress. Reconnect shortly."
    );
  }

  private File roundWorldWipeMarkerFile() {
    String markerName = getConfig().getString("round.world_wipe.marker_file", ".season_wipe_request");
    if (markerName == null || markerName.isBlank()) {
      markerName = ".season_wipe_request";
    }
    File worldContainer = Bukkit.getWorldContainer();
    return new File(worldContainer, markerName);
  }

  private List<String> roundWorldWipeWorlds() {
    List<String> configured = getConfig().getStringList("round.world_wipe.worlds");
    List<String> resolved = new ArrayList<>();
    if (configured != null) {
      for (String world : configured) {
        if (world == null) {
          continue;
        }
        String normalized = world.trim();
        if (normalized.isEmpty() || resolved.contains(normalized)) {
          continue;
        }
        resolved.add(normalized);
      }
    }
    if (!resolved.isEmpty()) {
      return resolved;
    }
    return List.of("world", "world_nether", "world_the_end");
  }

  private int autoSaveSeconds() {
    return Math.max(5, getConfig().getInt("round.autosave_seconds", 30));
  }

  private int scorePersistenceDbMinIntervalSeconds() {
    return Math.max(0, getConfig().getInt("score.persistence.db_min_interval_seconds", 5));
  }

  private boolean cancelResetIfPlayableAgain() {
    return getConfig().getBoolean("round.cancel_reset_if_playable_again", true);
  }

  private void closeAllVaultSessions() {
    if (vaultRepository == null || !vaultRepository.isReady() || openVaultSessions.isEmpty()) {
      openVaultSessions.clear();
      return;
    }

    String serverName = currentServerName();
    Set<UUID> owners = new HashSet<>(openVaultSessions.keySet());
    openVaultSessions.clear();

    for (UUID owner : owners) {
      if (vaultRequireDbLock()) {
        vaultRepository.releaseLock(owner.toString(), serverName);
      }
    }
  }

  private VaultMode resolveVaultMode() {
    if (serverRole == ServerRole.DISABLED) {
      return null;
    }
    if (serverRole == ServerRole.SEASON_GAMEPLAY) {
      String enabledState = vaultEnabledState();
      if (state.name().equalsIgnoreCase(enabledState)) {
        return VaultMode.DEPOSIT;
      }
      return null;
    }
    return VaultMode.WITHDRAW;
  }

  private int vaultRowsFor(UUID owner) {
    int defaultRows = Math.max(1, Math.min(6, getConfig().getInt("vault.default_rows", 3)));
    int outRows = Math.max(1, Math.min(6, getConfig().getInt("vault.out_rows", 1)));

    if (serverRole == ServerRole.SEASON_GAMEPLAY) {
      PlayerRoundData data = players.get(owner);
      if (data != null && data.isOut()) {
        return outRows;
      }
    }
    return defaultRows;
  }

  private boolean isSeasonGameplayServer() {
    return serverRole == ServerRole.SEASON_GAMEPLAY;
  }

  private String currentServerName() {
    return getConfig().getString("server.name", "season_1_end");
  }

  private String seasonId() {
    return getConfig().getString("season.id", "s1");
  }

  private boolean seasonDebugModeEnabled() {
    return getConfig().getBoolean("season.debug_mode", false);
  }

  private String activeSeasonProfile() {
    return getConfig().getString("season.active_profile", "ender_shadow_s1");
  }

  private String vaultDepositServer() {
    return getConfig().getString("vault.deposit_server", "season_1_end");
  }

  private String vaultEnabledState() {
    return getConfig().getString("vault.enabled_in_state", "FREE_PLAY");
  }

  private List<String> vaultWithdrawServers() {
    List<String> raw = getConfig().getStringList("vault.withdraw_servers");
    if (raw == null || raw.isEmpty()) {
      return List.of("lobby", "personal_yg");
    }
    return raw;
  }

  private long vaultLockStaleSeconds() {
    return Math.max(1L, getConfig().getLong("vault.lock_stale_seconds", 45L));
  }

  private boolean vaultRequireDbLock() {
    return getConfig().getBoolean("vault.require_db_lock", true);
  }

  private boolean internalVaultEnabled() {
    return getConfig().getBoolean("vault.internal_enabled", false);
  }

  private boolean databaseEnabled() {
    return getConfig().getBoolean("database.enabled", false);
  }

  private String databaseJdbcUrl() {
    return getConfig().getString("database.jdbc_url", "");
  }

  private String databaseUsername() {
    return getConfig().getString("database.username", "");
  }

  private String databasePassword() {
    return getConfig().getString("database.password", "");
  }

  private int databaseConnectTimeoutSeconds() {
    return Math.max(1, getConfig().getInt("database.connect_timeout_seconds", 5));
  }

  private boolean borderEnabled() {
    return getConfig().getBoolean("border.enabled", true);
  }

  private World borderWorld() {
    String configured = getConfig().getString("border.world_name", "world");
    if (configured != null && !configured.isBlank()) {
      World named = Bukkit.getWorld(configured);
      if (named != null) {
        return named;
      }
    }
    for (World world : Bukkit.getWorlds()) {
      if (world.getEnvironment() == World.Environment.NORMAL) {
        return world;
      }
    }
    return null;
  }

  private double borderCenterX() {
    return getConfig().getDouble("border.center.x", 0.0D);
  }

  private double borderCenterZ() {
    return getConfig().getDouble("border.center.z", 0.0D);
  }

  private double borderStartRadius() {
    return Math.max(1.0D, getConfig().getDouble("border.radius.start", 6000.0D));
  }

  private double borderEndRadius() {
    double start = borderStartRadius();
    double end = getConfig().getDouble("border.radius.end", 300.0D);
    return Math.max(1.0D, Math.min(start, end));
  }

  private long borderShrinkDurationSeconds() {
    return Math.max(1L, getConfig().getLong("border.shrink_seconds", 86400L));
  }

  private int borderWitherStepSeconds() {
    return Math.max(1, getConfig().getInt("border.wither.step_seconds", 30));
  }

  private int borderWitherMaxLevel() {
    return Math.max(1, getConfig().getInt("border.wither.max_level", 20));
  }

  private double borderWitherExtraDamagePerLevelPerSecond() {
    return Math.max(0.0D, getConfig().getDouble("border.wither.extra_damage_per_level_per_second", 2.0D));
  }

  private boolean borderParticlesEnabled() {
    return getConfig().getBoolean("border.particles.enabled", true);
  }

  private long borderParticleIntervalTicks() {
    return Math.max(1L, getConfig().getLong("border.particles.interval_ticks", 6L));
  }

  private double borderParticleRenderDistance() {
    return Math.max(16.0D, getConfig().getDouble("border.particles.render_distance", 224.0D));
  }

  private double borderParticleOutsideRenderDistance() {
    double inside = borderParticleRenderDistance();
    return Math.max(inside, getConfig().getDouble("border.particles.outside_render_distance", inside));
  }

  private double borderParticleArcDegrees() {
    double value = getConfig().getDouble("border.particles.arc_degrees", 120.0D);
    return Math.max(10.0D, Math.min(360.0D, value));
  }

  private double borderParticleOutsideArcDegrees() {
    double inside = borderParticleArcDegrees();
    double configured = getConfig().getDouble("border.particles.outside_arc_degrees", inside);
    return Math.max(10.0D, Math.min(360.0D, configured));
  }

  private double borderParticleAngleStepDegrees() {
    return Math.max(1.0D, getConfig().getDouble("border.particles.angle_step_degrees", 2.2D));
  }

  private double borderParticleWallHeight() {
    return Math.max(2.0D, getConfig().getDouble("border.particles.wall_height", 24.0D));
  }

  private double borderParticleVerticalStep() {
    return Math.max(0.5D, getConfig().getDouble("border.particles.vertical_step", 0.85D));
  }

  private int borderParticleCountPerPoint() {
    return Math.max(1, getConfig().getInt("border.particles.count_per_point", 4));
  }

  private boolean borderParticleOutsideIgnoreFacing() {
    return getConfig().getBoolean("border.particles.outside_ignore_facing", false);
  }

  private boolean borderParticleForce() {
    return getConfig().getBoolean("border.particles.force", false);
  }

  private boolean borderParticlePrimaryDustEnabled() {
    return getConfig().getBoolean("border.particles.primary_dust.enabled", false);
  }

  private int borderParticlePrimaryRed() {
    int value = getConfig().getInt("border.particles.primary_dust.red", 126);
    return Math.max(0, Math.min(255, value));
  }

  private int borderParticlePrimaryGreen() {
    int value = getConfig().getInt("border.particles.primary_dust.green", 82);
    return Math.max(0, Math.min(255, value));
  }

  private int borderParticlePrimaryBlue() {
    int value = getConfig().getInt("border.particles.primary_dust.blue", 255);
    return Math.max(0, Math.min(255, value));
  }

  private double borderParticlePrimarySize() {
    return Math.max(0.4D, getConfig().getDouble("border.particles.primary_dust.size", 1.35D));
  }

  private boolean borderParticleAccentEnabled() {
    return getConfig().getBoolean("border.particles.accent.enabled", false);
  }

  private int borderParticleAccentCountPerPoint() {
    return Math.max(1, getConfig().getInt("border.particles.accent.count_per_point", 1));
  }

  private int borderParticleAccentAngleStride() {
    return Math.max(1, getConfig().getInt("border.particles.accent.angle_stride", 2));
  }

  private int borderParticleAccentVerticalStride() {
    return Math.max(1, getConfig().getInt("border.particles.accent.vertical_stride", 2));
  }

  private double borderCurrentRadius() {
    double start = borderStartRadius();
    double end = borderEndRadius();
    long shrinkDuration = borderShrinkDurationSeconds();
    long started = Math.max(1L, borderShrinkStartedEpochSecond);
    long elapsed = Math.max(0L, nowEpochSecond() - started);

    if (shrinkDuration <= 0L || elapsed >= shrinkDuration) {
      return end;
    }

    double progress = elapsed / (double) shrinkDuration;
    return start + ((end - start) * progress);
  }

  private enum AbilityToken {
    NONE,
    MOBILITY,
    DEFENSE,
    OFFENSE,
    UTILITY,
    BUILD
  }

  private enum AbilityMode {
    NORMAL,
    SNEAK,
    SPRINT
  }

  private enum GimmickTag {
    SHIELD_CORE,
    REWIND,
    TELEPORT,
    DECOY,
    TREECAP,
    BLOCK_TRANSMUTE,
    PROJECTILE_CONTROL,
    HOTBAR_MANIPULATION,
    SLOT_LOCK,
    TOOL_BAN,
    BUILD_DECAY,
    POTION_MUTATION,
    NIGHTMARE,
    STALKER_PRESSURE,
    AURA_SHIFT,
    BORDER_PRESSURE,
    LOOT_CONTROL,
    AOE_COMBAT,
    SCORE_TRADE,
    ELEMENT_SHIFT,
    HUNGER_EXCHANGE,
    SCANNER,
    TRADEOFF_DEFENSE,
    CRAFT_GAMBLE,
    GENERIC_RUNTIME
  }

  private record Effect80Id(char group, int index) {}

  private record NumericPoolEffectId(char group, int index) {}

  private record EffectGimmickProfile(
      Effect80Id effect80Id,
      EffectKind kind,
      EnumSet<GimmickTag> tags,
      boolean hasActiveTrigger,
      AbilityToken token,
      AbilityMode mode,
      int baseCooldownSeconds
  ) {}

  private record PlayerRewindSnapshot(
      Location location,
      double health,
      int foodLevel,
      float saturation,
      int fireTicks,
      float fallDistance,
      long epochSecond
  ) {}

  private record StoredProjectile(EntityType entityType, Vector velocity) {}

  private record ScoreInvestmentLedgerEntry(
      long principal,
      int tier,
      long maturityEpochSecond
  ) {}

  private enum EffectKind {
    BLESSING,
    CURSE
  }

  private enum EffectArchetype {
    MOBILITY,
    COMBAT,
    DEFENSE,
    GATHERING,
    SCORE,
    SURVIVAL,
    UTILITY,
    ENDGAME
  }

  private enum TierScalingMode {
    ARCHETYPE_TOTAL,
    CARD_TIER
  }

  private enum RuntimeWorldScope {
    ANY,
    OVERWORLD,
    NETHER,
    END
  }

  private enum RuntimeCondition {
    SNEAKING,
    SPRINTING,
    BLOCKING,
    ON_ICE,
    IN_COLD_BIOME,
    IN_HOT_BIOME,
    IN_CAVE_BIOME,
    OUTSIDE_BORDER,
    INSIDE_BORDER,
    LOW_HEALTH_30,
    LOW_HEALTH_35,
    HAS_POSITIVE_EFFECT,
    MAIN_HAND_EMPTY,
    MAIN_HAND_AXE,
    MAIN_HAND_TRIDENT_LIKE,
    MAIN_HAND_ENDER_PEARL,
    NEAR_STRONGHOLD_500
  }

  private enum RuntimeModifierType {
    DAMAGE_DEALT_RATIO,
    DAMAGE_TAKEN_RATIO,
    FALL_DAMAGE_RATIO,
    WALK_SPEED_RATIO,
    ATTACK_SPEED_RATIO,
    STEP_HEIGHT_RATIO,
    SNEAK_SPEED_RATIO,
    BLOCK_BREAK_SPEED_RATIO,
    WATER_MOVEMENT_RATIO,
    MAX_HEALTH_RATIO,
    MAX_ABSORPTION_RATIO,
    SAFE_FALL_DISTANCE_RATIO,
    BURNING_TIME_RATIO,
    KNOCKBACK_TAKEN_RATIO,
    SHIELD_BLOCK_RATIO,
    OXYGEN_DRAIN_RATIO,
    AURA_MONSTER_POWER_RATIO,
    STALKER_SPAWN_INTERVAL_RATIO,
    STALKER_DAMAGE_TAKEN_RATIO,
    BORDER_WITHER_RATIO,
    DARKNESS_DURATION_RATIO,
    BLINDNESS_DURATION_RATIO,
    GLOWING_DURATION_RATIO,
    WEAKNESS_DURATION_RATIO,
    WITHER_DURATION_RATIO,
    POISON_DURATION_RATIO,
    PROJECTILE_DAMAGE_TAKEN_RATIO,
    EXPLOSION_DAMAGE_TAKEN_RATIO,
    FIRE_DAMAGE_TAKEN_RATIO,
    WITHER_DAMAGE_TAKEN_RATIO,
    END_DAMAGE_TAKEN_RATIO,
    LOW_HEALTH_DAMAGE_DEALT_RATIO,
    LOW_HEALTH_DAMAGE_TAKEN_RATIO,
    PVP_DAMAGE_DEALT_RATIO,
    PVP_DAMAGE_TAKEN_RATIO,
    MOB_DAMAGE_DEALT_RATIO,
    NATURAL_REGEN_RATIO,
    HUNGER_DRAIN_RATIO,
    FOOD_GAIN_RATIO,
    ITEM_DURABILITY_LOSS_RATIO,
    STARVATION_DAMAGE_RATIO,
    SCORE_DECAY_PER_MINUTE_RATIO,
    MINING_SCORE_RATIO,
    MOB_SCORE_RATIO,
    SURVIVAL_SCORE_RATIO,
    PVP_STEAL_GAIN_RATIO,
    PVP_STEAL_TAKEN_RATIO,
    RESPAWN_COST_RATIO,
    DRAGON_DAMAGE_RATIO,
    DRAGON_JACKPOT_RATIO
  }

  private record EffectDefinition(
      String id,
      String displayName,
      EffectKind kind,
      double weight,
      EffectArchetype archetype,
      boolean severeCurse,
      long severeBonusPoints,
      EffectRuntimeProfile runtimeProfile
  ) {}

  private record RuntimeModifierRule(
      RuntimeModifierType type,
      double valuePerTier,
      int minTotalTier,
      TierScalingMode scalingMode,
      RuntimeWorldScope worldScope,
      Set<RuntimeCondition> conditions,
      String labelKo
  ) {}

  private record RuntimeArchetypeTotals(
      EnumMap<EffectArchetype, Integer> blessingTotals,
      EnumMap<EffectArchetype, Integer> curseTotals
  ) {}

  private record EffectLoreSections(
      String stat,
      String special,
      String t2,
      String t4,
      String t6
  ) {}

  private record EffectRuntimeProfile(
      List<RuntimeModifierRule> modifierRules,
      Double scoreMultiplierPerTier,
      List<String> detailKo
  ) {}

  private record RollOutcome(
      int expiredCount,
      int addedCount,
      int upgradedCount,
      int overflowCount,
      long severeBonus,
      String summary
  ) {
    private boolean isEmpty() {
      return expiredCount <= 0 && addedCount <= 0 && upgradedCount <= 0 && overflowCount <= 0 && severeBonus <= 0L;
    }
  }

  private static final class RuntimeModifierSummaryBucket {
    private final RuntimeModifierType type;
    private final String labelKo;
    private final TierScalingMode scalingMode;
    private final RuntimeWorldScope worldScope;
    private final Set<RuntimeCondition> conditions;
    private final int sourceTier;
    private int minRequiredTier;
    private double valueSum;
    private boolean hasActive;

    private RuntimeModifierSummaryBucket(RuntimeModifierRule seedRule, int sourceTier) {
      RuntimeModifierRule safeRule = Objects.requireNonNull(seedRule, "seedRule");
      this.type = safeRule.type();
      this.labelKo = safeRule.labelKo();
      this.scalingMode = safeRule.scalingMode();
      this.worldScope = safeRule.worldScope();
      this.conditions = safeRule.conditions() == null
          ? Set.of()
          : Collections.unmodifiableSet(new LinkedHashSet<>(safeRule.conditions()));
      this.sourceTier = Math.max(0, sourceTier);
      this.minRequiredTier = Math.max(1, safeRule.minTotalTier());
      this.valueSum = 0.0D;
      this.hasActive = false;
    }

    private void accept(RuntimeModifierRule rule, int sourceTier) {
      if (rule == null) {
        return;
      }
      this.minRequiredTier = Math.min(this.minRequiredTier, Math.max(1, rule.minTotalTier()));
      Double value = sourceTier < rule.minTotalTier()
          ? null
          : rule.valuePerTier() * sourceTier;
      if (value != null) {
        this.valueSum += value;
        this.hasActive = true;
      }
    }

    private RuntimeModifierType type() {
      return type;
    }

    private String labelKo() {
      return labelKo;
    }

    private TierScalingMode scalingMode() {
      return scalingMode;
    }

    private RuntimeWorldScope worldScope() {
      return worldScope;
    }

    private Set<RuntimeCondition> conditions() {
      return conditions;
    }

    private int sourceTier() {
      return sourceTier;
    }

    private int minRequiredTier() {
      return minRequiredTier;
    }

    private double valueSum() {
      return valueSum;
    }

    private boolean hasActive() {
      return hasActive;
    }
  }

  private static final class EnumMapCounter {
    private final int[] values = new int[EffectArchetype.values().length];

    private void add(EffectArchetype archetype, int value) {
      if (archetype == null || value == 0) {
        return;
      }
      int index = archetype.ordinal();
      values[index] = Math.max(0, values[index] + value);
    }

    private int get(EffectArchetype archetype) {
      if (archetype == null) {
        return 0;
      }
      return Math.max(0, values[archetype.ordinal()]);
    }
  }
}
