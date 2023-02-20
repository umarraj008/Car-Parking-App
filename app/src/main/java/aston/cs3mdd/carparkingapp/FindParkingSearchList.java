package aston.cs3mdd.carparkingapp;

import android.icu.util.ULocale;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputLayout;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

/**
 * This is a fragment class for the find parking page that will contain a list of parking spots nearby.
 * @author Umar Rajput.
 */
public class FindParkingSearchList extends Fragment implements SearchParkingRecyclerViewInterface {

    /**
     * This stores the parking spots nearby.
     */
    private ArrayList<SearchItem> searchItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_parking_search_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //update the recycler view
        updateRecycler();
    }

    /**
     * Function for when an item in the find parking list is clicked.
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        //move map camera to selected parking location
        ((MainActivity)getActivity()).moveCameraToParking(position);
    }

    /**
     * Function for when favourite button is pressed on a parking item in the list.
     * @param position
     */
    @Override
    public void onFavouriteClick(int position) {
        //get item
        SearchItem s = searchItems.get(position);

        //if the item is already favourited
        if (s.isCheck()) {
            //add to favourites and save to files
            s.setCheck(false);
            ((MainActivity)getActivity()).removeFromFavourites(s.getId());
            ((MainActivity)getActivity()).saveFavouriteData();
            ((MainActivity)getActivity()).updateFavouriteItemsList();

            //display a success message to user
            Toast toast = Toast.makeText(getContext(), "Removed from favourites", Toast.LENGTH_SHORT);
            toast.show();
        } else { //if item isnt favourited
            //remove from favourites and remove from files
            s.setCheck(true);
            ((MainActivity)getActivity()).addToFavourites(s.getId(), s.getName(), s.getAddress().replaceAll(",", "#"), s.getLat() + "#" + s.getLng());
            ((MainActivity)getActivity()).saveFavouriteData();
            ((MainActivity)getActivity()).updateFavouriteItemsList();

            //display a success message
            Toast toast = Toast.makeText(getContext(), "Added to favourites", Toast.LENGTH_SHORT);
            toast.show();
        }

        //reload the favourites fragment
        ((MainActivity)getActivity()).reloadFavouritesFragment(0);
    }

    /**
     * Function to update the recycler view.
     */
    private void updateRecycler() {
        //clear search items
        searchItems = new ArrayList<>();

        //Google Places API request
        String key = getResources().getString(R.string.google_api_key);
        Location currentLocation = ((MainActivity)getActivity()).getCurrentLocation();
        String location = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        int radius = 5000;

        //API request
        GooglePlacesApi googlePlacesApi = APIClient.getClient().create(GooglePlacesApi.class);
        googlePlacesApi.getPlaces(location,radius,"parking",key).enqueue(new Callback<PlacesResults>() {

            //on API response
            @Override
            public void onResponse(Call<PlacesResults> call, Response<PlacesResults> response) {

                //successful response
                if (response.isSuccessful()) {
                    //get results and refresh markers on map
                    List<Result> results = response.body().getResults();
                    ((MainActivity)getActivity()).refreshParkingMarkerList();

                    //loop through all places result
                    for (Result r : results) {
//                        Log.i("api", r.getName() + ", " + r.getVicinity() + ", {" + r.getGeometry().getLocation().getLat() + "," + r.getGeometry().getLocation().getLng() + "}");
                        //get place location
                        Location parkingLocation = new Location("");
                        parkingLocation.setLatitude(r.getGeometry().getLocation().getLat());
                        parkingLocation.setLongitude(r.getGeometry().getLocation().getLng());

                        //get distance
                        float distance = currentLocation.distanceTo(parkingLocation);
                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
                        float formattedDistance = Float.valueOf(decimalFormat.format(distance));

                        //convert to miles
                        Double miles = distance*0.000621371192;
                        double formattedMiles = Double.parseDouble(decimalFormat.format(miles));
                        String distanceOutput = formattedDistance + "m | " + formattedMiles + "m";

                        //check if place has been liked
                        ((MainActivity) getActivity()).updateFavouriteItemsList();
                        ArrayList<FavouriteItem> favouriteItems = ((MainActivity) getActivity()).getFavouriteItems();
                        boolean shouldCheck = false;
                        for (FavouriteItem f : favouriteItems) {
                            if (f.getId().equals(r.getPlaceID())) {
                                shouldCheck = true;
                                break;
                            }
                        }

                        //add each place item to search list
                        searchItems.add(new SearchItem(r.getName(), r.getVicinity(), r.getGeometry().getLocation().getLat(), r.getGeometry().getLocation().getLat(), distanceOutput, r.getPlaceID(), shouldCheck));
                        ((MainActivity)getActivity()).addParkingLocation(r.getName(), new LatLng(r.getGeometry().getLocation().getLat(), r.getGeometry().getLocation().getLng()));
                    }

                    //add items to recycler view and update map markers
                    addItemsToRecycler();
                    ((MainActivity)getActivity()).refreshParkingMarkers();

                } else {
                    //unsuccessfull response
                    Log.i("api", "error getting data");
                }
            }

            //API failure
            @Override
            public void onFailure(Call<PlacesResults> call, Throwable t) {
                Log.i("api", t.getMessage());
            }
        });
    }

    /**
     * Function to add list of items to recycler view
     */
    private void addItemsToRecycler() {
        RecyclerView recyclerView = getView().findViewById(R.id.find_parking_search_list_recycler_view);
        FindParkingSearchListRecyclerViewAdapter adapter = new FindParkingSearchListRecyclerViewAdapter(getContext(), searchItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}