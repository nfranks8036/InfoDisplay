///usr/bin/env jbang "$0" "$@" ; exit $?


package net.cybercake.display;

import me.friwi.jcefmaven.impl.util.FileUtils;
import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.boot.LoadingWindow;
import net.cybercake.display.libraries.LibUnpacker;
import net.cybercake.display.utils.Center;
import net.cybercake.display.utils.Log;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

@SuppressWarnings("CallToPrintStackTrace")
public class Main {

    public static Application app;
    public static LoadingWindow loading;

    public static long startTime;

    public static String SEPARATOR = "-".repeat(75);

    public static void main(String[] args) {
        System.out.println("Loading program... please wait!");
        startTime = System.currentTimeMillis();
        try {
            loading = new LoadingWindow();
            ArgumentReader reader = new ArgumentReader(args);

            if(reader.getArg("runtime-args").getAsBoolean()) {
                Scanner scanner = new Scanner(System.in);
                Log.line("Enter your runtime arguments: ");
                String[] runtimeArgs = scanner.nextLine().split(" ");
                reader = new ArgumentReader(runtimeArgs);
            }

            Log.line("Project directory: " + new File("").getAbsolutePath());
            Log.line("Logged in as user '" + Main.getUser() + "'");

            Log.line(     SEPARATOR                                                           );
            Log.line(     Center.text("InfoDisplay", SEPARATOR.length())              );
            Log.line(     Center.text("Made by Noah Franks", SEPARATOR.length())      );
            Log.line(     Center.text("Version 1.0.0", SEPARATOR.length())            );
            Log.line(     SEPARATOR                                                           );

            Thread.sleep(2000); // see the opener text

            if (GraphicsEnvironment.isHeadless()) {
                throw new IllegalStateException("This program cannot run in a headless environment.");
            }

            unpackLibraries();

            Application.instance(reader);
        } catch (Exception exception) {
            Log.line(SEPARATOR);
            Log.line("AN EXCEPTION OCCURRED: [SEE BELOW FOR DETAILS]");
            exception.printStackTrace();
            System.exit(1); // generic exception
        }
    }

    static void unpackLibraries() {
        try {
            LibUnpacker.setState(LibUnpacker.UnpackProgress.NOT_STARTED);
            LibUnpacker.unpack();
        } catch (Exception exception) {
            throw new RuntimeException("**** CRITICAL ERROR: FAILED TO EXTRACT REQUIRED LIBRARIES!! ****", exception);
        }
    }

    public static void clean() {
        String[] dirs = new String[]{"build", "cache", "cookies", "libs"};
        try {
            for (String dir : dirs) {
                FileUtils.deleteDir(new File(".", dir));
            }
        } catch (Exception exception) {
            Log.debug("Failed to clean up directories " + Arrays.toString(dirs) + ": " + exception);
        }
    }

    public static String getUser() {
        return Objects.requireNonNullElse(System.getProperty("user.name"), "");
    }
}