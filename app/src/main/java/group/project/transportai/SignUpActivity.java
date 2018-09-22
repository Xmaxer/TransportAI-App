package group.project.transportai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button bSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bSignUp = (Button) findViewById(R.id.bSignUpScreenButton);
        bSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Eventually this will send sign up details to database before going to booking activity
        Intent openBookingActivity = new Intent(SignUpActivity.this, BookingActivity.class);
        startActivity(openBookingActivity);
    }
}