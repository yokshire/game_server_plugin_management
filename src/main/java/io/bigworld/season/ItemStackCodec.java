package io.bigworld.season;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class ItemStackCodec {
  private ItemStackCodec() {}

  public static String encode(ItemStack item) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytes)) {
      out.writeObject(item);
    }
    return Base64.getEncoder().encodeToString(bytes.toByteArray());
  }

  public static ItemStack decode(String encoded) throws IOException, ClassNotFoundException {
    byte[] bytes = Base64.getDecoder().decode(encoded);
    try (BukkitObjectInputStream in = new BukkitObjectInputStream(new ByteArrayInputStream(bytes))) {
      Object raw = in.readObject();
      if (raw instanceof ItemStack item) {
        return item;
      }
      throw new IOException("Decoded object is not an ItemStack");
    }
  }
}
