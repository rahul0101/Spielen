package com.example.spielen.ui.joined;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spielen.AddEventActivity;
import com.example.spielen.Event;
import com.example.spielen.EventAdapter;
import com.example.spielen.JoinedActivity;
import com.example.spielen.MyEventAdapter;
import com.example.spielen.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class JoinedFragment extends Fragment {

    private JoinedViewModel myEventsViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = db.collection("events");
    private MyEventAdapter adapter;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myEventsViewModel =
                ViewModelProviders.of(this).get(JoinedViewModel.class);
        View root = inflater.inflate(R.layout.joined_fragment, container, false);


        Query query = eventsRef.whereArrayContains("players", user.getEmail());
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        adapter = new MyEventAdapter(options);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyEventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String id = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), JoinedActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    
}