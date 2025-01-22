package net.cybercake.display;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.cybercake.display.args.ArgumentReader;

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

        Scene scene = new Scene(grid, 300, 275);
        scene.setFill(Color.rgb(0, 0, 0, 1.0));
        stage.setScene(scene);

        Text text = new Text("No program data.");
        text.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        text.setFill(Color.rgb(255, 255, 255, 1.0));
        grid.add(text, 0, 0, 1, 1);

//        WebView view = new WebView();
//        WebEngine engine = view.getEngine();
//
//        engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
//            if (newDoc != null) {
//                engine.executeScript(
//                        "document.body.style.backgroundColor = 'black';" +
//                                "document.body.style.color = 'white';"
//                );
//            }
//        });
//
//        engine.load("https://obscountdown.com/lwf?api_key=8bb09be56ab7764152e7a4df426c7de0&lat=37.2296566&lon=-80.4136767&unit=imperial&weather_round=0&theme=gray&lang=en&timezone=America%252FNew_York&hour_format=1&bg_color=%23303d50&font_color=%23f0f0f0&font=Cabin&background_transparency=0&scroll_speed=1&scroll_direction=left");
//        grid.add(view, 0, 0, 2, 2);

        Image image = new Image(new File(new File(".", "images"), "kiss.gif").toURI().toURL().openStream(), 1920, 1080, false, false);
        ImageView view = new ImageView(image);
        grid.add(view, 0, 0, 2, 2);



        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Loading program, please wait!");
        stage.show();
    }

}
