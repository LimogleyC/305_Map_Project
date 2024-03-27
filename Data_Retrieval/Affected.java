package Data_Retrieval;

import java.util.Objects;

public class Affected {
    private String bike;
    private String pedestrian;
    private String streetParking;

    public Affected(String bike, String pedestrian, String streetParking) {
        this.bike = bike;
        this.pedestrian = pedestrian;
        this.streetParking = streetParking;
    }

    @Override
    public String toString() {
        return "Bikes affected = " + bike + ", Pedestrian affected = " + pedestrian + ", Street Parking affected = " + streetParking;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Affected)) {
            return false;
        }
        Affected otherAffected = (Affected) obj;
        return (Objects.equals(this.bike, otherAffected.bike)) &&
                (Objects.equals(this.pedestrian, otherAffected.pedestrian)) &&
                (Objects.equals(this.streetParking, otherAffected.streetParking));
    }

    @Override
    public int hashCode() {
        return Objects.hash(bike, pedestrian, streetParking);
    }

    public String getBike() {
        return bike;
    }

    public String getPedestrian() {
        return pedestrian;
    }

    public String getStreetParking() {
        return streetParking;
    }
}
