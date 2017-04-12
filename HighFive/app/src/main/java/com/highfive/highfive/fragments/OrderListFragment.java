package com.highfive.highfive.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderListAdapter;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.OrderType;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.Subject;
import com.highfive.highfive.model.SubjectList;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.highfive.highfive.util.HighFiveSyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 26.11.16.
 */

public class OrderListFragment extends Fragment implements OrderListAdapter.OnItemClickListener{

    private ArrayList<Order> orderList;
    private OrderListAdapter adapter;
    private FloatingActionButton fab;
    private Navigator navigator;
    private SubjectList subList;
    private List<Subject> subjects = new ArrayList<>();
    private Profile profile;
    private Bundle savedState = null;
    private OrderTypeList orderTypeList;
    private ArrayList<OrderType> orderTypes;
    private Order order;

    private ArrayList<String> orderIdList = new ArrayList<>();
    private ArrayList<Order> studentOrderList = new ArrayList<>();

    private String curTab;


    @InjectView(R.id.order_list_rv_id)      RecyclerView orderListrv;
    @InjectView(R.id.orderTypeSpinner)      SearchableSpinner orderTypeSpinner;
    @InjectView(R.id.subjectSpinner)        SearchableSpinner subjectSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.inject(this, v);

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        Type subListType = new TypeToken<SubjectList>(){}.getType();
        subList = (SubjectList) Cache.getCacheManager().get("subjectList", SubjectList.class, subListType);
        Type orderTypeListType = new TypeToken<OrderTypeList>(){}.getType();
        orderTypeList = (OrderTypeList) Cache.getCacheManager().get("orderTypeList", OrderTypeList.class, orderTypeListType);

        Bundle bundle = getArguments();
        if (bundle.getString("key").equals("0")) {
                curTab = "active";
                ///orderList = orders in progress
        } else {
                curTab = "completed";
                ///orderList = done orders
        }

        HighFiveHttpClient.initCookieStore(getContext());




        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, getSubjectNames());

        subjectSpinner.setAdapter(subjectAdapter);
        subjectSpinner.setTitle("Выберите предмет");
        subjectSpinner.setPositiveButton("OK");



        ArrayAdapter<String> ordeTypeAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, getOrderTypeNames());


        orderTypeSpinner.setAdapter(ordeTypeAdapter);
        orderTypeSpinner.setTitle("Выберите тип работы");
        orderTypeSpinner.setPositiveButton("OK");

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.navigateToAddOrder();
            }
        });
        if (profile.getType().equals("teacher")) {
            fab.setVisibility(View.GONE);
        }
        resetAdapter();

        RequestParams params = new RequestParams();
        params.put("offset", 0);
        params.put("limit", 50);
        if (profile.getType().equals("teacher")) {
            HighFiveHttpClient.get("orders", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONObject contents = response.getJSONObject("response");
                        JSONArray subjArray = contents.getJSONArray("items");
                        for (int i = 0; i < contents.getInt("count"); i++) {
                            JSONObject current = (JSONObject) subjArray.get(i);
                            Order tmp = new Order(current.getString("id"), current.getString("title"), " ");
                            tmp.setOrderCreatorId(current.getString("creator"));
                            tmp.setSubjectId(current.getString("subject"));
                            tmp.setStatus(current.getString("status"));
                            tmp.setType(current.getString("type"));
                            tmp.setOffer(current.getString("offer"));
                            tmp.setdeadLine(current.getString("deadline"));
                            JSONArray bidArray = current.getJSONArray("bids");
                            tmp.setBidArraySize(bidArray.length());
                            profile.addOrder(tmp);
                        }
                        orderList = profile.getAllOrders();
                        adapter.setOrderList(orderList);
                        adapter.notifyDataSetChanged();
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
        } else {
            getStudentOrders();
        }

        return v;
    }

    private void resetAdapter() {
        if (curTab.equals("active")) {
            orderList = profile.getActiveOrders();
        } else {
            orderList = profile.getCompletedOrders();
        }
        adapter = new OrderListAdapter(orderList, this, subList, orderTypeList, curTab);
        if (orderListrv != null) {
            orderListrv.setAdapter(adapter);
        }
    }

    public void setOrderList(ArrayList<Order> list) {
        this.orderList = list;
        profile.setOrderList(list);
        resetAdapter();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /*subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                orderList = profile.getOrdersByFilter(subjects.get(i).getId(), orderTypes.get(i).getId(), profile.getType());
                resetAdapter();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                if (profile.getType().equals("teacher")) {
                    orderList = profile.getAllOrders();
                } else {
                    orderList = profile.getStudentOrders();
                }
                resetAdapter();
            }
        });*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigator = (Navigator) getActivity();
    }

    @Override
    public void onItemClick(int item) {
            Order order = profile.getOrderById(orderList.get(item).getOrderdId());
            navigator.navigateToOrderDetail(order);
    }

    private List<String> getSubjectNames() {
        if (profile.getType().equals("teacher") && subList != null) {
            subjects = subList.getSubjectList();
        } else {
            if (subList != null) {
                subjects = subList.getStudentSubjectList();
            }
        }

        List<String> list = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            list.add(subjects.get(i).getName());
        }
        return list;
    }

    private List<String> getOrderTypeNames() {
        orderTypes = orderTypeList.getorderTypelist();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < orderTypes.size(); i++) {
            names.add(orderTypes.get(i).getName());
        }
        return names;
    }

    private void getStudentOrders() {
        //final ArrayList<String> orderIdList = profile.getStudentOrderIdList();
        //new LoaderThread(orderIdList, adapter, getActivity()).start();
    }
}


class LoaderThread extends Thread {

    private List<String> orderIds;
    private OrderListAdapter adapter;
    private List<Order> orderList;
    private final Activity activity;


    public LoaderThread(List<String> list, OrderListAdapter adapter, Activity activity) {
        super("LoaderThread");
        this.orderIds = list;
        this.adapter = adapter;
        orderList = new ArrayList<>(orderIds.size());
        this.activity = activity;
    }

    @Override
    public void run() {
        final CountDownLatch latch = new CountDownLatch(orderIds.size());
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
                    finally {
                        latch.countDown();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    latch.countDown();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    latch.countDown();
                }
            };
            handler.setUseSynchronousMode(true);

            HighFiveSyncHttpClient.get("orders/" + orderId, orderParams, handler);
        }
        try {
            latch.await();
            adapter.setOrderList(orderList);
            adapter.notifyDataSetChanged();

            /*activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setOrderList(orderList);
                }
            });*/

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

