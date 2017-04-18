package com.highfive.highfive.util;

import com.highfive.highfive.model.Profile;
import com.highfive.highfive.responseModels.Items;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.model.Order;

import retrofit2.http.GET;
import retrofit2.http.Header;
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
                                                      @Query("status") String status);
    @GET("users/{id}")
    Observable<Response<Profile>> getUserById(@Path("id") String id);
}
