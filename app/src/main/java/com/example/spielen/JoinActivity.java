package com.example.spielen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Date;

public class JoinActivity extends AppCompatActivity {

    Button join,leave;
    TextView tv2,tv3,tv4,tv5,tv6;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    String id;
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

        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd/MM/yyyy");


        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        rootRef.collection("events").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()) {
                    Timestamp ts = (Timestamp) doc.getData().get("time");
                    Date date = ts.toDate();
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
                    tv4.setText(dateFormat.format(date) + "  " + timeFormat.format(date));
                    tv5.setText("Players: "+x+ " / "+ doc.getData().get("size"));

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
}
