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

import adapters.ReviewListAdapter;

public class ReviewsFragment extends Fragment {

    RecyclerView rvReviewList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvReviewList = view.findViewById(R.id.rvReviewsList);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());

        rvReviewList.setLayoutManager(linearLayout);

        DividerItemDecoration divider = new DividerItemDecoration(rvReviewList.getContext(), linearLayout.getOrientation());

        rvReviewList.addItemDecoration(divider);

        rvReviewList.setItemAnimator(new DefaultItemAnimator());

        ReviewListAdapter reviewAdapter = new ReviewListAdapter();
        rvReviewList.setAdapter(reviewAdapter);
    }
}
