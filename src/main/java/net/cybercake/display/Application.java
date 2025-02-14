package net.cybercake.display;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.utils.Log;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Application extends javafx.application.Application {

    public static ArgumentReader args;

    public static void instance(ArgumentReader args) {
        Application.args = args;
        launch(args.toString());
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Info Display");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setBackground(Background.fill(Color.rgb(0, 0, 0, 1.0)));
        grid.setPadding(new Insets(25, 25, 25, 25));
        Log.debug("Created grid: " + grid);

        Scene scene = new Scene(grid, 300, 275);
        scene.setCursor(Cursor.NONE);
        scene.setFill(Color.rgb(0, 0, 0, 1.0));
        stage.setScene(scene);
        Log.debug("Created scene: Scene width=" + scene.getWidth() + ", height=" + scene.getHeight() + ", fill=" + scene.getFill());

//        Text text = new Text("No program data.");
//        text.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//        text.setFill(Color.rgb(255, 255, 255, 1.0));
//        grid.add(text, 0, 0, 1, 1);

        File specialImageUsed = new File(new File(".", "images"), "kiss.gif");
        Image specialImage = new Image(specialImageUsed.toURI().toURL().openStream(), (double) 1920 / 2, (double) 1080 / 2, false, false);
        ImageView specialImageViewer = new ImageView(specialImage);
        grid.add(specialImageViewer, 2, 1, 1, 1);
        Log.debug("Created special image from file " + specialImageUsed.getPath());

        Text specialText = new Text("(LESBIANS)");
        specialText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 185));
        specialText.setFill(Color.rgb(255, 255, 255, 1.0));
        grid.add(specialText, 2, 1, 1, 1);
        Log.debug("Created special text: " + specialText.getText());

//        Text text = new Text("???");
//        text.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//        text.setFill(Color.rgb(255, 255, 255, 1.0));
//        grid.add(text, 1, 2, 1, 1);
//
//        Task<Void> task = new Task<Void>() {
//            private long ONE_DAY = 5_000L; //86400000L;
//
//            @Override
//            protected Void call() throws Exception {
//                try {
//                    while (true) {
//                        System.out.println("Chore chart");
//                        String person = "???";
//                        int time = (Math.round((float) System.currentTimeMillis() / ONE_DAY) % 3);
//                        if (time == 0)
//                            person = "Noah";
//                        else if (time == 1)
//                            person = "Jesse";
//                        else if (time == 2)
//                            person = "Avi";
//                        final String realPerson = person;
//
//                        Platform.runLater(() -> {
//                            text.setText(realPerson);
//                        });
//
//                        Thread.sleep(5_000);
//                    }
//                } catch (Exception exception) {
//                    Log.line("Failed to change chore chart: " + exception);
//                }
//
//                return null;
//            }
//        };
//        Thread thread = new Thread(task);
//        thread.setDaemon(true);
//        thread.start();

        WebView weatherView = new WebView();
        weatherView.setStyle("-fx-background-color: rgba(255,0,0,0);");
        WebEngine weatherDisplay = weatherView.getEngine();
        weatherDisplay.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                weatherDisplay.executeScript(
                        "document.body.style.backgroundColor = 'rgba(0, 0, 0, 1)';"
                );
            }
        });
        weatherDisplay.load("https://obscountdown.com/lwf?api_key=8bb09be56ab7764152e7a4df426c7de0&lat=37.2296566&lon=-80.4136767&unit=imperial&weather_round=0&theme=gray&lang=en&timezone=America%252FNew_York&hour_format=1&bg_color=%23303d50&font_color=%23f0f0f0&font=Cabin&background_transparency=0&scroll_speed=1&scroll_direction=left");
        grid.add(weatherView, 1, 1, 1, 1);
        Log.debug("Created weather widget, browser is currently displaying " + weatherDisplay.getLocation());

        WebView timeView = new WebView();
        timeView.setPrefSize(((double) 1920 / 2) + 100, (double) 1080 / 2);
        WebEngine timeDisplay = timeView.getEngine();
        timeDisplay.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                timeDisplay.executeScript(
                        "document.body.style.backgroundColor = 'rgba(0, 0, 0, 1)';" +
                                "document.body.style.color = 'white';" +
                                "window.scrollTo(0, 40);"
                );
            }
        });
        timeDisplay.load("https://time.gov/?t=24");
        timeDisplay.reload();
        grid.add(timeView, 1, 2, 1, 1);
        Log.debug("Created time widget, browser is currently displaying " + timeDisplay.getLocation());

        WebView newsView = new WebView();
        newsView.setPrefSize((double) 1920 / 2, (double) 1080 / 2);
        WebEngine newsDisplay = newsView.getEngine();
//        newsDisplay.load("https://www.youtube.com/embed/YDfiTGGPYCk?autoplay=1");
        newsDisplay.load("https://www.ground.news/");
        grid.add(newsView, 2, 2, 1, 1);
        Log.debug("Created news widget, browser is currently displaying " + newsDisplay.getLocation());

        if (Main.getUser().equalsIgnoreCase("oeroo")) {
            stage.setResizable(false);
            stage.setWidth(1920);
            stage.setHeight(1080);
        } else {
            Log.debug("Maximizing screen...");
            stage.setMaximized(true);
            stage.setFullScreen(true);
        }
        stage.setFullScreenExitHint("Loading dashboard, please wait!");
        stage.show();
    }

}