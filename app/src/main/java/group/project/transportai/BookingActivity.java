package group.project.transportai;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import interfaces.BookingProcessCompleteListener;
import interfaces.CarSelectedListener;
import interfaces.PaymentCompletedListener;
import interfaces.RouteSelectedListener;

public class BookingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, BookingProcessCompleteListener,
        RouteSelectedListener, CarSelectedListener, PaymentCompletedListener {

    private DrawerLayout drawer;

    private Fragment locationFragment, reviewFragment, travelPointsFragment, carSelectionFragment, trackCarFragment;
    private FragmentManager fragmentManager;

    private Button bPrevious, bNext;

    private static final int MAP_STAGE = 1;
    private static final int CAR_SELECT_STAGE = 2;
    private static final int PAYMENT_STAGE = 3;

    private int bookingStage;

    private String origin, destination, carModel;
    private double distance;

    private boolean routeValid, carValid, paymentMade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        bNext = findViewById(R.id.bNext);
        bNext.setOnClickListener(this);

        bPrevious = findViewById(R.id.bPrevious);
        bPrevious.setOnClickListener(this);
        bPrevious.setVisibility(View.INVISIBLE);
        bPrevious.setClickable(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
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
        trackCarFragment = new TrackCarFragment();

        fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, locationFragment).commit();

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
        // Inflate the menu this adds items to the action bar if it is present.
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

                bookingStage = MAP_STAGE;
                break;
            case R.id.nav_myReviews:
                fragment = reviewFragment;

                bPrevious.setVisibility(View.GONE);
                bNext.setVisibility(View.GONE);
                break;
            case R.id.nav_travelPoints:
                fragment = travelPointsFragment;

                bPrevious.setVisibility(View.GONE);
                bNext.setVisibility(View.GONE);
                break;
            case R.id.nav_trackCar:
                fragment = trackCarFragment;

                bPrevious.setVisibility(View.GONE);
                bNext.setVisibility(View.GONE);
                break;
        }

        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, fragment).commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPrevious:
                // TODO Move to previous fragment in booking process, make invisible and un-clickable if going back to LocationFragment

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
                    if(routeValid) {
                        bookingStage = CAR_SELECT_STAGE;

                        fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, carSelectionFragment).commit();

                        bPrevious.setVisibility(View.VISIBLE);
                        bPrevious.setClickable(true);
                    } else {
                        Toast.makeText(this, "Please enter pickup point and destination", Toast.LENGTH_SHORT).show();
                    }
                } else if (bookingStage == CAR_SELECT_STAGE) {
                    if(carValid) {
                        bookingStage = PAYMENT_STAGE;

                        PaymentDetailsFragment payDetailsFragment = new PaymentDetailsFragment();

                        Bundle args = new Bundle();
                        args.putString("Origin", origin);
                        args.putString("Destination", destination);
                        args.putString("CarModel", carModel);
                        args.putDouble("Distance", distance);

                        payDetailsFragment.setArguments(args);

                        fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, payDetailsFragment).commit();
                    } else {
                        Toast.makeText(this, "Please choose a car", Toast.LENGTH_SHORT).show();
                    }
                } else if (bookingStage == PAYMENT_STAGE) {
                    if(paymentMade) {
                        DialogFragment reviewDialog = new ReviewDialogFragment();
                        reviewDialog.show(fragmentManager, "ReviewDialog");
                    } else {
                        Toast.makeText(this, "Please pay", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onBookingComplete() {
        fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, locationFragment).commit();
        bookingStage = MAP_STAGE;
    }

    @Override
    public void onRouteSelected(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
        routeValid = true;
    }

    @Override
    public void onDistanceCalculated(double distance) {
        this.distance = distance;
    }

    @Override
    public void onCarSelected(String carID, String carModel) {
        this.carModel = carModel;
        carValid = true;

        Bundle args = new Bundle();
        args.putString("carID", carID);

        trackCarFragment.setArguments(args);
    }

    @Override
    public void onPaymentCompleted() {
        paymentMade = true;
    }
}
