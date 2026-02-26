package io.bigworld.season;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class VaultInventoryHolder implements InventoryHolder {
  private final UUID owner;
  private final VaultMode mode;
  private Inventory inventory;

  public VaultInventoryHolder(UUID owner, VaultMode mode) {
    this.owner = owner;
    this.mode = mode;
  }

  public UUID owner() {
    return owner;
  }

  public VaultMode mode() {
    return mode;
  }

  public void bindInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }
}
