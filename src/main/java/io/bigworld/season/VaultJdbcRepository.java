package io.bigworld.season;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class VaultJdbcRepository {
  private final JavaPlugin plugin;
  private final String jdbcUrl;
  private final String username;
  private final String password;
  private final int loginTimeoutSeconds;

  private boolean ready;

  public VaultJdbcRepository(JavaPlugin plugin, String jdbcUrl, String username, String password, int loginTimeoutSeconds) {
    this.plugin = plugin;
    this.jdbcUrl = jdbcUrl;
    this.username = username;
    this.password = password;
    this.loginTimeoutSeconds = Math.max(1, loginTimeoutSeconds);
    this.ready = false;
  }

  public boolean initialize() {
    if (jdbcUrl == null || jdbcUrl.isBlank()) {
      plugin.getLogger().warning("Vault DB is disabled: database.jdbc_url is empty.");
      return false;
    }

    try {
      DriverManager.setLoginTimeout(loginTimeoutSeconds);
      try (Connection connection = openConnection()) {
        ensureSchema(connection);
      }
      ready = true;
      plugin.getLogger().info("Vault DB initialized: " + jdbcUrl);
      return true;
    } catch (SQLException exception) {
      ready = false;
      plugin.getLogger().log(Level.SEVERE, "Failed to initialize vault DB", exception);
      return false;
    }
  }

  public boolean isReady() {
    return ready;
  }

  public boolean tryAcquireLock(String ownerUuid, String serverName, long staleSeconds) throws SQLException {
    long now = Instant.now().getEpochSecond();
    long staleBefore = now - Math.max(1L, staleSeconds);

    try (Connection connection = openConnection()) {
      connection.setAutoCommit(false);
      try {
        try (PreparedStatement deleteStale = connection.prepareStatement(
            "DELETE FROM vault_lock WHERE uuid = ? AND locked_at < ?")) {
          deleteStale.setString(1, ownerUuid);
          deleteStale.setLong(2, staleBefore);
          deleteStale.executeUpdate();
        }

        boolean inserted = false;
        try (PreparedStatement insert = connection.prepareStatement(
            "INSERT INTO vault_lock (uuid, locked_by_server, locked_at) VALUES (?, ?, ?)")) {
          insert.setString(1, ownerUuid);
          insert.setString(2, serverName);
          insert.setLong(3, now);
          inserted = insert.executeUpdate() > 0;
        } catch (SQLException ignored) {
          // Duplicate key can happen when lock already exists.
        }

        if (!inserted) {
          String lockOwner = null;
          try (PreparedStatement read = connection.prepareStatement(
              "SELECT locked_by_server FROM vault_lock WHERE uuid = ?")) {
            read.setString(1, ownerUuid);
            try (ResultSet rs = read.executeQuery()) {
              if (rs.next()) {
                lockOwner = rs.getString("locked_by_server");
              }
            }
          }

          if (lockOwner == null) {
            try (PreparedStatement retryInsert = connection.prepareStatement(
                "INSERT INTO vault_lock (uuid, locked_by_server, locked_at) VALUES (?, ?, ?)")) {
              retryInsert.setString(1, ownerUuid);
              retryInsert.setString(2, serverName);
              retryInsert.setLong(3, now);
              inserted = retryInsert.executeUpdate() > 0;
            }
            if (!inserted) {
              connection.rollback();
              return false;
            }
          } else if (serverName.equals(lockOwner)) {
            try (PreparedStatement updateOwnLock = connection.prepareStatement(
                "UPDATE vault_lock SET locked_at = ? WHERE uuid = ? AND locked_by_server = ?")) {
              updateOwnLock.setLong(1, now);
              updateOwnLock.setString(2, ownerUuid);
              updateOwnLock.setString(3, serverName);
              updateOwnLock.executeUpdate();
            }
          } else {
            connection.rollback();
            return false;
          }
        }

        connection.commit();
        return true;
      } catch (SQLException exception) {
        connection.rollback();
        throw exception;
      }
    }
  }

  public void releaseLock(String ownerUuid, String serverName) {
    try (Connection connection = openConnection();
         PreparedStatement delete = connection.prepareStatement(
             "DELETE FROM vault_lock WHERE uuid = ? AND locked_by_server = ?")) {
      delete.setString(1, ownerUuid);
      delete.setString(2, serverName);
      delete.executeUpdate();
    } catch (SQLException exception) {
      plugin.getLogger().log(Level.WARNING, "Failed to release vault lock for " + ownerUuid, exception);
    }
  }

  public void forceReleaseLock(String ownerUuid) {
    try (Connection connection = openConnection();
         PreparedStatement delete = connection.prepareStatement(
             "DELETE FROM vault_lock WHERE uuid = ?")) {
      delete.setString(1, ownerUuid);
      delete.executeUpdate();
    } catch (SQLException exception) {
      plugin.getLogger().log(Level.WARNING, "Failed to force-release vault lock for " + ownerUuid, exception);
    }
  }

  public ItemStack[] loadVaultItems(String ownerUuid, int maxSlots) throws SQLException {
    ItemStack[] contents = new ItemStack[maxSlots];

    try (Connection connection = openConnection();
         PreparedStatement read = connection.prepareStatement(
             "SELECT slot, item_blob FROM vault_items WHERE uuid = ?")) {
      read.setString(1, ownerUuid);
      try (ResultSet rs = read.executeQuery()) {
        while (rs.next()) {
          int slot = rs.getInt("slot");
          if (slot < 0 || slot >= maxSlots) {
            continue;
          }

          String encoded = rs.getString("item_blob");
          if (encoded == null || encoded.isBlank()) {
            continue;
          }

          try {
            contents[slot] = ItemStackCodec.decode(encoded);
          } catch (IOException | ClassNotFoundException decodeError) {
            plugin.getLogger().log(Level.WARNING,
                "Failed to decode vault item owner=" + ownerUuid + " slot=" + slot, decodeError);
          }
        }
      }
    }

    return contents;
  }

  public void saveVaultItems(String ownerUuid, ItemStack[] contents, int maxSlots) throws SQLException {
    long now = Instant.now().getEpochSecond();

    try (Connection connection = openConnection()) {
      connection.setAutoCommit(false);
      try {
        try (PreparedStatement deleteAll = connection.prepareStatement(
            "DELETE FROM vault_items WHERE uuid = ?")) {
          deleteAll.setString(1, ownerUuid);
          deleteAll.executeUpdate();
        }

        try (PreparedStatement insert = connection.prepareStatement(
            "INSERT INTO vault_items (uuid, slot, item_blob, updated_at) VALUES (?, ?, ?, ?)")) {
          int bound = Math.min(maxSlots, contents.length);
          for (int slot = 0; slot < bound; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType() == Material.AIR) {
              continue;
            }

            String encoded = ItemStackCodec.encode(item);
            insert.setString(1, ownerUuid);
            insert.setInt(2, slot);
            insert.setString(3, encoded);
            insert.setLong(4, now);
            insert.addBatch();
          }
          insert.executeBatch();
        }

        connection.commit();
      } catch (Exception exception) {
        connection.rollback();
        if (exception instanceof SQLException sqlException) {
          throw sqlException;
        }
        throw new SQLException("Failed to save vault items for " + ownerUuid, exception);
      }
    }
  }

  public void releaseAllLocksByServer(String serverName) {
    try (Connection connection = openConnection();
         PreparedStatement delete = connection.prepareStatement(
             "DELETE FROM vault_lock WHERE locked_by_server = ?")) {
      delete.setString(1, serverName);
      delete.executeUpdate();
    } catch (SQLException exception) {
      plugin.getLogger().log(Level.WARNING, "Failed to release all vault locks for server " + serverName, exception);
    }
  }

  private Connection openConnection() throws SQLException {
    if (username == null || username.isBlank()) {
      return DriverManager.getConnection(jdbcUrl);
    }
    return DriverManager.getConnection(jdbcUrl, username, password);
  }

  private void ensureSchema(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(
          "CREATE TABLE IF NOT EXISTS vault_items ("
              + "uuid VARCHAR(36) NOT NULL,"
              + "slot INT NOT NULL,"
              + "item_blob TEXT NOT NULL,"
              + "updated_at BIGINT NOT NULL,"
              + "PRIMARY KEY (uuid, slot)"
              + ")");

      statement.executeUpdate(
          "CREATE TABLE IF NOT EXISTS vault_lock ("
              + "uuid VARCHAR(36) PRIMARY KEY,"
              + "locked_by_server VARCHAR(64) NOT NULL,"
              + "locked_at BIGINT NOT NULL"
              + ")");
    }
  }
}
