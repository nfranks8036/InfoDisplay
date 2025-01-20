///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.jetbrains:annotations:24.1.0
//DEPS org.openjfx:javafx-controls:22.0.1

package net.cybercake.display;

import javafx.stage.Stage;
import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.utils.Center;
import net.cybercake.display.utils.Log;

import java.util.Scanner;

public class Main {

    public static Application app;

    private static long startTime;

//    public static String OPEN_WEATHER_API_KEY = "8bb09be56ab7764152e7a4df426c7de0";
    public static String SEPARATOR = "-".repeat(75);

    public static void main(String[] args) {
        System.out.println("Loading program... please wait!");
        startTime = System.currentTimeMillis();
        try {
            ArgumentReader reader = new ArgumentReader(args);

            if(reader.getArg("runtimeArgs").getAsBoolean()) {
                Scanner scanner = new Scanner(System.in);
                Log.line("Enter your runtime arguments: ");
                String[] runtimeArgs = scanner.nextLine().split(" ");
                reader = new ArgumentReader(runtimeArgs);
            }

            Log.line(     SEPARATOR                                                           );
            Log.line(     Center.text("InfoDisplay", SEPARATOR.length())              );
            Log.line(     Center.text("Made by Noah Franks", SEPARATOR.length())      );
            Log.line(     Center.text("Version 1.0.0", SEPARATOR.length())            );
            Log.line(     SEPARATOR                                                           );

            Application.instance(reader);
        } catch (Exception exception) {
            Log.line(SEPARATOR);
            Log.line("AN EXCEPTION OCCURRED: [SEE BELOW FOR DETAILS]");
            exception.printStackTrace();
            System.exit(1); // generic exception
        }
    }
}