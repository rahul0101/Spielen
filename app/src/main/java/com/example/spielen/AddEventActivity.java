package com.example.spielen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.squareup.okhttp.internal.http.HttpDate.format;

public class AddEventActivity extends AppCompatActivity {

    EditText editTextName, editTextSize, editTextTime, editTextDate;
    Button addButton, mapOpenButton;
    FirebaseUser user;
    FirebaseFirestore rootRef;
    Double lon, lat;
    Date date;
    public int year, month, day, hr, min;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        editTextDate = findViewById(R.id.editTextDate);
        editTextName = findViewById(R.id.editText);
        editTextSize = findViewById(R.id.editTextSize);
        editTextTime = findViewById(R.id.editTextTime);
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
                date = new Date(year-1900, month, day, hr, min);
                List<String> players = Arrays.asList();
                GeoPoint geoPoint = new GeoPoint(lat, lon);
                datetime.put(editTextDate.getText().toString(), editTextTime.getText().toString());
                //location.put(editTextLat.getText().toString(), editTextLong.getText().toString());
                event.put("name", editTextName.getText().toString());
                event.put("host", user.getEmail());
                event.put("location", geoPoint);
                event.put("time", editTextTime.getText().toString());
                Timestamp timestamp = new Timestamp(date);
                event.put("date", editTextDate.getText().toString());
                event.put("players", players);
                event.put("size", editTextSize.getText().toString());
                event.put("time", timestamp);


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
                                Toast.makeText(getApplicationContext(), "Error adding event! Please try again", Toast.LENGTH_SHORT).show();
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

        editTextTime.setLongClickable(false);
        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        min = selectedMinute;
                        hr = selectedHour;

                        if(selectedHour > 12)
                        {
                            editTextTime.setText(selectedHour-12 + ":" + selectedMinute + " PM");
                        }
                        else if(selectedHour == 12)
                        {
                            editTextTime.setText(selectedHour + ":" + selectedMinute + " PM");
                        }
                        else if(selectedHour == 0)
                        {
                            editTextTime.setText(selectedHour+12 + ":" + selectedMinute + " AM");
                        }
                        else
                        {
                            editTextTime.setText(selectedHour + ":" + selectedMinute + " AM");
                        }

                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        editTextDate.setLongClickable(false);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentDate = Calendar.getInstance();
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker DatePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDay;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
                        String dateString = dateFormat.format(newDate.getTime());
                        //editTextDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(dt));
                        editTextDate.setText(dateString);
                    }
                }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));

                mDatePicker.setTitle("Select Date");
                mDatePicker.show();

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