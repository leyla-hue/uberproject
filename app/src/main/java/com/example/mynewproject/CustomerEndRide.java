package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerEndRide extends AppCompatActivity {

    private TextView price;
    private CircleImageView image;
    private RatingBar rating;
    private Button ok,cancel;
    private FirebaseAuth mAuth;
    private String customerId, driverId, historyId, priceNumber, profileImageUrl;
    private DatabaseReference historycurrentinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_end_ride);

        mAuth = FirebaseAuth.getInstance();
        customerId = mAuth.getCurrentUser().getUid();
        historycurrentinfo = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("currentHistoryUid");

        price = (TextView) findViewById(R.id.priceride);
        image = (CircleImageView) findViewById(R.id.imagedriver);
        rating = (RatingBar) findViewById(R.id.ratingride);
        ok = (Button) findViewById(R.id.okride);
        cancel = (Button) findViewById(R.id.cancelride);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historycurrentinfo.removeValue();
                finish();
                return;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historycurrentinfo.removeValue();
                finish();
                return;
            }
        });

        getHistoryAndDriverUid();

    }

    private void getHistoryAndDriverUid() {
        historycurrentinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("driverId") != null) {
                        driverId = map.get("driverId").toString();
                    }
                    if (map.get("historyId") != null) {
                        historyId = map.get("historyId").toString();
                        displayRating();
                    }
                    if (map.get("price") != null) {
                        priceNumber = map.get("price").toString();
                        price.setText(priceNumber+" DA");
                    }
                    if (map.get("profileImageUrl") != null) {
                        profileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(profileImageUrl).into(image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void displayRating() {
        final DatabaseReference historyRideInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(historyId);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                historyRideInfoDb.child("rating").setValue(rating);
                DatabaseReference mDriverRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("rating");
                mDriverRatingDb.child(historyId).setValue(rating);
            }
        });
    }

        }