package org.example.constructionmap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class ConstructionMap extends Application {
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
        Filter.setPrefWidth(mainStage.getWidth() / 8);
        Filter.setPadding(new Insets(10,10,10,10));
        Label filterLabel = new Label("Filter");
        filterLabel.setAlignment(Pos.CENTER);
        filterLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
//        Filter.getChildren().add(filterLabel);
        Filter.setStyle("-fx-background-color: red;");


        Label assessmentType = new Label("Assessment Type") ;
        assessmentType.setFont(new Font("Arial", 14));
        assessmentType.setPadding(new Insets(5));
//        Filter.getChildren().add(assessmentType);
        ComboBox<String> filters = new ComboBox<>();
        filters.getItems().addAll("*", "COMMERCIAL", "RESIDENTIAL", "FARMLAND", "NONRES MUNICIPAL/RES EDUCATION");
        filters.setPadding(new Insets(5));
//        Filter.getChildren().add(filters);

        Label assessmentRange = new Label("Assessed Value Range") ;
        assessmentRange.setFont(new Font("Arial", 14));
        assessmentRange.setPadding(new Insets(10));
//        Filter.getChildren().add(assessmentRange);
        TextField minValue = new TextField();
        minValue.setPromptText("Min Value");
        minValue.setPadding(new Insets(10,10,10,10));
//        Filter.getChildren().add(minValue);
        TextField maxValue = new TextField();
        maxValue.setPromptText("Max Value");
        maxValue.setPadding(new Insets(10,10,10,10));

        Filter.getChildren().addAll(filterLabel, assessmentType, filters, assessmentRange, minValue, maxValue);



//        Filter.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));


        HBox Map    = new HBox();
        Map.setStyle("-fx-background-color: blue;");
        Map.setPrefWidth(mainStage.getWidth() - mainStage.getWidth() / 4);
        TabPane mapTab = new TabPane();
        Tab map = new Tab("Map");
        Tab data = new Tab("Data");
        // Add content to the tabs (you can customize this)
        Image edmonton = new Image("https://previews.123rf.com/images/tish11/tish111911/tish11191100212/133936581-vector-map-of-the-city-of-edmonton-canada.jpg");
        ImageView edmontonView = new ImageView(edmonton);
        edmontonView.setFitWidth(Map.getWidth());
        edmontonView.setFitHeight(Map.getHeight());
        map.setContent(edmontonView);
        data.setContent(new Label("Content for Tab 2"));
        mapTab.getTabs().addAll(map, data);
        mapTab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mapTab.prefWidthProperty().bind(Map.widthProperty());
        Map.getChildren().add(mapTab);


        // Create an ImageView to display the image


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