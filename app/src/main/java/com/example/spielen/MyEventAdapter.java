package com.example.spielen;

import android.graphics.Color;
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
import java.util.Calendar;
import java.util.Date;

public class MyEventAdapter extends FirestoreRecyclerAdapter<Event, MyEventAdapter.MyEventHolder> {
    public  OnItemClickListener listener;

    public MyEventAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyEventHolder holder, int position, @NonNull Event model) {
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
            } else if (model.getName().toLowerCase().equals("basketball")) {
                holder.rl.setBackgroundResource(R.drawable.basketball);
            } else if (model.getName().toLowerCase().equals("volleyball")) {
                holder.rl.setBackgroundResource(R.drawable.volleyball);
            } else if (model.getName().toLowerCase().equals("baseball")) {
                holder.rl.setBackgroundResource(R.drawable.baseball);
            } else if (model.getName().toLowerCase().equals("chess")) {
                holder.rl.setBackgroundResource(R.drawable.chess);
            } else if (model.getName().toLowerCase().equals("badminton")) {
                holder.rl.setBackgroundResource(R.drawable.badminton);
            } else if (model.getName().toLowerCase().equals("hockey")) {
                holder.rl.setBackgroundResource(R.drawable.hockey);
            }
        }

        Timestamp timeNow = new Timestamp(Calendar.getInstance().getTime());

        if(model.getTime().compareTo(timeNow)<0)
        {
            holder.rl.setBackgroundColor(Color.GRAY);
            holder.textViewName.setText(model.getName() + " -Expired");
        }
    }

    @NonNull
    @Override
    public MyEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new MyEventHolder(v);
    }

    class MyEventHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewTime, textViewDate;
        RelativeLayout rl;

        public MyEventHolder(View view){
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
