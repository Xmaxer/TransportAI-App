package utils;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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

import group.project.transportai.R;
import interfaces.RouteSelectedListener;

public class MapUtils {

    private static RouteSelectedListener routeSelectedListener;

    private static GoogleMap map;

    private static Polyline routeLine;

    private Marker originMarker, destMarker;

    public MapUtils(GoogleMap map) {
        MapUtils.map = map;
    }

    public MapUtils(GoogleMap map, RouteSelectedListener routeSelectedListener) {
        MapUtils.map = map;
        MapUtils.routeSelectedListener = routeSelectedListener;
    }

    public void removeMarkerAndLine() {

        if(routeLine != null) {
            routeLine.remove();
        }

        if (originMarker != null) {
            originMarker.remove();
        }

        if(destMarker != null) {
            destMarker.remove();
        }
    }

    public void drawRoute(LatLng origin, LatLng dest) {

        removeMarkerAndLine();

        originMarker = map.addMarker(new MarkerOptions().position(origin).title("Origin"));
        destMarker = map.addMarker(new MarkerOptions().position(dest).title("Destination"));

        map.moveCamera(CameraUpdateFactory.newLatLng(origin));
        map.animateCamera(CameraUpdateFactory.zoomTo(16.0f));

        String url = getDirectionsURL(origin, dest);

        DownloadDirections downloadDir = new DownloadDirections();
        downloadDir.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

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
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
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
                double dist = parser.getDistance();
                int time = parser.getTime();

                if(routeSelectedListener != null) {
                    routeSelectedListener.onJourneyCalculated(dist, time);
                }

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
