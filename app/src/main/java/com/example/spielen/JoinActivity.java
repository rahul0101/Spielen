package com.example.spielen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class JoinActivity extends AppCompatActivity {

    Button join, leave;
    TextView tv2, tv3, tv4, tv5, tv6, tv7;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    String id;
    Date date;
    String CHANNEL_ID= "SpielenRemindChID" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join);

        join = findViewById(R.id.buttonJoin);
        leave = findViewById(R.id.buttonLeave);
        leave.setVisibility(View.GONE);
        tv2 = findViewById(R.id.textView2);
        tv3 = findViewById(R.id.textView3);
        tv4 = findViewById(R.id.textView4);
        tv5 = findViewById(R.id.textView5);
        tv6 = findViewById(R.id.textView6);
        tv7 = findViewById(R.id.textView7);

        final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd/MM/yyyy");


        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        rootRef.collection("events").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()) {
                    Timestamp ts = (Timestamp) doc.getData().get("time");
                    date = ts.toDate();
                    ArrayList arrayList = (ArrayList) doc.getData().get("players");
                    int x = arrayList.size() + 1;
                    rootRef.collection("user_data").document(doc.getData().get("host").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot sd = task.getResult();
                            if(sd.exists()) {
                                tv2.setText(sd.getData().get("name").toString());
                            }
                        }
                    });
                    tv3.setText(doc.getData().get("name").toString());
                    tv4.setText(dateFormat.format(date)) ;
                    tv7.setText(timeFormat.format(date));
                    tv5.setText(x+ " / "+ doc.getData().get("size"));

                }
            }
        });



        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.collection("events").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                ArrayList arrayList = (ArrayList) doc.getData().get("players");
                                int x = arrayList.size();
                                if(doc.getData().get("host").toString().equals(user.getEmail())) {
                                    Toast.makeText(getApplicationContext(), "You are the host", Toast.LENGTH_SHORT).show();
                                }
                                else if(arrayList.contains(user.getEmail())) {
                                    Toast.makeText(getApplicationContext(), "You have already joined this event", Toast.LENGTH_SHORT).show();
                                }
                                else if(x+1 == Integer.parseInt(doc.getData().get("size").toString())){
                                    Toast.makeText(getApplicationContext(), "Event Full!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    rootRef.collection("events").document(id)
                                            .update("players", FieldValue.arrayUnion(user.getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Joined Event!", Toast.LENGTH_SHORT).show();

                                            setAlarm();

                                            finish();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.collection("events").document(id)
                        .update("players", FieldValue.arrayRemove(user.getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Left Event!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

    }

    private void setAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
        final PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
                intent, 0);

        Date currentTime = Calendar.getInstance().getTime();

        long delay = date.getTime() - currentTime.getTime() - 7200000;

        createNotificationChannel();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                final int notificationID = (int)System.currentTimeMillis();
                NotificationCompat.Builder n = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setContentTitle("Event Reminder")
                        .setContentText("You have an event in 2 hours!")
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .addAction(android.R.drawable.ic_btn_speak_now, "Open App", pIntent);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setSmallIcon(R.drawable.spielen_logo_transparent);
                    n.setColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    n.setSmallIcon(R.drawable.spielen_logo);
                }

                NotificationManagerCompat notification = NotificationManagerCompat.from(getApplicationContext());
                notification.notify(notificationID, n.build());
            }
        }, delay);
        Toast.makeText(getApplicationContext(), String.valueOf(delay) , Toast.LENGTH_SHORT).show();


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
