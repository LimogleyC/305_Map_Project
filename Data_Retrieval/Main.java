package Data_Retrieval;

import java.util.ArrayList;

public class Main {
    /**
     * Demo generating data
     */
    public static void main(String[] args) throws Exception {

        ArrayList<ConstructionSite> data = ConstructionSites.getWebData();
        System.out.println(data.size());
        // uncomment line below to see what the data looks like
        //System.out.println(data);

        // account number that exists
        PropertyAssessment property = PropertyAssessments.getProperty("10884521");
        System.out.println("REAL PROPERTY");
        System.out.println(property);
        // account number that does not
        PropertyAssessment noproperty = PropertyAssessments.getProperty("0");
        System.out.println("\nNO PROPERTY");
        System.out.println(noproperty);

    }
}
