package group.project.transportai;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Fragment locationFragment, reviewFragment, travelPointsFragment, carSelectionFragment;
    FragmentManager fragmentManager;

    Button bPrevious, bNext;

    private static final int MAP_STAGE = 1;
    private static final int CAR_SELECT_STAGE = 2;
    private static final int PAYMENT_STAGE = 3;

    private int bookingStage;

    private static int PAYPAL_REQUEST_CODE = 2120;

    // TODO Put in URL for retrieving token
    private static final String API_GET_TOKEN = "https://ardra.herokuapp.com/braintree/client_token";

    // TODO Add in URL for checkout process
    private static final String API_CHECKOUT = "https://ardra.herokuapp.com/braintree/checkout";

    private static String token;
    private String amount;
    private HashMap<String, String> paramsHashmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        locationFragment = new LocationFragment();
        reviewFragment = new ReviewsFragment();
        travelPointsFragment = new TravelPointsFragment();
        carSelectionFragment = new CarSelectionFragment();

        fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, locationFragment).commit();

        bPrevious = findViewById(R.id.bPrevious);
        bPrevious.setOnClickListener(this);
        bPrevious.setVisibility(View.INVISIBLE);
        bPrevious.setClickable(false);

        bNext = findViewById(R.id.bNext);
        bNext.setOnClickListener(this);

        bookingStage = MAP_STAGE;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.booking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(BookingActivity.this, Main.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_bookRide:
                fragment = locationFragment;

                bPrevious.setVisibility(View.INVISIBLE);
                bPrevious.setClickable(false);

                bNext.setVisibility(View.VISIBLE);
                bNext.setClickable(true);

                bookingStage = MAP_STAGE;
                break;
            case R.id.nav_myReviews:
                fragment = reviewFragment;

                bPrevious.setVisibility(View.INVISIBLE);
                bPrevious.setClickable(false);

                bNext.setVisibility(View.INVISIBLE);
                bNext.setClickable(false);
                break;
            case R.id.nav_travelPoints:
                fragment = travelPointsFragment;

                bPrevious.setVisibility(View.INVISIBLE);
                bPrevious.setClickable(false);

                bNext.setVisibility(View.INVISIBLE);
                bNext.setClickable(false);
                break;
        }

        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, fragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPrevious:
                // TODO Move to previous fragment in booking process, make invisible and unclickable if going back to LocationFragment

                if (bookingStage == CAR_SELECT_STAGE) {
                    bookingStage = MAP_STAGE;

                    fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, locationFragment).commit();

                    bPrevious.setVisibility(View.INVISIBLE);
                    bPrevious.setClickable(false);
                } else if (bookingStage == PAYMENT_STAGE) {
                    bookingStage = CAR_SELECT_STAGE;

                    fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, carSelectionFragment).commit();
                }

                break;
            case R.id.bNext:
                // TODO Move forward to next fragment, make bPrevious visible and clickable to go back
                if (bookingStage == MAP_STAGE) {
                    bookingStage = CAR_SELECT_STAGE;

                    fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, carSelectionFragment).commit();

                    bPrevious.setVisibility(View.VISIBLE);
                    bPrevious.setClickable(true);
                } else if (bookingStage == CAR_SELECT_STAGE) {
                    bookingStage = PAYMENT_STAGE;
                    getPayment();
                } else if (bookingStage == PAYMENT_STAGE) {
                    DialogFragment reviewDialog = new ReviewDialogFragment();
                    reviewDialog.show(fragmentManager, "ReviewDialog");
                }
                break;
        }
    }

    private void getPayment() {
        new getToken().execute();
    }

    private void submitPayment() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE && resultCode == RESULT_OK) {
            DropInResult dropInResult = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            PaymentMethodNonce paymentNonce = dropInResult.getPaymentMethodNonce();

            String strNonce = paymentNonce.getNonce();

            amount = "15.00";

            paramsHashmap = new HashMap<>();
            paramsHashmap.put("amount", amount);
            paramsHashmap.put("payment_method_nonce", strNonce);

            sendPayment();
        }
    }

    private void sendPayment() {

        RequestQueue requestQ = Volley.newRequestQueue(BookingActivity.this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, API_CHECKOUT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Successful")) {
                            Toast.makeText(BookingActivity.this, "Payment Completed", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(BookingActivity.this, "Payment not successful", Toast.LENGTH_LONG).show();
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                for(String key : paramsHashmap.keySet()) {
                    params.put(key, paramsHashmap.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerParams = new HashMap<>();
                headerParams.put("Content-Type", "application/x-www-form-urlencoded");
                return headerParams;
            }
        };

        requestQ.add(strRequest);

    }

    private class getToken extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            HttpClient client = new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {

                @Override
                public void success(String responseBody) {
                    token = responseBody;
                    Log.d("Token", token);
                }

                @Override
                public void failure(Exception exception) {
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DropInRequest dropInRequest = new DropInRequest().clientToken(token);
            startActivityForResult(dropInRequest.getIntent(BookingActivity.this), PAYPAL_REQUEST_CODE);
        }
    }
}
