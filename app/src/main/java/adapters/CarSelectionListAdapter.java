package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import interfaces.CarSelectedListener;
import objects.Car;

public class CarSelectionListAdapter extends RecyclerView.Adapter<CarSelectionListAdapter.ViewHolder> {

    private ArrayList<Car> carList = new ArrayList<>();
    private int selectedItem = -1;

    private CarSelectedListener carSelectedListener;

    public CarSelectionListAdapter(CarSelectedListener carSelectedListener) {

        this.carSelectedListener = carSelectedListener;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cars").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        String make = document.get("make").toString();
                        String model = document.get("model").toString();
                        String regNo = document.getId();
                        int status = Integer.parseInt(document.get("status").toString());

                        Car car = new Car(make, model, regNo, status);
                        carList.add(car);
                    }

                    notifyDataSetChanged();
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
        holder.carModel.setText(car.toString());
        holder.carRegNo.setText(car.getRegNumber());
        holder.selected.setChecked(position == selectedItem);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView carModel, carRegNo;
        RadioButton selected;

        ViewHolder(View itemView) {
            super(itemView);

            View.OnClickListener l = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    selectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, carList.size());
                    carSelectedListener.onCarSelected(carList.get(selectedItem).toString());
                }
            };

            selected = itemView.findViewById(R.id.rbCarSelectButton);
            carModel = itemView.findViewById(R.id.tvCarModel);
            carRegNo = itemView.findViewById(R.id.tvCarRegNo);

            selected.setOnClickListener(l);
            itemView.setOnClickListener(l);
        }
    }
}
