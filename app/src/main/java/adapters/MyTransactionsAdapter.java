package adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import group.project.transportai.R;
import objects.Transaction;

public class MyTransactionsAdapter extends RecyclerView.Adapter<MyTransactionsAdapter.ViewHolder> {

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public MyTransactionsAdapter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("transactions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()) {

                        double cost = Double.parseDouble(doc.get("amount").toString());
                        String paymentMethod = doc.getString("payment_method");
                        int pointsUsed = Integer.parseInt(doc.get("points_used").toString());
                        String date = doc.get("created_at").toString();

                        Transaction transaction  = new Transaction(paymentMethod, cost, pointsUsed, date);
                        transactions.add(transaction);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @NonNull
    @Override
    public MyTransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTransactionsAdapter.ViewHolder holder, int position) {

        Transaction transaction = transactions.get(position);

        holder.cost.setText(String.valueOf(transaction.getCost()));
        holder.date.setText(transaction.getDate());
        holder.pointsUsed.setText(String.valueOf(transaction.getPointsUsed()));
        holder.paymentMethod.setText(transaction.getPaymentMethod());

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView cost, paymentMethod, pointsUsed, date;

        ViewHolder(View itemView) {
            super(itemView);

            cost = itemView.findViewById(R.id.tvTransactionItemCost);
            cost.setTextColor(Color.WHITE);

            paymentMethod = itemView.findViewById(R.id.tvTransactionItemPaymentMethod);
            paymentMethod.setTextColor(Color.WHITE);

            pointsUsed = itemView.findViewById(R.id.tvTransactionItemPointsUsed);
            pointsUsed.setTextColor(Color.WHITE);

            date = itemView.findViewById(R.id.tvTransactionItemDate);
            date.setTextColor(Color.WHITE);
        }
    }
}
