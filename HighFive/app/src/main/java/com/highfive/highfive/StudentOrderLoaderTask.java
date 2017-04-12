package com.highfive.highfive;

import android.app.Activity;
import android.os.AsyncTask;

import com.highfive.highfive.model.Order;
import com.highfive.highfive.util.HighFiveSyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 12.04.17.
 */

public class StudentOrderLoaderTask extends AsyncTask<List<String>, Integer, List<Order> > {

    private List<String> orderIds;
    private List<Order> orderList;
    private LandingActivity activity;

    @Override
    protected void onPostExecute(List<Order> orders) {
        super.onPostExecute(orders);
        activity.updateChooseOrder(orders);
    }

    public StudentOrderLoaderTask(LandingActivity activity) {
        this.activity = activity;
    }

    @Override
    protected List<Order> doInBackground(List<String>... params) {
        orderIds = params[0];
        orderList = new ArrayList<>(orderIds.size());

        for (int ind = 0; ind < orderIds.size(); ind++) {
            RequestParams orderParams = new RequestParams();
            String orderId = orderIds.get(ind);
            orderParams.add("id", orderId);

            JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONObject contents = response.getJSONObject("response");
                        Order order = new Order();
                        order.setTitle(contents.get("title").toString());
                        order.setDescription(contents.get("description").toString());
                        order.setdeadLine(contents.get("deadline").toString());
                        order.setSubjectId(contents.get("subject").toString());
                        order.setStatus(contents.get("status").toString());
                        order.setType(contents.get("type").toString());
                        order.setOrderdId(contents.getString("id"));
                        order.setOffer(contents.getString("offer"));

                        JSONArray bidArray = contents.getJSONArray("bids");
                        order.setBidArraySize(bidArray.length());

                        for (int i = 0; i < orderIds.size(); i++) {
                            if (order.getOrderdId().equals(orderIds.get(i))) {
                                orderList.set(i, order);
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
            };
            handler.setUseSynchronousMode(true);
            HighFiveSyncHttpClient.get("orders/" + orderId, orderParams, handler);

        }
        return orderList;
    }



}
