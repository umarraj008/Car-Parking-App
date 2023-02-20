package aston.cs3mdd.carparkingapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class stores the route data used for Google Places Directions API.
 * @author Umar Rajput.
 */
public class RouteResult implements Serializable {
    /**
     * This stores a list of routes from a Google Places Directions API request.
     */
    @SerializedName("routes")
    private List<DirectionResult> results = new ArrayList<>();

    public List<DirectionResult> getResults() {
        return results;
    }

    public void setResults(List<DirectionResult> results) {
        this.results = results;
    }
}
