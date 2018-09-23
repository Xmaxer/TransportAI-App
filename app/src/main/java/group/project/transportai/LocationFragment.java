package group.project.transportai;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

public class LocationFragment extends Fragment {

    SupportPlaceAutocompleteFragment fromSearch, toSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fromSearch = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.placeAutoCompleteFrom);
        fromSearch.setHint("Pickup From");

        toSearch = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.placeAutoCompleteTo);
        toSearch.setHint("Destination");
    }
}
