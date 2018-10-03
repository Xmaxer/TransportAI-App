package group.project.transportai;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewDialogFragment extends DialogFragment {

    private AlertDialog.Builder dialog;
    private Context context;
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_review_layout, null);

        final EditText editText = view.findViewById(R.id.etEnterReview);
        final RatingBar ratingBar = view.findViewById(R.id.rbEnterRating);
        ratingBar.setNumStars(5);

        dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle("Leave Review").setView(view);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.getText();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> reviews = new HashMap<>();
                reviews.put("rating", ratingBar.getRating());
                reviews.put("comment", editText.getText().toString());
                Log.d("ONCLICK", reviews.toString());
                db.collection("reviews").add(reviews).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Thank you for your review!", Toast.LENGTH_SHORT);
                        //TODO Go back to the main screen (flBookingScreenArea)
                    }
                });

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return dialog.create();
    }
}
