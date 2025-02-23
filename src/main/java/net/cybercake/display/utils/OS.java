package net.cybercake.display.utils;

import java.util.Locale;

public enum OS {

    WINDOWS,

    LINUX;

    private static OS os = null;
    public static OS getOS() {
        if (os == null) {
            String determine = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (determine.contains("win")) {
                os = OS.WINDOWS;
            } else if (determine.contains("linux")) {
                os = OS.LINUX;
            }
        }
        return os;
    }

    public static boolean isWindows() {
        return getOS() == OS.WINDOWS;
    }

    public static boolean isLinux() {
        return getOS() == OS.LINUX;
    }

}
