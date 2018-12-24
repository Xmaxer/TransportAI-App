package group.project.transportai;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import interfaces.RouteSelectedListener;
import utils.MapUtils;

public class LocationFragment extends Fragment implements OnMapReadyCallback {

    private Place pickupPoint, endPoint;

    private static RouteSelectedListener routeSelectedListener;

    private MapUtils mapUtils;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        routeSelectedListener = (RouteSelectedListener) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SupportPlaceAutocompleteFragment fromSearch = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.placeAutoCompleteFrom);
        fromSearch.setFilter(new AutocompleteFilter.Builder().setCountry("IE").build());
        fromSearch.setHint("Pickup From");

        SupportPlaceAutocompleteFragment toSearch = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.placeAutoCompleteTo);
        toSearch.setFilter(new AutocompleteFilter.Builder().setCountry("IE").build());
        toSearch.setHint("Destination");

        fromSearch.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                pickupPoint = place;

                if(endPoint != null) {
                    mapUtils.drawRoute(pickupPoint.getLatLng(), endPoint.getLatLng());
                    routeSelectedListener.onRouteSelected(pickupPoint.getName().toString(),
                            endPoint.getName().toString(), place.getLatLng(), endPoint.getLatLng());
                }
            }

            @Override
            public void onError(Status status) {
                Log.d("LOCATION-FROM-ERROR", "Couldn't get location");
            }
        });

        toSearch.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                endPoint = place;

                if(pickupPoint != null) {
                    mapUtils.drawRoute(pickupPoint.getLatLng(), endPoint.getLatLng());
                    routeSelectedListener.onRouteSelected(pickupPoint.getName().toString(),
                            endPoint.getName().toString(), pickupPoint.getLatLng(), place.getLatLng());
                }
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
        googleMap.setMinZoomPreference(8.0f);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(51.903614, -8.468399)));
        mapUtils = new MapUtils(googleMap, routeSelectedListener);
    }
}
