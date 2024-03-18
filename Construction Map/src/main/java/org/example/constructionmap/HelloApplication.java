package org.example.constructionmap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.paint.Color.WHITE;

public class HelloApplication extends Application {
    //        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
    @Override
    public void start(Stage mainStage) throws IOException {

        mainStage.setTitle("Four Lemmings Ltd.");
        mainStage.setWidth(1200);
        mainStage.setHeight(600);

        VBox Filter = new VBox();
        Label filterLabel = new Label("Filter");
        Filter.getChildren().add(filterLabel);
        Filter.setStyle("-fx-background-color: red;");
//        Filter.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        Filter.setPrefWidth(mainStage.getWidth() / 8);

        HBox Map    = new HBox();
        Map.setStyle("-fx-background-color: blue;");
        Map.setPrefWidth(mainStage.getWidth() - mainStage.getWidth() / 4);

//        Map.setBackground(new Background(new BackgroundFill(WHITE, null, null)));

        VBox Info   = new VBox() ;
        Info.setStyle("-fx-background-color: green;");

//        Info.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        Info.setPrefWidth(  mainStage.getWidth() / 8);

        HBox windowFilterMapInfo = new HBox(Filter, Map, Info);

        Scene scene = new Scene(windowFilterMapInfo,1200,600);

        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}