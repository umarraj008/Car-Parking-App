package aston.cs3mdd.carparkingapp;

/**
 * This interface is used for recycler view adapter classes that that require an onClick and onFavouriteClick method.
 * @author Umar Rajput.
 */
public interface SearchParkingRecyclerViewInterface {
    void onItemClick(int position);
    void onFavouriteClick(int position);
}
