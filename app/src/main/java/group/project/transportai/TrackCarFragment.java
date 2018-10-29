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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

public class TrackCarFragment extends Fragment implements OnMapReadyCallback {

    private static GoogleMap map;
    private static Marker carMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track_car, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment trackCarMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapTrackCar);
        trackCarMap.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(2.0f);

        new GetCarLocation().execute();
    }

    private static class GetCarLocation extends AsyncTask<Void, GeoPoint, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            while(true) {

                FirebaseFirestore.getInstance().collection("cars").get().
                        addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);

                                    GeoPoint location = (GeoPoint) doc.get("location");

                                    publishProgress(location);

                                }
                            }
                        });

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

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
            map.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        }
    }
}
