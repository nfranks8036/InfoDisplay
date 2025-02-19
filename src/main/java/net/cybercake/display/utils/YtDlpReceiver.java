package net.cybercake.display.utils;

import jogamp.graph.font.typecast.ot.table.CffTable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class YtDlpReceiver {

    public static String getRawLinkFor(String url) {
        try {
            Process process = new ProcessBuilder("yt-dlp", url, "-g").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            return reader.readLine();

        } catch (Exception exception) {
            throw new IllegalStateException("Unable to get raw link from " + url + ": " + exception, exception);
        }
    }

}
