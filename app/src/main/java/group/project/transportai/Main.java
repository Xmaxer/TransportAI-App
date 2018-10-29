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

    private static final int SIGN_IN_REQUEST_CODE = 1100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            startActivity(new Intent(Main.this, BookingActivity.class));
            finish();
        }

        Button bSignIn = findViewById(R.id.bSignIn);
        bSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bSignIn:

                List<AuthUI.IdpConfig> signInProviders = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.TwitterBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().setPermissions(Arrays.asList("email", "public_profile", "user_friends")).build());

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
                    finish();
                }
            }
        }
    }
}
