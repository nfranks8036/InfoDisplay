package net.cybercake.display.utils;

import net.cybercake.display.Main;
import net.cybercake.display.boot.LoadingWindow;

import java.security.MessageDigest;

public class Log {

    public static int SHOWN_OF_CLASS_CHARACTER_LIMIT = 25;

    public static void line(Object object) {
        line(String.valueOf(object), ShowSource.NO);
    }

    @SuppressWarnings("ConstantValue")
    public static void line(String message, ShowSource showSource) {
        String displayed = null;
        boolean useOfPlaceholder = false;
        if (!message.contains("DEBUG//")) {
            Main.loading.ofLog(message);
        }
        if(showSource.enabled) {
            StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
            displayed = caller.getClassName();
            if (showSource == ShowSource.TRUNCATED) {
                displayed = "..." + displayed.substring(displayed.length()-SHOWN_OF_CLASS_CHARACTER_LIMIT);
                if(caller.getClassName().equalsIgnoreCase(Main.class.getCanonicalName()))
                    displayed = " ".repeat(SHOWN_OF_CLASS_CHARACTER_LIMIT + 5);
            }
            if (message.contains("%%class%%")) {
                message = message.replace("%%class%%", displayed);
                useOfPlaceholder = true;
            }
        }
        System.out.println((displayed != null && showSource.enabled && !useOfPlaceholder ? displayed + ": " : "") + message);
    }

    public static void debug(String message) {
        Main.loading.ofLog(message);
        Log.line("[DEBUG//%%class%%] " + message, ShowSource.YES);
    }

    public enum ShowSource {
        YES(true),
        NO(false),
        TRUNCATED(true);

        final boolean enabled;
        ShowSource(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
}
