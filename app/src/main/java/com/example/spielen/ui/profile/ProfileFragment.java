package com.example.spielen.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.spielen.AddEventActivity;
import com.example.spielen.EditProfileActivity;
import com.example.spielen.MainActivity;
import com.example.spielen.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView imageView;
    FloatingActionButton fab;
    private FirebaseAuth mAuth;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    TextView name,age,email,phone;
    Button signOutButton;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        age = root.findViewById(R.id.textViewAge);
        phone = root.findViewById(R.id.textViewPhone);
        name = root.findViewById(R.id.textViewName);
        email = root.findViewById(R.id.textViewEmail);
        fab = root.findViewById(R.id.floatingActionButton);
        signOutButton = root.findViewById(R.id.signOutButton);
        imageView = root.findViewById(R.id.imageViewProfile);

        rootRef.collection("user_data").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        email.setText(user.getEmail());
                        age.setText(doc.getData().get("age").toString());
                        phone.setText(doc.getData().get("phone").toString());
                        name.setText(doc.getData().get("name").toString());
                        //Glide.with(ProfileFragment.this).load("gs://spielen-9b364.appspot.com/"+user.getEmail()+".jpg").into(imageView);
                    }
                }
            }
        });

        StorageReference ref = storageRef.child(user.getEmail()+".jpg");
        Task<Uri> url = ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri url = task.getResult();
//                Toast.makeText(getContext(), url.toString(), Toast.LENGTH_LONG).show();
                Glide.with(ProfileFragment.this).load(url).into(imageView);
            }
        });


        mAuth = FirebaseAuth.getInstance();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            //navigate(currentUser);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rootRef.collection("user_data").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        email.setText(user.getEmail());
                        age.setText(doc.getData().get("age").toString());
                        phone.setText(doc.getData().get("phone").toString());
                        name.setText(doc.getData().get("name").toString());
                    }
                }
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        GoogleSignIn.getClient(
                getContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        Toast.makeText(getActivity(), "Signed out!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getContext(), MainActivity.class));

    }
}