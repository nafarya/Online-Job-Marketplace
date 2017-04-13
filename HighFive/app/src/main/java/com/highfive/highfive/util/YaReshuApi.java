package com.highfive.highfive.util;

import com.highfive.highfive.Response;
import com.highfive.highfive.model.Order;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by dan on 13.04.17.
 */

public interface YaReshuApi {

    @GET("orders/{id}")
    Observable<Response<Order>> getOrderDetailsById(@Path("id") String id);
}
