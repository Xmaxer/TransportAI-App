package group.project.transportai;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SupportFragment extends Fragment implements View.OnClickListener {

    private EditText supportTitle, supportBody;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supprt_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        supportTitle = view.findViewById(R.id.etSupportTitle);
        supportBody = view.findViewById(R.id.etSupportBody);

        Button sendSupportReq = view.findViewById(R.id.bSendSupportRequest);
        sendSupportReq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String title = supportTitle.getText().toString();
        String body = supportBody.getText().toString();

        if(title.equals("") || body.equals("")) {
            Toast.makeText(getContext(), "Please enter a title and body", Toast.LENGTH_LONG).show();
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(user != null) {

                Map<String, Object> supportData = new HashMap<>();
                supportData.put("title", title);
                supportData.put("body", body);

                FirebaseFirestore.getInstance().collection("users")
                        .document(user.getUid()).collection("messages")
                        .add(supportData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Support request sent successfully", Toast.LENGTH_LONG).show();
                            supportTitle.setText("");
                            supportBody.setText("");
                        }
                    }
                });
            }
        }
    }
}
