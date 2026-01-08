package com.electroblob.wizardry.core;

public final class EBConstants {

    // Gameplay constants
    public static final int CONDENSER_TICK_INTERVAL = 50;
    public static final int SIPHON_MANA_PER_LEVEL = 5;
    public static final int MAX_RECENT_SPELLS = 5;
    public static final int RECENT_SPELL_EXPIRY_TICKS = 1200;
    public static float COOLDOWN_REDUCTION_PER_LEVEL = 0.15f;
    public static float POTENCY_INCREASE_PER_TIER = 0.15f;
    public static float DURATION_INCREASE_PER_LEVEL = 0.25f;
    public static float RANGE_INCREASE_PER_LEVEL = 0.25f;
    public static float BLAST_RADIUS_INCREASE_PER_LEVEL = 0.25f;

    private EBConstants() {
    }
}
