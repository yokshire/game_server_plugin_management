package io.bigworld.season;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

  public void replaceRoundLeaderboard(
      String seasonId,
      String serverName,
      long roundId,
      List<RoundLeaderboardEntry> entries
  ) throws SQLException {
    try (Connection connection = openConnection()) {
      connection.setAutoCommit(false);
      try {
        try (PreparedStatement delete = connection.prepareStatement(
            "DELETE FROM season_round_leaderboard WHERE season_id = ? AND server_name = ? AND round_id = ?")) {
          delete.setString(1, seasonId);
          delete.setString(2, serverName);
          delete.setLong(3, roundId);
          delete.executeUpdate();
        }

        if (entries != null && !entries.isEmpty()) {
          try (PreparedStatement insert = connection.prepareStatement(
              "INSERT INTO season_round_leaderboard ("
                  + "season_id, server_name, round_id, rank_no, player_uuid, player_name, "
                  + "snapshot_score, weighted_score, escaped_within_window, winner_bonus_granted, saved_at"
                  + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            for (RoundLeaderboardEntry entry : entries) {
              if (entry == null) {
                continue;
              }
              insert.setString(1, seasonId);
              insert.setString(2, serverName);
              insert.setLong(3, roundId);
              insert.setInt(4, Math.max(1, entry.rankNo()));
              insert.setString(5, entry.playerUuid() == null ? "" : entry.playerUuid());
              insert.setString(6, entry.playerName() == null ? "" : entry.playerName());
              insert.setLong(7, Math.max(0L, entry.snapshotScore()));
              insert.setDouble(8, entry.weightedScore());
              insert.setBoolean(9, entry.escapedWithinWindow());
              insert.setBoolean(10, entry.winnerBonusGranted());
              insert.setLong(11, Math.max(0L, entry.savedAtEpochSecond()));
              insert.addBatch();
            }
            insert.executeBatch();
          }
        }
        connection.commit();
      } catch (SQLException exception) {
        connection.rollback();
        throw exception;
      }
    }
  }

  public List<RoundLeaderboardEntry> loadRoundLeaderboard(
      String seasonId,
      String serverName,
      long roundId,
      int limit
  ) throws SQLException {
    List<RoundLeaderboardEntry> out = new ArrayList<>();
    int resolvedLimit = Math.max(1, limit);
    try (Connection connection = openConnection();
         PreparedStatement read = connection.prepareStatement(
             "SELECT rank_no, player_uuid, player_name, snapshot_score, weighted_score, "
                 + "escaped_within_window, winner_bonus_granted, saved_at "
                 + "FROM season_round_leaderboard "
                 + "WHERE season_id = ? AND server_name = ? AND round_id = ? "
                 + "ORDER BY rank_no ASC LIMIT ?")) {
      read.setString(1, seasonId);
      read.setString(2, serverName);
      read.setLong(3, roundId);
      read.setInt(4, resolvedLimit);
      try (ResultSet rs = read.executeQuery()) {
        while (rs.next()) {
          out.add(new RoundLeaderboardEntry(
              Math.max(1, rs.getInt("rank_no")),
              rs.getString("player_uuid"),
              rs.getString("player_name"),
              Math.max(0L, rs.getLong("snapshot_score")),
              rs.getDouble("weighted_score"),
              rs.getBoolean("escaped_within_window"),
              rs.getBoolean("winner_bonus_granted"),
              Math.max(0L, rs.getLong("saved_at"))
          ));
        }
      }
    }
    return out;
  }

  public Long findLatestLeaderboardRoundId(String seasonId, String serverName) throws SQLException {
    try (Connection connection = openConnection();
         PreparedStatement read = connection.prepareStatement(
             "SELECT MAX(round_id) AS latest_round_id FROM season_round_leaderboard "
                 + "WHERE season_id = ? AND server_name = ?")) {
      read.setString(1, seasonId);
      read.setString(2, serverName);
      try (ResultSet rs = read.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        long value = rs.getLong("latest_round_id");
        return rs.wasNull() ? null : value;
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
      statement.executeUpdate(
          "CREATE TABLE IF NOT EXISTS season_round_leaderboard ("
              + "season_id VARCHAR(64) NOT NULL,"
              + "server_name VARCHAR(64) NOT NULL,"
              + "round_id BIGINT NOT NULL,"
              + "rank_no INT NOT NULL,"
              + "player_uuid VARCHAR(36) NOT NULL,"
              + "player_name VARCHAR(64) NOT NULL,"
              + "snapshot_score BIGINT NOT NULL,"
              + "weighted_score DOUBLE NOT NULL,"
              + "escaped_within_window BOOLEAN NOT NULL,"
              + "winner_bonus_granted BOOLEAN NOT NULL,"
              + "saved_at BIGINT NOT NULL,"
              + "PRIMARY KEY (season_id, server_name, round_id, rank_no)"
              + ")"
      );
    }
  }

  public record RoundLeaderboardEntry(
      int rankNo,
      String playerUuid,
      String playerName,
      long snapshotScore,
      double weightedScore,
      boolean escapedWithinWindow,
      boolean winnerBonusGranted,
      long savedAtEpochSecond
  ) {
  }
}
