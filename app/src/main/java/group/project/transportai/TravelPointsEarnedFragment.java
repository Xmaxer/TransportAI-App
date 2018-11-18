package group.project.transportai;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapters.TravelPointsEarnedAdapter;

public class TravelPointsEarnedFragment extends Fragment {

    private RecyclerView rvpointList;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvpointList = view.findViewById(R.id.rvpointsList);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());

        rvpointList.setLayoutManager(linearLayout);

        DividerItemDecoration divider = new DividerItemDecoration(rvpointList.getContext(), linearLayout.getOrientation());

        rvpointList.addItemDecoration(divider);

        rvpointList.setItemAnimator(new DefaultItemAnimator());

        TravelPointsEarnedAdapter pointListAdapter = new TravelPointsEarnedAdapter(getContext());
        rvpointList.setAdapter(pointListAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel_points_earned, container, false);
    }
}
