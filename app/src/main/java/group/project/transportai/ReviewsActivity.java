package group.project.transportai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import adapters.ReviewListAdapter;

public class ReviewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView rvReviewsList = (RecyclerView) findViewById(R.id.rvReviewsList);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext());

        rvReviewsList.setLayoutManager(linearLayout);

        DividerItemDecoration itemDivider = new DividerItemDecoration(rvReviewsList.getContext(), linearLayout.getOrientation());

        rvReviewsList.addItemDecoration(itemDivider);
        rvReviewsList.setItemAnimator(new DefaultItemAnimator());

        ReviewListAdapter reviewAdapter = new ReviewListAdapter();
        rvReviewsList.setAdapter(reviewAdapter);
    }
}
