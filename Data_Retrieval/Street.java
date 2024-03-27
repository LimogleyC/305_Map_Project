package Data_Retrieval;

import java.util.Objects;

public class Street {
    private String streetName;
    private String roadSegments;
    private String intersectingStreets;
    private String hourRestrictions;

    public Street(String streetName, String roadSegments, String intersectingStreets, String hourRestrictions) {
        this.streetName = streetName;
        this.roadSegments = roadSegments;
        this.intersectingStreets = intersectingStreets;
        this.hourRestrictions = hourRestrictions;
    }

    @Override
    public String toString() {
        return "StreetName = " + streetName + ",\nRoadSegments = " + roadSegments + ",\nIntersectingStreets = "
                + intersectingStreets + ",\nHourRestrictions = " + hourRestrictions;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Street)) {
            return false;
        }
        Street otherStreet = (Street) obj;
        return Objects.equals(this.streetName, otherStreet.streetName) &&
                Objects.equals(this.roadSegments, otherStreet.roadSegments) &&
                Objects.equals(this.intersectingStreets, otherStreet.streetName) &&
                Objects.equals(this.hourRestrictions, otherStreet.hourRestrictions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetName, roadSegments, intersectingStreets, hourRestrictions);
    }

    public String getStreetName() {
        return streetName;
    }

    public String getRoadSegments() {
        return roadSegments;
    }

    public String getIntersectingStreets() {
        return intersectingStreets;
    }

    public String getHourRestrictions() {
        return hourRestrictions;
    }
}
