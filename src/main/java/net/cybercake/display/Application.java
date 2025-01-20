package net.cybercake.display;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.cybercake.display.args.ArgumentReader;

public class Application extends javafx.application.Application {

    public static ArgumentReader args;

    public static void instance(ArgumentReader args) {
        Application.args = args;
        launch(args.toString());
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Info Display");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
        scene.setFill(Color.rgb(0, 0, 0, 1.0));
        stage.setScene(scene);

        Text text = new Text("No program data: 221841");
        text.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        text.setFill(Color.rgb(255, 255, 255, 1.0));
        grid.add(text, 0, 0, 2, 1);

        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Loading program, please wait!");
        stage.show();
    }

}
