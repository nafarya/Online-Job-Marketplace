package com.highfive.highfive.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.App;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.BidComment;
import com.highfive.highfive.model.Order;
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
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heat_wave on 19.01.17.
 */

public class OrderDetailsFragment extends Fragment {
    @InjectView(R.id.order_title)               TextView orderTitle;
    @InjectView(R.id.order_description)         TextView orderDescription;
    @InjectView(R.id.order_subject)             TextView orderSubject;
    @InjectView(R.id.order_details_type)        TextView orderType;
    @InjectView(R.id.order_deadline)            TextView orderDeadline;
    @InjectView(R.id.order_details_status)      TextView orderStatus;
    @InjectView(R.id.button_add_bid)            Button addBid;
    @InjectView(R.id.bid_amount)                EditText bidAmount;
    @InjectView(R.id.bid_card)                  RelativeLayout bidCard;
    @InjectView(R.id.current_bids_number)       TextView bidsNumber;
    @InjectView(R.id.avgBidPrice)               TextView avgBidPrice;
    @InjectView(R.id.order_details_budget)      TextView orderBudget;

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
        View v = inflater.inflate(R.layout.fragment_order_details, container, false);
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
                postBid();
            }
        });
        getOrderDetails();
        getBids();

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
        orderId = args.getString("orderId");

    }

    private void getOrderDetails() {
        App.getApi()
                .getOrderDetailsById(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Order>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Response<Order> orderResponse) {
                        Order order = orderResponse.getResponse();
                        orderTitle.setText(order.getTitle());
                        orderDescription.setText(order.getDescription());
                        orderDeadline.setText(order.getdeadLine());
                        orderSubject.setText(getSubjectNameById(order.getSubjectId()));
                        orderStatus.setText(order.getStatus());
                        orderType.setText(getOrderTypeById(order.getType()));
                        orderBudget.setText(order.getOffer() + " Р");
                    }
                });
    }

    private void getBids() {
        RequestParams params = new RequestParams();
        params.add("id", orderId);
        HighFiveHttpClient.get("orders/" + orderId + "/bids", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    int count = contents.getInt("count");
                    double avgBid = 0;
                    JSONArray items = contents.getJSONArray("items");
                    for (int i = 0; i < count; i++) {
                        JSONObject current = items.getJSONObject(i);
                        Bid bid = new Bid();
                        bid.setCreatedAt(current.getString("createdAt"));
                        bid.setUpdatedAt(current.getString("updatedAt"));
                        bid.setOffer(current.getDouble("offer"));
                        bid.setBidId(current.getString("id"));
                        bid.setOrderId(current.getString("order"));
                        bid.setBidCreatorId(current.getString("creator"));
                        JSONArray comments = current.getJSONArray("comments");
                        for (int j = 0; j < comments.length(); j++) {
                            JSONObject comment = comments.getJSONObject(j);
                            BidComment bidComment = new BidComment();
                            bidComment.setUpdatedAt(comment.getString("updatedAt"));
                            bidComment.setCreatedAt(comment.getString("createdAt"));
                            bidComment.setText(comment.getString("text"));
                            bidComment.setOwner(comment.getString("owner"));
                            bidComment.set_id(comment.getString("_id"));
                            bid.addBidComment(bidComment);
                        }
                        bidlist.add(bid);
                        avgBid += bid.getOffer();
                    }
                    bidsNumber.setText(String.valueOf(bidlist.size()));
                    if (count > 0) {
                        avgBidPrice.setText(String.valueOf((int)(avgBid / count)) + " Р");
                    }
                    for (int i = 0; i < bidlist.size(); i++) {
                        if (bidlist.get(i).getBidCreatorId().equals(profile.getUid())) {
                            bidCard.setVisibility(View.GONE);
                        }
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

    private void postBid() {
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
                if (statusCode == 400) {
                    Toast.makeText(getContext(), "Вы уже сделали ставку", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
