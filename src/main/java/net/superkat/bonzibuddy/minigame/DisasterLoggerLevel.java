package net.superkat.bonzibuddy.minigame;

//I have no comment other than this comment here
public enum DisasterLoggerLevel {
    THINGS_ARE_OKAY,
    SOMETHING_IS_BROKEN_BUT_IT_IS_PROBABLY_FINE,
    EVERYTHING_IS_BROKEN_THE_WORLD_IS_ENDING_THIS_IS_NOT_FINE;

    public static boolean extras(DisasterLoggerLevel level) {
        return level != THINGS_ARE_OKAY;
    }

    public static boolean full(DisasterLoggerLevel level) {
        return level == EVERYTHING_IS_BROKEN_THE_WORLD_IS_ENDING_THIS_IS_NOT_FINE;
    }
}
