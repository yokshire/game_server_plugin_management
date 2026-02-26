package io.bigworld.season;

import java.util.UUID;

public record VaultSession(UUID owner, int maxSlots, VaultMode mode) {}
