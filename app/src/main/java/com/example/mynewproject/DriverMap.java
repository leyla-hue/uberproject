package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.google.firebase.storage.FileDownloadTask;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location nLastLocation;
    LocationRequest nLocationRequest;
    private  SupportMapFragment mapFragment;
    private Button mRideStatus;
    private ImageButton button;
    private String customerId = "", destination, pickup;
    private LatLng destinationLatLng;
    private Boolean isLoggingOut = false;
    private LinearLayout mCustomerInfo;
    private CircleImageView mCustomerProfileImage;
    private TextView mCustomerName, mCustomerPhone, mCustomerDestination, mNavName, mCustomerPickup;
    private String mName, call,imageurl;
    private int status = 0;
    private DrawerLayout drawerLayout;
    private Switch mWorkingSwitch;
    private BottomSheetBehavior mBottomSheetBehavior;
    private LatLng pickupLatLng;
    private float rideDistance;
    private ImageView btcall;
    private static final int REQUEST_CALL = 1;
    private Marker mCurrent;
    private Marker destinationMarker;
    private Marker pickupMarker;
    private TextView distance, price, time;
    private FirebaseAuth mAuth;
    private String service;
    private float distanceB=0, distanceA;
    private TextView navName,navPseudo;
    private CircleImageView navImage;
    private NavigationView mNavigationView;
    private int totalPrice;
    private int globalPrice;
    private View mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != (PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            ActivityCompat.requestPermissions(DriverMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else{
        mapFragment.getMapAsync(this);
        }

        polylines = new ArrayList<>();

        mCustomerInfo = (LinearLayout) findViewById(R.id.CustomerInfo);
        mCustomerProfileImage = (CircleImageView) findViewById(R.id.customerProfileImage);
        mCustomerName= (TextView) findViewById(R.id.customerName);
        mCustomerPhone= (TextView) findViewById(R.id.customerPhone);
        mCustomerDestination= (TextView) findViewById(R.id.customerDestination);
        mCustomerPickup = (TextView) findViewById(R.id.customerPickup);
        mNavName = (TextView) findViewById(R.id.nav_name);
        btcall = (ImageView) findViewById(R.id.bt_call);

        distance = (TextView) findViewById(R.id.distance);
        price = (TextView) findViewById(R.id.price);
        time = (TextView) findViewById(R.id.time);

        View bottomSheet = findViewById(R.id.bottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        mRideStatus =(Button) findViewById(R.id.rideStatus);
        mWorkingSwitch = (Switch) findViewById(R.id.working);
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    connectDriver();

                }else{
                    disconnectDriver();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status){
                    case 1:
                        status=2;
                        erasePolylines();

                        mRideStatus.setText("drive complete");
                        if(!customerId.equals("")&& pickupMarker!=null){
                            pickupMarker.remove();
                            destinationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).position(new LatLng(destinationLatLng.latitude, destinationLatLng.longitude)).title("Destination"));
                        }

                        break;

                    case 2:
                        button.setVisibility(View.GONE);
                        if(pickupMarker!=null){
                            pickupMarker.remove();
                        }
                        if(destinationMarker!=null){
                            destinationMarker.remove();
                        }
                        recordRide();
                        endRide();
                        break;
                }
            }
        });

        destinationLatLng = new LatLng(0,0);

        mapView = mapFragment.getView();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        navName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_name);
        navPseudo = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.navpseudo);
        navImage = (CircleImageView) mNavigationView.getHeaderView(0).findViewById(R.id.navpicture);
        getCurrentUserInfo();

        getDriverServiceInfo();
        getAssignedCustomer();

    }




    private void getCurrentUserInfo(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentInfo = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(currentUserId);
        currentInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("pseudo") != null) {
                        navPseudo.setText("Hi, "+map.get("pseudo").toString());
                    }
                    if (map.get("name") != null) {
                        navName.setText(map.get("name").toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        imageurl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(imageurl).into(navImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private BitmapDescriptor bitmapDescriiptorFromVector(Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getMinimumWidth(), vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_setting:
                Intent intent = new Intent(DriverMap.this,DriverSetting.class);
                startActivity(intent);
                break;

            case R.id.nav_logout:
                isLoggingOut=true;
                disconnectDriver();
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(DriverMap.this,firstActivity.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.nav_history:
                Intent intent3 = new Intent(DriverMap.this,History.class);
                intent3.putExtra("customerOrDriver","Drivers");
                startActivity(intent3);
                break;

            case R.id.nav_password:
                Intent intent4 = new Intent(DriverMap.this,ResetPassword.class);
                intent4.putExtra("customerOrDriver","Drivers");
                startActivity(intent4);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

    public void getDriverServiceInfo(){
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId);
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("service")!=null){
                        service = map.get("service").toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    status =1;
                    customerId = dataSnapshot.getValue().toString();

                    getDriverServiceInfo();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private String getCompleteAdress(double latitude, double longitude){
        String address ="";
        Geocoder geocoder = new Geocoder(DriverMap.this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
            if(address!=null){
                Address returnAdress = addresses.get(0);
                StringBuilder stringBuilderReturnAddress = new StringBuilder("");
                for(int i=0;i<=returnAdress.getMaxAddressLineIndex();i++){
                    stringBuilderReturnAddress.append(returnAdress.getAddressLine(i)).append("\n");
                }
                address = stringBuilderReturnAddress.toString();
            }
            else{
                Toast.makeText(this,"Address not found",Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return address;
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
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.drawable.locationcustomer32)));


                    mCustomerPickup.setText(getCompleteAdress(pickupLatLng.latitude,pickupLatLng.longitude));
                    pickup = getCompleteAdress(pickupLatLng.latitude,pickupLatLng.longitude);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getRouteToMarker(LatLng pickupLatLng, LatLng pickupLatLngEnd) {
        if(pickupLatLng != null && nLastLocation != null) {
            Routing routing = new Routing.Builder().key("AIzaSyChfIz5scL-6OEYQgFsicWUoNZI9mvue6U")
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(pickupLatLng,pickupLatLngEnd)
                    .build();
            routing.execute();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private  void endRide() {
        mRideStatus.setText("picked customer");
        erasePolylines();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);

        customerId = "";
        rideDistance = 0;
        destination=null;

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if(destinationMarker!=null){
            destinationMarker.remove();
        }

        if(assignedCustomerPickupLocationRefListener != null){
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        button.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerPickup.setText("");
        destination=null;
        distanceB=0;
        time.setText("0min");
        distance.setText("0km");
        price.setText("0DA");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.mipmap.ic_defaultuser);

    }


    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");

        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver",userId);
        map.put("customer",customerId);
        map.put("rating",0);
        map.put("timeStamp",getCurrentTimestamp());
        map.put("destination",destination);
        map.put("pickup",pickup);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("location/to/lat", destinationLatLng.latitude);
        map.put("location/to/lng", destinationLatLng.longitude);
        map.put("distance", rideDistance);
        map.put("price",totalPrice);
        historyRef.child(requestId).updateChildren(map);


        DatabaseReference currentHistoryUid = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("currentHistoryUid");
        HashMap map1 = new HashMap();
        map1.put("driverId",userId);
        map1.put("historyId",requestId);
        map1.put("price",totalPrice);
        map1.put("profileImageUrl",imageurl);
        currentHistoryUid.updateChildren(map1);

    }


    private Long getCurrentTimestamp() {
        Long timeStamp = System.currentTimeMillis()/1000;
        return  timeStamp;
    }


    private void getAssignedCustomerDestination(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination") != null){
                        destination= map.get("destination").toString();
                        mCustomerDestination.setText(destination);
                    }
                    else{
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;

                    if(map.get("destinationLat") != null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());

                    }
                    if(map.get("destinationLng") != null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat,destinationLng);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getAssignedCustomerInfo(){
        button.setVisibility(View.VISIBLE);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mCustomerInfo.setVisibility(View.VISIBLE);

        Intent intent = new Intent(DriverMap.this,driverNotification.class);
        startActivity(intent);

        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("pseudo") != null){
                        mCustomerName.setText(map.get("pseudo").toString());

                    }

                    if(map.get("phone") != null){
                        mCustomerPhone.setText(map.get("phone").toString());
                        call = mCustomerPhone.getText().toString();
                    }

                    if(map.get("profileImageUrl") != null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                    }

                    btcall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makePhoneCall();
                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void makePhoneCall(){
        if(call.trim().length() > 0){
            if(ContextCompat.checkSelfPermission(DriverMap.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(DriverMap.this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }else{
                String dial = "tel:"+call;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }


        }else {
            Toast.makeText(DriverMap.this,"Don't found phone number", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != (PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            ActivityCompat.requestPermissions(DriverMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        }
         buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setMyLocationButtonEnabled(true);
        mapSettings.setZoomControlsEnabled(true);

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {

            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            View zoomControl = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("3"));

            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams)
                    zoomControl.getLayoutParams();

            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 180, 180, 0);

            // position on right bottom
            layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams2.setMargins(0, 0, 180, 0);

        }

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplication()!=null) {

            if(!customerId.equals("")){
                rideDistance += nLastLocation.distanceTo(location);


                globalPrice = Math.round((rideDistance* 5) / 100);
                if (service.equals("CarX")) {
                    totalPrice = globalPrice + 100;
                    price.setText(totalPrice + "DA");
                }
                if (service.equals("CarXL")) {
                    totalPrice = globalPrice + 200;
                    price.setText(totalPrice + "DA");
                }
                if (service.equals("CarBlack")) {
                    totalPrice = globalPrice + 500;
                    price.setText(totalPrice + "DA");
                }
                if(pickupLatLng!=null) {
                    getDistanceBetweenPickupDestination();
                }

            }else{
                erasePolylines();
            }

            nLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(mCurrent != null){
                mCurrent.remove();
            }
            mCurrent = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.carfromtop32px)).position((latLng)).title("YOU"));
            rotateMarker(mCurrent,-360,mMap);


            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);

            switch (customerId){
                    case "":

                        geoFireWorking.removeLocation(userId);
                        geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;

                    default:
                        geoFireAvailable.removeLocation(userId);
                        geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;
            }

        }
    }

    public void getDistanceBetweenPickupDestination(){
        Location loc1 = new Location("");
        loc1.setLatitude(pickupLatLng.latitude);
        loc1.setLongitude(pickupLatLng.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(nLastLocation.getLatitude());
        loc2.setLongitude(nLastLocation.getLongitude());


        if(destinationLatLng.latitude != 0 && destinationLatLng.longitude!=0) {
            Location loc3 = new Location("");
            loc3.setLatitude(destinationLatLng.latitude);
            loc3.setLongitude(destinationLatLng.longitude);
            distanceB = loc2.distanceTo(loc3);
        }

        distanceA = loc1.distanceTo(loc2);

        if(status==1){
            getRouteToMarker(new LatLng(nLastLocation.getLatitude(), nLastLocation.getLongitude()),pickupLatLng);
            int speedIs1KmMinute = 100;
            float estimatedDriveTimeInMinutes = distanceA / speedIs1KmMinute;
            int timeT = Math.round(estimatedDriveTimeInMinutes);
            time.setText(timeT + " min");

            if(distanceA<1000){
                int distanceInt = Math.round(distanceA);
                distance.setText(distanceInt + "m");
            }
            else {
                distanceA = distanceA/1000;
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(1);
                String str = df.format(distanceA);
                distance.setText(str+ "km");
            }

        }
        if(status==2) {
            if (distanceB != 0) {
                int speedIs1KmMinute = 100;
                float estimatedDriveTimeInMinutes = distanceB / speedIs1KmMinute;
                int timeT = Math.round(estimatedDriveTimeInMinutes);
                time.setText(timeT + " min");

                if (distanceB < 1000) {
                    int distanceInt = Math.round(distanceB);
                    distance.setText(distanceInt + "m");
                } else {

                    distanceB = distanceB / 1000;

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(1);
                    String str = df.format(distanceB);
                    distance.setText(str + "km");
                }

                if(destinationLatLng.latitude != 0.0 && destinationLatLng.longitude != 0.0){
                    getRouteToMarker(new LatLng(nLastLocation.getLatitude(), nLastLocation.getLongitude()),destinationLatLng);
                }
            }
            else{
                distance.setText("0m");
                time.setText("0min");
            }
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        nLocationRequest = new LocationRequest();
        nLocationRequest.setInterval(5000);
        nLocationRequest.setFastestInterval(3000);
        nLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        nLocationRequest.setSmallestDisplacement(10);


    }

    final int LOCATION_REQUEST_CODE=1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(this);

                }
                else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
                }
            }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void connectDriver(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != (PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            ActivityCompat.requestPermissions(DriverMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,nLocationRequest,this);


        nLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng latLng = new LatLng(nLastLocation.getLatitude(), nLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));

        if(mCurrent!=null){
            mCurrent.remove();
        }
        if(destinationMarker!=null){
            destinationMarker.remove();
        }


    }

    private void rotateMarker(final Marker mCurrent, final float i, GoogleMap mMap) {
        final Handler handler = new Handler();
        final long start = SystemClock.currentThreadTimeMillis();
        final float startRotation = mCurrent.getRotation();
        final long duration = 1500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float)elapsed/duration);
                float rot = t*i+(1-t)*startRotation;
                mCurrent.setRotation(-rot > 180?rot/2:rot);
                if(t<1.0){
                    handler.postDelayed(this,16);
                }
            }
        });
    }

    public void disconnectDriver(){
        if(mCurrent!=null){
            mCurrent.remove();
        }
        if(destinationMarker!=null){
            destinationMarker.remove();
        }
        erasePolylines();

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }



    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent};

    @Override
    public void onRoutingFailure(RouteException e) {

        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {


    }

    @Override
    public void onRoutingSuccess(ArrayList<Route>route, int shortestRouteIndex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }





    }





