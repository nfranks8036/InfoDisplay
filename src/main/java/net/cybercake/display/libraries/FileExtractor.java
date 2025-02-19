package net.cybercake.display.libraries;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import net.cybercake.display.utils.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileExtractor {

    private final File file;

    public FileExtractor(File file) {
        this.file = file;
    }

    public void extract(File destination) throws RarException, IOException {
        Log.debug("|- Extracting archive at " + this.file + "...");
        if (!destination.exists()) {
            if (!destination.getAbsoluteFile().mkdirs())
                throw new DestinationAlreadyExistsException("[CHECK] Failed to create destination dir for libraries: " + destination);
            Log.debug("|- Created destination folder at " + destination);
        } else {
            Log.debug("|- Extraction deemed unnecessary, " + destination + " already exists!");
            return;
        }

        long mss = System.currentTimeMillis();
        Log.debug("|- Absolute path of archive: " + this.file.getAbsolutePath());
        Log.debug("|- Absolute path of receiving folder: " + destination.getAbsolutePath());
        try (Archive archive = new Archive(this.file)) {
            FileHeader header;
            while ((header = archive.nextFileHeader()) != null) {
                File output = new File(destination, header.getFileName().trim());
                if (header.isDirectory()) {
                    if (!output.exists() && !output.mkdirs()) {
                        throw new IllegalStateException("[CHECK] Failed to create output dir from extraction at: " + output);
                    }
                    continue;
                }

                if (!checkParent(output) && !output.createNewFile()) {
                    throw new IllegalStateException("[CHECK] Failed to create new file at: " + output);
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(output)) {
                    archive.extractFile(header, fileOutputStream);
                    Log.debug("| Extracted " + header.getFileName());
                }
            }
        }

        Log.debug("|- Successfully completed extraction in " + (System.currentTimeMillis() - mss) + "ms!");
    }

    private boolean checkParent(File file) {
        return createParent(file.getParentFile());
    }

    private boolean createParent(File file) {
        if (file.exists())
            return true;
        if (createParent(file.getParentFile())) {
            if (!file.mkdir()) {
                throw new IllegalStateException("Failed to create parent file @ " + file.getAbsolutePath());
            }
            return true;
        }
        return false;
    }

    static class DestinationAlreadyExistsException extends IllegalStateException {

        DestinationAlreadyExistsException(String message) {
            super(message);
        }

    }

}
