package net.cybercake.display.browser;

import net.cybercake.display.browser.youtube.YouTubePlayerManager;
import net.cybercake.display.utils.Log;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JWebPage extends JPanel {

    private final WebPageManager webPageManager;

    private final String originalUrl;
    private final CefBrowser browser;

    private List<JavaScriptCode> javaScript;

    protected JWebPage(WebPageManager manager, String url) {
        super(new BorderLayout());
        this.webPageManager = manager;
        this.originalUrl = url;
        this.javaScript = new ArrayList<>();

        this.browser = WebPageManager.CLIENT.createBrowser(url, false, false); // fix later
        this.browser.setFocus(true);

//        this.executeJavaScript(
//                "document.body.style.backgroundColor = 'rgba(0, 0, 0, 1)';" +
//                        "document.body.style.color = 'white';"
//        );

        this.add(browser.getUIComponent(), BorderLayout.CENTER);
        this.setFocusable(true);

        Log.debug("Created a browser object directed at '" + this.originalUrl + "'");
    }

    public String getOriginalWebPageUrl() {
        return this.originalUrl;
    }

    public CefBrowser getWebPageBrowser() {
        return this.browser;
    }

    public WebPageManager getWebPageManager() {
        return this.webPageManager;
    }

    public void executeJavaScript(String code) {
        JavaScriptCode javaScriptCode = new JavaScriptCode(code);
        if (!this.getWebPageManager().isFrameCreated()) {
            this.javaScript.add(javaScriptCode);
            return;
        }
        this.javaScript.add(javaScriptCode);
        this.executeJavaScript();
    }

    void executeJavaScript() {
        for (JavaScriptCode item : this.javaScript) {
            item.execute(this);
        }
    }

}
