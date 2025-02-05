package net.cybercake.display;

import com.jogamp.opengl.GLException;
import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.browser.JWebPage;
import net.cybercake.display.browser.WebPageManager;
import net.cybercake.display.browser.youtube.JYouTubePlayer;
import net.cybercake.display.browser.youtube.YouTubePlayerManager;
import net.cybercake.display.utils.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("CallToPrintStackTrace")
public class Application {

    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;

    public static void instance(ArgumentReader args) throws IOException {
        Application application = new Application(args, new WebPageManager(args));
        SwingUtilities.invokeLater(() -> {
            try {
                application.start();
            } catch (GLException glException) {
                glException.printStackTrace();
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Failed to initialize OpenGL components:\n\n" + glException, "Info Display - OpenGL Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) {
                exception.printStackTrace();
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "A fatal exception has occurred in the program:\n\n" + exception, "Info Display - Fatal Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private final ArgumentReader args;
    private final WebPageManager web;

    public Application(ArgumentReader args, WebPageManager web) {
        this.args = args;
        this.web = web;
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

        File specialImageUsed = new File(new File(".", "images"), "kiss.gif");
        BufferedImage specialImage = ImageIO.read(specialImageUsed);
        JLabel specialImageViewer = new JLabel(new ImageIcon(specialImage));
        specialImageViewer.setSize(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        this.root.add(specialImageViewer);
        Log.debug("Created special image from file " + specialImageUsed.getPath());

        JWebPage weather = this.web.createWebPage("https://obscountdown.com/lwf?api_key=8bb09be56ab7764152e7a4df426c7de0&lat=37.2296566&lon=-80.4136767&unit=imperial&weather_round=0&theme=gray&lang=en&timezone=America%252FNew_York&hour_format=1&bg_color=%23303d50&font_color=%23f0f0f0&font=Cabin&background_transparency=0&scroll_speed=1&scroll_direction=left");
        weather.setSize(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        this.root.add(weather);

        JWebPage time = this.web.createWebPage("https://www.time.gov/?t=24");
        time.executeJavaScript("window.scrollTo(0, 40);");
        time.setSize(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        this.root.add(time);

        JYouTubePlayer player = this.web.youtube.createPlayer("https://www.youtube.com/embed/LXb3EKWsInQ?autoplay=1");
        time.setSize(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        this.root.add(player);

        this.frame.getContentPane().add(this.root, BorderLayout.CENTER);
        this.frame.pack();

        if (Main.getUser().equalsIgnoreCase("oeroo")) {
            this.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            this.frame.setLocationRelativeTo(null);
            this.frame.setResizable(false);
        } else {
            Log.debug("Maximizing screen...");
            this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.frame.setUndecorated(true);
        }
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Application.this.dispose();
            }
        });
        this.frame.setVisible(true);
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

}