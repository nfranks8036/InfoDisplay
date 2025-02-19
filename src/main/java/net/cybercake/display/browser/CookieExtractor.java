package net.cybercake.display.browser;

import net.cybercake.display.utils.Log;
import org.cef.network.CefCookie;
import org.cef.network.CefCookieManager;

import java.net.CookieManager;
import java.util.List;

public abstract class CookieExtractor {

    protected static final String DB_PATH = "./cookies/cookies-db.sqlite";
    protected static final String URL = "jdbc:sqlite:" + DB_PATH;

    protected List<CefCookie> cookies;

    public abstract List<CefCookie> getCookies();

    public CookieExtractor apply(WebPageManager manager) {
        CefCookieManager cookies = manager.getCookieManager();
        int index = 0;
        int failures = 0;
        long mss = System.currentTimeMillis();
        Log.debug("Applying cookies...");
        for (CefCookie cookie : this.cookies) {
            if (!cookies.setCookie(cookie.domain, cookie)) {
                Log.debug("WARNING! Cookie NOT applied: " + cookie.name + " for " + cookie.domain);
                failures++;
            } else {
                index++;
            }
        }
        Log.debug("Applied " + index + " cookies in " + (System.currentTimeMillis() - mss) + "ms!");
        if (failures > 0) {
            Log.debug("Failed to apply " + failures + " cookies!");
        }

        return this;
    }

}
