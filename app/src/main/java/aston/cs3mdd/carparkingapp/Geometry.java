package aston.cs3mdd.carparkingapp;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This is class stores location data for a place item from Google Places API.
 * @author Umar Rajput.
 */
public class Geometry implements Serializable {
    /**
     * This stores the location data of a place item.
     */
    @SerializedName("location")
    private aston.cs3mdd.carparkingapp.Location location;

    public aston.cs3mdd.carparkingapp.Location getLocation() {
        return location;
    }

    public void setLocation(aston.cs3mdd.carparkingapp.Location location) {
        this.location = location;
    }
}
