package net.superkat.bonzibuddy.minigame.api;

import java.util.Locale;

public enum BonziMinigameType {
    ABSTRACT,
    CATASTROPHIC_CLONES;
    private static final BonziMinigameType[] VALUES = values();
    public static BonziMinigameType fromName(String name) {
        for (BonziMinigameType type : VALUES) {
            if(name.equalsIgnoreCase(type.name())) {
                return type;
            }
        }

        return ABSTRACT;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
