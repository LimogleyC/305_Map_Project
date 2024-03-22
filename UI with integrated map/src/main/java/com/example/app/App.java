
//   Copyright 2020 Esri
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.example.app;

import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;



import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;


public class App extends Application {

  private MapView mapView;


  public static void main(String[] args) {
    Application.launch(args);
  }


  @Override
public void start(Stage stage) throws IOException {

    // set the title and size of the stage and show it
    stage.setTitle("Four Lemmings Ltd.");
    stage.setWidth(1200);
    stage.setHeight(600);

    // create a JavaFX scene with a stack pane as the root node
    // and add it to the scene


    // Note: it is not best practice to store API keys in source code.
    // The API key is referenced here for the convenience of this tutorial.
    String yourApiKey = "PUT API KEY HERE";
    ArcGISRuntimeEnvironment.setApiKey(yourApiKey);

    // create a map view to display the map and add it to the stack pane
    mapView = new MapView();

    ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
    // set the map on the map view
    mapView.setMap(map);
    mapView.setViewpoint(new Viewpoint(53.5461, -113.4937, 250000));

    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().add(graphicsOverlay);
    // create a point geometry with a location and spatial reference
    Point point = new Point(-113.5957277,53.50309322, SpatialReferences.getWgs84());
    // create an opaque orange point symbol with a opaque blue outline symbol
    SimpleMarkerSymbol simpleMarkerSymbol =
            new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.ORANGE, 10);
    SimpleLineSymbol blueOutlineSymbol =
            new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2);

    simpleMarkerSymbol.setOutline(blueOutlineSymbol);
    // create a graphic with the point geometry and symbol
    Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);

    // add the point graphic to the graphics overlay
    graphicsOverlay.getGraphics().add(pointGraphic);

    VBox Filter = new VBox();
    Filter.setPrefWidth(stage.getWidth() / 8);
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
    Map.setPrefWidth(stage.getWidth() - stage.getWidth() / 4);
    TabPane mapTab = new TabPane();
    Tab map2 = new Tab("Map");
    Tab data = new Tab("Data");
    // Add content to the tabs (you can customize this)
    map2.setContent(mapView);
    data.setContent(new Label("Content for Tab 2"));
    mapTab.getTabs().addAll(map2, data);
    mapTab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    mapTab.prefWidthProperty().bind(Map.widthProperty());
    Map.getChildren().add(mapTab);


    // Create an ImageView to display the image


//        Map.setBackground(new Background(new BackgroundFill(WHITE, null, null)));

    VBox Info   = new VBox() ;
    Info.setStyle("-fx-background-color: green;");

//        Info.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
    Info.setPrefWidth(  stage.getWidth() / 8);
    HBox windowFilterMapInfo = new HBox(Filter, Map, Info);
    Scene scene2 = new Scene(windowFilterMapInfo,1200,600);
    stage.setScene(scene2);
    stage.show();

  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    if (mapView != null) {
      mapView.dispose();
    }
  }

}


