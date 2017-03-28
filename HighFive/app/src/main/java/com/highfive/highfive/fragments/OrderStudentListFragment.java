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

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderListAdapter;
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

import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 26.11.16.
 */

public class OrderStudentListFragment extends Fragment implements OrderListAdapter.OnItemClickListener{
    private RecyclerView orderList;
    private Profile profile;
    private FloatingActionButton fab;
    private Navigator navigator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);
        orderList = (RecyclerView) v.findViewById(R.id.order_list_rv_id);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       AddOrderFragment fragment = new AddOrderFragment();
                                       FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                       fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
                                   }});

        HighFiveHttpClient.initCookieStore(getContext());

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);

        OrderListAdapter adapter = new OrderListAdapter(profile.getAllOrders(), this);
        orderList.setAdapter(adapter);
        return v;
    }

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
                        Order tmp = new Order(current.getString("id"), current.getString("title"), current.getString("description"));
                        tmp.setOrderCreatorId(current.getString("creator"));
                        if (profile.getUid().equals(tmp.getOrderCreatorId())) {
                            profile.addOrder(tmp);
                        }
                    }
                    OrderListAdapter adapter = new OrderListAdapter(profile.getAllOrders(), OrderStudentListFragment.this);
//                    OrderTeacherListAdapter adapter = new OrderTeacherListAdapter(profile.getAllOrders(), OrderTeacherListFragment.this);
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
