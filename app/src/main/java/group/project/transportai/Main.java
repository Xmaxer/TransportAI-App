package group.project.transportai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Main extends AppCompatActivity implements View.OnClickListener {

    private EditText etEnterEmail, etEnterPassword;
    private Button bSignIn, bSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEnterEmail = findViewById(R.id.etSignInEnterEmail);
        etEnterPassword = findViewById(R.id.etSignInEnterPassword);

        bSignIn = findViewById(R.id.bSignIn);
        bSignIn.setOnClickListener(this);

        bSignUp = findViewById(R.id.bSignUp);
        bSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bSignIn:
                // Add functionality for signing in
                Intent testMapsActivity = new Intent(Main.this, MapsActivity.class);
                startActivity(testMapsActivity);
                break;
            case R.id.bSignUp:
                Intent openSignUpActivity = new Intent(Main.this, SignUpActivity.class);
                startActivity(openSignUpActivity);
                break;
        }
    }
}
