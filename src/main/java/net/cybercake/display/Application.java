package net.cybercake.display;

import com.jogamp.opengl.GLException;
import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.browser.JWebPage;
import net.cybercake.display.browser.WebPageManager;
import net.cybercake.display.libraries.UnpackerChecker;
import net.cybercake.display.status.StatusIndicatorManager;
import net.cybercake.display.utils.Log;
import net.cybercake.display.utils.OS;
import net.cybercake.display.utils.TimeUtils;
import net.cybercake.display.vlc.JVlcPlayer;
import net.cybercake.display.vlc.VlcManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("CallToPrintStackTrace")
public class Application {

    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;

    public static long startTime;

    public static void instance(ArgumentReader args) {
        Application application;
        try {
            startTime = System.currentTimeMillis();
            application = new Application(
                    args,
                    new WebPageManager(args),
                    new VlcManager(args),
                    new StatusIndicatorManager(args)
            );
        } catch (UnsatisfiedLinkError e) { // possible library issue
            if (!UnpackerChecker.shouldTryAgain()) throw e;

            Log.debug("Failed to load libraries for application (" + e.toString() + ")... trying to unpack libraries again if possible!");
            Main.clean();
            Main.unpackLibraries();
            instance(args);
            return;
        }

        Log.debug("Application#start()");
        SwingUtilities.invokeLater(() -> {
            try {
                application.start();
            } catch (GLException glException) {
                Main.loading.dispose();
                glException.printStackTrace();
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Failed to initialize OpenGL components:\n\n" + paginate(glException), "Info Display - OpenGL Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) {
                Main.loading.dispose();
                exception.printStackTrace();
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "A fatal exception has occurred in the program:\n\n" + paginate(exception), "Info Display - Fatal Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private final ArgumentReader args;
    private final WebPageManager web;
    private final VlcManager vlc;
    private final StatusIndicatorManager status;

    public Application(ArgumentReader args, WebPageManager web, VlcManager vlc, StatusIndicatorManager status) {
        this.args = args;
        this.web = web;
        this.vlc = vlc;
        this.status = status;
    }

    private JFrame frame;
    private JPanel root;

    public void start() {
        this.frame = new JFrame("Info Display");

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setUndecorated(OS.isLinux());
        this.frame.setCursor(Cursor.getDefaultCursor());
        this.frame.setBackground(Color.black);
        Log.debug("Created frame: JFrame width=" + WINDOW_WIDTH + ", height=" + WINDOW_HEIGHT + ", fill=" + frame.getBackground() + ", cursor=" + frame.getCursor());

        this.root = new JPanel(new GridLayout(2, 2, 20, 20));
        this.root.setBackground(Color.black);
//        grid.setAlignment(Pos.CENTER);
//        grid.setPadding(new Insets(25, 25, 25, 25));
        Log.debug("Created panel of type GridLayout: " + this.root);

//        Text text = new Text("No program data.");
//        text.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//        text.setFill(Color.rgb(255, 255, 255, 1.0));
//        grid.add(text, 0, 0, 1, 1);

        JWebPage time = this.web.createWebPage("https://www.timeanddate.com/worldclock/fullscreen.html?n=881");
        time.executeJavaScript(
                "document.body.style.color = 'white';" +
                        "document.body.style.backgroundColor = 'rgba(0, 0, 0, 1)';"
        );
        this.root.add(time);

        JWebPage weather = this.web.createWebPage("https://obscountdown.com/lwf?api_key=8bb09be56ab7764152e7a4df426c7de0&lat=37.2296566&lon=-80.4136767&unit=imperial&weather_round=0&theme=gray&lang=en&timezone=America%252FNew_York&hour_format=1&bg_color=%23303d50&font_color=%23f0f0f0&font=Cabin&background_transparency=0&scroll_speed=1&scroll_direction=left");
        weather.executeJavaScript("document.body.style.backgroundColor = 'rgba(0, 0, 0, 1)';");
        this.root.add(weather);

        JWebPage timezones = this.web.createWebPage("https://www.time.gov/?t=24");
        timezones.executeJavaScript(
                "window.scrollTo(0, 30);" +
                        "document.body.style.color = 'white';" +
                        "document.body.style.backgroundColor = 'rgba(0, 0, 0, 1)';" +
                        "document.body.style.zoom = 0.9;"
        );
        this.root.add(timezones);

//        JVlcPlayer youtube = this.vlc.createVlcPlayer("https://www.youtube.com/watch?v=YDfiTGGPYCk", true);
//        this.root.add(youtube);
        JWebPage windy = this.web.createWebPage("https://embed.windy.com/embed.html?type=map&location=coordinates&metricRain=default&metricTemp=default&metricWind=default&zoom=9&overlay=rain&product=ecmwf&level=surface&lat=37.05&lon=-80.228&detailLat=37.244&detailLon=-80.421&detail=true&message=true");
        windy.executeJavaScript(
                "document.body.style.color = 'white';" +
                        "document.body.style.backgroundColor = 'rgba(0, 0, 0, 1)';"
        );
        this.root.add(windy);

        this.status.implement(this.frame);
        this.status.addFromSupp(() -> "DEBUG INFORMATION:");
        this.status.addFromSupp(() -> "Uptime: " + TimeUtils.getFormattedDuration(((System.currentTimeMillis() - Application.startTime) / 1000)));
        this.status.addFromSupp(() -> "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ") as " + System.getProperty("user.name"));
        this.status.addFromCmd("Temperature", "vcgencmd measure_temp").peek((s) -> s.replace("'C", "°C"));
//        this.status.addFromCmd("CPU Usage", "/bin/sh -c top -bn1 | grep \"Cpu(s)\" | awk '{print 100 - $8}'").peek((s) -> s + "%");
        this.status.addFromCmd("IP", "hostname -I");
        this.status.addFromCmd("Clock Speed", "vcgencmd measure_clock arm").peek((s) -> ((Long.parseLong(s))/1000000) + " MHz");
        this.status.addFromCmd("ARM Allocated Memory", "vcgencmd get_mem arm");

        this.frame.getContentPane().add(this.root, BorderLayout.CENTER);
        this.frame.pack();

        if (OS.isWindows()) {
            this.frame.setSize(dimension(Toolkit.getDefaultToolkit().getScreenSize()));
            this.frame.setLocationRelativeTo(null);
            this.frame.setResizable(false);
            this.frame.setExtendedState(JFrame.NORMAL);
        } else {
            Log.debug("Maximizing screen...");
            this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        this.frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                Application.this.dispose();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                Application.this.dispose();
            }
        });

        Log.debug("Making screen visible... program took " + (System.currentTimeMillis() - Main.startTime) + "ms to boot!");
        SwingUtilities.invokeLater(() -> {
            this.frame.setVisible(true);
            Main.loading.dispose();
        });
    }

    public JFrame getFrame() {
        return this.frame;
    }

    public JPanel getRootPanel() {
        return this.root;
    }

    public void dispose() {
        Log.line("Disposing of Application...");
        this.web.dispose();
    }



    private static Dimension dimension(Dimension screen) {
        Dimension dimension = new Dimension();
        dimension.width = Math.min(screen.width - 100, 1920);
        dimension.height = Math.min(screen.height - 100, 1080);
        return dimension;
    }

    private static String paginate(Exception exception) {
        String msg = exception.toString();
        StringBuilder lines = new StringBuilder();
        int characters = 0;
        for (char c : msg.toCharArray()) {
            if (characters > 70 || (characters > 60 && c == ' ')) {
                characters = 0;
                lines.append("\n");
                if (c == ' ')
                    continue;
            }
            lines.append(String.valueOf(c));
            characters++;
        }
        return lines.toString();
    }

}