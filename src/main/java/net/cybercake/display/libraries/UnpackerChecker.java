package net.cybercake.display.libraries;

import java.io.File;

public class UnpackerChecker {

    static final int MAX_ATTEMPTS = 1;
    static int attempts = 0;

    private static File file;

    static void confirm() {
        file = new File(".");

        assertExists("~libs", "directory doesn't exist", true);

        assertExists("~jcef", "directory doesn't exist", true);

        assertExists("resources.pak", "required file doesn't exist", false);

        assertExists("locales",  "directory doesn't exist", true);

        assertExists("en-US.pak", "locale doesn't exist", false);
    }

    public static boolean shouldTryAgain() {
        boolean again = attempts < UnpackerChecker.MAX_ATTEMPTS;
        attempts++;
        return again;
    }

    private static void assertExists(String name, String msg, boolean isDirectory) {
        if (isDirectory && !name.contains("~")) {
            file = file.getParentFile();
        }
        name = name.replace("~", "");
        file = new File(file, name);
        msg = msg + ": " + file;
        if (!file.exists() || (isDirectory && !file.isDirectory())) {
            throw new LibUnpacker.ExtractorFailed(msg);
        }
    }

}
