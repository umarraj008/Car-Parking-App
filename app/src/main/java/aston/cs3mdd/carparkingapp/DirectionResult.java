package aston.cs3mdd.carparkingapp;

import com.google.android.gms.maps.model.Polyline;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * This class stores the directions data from a Google Places Directions API Request.
 * @author Umar Rajput.
 */
public class DirectionResult implements Serializable {
    /**
     * This stores the bounds data for a directions result.
     */
    @SerializedName("bounds")
    private BothBound bounds;

    /**
     * This stores the polyline data for a directions result.
     */
    @SerializedName("overview_polyline")
    private Polly polyline;


    public BothBound getBounds() {
        return bounds;
    }

    public void setBounds(BothBound bounds) {
        this.bounds = bounds;
    }

    public Polly  getPolly() {
        return polyline;
    }

    public void setPolly(Polly  polyline) {
        this.polyline = polyline;
    }
}
