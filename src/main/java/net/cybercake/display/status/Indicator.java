package net.cybercake.display.status;

import javax.swing.*;
import java.awt.*;

public abstract class Indicator {

    private final StatusIndicatorManager manager;

    private JLabel label;

    private long lastUpdate;
    private String lastResult;

    Indicator(StatusIndicatorManager manager, String startingText) {
        this.manager = manager;

        this.label = new JLabel(startingText);
        this.label.setForeground(Color.white);
        this.label.setHorizontalAlignment(SwingConstants.LEFT);
        this.label.setFont(new Font("Consolas", Font.PLAIN, 15));

        this.lastUpdate = 0L;
        this.lastResult = null;
    }

    abstract void update();

    public void requestUpdate() {
        if (this.lastUpdate == -1) {
            throw new RequestDeniedException("Current thread is still updating text!");
        }

        this.update();
    }

    public long getLastUpdate() { return this.lastUpdate; }
    public String getLastResult() { return this.lastResult; }

    public void refreshLabel() { this.label.setText(this.lastResult); }

    protected void newResult(String result) {
        if (result == null) {
            throw new NullPointerException("Result is null");
        }

        this.lastResult = result;
        this.lastUpdate = System.currentTimeMillis();
        this.refreshLabel();
    }

    protected void errorResult() {
        this.lastResult = null;
        this.lastUpdate = 0L;
        this.refreshLabel();
    }


    Indicator executeAndReturn() {
        update();
        return this;
    }

    void implement(JPanel parent) {
        parent.add(this.label);
    }

}
