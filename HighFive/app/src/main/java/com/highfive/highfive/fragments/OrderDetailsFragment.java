package com.highfive.highfive.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by heat_wave on 19.01.17.
 */

public class OrderDetailsFragment extends Fragment {
    @InjectView(R.id.order_title)               TextView orderTitle;
    @InjectView(R.id.order_description)         TextView orderDescription;
    @InjectView(R.id.order_subject)             TextView orderSubject;
    @InjectView(R.id.order_type)                TextView orderType;
    @InjectView(R.id.order_deadline)            TextView orderDeadline;
    @InjectView(R.id.bids_list)                 RecyclerView bidsList;
    @InjectView(R.id.button_add_bid)            Button addBid;
    @InjectView(R.id.bid_amount)                EditText bidAmount;
    @InjectView(R.id.bid_card)                  RelativeLayout bidCard;

    private String orderId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HighFiveHttpClient.initCookieStore(getContext());
        View v = inflater.inflate(R.layout.fragment_order_details_teacher, container, false);
        ButterKnife.inject(this, v);
        bidsList.setNestedScrollingEnabled(false);

        Type profileType = new TypeToken<Profile>(){}.getType();
        Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        if (!profile.getType().equals("teacher")) {
            bidCard.setVisibility(View.GONE);
        }

        addBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.add("id", orderId);
                params.add("offer", bidAmount.getText().toString());
                HighFiveHttpClient.post("orders/" + orderId + "/bids", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Toast.makeText(getContext(), "Ставка отправлена", Toast.LENGTH_SHORT).show();
                        bidAmount.setText("");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();

        RequestParams params = new RequestParams();
        orderId = args.getString("orderId");
        params.add("id", orderId);

        HighFiveHttpClient.get("orders/" + orderId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    orderTitle.setText(contents.get("title").toString());
                    orderDescription.setText(contents.get("description").toString());
                    orderDeadline.setText(contents.get("deadline").toString());
                    orderSubject.setText(contents.get("subjects").toString());
                    orderType.setText(contents.get("type").toString());
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

        HighFiveHttpClient.get("orders/" + orderId + "/bids", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    int count = contents.getInt("count");
                    JSONArray items = contents.getJSONArray("items");
                    for (int i = 0; i < count; i++) {
                        JSONObject current = items.getJSONObject(i);
                        //make a list item here
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
}
