package com.highfive.highfive;

import android.app.Application;

import com.highfive.highfive.util.YaReshuApi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dan on 13.04.17.
 */

public class App extends Application {

    private static YaReshuApi yaReshuApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {

        retrofit = new Retrofit.Builder()
                .baseUrl("https://yareshu.ru/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        yaReshuApi = retrofit.create(YaReshuApi.class);
    }

    public static YaReshuApi getApi() {
        return yaReshuApi;
    }
}