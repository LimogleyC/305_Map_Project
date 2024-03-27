package Data_Retrieval;

import java.awt.*;

public class PropertyAssessment implements Comparable<PropertyAssessment> {
    private int account; // unique identifier
    private Location location;

    public PropertyAssessment(int account, double latitude, double longitude) {
        this.account = account;
        this.location = new Location(latitude, longitude, new Point((int)longitude, (int)latitude));
    }

    @Override
    public String toString() {
        return "Account number = " + account + "\n" + location;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PropertyAssessment)) {
            return false;
        }
        PropertyAssessment otherProperty = (PropertyAssessment) obj;
        return this.account == otherProperty.account;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(account);
    }

    @Override
    public int compareTo(PropertyAssessment value) {
        return 0;
    }

    public int getAccount() {
        return account;
    }

    public Location getLocation() {
        return location;
    }


}