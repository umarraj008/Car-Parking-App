package aston.cs3mdd.carparkingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * This is a fragment class for the favourite car parking list that will be shown in the tab layout in find parking page.
 * @author Umar Rajput.
 */
public class FindParkingFavouriteList extends Fragment implements SearchParkingRecyclerViewInterface {

    /**
     * This stores the favourite parking spots.
     */
    private ArrayList<FavouriteItem> favouriteItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get favourite items from main activity
        ((MainActivity)getActivity()).updateFavouriteItemsList();
        this.favouriteItems = ((MainActivity)getActivity()).getFavouriteItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_parking_favourite_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //refresh parking markers on map
        ((MainActivity)getActivity()).refreshFavouriteParkingMarkersList();

        //loop through all favourite parking spots in list
        for (FavouriteItem f : favouriteItems) {

            //get location of item
            String[] locData = f.getLocation().split("#");
            double v1;
            double v2;

            try {
                v1 = Double.parseDouble(locData[0]);
                v2 = Double.parseDouble(locData[1]);
            } catch (Exception e) {
                Log.i("asd", e.getMessage());
                continue;
            }

            //add to list
            LatLng latLng = new LatLng(v1,v2);
            ((MainActivity)getActivity()).addFavouriteParkingLocation(f.getName(), latLng);
        }

        //setup favourites list recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.find_parking_favourite_list_recycler_view);
        FindParkingFavouriteListRecyclerViewAdapter adapter = new FindParkingFavouriteListRecyclerViewAdapter(getContext(), favouriteItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //((MainActivity)getActivity()).refreshFavouriteParkingMarkers();
    }

    /**
     * This function is for when a favourite item is clicked in the favourites list.
     * @param position of item in recycler view.
     */
    @Override
    public void onItemClick(int position) {
        //get location of selected favourite parking item
        String[] locData = favouriteItems.get(position).getLocation().split("#");
        double v1;
        double v2;

        try {
            v1 = Double.parseDouble(locData[0]);
            v2 = Double.parseDouble(locData[1]);
        } catch (Exception e) {
            Log.i("asd", e.getMessage());
            return;
        }

        //move camera to favourite parking location
        LatLng latLng = new LatLng(v1,v2);
        ((MainActivity)getActivity()).moveCameraToFavouriteParking(latLng);
//        ((MainActivity)getActivity()).transisionToFragment(new ProfileView(profileItems.get(position).getName(), profileItems.get(position).getCarType()), "R-L", profileItems.get(position).getName() + "'s Profile", true, false);
    }

    /**
     * This function is for when the favourite button is clicked on a favourite parking item in the favourites list.
     * @param position
     */
    @Override
    public void onFavouriteClick(int position) {
        //create an alert dialog box that warns user if they want to remove item from favourites
        new AlertDialog.Builder(getContext())
                .setTitle("Remove from favourites")
                .setMessage("Are you sure you want to remove this parking from favourites?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    //if user confirms dialog
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //remove item from favourites list and update list in files
                        ((MainActivity)getActivity()).removeFromFavourites(favouriteItems.get(position).getId());
                        ((MainActivity)getActivity()).saveFavouriteData();
                        ((MainActivity)getActivity()).updateFavouriteItemsList();

                        //display success toast to user
                        Toast toast = Toast.makeText(getContext(), "Removed from favourites", Toast.LENGTH_SHORT);
                        toast.show();

                        //reload this fragment
                        ((MainActivity)getActivity()).reloadFavouritesFragment(1);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}