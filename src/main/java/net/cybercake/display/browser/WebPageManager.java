package net.cybercake.display.browser;

import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.utils.Log;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefCookieManager;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WebPageManager {

    public static CefApp APP;
    public static CefClient CLIENT;

    private final List<JWebPage> webPages;
    private final CefCookieManager cookies;
    private final CookieExtractor extractor;

    private boolean frameCreated;

    public WebPageManager(ArgumentReader args) {
        this.webPages = new ArrayList<>();

        String[] cefArgs = new String[]{
                "--disable-software-rasterizer", "--disable-gpu-compositing"
        };
        if (!CefApp.startup(cefArgs)) {
            throw new RuntimeException("Failed to startup JCEF");
        }
        APP = CefApp.getInstance(cefArgs);
        CefSettings settings = new CefSettings();
        settings.cache_path = new File(".").getAbsolutePath() + File.separator + "cache";
        settings.windowless_rendering_enabled = false;
        settings.user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:134.0) Gecko/20100101 Firefox/134.0";
        Log.debug("Utilizing " + APP.getVersion());
        APP.setSettings(settings);

        CLIENT = APP.createClient();
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

        this.cookies = CefCookieManager.getGlobalManager();
        CookieExtractor newExtractor = null;
        long mss = System.currentTimeMillis();
        try {
            newExtractor = new FirefoxCookieExtractor().apply(this);
        } catch (Exception exception) {
            Log.line("-".repeat(80));
            Log.line("FAILED TO EXTRACT COOKIES FROM FIREFOX");
            this.printStackTrace(exception, null);
            Log.line("-".repeat(80));
        }
        Log.debug("Done applying all cookies in " + (System.currentTimeMillis() - mss) + "ms!");
        this.extractor = newExtractor;
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

    public JWebPage createFrom(JWebPage page) {
        try {
            this.webPages.add(page);
            return page;
        } catch (Exception exception) {
            throw new IllegalStateException("Pre-created web page was not initialized corrected: " + exception, exception);
        }
    }

    public boolean isFrameCreated() {
        return this.frameCreated;
    }

    public void dispose() {
        try {
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
        } catch (Exception exception) {
            Log.line("Failed to clean up and dispose of app: " + exception);
            exception.printStackTrace();
        }

        Log.line("Program closed!");
    }

    CefCookieManager getCookieManager() {
        return this.cookies;
    }

    CookieExtractor getCookieExtractor() {
        return this.extractor;
    }

    private void printStackTrace(Throwable throwable, @Nullable String prefix) {
        Log.line("\t" + (prefix != null ? prefix : "") + throwable.getClass().getName() + ": " + throwable.getMessage());
        for (StackTraceElement element : throwable.getStackTrace()) {
            Log.line("\t\tat " + element.getClassName() + "." + element.getMethodName() + "(" + element.getClassName() + ":" + element.getLineNumber() + ")");
        }
        Throwable causedBy = throwable.getCause();
        if (causedBy != null) {
            this.printStackTrace(causedBy, "Caused By: ");
        }
    }

}
