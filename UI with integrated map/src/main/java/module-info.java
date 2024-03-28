/*
 COPYRIGHT 1995-2022 ESRI
 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the
 Copyright Laws of the United States.
 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373
 email: contracts@esri.com
 */

module com.example.app {
    // require ArcGIS Runtime module
    requires com.esri.arcgisruntime;

    // requires JavaFX modules that the application uses
    requires javafx.graphics;

    // requires SLF4j module
    requires org.slf4j.nop;
    requires java.datatransfer;
    requires java.desktop;
    requires java.net.http;

    exports com.example.app;
}

