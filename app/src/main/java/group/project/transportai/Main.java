package group.project.transportai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class Main extends AppCompatActivity implements View.OnClickListener {

    Button bSignIn;

    private static final int SIGN_IN_REQUEST_CODE = 1100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bSignIn = findViewById(R.id.bSignIn);
        bSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bSignIn:
                // Add functionality for signing in

                //TODO Uncomment the below section and comment out this line ONLY WHEN TESTING LOGINS OR FOR FINAL BUILD
                //I just can't bother logging in everytime
                startActivity(new Intent(Main.this, BookingActivity.class));

                List<AuthUI.IdpConfig> signInProviders = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.TwitterBuilder().build());

                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(signInProviders).build(), SIGN_IN_REQUEST_CODE);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null) {
                    startActivity(new Intent(Main.this, BookingActivity.class));
                }
            }
        }
    }
}
