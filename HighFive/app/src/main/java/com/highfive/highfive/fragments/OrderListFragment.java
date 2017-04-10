package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

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


    @InjectView(R.id.order_list_rv_id)      RecyclerView orderListrv;
    @InjectView(R.id.orderTypeSpinner)      SearchableSpinner orderTypeSpinner;
    @InjectView(R.id.subjectSpinner)        SearchableSpinner subjectSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.inject(this, v);

        Bundle bundle = getArguments();
        if (bundle.get("key").equals(0)) {
                ///orderList = orders in progress
        } else {
                ///orderList = done orders

        }

        HighFiveHttpClient.initCookieStore(getContext());

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        Type subListType = new TypeToken<SubjectList>(){}.getType();
        subList = (SubjectList) Cache.getCacheManager().get("subjectList", SubjectList.class, subListType);
        Type orderTypeListType = new TypeToken<OrderTypeList>(){}.getType();
        orderTypeList = (OrderTypeList) Cache.getCacheManager().get("orderTypeList", OrderTypeList.class, orderTypeListType);


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


        if(savedState != null) {
            orderList = savedState.getParcelableArrayList("orderList");
        } else {
            if (profile.getType().equals("teacher")) {
                orderList = profile.getAllOrders();
            } else {
                orderList = profile.getStudentOrders();
            }
        }
        savedState = null;



        resetAdapter();

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOrderFragment fragment = new AddOrderFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
            }
        });
        if (profile.getType().equals("teacher")) {
            fab.setVisibility(View.GONE);
        }

        return v;
    }

    private void resetAdapter() {
        adapter = new OrderListAdapter(orderList, this, subList, orderTypeList);
        orderListrv.setAdapter(adapter);
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

        RequestParams params = new RequestParams();
        params.put("offset", 0);
        params.put("limit", 50);

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
                        Order tmp = new Order(current.getString("id"), current.getString("title"), " ");
                        tmp.setOrderCreatorId(current.getString("creator"));
                        tmp.setSubjectId(current.getString("subject"));
                        tmp.setStatus(current.getString("status"));
                        tmp.setType(current.getString("type"));
                        tmp.setOffer(current.getString("offer"));
                        tmp.setdeadLine(current.getString("deadline"));
                        profile.addOrder(tmp);
                    }
                    OrderListAdapter adapter;
                    if (profile.getType().equals("student")) {
                        adapter = new OrderListAdapter(profile.getStudentOrders(),
                                OrderListFragment.this, subList, orderTypeList);
                    } else {
                        adapter = new OrderListAdapter(profile.getAllOrders(),
                                OrderListFragment.this, subList, orderTypeList);
                    }
                    if (orderListrv != null) {
                        orderListrv.setAdapter(adapter);
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

}
