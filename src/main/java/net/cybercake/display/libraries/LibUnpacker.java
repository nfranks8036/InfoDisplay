package net.cybercake.display.libraries;

import com.github.junrar.exception.RarException;
import me.friwi.jcefmaven.impl.util.FileUtils;
import net.cybercake.display.Main;
import net.cybercake.display.utils.Log;

import java.io.File;
import java.io.IOException;

public class LibUnpacker {

    private static UnpackProgress progress = UnpackProgress.NOT_STARTED;

    public static void unpack() throws Exception {
        File input = new File(new File(".").getParentFile(), "libs.rar");
        File output = new File(new File(".").getParentFile(), "libs");

        try {
            if (progress != UnpackProgress.NOT_STARTED)
                throw new IllegalArgumentException("Called LibUnpacker::unpack, expected state " + UnpackProgress.NOT_STARTED + ", got " + progress);

            LibUnpacker.setState(UnpackProgress.IN_PROGRESS);
            FileExtractor extractor = new FileExtractor(input);
            extractor.extract(output);

            if (!output.exists())
                throw new IllegalStateException("[VERIFICATION] Libraries weren't created at " + output + " from " + input);

            UnpackerChecker.confirm();

            //             "-Djava.library.path=libs/native",   // Path to native libraries
            //            "-Djcef.resources.dir=libs/native"  // Path to JCEF resources
            System.setProperty("jcef.resources.dir", "libs" + File.separator + "native");
            Log.debug("|- Set system property to point to: " + System.getProperty("jcef.resources.dir"));

            LibUnpacker.setState(UnpackProgress.COMPLETE_SUCCESS);
            Log.debug("|- [SUCCESS] LibUnpacker succeeded!");
            Log.debug("|- Exiting LibUnpacker...");
        } catch (Exception exception) {
            Log.debug("|- [FAILURE] LibUnpacker failed: " + exception);
            LibUnpacker.setState(UnpackProgress.COMPLETE_FAILURE);
            Main.clean();
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
