package group.project.transportai;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFragment extends Fragment implements OnMapReadyCallback{

    private SupportPlaceAutocompleteFragment fromSearch, toSearch;
    private GoogleMap map;
    private final int MY_LOCATION_PERMISSION = 100;

    private Place origin, dest;
    private float[] distanceResults = new float[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fromSearch = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.placeAutoCompleteFrom);
        fromSearch.setFilter(new AutocompleteFilter.Builder().setCountry("IE").build());
        fromSearch.setHint("Pickup From");

        toSearch = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.placeAutoCompleteTo);
        toSearch.setFilter(new AutocompleteFilter.Builder().setCountry("IE").build());
        toSearch.setHint("Destination");

        fromSearch.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.addMarker(new MarkerOptions().position(place.getLatLng()).title("Origin"));
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                origin = place;
            }

            @Override
            public void onError(Status status) {
                Log.d("LOCATION-FROM-ERROR", "Couldn't get location");
            }
        });

        toSearch.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.addMarker(new MarkerOptions().position(place.getLatLng()).title("Destination"));
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                dest = place;

                Location.distanceBetween(origin.getLatLng().latitude,
                        origin.getLatLng().longitude, dest.getLatLng().latitude, dest.getLatLng().longitude, distanceResults);

                Log.d("Distance between loc", String.valueOf(distanceResults[0]));
            }

            @Override
            public void onError(Status status) {
                Log.d("LOCATION-TO-ERROR", "Couldn't get location");
            }
        });

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION);
        } else {
            map.setMyLocationEnabled(true);
        }

        map.setMinZoomPreference(8.0f);
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(51.903614, -8.468399)));
    }
}
