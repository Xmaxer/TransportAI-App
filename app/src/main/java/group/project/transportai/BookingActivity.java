package group.project.transportai;

import android.content.Intent;
import android.os.Bundle;
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

public class BookingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Fragment locationFragment, reviewFragment, travelPointsFragment, carSelectionFragment, paymentFragment;
    FragmentManager fragmentManager;

    Button bPrevious, bNext;

    private static final int MAP_STAGE = 1;
    private static final int CAR_SELECT_STAGE = 2;
    private static final int PAYMENT_STAGE = 3;

    private int bookingStage;


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
        paymentFragment = new PaymentFragment();

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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;

        switch(item.getItemId()) {
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

        if(fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, fragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bPrevious:
                // TODO Move to previous fragment in booking process, make invisible and unclickable if going back to LocationFragment

                if(bookingStage == CAR_SELECT_STAGE) {
                    bookingStage = MAP_STAGE;

                    fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, locationFragment).commit();

                    bPrevious.setVisibility(View.INVISIBLE);
                    bPrevious.setClickable(false);
                } else if(bookingStage == PAYMENT_STAGE) {
                    bookingStage = CAR_SELECT_STAGE;

                    fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, carSelectionFragment).commit();
                }

                break;
            case R.id.bNext:
                // TODO Move forward to next fragment, make bPrevious visible and clickable to go back
                if(bookingStage == MAP_STAGE) {
                    bookingStage = CAR_SELECT_STAGE;

                    fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, carSelectionFragment).commit();

                    bPrevious.setVisibility(View.VISIBLE);
                    bPrevious.setClickable(true);
                } else if(bookingStage == CAR_SELECT_STAGE) {
                    bookingStage = PAYMENT_STAGE;

                    fragmentManager.beginTransaction().replace(R.id.flBookingScreenArea, paymentFragment).commit();
                } else if(bookingStage == PAYMENT_STAGE) {

                    DialogFragment reviewDialog = new ReviewDialogFragment();
                    reviewDialog.show(fragmentManager, "ReviewDialog");

                }
                break;
        }
    }
}
