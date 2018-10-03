package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import group.project.transportai.R;
import objects.Review;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private ArrayList<Review> reviews = new ArrayList<>();

    public ReviewListAdapter() {
        // Test Reviews
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        reviews.add(new Review(Float.parseFloat(document.get("rating").toString()), document.get("comment").toString()));
                        notifyDataSetChanged();
                    }
                }
            }
        });
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
