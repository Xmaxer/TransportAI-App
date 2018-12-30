package group.project.transportai;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import interfaces.BookingProcessCompleteListener;

public class EnterReviewFragment extends Fragment implements View.OnClickListener {

    private BookingProcessCompleteListener bookingCompleteListener;

    private RatingBar rating;
    private EditText reviewComment;

    private String carID, routeID;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bookingCompleteListener = (BookingProcessCompleteListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();

        if(args != null) {
            this.carID = args.getString("carID");
            this.routeID = args.getString("routeID");
        }

        return inflater.inflate(R.layout.fragment_review_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rating = view.findViewById(R.id.rbEnterRating);
        rating.setNumStars(5);

        reviewComment = view.findViewById(R.id.etEnterReview);
        Button postReview = view.findViewById(R.id.bPostReview);
        postReview.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final float customerRating = rating.getRating();
        final String customerComment = reviewComment.getText().toString();

        if (customerRating == 0 || customerComment.equals("")) {
            Toast.makeText(getActivity(), R.string.please_enter_review, Toast.LENGTH_LONG).show();
        } else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Timestamp reviewCreated = new Timestamp(new Date());

            Map<String, Object> reviewParams = new HashMap<>();
            reviewParams.put("comment", customerComment);
            reviewParams.put("rating", customerRating);
            reviewParams.put("created_at", reviewCreated);
            reviewParams.put("car", carID);
            reviewParams.put("route_id", routeID);

            if(user != null) {
                FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                        .collection("reviews").add(reviewParams)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getContext(), R.string.error_posting_review, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Review Posted", Toast.LENGTH_LONG).show();
                                    reviewComment.setText("");
                                    rating.setRating(0.0f);
                                    bookingCompleteListener.onBookingComplete();
                                }
                            }
                        });
            }
        }
    }
}
