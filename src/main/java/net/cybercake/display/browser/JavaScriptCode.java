package net.cybercake.display.browser;

import org.cef.browser.CefFrame;

import javax.swing.*;

public class JavaScriptCode {

    private final String code;

    public JavaScriptCode(String code) {
        this.code = code;
    }

    public void execute(JWebPage web) {
        web.getWebPageBrowser().getMainFrame().executeJavaScript(this.code, web.getOriginalWebPageUrl(), 0);
    }

}
