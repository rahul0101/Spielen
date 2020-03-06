package com.example.spielen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    EditText editTextName, editTextSize, editTextTime, editTextDate, editTextLat, editTextLong;
    Button addButton, mapOpenButton;
    FirebaseUser user;
    FirebaseFirestore rootRef;
    Double lon, lat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        editTextDate = findViewById(R.id.editTextDate);
        editTextName = findViewById(R.id.editText);
        editTextSize = findViewById(R.id.editTextSize);
        editTextTime = findViewById(R.id.editTextTime);
        editTextLat = findViewById(R.id.editTextLatitude);
        editTextLong = findViewById(R.id.editTextLongitude);
        addButton = findViewById(R.id.buttonAdd);
        mapOpenButton = findViewById(R.id.buttonMapOpen);
        rootRef = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> event = new HashMap<>();
                Map<String, String> datetime = new HashMap<>();
                Map<String, String> location = new HashMap<>();
                List<String> players = Arrays.asList();
                GeoPoint geoPoint = new GeoPoint(lat, lon);
                datetime.put(editTextDate.getText().toString(), editTextTime.getText().toString());
                location.put(editTextLat.getText().toString(), editTextLong.getText().toString());
                event.put("name", editTextName.getText().toString());
                event.put("host", user.getEmail());
                event.put("location", geoPoint);
                event.put("time", editTextTime.getText().toString());
                event.put("date", editTextDate.getText().toString());
                event.put("players", players);
                event.put("size", editTextSize.getText().toString());


                rootRef.collection("events").add(event)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(), "Event added!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Please try again later!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        mapOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                lon = data.getDoubleExtra("lon", 74.79347404);
                lat = data.getDoubleExtra("lat", 13.35406421);
                //Toast.makeText(getApplicationContext(), lon.toString() + " " + lat.toString(), Toast.LENGTH_LONG).show();
                addButton.setEnabled(true);
            }
        }
    }
}