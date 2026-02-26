package io.bigworld.season;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeasonStateJdbcRepository {
  private final JavaPlugin plugin;
  private final String jdbcUrl;
  private final String username;
  private final String password;
  private final int loginTimeoutSeconds;

  private boolean ready;

  public SeasonStateJdbcRepository(
      JavaPlugin plugin,
      String jdbcUrl,
      String username,
      String password,
      int loginTimeoutSeconds
  ) {
    this.plugin = plugin;
    this.jdbcUrl = jdbcUrl;
    this.username = username;
    this.password = password;
    this.loginTimeoutSeconds = Math.max(1, loginTimeoutSeconds);
    this.ready = false;
  }

  public boolean initialize() {
    if (jdbcUrl == null || jdbcUrl.isBlank()) {
      plugin.getLogger().warning("Season DB state persistence disabled: database.jdbc_url is empty.");
      ready = false;
      return false;
    }

    try {
      DriverManager.setLoginTimeout(loginTimeoutSeconds);
      try (Connection connection = openConnection()) {
        ensureSchema(connection);
      }
      ready = true;
      plugin.getLogger().info("Season state DB initialized.");
      return true;
    } catch (SQLException exception) {
      ready = false;
      plugin.getLogger().log(Level.SEVERE, "Failed to initialize season state DB", exception);
      return false;
    }
  }

  public boolean isReady() {
    return ready;
  }

  public String loadStateBlob(String seasonId, String serverName) throws SQLException {
    try (Connection connection = openConnection();
         PreparedStatement read = connection.prepareStatement(
             "SELECT state_blob FROM season_state_blob WHERE season_id = ? AND server_name = ?")) {
      read.setString(1, seasonId);
      read.setString(2, serverName);
      try (ResultSet rs = read.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        String value = rs.getString("state_blob");
        return value == null || value.isBlank() ? null : value;
      }
    }
  }

  public void saveStateBlob(String seasonId, String serverName, String stateBlob) throws SQLException {
    try (Connection connection = openConnection()) {
      connection.setAutoCommit(false);
      try {
        int updated;
        try (PreparedStatement update = connection.prepareStatement(
            "UPDATE season_state_blob SET state_blob = ?, updated_at = ? "
                + "WHERE season_id = ? AND server_name = ?")) {
          update.setString(1, stateBlob);
          update.setLong(2, System.currentTimeMillis() / 1000L);
          update.setString(3, seasonId);
          update.setString(4, serverName);
          updated = update.executeUpdate();
        }

        if (updated <= 0) {
          try (PreparedStatement insert = connection.prepareStatement(
              "INSERT INTO season_state_blob (season_id, server_name, state_blob, updated_at) VALUES (?, ?, ?, ?)")) {
            insert.setString(1, seasonId);
            insert.setString(2, serverName);
            insert.setString(3, stateBlob);
            insert.setLong(4, System.currentTimeMillis() / 1000L);
            insert.executeUpdate();
          }
        }

        connection.commit();
      } catch (SQLException exception) {
        connection.rollback();
        throw exception;
      }
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
          "CREATE TABLE IF NOT EXISTS season_state_blob ("
              + "season_id VARCHAR(64) NOT NULL,"
              + "server_name VARCHAR(64) NOT NULL,"
              + "state_blob LONGTEXT NOT NULL,"
              + "updated_at BIGINT NOT NULL,"
              + "PRIMARY KEY (season_id, server_name)"
              + ")"
      );
    }
  }
}
