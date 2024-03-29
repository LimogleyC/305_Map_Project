package Data_Retrieval;

import java.awt.*;
import java.util.Objects;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;

public class ConstructionSite implements Comparable<ConstructionSite> {
    private String fileNumber; // unique identifier
    private Dates dates;
    private String reason;
    private Street street;
    private Affected affected;
    private Location location;

    public ConstructionSite(String fileNumber, String startDate, String finishDate,
                            String reason, String streetName, String roadSegments,
                            String intersectingStreets, String hourRestrictions,
                            String bike, String pedestrian, String streetParking,
                            double latitude, double longitude) {
        this.fileNumber = fileNumber;
        this.dates = new Dates(startDate, finishDate);
        this.reason = reason;
        this.street = new Street(streetName, roadSegments, intersectingStreets, hourRestrictions);
        this.affected = new Affected(bike, pedestrian, streetParking);
        this.location = new Location(latitude, longitude, new Point(longitude, latitude, SpatialReferences.getWgs84()));
    }

    @Override
    public String toString() {
        return "File number = " + fileNumber + "\n" + dates + "\nReason = "
                + reason + "\n" + street + "\n" + affected + "\n" + location;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConstructionSite)) {
            return false;
        }
        ConstructionSite otherSite = (ConstructionSite) obj;
        return Objects.equals(this.fileNumber, otherSite.fileNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileNumber);
    }

    @Override
    public int compareTo(ConstructionSite o) {
        return 0;
    }

    public String getFileNumber() { return fileNumber; }

    public Dates getDates() {
        return dates;
    }

    public String getReason() {
        return reason;
    }

    public Street getStreet() {
        return street;
    }

    public Affected getAffected() {
        return affected;
    }

    public Location getLocation() {
        return location;
    }

}
