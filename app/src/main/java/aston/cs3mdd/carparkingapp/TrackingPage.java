package aston.cs3mdd.carparkingapp;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.maps.android.PolyUtil;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is a fragment class for the tracking page.
 * @author Umar Rajput.
 */
public class TrackingPage extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    public ArrayList<ProfileItem> profileItems;
    private int currentId;
    //    private LatLng parkedLatLng;
//    private String parkedTitle;
    private ArrayList<ParkedMarker> parkedCars = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get updated profile items
        ((MainActivity) getActivity()).updateProfileItemsList();
        this.profileItems = ((MainActivity) getActivity()).getProfileItems();

        //refresh markers on map
        refreshMarkerList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //google map
        MapsInitializer.initialize(getContext(), MapsInitializer.Renderer.LATEST, new OnMapsSdkInitializedCallback() {
            @Override
            public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {

            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracking_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //google map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.tracking_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //profile chips
        ChipGroup chipGroup = getView().findViewById(R.id.tracking_page_chip_group);

        //settings for each profile chip
        for (ProfileItem profileItem : profileItems) {
            Chip chip = new Chip(getContext());

            //set text
            chip.setText(profileItem.getName());

            //onClick method
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //when chip is clicked
                    chipPressed(profileItem.getId());
                }
            });

            //add chips to chipgroup
            chipGroup.addView(chip);
        }

        //set text to default
        TextView profileName = getView().findViewById(R.id.tracking_profile_name);
        TextView carType = getView().findViewById(R.id.tracking_profile_car_type);
        TextView profileStreetLocation = getView().findViewById(R.id.tracking_street_location);
        TextView profileNotes = getView().findViewById(R.id.tracking_page_notes);

        profileName.setText("Current Location:");
        carType.setText("");
        profileStreetLocation.setText("");
        profileNotes.setText("");
