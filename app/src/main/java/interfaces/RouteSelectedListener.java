package interfaces;

public interface RouteSelectedListener {
    void onRouteSelected(String origin, String destination);

    void onDistanceCalculated(double distance);
}
