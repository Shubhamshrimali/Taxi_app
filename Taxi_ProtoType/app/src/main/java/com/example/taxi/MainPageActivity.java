package com.example.taxi;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainPageActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseAuth auth;
    FirebaseUser user;
    String user_id;
    DatabaseReference reference;
    TextView t1_name,t2_email;
    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng startlatlog,endlatlog;
    Marker currentMarker;
    Marker destinationMarker;
    Button b4_sourceButton,b5_destinationButton;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        auth = FirebaseAuth.getInstance();

        permissionManager=new PermissionManager(){};
        permissionManager.checkAndRequestPermissions(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        t1_name = findViewById(R.id.name_text);
        t2_email = findViewById(R.id.email_text);
        b4_sourceButton=findViewById(R.id.button_pickup);
        b5_destinationButton=findViewById(R.id.button_destination);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent i = new Intent(MainPageActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            user_id = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    t1_name.setText(name);
                    t2_email.setText(email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            b4_sourceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i =new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainPageActivity.this);
                        startActivity(i);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                }
            });
            b5_destinationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i =new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainPageActivity.this);
                        startActivity(i);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_view);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request=new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(500);

        LocationServices.FusedLocationApi.requestLocationUpdates(client,request, (com.google.android.gms.location.LocationListener) this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        LocationServices.FusedLocationApi.removeLocationUpdates(client, (com.google.android.gms.location.LocationListener) this);

        if(location==null){
            Toast.makeText(getApplicationContext(),"Location could not found",Toast.LENGTH_SHORT).show();
        }
        else{
            startlatlog=new LatLng(location.getLatitude(),location.getLongitude());

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> myaddress = geocoder.getFromLocation(startlatlog.latitude, startlatlog.longitude, 1);
                String address=myaddress.get(0).getAddressLine(0);
                String city=myaddress.get(0).getLocality();
                b4_sourceButton.setText(address+" "+city);
            }
            catch (IOException e){
                e.printStackTrace();
            }

            if(currentMarker==null){
                MarkerOptions options=new MarkerOptions();
                options.position(startlatlog);
                options.title("Current position");
                currentMarker=mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startlatlog,15));
            }
            else{
                currentMarker.setPosition(startlatlog);
            }

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

//        client=new GoogleApiClient.Builder(this).addApi(LocationListener.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
//        client.connect();

        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode,permissions,grantResults);

        ArrayList<String> denied_array=permissionManager.getStatus().get(0).denied;
        if(denied_array.isEmpty()){
            Toast.makeText(getApplicationContext(),"User grant permission",Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void OnBackPressed(){
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if(drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START);
//        }
//        else{
//            super.OnBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_controller_view_tag);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public boolean onNavigationItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id == R.id.nav_signout){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null){
                auth.signOut();
                finish();
                Intent i =new Intent(MainPageActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"User is signed out already",Toast.LENGTH_SHORT).show();
            }

        }
        else if(id == R.id.nav_trips){}
        else if(id==R.id.nav_help){}
        else if(id==R.id.nav_ride){}
        else if(id==R.id.nav_Payment){}
        DrawerLayout drawer=findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==200){
            if(resultCode==RESULT_OK){
                Place place=PlaceAutocomplete.getPlace(this,data);
                String name=place.getName().toString();
                startlatlog=place.getLatLng();
                b4_sourceButton.setText(name);
                if(currentMarker==null){
                    MarkerOptions options=new MarkerOptions();
                    options.title("Pick up location");
                    options.position(startlatlog);
                    currentMarker=mMap.addMarker(options);

                }
                else
                {
                    currentMarker.setPosition(startlatlog);
                }



            }
            else if(requestCode==400){
                if(resultCode==RESULT_OK){
                    Place myplace=PlaceAutocomplete.getPlace(this,data);
                    String name=myplace.getName().toString();

                    b5_destinationButton.setText(name);

                    if(destinationMarker==null){
                        MarkerOptions options=new MarkerOptions();
                        options.title("Destination");
                        options.position(endlatlog);
                        destinationMarker=mMap.addMarker(options);
                    }
                    else {
                        destinationMarker.setPosition(endlatlog);
                    }

                }
            }
        }
    }
}

