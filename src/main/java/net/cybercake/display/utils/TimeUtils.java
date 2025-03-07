package net.cybercake.display.utils;

public class TimeUtils {

    public static String getFormattedDuration(long duration) {
        long seconds = duration % 60;
        long minutes = (duration / 60) % 60;
        long hours = (duration / 60 * 60) % 24;
        return (hours > 0 ? hours + "h" : "")
                + (minutes > 0 ?  (hours > 0 ? " " : "") + minutes + "m" : "")
                + (seconds > 0 ? (minutes > 0 ? " " : "") + seconds + "s" : "");
    }

}
