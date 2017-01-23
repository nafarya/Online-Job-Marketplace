package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderListAdapter;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 26.11.16.
 */

public class OrderListFragment extends Fragment implements OrderListAdapter.OnItemClickListener{
    private RecyclerView orderList;
    private Profile profile;
    private FloatingActionButton fab;
    private Navigator navigator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);
        orderList = (RecyclerView) v.findViewById(R.id.order_list_rv_id);

        Log.i("orderList", "student");

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       AddOrderFragment fragment = new AddOrderFragment();
                                       FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                       fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
                                   }});



        HighFiveHttpClient.initCookieStore(getContext());

        RequestParams params = new RequestParams();
        params.put("offset", 0);
        params.put("limit", 20);

        HighFiveHttpClient.get("orders", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    String id = contents.getString("id");
                    String token = contents.getString("token");
                    HighFiveHttpClient.addUidCookie(id);
                    HighFiveHttpClient.addTokenCookie(token);
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


        addOrders();
        OrderListAdapter adapter = new OrderListAdapter(profile.getAllOrders(), this);
        orderList.setAdapter(adapter);
        return v;
    }

    void addOrders() {
        if (profile == null) {
            profile = new Profile("test", "test");
        }
        Order order = new Order(0, "Математика", "Завтра будет кр, 8 класс");
        Order order1 = new Order(1, "Русский язык", "Подстраховать на диктанте");
        Order order2 = new Order(2, "География", "проверочная работа");
        Order order3 = new Order(3, "Геометрия", "Контрльная работа, подстраховать");
        Order order4 = new Order(4, "Английский", "Помочь с домашкой");
        profile.addOrder(order);
        profile.addOrder(order1);
        profile.addOrder(order2);
        profile.addOrder(order3);
        profile.addOrder(order4);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigator = (Navigator) getActivity();
    }

    @Override
    public void onItemClick(int item) {
        navigator.navigateToOrderDetail(profile.getAllOrders().get(item));
    }
}
