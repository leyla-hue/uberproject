package com.example.mynewproject.historyRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewproject.HistorySingleActivity;
import com.example.mynewproject.R;

public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView rideId;
    public TextView time;
    public ImageView image;
    public TextView destination;
    public TextView pickup;
    public HistoryViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        rideId=(TextView) itemView.findViewById(R.id.rideId);
        time=(TextView) itemView.findViewById(R.id.timeS);
        image=(ImageView) itemView.findViewById(R.id.image_view);
        destination =(TextView) itemView.findViewById(R.id.destinationHistory);
        pickup = (TextView) itemView.findViewById(R.id.pickupHistory);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), HistorySingleActivity.class);
        Bundle b = new Bundle();
        b.putString("rideId", rideId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);

    }
}
