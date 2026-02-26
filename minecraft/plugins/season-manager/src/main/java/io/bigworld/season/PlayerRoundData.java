package io.bigworld.season;

import java.util.HashMap;
import java.util.Map;

public final class PlayerRoundData {
  private long score;
  private long peakScore;
  private int livesRemaining;
  private int deathCount;
  private boolean out;
  private long newbieProtectUntilEpochSecond;
  private Long scoreSnapshot;
  private boolean escapedWithinWindow;
  private int winnerRank;
  private boolean winnerBonusGranted;
  private boolean joinSpawnAssigned;
  private int blessingSlotsUnlocked;
  private int curseSlotsUnlocked;
  private boolean initialCardRollCompleted;
  private int initialCardRerollsUsed;
  private final Map<String, Integer> blessingCardStacks;
  private final Map<String, Integer> curseCardStacks;
  private final Map<String, ActiveSeasonEffect> blessingEffects;
  private final Map<String, ActiveSeasonEffect> curseEffects;

  public PlayerRoundData(int baseLives) {
    this.score = 0L;
    this.peakScore = 0L;
    this.livesRemaining = Math.max(0, baseLives);
    this.deathCount = 0;
    this.out = false;
    this.newbieProtectUntilEpochSecond = 0L;
    this.scoreSnapshot = null;
    this.escapedWithinWindow = false;
    this.winnerRank = 0;
    this.winnerBonusGranted = false;
    this.joinSpawnAssigned = false;
    this.blessingSlotsUnlocked = 0;
    this.curseSlotsUnlocked = 0;
    this.initialCardRollCompleted = false;
    this.initialCardRerollsUsed = 0;
    this.blessingCardStacks = new HashMap<>();
    this.curseCardStacks = new HashMap<>();
    this.blessingEffects = new HashMap<>();
    this.curseEffects = new HashMap<>();
  }

  public long getScore() {
    return score;
  }

  public void setScore(long score) {
    this.score = Math.max(0L, score);
    this.peakScore = Math.max(this.peakScore, this.score);
  }

  public void addScore(long delta) {
    setScore(this.score + delta);
  }

  public long getPeakScore() {
    return peakScore;
  }

  public void setPeakScore(long peakScore) {
    this.peakScore = Math.max(0L, Math.max(peakScore, this.score));
  }

  public int getLivesRemaining() {
    return livesRemaining;
  }

  public void setLivesRemaining(int livesRemaining) {
    this.livesRemaining = Math.max(0, livesRemaining);
  }

  public int getDeathCount() {
    return deathCount;
  }

  public void setDeathCount(int deathCount) {
    this.deathCount = Math.max(0, deathCount);
  }

  public boolean isOut() {
    return out;
  }

  public void setOut(boolean out) {
    this.out = out;
  }

  public long getNewbieProtectUntilEpochSecond() {
    return newbieProtectUntilEpochSecond;
  }

  public void setNewbieProtectUntilEpochSecond(long newbieProtectUntilEpochSecond) {
    this.newbieProtectUntilEpochSecond = Math.max(0L, newbieProtectUntilEpochSecond);
  }

  public Long getScoreSnapshot() {
    return scoreSnapshot;
  }

  public void setScoreSnapshot(Long scoreSnapshot) {
    if (scoreSnapshot == null) {
      this.scoreSnapshot = null;
      return;
    }
    this.scoreSnapshot = Math.max(0L, scoreSnapshot);
  }

  public boolean hasScoreSnapshot() {
    return scoreSnapshot != null;
  }

  public boolean isEscapedWithinWindow() {
    return escapedWithinWindow;
  }

  public void setEscapedWithinWindow(boolean escapedWithinWindow) {
    this.escapedWithinWindow = escapedWithinWindow;
  }

  public int getWinnerRank() {
    return winnerRank;
  }

  public void setWinnerRank(int winnerRank) {
    this.winnerRank = Math.max(0, winnerRank);
  }

  public boolean isWinnerBonusGranted() {
    return winnerBonusGranted;
  }

  public void setWinnerBonusGranted(boolean winnerBonusGranted) {
    this.winnerBonusGranted = winnerBonusGranted;
  }

  public boolean isJoinSpawnAssigned() {
    return joinSpawnAssigned;
  }

  public void setJoinSpawnAssigned(boolean joinSpawnAssigned) {
    this.joinSpawnAssigned = joinSpawnAssigned;
  }

  public int getBlessingSlotsUnlocked() {
    return blessingSlotsUnlocked;
  }

  public void setBlessingSlotsUnlocked(int blessingSlotsUnlocked) {
    this.blessingSlotsUnlocked = Math.max(0, blessingSlotsUnlocked);
  }

  public int getCurseSlotsUnlocked() {
    return curseSlotsUnlocked;
  }

  public void setCurseSlotsUnlocked(int curseSlotsUnlocked) {
    this.curseSlotsUnlocked = Math.max(0, curseSlotsUnlocked);
  }

  public boolean isInitialCardRollCompleted() {
    return initialCardRollCompleted;
  }

