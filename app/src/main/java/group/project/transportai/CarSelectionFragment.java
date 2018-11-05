package group.project.transportai;

import android.content.Context;
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
import interfaces.CarSelectedListener;

public class CarSelectionFragment extends Fragment {

    private RecyclerView rvCarSelectList;

    private CarSelectedListener carSelectedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        carSelectedListener = (CarSelectedListener) context;
    }

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

        rvCarSelectList.setLayoutManager(linearLayout);

        DividerItemDecoration divider = new DividerItemDecoration(rvCarSelectList.getContext(), linearLayout.getOrientation());

        rvCarSelectList.addItemDecoration(divider);

        rvCarSelectList.setItemAnimator(new DefaultItemAnimator());

        CarSelectionListAdapter carListAdapter = new CarSelectionListAdapter(getContext(), carSelectedListener);
        rvCarSelectList.setAdapter(carListAdapter);
    }
}
