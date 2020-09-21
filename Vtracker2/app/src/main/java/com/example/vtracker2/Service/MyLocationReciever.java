package com.example.vtracker2.Service;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;

import com.example.vtracker2.Utils.Common;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class MyLocationReciever extends BroadcastReceiver {

    public static final String ACTION = "com.example.vtracker2.UPDATE_LOCATION";
    DatabaseReference publicLocation;
    String uid;

    public MyLocationReciever() {

        publicLocation = FirebaseDatabase.getInstance().getReference(Common.PUBLIC_LOCATION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Paper.init(context);

        uid = Paper.book().read(Common.USER_UID_SAVE_KEY);
        if(intent!=null)
        {
            final String action = intent.getAction();
            if(action.equals(ACTION))
            {
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null)
                {
                    Location location = result.getLastLocation();
                    if(Common.loggeduser!=null)//app in foreground
                    {
                        publicLocation.child(Common.loggeduser.getUid()).setValue(location);
                    }
                    else //app be killed
                    {
                        publicLocation.child(uid).setValue(location);
                    }
                }
            }
        }
    }
}
