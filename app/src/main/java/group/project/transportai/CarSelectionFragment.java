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

import adapters.CarSelectionListAdapter;

public class CarSelectionFragment extends Fragment {

    private RecyclerView rvCarSelectList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCarSelectList = view.findViewById(R.id.rvCarSelectionList);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());

        DividerItemDecoration divider = new DividerItemDecoration(rvCarSelectList.getContext(), linearLayout.getOrientation());

        rvCarSelectList.addItemDecoration(divider);

        rvCarSelectList.setItemAnimator(new DefaultItemAnimator());

        CarSelectionListAdapter carListAdapter = new CarSelectionListAdapter();
        rvCarSelectList.setAdapter(carListAdapter);
    }
}
