package interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface RouteSelectedListener {
    void onRouteSelected(String origin, String destination, LatLng originCoords, LatLng destCoords);

    void onDistanceCalculated(double distance);
}
