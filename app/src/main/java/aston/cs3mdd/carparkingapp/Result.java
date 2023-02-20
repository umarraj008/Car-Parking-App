package aston.cs3mdd.carparkingapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This class stores place data used in Google Places API.
 * @author Umar Rajput.
 */
public class Result implements Serializable {

    /**
     * This stores the location data of the place.
     */
    @SerializedName("geometry")
    private Geometry geometry;

    /**
     * This stores the name of the place.
     */
    @SerializedName("name")
    private String name;

    /**
     * This stores the address of the place.
     */
    @SerializedName("vicinity")
    private String vicinity;

    /**
     * This stores the Google Place ID of the place.
     */
    @SerializedName("place_id")
    private String placeID;

    public Geometry getGeometry() {
        return geometry;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }
}
