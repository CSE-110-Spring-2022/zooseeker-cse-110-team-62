/*package com.example.zooseeker_t62;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "FOOBAR";
    public static final String EXTRA_USE_LOCATION_SERVICE = "use_location_updated";

    private boolean useLocationService;

    private GoogleMap map;
    private ActivityMapsBinding binding;

    private LocationModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        // Set up the model.
        model = new ViewModelProvider(this).get(LocationModel.class);

        // If GPS is enabled, then update the model from the Location service.
        if (useLocationService) {
            var permissionChecker = new LocationPermissionChecker(this);
            permissionChecker.ensurePermissions();

            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var provider = LocationManager.GPS_PROVIDER;
            model.addLocationProviderSource(locationManager, provider);
        }
    } */

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    /*
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        initializeMap(map);

        // Observe the model and place a blue pin whenever the location is updated.
        model.getLastKnownCoords().observe(this, (coord) -> {
            Log.i(TAG, String.format("Observing location model update to %s", coord));
            var marker = new MarkerOptions()
                    .position(coord.toLatLng())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Last Known Location");
            map.addMarker(marker);
        });

        // Test the above by mocking movement...
        var route = Coords
                .interpolate(Coords.UCSD, Coords.ZOO, 12)
                .collect(Collectors.toList());

        if (!useLocationService) {
            model.mockRoute(route, 500, TimeUnit.MILLISECONDS);
        }
    }

    private void initializeMap(GoogleMap map) {
        // Enable zoom controls.
        var uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        var start = Coords.UCSD;
        var end = Coords.ZOO;

        // Compute the midpoint between UCSD and the zoo.
        var cameraPosition = Coords
                .midpoint(start, end)
                .toLatLng();

        // Place a marker at the start.
        map.addMarker(new MarkerOptions()
                .position(start.toLatLng())
                .title("Start")
        );

        // Place a marker at the end.
        map.addMarker(new MarkerOptions()
                .position(end.toLatLng())
                .title("End")
        );

        // Move the camera and zoom to the right level.
        map.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition));
        map.moveCamera(CameraUpdateFactory.zoomTo(11.5f));
    }

    @VisibleForTesting
    public void mockLocation(Coord coords) {
        model.mockLocation(coords);
    }

    @VisibleForTesting
    public Future<?> mockRoute(List<Coord> route, long delay, TimeUnit unit) {
        return model.mockRoute(route, delay, unit);
    }
} */

