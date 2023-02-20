package aston.cs3mdd.carparkingapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This class stores the northeast and southwest bounds for a path from Google Places Directions API request.
 * @author Umar Rajput.
 */
public class BothBound implements Serializable {
    /**
     * Stores the northeast bound of path.
     */
    @SerializedName("northeast")
    private Bound northeast;

    /**
     * Stores the southwest bound of path.
     */
    @SerializedName("southwest")
    private Bound southwest;

    public Bound getNortheast() {
        return northeast;
    }

    public Bound getSouthwest() {
        return southwest;
    }

    public void setNortheast(Bound northeast) {
        this.northeast = northeast;
    }

    public void setSouthwest(Bound southwest) {
        this.southwest = southwest;
    }
}
