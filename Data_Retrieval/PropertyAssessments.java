package Data_Retrieval;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class PropertyAssessments {

    /**
     * Send a URL request for data related to the provided account number
     * in json format related to the Property Assessment Data
     * (Current Calendar Year) dataset
     * @return null if account cannot be found or the PropertyAssessment related to the account
     */
    public static PropertyAssessment getProperty(String account_number) throws Exception {

        // creating the Http client
        HttpClient client = HttpClient.newHttpClient();

        // constructing the request for the data related to the provided account_number
        String url = "https://data.edmonton.ca/resource/q7d6-ambg.json?account_number=" + account_number;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        try {
            // converting the received data into a string and splitting it at each " character
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String[] line = response.body().split("\"");

            // checking if data for the provided account_number exists
            if (line.length != 1) {

                // dictionary to store any found values
                HashMap<String, String> propertyDict = new HashMap<String, String>();
                propertyDict.put("account_number", "0");
                propertyDict.put("latitude", "0");
                propertyDict.put("longitude", "0");

                // looping through the split line
                for (int i = 1; i + 2 <= line.length - 2; i = i + 2) {
                    // if key exists in the dictionary, update its value
                    if (propertyDict.get(line[i]) != null) {
                        propertyDict.put(line[i], line[i+2]);
                    }
                }
                // creating a new PropertyAssessment object with the found values
                return new PropertyAssessment(Integer.parseInt(propertyDict.get("account_number")),
                        Double.parseDouble(propertyDict.get("latitude")),
                        Double.parseDouble(propertyDict.get("longitude")));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // no data was found for the provided account_number
        return null;
    }
}
