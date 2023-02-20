package aston.cs3mdd.carparkingapp;

/**
 * This class stores data for a favourite parking spot.
 * @author Umar Rajput.
 */
public class FavouriteItem {

    /**
     * This stores the Google Places ID of the favourite parking spot.
     */
    private String id;

    /**
     * This stores the name of the favourite parking spot.
     */
    private String name;

    /**
     * This stores the address of the favourite parking spot.
     */
    private String address;

    /**
     * This stores the location of the favourite parking spot.
     */
    private String location;

    /**
     * This constructor creates a favourite parking spot item.
     * @param id Google Places ID of parking spot.
     * @param name parking spot name.
     * @param address parking spot address.
     * @param location parking spot location.
     */
    public FavouriteItem(String id, String name, String address, String location) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.location = location;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * This function will concatenate the data in CSV format, so that it can save to files easier.
     * @return String in CSV format.
     */
    @Override
    public String toString() {
        return id + "," + name + "," + address + "," + location;
    }
}
