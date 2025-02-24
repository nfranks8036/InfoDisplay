package net.cybercake.display.browser;

public class JavaScriptCode {

    private final String code;

    public JavaScriptCode(String code) {
        this.code = code;
    }

    public void execute(JWebPage web) {
        web.getWebPageBrowser().getMainFrame().executeJavaScript(this.code, web.getOriginalWebPageUrl(), 0);
    }

}
