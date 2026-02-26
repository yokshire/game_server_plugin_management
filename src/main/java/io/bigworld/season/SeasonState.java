package io.bigworld.season;

import java.util.Locale;
import java.util.Optional;

public enum SeasonState {
  HARDCORE,
  CLIMAX,
  FREE_PLAY,
  RESET_PENDING;

  public static Optional<SeasonState> parse(String raw) {
    if (raw == null || raw.isBlank()) {
      return Optional.empty();
    }
    try {
      return Optional.of(SeasonState.valueOf(raw.trim().toUpperCase(Locale.ROOT)));
    } catch (IllegalArgumentException ignored) {
      return Optional.empty();
    }
  }
}
