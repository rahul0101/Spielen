package com.example.spielen;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventAdapter extends FirestoreRecyclerAdapter<Event, EventAdapter.EventHolder> {
    public  OnItemClickListener listener;

    public EventAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull Event model) {
        Date date = model.getTime().toDate();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd/MM/yyyy");
        holder.textViewDate.setText(dateFormat.format(date));
        holder.textViewTime.setText(timeFormat.format(date));
        holder.textViewName.setText(model.getName());
        if(model.getName()!=null) {
            if (model.getName().toLowerCase().equals("football")) {
                holder.rl.setBackgroundResource(R.drawable.football);
            } else if (model.getName().toLowerCase().equals("cricket")) {
                holder.rl.setBackgroundResource(R.drawable.cricket);
            } else if (model.getName().toLowerCase().equals("tennis")) {
                holder.rl.setBackgroundResource(R.drawable.tennis);
            } else if (model.getName().toLowerCase().equals("table tennis")) {
                holder.rl.setBackgroundResource(R.drawable.tabletennis);
            }
        }
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new EventHolder(v);
    }

    class EventHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewTime, textViewDate;
        RelativeLayout rl;

        public EventHolder(View view){
            super(view);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewTime = view.findViewById(R.id.textViewTime);
            textViewName = view.findViewById(R.id.textViewEventName);
            rl = view.findViewById(R.id.relativeLayout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
