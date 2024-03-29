package com.example.app;

import java.util.Objects;
import com.esri.arcgisruntime.geometry.Point;

public class Location {
    private double latitude;
    private double longitude;
    private Point point;

    public Location(double latitude, double longitude, Point point) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.point = point;
    }

    @Override
    public String toString() {
        return "Location = (" + longitude + ", " + latitude + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) {
            return false;
        }
        Location otherLocation = (Location) obj;
        return (this.latitude == otherLocation.latitude) && (this.longitude == otherLocation.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Point getPoint() { return point; }
}
