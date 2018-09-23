package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import group.project.transportai.R;
import objects.Car;

public class CarSelectionListAdapter extends RecyclerView.Adapter<CarSelectionListAdapter.ViewHolder> {

    private ArrayList<Car> carList = new ArrayList<>();
    private int selectedItem = -1;

    public CarSelectionListAdapter() {
        carList.add(new Car("Toyota Corolla"));
        carList.add(new Car("Renault Clio"));
        carList.add(new Car("Ferrari"));
    }

    @Override
    public CarSelectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_selection_list_item_layout, parent, false);

        return new CarSelectionListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CarSelectionListAdapter.ViewHolder holder, int position) {
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
