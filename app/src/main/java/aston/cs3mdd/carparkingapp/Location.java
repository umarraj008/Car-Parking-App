package aston.cs3mdd.carparkingapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This class stores latitude and longitude data used for Google Places API.
 * @author Umar Rajput
 */
public class Location implements Serializable {

    /**
     * This stores a latitude value.
     */
    @SerializedName("lat")
    private Double lat;

    /**
     * This stores a longitude value.
     */
    @SerializedName("lng")
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
