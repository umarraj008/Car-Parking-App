package aston.cs3mdd.carparkingapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class stores data for a place result used for Google Places API.
 * @author Umar Rajput
 */
public class PlacesResults implements Serializable {
    @SerializedName("results")
    private List<Result> results = new ArrayList<>();

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
