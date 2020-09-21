package com.example.vtracker2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.example.vtracker2.Model.MyLocation;
import com.example.vtracker2.Utils.Common;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    DatabaseReference trackingUserLocation;

    boolean bol = true;

    String x ="null",y = "null";
    double X = 1, Y = 1;

    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
               .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerEventRealTime();
    }

    private void registerEventRealTime() {

        trackingUserLocation = FirebaseDatabase.getInstance()
                .getReference(Common.PUBLIC_LOCATION)
                .child(Common.trackingUser.getUid());
        s=Common.trackingUser.getEmail ();
        if(!Common.trackingUser.getEmail().toString().equals(Common.trackingUser.getRout().toString()))
        {
            trackingUserLocation = FirebaseDatabase.getInstance()
                    .getReference(Common.Driver_LOCATION)
                    .child(Common.trackingUser.getUid());
            s=Common.trackingUser.getBusName ();
        }

        trackingUserLocation.addValueEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        trackingUserLocation.addValueEventListener(this);


    }

    @Override
    protected void onStop() {

        trackingUserLocation.removeEventListener(this);
        super.onStop();
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //enable zoom ui

        mMap.getUiSettings().setZoomControlsEnabled(true);

        //set skin for map

        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                R.raw.my_uber_style));




    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        if(dataSnapshot.getValue()!=null)
        {
            MyLocation location = dataSnapshot.getValue(MyLocation.class);

            //Add marker



            LatLng userMarker = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.clear ();

            mMap.addMarker(new MarkerOptions().position(userMarker)
                    .title(Common.trackingUser.getEmail())
                   .snippet(Common.getDateFormatted(Common.convertTimeStampToDate(location.getTime()))));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker,16f));

            DatabaseReference df = FirebaseDatabase.getInstance()
                    .getReference(Common.PUBLIC_LOCATION)
                    .child(Common.loggeduser.getUid());
            df.addValueEventListener (new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                    x = dataSnapshot2.child ("latitude").getValue ().toString ();
                    y = dataSnapshot2.child ("longitude").getValue ().toString ();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if(x!="null")
            X = Double.parseDouble (x);
            if(y!="null")
            Y = Double.parseDouble (y);

           double distance = calculateDistance (location.getLatitude (),location.getLongitude(),X,Y);

           if(distance<=10 && bol && x!="null" && y!="null" && Common.loggeduser.getNotificationStatus ()=="1")
           {
               Toast.makeText (this, X+"  "+Y+" "+location.getLatitude ()+" "+location.getLatitude ()+" "+ distance, Toast.LENGTH_LONG).show ();
               addNotification();
               bol = false;
           }

           if(distance>10)
           {
               bol = true;
           }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification() {
      //  Intent intent=new Intent(getApplicationContext(),TrackingActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel (CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
      //  PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,0);
        Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText(s+ " Almost there!")
                .setContentTitle("Hey Get yourself ready!")
              //  .setContentIntent(pendingIntent)
             //   .addAction(android.R.drawable.sym_action_chat,"OK",pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .build();

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);

    }


    public double calculateDistance(double x1, double y1, double x2, double y2)

    {
        double ans = (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
        return Math.sqrt (ans);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
