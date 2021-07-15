package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class driverNotification extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase, mDriverLocation, mCustomerLocation;
    private TextView textTime, textDistance, textAddress;
    String userId="", customerId="",destination;
    private LatLng destinationLatLng, pickupLatLng;
    private Button go;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_notification);

        textTime = (TextView) findViewById(R.id.txtTime);
        textDistance = (TextView) findViewById(R.id.txtDistance);
        textAddress = (TextView) findViewById(R.id.txtAdress);
        go = (Button) findViewById(R.id.go);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        mDriverLocation = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(userId).child("l");
        mCustomerLocation = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end();
                finish();
                return;
            }
        });

        getDestinationInfo();
    }

    private void end() {
        driverLocationRef.removeEventListener(driverLocationRefListener);
        assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        textDistance.setText("");
        textTime.setText("");
        textAddress.setText("");
    }

    private void getDestinationInfo() {
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    double destinationLat = 0;
                    double destinationLng = 0;

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("destination") != null) {
                        destination = map.get("destination").toString();
                        textAddress.setText(destination);
                    }
                    if(map.get("destinationLat")!=null){
                        destinationLat = Double.parseDouble(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLng")!=null){
                        destinationLng = Double.parseDouble(map.get("destinationLng").toString());
                    }
                    if(map.get("customerRideId")!=null){
                        customerId = map.get("customerRideId").toString();
                    }
                    destinationLatLng = new LatLng(destinationLat,destinationLng);

                    getAssignedCustomerPickupLocation();
                    getDriverLocation();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private DatabaseReference assignedCustomerPickupLocationRef;
    private  ValueEventListener assignedCustomerPickupLocationRefListener;
    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat,locationLng);
                    getDriverLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(userId).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map =(List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;

                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);

                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLatLng.latitude);
                    loc1.setLongitude(pickupLatLng.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
                    int speedIs1KmMinute = 100;
                    float estimatedDriveTimeInMinutes = distance / speedIs1KmMinute;
                    int time = Math.round(estimatedDriveTimeInMinutes);
                    textTime.setText(time+" min");

                    if(distance>1000){
                        distance = distance/1000;

                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(1);
                        String str = df.format(distance);

                        textDistance.setText(str+" km");
                    }
                    else{
                        int distanceInt = Math.round(distance);
                        textDistance.setText(distanceInt+" m");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}

