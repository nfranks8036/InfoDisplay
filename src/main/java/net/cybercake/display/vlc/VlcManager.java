package net.cybercake.display.vlc;

import net.cybercake.display.args.ArgumentReader;

import java.util.ArrayList;
import java.util.List;

public class VlcManager {

    public static final String DEFERRED = "--deferred";

    private List<JVlcPlayer> vlcPlayers;

    public VlcManager(ArgumentReader args) {
        this.vlcPlayers = new ArrayList<>();
    }

    public JVlcPlayer createVlcPlayer(String url, boolean youtubeConvert) {
        try {
            if (url.contains("yt:"))
                throw new IllegalArgumentException("URL cannot contain 'yt:': " + url);

            JVlcPlayer vlc = new JVlcPlayer(this, youtubeConvert ? "yt:" + url : url);
            this.vlcPlayers.add(vlc);
            return vlc;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create VLC player for '" + url + "': " + exception, exception);
        }
    }

}
