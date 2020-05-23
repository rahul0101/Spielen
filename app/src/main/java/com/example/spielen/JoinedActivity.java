package com.example.spielen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class JoinedActivity extends AppCompatActivity {

    Button buttonMaps, buttonLeave;
    FloatingActionButton buttonWhatsapp, buttonMessage, buttonCall;
    TextView title,textViewNotes,textViewPlayers,textViewHost;
    String id;
    Double lat,lon;
    String phone="";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        lat=0.0; lon=0.0;

        buttonMaps = findViewById(R.id.buttonMaps);
        buttonLeave = findViewById(R.id.buttonLeave);
        buttonCall = findViewById(R.id.btnCall);
        buttonMessage = findViewById(R.id.btnMessage);
        buttonWhatsapp = findViewById(R.id.btnWhatsapp);
        title = findViewById(R.id.textViewHeading);
        textViewHost = findViewById(R.id.textViewHost);
        textViewNotes = findViewById(R.id.textViewNotes);
        textViewPlayers = findViewById(R.id.textViewPlayers);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }


        rootRef.collection("events").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        title.setText(doc.getData().get("name").toString());
                        if(doc.getData().get("note") != null) {
                            textViewNotes.setText(doc.getData().get("note").toString());
                        }
                        ArrayList arrayList = (ArrayList) doc.getData().get("players");
                        int x = arrayList.size() + 1;
                        textViewPlayers.setText(x+ " / "+ doc.getData().get("size"));
                        rootRef.collection("user_data").document(doc.getData().get("host").toString()).get().addOnCompleteListener(
                                new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot ds = task.getResult();
                                            if(ds.exists()) {
                                                phone = ds.getData().get("phone").toString();
                                                textViewHost.setText("Contact "+ds.getData().get("name").toString());
                                                buttonCall.setEnabled(true);
                                                buttonMessage.setEnabled(true);
                                                buttonWhatsapp.setEnabled(true);
                                            }
                                        }
                                    }
                                }
                        );
                        GeoPoint geoPoint = (GeoPoint) doc.getData().get("location");
                        lat = geoPoint.getLatitude();
                        lon = geoPoint.getLongitude();
                        buttonMaps.setEnabled(true);
                    }
                }
            }
        });

        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mapUri = Uri.parse("geo:0,0?q="+lat+","+lon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        buttonLeave.setOnClickListener(new View.OnClickListener() {
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

        buttonWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String text = "";// Replace with your message.

                    String toNumber = "91"+phone; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                    //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        buttonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:91" + phone));
                startActivity(intent);
            }
        });

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:91"+phone));
                startActivity(intent);
            }
        });
    }
}
