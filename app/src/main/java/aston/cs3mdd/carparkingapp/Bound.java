package aston.cs3mdd.carparkingapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This class stores bound data.
 * @author Umar Rajput.
 */
public class Bound implements Serializable {
    /**
     * Stores latitude for bound.
     */
    @SerializedName("lat")
    private String lat;

    /**
     * Stores longitude for bound.
     */
    @SerializedName("lng")
    private String lng;

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
