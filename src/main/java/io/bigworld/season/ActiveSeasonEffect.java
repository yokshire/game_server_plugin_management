package io.bigworld.season;

public final class ActiveSeasonEffect {
  private final String id;
  private String displayName;
  private int tier;
  private long startIngameDay;
  private long expireIngameDay;
  private boolean severeCurse;
  private long severeBonusPoints;

  public ActiveSeasonEffect(
      String id,
      String displayName,
      int tier,
      long startIngameDay,
      long expireIngameDay,
      boolean severeCurse,
      long severeBonusPoints
  ) {
    this.id = id == null ? "" : id.trim().toUpperCase();
    this.displayName = displayName == null || displayName.isBlank() ? this.id : displayName.trim();
    this.tier = Math.max(1, tier);
    this.startIngameDay = Math.max(0L, startIngameDay);
    this.expireIngameDay = Math.max(this.startIngameDay + 1L, expireIngameDay);
    this.severeCurse = severeCurse;
    this.severeBonusPoints = Math.max(0L, severeBonusPoints);
  }

  public String getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    if (displayName == null || displayName.isBlank()) {
      return;
    }
    this.displayName = displayName.trim();
  }

  public int getTier() {
    return tier;
  }

  public void setTier(int tier) {
    this.tier = Math.max(1, tier);
  }

  public void increaseTier(int maxTier) {
    int upper = Math.max(1, maxTier);
    if (tier < upper) {
      tier++;
    }
  }

  public long getStartIngameDay() {
    return startIngameDay;
  }

  public void setStartIngameDay(long startIngameDay) {
    this.startIngameDay = Math.max(0L, startIngameDay);
    if (this.expireIngameDay <= this.startIngameDay) {
      this.expireIngameDay = this.startIngameDay + 1L;
    }
  }

  public long getExpireIngameDay() {
    return expireIngameDay;
  }

  public void setExpireIngameDay(long expireIngameDay) {
    this.expireIngameDay = Math.max(startIngameDay + 1L, expireIngameDay);
  }

  public boolean isExpired(long currentIngameDay) {
    return currentIngameDay >= expireIngameDay;
  }

  public boolean isSevereCurse() {
    return severeCurse;
  }

  public void setSevereCurse(boolean severeCurse) {
    this.severeCurse = severeCurse;
  }

  public long getSevereBonusPoints() {
    return severeBonusPoints;
  }

  public void setSevereBonusPoints(long severeBonusPoints) {
    this.severeBonusPoints = Math.max(0L, severeBonusPoints);
  }
}
