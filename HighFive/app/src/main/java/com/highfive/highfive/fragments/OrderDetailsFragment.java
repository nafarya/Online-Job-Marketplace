package com.highfive.highfive.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.OrderType;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.Subject;
import com.highfive.highfive.model.SubjectList;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    @InjectView(R.id.button_add_bid)            Button addBid;
    @InjectView(R.id.bid_amount)                EditText bidAmount;
    @InjectView(R.id.bid_card)                  RelativeLayout bidCard;
    @InjectView(R.id.current_bids_number)       TextView bidsNumber;

    private String orderId;
    private ArrayList<Bid> bidlist = new ArrayList<>();
    private List<Subject> subjectList;
    private OrderTypeList orderTypeList;
    private List<OrderType> typeList;
    private Navigator navigator;
    private Profile profile;
    private SubjectList subList;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bidsNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.navigateToBidsList(bidlist);
            }
        });
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HighFiveHttpClient.initCookieStore(getContext());
        View v = inflater.inflate(R.layout.fragment_order_details_teacher, container, false);
        ButterKnife.inject(this, v);


        navigator = (Navigator) getActivity();

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        Type subListType = new TypeToken<SubjectList>(){}.getType();
        subList = (SubjectList) Cache.getCacheManager().get("subjectList", SubjectList.class, subListType);
        Type orderTypeListType = new TypeToken<OrderTypeList>(){}.getType();
        orderTypeList = (OrderTypeList) Cache.getCacheManager().get("orderTypeList", OrderTypeList.class, orderTypeListType);




        if (!profile.getType().equals("teacher")) {
            bidCard.setVisibility(View.GONE);
            subjectList = subList.getStudentSubjectList();
        } else {
            subjectList = subList.getSubjectList();
        }
        typeList = orderTypeList.getorderTypelist();

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

    private String getSubjectNameById(String id) {
        for (int i = 0; i < subjectList.size(); i++) {
            if (subjectList.get(i).getId().equals(id)) {
                return subjectList.get(i).getName();
            }
        }
        return "Error, subject name with id " + id + "does not exist";
    }

    private String getOrderTypeById(String id) {
        for (int i = 0; i < typeList.size(); i++) {
            if (typeList.get(i).getId().equals(id)) {
                return typeList.get(i).getName();
            }
        }
        return "Error, ordertype with id " + id + "does not exist";
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
                    orderSubject.setText(getSubjectNameById(contents.get("subject").toString()));
                    orderType.setText(getOrderTypeById(contents.get("type").toString()));
                    JSONObject bidObject = contents.getJSONObject("bids");
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
                        Bid bid = new Bid(current.getDouble("offer"), current.getString("id"));
                        bid.setOrderId(current.getString("order"));
                        bid.setCreatedAt(current.getString("createdAt"));
                        bid.setCreatedAt(current.getString("updatedAt"));
                        bid.setBidId(current.getString("id"));
                        bidlist.add(bid);
                    }

                    bidsNumber.setText(String.valueOf(bidlist.size()));

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
