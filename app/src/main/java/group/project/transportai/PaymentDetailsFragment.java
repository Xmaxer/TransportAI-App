package group.project.transportai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import interfaces.PaymentCompletedListener;

import static android.app.Activity.RESULT_OK;

public class PaymentDetailsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private String origin, destination, carModel;
    private double distance, discountedCost;
    private static int PAYPAL_REQUEST_CODE = 2120;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393";
    private PaymentCompletedListener paymentCompletedListener;

    private static final String API_CHECKOUT = "https://ardra.herokuapp.com/braintree/checkout",
            API_CALCULATE_PRICE = "http://www.transport-ai.com/requests/calculate_price";

    private String strNonce, amount, carID, strPaymentMethod;

    private int time;

    private LatLng originCoords, destCoords;

    private HashMap<String, String> paramsHashmap;

    private long currentTravelPoints;
    private int travelPointsEarned, travelPointsUsed;

    private CheckBox useTravelPointsCheckBox;
    private TextView costText;

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        paymentCompletedListener = (PaymentCompletedListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle args = this.getArguments();

        origin = args.getString("Origin");
        destination = args.getString("Destination");
        carID = args.getString("carID");
        carModel = args.getString("CarModel");
        distance = args.getDouble("Distance");
        time = args.getInt("Time");
        originCoords = args.getParcelable("originCoords");
        destCoords = args.getParcelable("destCoords");

        calculateCost(distance / 1000, time);//baseCost = calculateCost(distance/1000, 0); //round(10 + (distance / 1000));

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
        distanceText.setText(String.valueOf(distance / 1000));

        TextView carModelText = view.findViewById(R.id.tvCarModelData);
        carModelText.setText(carModel);

        costText = view.findViewById(R.id.tvCostData);
        costText.setText("...");

        useTravelPointsCheckBox = view.findViewById(R.id.cbUseTravelPoints);
        useTravelPointsCheckBox.setOnCheckedChangeListener(this);

        Button payButton = view.findViewById(R.id.bPay);
        payButton.setOnClickListener(this);
    }

    private void updateBaseCost(double i) {
        costText.setText(String.valueOf(i));
    }

    private void calculateCost(final double distance, final int time) {
        RequestQueue requestQ = Volley.newRequestQueue(context);

        StringRequest strRequest = new StringRequest(Request.Method.GET, API_CALCULATE_PRICE + "?distance=" + distance + "&time=" + time,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        double result = 0;
                        try {
                            result = round(response);
                            amount = String.valueOf(result);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        updateBaseCost(result);
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), R.string.error_calculating_price, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerParams = new HashMap<>();
                headerParams.put("USER-AGENT", USER_AGENT);
                return headerParams;
            }
        };

        requestQ.add(strRequest);
    }

    private double round(String cost) {
        BigDecimal bd = new BigDecimal(cost);
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

                strNonce = paymentNonce.getNonce();
                strPaymentMethod = paymentNonce.getTypeLabel();
                paramsHashmap = new HashMap<>();
                paramsHashmap.put("amount", amount);
                paramsHashmap.put("payment_method_nonce", strNonce);

                sendPayment();
            }
        }
    }

    private void sendPayment() {

        RequestQueue requestQ = Volley.newRequestQueue(context);

        StringRequest strRequest = new StringRequest(Request.Method.POST, API_CHECKOUT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Payment", response);
                        if (response.contains("SuccessfulResult")) {
                            paymentCompletedListener.onPaymentCompleted();
                            Toast.makeText(getActivity(), R.string.paymentCompleted, Toast.LENGTH_LONG).show();

                            Map<String, Object> updatePoints = new HashMap<>();

                            if (useTravelPointsCheckBox.isChecked()) {
                                updatePoints.put("points", currentTravelPoints - travelPointsUsed);
                            } else {
                                updatePoints.put("points", travelPointsEarned);
                            }

                            FirebaseFirestore.getInstance().collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(updatePoints).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), R.string.updatedTravelPoints, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            Map<String, Object> transactionParams = new HashMap<>();
                            transactionParams.put("amount", amount);
                            transactionParams.put("created_at", new Timestamp(new Date()));
                            transactionParams.put("payment_method", strPaymentMethod);
                            transactionParams.put("points_used", travelPointsUsed);

                            FirebaseFirestore.getInstance().collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("transactions")
                                    .add(transactionParams).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(),
                                                "Error sending bill amount to server", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            Map<String, Object> routeParams = new HashMap<>();
                            routeParams.put("car", carID);
                            routeParams.put("completed", false);
                            routeParams.put("created_at", new Timestamp(new Date()));
                            routeParams.put("destination", new GeoPoint(destCoords.latitude, destCoords.longitude));
                            routeParams.put("distance", distance);
                            routeParams.put("origin", new GeoPoint(originCoords.latitude, originCoords.longitude));
                            routeParams.put("points_gained", travelPointsEarned);
                            routeParams.put("time_taken", time);
                            routeParams.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseFirestore.getInstance().collection("routes").add(routeParams);

                        } else {
                            Log.d("Payment", response);
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), R.string.paymentFailed, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
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
        final TaskCompleteCallback callback = new TaskCompleteCallback() {
            @Override
            public void onTaskComplete(double discounted) {
                if (discounted < 0) {
                    discounted = 0;
                }

                costText.setText(String.valueOf(discounted));
            }
        };
        if (isChecked) {

            FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        Object obj = task.getResult().get("points");
                        if(obj == null) {
                            Toast.makeText(getContext(), "You have no points", Toast.LENGTH_LONG).show();
                            return;
                        }
                        currentTravelPoints = (long) obj;

                        double moneyOff = (double) (currentTravelPoints / 20);
                        travelPointsUsed = (int) currentTravelPoints;

                        discountedCost = Math.round(Double.parseDouble(amount) - moneyOff);
                        callback.onTaskComplete(discountedCost);
                    }
                }
            });

        } else {
            discountedCost = Double.parseDouble(amount);
            travelPointsUsed = 0;
            costText.setText(String.valueOf(discountedCost));
        }
    }

    private interface TaskCompleteCallback {
        void onTaskComplete(double discounted);
    }
}
