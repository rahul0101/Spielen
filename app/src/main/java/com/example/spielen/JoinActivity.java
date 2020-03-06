package com.example.spielen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class JoinActivity extends AppCompatActivity {

    Button join,leave;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join);

        join = findViewById(R.id.buttonJoin);
        leave = findViewById(R.id.buttonLeave);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.collection("events").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                if(doc.getData().get("host").toString().equals(user.getEmail())) {
                                    Toast.makeText(getApplicationContext(), "You are the host", Toast.LENGTH_SHORT).show();
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
