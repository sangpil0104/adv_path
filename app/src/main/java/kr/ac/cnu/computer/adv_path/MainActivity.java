package kr.ac.cnu.computer.adv_path;

import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String API_KEY = "AIzaSyBDYkDmrf6EI2kU8km4wNEMke_KsRU6sOI";
    private int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private List<Marker> markers = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(), API_KEY);
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



    }
    private float calculateLocationDifference(LatLng lastLocation, LatLng firstLocation) {
        float[] dist = new float[1];
        Location.distanceBetween(lastLocation.latitude, lastLocation.longitude, firstLocation.latitude, firstLocation.longitude, dist);
        return dist[0];
    }
    public void MoveCamera(List<Place> locations){
        double top = 0;
        double bottom = Integer.MAX_VALUE;
        double left = Integer.MAX_VALUE;
        double right = 0;
        for(Place location : locations){
            top = Math.max(top, location.geometry.location.lng); //고위도
            bottom = Math.min(bottom, location.geometry.location.lng); //저위도
            left = Math.min(left, location.geometry.location.lat);
            right = Math.max(right, location.geometry.location.lat);
        }
        LatLngBounds latLngBounds = new LatLngBounds(new LatLng(left, bottom), new LatLng(right, top));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 100);
        mMap.moveCamera(cameraUpdate);

    }

    public void getNearBy(LatLng latLng) throws IOException {
        markers = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        NearByRetrofit retrofitService = retrofit.create(NearByRetrofit.class);
        Call<NearByResponse> call = retrofitService.getPosts(latLng.latitude+","+latLng.longitude, 1500, "park", API_KEY, "ko");
        call.enqueue(new Callback<NearByResponse>() {
            @Override
            public void onResponse(Call<NearByResponse> call, Response<NearByResponse> response) {
                if(response.body() != null){
                    List<Place> placeList = response.body().results;
                    for(Place place : placeList){
                        Marker park = mMap.addMarker(new MarkerOptions()
                                .position(place.geometry.location.getLatLng()).title(place.name));
                        markers.add(park);
                    }
                    MoveCamera(placeList);
                }else {

                }
            }

            @Override
            public void onFailure(Call<NearByResponse> call, Throwable t) {

            }
        });

    }




    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            getMyLocation();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public void getMyLocation(){
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(),
                                location.getLongitude()), 17));
                try {
                    getNearBy( new LatLng(location.getLatitude(),
                            location.getLongitude()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        getMyLocation();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                try {
                    getNearBy(latLng);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}