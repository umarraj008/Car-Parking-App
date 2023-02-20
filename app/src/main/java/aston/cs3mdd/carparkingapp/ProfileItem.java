package aston.cs3mdd.carparkingapp;

/**
 * This class stores information for a profile.
 * @author Umar Rajput
 */
public class ProfileItem {
    /**
     * Index of profile item in list.
     */
    private int id;

    /**
     * Name of profile.
     */
    private String name;

    /**
     * Car type of profile.
     */
    private String carType;

    /**
     * Image of profile
     */
    private int image;

    /**
     * To know if this profile is activated.
     * @deprecated this is not used anymore.
     */
    private boolean active;

    /**
     * Parking information for profile.
     */
    private String parking;

    /**
     * Notes of parking for profile.
     */
    private String notes;

    /**
     * Constructor to create profile item.
     * @param id index of item in list.
     * @param name of profile.
     * @param carType of profile.
     * @param image of profile.
     * @param parking of profile.
     * @param notes of profile.
     */
    public ProfileItem(int id, String name, String carType, int image, String parking, String notes) {
        this.id = id;
        this.name = name;
        this.carType = carType;
        this.image = image;
        this.parking = parking;
        this.notes = notes;
    }

    public String getParking() {
        return parking;
    }

    public String getNotes() {
        return notes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCarType() {
        return carType;
    }

    public int getImage() {
        return image;
    }

    public boolean getActive() {
        return active;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * This toString function will concatenate data so that it can be saved to files easier.
     * @return data String.
     */
    @Override
    public String toString() {
        return name + "," + carType + "," + image + "," + parking + "," + notes;
    }
}
