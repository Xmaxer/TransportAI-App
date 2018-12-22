package services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import group.project.transportai.R;

public class TransportAICloudMessagingNotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()) {
                    Map<String, Object> tokenParams = new HashMap<>();
                    tokenParams.put("messaging_token", task.getResult().getToken());

                    FirebaseFirestore.getInstance().collection("users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(tokenParams, SetOptions.merge());
                }
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().isEmpty()) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else {
            showNotification(remoteMessage.getData());
        }
    }

    private void showNotification(Map<String, String> data) {
        showNotification(data.get("title"), data.get("body"));
    }

    private void showNotification(String title, String body) {

        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIFICATION_CHANNEL_ID = "TransportAI";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Ardra", NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("TransportAI Notification Channel");

            notifManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ardra_logo_round);

        notifManager.notify(23,  builder.build());

    }
}
