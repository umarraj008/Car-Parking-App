package aston.cs3mdd.carparkingapp;

/**
 * This class stores data for a parking spot item.
 * @author Umar Rajput.
 */
public class SearchItem {

    /**
     * Name of parking spot.
     */
    private String name;

    /**
     * Address of parking spot.
     */
    private String address;

    /**
     * Latitude of parking spot.
     */
    private Double lat;

    /**
     * Longitude of parking spot.
     */
    private Double lng;

    /**
     * Distance of parking spot to users location.
     */
    private String distance;

    /**
     * Google Places ID of parking spot.
     */
    private String id;

    /**
     * To see if parking spot is favourited.
     */
    private boolean check;

    /**
     * Constructor to create search item.
     * @param name of parking spot.
     * @param address of parking spot.
     * @param lat of parking spot.
     * @param lng of parking spot.
     * @param distance of parking spot.
     * @param id of parking spot.
     * @param check of parking spot.
     */
    public SearchItem(String name, String address, Double lat, Double lng, String distance, String id, Boolean check) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.id = id;
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
