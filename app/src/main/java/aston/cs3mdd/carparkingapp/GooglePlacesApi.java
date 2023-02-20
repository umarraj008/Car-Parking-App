package aston.cs3mdd.carparkingapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * This interface is used for Google Places API.
 * @author Umar Rajput.
 */
public interface GooglePlacesApi {

    @GET("place/nearbysearch/json")
    Call<PlacesResults> getPlaces(
            @Query("location") String location,
            @Query("radius") Integer radius,
            @Query("types") String types,
            @Query("key") String key
    );
}
