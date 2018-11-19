package group.project.transportai;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import adapters.TravelPointsEarnedAdapter;

public class TravelPointsFragment extends Fragment {

    private TextView tvTotalPoints;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.travel_points, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvpointList = view.findViewById(R.id.rvpointsList);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());

        rvpointList.setLayoutManager(linearLayout);

        DividerItemDecoration divider = new DividerItemDecoration(rvpointList.getContext(), linearLayout.getOrientation());

        rvpointList.addItemDecoration(divider);

        rvpointList.setItemAnimator(new DefaultItemAnimator());

        TravelPointsEarnedAdapter pointListAdapter = new TravelPointsEarnedAdapter(getContext());
        rvpointList.setAdapter(pointListAdapter);

        tvTotalPoints = view.findViewById(R.id.tvTotalTravelPoints);
        getTravelPoints();
    }

    private void getTravelPoints() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore travelPointsDB = FirebaseFirestore.getInstance();

        if(currentUser != null) {
            travelPointsDB.collection("users").document(currentUser.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    Object points = task.getResult().get("points");

                    if(points != null) {

                        if (points.equals("")) {
                            tvTotalPoints.setText("0");
                        } else {
                            tvTotalPoints.setText(points.toString());
                        }
                    } else {
                        tvTotalPoints.setText("0");
                    }
                }
            });
        }
    }
}
