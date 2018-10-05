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

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

public class BookingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Fragment locationFragment, reviewFragment, travelPointsFragment, carSelectionFragment;
    FragmentManager fragmentManager;

    Button bPrevious, bNext;

    private static final int MAP_STAGE = 1;
    private static final int CAR_SELECT_STAGE = 2;
    private static final int PAYMENT_STAGE = 3;

    private int bookingStage;

    private int PAYPAL_REQUEST_CODE = 2120;

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

                    DropInRequest dropInUIRequest = new DropInRequest()
                            .clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJmOWUxMmM4ZjI5YWMxZjZiMTJhNzliNGEyOWQzMjQ1YTc4MGZjOTAzYWE2M2U4OTAwNGNlZTA3YjBjNzI5MGU1fGNyZWF0ZWRfYXQ9MjAxOC0xMC0wNVQxODo0OTo1My41ODkxNzkyNjcrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJncmFwaFFMIjp7InVybCI6Imh0dHBzOi8vcGF5bWVudHMuc2FuZGJveC5icmFpbnRyZWUtYXBpLmNvbS9ncmFwaHFsIiwiZGF0ZSI6IjIwMTgtMDUtMDgifSwiY2hhbGxlbmdlcyI6W10sImVudmlyb25tZW50Ijoic2FuZGJveCIsImNsaWVudEFwaVVybCI6Imh0dHBzOi8vYXBpLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb206NDQzL21lcmNoYW50cy8zNDhwazljZ2YzYmd5dzJiL2NsaWVudF9hcGkiLCJhc3NldHNVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbSIsImF1dGhVcmwiOiJodHRwczovL2F1dGgudmVubW8uc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbSIsImFuYWx5dGljcyI6eyJ1cmwiOiJodHRwczovL29yaWdpbi1hbmFseXRpY3Mtc2FuZC5zYW5kYm94LmJyYWludHJlZS1hcGkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=");

                    startActivityForResult(dropInUIRequest.getIntent(this), PAYPAL_REQUEST_CODE);
                } else if(bookingStage == PAYMENT_STAGE) {

                    DialogFragment reviewDialog = new ReviewDialogFragment();
                    reviewDialog.show(fragmentManager, "ReviewDialog");

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PAYPAL_REQUEST_CODE && resultCode == RESULT_OK) {
            DropInResult dropInResult = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

        }
    }
}
