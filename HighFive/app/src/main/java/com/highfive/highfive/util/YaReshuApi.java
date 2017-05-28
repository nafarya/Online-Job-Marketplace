package com.highfive.highfive.util;

import com.highfive.highfive.model.AddFileObj;
import com.highfive.highfive.model.AddFileParams;
import com.highfive.highfive.model.MyFile;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.responseModels.Items;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.model.Order;

import org.json.JSONArray;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dan on 13.04.17.
 */

public interface YaReshuApi {

    @GET("orders/{id}")
    Observable<Response<Order>> getOrderDetailsById(@Path("id") String id);

    @GET("users/{id}/orders")
    Observable<Response<Items<Order>>> getUsersOrders(@Header("api-token") String apiToken,
                                                      @Path("id") String id,
                                                      @Query("status") String status,
                                                      @Query("offset") int offset,
                                                      @Query("limit") int limit
                                                      );

    @GET("users/{id}/waitingOrders")
    Observable<Response<Items<Order>>> getWaitingTeacherOrders(
                                                      @Header("api-token") String apiToken,
                                                      @Path("id") String id,
                                                      @Query("offset") int offset,
                                                      @Query("limit") int limit);


    @GET("users/{id}")
    Observable<Response<Profile>> getUserById(@Path("id") String id);

    @GET("files/{id}")
    Observable<Response<MyFile>> getFileById(@Path("id") String id);



    @POST("orders/{id}/{status}")
    Call<Response> changeOrderStatus(@Header("api-token") String apiToken,
                                     @Path("id") String id,
                                     @Path("status") String status);

    @FormUrlEncoded
    @POST("orders/{id}/submit")
    Call<Response> teacherSubmitOrder(@Header("api-token") String apiToken,
                                     @Path("id") String id,
                                     @Field("action") String action);

    @FormUrlEncoded
    @POST("bids/{id}/comments")
    Call<Response> addComment(@Header("api-token") String apiToken,
                              @Path("id") String id,
                              @Field("comment") String comment);

    @FormUrlEncoded
    @POST("orders/{id}/choose")
    Call<Response> chooseBidForOrder(@Header("api-token") String apiToken,
                                     @Path("id") String id,
                                     @Field("bid") String bidId);

    @Multipart
    @POST("users/{id}/avatar")
    Call<Response> uploadAvatar(@Header("api-token") String apiToken,
                                @Path("id") String id,
                                @Part MultipartBody.Part file);


    @Multipart
    @POST("files/upload")
    Call<Response> uploadFile(@Header("api-token") String apiToken,
                                @Part MultipartBody.Part file);

    @POST("orders/")
    Call<Response> addOrder(@Header("api-token") String apiToken,
                            @Body AddFileParams file);


}
