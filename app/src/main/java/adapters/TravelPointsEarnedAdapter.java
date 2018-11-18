package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import group.project.transportai.R;
import interfaces.CarSelectedListener;
import objects.Car;
import objects.GlideApp;
import objects.Points;

public class TravelPointsEarnedAdapter extends RecyclerView.Adapter<TravelPointsEarnedAdapter.ViewHolder> {

    private ArrayList<String> pointsList = new ArrayList<>();
    private int selectedItem = -1;


    private Context context;

    public TravelPointsEarnedAdapter(Context context) {

        this.context = context;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("routes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        String amount = document.get("points_earned").toString();
                        String date = document.get("created_at").toString();


                        pointsList.add(date);
                        pointsList.add(amount);
                    }

                    notifyDataSetChanged();
                }

            }
        });
    }

    @NonNull
    @Override
    public TravelPointsEarnedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_points_list_layout, parent, false);

        return new TravelPointsEarnedAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelPointsEarnedAdapter.ViewHolder holder, int position) {
        String point = pointsList.get(position);
        holder.dateMade.setText(point.toString());
        holder.pointsEarned.setText(point.toString());

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
