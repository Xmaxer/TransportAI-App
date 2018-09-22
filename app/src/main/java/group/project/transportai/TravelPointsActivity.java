package group.project.transportai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TravelPointsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_points);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvTotalPoints = findViewById(R.id.tvTotalTravelPoints);

        tvTotalPoints.setText("236");
    }
}
