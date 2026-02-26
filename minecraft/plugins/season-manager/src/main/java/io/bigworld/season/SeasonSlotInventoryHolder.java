package io.bigworld.season;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class SeasonSlotInventoryHolder implements InventoryHolder {
  private final UUID targetUuid;
  private final String targetName;
  private final int page;
  private final int residualPage;
  private final int residualFilterMode;
  private Inventory inventory;

  public SeasonSlotInventoryHolder(UUID targetUuid, String targetName) {
    this(targetUuid, targetName, 1, 0, 0);
  }

  public SeasonSlotInventoryHolder(UUID targetUuid, String targetName, int page) {
    this(targetUuid, targetName, page, 0, 0);
  }

  public SeasonSlotInventoryHolder(UUID targetUuid, String targetName, int page, int residualPage, int residualFilterMode) {
    this.targetUuid = targetUuid;
    this.targetName = targetName == null ? "" : targetName;
    this.page = Math.max(1, page);
    this.residualPage = Math.max(0, residualPage);
    this.residualFilterMode = Math.max(0, residualFilterMode);
  }

  public UUID targetUuid() {
    return targetUuid;
  }

  public String targetName() {
    return targetName;
  }

  public int page() {
    return page;
  }

  public int residualPage() {
    return residualPage;
  }

  public int residualFilterMode() {
    return residualFilterMode;
  }

  public void bindInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }
}
