package com.example.spielen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class EventAdapter extends FirestoreRecyclerAdapter<Event, EventAdapter.EventHolder> {

    public EventAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull Event model) {
        holder.textViewDate.setText(model.getDate());
        holder.textViewTime.setText(model.getTime());
        holder.textViewName.setText(model.getName());
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new EventHolder(v);
    }

    class EventHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewTime, textViewDate;

        public EventHolder(View view){
            super(view);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewTime = view.findViewById(R.id.textViewTime);
            textViewName = view.findViewById(R.id.textViewEventName);
        }
    }
}
