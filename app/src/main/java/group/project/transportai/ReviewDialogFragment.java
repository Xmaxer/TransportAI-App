package group.project.transportai;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewDialogFragment extends DialogFragment {

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_review_layout, null);

        final EditText editText = view.findViewById(R.id.etEnterReview);
        final RatingBar ratingBar = view.findViewById(R.id.rbEnterRating);
        ratingBar.setNumStars(5);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle("Leave Review").setView(view);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.getText();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Map<String, Object> reviews = new HashMap<>();
                reviews.put("userID", currentUser.getUid());
                reviews.put("rating", ratingBar.getRating());
                reviews.put("comment", editText.getText().toString());
                Log.d("ONCLICK", reviews.toString());
                db.collection("reviews").add(reviews).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });
                dialog.dismiss();
                Toast.makeText(context,"Thank you for your input!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, BookingActivity.class));

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(context, BookingActivity.class));
            }
        });

        return dialog.create();
    }
}
