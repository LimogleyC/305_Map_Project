package com.example.app;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ConstructionSites {

    /**
     * Send a URL request for data in json format related to the
     * On-street Construction and Maintenance dataset and convert it
     * into a list of ConstructionSite objects.
     * @return data - List of ConstructionSite objects
     */
    public static ArrayList<ConstructionSite> getWebData() throws Exception {

        // creating the list to store the ConstructionSite objects into
        ArrayList<ConstructionSite> data =  new ArrayList<>();

        // offset for paging through the dataset
        int offset = 0;

        // creating the Httpclient
        HttpClient client = HttpClient.newHttpClient();

        // looping until the end of the dataset
        while (true) {

            // constructing the url request with its offset
            String url = "https://data.edmonton.ca/resource/7wiq-4rgy.json?$offset=" + offset;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            try {
                // converting the received data into a string and splitting it at each \n character
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String[] lines = response.body().split("\n");

                // breaking out of loop when it reaches the end of the dataset
                if (Objects.equals(lines[0], "[]")) {
                    break;
                }

                //
                for (String line : lines) {
                    filterWebData(line, data);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            offset += 1000;
        }
        return data;
    }

    /**
     * Split the provided string at each " and then loop through it, collecting any values
     * that have a key already existing in the premade dictionary, and then savign them into
     * a ConstructionSite object and adding that to the provided list.
     * @param line line containing values and keys related to a ConstructionSite object
     * @param data the list that contains ConstructionSite objects
     * @return data - the updated List of ConstructionSite objects
     */
    private static ArrayList<ConstructionSite> filterWebData(String line, ArrayList<ConstructionSite> data) {

        // splitting the line at each "
        String[] values = line.split("\"");

        // ignoring any line that does not contain "file_number"
        if (Objects.equals(values[1], "file_number")) {

            // dictionary to store any found values
            HashMap<String, String> siteDict = new HashMap<String, String>();
            siteDict.put("file_number", "");
            siteDict.put("start_date", "");
            siteDict.put("finish_date", "");
            siteDict.put("work_reason", "");
            siteDict.put("street_full_name", "");
            siteDict.put("road_segment_intersections", "");
            siteDict.put("intersecting_streets", "");
            siteDict.put("peak_hour_restrictions", "");
            siteDict.put("bike_facilities_affected", "Unknown");
            siteDict.put("on_street_parking_affected", "Unknown");
            siteDict.put("pedestrian_affected", "Unknown");
            siteDict.put("latitude", "0");
            siteDict.put("longitude", "0");

            // looping through the split line
            for (int i = 1; i + 2 <= values.length - 2; i = i + 2) {
                // if key exists in the dictionary, update its value
                if (siteDict.get(values[i]) != null) {
                    siteDict.put(values[i], values[i+2]);
                }
            }

            // creating a new ConstructionSite object with the found values
            ConstructionSite site = new ConstructionSite(
                    siteDict.get("file_number"), siteDict.get("start_date"), siteDict.get("finish_date"),
                    siteDict.get("work_reason"), siteDict.get("street_full_name"),
                    siteDict.get("road_segment_intersections"), siteDict.get("intersecting_streets"),
                    siteDict.get("peak_hour_restrictions"), siteDict.get("bike_facilities_affected"),
                    siteDict.get("pedestrian_affected"), siteDict.get("on_street_parking_affected"),
                    Double.parseDouble(siteDict.get("latitude")), Double.parseDouble(siteDict.get("longitude")));

            // adding new ConstructionSite object to the provided list
            data.add(site);
        }
        return data;
    }

    /**
     * Loop through the ConstructionSite objects of the provided list and return the one that
     * has the desired file number.
     * @param targetFileNum the file number belonging to the desired ConstructionSite
     * @param data the list that contains ConstructionSite objects
     * @return site - the desired ConstructionSite
     */
    public static ConstructionSite findFileNum(String targetFileNum, ArrayList<ConstructionSite> data) {
        try {
            // looping through the list data
            for (ConstructionSite site : data) {
                // found matching file number
                if (Objects.equals(site.getFileNumber(), targetFileNum)) {
                    return site;
                }
            }
        } catch(Exception e) {
            System.out.println("Error: invalid file number...");
        }
        // could not find site
        return null;
    }

    /**
     * Loop through the ConstructionSite objects of the provided list and return the one that
     * has the point object.
     * @param targetPoint the point object belonging to the desired ConstructionSite
     * @param data the list that contains ConstructionSite objects
     * @return site - the desired ConstructionSite
     */
    public static ConstructionSite findPoint(Point targetPoint, ArrayList<ConstructionSite> data) {
        try {
            // looping through the list data
            for (ConstructionSite site : data) {
                // found matching point object
                if (Objects.equals(site.getLocation().getPoint(), targetPoint)) {
                    return site;
                }
            }
        } catch(Exception e) {
            System.out.println("Error: invalid point...");
        }
        // could not find site
        return null;
    }

    /**
     * Loop through the provided list data of ConstructionSite objects and
     * copy any objects that have the same bike affect as the filter
     * string into a new list.
     * @param data the list that contains ConstructionSite objects
     * @param filter the affect used to filter the new list
     * @return filteredData - a new list that contains all the sites
     * from the filter
     */
    public static ArrayList<ConstructionSite> filterBikeAffected(ArrayList<ConstructionSite> data, String filter) {
        ArrayList<ConstructionSite> filteredData =  new ArrayList<>();
        for (ConstructionSite site : data) {
            if (site.getAffected().getBike().equals(filter)) {
                filteredData.add(site);
            }
        }
        return filteredData;
    }

    /**
     * Loop through the provided list data of ConstructionSite objects and
     * copy any objects that have the same pedestrian affect as the filter
     * string into a new list.
     * @param data the list that contains ConstructionSite objects
     * @param filter the affect used to filter the new list
     * @return filteredData - a new list that contains all the sites
     * from the filter
     */
    public static ArrayList<ConstructionSite> filterPedestrianAffected(ArrayList<ConstructionSite> data, String filter) {
        ArrayList<ConstructionSite> filteredData =  new ArrayList<>();
        for (ConstructionSite site : data) {
            if (site.getAffected().getPedestrian().equals(filter)) {
                filteredData.add(site);
            }
        }
        return filteredData;
    }

    /**
     * Loop through the provided list data of ConstructionSite objects and
     * copy any objects that have the same street parking affect as the filter
     * string into a new list.
     * @param data the list that contains ConstructionSite objects
     * @param filter the affect used to filter the new list
     * @return filteredData - a new list that contains all the sites
     * from the filter
     */
    public static ArrayList<ConstructionSite> filterParkingAffected(ArrayList<ConstructionSite> data, String filter) {
        ArrayList<ConstructionSite> filteredData =  new ArrayList<>();
        for (ConstructionSite site : data) {
            if (site.getAffected().getStreetParking().equals(filter)) {
                filteredData.add(site);
            }
        }
        return filteredData;
    }

    /**
     * Find the distance between two points using their longitude and latitude values
     * and output it in kilometers.
     * Reference:
     * https://www.geeksforgeeks.org/program-distance-two-points-earth/
     * @param lat1 latitude of point 1
     * @param lat2 latitude of point 2
     * @param lon1 longitude of point 1
     * @param lon2 longitude of point 2
     * @return distance between two points in km
     */
    private static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2)
    {

        // Converting all points to radians
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers
        double r = 6371;

        // calculate the result
        return(c * r);
    }
}
