
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

import com.esri.arcgisruntime.geometry.GeometryEngine;
import javafx.geometry.Point2D;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.time.LocalDate;
import java.util.ArrayList;


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
  private GraphicsOverlay graphicsOverlay;

  private ArrayList<ConstructionSite> data;

  private Stage UI;


  public static void main(String[] args) throws Exception {
//      ArrayList<ConstructionSite> data = ConstructionSites.getWebData();
//      System.out.println(data.size());
//      // uncomment line below to see what the data looks like
//      //System.out.println(data);
//
//      // account number that exists
//      PropertyAssessment property = PropertyAssessments.getProperty("10884521");
//      System.out.println("REAL PROPERTY");
//      System.out.println(property);
//      // account number that does not
//      PropertyAssessment noproperty = PropertyAssessments.getProperty("0");
//      System.out.println("\nNO PROPERTY");
//      System.out.println(noproperty);

      Application.launch(args);
  }


  @Override
  public void start(Stage stage) throws Exception {

        // set the title and size of the stage and show it
        stage.setTitle("Four Lemmings Ltd.");
        stage.setWidth(1200);
        stage.setHeight(600);
        graphicsOverlay = new GraphicsOverlay();


        createMap();
        //Note: point is in the form Longitude, Latitude
          //    need to add spatial refrence to point for it to display properly
        addPoint(new Point(-113.5957277,53.50309322, SpatialReferences.getWgs84()));
        this.data = ConstructionSites.getWebData();

        for (ConstructionSite site : data) {
//            addPoint(new Point(site.getLocation().getLatitude(),site.getLocation().getLongitude(), SpatialReferences.getWgs84()));
            addPoint(site.getLocation().getPoint());
        }
        checkClick();
        UI(stage);
  }


  private void createMap(){
      // create a map using the arcgis api

      // Note: it is not best practice to store API keys in source code.
      String yourApiKey = "AAPK27311aef2718478dae7001749a2b962dnvPlYefUnRx8Fd21z8gbJMvMwCqtH3N8mE-0pqLcO1oTYhYmKV8q8qxEuZZcHv-b";
      ArcGISRuntimeEnvironment.setApiKey(yourApiKey);

      mapView = new MapView();

      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
      // set the map on the map view
      mapView.setMap(map);
      mapView.setViewpoint(new Viewpoint(53.5461, -113.4937, 250000));
      mapView.getGraphicsOverlays().add(graphicsOverlay);
  }

  private void addPoint(Point point){
      //adds a point graphic to the map based on a given point with spatial reference
//      mapView.getGraphicsOverlays().add(graphicsOverlay);
      // create an opaque orange point symbol with a opaque blue outline symbol
      SimpleMarkerSymbol simpleMarkerSymbol =
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);       //can make different colors for different construction types
      SimpleLineSymbol blueOutlineSymbol =
              new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2);

      simpleMarkerSymbol.setOutline(blueOutlineSymbol);
      // create a graphic with the point geometry and symbol
      Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);
      // add the point graphic to the graphics overlay
      graphicsOverlay.getGraphics().add(pointGraphic);
  }

  private void checkClick(){
      //checks of a point graphic on a map was clicked
      mapView.setOnMouseClicked(e -> {
          Point2D point = new Point2D(e.getX(), e.getY());

          // create a map point from a point
          Point mapPoint = mapView.screenToLocation(point);
          // project user-tapped map point location
          Point projectedPoint = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
          double mapScale = mapView.getMapScale();

          // Calculate the tolerance based on the map scale
          double pixelTolerance = 0.00001; // Adjust this value as needed
          double tolerance = pixelTolerance * mapScale / 1000.0; // Convert pixel to map units

          // Check if any graphic is clicked
          graphicsOverlay.getGraphics().forEach(graph -> {
              Point markerPoint = (Point) graph.getGeometry();
              double distance = calculateDistance(projectedPoint, markerPoint);
              if (distance <= tolerance) {
                  // Graphic is clicked
                  System.out.println("Marker clicked!");
                  pointClicked(markerPoint);
              }
          });
      });
  }

  private double calculateDistance(Point point1, Point point2) {
      //used to calculate the distance between 2 points
      return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) +
              Math.pow(point1.getY() - point2.getY(), 2));
  }

  private void pointClicked(Point mapPoint){
      //parameter: point of graphic clicked
      //add what we want to happen to this part
      ConstructionSite site = ConstructionSites.findPoint(mapPoint, data);
      if (site == null) {System.out.println("ERROR"); return ;}
      HBox window = (HBox) this.UI.getScene().getRoot();
      ScrollPane infoS = (ScrollPane) window.getChildren().get(2);
      VBox infoV = (VBox) infoS.getContent();
      infoV.getChildren().clear();
      fillInfo(infoV, site);

  }

  private void UI(Stage stage) throws Exception {
      // UI elements for the application

      VBox filterBox = createFilterBox(stage.getWidth());

      TableView<ConstructionSite> constructionSiteTableView = createConstructionSiteTableView();

      HBox mapSection = createMapSection(stage, mapView, constructionSiteTableView);


      VBox infoSection = createInfoSection(stage.getWidth());

      // Wrap the infoSection VBox in a ScrollPane
      ScrollPane infoScrollPane = new ScrollPane(infoSection);
      infoScrollPane.setFitToWidth(true);
      infoScrollPane.setFitToHeight(true);

      // HBox to hold Filter, Map, and Info sections
      HBox windowFilterMapInfo = new HBox(filterBox, mapSection, infoScrollPane);
      Scene scene2 = new Scene(windowFilterMapInfo, 1200, 600);
      stage.setScene(scene2);
      this.UI = stage;
      stage.show();
  }

    public static VBox createFilterBox(double stageWidth) {
        VBox filterBox = new VBox();
        filterBox.setPrefWidth(stageWidth / 8);
        filterBox.setPadding(new Insets(10, 10, 10, 10));

        addUIElements(filterBox);

        filterBox.setStyle("-fx-background-color: blue;");

        return filterBox;
    }

    private static void addUIElements(VBox filterBox) {
        // Title
        Label titleLabel = new Label("Filter");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setPadding(new Insets(5));

      // Label for file number
        Label fileNumLabel = new Label("File Number:");
        fileNumLabel.setFont(new Font("Arial", 14));
        fileNumLabel.setTextFill(Color.WHITE);
        fileNumLabel.setPadding(new Insets(5));

        // Textfield for file number
        TextField fileNumTxt = new TextField();
        fileNumTxt.setPromptText("Insert file number here");
        fileNumTxt.setStyle("-fx-padding: 0 0 0 5;");

        // Label for Assessment Account number
        Label assessmentLabel = new Label("Account Number:");
        assessmentLabel.setFont(new Font("Arial", 14));
        assessmentLabel.setTextFill(Color.WHITE);
        assessmentLabel.setPadding(new Insets(5));

        // Textfield for address
        TextField accountNumTxt = new TextField();
        accountNumTxt.setPromptText("Insert account number here");
        accountNumTxt.setFont(new Font("Arial", 8));
        accountNumTxt.setStyle("-fx-padding: 0 0 0 5;");

        // Label for distance
        Label distanceLabel = new Label("Distance from Assessment:");
        distanceLabel.setFont(new Font("Arial", 9));
        distanceLabel.setTextFill(Color.WHITE);
        distanceLabel.setPadding(new Insets(5));

        // Spinner for distance
        Spinner<Integer> distanceSpinner = new Spinner<>(0, 100, 50, 5);
        distanceSpinner.setEditable(true);
        distanceSpinner.setStyle("-fx-padding: 0 0 0 5;");

        // Label for obstruction type
        Label obstructionLabel = new Label("Obstuction type:");
        obstructionLabel.setFont(new Font("Arial", 14));
        obstructionLabel.setTextFill(Color.WHITE);
        obstructionLabel.setPadding(new Insets(5));

        // Creating a ComboBox for filters with obstruction type
        ComboBox<String> obstructionFilter = new ComboBox<>();
        obstructionFilter.getItems().addAll("All", "Bike path", "Pedestrian", "Street Parking");
        obstructionFilter.getSelectionModel().selectFirst();
        obstructionFilter.setPadding(new Insets(5));

        // Label for date
        Label dateLabel = new Label("Date:");
        dateLabel.setFont(new Font("Arial", 16));
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setPadding(new Insets(5));

        Label fromLabel = new Label("From");
        fromLabel.setFont(new Font("Arial", 8));
        fromLabel.setTextFill(Color.WHITE);
        fromLabel.setStyle("-fx-padding: 0 0 0 5;");
//        fromLabel.setPadding(new Insets(5));

        DatePicker datePickerFrom = new DatePicker();
        datePickerFrom.setValue(LocalDate.now());
        datePickerFrom.setStyle("-fx-padding: 0 0 0 5;");

        Label toLabel = new Label("To");
        toLabel.setFont(new Font("Arial", 8));
        toLabel.setTextFill(Color.WHITE);
        toLabel.setStyle("-fx-padding: 0 0 0 5;");
//        toLabel.setPadding(new Insets(5));

        DatePicker datePickerTo = new DatePicker();
        datePickerTo.setValue(LocalDate.now());
        datePickerTo.setStyle("-fx-padding: 0 0 0 5;");

        Label gap = new Label();
        Button filter = new Button("Filter Data");

        // Adding UI elements to the Filter VBox
        filterBox.getChildren().addAll(titleLabel, fileNumLabel, fileNumTxt, assessmentLabel, accountNumTxt,
                distanceLabel, distanceSpinner,  obstructionLabel,obstructionFilter,
                dateLabel, fromLabel, datePickerFrom, toLabel, datePickerTo, gap, filter);
    }
    public static TableView<ConstructionSite> createConstructionSiteTableView() throws Exception {
        TableView<ConstructionSite> tableView = new TableView<>();

        // Create columns for each field of Construction site class
        TableColumn<ConstructionSite, String> fileNumber = new TableColumn<>("File Number");
        fileNumber.setCellValueFactory(new PropertyValueFactory<>("fileNumber"));

        TableColumn<ConstructionSite, Dates> dates = new TableColumn<>("Dates");
        dates.setCellValueFactory(new PropertyValueFactory<>("dates"));

        TableColumn<ConstructionSite, String> reason = new TableColumn<>("Reason");
        reason.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<ConstructionSite, Street> street = new TableColumn<>("Street");
        street.setCellValueFactory(new PropertyValueFactory<>("street"));

        TableColumn<ConstructionSite, Affected> affected = new TableColumn<>("Affected");
        affected.setCellValueFactory(new PropertyValueFactory<>("affected"));

        TableColumn<ConstructionSite, Location> location = new TableColumn<>("Location");
        location.setCellValueFactory(new PropertyValueFactory<>("location"));

        // Add columns to tableview
        tableView.getColumns().addAll(fileNumber, dates, reason, street, affected, location);

        // Get items from arraylist to populate data (you can replace this with your actual data)
        ArrayList<ConstructionSite> data = ConstructionSites.getWebData();
        tableView.getItems().addAll(data);

        return tableView;
    }
    public static HBox createMapSection(Stage stage, MapView mapView, TableView<ConstructionSite> constructionSiteTableView) {
        HBox mapSection = new HBox();
        mapSection.setStyle("-fx-background-color: blue;");
        mapSection.setPrefWidth(stage.getWidth() - stage.getWidth() / 4);

        TabPane mapTab = new TabPane();

        Tab mapTab2 = new Tab("Map");
        Tab dataTab = new Tab("Data");

        mapTab2.setContent(mapView);
        dataTab.setContent(constructionSiteTableView);

        mapTab.getTabs().addAll(mapTab2, dataTab);
        mapTab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mapTab.prefWidthProperty().bind(mapSection.widthProperty());

        mapSection.getChildren().add(mapTab);
        return mapSection;
    }
    private VBox createInfoSection(double width) {
        VBox infoBox = new VBox();
        infoBox.setStyle("-fx-background-color: blue;");
        infoBox.setPrefWidth(width / 8);

        addInfoElements(infoBox);

        return infoBox;
    }

    private static void addInfoElements(VBox infoBox) {
        // Title
        Label titleLabel = new Label("Construction");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setPadding(new Insets(5));
        // Label for obstruction type
        Label fileNumLabel = new Label("File Number:");
        fileNumLabel.setFont(new Font("Arial", 14));
        fileNumLabel.setTextFill(Color.WHITE);
        fileNumLabel.setPadding(new Insets(5));

        Label fileNumInfo = new Label("Selected file number");
        fileNumInfo.setFont(new Font("Arial", 12));
        fileNumInfo.setTextFill(Color.YELLOW);
        fileNumInfo.setWrapText(true);
        fileNumInfo.setPadding(new Insets(5));

        // Label for date
        Label dateLabel = new Label("Date:");
        dateLabel.setFont(new Font("Arial", 16));
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setPadding(new Insets(5));

        Label fromLabel = new Label("From");
        fromLabel.setFont(new Font("Arial", 10));
        fromLabel.setTextFill(Color.WHITE);
        fromLabel.setStyle("-fx-padding: 0 0 0 5;");

        Label fromDate = new Label("Selected from Date");
        fromDate.setFont(new Font("Arial", 12));
        fromDate.setTextFill(Color.YELLOW);
        fromDate.setWrapText(true);
        fromDate.setStyle("-fx-padding: 0 0 0 5;");

        Label toLabel = new Label("To");
        toLabel.setFont(new Font("Arial", 10));
        toLabel.setTextFill(Color.WHITE);
        toLabel.setStyle("-fx-padding: 0 0 0 5;");

        Label toDate = new Label("Selected to Date");
        toDate.setFont(new Font("Arial", 12));
        toDate.setTextFill(Color.YELLOW);
        toDate.setWrapText(true);
        toDate.setStyle("-fx-padding: 0 0 0 5;");

        // Label for Reason
        Label reasonLabel = new Label("Reason:");
        reasonLabel.setFont(new Font("Arial", 16));
        reasonLabel.setTextFill(Color.WHITE);
        reasonLabel.setPadding(new Insets(5));

        Label reasonInfo = new Label("Selected to Reason");
        reasonInfo.setFont(new Font("Arial", 12));
        reasonInfo.setTextFill(Color.YELLOW);
        reasonInfo.setWrapText(true);
        reasonInfo.setStyle("-fx-padding: 0 0 0 5;");

        // Label for Affected
        Label affectedLabel = new Label("Affected:");
        affectedLabel.setFont(new Font("Arial", 16));
        affectedLabel.setTextFill(Color.WHITE);
        affectedLabel.setWrapText(true);
        affectedLabel.setPadding(new Insets(5));

        Label affectedInfo = new Label("Selected to Affected");
        affectedInfo.setFont(new Font("Arial", 12));
        affectedInfo.setTextFill(Color.YELLOW);
        affectedInfo.setWrapText(true);
        affectedInfo.setStyle("-fx-padding: 0 0 0 5;");

        infoBox.getChildren().addAll(titleLabel, fileNumLabel, fileNumInfo,
                dateLabel, fromLabel, fromDate, toLabel, toDate,
                reasonLabel, reasonInfo, affectedLabel, affectedInfo);
    }
    private void fillInfo(VBox infoBox, ConstructionSite site){
        // Title
        Label titleLabel = new Label("Construction");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setPadding(new Insets(5));
        // Label for obstruction type
        Label fileNumLabel = new Label("File Number:");
        fileNumLabel.setFont(new Font("Arial", 14));
        fileNumLabel.setTextFill(Color.WHITE);
        fileNumLabel.setPadding(new Insets(5));

        Label fileNumInfo = new Label(site.getFileNumber());
        fileNumInfo.setFont(new Font("Arial", 12));
        fileNumInfo.setTextFill(Color.YELLOW);
        fileNumInfo.setWrapText(true);
        fileNumInfo.setPadding(new Insets(5));

        // Label for date
        Label dateLabel = new Label("Date:");
        dateLabel.setFont(new Font("Arial", 16));
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setPadding(new Insets(5));

        Label fromLabel = new Label("From");
        fromLabel.setFont(new Font("Arial", 10));
        fromLabel.setTextFill(Color.WHITE);
        fromLabel.setStyle("-fx-padding: 0 0 0 5;");

        Label fromDate = new Label(site.getDates().getStartDate());
        fromDate.setFont(new Font("Arial", 12));
        fromDate.setTextFill(Color.YELLOW);
        fromDate.setWrapText(true);
        fromDate.setStyle("-fx-padding: 0 0 0 5;");

        Label toLabel = new Label("To");
        toLabel.setFont(new Font("Arial", 10));
        toLabel.setTextFill(Color.WHITE);
        toLabel.setStyle("-fx-padding: 0 0 0 5;");

        Label toDate = new Label(site.getDates().getFinishDate());
        toDate.setFont(new Font("Arial", 12));
        toDate.setTextFill(Color.YELLOW);
        toDate.setWrapText(true);
        toDate.setStyle("-fx-padding: 0 0 0 5;");

        // Label for Reason
        Label reasonLabel = new Label("Reason:");
        reasonLabel.setFont(new Font("Arial", 16));
        reasonLabel.setTextFill(Color.WHITE);
        reasonLabel.setPadding(new Insets(5));

        Label reasonInfo = new Label(site.getReason());
        reasonInfo.setFont(new Font("Arial", 12));
        reasonInfo.setTextFill(Color.YELLOW);
        reasonInfo.setWrapText(true);
        reasonInfo.setStyle("-fx-padding: 0 0 0 5;");

        // Label for Affected
        Label affectedLabel = new Label("Affected:");
        affectedLabel.setFont(new Font("Arial", 16));
        affectedLabel.setTextFill(Color.WHITE);
        affectedLabel.setWrapText(true);
        affectedLabel.setPadding(new Insets(5));

        Label affectedInfo = new Label(site.getAffected().toString());
        affectedInfo.setFont(new Font("Arial", 12));
        affectedInfo.setTextFill(Color.YELLOW);
        affectedInfo.setWrapText(true);
        affectedInfo.setStyle("-fx-padding: 0 0 0 5;");

        infoBox.getChildren().addAll(titleLabel, fileNumLabel, fileNumInfo,
                dateLabel, fromLabel, fromDate, toLabel, toDate,
                reasonLabel, reasonInfo, affectedLabel, affectedInfo);
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


