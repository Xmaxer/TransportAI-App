package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import group.project.transportai.R;
import objects.PointsEarned;

public class TravelPointsEarnedAdapter extends RecyclerView.Adapter<TravelPointsEarnedAdapter.ViewHolder> {

    private ArrayList<PointsEarned> pointsList = new ArrayList<>();

    private Context context;

    private FirebaseFirestore db;
    private FirebaseUser user;

    public TravelPointsEarnedAdapter(Context context) {

        this.context = context;

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("cars").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    QuerySnapshot querySnap = task.getResult();

                    if(querySnap != null) {
                        for (QueryDocumentSnapshot doc : querySnap) {
                            db.collection("cars").document(doc.getId()).collection("routes")
                                    .whereEqualTo("user_id", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (task.isSuccessful()) {

                                        QuerySnapshot routeSnap = task.getResult();

                                        if (routeSnap != null) {
                                            for (QueryDocumentSnapshot document : routeSnap) {

                                                String amount = document.get("points_gained").toString();
                                                Timestamp date = document.getTimestamp("created_at");

                                                Calendar cal = Calendar.getInstance();

                                                if(date != null) {
                                                    cal.setTime(date.toDate());
                                                }

                                                int day = cal.get(Calendar.DAY_OF_WEEK);
                                                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                                                int month = cal.get(Calendar.MONTH);
                                                int year = cal.get(Calendar.YEAR);

                                                String strDate = getDateString(day, dayOfMonth, month, year);

                                                PointsEarned pointsEarned = new PointsEarned(strDate, amount);

                                                pointsList.add(pointsEarned);
                                            }

                                            notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private String getDateString(int day, int dayOfMonth, int month, int year) {

        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
                        "October", "November", "December"};

        String d = days[day - 1];
        String m = months[month];

        return d + " " + dayOfMonth + " " + m + " " + year;

    }

    @NonNull
    @Override
    public TravelPointsEarnedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.travel_points_list_layout, parent, false);
        return new TravelPointsEarnedAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelPointsEarnedAdapter.ViewHolder holder, int position) {
        PointsEarned point = pointsList.get(position);
        holder.dateMade.setText(point.getDate());
        holder.pointsEarned.setText(point.getPointsGained());
    }

    @Override
    public int getItemCount() {
        return pointsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateMade, pointsEarned;

        ViewHolder(View itemView) {
            super(itemView);

            dateMade = itemView.findViewById(R.id.routeCreated);
            pointsEarned = itemView.findViewById(R.id.pointsearned);

        }
    }
}
