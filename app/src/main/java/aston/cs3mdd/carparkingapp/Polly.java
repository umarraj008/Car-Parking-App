package aston.cs3mdd.carparkingapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This class stores path data used for Google Places Directions API.
 */
public class Polly implements Serializable {
    @SerializedName("points")
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
