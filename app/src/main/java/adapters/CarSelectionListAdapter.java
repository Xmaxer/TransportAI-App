package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import group.project.transportai.R;
import objects.Car;

public class CarSelectionListAdapter extends RecyclerView.Adapter<CarSelectionListAdapter.ViewHolder> {

    private ArrayList<Car> carList = new ArrayList<>();

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

        holder.tvCarModel.setText(car.getCarModel());
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCarModel;

        ViewHolder(View itemView) {
            super(itemView);

            tvCarModel = itemView.findViewById(R.id.tvCarModel);
        }
    }
}
