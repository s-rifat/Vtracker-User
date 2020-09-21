package com.example.vtracker2.Remote;
import com.example.vtracker2.Model.MyResponse;
import com.example.vtracker2.Model.Request;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface IFCMService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_BAp2Uk:APA91bGZ9v-wV7J7kykje22IJi2IKumYoHh_qsAS8ZEMpzNZywVHhHViOkuBcugCBsm5YtSy6oCbI73xdy-Kak5_0yaH4mp3zrKFHrxdXEM5jmY6M5r-WX0qncgU9KGP7LHLQZXTuLHE"
            }
    )
    @POST("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);
}
