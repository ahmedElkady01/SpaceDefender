package com.example.spacedefender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_lafoK8:APA91bFEShZRbzTLKVae4_eUtWUdR_1mV6NVwMYseDLuq1RqfqeIBLT_NFUVwvIaa4XC5y9AL_uLITwBfL2nFbGqKjenbP2qg0G90xthqBMIziSM9rmNvOjnmQ5cYbKQ8JWh8JKP2-5W"
                    //firebase > settings >cloud messaging > token server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
