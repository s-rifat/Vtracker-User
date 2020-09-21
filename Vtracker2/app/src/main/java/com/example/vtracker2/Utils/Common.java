package com.example.vtracker2.Utils;

import com.example.vtracker2.Model.User;
import com.example.vtracker2.Remote.IFCMService;
import com.example.vtracker2.Remote.RetrofitClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String USER_INFORMATION = "UserInformation";
    public static final String Driver_INFORMATION = "DriverInformation";
    public static final String USER_UID_SAVE_KEY = "SaveUid";
    public static final String TOKENS = "Tokens";
    public static final String FROM_NAME = "FromName";
    public static final String ACCEPT_LIST = "acceptList";
    public static final String FROM_UID = "FromUid";
    public static final String TO_UID = "ToUid";
    public static final String TO_NAME = "ToName";
    public static final String NOTIFICATION_STATUS = "notificationStatus";
    public static final String DRIVER_ID = "driverId";
    public static final String FRIEND_REQUEST = "FriendRequests" ;
    public static final String PUBLIC_LOCATION = "publicLocation" ;
    public static final String Driver_LOCATION = "driverLocation" ;
    public static final String ROUT = "rout" ;
    public static boolean VEHICLE_LIST = true;
    public static User loggeduser;
    public static User trackingUser;

    public static IFCMService getFCMService(){
        return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMService.class);
    }

    public  static Date convertTimeStampToDate(long time)
    {
        return new Date(new Timestamp(time).getTime());
    }

    public static String getDateFormatted(Date date) {

        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString();

    }
}
