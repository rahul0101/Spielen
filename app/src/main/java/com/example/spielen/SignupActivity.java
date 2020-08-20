package com.example.spielen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    String gender[] = {"Select Gender", "Male", "Female", "Other"};
    EditText editTextName, editTextPhone, editTextAge;
    Button buttonNext, buttonUpload;
    Spinner spinnerGender;
    FirebaseUser user;
    FirebaseFirestore rootRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://spielen-d8519.appspot.com");
    Uri filePath;
    final int PICK_IMAGE_REQUEST = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAge = findViewById(R.id.editTextAge);
        spinnerGender = findViewById(R.id.spinner);
        buttonNext = findViewById(R.id.buttonNext);
        buttonUpload = findViewById(R.id.buttonUpload);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, gender)
        {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(arrayAdapter);

        rootRef = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextName.getText().toString().length() != 0 && editTextAge.getText().toString().length()!= 0 && editTextPhone.getText().toString().length()==10 && spinnerGender.getSelectedItem() != null && !spinnerGender.getSelectedItem().toString().equals("Select Gender"))
                {
                    Map<String, Object> userdata = new HashMap<>();
                    userdata.put("name", editTextName.getText().toString());
                    userdata.put("phone", editTextPhone.getText().toString());
                    userdata.put("gender", spinnerGender.getSelectedItem().toString());
                    userdata.put("age", Integer.valueOf(editTextAge.getText().toString()));

                    rootRef.collection("user_data").document(user.getEmail())
                            .set(userdata)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    uploadImage();
                                    //startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Please try again later!", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter valid details!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

    }

    protected void uploadImage()
    {
        if(filePath != null) {
            StorageReference childRef = storageRef.child(user.getEmail()+".jpg");

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(getApplicationContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
            startActivity(new Intent(SignupActivity.this, HomeActivity.class));
        }
        else {
            Toast.makeText(getApplicationContext(), "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Toast.makeText(getApplicationContext(), "Image selected!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
