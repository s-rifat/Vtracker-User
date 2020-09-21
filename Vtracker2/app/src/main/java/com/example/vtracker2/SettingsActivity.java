package com.example.vtracker2;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.vtracker2.Model.User;
import com.example.vtracker2.Utils.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_settings);

        final Switch sw = (Switch) findViewById(R.id.switch1);
        final SharedPreferences sharedPreferences = getSharedPreferences ("save",MODE_PRIVATE);
        sw.setChecked (sharedPreferences.getBoolean ("value",true));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final DatabaseReference df = FirebaseDatabase.getInstance()
                            .getReference(Common.USER_INFORMATION)
                            .child(Common.loggeduser.getUid())
                            .child (Common.NOTIFICATION_STATUS);
                    Common.loggeduser.setNotificationStatus ("1");
                    df.setValue ("1");

                    SharedPreferences.Editor editor = getSharedPreferences ("save",MODE_PRIVATE).edit ();
                    editor.putBoolean ("value",true);
                    editor.apply ();
                    sw.setChecked (true);





                    } else {
                    final DatabaseReference df = FirebaseDatabase.getInstance()
                            .getReference(Common.USER_INFORMATION)
                            .child(Common.loggeduser.getUid())
                            .child (Common.NOTIFICATION_STATUS);
                    Common.loggeduser.setNotificationStatus ("0");
                    df.setValue ("0");

                    SharedPreferences.Editor editor = getSharedPreferences ("save",MODE_PRIVATE).edit ();
                    editor.putBoolean ("value",false);
                    editor.apply ();
                    sw.setChecked (false);

                }
            }
        });
    }
}