package group.project.transportai;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import utils.MapUtils;

public class TrackCarFragment extends Fragment implements OnMapReadyCallback {

    private static GoogleMap map;
    private static Marker carMarker;

    private boolean canTrack;
    private static String carID;

    private static LatLng origin, dest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();

        if(args != null) {
            carID = args.getString("carID");

            if(carID != null && carID.length() > 0) {

                origin = new LatLng(args.getDouble("originLatitude"), args.getDouble("originLongitude"));
                dest = new LatLng(args.getDouble("destLatitude"), args.getDouble("destLongitude"));

                canTrack = true;
                return inflater.inflate(R.layout.fragment_track_car, container, false);
            }
        }

        return inflater.inflate(R.layout.fragment_track_car_no_booking_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(canTrack) {
            SupportMapFragment trackCarMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapTrackCar);
            trackCarMap.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(2.0f);

        new GetCarLocation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class GetCarLocation extends AsyncTask<Void, GeoPoint, Void> {

        DocumentReference doc;
        int carStatus = -1;
        MapUtils mapUtils;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mapUtils = new MapUtils(map);
            mapUtils.drawRoute(origin, dest);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while(carStatus != 4) {

                doc = FirebaseFirestore.getInstance().collection("cars").document(carID);

                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {

                                    DocumentSnapshot docSnap = task.getResult();

                                    if(docSnap != null) {
                                        GeoPoint location = (GeoPoint) docSnap.get("location");
                                        publishProgress(location);
                                        carStatus = (int) task.getResult().get("status");
                                    }
                                }
                            }
                        });

                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(GeoPoint... values) {
            super.onProgressUpdate(values);

            GeoPoint point = values[0];
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());

            if(carMarker != null) {
                carMarker.remove();
            }

            carMarker = map.addMarker(new MarkerOptions().position(latLng).title("Car Location"));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            FirebaseFirestore.getInstance().collection("cars").document(carID)
                    .update("status", 0);

            mapUtils.removeMarkerAndLine();

        }
    }
}
