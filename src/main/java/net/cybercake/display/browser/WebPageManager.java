package net.cybercake.display.browser;

import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.browser.youtube.YouTubeAuthentication;
import net.cybercake.display.browser.youtube.YouTubePlayerManager;
import net.cybercake.display.utils.Log;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.*;
import org.cef.misc.BoolRef;
import org.cef.network.CefCookie;
import org.cef.network.CefCookieManager;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.cybercake.display.browser.youtube.YouTubePlayerManager.YOUTUBE_DOMAIN;

public class WebPageManager {

    public static CefApp APP;
    public static CefClient CLIENT;

    private final List<JWebPage> webPages;
    private final CefCookieManager cookies;

    private boolean frameCreated;

    public YouTubePlayerManager youtube;

    public WebPageManager(ArgumentReader args) {
        this.webPages = new ArrayList<>();

        APP = CefApp.getInstance(new String[]{
                "--enable-features=UseModernMediaControls",
                "--autoplay-policy=no-user-gesture-required",
                "--enable-media-stream"
        });
        CefSettings settings = new CefSettings();
        settings.cache_path = new File(".").getAbsolutePath() + File.separator + "cache" + File.separator + "nonroot";
        settings.root_cache_path = new File(".").getAbsolutePath() + File.separator + "cache";
        settings.persist_session_cookies = true;
        settings.windowless_rendering_enabled = false;
        settings.user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:134.0) Gecko/20100101 Firefox/134.0";
        Log.line("Root cache for Chromium browser: " + settings.root_cache_path);
        APP.setSettings(settings);

        CLIENT = APP.createClient();
        CLIENT.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public void onAfterCreated(CefBrowser browser) {
                Log.line("[JCEF] Browser created: " + browser);
            }
        });
        CLIENT.addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if (!frame.isMain()) return;
                WebPageManager.this.frameCreated = true;
                for (JWebPage page : WebPageManager.this.webPages) {
                    page.executeJavaScript();
                }
            }
        });

        youtube = new YouTubePlayerManager(this, args);

        this.cookies = CefCookieManager.getGlobalManager();
        this.applyCookies();
    }

    public JWebPage createWebPage(String url) {
        try {
            JWebPage webPage = new JWebPage(this, url);
            this.webPages.add(webPage);
            return webPage;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create web page for '" + url + "': " + exception, exception);
        }
    }

    public boolean isFrameCreated() {
        return this.frameCreated;
    }

    public void dispose() {
        if (!this.webPages.isEmpty()) {
            Log.line("Removing web pages...");
            for (JWebPage page : this.webPages) {
                if (page.getWebPageBrowser() == null) continue;
                Log.line("| Removing " + page.getOriginalWebPageUrl());
                page.getWebPageBrowser().close(true);
            }
        }

        if (CLIENT != null) {
            Log.line("Closing Chromium client...");
            CLIENT.dispose();
        }

        if (APP != null) {
            Log.line("Finalizing and closing app...");
            APP.dispose();
        }
    }



    private void applyCookies() {
        String COOKIE_NAME = "Authorization";
        String COOKIE_VALUE = "Bearer " + this.youtube.getAuth().getRefreshToken();

        CefCookie cookie = new CefCookie(
                COOKIE_NAME, COOKIE_VALUE, YOUTUBE_DOMAIN, "/", true, false, new Date(), new Date(), true, new Date(System.currentTimeMillis() + Long.MAX_VALUE)
        );

        this.cookies.setCookie(cookie.domain, cookie);
        this.cookies.flushStore(null);
    }

}
