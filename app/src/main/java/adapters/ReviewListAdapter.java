package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import group.project.transportai.R;
import objects.Review;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private ArrayList<Review> reviews = new ArrayList<>();

    public ReviewListAdapter() {
        // Test Reviews

        reviews.add(new Review(4, "Excellent service"));
        reviews.add(new Review(1, "Very poor car ride, took me to the wrong place"));
        reviews.add(new Review(3, "Could have been better but not bad"));
    }

    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item_layout, parent, false);

        return new ReviewListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewListAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.rating.setRating(review.getRating());
        holder.reviewText.setText(review.getReviewText());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar rating;
        TextView reviewText;

        ViewHolder(View itemView) {
            super(itemView);

            rating = itemView.findViewById(R.id.reviewItemRatingBar);
            rating.setNumStars(5);

            reviewText = itemView.findViewById(R.id.tvReviewTextView);
        }

    }
}