  public void setInitialCardRollCompleted(boolean initialCardRollCompleted) {
    this.initialCardRollCompleted = initialCardRollCompleted;
  }

  public int getInitialCardRerollsUsed() {
    return initialCardRerollsUsed;
  }

  public void setInitialCardRerollsUsed(int initialCardRerollsUsed) {
    this.initialCardRerollsUsed = Math.max(0, initialCardRerollsUsed);
  }

  public Map<String, Integer> getBlessingCardStacks() {
    return blessingCardStacks;
  }

  public Map<String, Integer> getCurseCardStacks() {
    return curseCardStacks;
  }

  public int getTotalBlessingCardStacks() {
    int sum = 0;
    for (Integer value : blessingCardStacks.values()) {
      if (value != null && value > 0) {
        sum += value;
      }
    }
    return Math.max(0, sum);
  }

  public int getTotalCurseCardStacks() {
    int sum = 0;
    for (Integer value : curseCardStacks.values()) {
      if (value != null && value > 0) {
        sum += value;
      }
    }
    return Math.max(0, sum);
  }

  public int addBlessingCardStack(String cardId, int delta) {
    return addCardStack(blessingCardStacks, cardId, delta);
  }

  public int addCurseCardStack(String cardId, int delta) {
    return addCardStack(curseCardStacks, cardId, delta);
  }

  public Map<String, ActiveSeasonEffect> getBlessingEffects() {
    return blessingEffects;
  }

  public Map<String, ActiveSeasonEffect> getCurseEffects() {
    return curseEffects;
  }

  public int getActiveBlessingEffectCount() {
    return blessingEffects.size();
  }

  public int getActiveCurseEffectCount() {
    return curseEffects.size();
  }

  public int getTotalBlessingEffectTier() {
    int sum = 0;
    for (ActiveSeasonEffect effect : blessingEffects.values()) {
      if (effect != null) {
        sum += Math.max(0, effect.getTier());
      }
    }
    return Math.max(0, sum);
  }

  public int getTotalCurseEffectTier() {
    int sum = 0;
    for (ActiveSeasonEffect effect : curseEffects.values()) {
      if (effect != null) {
        sum += Math.max(0, effect.getTier());
      }
    }
    return Math.max(0, sum);
  }

  public ActiveSeasonEffect getBlessingEffect(String id) {
    if (id == null || id.isBlank()) {
      return null;
    }
    return blessingEffects.get(id.trim().toUpperCase());
  }

  public ActiveSeasonEffect getCurseEffect(String id) {
    if (id == null || id.isBlank()) {
      return null;
    }
    return curseEffects.get(id.trim().toUpperCase());
  }

  public void putBlessingEffect(ActiveSeasonEffect effect) {
    putEffect(blessingEffects, effect);
  }

  public void putCurseEffect(ActiveSeasonEffect effect) {
    putEffect(curseEffects, effect);
  }

  public ActiveSeasonEffect removeBlessingEffect(String id) {
    return removeEffect(blessingEffects, id);
  }

  public ActiveSeasonEffect removeCurseEffect(String id) {
    return removeEffect(curseEffects, id);
  }

  public int removeExpiredBlessingEffects(long currentIngameDay) {
    return removeExpiredEffects(blessingEffects, currentIngameDay);
  }

  public int removeExpiredCurseEffects(long currentIngameDay) {
    return removeExpiredEffects(curseEffects, currentIngameDay);
  }

  private int addCardStack(Map<String, Integer> stacks, String cardId, int delta) {
    if (stacks == null || cardId == null || cardId.isBlank() || delta == 0) {
      return 0;
    }
    String normalized = cardId.trim().toLowerCase();
    int before = Math.max(0, stacks.getOrDefault(normalized, 0));
    int after = Math.max(0, before + delta);
    if (after <= 0) {
      stacks.remove(normalized);
      return 0;
    }
    stacks.put(normalized, after);
    return after;
  }

  private void putEffect(Map<String, ActiveSeasonEffect> effects, ActiveSeasonEffect effect) {
    if (effects == null || effect == null || effect.getId() == null || effect.getId().isBlank()) {
      return;
    }
    String normalized = effect.getId().trim().toUpperCase();
    effects.put(normalized, effect);
  }

  private ActiveSeasonEffect removeEffect(Map<String, ActiveSeasonEffect> effects, String id) {
    if (effects == null || id == null || id.isBlank()) {
      return null;
    }
    return effects.remove(id.trim().toUpperCase());
  }

  private int removeExpiredEffects(Map<String, ActiveSeasonEffect> effects, long currentIngameDay) {
    if (effects == null || effects.isEmpty()) {
      return 0;
    }
    int before = effects.size();
    effects.entrySet().removeIf(entry -> {
      ActiveSeasonEffect effect = entry.getValue();
      return effect == null || effect.isExpired(currentIngameDay);
    });
    return Math.max(0, before - effects.size());
  }

  public void clearFreePlayResult() {
    this.scoreSnapshot = null;
    this.escapedWithinWindow = false;
    this.winnerRank = 0;
    this.winnerBonusGranted = false;
  }
}
