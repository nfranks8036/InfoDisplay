package net.cybercake.display.browser.youtube;

import net.cybercake.display.browser.JWebPage;
import net.cybercake.display.browser.WebPageManager;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static net.cybercake.display.browser.youtube.YouTubePlayerManager.YOUTUBE_EMBED_URL_BASE;

public class JYouTubePlayer extends JWebPage {

    JYouTubePlayer(WebPageManager web, String url) {
        super(web, url);
    }



    private static String applyYouTubeNormalization(String url) {
        try {
            String watch = getUrlParameter(url, "v");
            if (watch != null) {
                url = YOUTUBE_EMBED_URL_BASE.replace("{0}", watch);
            }
            return url;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to create an embed YouTube link from '" + url + "': " + exception, exception);
        }
    }

    private static @Nullable String getUrlParameter(String urlStr, String parameter) throws URISyntaxException, MalformedURLException {
        URL url = new URL(urlStr);
        List<NameValuePair> parameters = URLEncodedUtils.parse(url.toURI(), Charset.defaultCharset());
        for (NameValuePair param : parameters) {
            if (!param.getName().equalsIgnoreCase(parameter))
                continue;
            return param.getValue();
        }
        return null;
    }

}
