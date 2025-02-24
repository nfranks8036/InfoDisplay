package net.cybercake.display.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public class YtDlpReceiver {

    public static String getRawLinkFor(String url) {
        String[] command = new String[]{"yt-dlp", url, "-g"};
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringJoiner returned = new StringJoiner(" ");
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("https"))
                    continue;

                returned.add(line);
            }

            String found = returned.toString();
            if (found.trim().isEmpty()) {
                throw new NullPointerException("No streamed link found");
            }

            Log.line("YtDlpReceiver.getRawLinkFor(" + url + "): " + found);

            return found;

        } catch (Exception exception) {
            throw new IllegalStateException(String.join(" ", command) + " resulted in non-zero exit status\n" +
                    "Unable to get raw link from " + url + ": " + exception, exception);
        }
    }

}
