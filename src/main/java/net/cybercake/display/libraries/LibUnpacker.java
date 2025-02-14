package net.cybercake.display.libraries;

import com.github.junrar.exception.RarException;
import me.friwi.jcefmaven.impl.util.FileUtils;
import net.cybercake.display.utils.Log;

import java.io.File;
import java.io.IOException;

public class LibUnpacker {

    private static UnpackProgress progress = UnpackProgress.NOT_STARTED;

    public static void unpack() throws Exception {
        File input = new File(".", "libs.rar");
        File output = new File(".", "libs");

        try {
            if (progress != UnpackProgress.NOT_STARTED)
                throw new IllegalArgumentException("Called LibUnpacker::unpack, expected state " + UnpackProgress.NOT_STARTED + ", got " + progress);

            LibUnpacker.setState(UnpackProgress.IN_PROGRESS);
            FileExtractor extractor = new FileExtractor(input);
            extractor.extract(output);

            if (!output.exists())
                throw new IllegalStateException("[VERIFICATION] Libraries weren't created at " + output + " from " + input);

            LibUnpacker.setState(UnpackProgress.COMPLETE_SUCCESS);
            Log.debug("|- [SUCCESS] LibUnpacker succeeded!");
            Log.debug("|- Exiting LibUnpacker...");
        } catch (Exception exception) {
            Log.debug("|- [FAILURE] LibUnpacker failed: " + exception);
            LibUnpacker.setState(UnpackProgress.COMPLETE_FAILURE);
            LibUnpacker.cleanUp(output);
            Log.debug("|- Exiting LibUnpacker...");
            throw exception;
        }
    }

    static void setState(UnpackProgress progress) {
        LibUnpacker.progress = progress;
    }

    static void cleanUp(File directory) {
        Log.debug("|- [FAILURE] Attempting to clean up " + directory + "...");
        try {
            FileUtils.deleteDir(directory);
        } catch (Exception exception) {
            Log.debug("|- [FAILURE] Failed to clean up directory " + directory + ": " + exception);
        }
    }

    public enum UnpackProgress {
        NOT_STARTED,

        IN_PROGRESS,

        COMPLETE_SUCCESS,

        COMPLETE_FAILURE;
    }

}
