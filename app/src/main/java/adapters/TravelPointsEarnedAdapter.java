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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import group.project.transportai.R;
import objects.PointsEarned;

public class TravelPointsEarnedAdapter extends RecyclerView.Adapter<TravelPointsEarnedAdapter.ViewHolder> {

    private ArrayList<PointsEarned> pointsList = new ArrayList<>();

    private Context context;

    public TravelPointsEarnedAdapter(Context context) {

        this.context = context;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("cars").document(user.getUid()).collection("routes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        String amount = document.get("points_gained").toString();
                        String date = document.get("created_at").toString();

                        PointsEarned pointsEarned = new PointsEarned(date, amount);

                        pointsList.add(pointsEarned);
                    }

                    notifyDataSetChanged();
                }

            }
        });
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
