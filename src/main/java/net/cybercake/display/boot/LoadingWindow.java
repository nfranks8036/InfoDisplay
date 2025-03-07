package net.cybercake.display.boot;

import net.cybercake.display.Application;
import net.cybercake.display.browser.JavaScriptCode;

import javax.swing.*;
import java.awt.*;

public class LoadingWindow {

    private final JFrame frame;

    private final JPanel panel;

    private final JLabel clazz;
    private final JLabel log;

    private boolean hold;

    public LoadingWindow() {
        this.frame = new JFrame("Info Display | Loading...");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setSize(900, 100);

        this.frame.setLocation(100, 100);
        this.frame.setBackground(Color.BLACK);

        this.panel = new JPanel(new GridLayout(2, 1));
        this.panel.setBackground(Color.BLACK);
        this.panel.setSize(900, 100);

        this.clazz = new JLabel("Loading...");
        this.clazz.setFont(new Font("Consolas", Font.ITALIC, 15));
        this.clazz.setForeground(Color.LIGHT_GRAY);
        this.clazz.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.panel.add(this.clazz);

        this.log = new JLabel("Loading...");
        this.log.setFont(new Font("Consolas", Font.ITALIC, 25));
        this.log.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.log.setForeground(Color.WHITE);
        this.panel.add(this.log);

        this.frame.getContentPane().add(this.panel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            this.frame.setVisible(true);
        });

    }

    public void ofLog(String clazz, String text) {
        if (!this.frame.isActive()) {
            return;
        }
        if (hold) {
            return;
        }

        try {
            this.clazz.setText(clazz);
            this.log.setText(text);
            if (text.equalsIgnoreCase("Application#start()")) {
                this.ofLog(Application.class.getCanonicalName(), "Booting main window...");
                hold = true;
            }
            Thread.sleep(50);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void dispose() {
        this.frame.dispose();
    }

}
