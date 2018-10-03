package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import group.project.transportai.R;
import objects.Car;

public class CarSelectionListAdapter extends RecyclerView.Adapter<CarSelectionListAdapter.ViewHolder> {

    private ArrayList<Car> carList = new ArrayList<>();
    private int selectedItem = -1;

    public CarSelectionListAdapter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cars").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Car car = new Car(document.get("model").toString());
                        carList.add(car);
                        notifyDataSetChanged();
                    }
                }

            }
        });
    }

    @NonNull
    @Override
    public CarSelectionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_selection_list_item_layout, parent, false);

        return new CarSelectionListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarSelectionListAdapter.ViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.carModel.setText(car.getCarModel());
        holder.selected.setChecked(position == selectedItem);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView carModel;
        RadioButton selected;

        ViewHolder(View itemView) {
            super(itemView);

            View.OnClickListener l = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    selectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, carList.size());
                }
            };

            selected = itemView.findViewById(R.id.rbCarSelectButton);
            carModel = itemView.findViewById(R.id.tvCarModel);

            selected.setOnClickListener(l);
            itemView.setOnClickListener(l);
        }
    }
}