//        if (profileItems.size() > 0) {
//            chipPressed(profileItems.get(0).getId());
//        }

        //park it button
        Button parkItButton = getView().findViewById(R.id.tracking_page_park_it_button);
        parkItButton.setOnClickListener(new View.OnClickListener() {
            //when button clicked
            @Override
            public void onClick(View view) {
                //create textbox for popup
                EditText textField = new EditText(getContext());
                textField.setHint("Notes (optional)");
                textField.setInputType(InputType.TYPE_CLASS_TEXT);
                textField.setSingleLine();

                //display alert dialog with textbox
                new AlertDialog.Builder(getContext())
                        .setTitle("Add Parking")
                        .setView(textField)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            //if user confirms dialog
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //start tracking parking and save parking for profile to files
                                String notes = (textField.getText().toString().length() <= 0) ? "null" : textField.getText().toString();
                                ((MainActivity) getActivity()).setProfileParking(currentId, notes);
                                ((MainActivity) getActivity()).saveProfileData();
                                chipPressed(currentId);

                                //display success message
                                Toast toast = Toast.makeText(getContext(), "Parking Added", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

        //stop parking button
        Button stopParkingButton = getView().findViewById(R.id.tracking_page_stop_parking_button);
        stopParkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display alert dialog to warn user
                new AlertDialog.Builder(getContext())
                        .setTitle("Stop Parking")
                        .setMessage("Are you sure you want to stop parking this car?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            //if user confirms dialog
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //stop tracking and remove parking from files
                                ((MainActivity)getActivity()).removeProfileParking(currentId);
                                ((MainActivity)getActivity()).saveProfileData();
                                refreshMarkerList();
                                refreshMarkers();
                                chipPressed(currentId);

                                //display success message
                                Toast toast = Toast.makeText(getContext(), "Parking Removed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        //show directions button
        Button directionsButton = getView().findViewById(R.id.tracking_page_direction_button);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if button text says show directions
                if ("Show Directions".equals(directionsButton.getText())) {
                    //show directions to parking from users location
                    directionsButton.setText("Stop Showing Directions");
                    try {
                        getDirections(parkedCars.get(currentId).getLocation());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //stop showing directions
                    directionsButton.setText("Show Directions");
                    refreshMarkers();
                    chipPressed(currentId);
                }
            }
        });
    }

    /**
     * Function to get directions from current location to destination.
     * @param destination location.
     */
    private void getDirections(LatLng destination) {
        //Google Places Directions API request
        String key = getResources().getString(R.string.google_api_key);
        Location currentLocation = ((MainActivity)getActivity()).getCurrentLocation();
        String location = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        double v1 = destination.latitude;
        double v2 = destination.longitude;
        String dest = v1 + "," + v2;

        //API request
        GoogleDirectionsApi googlePlacesApi = APIClient.getClient().create(GoogleDirectionsApi.class);
        googlePlacesApi.getRoute(location, dest, key).enqueue(new Callback<RouteResult>() {
            //on response
            @Override
            public void onResponse(Call<RouteResult> call, Response<RouteResult> response) {
                //if successful response
                if (response.isSuccessful()) {
                    //get list of results
                    List<DirectionResult> results = response.body().getResults();

                    //decode route path
                    List<LatLng> polyLineList = new ArrayList<>();
                    LatLngBounds bounds = null;
                    for (DirectionResult d : results) {
                        String poly = d.getPolly().getPoints();

                        polyLineList = PolyUtil.decode(poly);
                        LatLng sw = new LatLng(Double.parseDouble(d.getBounds().getSouthwest().getLat()), Double.parseDouble(d.getBounds().getSouthwest().getLng()));
                        LatLng ne = new LatLng(Double.parseDouble(d.getBounds().getNortheast().getLat()), Double.parseDouble(d.getBounds().getNortheast().getLng()));
                        bounds = new LatLngBounds(sw, ne);
                    }

                    //display path on map and move camera to fit path
                    googleMap.addPolyline(new PolylineOptions().addAll(polyLineList).width(3).color(Color.RED));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } else {
                    Log.i("api", "error getting data");
                }
            }

            //on API failure
            @Override
            public void onFailure(Call<RouteResult> call, Throwable t) {
                Log.i("api", t.getMessage());
            }
        });
    }

    /**
     * Function for when the map is loaded.
     * @param gm google map.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap gm) {

        //set google map
        googleMap = gm;

//        Log.i("asd", "test");
        //permissions check
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //google map settings
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        //get current location
        Criteria criteria = new Criteria();
        LocationManager locationManager = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        }

        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        LatLng currentLocationLATLNG = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i("asd", "Found location: " + currentLocationLATLNG.toString());

        //move map camera to current location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLATLNG, 15));

        //get address
//        String streetName = "";
//        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            if (addresses.size() > 0) {
//                streetName = addresses.get(0).getThoroughfare();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //updateAddress(location);

        //display current location address
        TextView carType = getView().findViewById(R.id.tracking_profile_car_type);
        carType.setText(getAddress(currentLocationLATLNG));

        //google map current location button onClick listener
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //move camera to current location
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLATLNG, 15));
                return false;
            }
        });

//        TextView streetLocationText = getView().findViewById(R.id.tracking_street_location);
//        streetLocationText.setText(streetName);

        //refresh all markers on map
        refreshMarkers();
    }

    /**
     * Function to get the address of a location.
     * @param location in LatLnt format.
     * @return the address of the location.
     */
    public String getAddress(LatLng location) {
        //get address
        String streetName = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses.size() > 0) {
                streetName = addresses.get(0).getThoroughfare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return address string
        return streetName;
    }

    /**
     * Function for when a chip is pressed in the chip group.
     * @param id index of the chip inside the chip group.
     */
    public void chipPressed(int id) {
        //get corresponding profile item to chip
        ProfileItem profileItem = ((MainActivity) getActivity()).getProfileItem(id);
        String[] profileItemLocationInput;
        LatLng profileItemLocation;

        //update name textview to profile name
        TextView profileName = getView().findViewById(R.id.tracking_profile_name);
        profileName.setText(profileItem.getName());

        //update cartype textview to profile cartype
        TextView carType = getView().findViewById(R.id.tracking_profile_car_type);
        carType.setText(profileItem.getCarType());

        //update imageview to profile image
        ImageView image = getView().findViewById(R.id.tracking_profile_image);
        image.setImageResource(R.drawable.car_image);

        //upadate the selected profile id
        currentId = id;

        //get parkit button and layout
        Button parkingButton = getView().findViewById(R.id.tracking_page_park_it_button);
        LinearLayout parkedButtonsLayout = getView().findViewById(R.id.tracking_page_parked_buttons_container);

        //if the selected profile is not parked
        if ("null".equals(profileItem.getParking())) {
            //show parkit button
            parkingButton.setVisibility(View.VISIBLE);
            parkingButton.setEnabled(true);
            parkedButtonsLayout.setVisibility(View.INVISIBLE);

            //display empty notes
            TextView notes = getView().findViewById(R.id.tracking_page_notes);
            notes.setText("");

            //get current location
            Criteria criteria = new Criteria();
            LocationManager locationManager = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            }

            String provider = locationManager.getBestProvider(criteria, true);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Location location = locationManager.getLastKnownLocation(provider);
            LatLng currentLocationLATLNG = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("asd", "Found location: " + currentLocationLATLNG.toString());

            //move google map camera to current location
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLATLNG, 15));
        } else { // if profile is parked
            //display parked container (stop parking button and show directions button)
            parkingButton.setVisibility(View.INVISIBLE);
            parkedButtonsLayout.setVisibility(View.VISIBLE);

            //decode parking location
            profileItemLocationInput  = profileItem.getParking().split("#");
            profileItemLocation= new LatLng(Double.parseDouble(profileItemLocationInput[0]), Double.parseDouble(profileItemLocationInput[1]));

            //display address of parking location
            TextView profileStreetLocation = getView().findViewById(R.id.tracking_street_location);
            profileStreetLocation.setText(getAddress(profileItemLocation));

            //display parking notes
            TextView notes = getView().findViewById(R.id.tracking_page_notes);
            if ("null".equals(profileItem.getNotes())) {
                notes.setText("");
            } else {
                notes.setText(profileItem.getNotes());
            }

            //move google map camera to parking location and refresh markers
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(profileItemLocation, 15));
            refreshMarkerList();
            refreshMarkers();
        }

        //reset show directions button text
        Button directionsButton = getView().findViewById(R.id.tracking_page_direction_button);
        if ("Stop Showing Directions".equals(directionsButton.getText())) {
            directionsButton.setText("Show Directions");
        }

    }

    /**
     * Function to refresh markers on map.
     */
    private void refreshMarkers() {
        //clear markers and add all markers from list
        googleMap.clear();
        for (ParkedMarker car : parkedCars) {
            googleMap.addMarker(new MarkerOptions().position(car.getLocation()).title(car.getTitle()));
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkedLatLng, 15));
        }
    }

    /**
     * Function to refresh markers list.
     */
    private void refreshMarkerList() {
        //reset marker list
        parkedCars = new ArrayList<>();

        //add a marker for each profile item to list
        for (ProfileItem p : profileItems) {
            if (!("null".equals(p.getParking()))) {
                String[] data = p.getParking().split("#");
                LatLng location = new LatLng(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
                parkedCars.add(new ParkedMarker(p.getName(), location));
            }
        }
    }
}