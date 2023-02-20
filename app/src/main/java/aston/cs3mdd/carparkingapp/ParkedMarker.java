package aston.cs3mdd.carparkingapp;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class stores data for a parking marker that would be displayed on the map.
 * @author Umar Rajput
 */
public class ParkedMarker {
    /**
     * Title of the marker.
     */
    private String title;

    /**
     * Location of the marker.
     */
    private LatLng location;

    /**
     * Constructor to create a parking marker.
     * @param title of marker.
     * @param location of marker.
     */
    public ParkedMarker(String title, LatLng location) {
        this.title = title;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
