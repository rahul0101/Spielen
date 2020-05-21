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
import com.google.android.gms.tasks.OnSuccessListener;
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

public class HostedActivity extends AppCompatActivity {

    Button deleteEvent;
    TextView tv3, tv4, tv5, tv6, tv7;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    String id;
    Date date;
    String CHANNEL_ID= "SpielenRemindChID" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hosted);

        deleteEvent = findViewById(R.id.buttonDelete);
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
                    tv3.setText(doc.getData().get("name").toString());
                    tv4.setText(dateFormat.format(date)) ;
                    tv7.setText(timeFormat.format(date));
                    tv5.setText(x+ " / "+ doc.getData().get("size"));

                }
            }
        });

        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.collection("events").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Event deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
}
