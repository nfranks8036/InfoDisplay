package net.cybercake.display;

import com.jogamp.opengl.GLException;
import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.browser.JWebPage;
import net.cybercake.display.browser.WebPageManager;
import net.cybercake.display.libraries.LibUnpacker;
import net.cybercake.display.libraries.UnpackerChecker;
import net.cybercake.display.status.StatusIndicatorManager;
import net.cybercake.display.utils.Log;
import net.cybercake.display.vlc.JVlcPlayer;
import net.cybercake.display.vlc.VlcManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Date;

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

        SwingUtilities.invokeLater(() -> {
            try {
                application.start();
            } catch (GLException glException) {
                glException.printStackTrace();
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Failed to initialize OpenGL components:\n\n" + paginate(glException), "Info Display - OpenGL Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) {
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

    public void start() throws IOException {
        this.frame = new JFrame("Info Display");

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        JVlcPlayer youtube = this.vlc.createVlcPlayer("https://www.youtube.com/watch?v=KDorKy-13ak", true);
        this.root.add(youtube);

        this.status.implement(this.frame);
        this.status.addFromSupp(() -> "DEBUG INFORMATION:");
        this.status.addFromSupp(() -> "Uptime: " + ((System.currentTimeMillis() - Application.startTime) / 1000) + "s");
        this.status.addFromSupp(() -> "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ") as " + System.getProperty("user.name"));
        this.status.addFromCmd("Temperature", "vcgencmd measure_temp").peek((s) -> s.replace("'C", "°C"));
        this.status.addFromCmd("CPU Usage", "sudo top -bn1 | sudo grep \"Cpu(s)\" | sudo awk '{print 100 - $8}'").peek((s) -> s + "%");
        this.status.addFromCmd("Clock Speed", "vcgencmd measure_clock arm").peek((s) -> s + " MHz");
        this.status.addFromCmd("ARM Allocated Memory", "vcgencmd get_mem arm");
        this.status.addFromCmd("Memory Usage", "sudo free -m | sudo awk '/Mem:/ {print $3}'").peek((s) -> s + "MB");
        this.status.addFromCmd("Memory Total", "sudo free -m | sudo awk '/Mem:/ {print $2}'").peek((s) -> s + "MB");

        this.frame.getContentPane().add(this.root, BorderLayout.CENTER);
        this.frame.pack();

        if (Main.getUser().equalsIgnoreCase("oeroo")) {
            this.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            this.frame.setLocationRelativeTo(null);
            this.frame.setResizable(false);
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

        SwingUtilities.invokeLater(() -> {
            this.frame.setVisible(true);
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