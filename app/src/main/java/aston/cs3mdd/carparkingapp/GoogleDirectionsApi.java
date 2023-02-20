package aston.cs3mdd.carparkingapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * This interface is used for Google Places Directions API.
 * @author Umar Rajput.
 */
public interface GoogleDirectionsApi {

    @GET("directions/json")
    Call<RouteResult> getRoute(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String key
    );
}
