package com.example.mynewproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;

import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mynewproject.R.drawable.ic_directions_car_black_24dp;

public class CustomerMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, NavigationView.OnNavigationItemSelectedListener, RoutingListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location nLastLocation;
    LocationRequest nLocationRequest;
    private SupportMapFragment mapFragment;
    private Button mRequest;
    private LatLng pickupLocation;
    private Boolean requestBol = false;
    private  Marker pickupMarker;
    private Boolean isLoggingOut = false;
    private String destination="Destination: --", requestService, call;
    private PlacesClient placesClient;

    private CircleImageView mBottomDriverProfileImage;
    private TextView mBottomName, mBottomCar,mBottomDestination, mtextRatingBar, mBottomCarNumber;
    private static final String TAG_MASTER_FRAGMENT = "TAG_MASTER_FRAGMENT";
    private static final String TAG_DETAIL_FRAGMENT = "TAG_DETAIL_FRAGMENT";
    private DrawerLayout drawerLayout;
    private LatLng destinationLatLng;
    private MenuView.ItemView logout;
    private BottomSheetBehavior mBottomSheetBehavior;
    private RadioGroup mRadioGroup;
    private String mService;
    private RadioButton uberx, uberxl, uberblack;
    private ImageView btcall, btstop;
    private static final int REQUEST_CALL = 1;
    private Boolean btmsheet = false;

    private int position = 1;
    private AnimationDrawable animationDrawable;
    private Marker destinationMarker;
    private Button button;
    private CoordinatorLayout sheet;
    private TextView distancem;
    private View mymap;
    private Button serviceButton;
    private TextView textcarx, textcarxl,textcarblack, numberallowed;
    private LinearLayout buttonservice, driverinfo;
    private Button bottomContact, bottomCancel;
    private String name, car, profilImageUrl, pseudo, carnumber, customerId;
    private float distance, distance2, numberrating;
    private TextView navName,navPseudo;
    private CircleImageView navImage;
    private NavigationView mNavigationView;
    private LocationManager locationManager;
    private AutocompleteSupportFragment autocompleteFragment;
    private View mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        polylines = new ArrayList<>();



        logout = (MenuView.ItemView) findViewById(R.id.nav_logout);
        View bottomSheet = findViewById(R.id.bottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!btmsheet){
                    btmsheet = true;
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    btmsheet = false;
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

        uberx = (RadioButton) findViewById(R.id.uberx);
        uberx.isChecked();
        uberxl = (RadioButton) findViewById(R.id.uberxl);
        uberblack = (RadioButton) findViewById(R.id.uberblack);


        sheet= (CoordinatorLayout) findViewById(R.id.sheet);
        mymap = (View) findViewById(R.id.map);
        driverinfo = (LinearLayout) findViewById(R.id.driverInfo);

        mBottomName = (TextView) findViewById(R.id.bottomdriverName);
        mBottomCar = (TextView) findViewById(R.id.bottomdriverCar);
        mBottomDestination = (TextView) findViewById(R.id.bottomdestination);
        mBottomCarNumber = (TextView) findViewById(R.id.matriculenumber);
        distancem = (TextView) findViewById(R.id.distance);
        mtextRatingBar = (TextView) findViewById(R.id.ratingnumber);
        bottomContact = (Button) findViewById(R.id.contact);
        bottomCancel = (Button) findViewById(R.id.bottomcancel);
        mBottomDriverProfileImage = (CircleImageView) findViewById(R.id.bottomdriverProfileImage);

        serviceButton = (Button) findViewById(R.id.servicemode);
        textcarx = (TextView) findViewById(R.id.textcarx);
        textcarxl = (TextView) findViewById(R.id.textcarxl);
        textcarblack = (TextView) findViewById(R.id.textcarblack);
        numberallowed = (TextView) findViewById(R.id.numberallowed);
        buttonservice = (LinearLayout) findViewById(R.id.buttonservice);

        uberx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uberx.setChecked(true);
                uberxl.setChecked(false);
                uberblack.setChecked(false);

                textcarx.setVisibility(View.VISIBLE);
                textcarxl.setVisibility(View.GONE);
                textcarblack.setVisibility(View.GONE);
                serviceButton.setText("Economic");
                numberallowed.setText("1-2");
            }
        });
        uberxl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uberx.setChecked(false);
                uberxl.setChecked(true);
                uberblack.setChecked(false);

                textcarx.setVisibility(View.GONE);
                textcarxl.setVisibility(View.VISIBLE);
                textcarblack.setVisibility(View.GONE);
                serviceButton.setText("Family");
                numberallowed.setText("1-8");
            }
        });
        uberblack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uberx.setChecked(false);
                uberxl.setChecked(false);
                uberblack.setChecked(true);

                textcarx.setVisibility(View.GONE);
                textcarxl.setVisibility(View.GONE);
                textcarblack.setVisibility(View.VISIBLE);
                serviceButton.setText("Business");
                numberallowed.setText("1-4");
            }
        });

        String apiKey = "AIzaSyCo6Bjsq0aHW6mdal2RHP7IxpLRrc23FgY";
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(CustomerMap.this, apiKey);
        }
        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,  Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS));



        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != (PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            ActivityCompat.requestPermissions(CustomerMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else{
            mapFragment.getMapAsync(this);
        }

        mapView = mapFragment.getView();
        destinationLatLng = new LatLng(0.0,0.0);


        mRadioGroup = (RadioGroup) findViewById(R.id.group);

        bottomCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                erasePolylines();
                endRide();
            }
        });

        bottomContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        mRequest = (Button) findViewById(R.id.request);
        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!requestBol) {
                    requestBol = true;

                    if(uberx.isChecked()){
                        requestService = "CarX";
                    }
                    if(uberxl.isChecked()){
                        requestService ="CarXL";
                    }
                    if(uberblack.isChecked()){
                        requestService = "CarBlack";
                    }

                    buttonservice.setVisibility(View.GONE);
                    autocompleteFragment.getView().setVisibility(View.GONE);

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(nLastLocation.getLatitude(), nLastLocation.getLongitude()));

                    pickupLocation = new LatLng(nLastLocation.getLatitude(), nLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pick up here"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation,15.0f));

                    mRequest.setText("Getting your driver..");

                    getClosestDriver();

                } else{
                    erasePolylines();
                    endRide();
                }

            }
        });


        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,Place.Field.ADDRESS));
        // Set up a PlaceSelectionListener to handle the response.

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getAddress();
                destinationLatLng = place.getLatLng();

                if(destinationMarker!=null){
                    destinationMarker.remove();
                }
                if(destinationLatLng!=null){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng,16));
                    destinationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).position(new LatLng(destinationLatLng.latitude,destinationLatLng.longitude)).title("Destination"));
                }
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                if(status.isCanceled()){
                    autocompleteSupportFragment.setText(null);
                    autocompleteFragment.setText(null);
                    if(destinationMarker!=null){
                        destination = "Destination --";
                        destinationMarker.remove();
                        destinationLatLng= new LatLng(0,0);
                    }
                }
            }
        });


        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        navName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_name);
        navPseudo = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.navpseudo);
        navImage = (CircleImageView) mNavigationView.getHeaderView(0).findViewById(R.id.navpicture);

        getCurrentUserInfo();

    }

    private void getCurrentUserInfo(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentInfo = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(currentUserId);
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
                        String imageurl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(imageurl).into(navImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCompleteAdress(double latitude, double longitude){
        String address ="";
        Geocoder geocoder = new Geocoder(CustomerMap.this, Locale.getDefault());

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
                Intent intent = new Intent(CustomerMap.this,CustomerSetting.class);
                startActivity(intent);
                break;

            case R.id.nav_logout:
                isLoggingOut=true;
                disconnectCustomer();
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(CustomerMap.this,firstActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.nav_history:
                Intent intent2 = new Intent(CustomerMap.this,History.class);
                intent2.putExtra("customerOrDriver","Customers");
                startActivity(intent2);
                break;

            case R.id.nav_password:
                Intent intent3 = new Intent(CustomerMap.this,ResetPassword.class);
                intent3.putExtra("customerOrDriver","Customers");
                startActivity(intent3);
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;
    GeoQuery geoQuery;
    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude,pickupLocation.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!driverFound && requestBol){
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                    Map<String,Object> drivermap = (Map<String,Object>) dataSnapshot.getValue();
                                    if(driverFound){

                                        return;
                                    }

                                    if(drivermap.get("service").equals(requestService)){
                                        driverFound=true;
                                        driverFoundID=dataSnapshot.getKey();

                                        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                        customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        HashMap map = new HashMap();
                                        map.put("customerRideId",customerId);
                                        map.put("destination",destination);
                                        map.put("destinationLat",destinationLatLng.latitude);
                                        map.put("destinationLng",destinationLatLng.longitude);

                                        driverRef.updateChildren(map);

                                        if(destination!=null){
                                            mBottomDestination.setText(destination);
                                        }

                                        getDriverLocation();
                                        getDriverInfo();
                                        getHasRideEnd();
                                        mRequest.setText("Looking for driver location..");
                                    }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverFound){
                    radius++;
                    getClosestDriver();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    private Marker nDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map =(List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    mRequest.setText("Driver Found");
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(nDriverMarker != null){
                        nDriverMarker.remove();
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    distance = loc1.distanceTo(loc2);

                    if(distance>100 && distance<1000){
                        int distanceInt = Math.round(distance);
                        distancem.setText(distanceInt +"m");
                        erasePolylines();
                        getRouteToMarker(driverLatLng,new LatLng(nLastLocation.getLatitude(),nLastLocation.getLongitude()));

                    }
                    else if(distance<100) {
                        mRequest.setText("Driver is here");
                        erasePolylines();
                        if (destinationLatLng.latitude != 0.0 && destinationLatLng.longitude != 0.0) {
                            Location loc3 = new Location("");
                            loc3.setLatitude(destinationLatLng.latitude);
                            loc3.setLongitude(destinationLatLng.longitude);
                            distance2 = loc2.distanceTo(loc3);
                            if(distance2<1000){
                                int distanceInt2 = Math.round(distance2);
                                distancem.setText(distanceInt2 +"m");
                            }
                            else{
                                distance2=distance2/1000;
                                DecimalFormat df = new DecimalFormat();
                                df.setMaximumFractionDigits(1);
                                String str = df.format(distance2);
                                distancem.setText(str+"km");
                            }
                            getRouteToMarker(driverLatLng,destinationLatLng);
                        }
                        else{
                            distancem.setText("Distance: --");
                        }

                        pickupMarker.remove();
                    }
                    else {
                        distance = distance/1000;

                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(1);
                        String str = df.format(distance);

                        distancem.setText(str+"km");
                        mRequest.setText("Your driver is coming.. ");

                        erasePolylines();
                        getRouteToMarker(driverLatLng,new LatLng(nLastLocation.getLatitude(),nLastLocation.getLongitude()));

                    }

                    nDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your driver").icon(bitmapDescriiptorFromVector(getApplicationContext(),R.drawable.carfromtop32px)));
                    rotateMarker(nDriverMarker,-360,mMap);

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRouteToMarker(LatLng driverLatLng,LatLng driverLatLngEnd) {
        Routing routing = new Routing.Builder().key("AIzaSyChfIz5scL-6OEYQgFsicWUoNZI9mvue6U")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(driverLatLng,driverLatLngEnd)
                .build();
        routing.execute();


    }

    public void showDriverInfoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMap.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(CustomerMap.this).inflate(R.layout.layout_driverinfo_dialog,(LinearLayout) findViewById(R.id.layoutDialogDriverinfo));
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();


        final TextView nameField = ((TextView) view.findViewById(R.id.driverName));
        final TextView carField = ((TextView) view.findViewById(R.id.driverCar));
        final TextView phoneField = ((TextView) view.findViewById(R.id.driverPhone));
        final TextView destinationField = ((TextView) view.findViewById(R.id.textgreenpoint));
        final TextView pickupField = ((TextView) view.findViewById(R.id.textredpoint));
        final TextView matriculeField = ((TextView) view.findViewById(R.id.matr));
        final RatingBar ratingBar = ((RatingBar) view.findViewById(R.id.ratingBar));
        final CircleImageView mProfileImage =((CircleImageView) view.findViewById(R.id.driverProfileImage));

        final Button book = ((Button) view.findViewById(R.id.book));
        final Button cancel = ((Button) view.findViewById(R.id.cancel));

        if(name!=null){nameField.setText(name);}
        if(car!=null){carField.setText(car);}
        if(call!=null){phoneField.setText(call);}
        if(carnumber!=null){matriculeField.setText(carnumber);}
        ratingBar.setRating(numberrating);
        Glide.with(getApplication()).load(profilImageUrl).into(mProfileImage);
        if(destination!=null){ destinationField.setText(destination);}
        else { destinationField.setText("Destination: --");}
        pickupField.setText(getCompleteAdress(nLastLocation.getLatitude(),nLastLocation.getLongitude()));

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheet.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                endRide();
            }
        });
        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void getDriverInfo(){
        sheet.setVisibility(View.GONE);
        driverinfo.setVisibility(View.VISIBLE);
        buttonservice.setVisibility(View.GONE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("name") != null){
                        name = map.get("name").toString();
                    }

                    if(map.get("pseudo") != null){
                        mBottomName.setText(map.get("pseudo").toString());
                        pseudo = mBottomName.getText().toString();
                    }
                    if(map.get("carNumber") != null){
                        mBottomCarNumber.setText(map.get("carNumber").toString());
                        carnumber = map.get("carNumber").toString();
                    }

                    if(map.get("phone") != null){
                        call = map.get("phone").toString();
                    }

                    if(map.get("car") != null) {
                        mBottomCar.setText(map.get("car").toString());
                        car = mBottomCar.getText().toString();
                    }

                    if(map.get("profileImageUrl") != null){
                        profilImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mBottomDriverProfileImage);
                    }

                    int ratingSum=0;
                    float ratingTotal=0;
                    float ratingAvg;
                    for(DataSnapshot child: dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingTotal++;
                    }
                    if(ratingTotal!=0){
                        ratingAvg = ratingSum/ratingTotal;

                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(1);
                        mtextRatingBar.setText(df.format(ratingAvg));
                        numberrating = ratingAvg;
                    }
                    showDriverInfoDialog();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void makePhoneCall(){
        if(call.trim().length() > 0){
            if(ContextCompat.checkSelfPermission(CustomerMap.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(CustomerMap.this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }else{
                String dial = "tel:"+call;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }


        }else {
            Toast.makeText(CustomerMap.this,"Don't found phone number", Toast.LENGTH_SHORT).show();
        }
    }




    private  DatabaseReference driverHasEndRef;
    private ValueEventListener driverHasEndRefListener;
    private void getHasRideEnd(){
        driverHasEndRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driverHasEndRefListener = driverHasEndRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void endRideActivity(){
        final DatabaseReference endRide = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        endRide.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("currentHistoryUid") != null){
                        Intent intent = new Intent(CustomerMap.this,CustomerEndRide.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private  void endRide() {

        requestBol=false;
        geoQuery.removeAllListeners();

        autocompleteFragment.getView().setVisibility(View.VISIBLE);


        if(destinationMarker!=null){
            destinationMarker.remove();
        }

        if(driverFoundID != null){

            driverLocationRef.removeEventListener(driverLocationRefListener);
            driverHasEndRef.removeEventListener(driverHasEndRefListener);
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();

            driverFoundID = null;

            endRideActivity();
        }
        driverFound = false;
        radius = 1;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }

        if(nDriverMarker != null){
            nDriverMarker.remove();
        }
        erasePolylines();

        mRequest.setText("Call uber");

        sheet.setVisibility(View.VISIBLE);
        driverinfo.setVisibility(View.GONE);

        buttonservice.setVisibility(View.VISIBLE);

        distancem.setText("0km");
        mBottomDriverProfileImage.setImageResource(R.mipmap.ic_defaultuser);
        mBottomName.setText("");
        mBottomCar.setText("");
        mBottomDestination.setText("Destination: --");
        mtextRatingBar.setText("0");
        destination="Destination: --";
        destinationLatLng= new LatLng(0.0,0.0);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != (PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            ActivityCompat.requestPermissions(CustomerMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
            nLastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


            if(!getDriversAroundStarted){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                getDriverAround();
            }


        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        nLocationRequest = new LocationRequest();
        nLocationRequest.setInterval(1000);
        nLocationRequest.setFastestInterval(1000);
        nLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != (PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            ActivityCompat.requestPermissions(CustomerMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,nLocationRequest,this);

        nLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng latLng = new LatLng(nLastLocation.getLatitude(), nLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));


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

    boolean getDriversAroundStarted = false;
    List<Marker> markerList = new ArrayList<>();
    private void getDriverAround(){
        getDriversAroundStarted = true;
        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(driversLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(nLastLocation.getLatitude(),nLastLocation.getLongitude()),10000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for(Marker markerIt : markerList){
                    if(markerIt.getTag().equals(key)){
                        return;
                    }
                }
                LatLng driverLocation = new LatLng(location.latitude,location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).icon(bitmapDescriiptorFromVector(getApplicationContext(),R.drawable.carfromtop32px)));
                mDriverMarker.setTag(key);
                markerList.add(mDriverMarker);

                rotateMarker(mDriverMarker,-360,mMap);

            }

            @Override
            public void onKeyExited(String key) {

                for(Marker markerIt : markerList){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                        markerList.remove(markerIt);
                        return;
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                for(Marker markerIt : markerList){
                    if(markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude,location.longitude));

                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void rotateMarker(final Marker mDriverMarker, final float i, GoogleMap mMap) {
        final Handler handler = new Handler();
        final long start = SystemClock.currentThreadTimeMillis();
        final float startRotation = mDriverMarker.getRotation();
        final long duration = 1500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float)elapsed/duration);
                float rot = t*i+(1-t)*startRotation;
                mDriverMarker.setRotation(-rot > 180?rot/2:rot);
                if(t<1.0){
                    handler.postDelayed(this,16);
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void disconnectCustomer(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(!isLoggingOut){
            disconnectCustomer();
        }
    }

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

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent,R.color.quantum_black_100};

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

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
    private void erasePolylines() {
        if (polylines != null) {
            for (Polyline line : polylines) {
                line.remove();
            }
            polylines.clear();
        }
    }
}
