package net.cybercake.display.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public class YtDlpReceiver {

    public static String getRawLinkFor(String url) {
        try {
            Process process = new ProcessBuilder("yt-dlp", url, "-f", "best", "-g", "--cookies-from-browser", "firefox").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringJoiner returned = new StringJoiner(" ");
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("https"))
                    continue;
                Log.line("yt-dlp searched " + url + " and found: " + line);

                returned.add(line);
            }

            return returned.toString();

        } catch (Exception exception) {
            throw new IllegalStateException("Unable to get raw link from " + url + ": " + exception, exception);
        }
    }

}
