package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.App;
import com.highfive.highfive.Items;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.Response;
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
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by dan on 26.11.16.
 */

public class OrderListFragment extends Fragment implements OrderListAdapter.OnItemClickListener{

    private List<Order> orderList = new ArrayList<>();
    private OrderListAdapter adapter;
    private FloatingActionButton fab;
    private Navigator navigator;
    private SubjectList subList;
    private List<Subject> subjects = new ArrayList<>();
    private Profile profile;
    private OrderTypeList orderTypeList;
    private ArrayList<OrderType> orderTypes = new ArrayList<>();

    private String curTab;


    @InjectView(R.id.order_list_rv_id)              RecyclerView orderListrv;
    @InjectView(R.id.card_order_list_subject)       CardView cardSubj;
    @InjectView(R.id.card_order_list_ordertypes)    CardView cardOrderTypes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.inject(this, v);
        Bundle bundle = getArguments();
        curTab = bundle.getString("key");

        adapter = new OrderListAdapter(orderList, this, subList, orderTypeList, curTab);
        orderListrv.setAdapter(adapter);
        //resetAdapter();

        getUsersOrders(curTab);
        cardSubj.setVisibility(View.GONE);
        cardOrderTypes.setVisibility(View.GONE);

        /*if (profile.getType().equals("teacher")) {
            if (curTab.equals("active")) {
                getUsersOrders("completed");
            } else {
                getUsersOrders("completed");
            }
        }
        if (profile.getType().equals("student")){
            if (curTab.equals("active")) {
                getUsersOrders("active");
            } else {
                getUsersOrders("completed");
            }
        }*/

        /*ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, getSubjectNames());


        subjectSpinner.setAdapter(subjectAdapter);
        subjectSpinner.setTitle("Выберите предмет");
        subjectSpinner.setPositiveButton("OK");

        ArrayAdapter<String> ordeTypeAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, getOrderTypeNames());


        orderTypeSpinner.setAdapter(ordeTypeAdapter);
        orderTypeSpinner.setTitle("Выберите тип работы");
        orderTypeSpinner.setPositiveButton("OK");
*/
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> navigator.navigateToAddOrder());
        if (profile.getType().equals("teacher")) {
            fab.setVisibility(View.GONE);
        }

        /*if (profile.getType().equals("teacher")) {

        } else {
            List<Observable<Response<Order>>> observables = new ArrayList<>();
            for (String orderId : profile.getStudentOrderIdList()) {

                // example how to "cache" objects and re-GET only those orders, which observable returned error
                App.getApi()
                        .getOrderDetailsById(orderId)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<Response<Order>, Pair<Integer, Order>>() {
                            int index = 0;

                            @Override
                            public Pair<Integer, Order> call(Response<Order> orderResponse) {
                                return new Pair<>(index++, orderResponse.getResponse());
                            }
                        })
                        .retry(5)
                        .distinct(integerOrderPair -> integerOrderPair.first)
                        .map(integerOrderPair -> integerOrderPair.second)
                        ;


                observables.add(App.getApi().getOrderDetailsById(orderId).subscribeOn(Schedulers.io()).retry(5).onErrorReturn(t -> null));
            }
            Observable.zip(observables, orderResponseObjects -> {
                List<Order> orders = new ArrayList<>();
                for (Object obj : orderResponseObjects) {
                    if (obj != null) {
                        orders.add(((Response<Order>) obj).getResponse());
                    }
                }
                return orders;
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Order>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(List<Order> orders) {
                    profile.setOrderList((ArrayList<Order>) orders);
                    orderList = (ArrayList<Order>) orders;
                    adapter.setOrderList(orders);
                    adapter.notifyDataSetChanged();
                }
            });
        }*/

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        /*subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (curTab.equals("active")) {
                    if (profile.getType().equals("teacher")) {
                        orderList.clear();
                        getTeacherActiveOrders();
                    } else {
                        orderList = profile.getOrdersByFilter(
                                subjects.get(i).getId(),
                                orderTypes.get(orderTypeSpinner.getSelectedItemPosition()).getId());
                    }
                } else {

                }

                if (profile.getType().equals("student")) {
                    orderList = profile.getOrdersByFilter(
                            subjects.get(i).getId(),
                            orderTypes.get(orderTypeSpinner.getSelectedItemPosition()).getId());
                } else {
                }
                adapter.setOrderList(orderList);
                adapter.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        orderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (profile.getType().equals("student")) {
                    orderList = profile.getOrdersByFilter(
                            subjects.get(subjectSpinner.getSelectedItemPosition()).getId(),
                            orderTypes.get(i).getId());
                } else {
                    orderList.clear();
                    getTeacherActiveOrders();
                }
                adapter.setOrderList(orderList);
                adapter.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = (Navigator) getActivity();

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        Type subListType = new TypeToken<SubjectList>(){}.getType();
        subList = (SubjectList) Cache.getCacheManager().get("subjectList", SubjectList.class, subListType);
        Type orderTypeListType = new TypeToken<OrderTypeList>(){}.getType();
        orderTypeList = (OrderTypeList) Cache.getCacheManager().get("orderTypeList", OrderTypeList.class, orderTypeListType);

        HighFiveHttpClient.initCookieStore(getContext());

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

    public void getTeacherActiveOrders() {
        RequestParams params = new RequestParams();
        params.put("offset", 0);
        params.put("limit", 50);

//        if (subjectSpinner.getSelectedItemPosition() != 0) {
//            params.put("subjectId", subjects.get(subjectSpinner.getSelectedItemPosition()).getId());
//        }
//        if (orderTypeSpinner.getSelectedItemPosition() != 0) {
//            params.put("typeId", orderTypes.get(orderTypeSpinner.getSelectedItemPosition()).getId());
//        }
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
                        orderList.add(tmp);
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

    private void getUsersOrders(String status) {
        App.getApi()
                .getUsersOrders(HighFiveHttpClient.getTokenCookie().getValue(), profile.getUid(), status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Items<Order>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Response<Items<Order>> orderResponse) {
                        orderList = orderResponse.getResponse().items();
                        adapter.setOrderList(orderList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
