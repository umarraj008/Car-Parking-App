package aston.cs3mdd.carparkingapp;

import static com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import aston.cs3mdd.carparkingapp.databinding.ActivityMainBinding;

/**
 * This class is an activity class used for the main activity.
 * @author Umar Rajput.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "asd";
    private static final String PROFILES = "PROFILE_DATA.csv";
    private static final String FAVOURITES = "FAVOURITES_DATA.csv";
//    private static final String PARKING = "PARKING_DATA.csv";
//    private static final String SETTINGS = "SETTINGS_DATA.csv";
    public Location currentLocation = null;
    public ArrayList<ProfileItem> profileItems = new ArrayList<>();
    private ArrayList<FavouriteItem> favouriteItems = new ArrayList<>();
    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private ArrayList<ParkedMarker> parkingLocations = new ArrayList<>();
    private ArrayList<ParkedMarker> favouriteParkingLocations = new ArrayList<>();
    private GoogleMap parkingGoogleMap;
    public FindParkingTabLayoutAdapter findParkingTabLayoutAdapter;

    /**
     * This function is for when the app first starts.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        //for location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //when app is loaded switch from splash screen to normal
        setTheme(R.style.Theme_CarParkingApp);
        setContentView(binding.getRoot());

        //saveProfileData();
//        ArrayList<ProfileItem> data = loadProfileData();
//        Log.i("asd", Integer.toString(data.size()));
//        for (ProfileItem p : data) {
//            Log.i("asd", p.getId() + "," + p.toString());
//        }

        //Bottom navigation change fragment on button press
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_trackingButton: //tracking button
                    transisionToFragment(new TrackingPage(), "inst", "Tracking", false, false);
                    break;

                case R.id.bottom_navigation_findParkingButton: //find parking button
                    transisionToFragment(new FindParkingPage(), "inst", "Find Parking", false, false);
                    break;

                case R.id.bottom_navigation_profileButton: //profile button
                    transisionToFragment(new ProfilePage(), "inst", "Profiles", false, false);
                    break;

                case R.id.bottom_navigation_settingsButton: //settings button
                    transisionToFragment(new SettingsPage(), "inst", "Settings", false, false);
                    break;

                default:
                    break;
            }
            return true;
        });

        //permissions settings button
        Button permissionSettingsButton = findViewById(R.id.location_access_button);
        permissionSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        //relaunch app button
        Button relaunchAppButton = findViewById(R.id.location_access_button2);
        relaunchAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        //location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.i(TAG, "Location Callback");
                if (locationResult == null) {
                    Log.i(TAG, "Location Update: NO LOCATION");
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    Log.i(TAG, "Location Update: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
                    currentLocation = location;
                }
            }
        };

        //Get location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "Location permissions have not been granted");

            //no permissions granted so ask for permissions
            askPermissions();
        } else {
            //permissions already granted
            Log.i(TAG, "Location permissions already granted.");
            getLastLocation();
        }
    }

    /**
     * Function for when the app is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();

        //start location updates
        Log.i(TAG, "Location Updates Resumed.");
        startLocationUpdates();
    }

    /**
     * Function to ask for location permissions.
     */
    private void askPermissions() {
        //location permission request
        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

            Boolean fineLocationGranted = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            }

            Boolean coarseLocationGranted = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
            }

            //if location perms not granted show text asking for perms
            if (fineLocationGranted != null && fineLocationGranted) {
                Log.i(TAG, "Precise location access granted.");
                findViewById(R.id.location_access_text).setVisibility(View.GONE);
                findViewById(R.id.location_access_button).setVisibility(View.GONE);
                findViewById(R.id.location_access_button2).setVisibility(View.GONE);
                findViewById(R.id.bottom_navigation_trackingButton).setEnabled(true);
                findViewById(R.id.bottom_navigation_findParkingButton).setEnabled(true);
                findViewById(R.id.bottom_navigation_profileButton).setEnabled(true);
                findViewById(R.id.bottom_navigation_settingsButton).setEnabled(true);
                this.getLastLocation();
            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                Log.i(TAG, "Only approximate location access granted.");
                findViewById(R.id.location_access_text).setVisibility(View.GONE);
                findViewById(R.id.location_access_button).setVisibility(View.GONE);
                findViewById(R.id.location_access_button2).setVisibility(View.GONE);
                findViewById(R.id.bottom_navigation_trackingButton).setEnabled(true);
                findViewById(R.id.bottom_navigation_findParkingButton).setEnabled(true);
                findViewById(R.id.bottom_navigation_profileButton).setEnabled(true);
                findViewById(R.id.bottom_navigation_settingsButton).setEnabled(true);
                this.getLastLocation();
            } else { //dont show text if perms are granted
                Log.i(TAG, "No location access granted.");
                findViewById(R.id.location_access_text).setVisibility(View.VISIBLE);
                findViewById(R.id.location_access_button).setVisibility(View.VISIBLE);
                findViewById(R.id.location_access_button2).setVisibility(View.VISIBLE);
                findViewById(R.id.bottom_navigation_trackingButton).setEnabled(false);
                findViewById(R.id.bottom_navigation_findParkingButton).setEnabled(false);
                findViewById(R.id.bottom_navigation_profileButton).setEnabled(false);
                findViewById(R.id.bottom_navigation_settingsButton).setEnabled(false);
            }
        });

        //Launch a permission request
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    /**
     * Function for when the app is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();

        //stop location updates
        Log.i(TAG, "Location Updates Paused.");
        stopLocationUpdates();
    }

    /**
     * Function to stop location updates.
     */
    private void stopLocationUpdates() {
        //stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Function to change the current fragment within the main activity.
     * @param goToThisFragment fragment you want to transistion to
     * @param animation style of animation
     * @param titleName name of title that will be displayed at top of app
     * @param addToBackStack if you want to add to back stack
     * @param clearBackStack if you want to clear back stack
     */
    public void transisionToFragment(Fragment goToThisFragment, String animation, String titleName, Boolean addToBackStack, Boolean clearBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        //if clear back stack
        if (clearBackStack) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //if add to back stack
        if (addToBackStack) {
            switch (animation) {
                case "L-R": //animation from left to right
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.from_left_to_right, R.anim.exit_left_to_right, R.anim.from_right_to_left, R.anim.exit_right_to_left)
                            .replace(R.id.frame_layout, goToThisFragment)
                            .addToBackStack(null)
                            .commit();
                    break;

                case "R-L": //animation from right to left
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.from_right_to_left, R.anim.exit_right_to_left, R.anim.from_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.frame_layout, goToThisFragment)
                            .addToBackStack(null)
                            .commit();
                    break;

                case "inst": //animation instant
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, goToThisFragment)
                            .addToBackStack(null)
                            .commit();
                    break;

                default:
                    break;

            }
        } else { //if not add to back stack
            switch (animation) {
                case "L-R": //animation from left to right
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.from_left_to_right, R.anim.exit_left_to_right, R.anim.from_right_to_left, R.anim.exit_right_to_left)
                            .replace(R.id.frame_layout, goToThisFragment)
                            .commit();
                    break;

                case "R-L": //animation from right to left
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.from_right_to_left, R.anim.exit_right_to_left, R.anim.from_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.frame_layout, goToThisFragment)
                            .commit();
                    break;

                case "inst": //animation instant
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, goToThisFragment)
                            .commit();
                    break;

                default:
                    break;

            }
        }

        //change title
        setTitle(titleName);
    }

    /**
     * Function to get the last location of the user
     */
    public void getLastLocation() {
        //permissions check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //get current location of user
        fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //if successful then update the current location
                    Log.i(TAG, "Current location: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
                    currentLocation = location;
                } else {
                    //else log error
                    Log.i(TAG, "Failed to get a last location");
                }
            }
        });

        //change current fragment to display tracking page and start location updates
        transisionToFragment(new TrackingPage(), "inst", "Tracking", false, false);
        startLocationUpdates();
    }

    /**
     * Function to start location updates.
     */
    private void startLocationUpdates() {
        //create location request
        if (locationRequest == null) {
            createLocationRequest();
        }

//        Log.i(TAG, "line 160: " + locationRequest.toString() + ", " + locationCallback.toString() );

        //permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

//        Log.i(TAG, "line 168: " + locationRequest.toString() + ", " + locationCallback.toString() );

        //location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Function to create location request.
     */
    protected void createLocationRequest() {
//        Log.i(TAG, "line 174: CreateLocationRequest called" );
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000);
    }

    /**
     * Function to save profile list.
     */
    public void saveProfileData() {
        //open file output stream
        FileOutputStream fileOutputStream = null;

        //convert profile list to CSV data
        ArrayList<ProfileItem> inputData = profileItems;
        StringBuilder data = new StringBuilder();
        ArrayList<String> dataList = new ArrayList<>();
        for (ProfileItem p : inputData) dataList.add(p.toString() + "\n");
        for (String s : dataList) data.append(s);

        //write data to file
        try {
            fileOutputStream = openFileOutput(PROFILES, MODE_PRIVATE);
            fileOutputStream.write(data.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close file output stream
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Function to load profile list.
     * @return list of profiles.
     */
    public ArrayList<ProfileItem> loadProfileData() {
        //create file input stream
        ArrayList<ProfileItem> output = new ArrayList<>();
        FileInputStream fileInputStream = null;

        //load data
        try {
            fileInputStream = openFileInput(PROFILES);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            //convert CSV data lines to profile item objects
            int ind = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                output.add(new ProfileItem(ind, data[0], data[1], Integer.parseInt(data[2]), data[3], data[4]));
                ind++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close file input stream
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //return profile list
        return output;
    }

    /**
     * Function to save favourite parking list.
     */
    public void saveFavouriteData() {
        //create file output stream
        FileOutputStream fileOutputStream = null;

        //convert favourites list to CSV data
        ArrayList<FavouriteItem> inputData = favouriteItems;
        StringBuilder data = new StringBuilder();
        ArrayList<String> dataList = new ArrayList<>();
        for (FavouriteItem p : inputData) dataList.add(p.toString() + "\n");
        for (String s : dataList) data.append(s);

        //write data to files
        try {
            fileOutputStream = openFileOutput(FAVOURITES, MODE_PRIVATE);
            fileOutputStream.write(data.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close file output stream
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Function to load favourites data
     * @return
     */
    public ArrayList<FavouriteItem> loadFavouriteData() {
        //create file input stream
        ArrayList<FavouriteItem> output = new ArrayList<>();
        FileInputStream fileInputStream = null;

        //load data
        try {
            fileInputStream = openFileInput(FAVOURITES);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            //convert CSV data lines to favourite item objects
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                output.add(new FavouriteItem(data[0], data[1], data[2], data[3]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close file input stream
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //return favourite items list
        return output;
    }

    /**
     * Function to reload and update favourite items list.
     */
    public void updateFavouriteItemsList() {
        favouriteItems = loadFavouriteData();
    }

    /**
     * Function to get favourite items list.
     * @return list of favourite items.
     */
    public ArrayList<FavouriteItem> getFavouriteItems() {
        return favouriteItems;
    }

    /**
     * Function to add an item to favourites.
     * @param id Google Places ID of parking spot.
     * @param name of parking spot.
     * @param address of parking spot.
     * @param location of parking spot.
     */
    public void addToFavourites(String id, String name, String address, String location) {
        //create favourite item object
        FavouriteItem item = new FavouriteItem(id, name, address, location);

        //add object to list
        this.favouriteItems.add(item);
        Log.i("api", Integer.toString(favouriteItems.size()));
    }

    /**
     * Function to remove item from favourites.
     * @param id index of item in list.
     */
    public void removeFromFavourites(String id) {
        //find item in list and remove it
        for (int i = 0; i < favouriteItems.size(); i++) {
            if (favouriteItems.get(i).getId().equals(id)) {
                this.favouriteItems.remove(i);
                break;
            }
        }
    }

    /**
     * Function to reload and update profile items list.
     */
    public void updateProfileItemsList() {
        profileItems = loadProfileData();
    }

    /**
     * Function to create a new profile.
     * @param name of profile.
     * @param carType of profile.
     * @param image of profile.
     */
    public void createProfile(String name, String carType, int image) {
        this.profileItems.add(new ProfileItem(profileItems.size(), name, carType, image, "null", "null"));
    }

    /**
     * Function to delete profile.
     * @param id index of profile item in list.
     */
    public void deleteProfile(int id) {
        profileItems.remove(id);
    }

    /**
     * Function to update profile item data in list.
     * @param index of item in list.
     * @param name new name of item.
     * @param carType new carType of item.
     * @param parking new parking location of item.
     * @param notes new notes of item.
     */
    public void updateProfile(int index, String name, String carType, String parking, String notes) {
        profileItems.set(index, new ProfileItem(index, name, carType, 0, parking, notes));
    }

    /**
     * Function to park a profiles car.
     * @param id index of profile in list.
     * @param notes for parking.
     */
    public void setProfileParking(int id, String notes) {
        Location currentLocation = getCurrentLocation();
        profileItems.get(id).setParking(currentLocation.getLatitude() + "#" + currentLocation.getLongitude());
        profileItems.get(id).setNotes(notes);
    }

    /**
     * Function to remove parking for a profile.
     * @param id index of profile in list.
     */
    public void removeProfileParking(int id) {
        profileItems.get(id).setParking("null");
        profileItems.get(id).setNotes("null");
    }

    /**
     * Function to delete all data within the app.
     */
    public void deleteAllData() {
        profileItems = new ArrayList<>();
        favouriteItems = new ArrayList<>();
        saveFavouriteData();
        saveProfileData();
    }

    /**
     * Function to get current location of user.
     * @return
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Function to get profile items list.
     * @return list of profile items.
     */
    public ArrayList<ProfileItem> getProfileItems() {
        return profileItems;
    }

    /**
     * Function to get a profile item within the profile items list.
     * @param id index of profile item.
     * @return profile item within the profile items list.
     */
    public ProfileItem getProfileItem(int id) {
//        Log.i("asd", Integer.toString(id));
//        Log.i("asd", profileItems.get(id).toString());
        return profileItems.get(id);
    }

    /**
     * Function to refresh and re-add all the parking markers on the map.
     */
    public void refreshParkingMarkers() {
        //clear markers
        parkingGoogleMap.clear();

        //add all markers in list to the map
        for (ParkedMarker parking : parkingLocations) {
            parkingGoogleMap.addMarker(new MarkerOptions().position(parking.getLocation()).title(parking.getTitle()));
        }
    }

    /**
     * Function to reset the parking markers list.
     */
    public void refreshParkingMarkerList() {
        parkingLocations = new ArrayList<>();
    }

    /**
     * Function to add a parking location marker to list.
     * @param title of marker.
     * @param location of parking spot.
     */
    public void addParkingLocation(String title, LatLng location) {
        parkingLocations.add(new ParkedMarker(title, location));
    }

    /**
     * Function to add a parking location marker to list.
     * @param title of marker.
     * @param location of parking spot.
     */
    public void addFavouriteParkingLocation(String title, LatLng location) {
        parkingLocations.add(new ParkedMarker(title, location));
    }

    /**
     * Function to refresh all the favourite parking markers on the map.
     */
    public void refreshFavouriteParkingMarkers() {
        //clear markers on the map
        parkingGoogleMap.clear();

        //add all markers from list on the map
        for (ParkedMarker parking : favouriteParkingLocations) {
            parkingGoogleMap.addMarker(new MarkerOptions().position(parking.getLocation()).title(parking.getTitle()));
        }
    }

    /**
     * Function to reset the favourite parking markers list.
     */
    public void refreshFavouriteParkingMarkersList() {
        favouriteParkingLocations = new ArrayList<>();
    }

    /**
     * Function to set the current google map.
     * @param googleMap google map fragment
     */
    public void setGoogleMap(GoogleMap googleMap) {
        parkingGoogleMap = googleMap;
    }

    /**
     * Function to reload the favourites fragment within the find parking page.
     * @param page index of page within the view pager.
     */
    public void reloadFavouritesFragment(int page) {
        ViewPager2 viewPager2 = findViewById(R.id.find_parking_view_pager);
        viewPager2.setAdapter(findParkingTabLayoutAdapter);
        viewPager2.setCurrentItem(page);
    }

    /**
     * Function to move the map camera to the selected parking spot.
     * @param position index of parking spot in list.
     */
    public void moveCameraToParking(int position) {
        LatLng parkingLatLng = parkingLocations.get(position).getLocation();
        parkingGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingLatLng, 15));
    }

    /**
     * Function to move the map camera to the selected favourite parking spot.
     * @param position index of parking spot in list.
     */
    public void moveCameraToFavouriteParking(LatLng position) {
        parkingGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }
}