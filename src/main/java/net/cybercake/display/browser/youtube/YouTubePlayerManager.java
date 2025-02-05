package net.cybercake.display.browser.youtube;

import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.browser.WebPageManager;
import org.cef.network.CefCookie;
import org.cef.network.CefCookieManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YouTubePlayerManager {

    public static final String YOUTUBE_DOMAIN = "youtube.com";
    public static final String YOUTUBE_EMBED_URL_BASE = "https://" + YOUTUBE_DOMAIN + "/embed/{0}?autoplay=1";

    private final YouTubeAuthentication auth;
    private final List<JYouTubePlayer> youtubePlayers;

    private final WebPageManager webPageManager;

    public YouTubePlayerManager(WebPageManager webPageManager, ArgumentReader args) {
        this.auth = new YouTubeAuthentication(args);
        this.auth.requestAuthentication();

        this.youtubePlayers = new ArrayList<>();
        this.webPageManager = webPageManager;
    }

    public JYouTubePlayer createPlayer(String url) {
        try {
            JYouTubePlayer player = new JYouTubePlayer(webPageManager, url);
            this.youtubePlayers.add(player);
            return player;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create YouTube video player for '" + url + "': " + exception, exception);
        }
    }

    public YouTubeAuthentication getAuth() {
        return this.auth;
    }

}
