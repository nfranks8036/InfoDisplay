package net.cybercake.display.boot;

import net.cybercake.display.browser.JavaScriptCode;

import javax.swing.*;
import java.awt.*;

public class LoadingWindow {

    private final JFrame frame;
    private final JLabel label;

    public LoadingWindow() {
        this.frame = new JFrame("Info Display | Loading...");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setSize(600, 200);

        this.frame.setBackground(Color.BLACK);
        this.frame.setLocationRelativeTo(null);

        this.label = new JLabel("Loading...");
        this.label.setFont(new Font("Consolas", Font.ITALIC, 20));
        this.label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.frame.add(this.label);

        SwingUtilities.invokeLater(() -> {
            this.frame.setVisible(true);
        });

    }

    public void ofLog(String text) {
        if (!this.frame.isActive()) {
            return;
        }
        this.label.setText(text);
    }

    public void dispose() {
        this.frame.dispose();
    }

}
