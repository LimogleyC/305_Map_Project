
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private TableView<ConstructionSite> dataTable;
  private Graphic lastClickedGraphic;


  public static void main(String[] args) throws Exception {
      Application.launch(args);
  }


    /**
     * Entry point of the JavaFX application.
     * Initializes the stage and starts the application.
     * @param stage The primary stage of the application.
     * @throws Exception If an error occurs during initialization.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Set the title and size of the stage
        stage.setTitle("Four Lemmings Ltd.");
        stage.setWidth(1200);
        stage.setHeight(600);


        // Initialize graphics overlay and last clicked graphic
        graphicsOverlay = new GraphicsOverlay();
        lastClickedGraphic = null;

        // Create and configure the map
        createMap();

        // Note: Points are in the form Longitude, Latitude
        // Need to add spatial reference to the point for it to display properly
        addPoint(new Point(-113.5957277, 53.50309322, SpatialReferences.getWgs84()));

        // Retrieve construction site data
        this.data = ConstructionSites.getWebData();

        // Add points for each construction site to the map
        for (ConstructionSite site : data) {
            addPoint(site.getLocation().getPoint());
        }

        // Enable click event handling on the map
        checkClick();

        // Set up the user interface
        UI(stage);
    }


    /**
     * Creates a map using the ArcGIS API.
     */
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
    /**
     * Adds a point graphic to the map based on a given point with spatial reference.
     * @param point The point to add to the map.
     * @param color The color of the point graphic (optional, default is RED).
     */
  private void addPoint(Point point, Color... color){
      //adds a point graphic to the map based on a given point with spatial reference
      Color pointColor = color.length > 0 ? color[0] : Color.RED;
      // create an opaque orange point symbol with a opaque blue outline symbol
      SimpleMarkerSymbol simpleMarkerSymbol =
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, pointColor, 10);
                //can make different colors for different construction types
      SimpleLineSymbol blackOutlineSymbol =
              new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2);

      simpleMarkerSymbol.setOutline(blackOutlineSymbol);
      // create a graphic with the point geometry and symbol
      Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);
      // add the point graphic to the graphics overlay
      graphicsOverlay.getGraphics().add(pointGraphic);
  }

    /**
     * Checks if a point graphic on a map was clicked.
     */
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
          for (Graphic graphic : graphicsOverlay.getGraphics()) {
              Point markerPoint = (Point) graphic.getGeometry();
              double distance = calculateDistance(projectedPoint, markerPoint);
              if (distance <= tolerance) {
                  // Graphic is clicked
                  System.out.println("Marker clicked!");
                  pointClicked(markerPoint);

                  // Change color to green
                  changeColor(graphic, Color.GREEN);

                  // Revert color of last clicked graphic (if exists)
                  if (lastClickedGraphic != null && !lastClickedGraphic.equals(graphic)) {
                      changeColor(lastClickedGraphic, Color.RED); // Change back to original color
                  }

                  // Update last clicked graphic
                  lastClickedGraphic = graphic;
              }
          }
      });
  }

    /**
     * Calculates the Euclidean distance between two points in a 2D space.
     *
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The distance between the two points.
     */
    private double calculateDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) +
                Math.pow(point1.getY() - point2.getY(), 2));
    }

    /**
     * Handles the action when a point on the map is clicked.
     *
     * @param mapPoint The point on the map that was clicked.
     */
    private void pointClicked(Point mapPoint){
        // Find the construction site associated with the clicked point
        ConstructionSite site = ConstructionSites.findPoint(mapPoint, data);

        // If no construction site is found, print an error message and return
        if (site == null) {
            System.out.println("ERROR");
            return;
        }

        // Get the VBox containing detailed information and clear its contents
        HBox window = (HBox) this.UI.getScene().getRoot();
        ScrollPane infoS = (ScrollPane) window.getChildren().get(2);
        VBox infoV = (VBox) infoS.getContent();
        infoV.getChildren().clear();

        // Fill the VBox with detailed information about the clicked construction site
        fillInfo(infoV, site);
    }

    /**
     * Changes the color of a graphic on the map.
     *
     * @param graphic The graphic whose color will be changed.
     * @param color The new color for the graphic.
     */
    private void changeColor(Graphic graphic, Color color) {
        // Create a new symbol with the specified color
        SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, color, 10);

        // Create a black outline symbol for the new symbol
        SimpleLineSymbol blackOutlineSymbol =
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2);

        // Set the outline for the new symbol
        symbol.setOutline(blackOutlineSymbol);

        // Set the new symbol for the graphic
        graphic.setSymbol(symbol);
    }


    /**
     * Sets up the user interface (UI) elements for the application.
     *
     * @param stage The primary stage of the application.
     * @throws Exception If an error occurs during UI setup.
     */
    private void UI(Stage stage) throws Exception {
        // UI elements for the application

        // Create the filter box
        VBox filterBox = createFilterBox(stage.getWidth());

        // Create the table view for displaying construction site data
        this.dataTable = createConstructionSiteTableView();

        // Create the map section containing the MapView and construction site table view
        HBox mapSection = createMapSection(stage, mapView, this.dataTable);

        // Create the info section for displaying detailed information about selected construction sites
        VBox infoSection = createInfoSection(stage.getWidth());

        // Wrap the info section VBox in a ScrollPane to allow scrolling if necessary
        ScrollPane infoScrollPane = new ScrollPane(infoSection);
        infoScrollPane.setFitToWidth(true);
        infoScrollPane.setFitToHeight(true);

        // HBox to hold Filter, Map, and Info sections
        HBox windowFilterMapInfo = new HBox(filterBox, mapSection, infoScrollPane);

        // Create the scene and set it to the primary stage
        Scene scene2 = new Scene(windowFilterMapInfo, 1200, 600);

        stage.setScene(scene2);
        this.UI = stage;
        // Set the application icon
        stage.show();
    }


    /**
     * Creates and configures a VBox to serve as a filter box.
     *
     * @param stageWidth The width of the stage, used to determine the preferred width of the filter box.
     * @return The configured VBox representing the filter box.
     */
    public VBox createFilterBox(double stageWidth) {
        // Create a new VBox to hold the filter elements
        VBox filterBox = new VBox();

        // Set the preferred width of the filter box to a fraction of the stage width
        filterBox.setPrefWidth(stageWidth / 8);

        // Set padding around the filter box
        filterBox.setPadding(new Insets(10, 10, 10, 10));

        // Add UI elements (not shown in the provided code)
        addUIElements(filterBox);

        // Apply styling to the filter box (background color)
        filterBox.setStyle("-fx-background-color: blue;");

        // Return the configured filter box
        return filterBox;
    }

    /**
     * Adds UI elements to the provided VBox for creating a filter box.
     *
     * @param filterBox The VBox to which UI elements will be added.
     */
    private void addUIElements(VBox filterBox) {
        // Title label for the filter box
        Label titleLabel = new Label("Filter");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setPadding(new Insets(5));

        // Label for file number
        Label fileNumLabel = new Label("File Number:");
        fileNumLabel.setFont(new Font("Arial", 14));
        fileNumLabel.setTextFill(Color.WHITE);
        fileNumLabel.setPadding(new Insets(5));

        // Textfield for file number input
        TextField fileNumTxt = new TextField();
        fileNumTxt.setPromptText("Insert file number here");
        fileNumTxt.setStyle("-fx-padding: 0 0 0 5;");

        // Label for assessment account number
        Label assessmentLabel = new Label("Account Number:");
        assessmentLabel.setFont(new Font("Arial", 14));
        assessmentLabel.setTextFill(Color.WHITE);
        assessmentLabel.setPadding(new Insets(5));

        // Textfield for account number input
        TextField accountNumTxt = new TextField();
        accountNumTxt.setPromptText("Insert account number here");
        accountNumTxt.setFont(new Font("Arial", 8));
        accountNumTxt.setStyle("-fx-padding: 0 0 0 5;");

        // Label for distance
        Label distanceLabel = new Label("Distance from Assessment:");
        distanceLabel.setFont(new Font("Arial", 9));
        distanceLabel.setTextFill(Color.WHITE);
        distanceLabel.setPadding(new Insets(5));

        // Spinner for distance input
        Spinner<Integer> distanceSpinner = new Spinner<>(0, 100, 5, 1);
        distanceSpinner.setEditable(true);
        distanceSpinner.setStyle("-fx-padding: 0 0 0 5;");

        // Label for obstruction type
        Label obstructionLabel = new Label("Obstuction type:");
        obstructionLabel.setFont(new Font("Arial", 14));
        obstructionLabel.setTextFill(Color.WHITE);
        obstructionLabel.setPadding(new Insets(5));

        // ComboBox for selecting obstruction type
        ComboBox<String> obstructionFilter = new ComboBox<>();
        obstructionFilter.getItems().addAll("None", "All", "Bike path", "Pedestrian", "Street Parking");
        obstructionFilter.getSelectionModel().selectFirst();
        obstructionFilter.setPadding(new Insets(5));

        // Label for date
        Label dateLabel = new Label("Date:");
        dateLabel.setFont(new Font("Arial", 16));
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setPadding(new Insets(5));

        // Label for "from" date
        Label fromLabel = new Label("From");
        fromLabel.setFont(new Font("Arial", 8));
        fromLabel.setTextFill(Color.WHITE);
        fromLabel.setStyle("-fx-padding: 0 0 0 5;");

        // DatePicker for selecting start date
        DatePicker datePickerFrom = new DatePicker();
        datePickerFrom.setValue(LocalDate.now());
        datePickerFrom.setStyle("-fx-padding: 0 0 0 5;");

        // Label for "to" date
        Label toLabel = new Label("To");
        toLabel.setFont(new Font("Arial", 8));
        toLabel.setTextFill(Color.WHITE);
        toLabel.setStyle("-fx-padding: 0 0 0 5;");

        // DatePicker for selecting end date
        DatePicker datePickerTo = new DatePicker();
        datePickerTo.setValue(LocalDate.now());
        datePickerTo.setStyle("-fx-padding: 0 0 0 5;");
        // Ensure that the end date cannot be before the start date
        datePickerTo.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        // Update minimum date for datePickerTo based on selected start date
        datePickerFrom.valueProperty().addListener((observable, oldValue, newValue) -> {
            datePickerTo.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(newValue));
                }
            });
            // If selected end date is before start date, set it to current date
            if (datePickerTo.getValue() != null && datePickerTo.getValue().isBefore(newValue)) {
                datePickerTo.setValue(newValue);
            }
        });

        // Create empty labels and buttons for spacing
        Label gap1 = new Label();
        Button filter = new Button("Filter Data");
        Label gap2 = new Label();
        Button clear = new Button("Clear Fields");

        // Add UI elements to the filter VBox
        filterBox.getChildren().addAll(titleLabel, fileNumLabel, fileNumTxt, assessmentLabel, accountNumTxt,
                distanceLabel, distanceSpinner, obstructionLabel, obstructionFilter,
                dateLabel, fromLabel, datePickerFrom, toLabel, datePickerTo, gap1, filter, gap2, clear);

        // Set actions for clear and filter buttons
        clear.setOnAction(e -> clearFields(filterBox));
        filter.setOnAction(e -> {
            try {
                filterData(filterBox);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    /**
     * Checks if the provided TextField is empty or contains only whitespace.
     *
     * @param text The TextField to be checked.
     * @return true if the TextField is not empty or contains only whitespace, false otherwise.
     */
    private boolean check_textfield(TextField text){
        if (text.getText().trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if the selected obstruction type in the provided ComboBox is 'None'.
     *
     * @param obstruction The ComboBox containing obstruction types.
     * @return true if the selected obstruction type is not 'None', false otherwise.
     */
    private boolean check_obstruction(ComboBox obstruction){
        String obs = (String) obstruction.getValue();
        if (obs.equals("None")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if both provided DatePickers have a date selected other than the current date.
     *
     * @param datePicker1 The first DatePicker to be checked.
     * @param datePicker2 The second DatePicker to be checked.
     * @return true if both DatePickers have dates selected other than the current date, false otherwise.
     */
    private boolean check_date(DatePicker datePicker1, DatePicker datePicker2){
        LocalDateTime date1 = datePicker1.getValue().atStartOfDay();
        LocalDateTime date2 = datePicker2.getValue().atStartOfDay();
        LocalDateTime today = LocalDate.now().atStartOfDay();

        if (date1.equals(today) && date2.equals(today)) {
            return false;
        } else {
            return true;
        }
    }
    private void updateTable(ArrayList<ConstructionSite> data){
        this.dataTable.getItems().clear();
        this.dataTable.getItems().addAll(data);
    }
    /**
     * Filters data based on user inputs provided through the UI elements within the specified VBox.
     *
     * @param filter The VBox containing UI elements for filtering data.
     * @throws Exception if an error occurs during the filtering process.
     */
    private void filterData(VBox filter) throws Exception {
        // Array to store all relevant data points
        ArrayList<ConstructionSite> data = this.data;
        // Extract TextField for file number from the filter VBox
        TextField fileNum = (TextField) filter.getChildren().get(2);
        ConstructionSite fileNumSite = null;
        // Check if file number TextField is not empty
        if (check_textfield(fileNum)) {
            // Filter ConstructionSite based on file number and add to filtered data set
            fileNumSite = fileNumFilter(fileNum.getText());
        }

        // Extract TextField for account number and Spinner for distance from the filter VBox
        TextField accountNum = (TextField) filter.getChildren().get(4);
        Spinner distance = (Spinner) filter.getChildren().get(6);
        PropertyAssessment propertyAssessment = null;
        // Check if account number TextField is not empty
        if (check_textfield(accountNum)) {
            // Retrieve PropertyAssessment based on account number
            propertyAssessment = PropertyAssessments.getProperty(accountNum.getText());
            // If PropertyAssessment is found, filter ConstructionSites within the specified distance range
            if (propertyAssessment != null) {
                double range = ((Integer) distance.getValue()).doubleValue();
                data = ConstructionSites.filterDistance(data, range, propertyAssessment);
            }
        }

        // Extract ComboBox for obstruction type from the filter VBox
        ComboBox obstruction = (ComboBox) filter.getChildren().get(8);
        // Check if obstruction ComboBox has a selected value other than 'None'
        if (check_obstruction(obstruction)) {
            // Filter ConstructionSites based on obstruction type
            data = obstructionFilter(data, String.valueOf(obstruction.getValue()));
        }

        // Extract DatePickers for 'from' and 'to' dates from the filter VBox
        DatePicker fromDatePicker = (DatePicker) filter.getChildren().get(11);
        DatePicker toDatePicker = (DatePicker) filter.getChildren().get(13);
        // Check if both date pickers have dates selected other than the current date
        if (check_date(fromDatePicker, toDatePicker)) {
            // Filter ConstructionSites based on the selected date range
            LocalDateTime fromDate = fromDatePicker.getValue().atStartOfDay();
            LocalDateTime toDate = toDatePicker.getValue().atStartOfDay();
            data = ConstructionSites.filterTime(data, fromDate, toDate);
        }

        // If no data matches the filter criteria, return
        if (data.isEmpty()) {this.graphicsOverlay.getGraphics().clear(); this.dataTable.getItems().clear(); return;}
        if(this.data.equals(data) && fileNumSite != null) {
            this.graphicsOverlay.getGraphics().clear();
            this.dataTable.getItems().clear();
            addPoint(fileNumSite.getLocation().getPoint(), Color.VIOLET);
            this.dataTable.getItems().add(fileNumSite);
        }
        else {
            // Clear existing graphics and add new points to the graphics overlay based on filtered data
            this.graphicsOverlay.getGraphics().clear();
            updateTable(data);
            for (ConstructionSite site : data) {
                addPoint(site.getLocation().getPoint());
            }
            // If PropertyAssessment is involved in the filter, mark its location with a green point
            if (propertyAssessment != null) {
                addPoint(propertyAssessment.getLocation().getPoint(), Color.GREEN);
            }
            if (fileNumSite != null) {
                addPoint(fileNumSite.getLocation().getPoint(), Color.VIOLET);
                this.dataTable.getItems().add(fileNumSite);
            }
        }
    }

    /**
     * Filters ConstructionSite based on file number.
     *
     * @param fileNum The file number to filter by.
     * @return The ConstructionSite matching the provided file number.
     */
    private ConstructionSite fileNumFilter(String fileNum){
        // Find ConstructionSite based on file number in the provided data
        ConstructionSite site = ConstructionSites.findFileNum(fileNum, this.data);
        return site;
    }

    /**
     * Filters ConstructionSites based on obstruction type.
     *
     * @param obstruction The obstruction type to filter by.
     * @return ArrayList of ConstructionSites matching the provided obstruction type.
     * @throws Exception if an error occurs during filtering.
     */
    private ArrayList<ConstructionSite> obstructionFilter(ArrayList<ConstructionSite> data, String obstruction) throws Exception {
        String yes = "Yes";
        // Perform different filters based on the obstruction type
        if (obstruction.equals("Bike path")) {
            return ConstructionSites.filterBikeAffected(data, yes);
        }
        if (obstruction.equals("Pedestrian")) {
            return ConstructionSites.filterPedestrianAffected(data, yes);
        }
        if (obstruction.equals("Street Parking")) {
            return ConstructionSites.filterParkingAffected(data, yes);
        }
        if (obstruction.equals("All")) {
            return ConstructionSites.getWebData();
        }
        return null;
    }

    /**
     * Clears the input fields in the filter UI and resets the graphics overlay.
     *
     * @param filter The VBox containing UI elements for filtering.
     */
    private void clearFields(VBox filter){
        // Extract UI elements from the filter VBox
        TextField  fileNum     = (TextField)  filter.getChildren().get(2);
        TextField  accountNum  = (TextField)  filter.getChildren().get(4);
        Spinner    distance    = (Spinner)    filter.getChildren().get(6);
        ComboBox   obstruction = (ComboBox)   filter.getChildren().get(8);
        DatePicker fromDate    = (DatePicker) filter.getChildren().get(11);
        DatePicker toDate      = (DatePicker) filter.getChildren().get(13);

        // Clear input fields
        fileNum.clear();
        accountNum.clear();
        distance.getValueFactory().setValue(50); // Reset distance Spinner to default value
        obstruction.getSelectionModel().selectFirst(); // Reset obstruction ComboBox to default selection
        fromDate.setValue(LocalDate.now()); // Set from DatePicker to current date
        toDate.setValue(LocalDate.now()); // Set to DatePicker to current date

        // Clear existing graphics and redraw points on the graphics overlay
        this.graphicsOverlay.getGraphics().clear();
        updateTable(this.data);
        for (ConstructionSite site: this.data) {
            addPoint(site.getLocation().getPoint());
        }
    }

    /**
     * Creates a TableView populated with ConstructionSite data.
     *
     * @return TableView<ConstructionSite> The constructed TableView.
     * @throws Exception if an error occurs during data retrieval.
     */
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

        // Get items from arraylist to populate data
        ArrayList<ConstructionSite> data = ConstructionSites.getWebData();
        tableView.getItems().addAll(data);

        return tableView;
    }
    /**
     * Creates a section containing a map view and a table view.
     *
     * @param stage The main stage of the application.
     * @param mapView The MapView to display.
     * @param constructionSiteTableView The TableView of ConstructionSite data.
     * @return HBox The constructed HBox containing the map section.
     */
    public static HBox createMapSection(Stage stage, MapView mapView, TableView<ConstructionSite> constructionSiteTableView) {
        // Create HBox for the map section
        HBox mapSection = new HBox();

        // Set background color of the map section
        mapSection.setStyle("-fx-background-color: blue;");

        // Set preferred width of the map section
        mapSection.setPrefWidth(stage.getWidth() - stage.getWidth() / 4);

        // Create TabPane to switch between map view and data table
        TabPane mapTab = new TabPane();

        // Create tabs for map view and data table
        Tab mapTab2 = new Tab("Map");
        Tab dataTab = new Tab("Data");

        // Set content of each tab to the map view and the data table
        mapTab2.setContent(mapView);
        dataTab.setContent(constructionSiteTableView);

        // Add tabs to the TabPane
        mapTab.getTabs().addAll(mapTab2, dataTab);

        // Set closing policy for tabs
        mapTab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Bind the width of the TabPane to the width of the map section
        mapTab.prefWidthProperty().bind(mapSection.widthProperty());

        // Add the TabPane to the map section HBox
        mapSection.getChildren().add(mapTab);

        return mapSection;
    }

    /**
     * Creates an information section with details about the construction.
     *
     * @param width The preferred width of the information section.
     * @return VBox The constructed VBox containing the information section.
     */
    private VBox createInfoSection(double width) {
        // Create VBox for the information section
        VBox infoBox = new VBox();

        // Set background color of the infoBox
        infoBox.setStyle("-fx-background-color: blue;");

        // Set preferred width of the infoBox
        infoBox.setPrefWidth(width / 8);

        // Add information elements to the infoBox
        addInfoElements(infoBox);

        return infoBox;
    }

    /**
     * Adds information elements to the specified VBox.
     *
     * @param infoBox The VBox to which information elements will be added.
     */
    private static void addInfoElements(VBox infoBox) {
        // Title
        Label titleLabel = new Label("Construction");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setPadding(new Insets(5));

        // Label for File Number
        Label fileNumLabel = new Label("File Number:");
        fileNumLabel.setFont(new Font("Arial", 14));
        fileNumLabel.setTextFill(Color.WHITE);
        fileNumLabel.setPadding(new Insets(5));

        // Label for displaying selected file number
        Label fileNumInfo = new Label("Selected file number");
        fileNumInfo.setFont(new Font("Arial", 12));
        fileNumInfo.setTextFill(Color.YELLOW);
        fileNumInfo.setWrapText(true);
        fileNumInfo.setPadding(new Insets(5));

        // Label for Date
        Label dateLabel = new Label("Date:");
        dateLabel.setFont(new Font("Arial", 16));
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setPadding(new Insets(5));

        // Labels for displaying selected from and to dates
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

        // Label for displaying selected reason
        Label reasonInfo = new Label("Selected reason");
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

        // Label for displaying affected information
        Label affectedInfo = new Label("Affected information");
        affectedInfo.setFont(new Font("Arial", 12));
        affectedInfo.setTextFill(Color.YELLOW);
        affectedInfo.setWrapText(true);
        affectedInfo.setStyle("-fx-padding: 0 0 0 5;");

        // Add all information labels to the infoBox
        infoBox.getChildren().addAll(titleLabel, fileNumLabel, fileNumInfo,
                dateLabel, fromLabel, fromDate, toLabel, toDate,
                reasonLabel, reasonInfo, affectedLabel, affectedInfo);
    }

    /**
     * Fills the information section with details of the given ConstructionSite.
     *
     * @param infoBox The VBox representing the information section.
     * @param site The ConstructionSite object containing the details to be displayed.
     */
    private void fillInfo(VBox infoBox, ConstructionSite site){
        // Title
        Label titleLabel = new Label("Construction");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setPadding(new Insets(5));

        // Label for File Number
        Label fileNumLabel = new Label("File Number:");
        fileNumLabel.setFont(new Font("Arial", 14));
        fileNumLabel.setTextFill(Color.WHITE);
        fileNumLabel.setPadding(new Insets(5));

        // Label displaying the file number of the ConstructionSite
        Label fileNumInfo = new Label(site.getFileNumber());
        fileNumInfo.setFont(new Font("Arial", 12));
        fileNumInfo.setTextFill(Color.YELLOW);
        fileNumInfo.setWrapText(true);
        fileNumInfo.setPadding(new Insets(5));

        // Label for Date
        Label dateLabel = new Label("Date:");
        dateLabel.setFont(new Font("Arial", 16));
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setPadding(new Insets(5));

        // Labels for displaying the start and finish dates of the ConstructionSite
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

        // Label displaying the reason for the ConstructionSite
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

        // Label displaying the affected information of the ConstructionSite
        Label affectedInfo = new Label(site.getAffected().toString());
        affectedInfo.setFont(new Font("Arial", 12));
        affectedInfo.setTextFill(Color.YELLOW);
        affectedInfo.setWrapText(true);
        affectedInfo.setStyle("-fx-padding: 0 0 0 5;");

        // Add all information labels to the infoBox
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


