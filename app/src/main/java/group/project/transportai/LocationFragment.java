package group.project.transportai;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.RouteSelectedListener;
import objects.DirectionsJSONParser;

public class LocationFragment extends Fragment implements OnMapReadyCallback {

    private static GoogleMap map;
    private final int MY_LOCATION_PERMISSION = 100;

    private Marker origin, dest;
    private Place pickupPoint, endPoint;

    private static Polyline routeLine;

    private static double dist;

    private static RouteSelectedListener routeSelectedListener;

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
                if (origin != null) {
                    origin.remove();
                }

                origin = map.addMarker(new MarkerOptions().position(place.getLatLng()).title("Origin"));
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                map.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                pickupPoint = place;

                if(endPoint != null) {

                    if(routeLine != null) {
                        routeLine.remove();
                    }

                    drawRoute(pickupPoint.getLatLng(), endPoint.getLatLng());

                    routeSelectedListener.onRouteSelected(pickupPoint.getName().toString(), endPoint.getName().toString());

                    postCoOrdsToDatabase();
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

                if (dest != null) {
                    dest.remove();
                }

                dest = map.addMarker(new MarkerOptions().position(place.getLatLng()).title("Destination"));
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                map.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                endPoint = place;

                if(pickupPoint != null) {

                    if(routeLine != null) {
                        routeLine.remove();
                    }

                    drawRoute(pickupPoint.getLatLng(), endPoint.getLatLng());

                    routeSelectedListener.onRouteSelected(pickupPoint.getName().toString(), endPoint.getName().toString());

                    postCoOrdsToDatabase();
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

    private void postCoOrdsToDatabase() {

        Map<String, Object> route = new HashMap<>();
        route.put("origin_latitude", pickupPoint.getLatLng().latitude);
        route.put("origin_longitude", pickupPoint.getLatLng().longitude);
        route.put("destination_latitude", endPoint.getLatLng().latitude);
        route.put("destination_longitude", endPoint.getLatLng().longitude);

        FirebaseFirestore.getInstance().collection("routes").add(route);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (getActivity() != null) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION);
            } else {
                map.setMyLocationEnabled(true);
            }
        }

        map.setMinZoomPreference(8.0f);
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(51.903614, -8.468399)));
    }

    private void drawRoute(LatLng origin, LatLng dest) {

        String url = getDirectionsURL(origin, dest);

        DownloadDirections downloadDir = new DownloadDirections();
        downloadDir.execute(url);

    }

    private String getDirectionsURL(LatLng begin, LatLng end) {

        String origin = "origin=" + begin.latitude + "," + begin.longitude;

        String destination = "destination=" + end.latitude + "," + end.longitude;

        String sensor = "sensor=false";

        String params = origin + "&" + destination + "&" + sensor;

        return "https://maps.googleapis.com/maps/api/directions/json?" +
                params + "&key=AIzaSyDl5UpgvdzRA9pFyjgFHT4yLlIHlAPgXGc";
    }

    private static String downloadUrl(String strUrl) throws IOException {

        String data = "";
        HttpURLConnection urlConnection;

        URL url = new URL(strUrl);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    private static class DownloadDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parser = new ParserTask();
            parser.execute(result);
        }
    }

    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser(jObject);

                // Starts parsing data
                routes = parser.parseRouteData();
                dist = parser.getDistance();

                routeSelectedListener.onDistanceCalculated(dist);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(R.color.routeColour);
            }

            // Drawing polyline in the Google Map for the i-th route
            routeLine = map.addPolyline(lineOptions);
        }
    }
}
