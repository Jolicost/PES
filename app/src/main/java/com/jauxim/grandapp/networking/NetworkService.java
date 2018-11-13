package com.jauxim.grandapp.networking;


import com.jauxim.grandapp.models.ActivityListItemModel;
import com.jauxim.grandapp.models.ActivityModel;
import com.jauxim.grandapp.models.AuthModel;
import com.jauxim.grandapp.models.CityListResponse;
import com.jauxim.grandapp.models.UserModel;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;


public interface NetworkService {

    @GET("v1/city")
    Observable<CityListResponse> getCityList(@Header("authorization") String auth);

    @GET("/activities/{id}")
    Observable<ActivityModel> getActivityInfo(@Path("id") String activityId,@Header("authorization") String auth);

    @POST("/activities")
    Observable<ActivityModel> createActivityInfo(@Body ActivityModel activityInfo,@Header("authorization") String auth);

    @GET("/activities")
    Observable<List<ActivityListItemModel>> getActivityList(@Header("authorization") String auth);

    @POST("/login")
    Observable<AuthModel> getLoginToken(@Body UserModel userModel,@Header("authorization") String auth);

    @POST("/users?username={user_name}&password={password}&email={email}")
    Observable<UserModel> postNewUser(@Path("user_name") String user_name, @Path("password") String password, @Path("email") String email);
}
