package group.project.transportai;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class PaymentDetailsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private String origin, destination, carModel;
    private double distance, discountedCost, baseCost;
    private static int PAYPAL_REQUEST_CODE = 2120;

    private static final String API_CHECKOUT = "https://ardra.herokuapp.com/braintree/checkout";

    private HashMap<String, String> paramsHashmap;

    private long currentTravelPoints;
    private int travelPointsEarned;

    private TextView costText;

    private boolean travelPointsUsed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle args = this.getArguments();

        origin = args.getString("Origin");
        destination = args.getString("Destination");
        carModel = args.getString("CarModel");
        distance = args.getDouble("Distance");

        baseCost = round(10 + (distance / 1000));

        travelPointsEarned = (int) Math.ceil(distance / 100);

        return inflater.inflate(R.layout.fragment_payment_details_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView originText = view.findViewById(R.id.tvOriginData);
        originText.setText(origin);

        TextView destText = view.findViewById(R.id.tvDestinationData);
        destText.setText(destination);

        TextView distanceText = view.findViewById(R.id.tvDistanceData);
        distanceText.setText(String.valueOf(distance));

        TextView carModelText = view.findViewById(R.id.tvCarModelData);
        carModelText.setText(carModel);

        costText = view.findViewById(R.id.tvCostData);
        costText.setText(String.valueOf(baseCost));

        CheckBox useTravelPointsCheckBox = view.findViewById(R.id.cbUseTravelPoints);
        useTravelPointsCheckBox.setOnCheckedChangeListener(this);

        Button payButton = view.findViewById(R.id.bPay);
        payButton.setOnClickListener(this);
    }

    private double round(double value) {

        BigDecimal bd = new BigDecimal(String.valueOf(value));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

        return bd.doubleValue();
    }

    @Override
    public void onClick(View v) {
        makePayment();
    }

    private void makePayment() {
        DropInRequest dropIn = new DropInRequest().clientToken("sandbox_b8hbk65s_s2w84n9y8wd4dkpm");
        startActivityForResult(dropIn.getIntent(getActivity()), PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                DropInResult dropInResult = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce paymentNonce = dropInResult.getPaymentMethodNonce();

                String strNonce = paymentNonce.getNonce();

                paramsHashmap = new HashMap<>();
                paramsHashmap.put("amount", String.valueOf(discountedCost));
                paramsHashmap.put("payment_method_nonce", strNonce);

                sendPayment();
            }
        }
    }

    private void sendPayment() {

        RequestQueue requestQ = Volley.newRequestQueue(getActivity());

        StringRequest strRequest = new StringRequest(Request.Method.POST, API_CHECKOUT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Successful")) {
                            Toast.makeText(getActivity(), "Payment Completed", Toast.LENGTH_LONG).show();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Map<String, Object> pointsMap = new HashMap<>();
                            if (travelPointsUsed) {
                                pointsMap.put("points", travelPointsEarned);
                            } else {
                                pointsMap.put("points", currentTravelPoints + travelPointsEarned);
                            }

                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(user.getUid()).set(pointsMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Added your travel points",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Payment Failed", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                for (String key : paramsHashmap.keySet()) {
                    params.put(key, paramsHashmap.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerParams = new HashMap<>();
                headerParams.put("Content-Type", "application/x-www-form-urlencoded");
                return headerParams;
            }
        };

        requestQ.add(strRequest);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

            travelPointsUsed = true;

            FirebaseFirestore.getInstance().collection("points")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        currentTravelPoints = (long) task.getResult().get("points");
                        discountedCost = Math.round(baseCost - currentTravelPoints / 500);
                    }
                }
            });

        } else {
            discountedCost = baseCost;
            travelPointsUsed = false;
        }

        if(discountedCost < 0) {
            discountedCost = 0;
        }

        costText.setText(String.valueOf(discountedCost));
    }
}
