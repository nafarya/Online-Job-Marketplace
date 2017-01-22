package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderTeacherListAdapter;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;


/**
 * Created by dan on 22.01.17.
 */

public class OrderTeacherListFragment extends Fragment {

    @InjectView(R.id.order_teacher_list_rv_id)      RecyclerView orderList;

    private Profile profile;
    private ArrayList<Order> orders = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequestParams params = new RequestParams();
        params.put("offset", 0);
        params.put("limit", 20);

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);

        HighFiveHttpClient.get("orders", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    JSONArray subjArray = contents.getJSONArray("items");
                    for (int i = 0; i < contents.getInt("count"); i++) {
                        JSONObject current = (JSONObject) subjArray.get(i);
                        profile.addOrder(new Order(current.getString("title"), current.getString("description")));
                    }
                    OrderTeacherListAdapter adapter = new OrderTeacherListAdapter(profile.getAllOrders());
                    if (orderList != null) {
                        orderList.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_teacher_list, container, false);

        ButterKnife.inject(this, v);

        HighFiveHttpClient.initCookieStore(getContext());

        //addOrders();

        return v;
    }

    void addOrders() {
        if (profile == null) {
            profile = new Profile("test", "test");
        }
        Order order = new Order("Инфа для препода", "Завтра будет кр, 8 класс");
        Order order1 = new Order("Русский язык", "Подстраховать на диктанте");
        Order order2 = new Order("География", "проверочная работа");
        Order order3 = new Order("Геометрия", "Контрльная работа, подстраховать");
        Order order4 = new Order("Английский", "Помочь с домашкой");
        profile.addOrder(order);
        profile.addOrder(order1);
        profile.addOrder(order2);
        profile.addOrder(order3);
        profile.addOrder(order4);

    }
}
