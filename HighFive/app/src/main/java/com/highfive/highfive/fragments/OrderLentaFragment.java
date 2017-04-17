package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderListAdapter;
import com.highfive.highfive.listeners.EndlessRecyclerOnScrollListener;
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
 * Created by dan on 16.04.17.
 */

public class OrderLentaFragment extends Fragment implements OrderListAdapter.OnItemClickListener{

    private Navigator navigator;
    private Profile profile;
    private SubjectList subList;
    private List<Subject> subjects;
    private OrderTypeList orderTypeList;
    private List<OrderType> orderTypes;

    private List<Order> orderList;
    private OrderListAdapter adapter;

    @InjectView(R.id.order_list_rv_id)      RecyclerView orderListrv;
    @InjectView(R.id.orderTypeSpinner)      SearchableSpinner orderTypeSpinner;
    @InjectView(R.id.subjectSpinner)        SearchableSpinner subjectSpinner;
    @InjectView(R.id.fab)                   FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.inject(this, v);

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

        if (profile.getType().equals("teacher")) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setOnClickListener(view -> navigator.navigateToAddOrder());
        }

        adapter = new OrderListAdapter(orderList, this, subList, orderTypeList, "lenta");
        orderListrv.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        orderListrv.setLayoutManager(linearLayoutManager);
        orderListrv.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getTeachersLenta(current_page * 50);
            }
        });

        //getTeachersLenta(0);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = (Navigator) getActivity();
        HighFiveHttpClient.initCookieStore(getContext());
        orderList = new ArrayList<>();

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        Type subListType = new TypeToken<SubjectList>(){}.getType();
        subList = (SubjectList) Cache.getCacheManager().get("subjectList", SubjectList.class, subListType);
        Type orderTypeListType = new TypeToken<OrderTypeList>(){}.getType();
        orderTypeList = (OrderTypeList) Cache.getCacheManager().get("orderTypeList", OrderTypeList.class, orderTypeListType);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                orderList.clear();
                getTeachersLenta(0);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        orderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                orderList.clear();
                getTeachersLenta(0);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onItemClick(int item) {
        Order order = orderList.get(item);
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

    public void getTeachersLenta(int startPos) {
        RequestParams params = new RequestParams();
        params.put("offset", startPos);
        params.put("limit", startPos + 50);

        if (subjectSpinner.getSelectedItemPosition() != 0) {
            params.put("subjectId", subjects.get(subjectSpinner.getSelectedItemPosition()).getId());
        }
        if (orderTypeSpinner.getSelectedItemPosition() != 0) {
            params.put("typeId", orderTypes.get(orderTypeSpinner.getSelectedItemPosition()).getId());
        }
        List<Order> tmpOrderList = new ArrayList<>();
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
                        List<String> bids = new ArrayList<>();
                        for (int j = 0; j < bidArray.length(); j++) {
                            bids.add(bidArray.getString(j));
                        }
                        tmp.setBidsIds(bids);
                        tmpOrderList.add(tmp);
                    }
                    if (startPos == 0) {
                        orderList = tmpOrderList;
                    } else {
                        orderList.addAll(tmpOrderList);
                    }
                    if (orderList.size() == 0) {
                        Toast.makeText(getContext(), "Таких заказов нет, выберите другие фильтры", Toast.LENGTH_LONG).show();
                    }
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
    }
}
