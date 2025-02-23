package net.cybercake.display.libraries;

import com.github.junrar.exception.RarException;
import me.friwi.jcefmaven.impl.util.FileUtils;
import net.cybercake.display.Main;
import net.cybercake.display.utils.Log;
import net.cybercake.display.utils.OS;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class LibUnpacker {

    private static UnpackProgress progress = UnpackProgress.NOT_STARTED;

    public static void unpack() throws Exception {
        String[] library = new String[2];
        if (OS.isLinux()) {
            library[0] = "libs-linux-pt1.rar";
            library[1] = "libs-linux-pt2.rar";
        } else if (OS.isWindows()) {
            library[0] = "libs-win.rar";
        } else {
            throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"));
        }

        File[] inputs = Arrays.stream(library)
                .filter(Objects::nonNull)
                .map(s ->
                        new File(new File(".").getParentFile(), s)
                )
                .toArray(File[]::new);
        File output = new File(new File(".").getParentFile(), "libs");

        try {
            if (progress != UnpackProgress.NOT_STARTED)
                throw new IllegalArgumentException("Called LibUnpacker::unpack, expected state " + UnpackProgress.NOT_STARTED + ", got " + progress);

            LibUnpacker.setState(UnpackProgress.IN_PROGRESS);
            for (File input : inputs) {
                FileExtractor extractor = new FileExtractor(input);
                extractor.extract(output, inputs.length > 1);
            }

            if (!output.exists())
                throw new IllegalStateException("[VERIFICATION] Libraries weren't created at " + output + " from " + Arrays.toString(inputs));

            UnpackerChecker.confirm();

            //             "-Djava.library.path=libs/native",   // Path to native libraries
            //            "-Djcef.resources.dir=libs/native"  // Path to JCEF resources
//            Log.debug("|- Set system property to point to: " + System.getProperty("jcef.resources.dir"));

            LibUnpacker.setState(UnpackProgress.COMPLETE_SUCCESS);
            Log.debug("|- [SUCCESS] LibUnpacker succeeded!");
            Log.debug("|- Exiting LibUnpacker...");
        } catch (Exception exception) {
            Log.debug("|- [FAILURE] LibUnpacker failed: " + exception);
            LibUnpacker.setState(UnpackProgress.COMPLETE_FAILURE);
//            Main.clean();
            if (UnpackerChecker.shouldTryAgain()) {
                Log.debug("|- [FAILURE] EXTRACTOR WILL TRY AGAIN! Attempt #" + UnpackerChecker.attempts + "/" + UnpackerChecker.MAX_ATTEMPTS);
                LibUnpacker.setState(UnpackProgress.NOT_STARTED);
                unpack();
                return;
            }
            Log.debug("|- Exiting LibUnpacker...");
            throw exception;
        }
    }

    public static void setState(UnpackProgress progress) {
        LibUnpacker.progress = progress;
    }

    public enum UnpackProgress {
        NOT_STARTED,

        IN_PROGRESS,

        COMPLETE_SUCCESS,

        COMPLETE_FAILURE;
    }

    static class ExtractorFailed extends IllegalStateException {

        ExtractorFailed(String message) {
            super("CHECK FAILED: " + message);
        }

    }

}
