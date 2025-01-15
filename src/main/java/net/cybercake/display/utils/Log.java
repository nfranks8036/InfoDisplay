package net.cybercake.display.utils;

import net.cybercake.display.Main;

public class Log {

    public static boolean SHOW_CLASSES = false;
    public static int SHOWN_OF_CLASS_CHARACTER_LIMIT = 25;

    public static void line(Object object) {
        line(String.valueOf(object));
    }

    @SuppressWarnings("ConstantValue")
    public static void line(String message) {
        String displayed = null;
        if(SHOW_CLASSES) {
            StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
            String callerDisplayed = caller.getClassName();
            displayed = "..." + callerDisplayed.substring(callerDisplayed.length()-SHOWN_OF_CLASS_CHARACTER_LIMIT) + ": ";
            if(callerDisplayed.equalsIgnoreCase(Main.class.getCanonicalName()))
                displayed = " ".repeat(SHOWN_OF_CLASS_CHARACTER_LIMIT + 5);
        }
        System.out.println((displayed != null && SHOW_CLASSES ? displayed : "") + message);
    }
    
}
