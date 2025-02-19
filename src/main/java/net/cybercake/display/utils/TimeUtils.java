package net.cybercake.display.utils;

public class TimeUtils {

    public static String getFormattedDuration(long duration) {
        long seconds = duration % 60;
        long minutes = (duration / 60) % 60;
        long hours = (duration / 60 * 60) % 24;
        return (hours > 0 ? hours + " hour" + (hours == 1 ? "" : "s") : "")
                + (minutes > 0 ? minutes + " minute" + (minutes == 1 ? "" : "s") : "")
                + (seconds + " second" + (seconds == 1 ? "" : "s"));
    }

}
