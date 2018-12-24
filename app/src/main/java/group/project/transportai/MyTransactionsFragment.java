package group.project.transportai;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapters.MyTransactionsAdapter;

public class MyTransactionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvMyTransactions = view.findViewById(R.id.rvMyTransactions);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());

        rvMyTransactions.setLayoutManager(linearLayout);

        DividerItemDecoration divider = new DividerItemDecoration(rvMyTransactions.getContext(), linearLayout.getOrientation());

        rvMyTransactions.addItemDecoration(divider);

        rvMyTransactions.setItemAnimator(new DefaultItemAnimator());

        MyTransactionsAdapter adapter = new MyTransactionsAdapter();
        rvMyTransactions.setAdapter(adapter);
    }
}
