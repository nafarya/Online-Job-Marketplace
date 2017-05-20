package com.highfive.highfive.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.App;
import com.highfive.highfive.LandingActivity;
import com.highfive.highfive.listeners.EndlessRecyclerOnScrollListener;
import com.highfive.highfive.responseModels.Items;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.responseModels.Response;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
    private List<Order> tmp = new ArrayList<>();


    @InjectView(R.id.order_list_rv_id)              RecyclerView orderListrv;
    @InjectView(R.id.card_order_list_subject)       CardView cardSubj;
    @InjectView(R.id.card_order_list_ordertypes)    CardView cardOrderTypes;
    @InjectView(R.id.order_list_no_orders)          TextView noOrders;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.inject(this, v);
        Bundle bundle = getArguments();
        curTab = bundle.getString("key");

        adapter = new OrderListAdapter(orderList, this, subList, orderTypeList, curTab, profile);
        orderListrv.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        orderListrv.setLayoutManager(linearLayoutManager);
        orderListrv.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getUsersOrders(curTab, current_page * 50);
            }
        });

        if (profile != null) {
            getUsersOrders(curTab, 0);
        }
        cardSubj.setVisibility(View.GONE);
        cardOrderTypes.setVisibility(View.GONE);


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

        if (profile == null) {
            getProfile();
        }
        if (subList == null) {
            getSubjects();
        }
        if (orderTypeList == null) {
            getOrderTypes();
        }

    }

    @Override
    public void onItemClick(int item) {
            Order order = orderList.get(item);
            navigator.navigateToOrderDetail(order);
    }

    @Override
    public void changeStatusButton(int item, String newStatus) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Call<Response> call = App.getApi().changeOrderStatus(HighFiveHttpClient.getTokenCookie().getValue(),
                                orderList.get(item).getId(),
                                newStatus);
                        call.enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                getUsersOrders(curTab, 0);
                            }

                            @Override
                            public void onFailure(Call<Response> call, Throwable t) {
                                int x = 0;
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Вы уверены?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();

    }

    @Override
    public void teacherSubmitOrder(int item, String action) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Call<Response> call = App.getApi().teacherSubmitOrder(HighFiveHttpClient.getTokenCookie().getValue(),
                                orderList.get(item).getId(),
                                action);
                        call.enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                getUsersOrders(curTab, 0);
                            }

                            @Override
                            public void onFailure(Call<Response> call, Throwable t) {
                                int x = 0;
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Вы уверены?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();
    }

    private List<String> getSubjectNames() {
        if (LandingActivity.userType.equals("teacher") && subList != null) {
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

    private void getUsersOrders(String status, int offset) {
        int limit = offset + 50;
        if (curTab.equals("waitingOrders")) {
            App.getApi()
                    .getWaitingTeacherOrders(HighFiveHttpClient.getTokenCookie().getValue(), profile.getUid(), offset, limit)
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
                            tmp = orderResponse.getResponse().items();
                            if (offset == 0) {
                                orderList = tmp;
                            } else {
                                orderList.addAll(tmp);
                            }
                            if (orderList.size() != 0) {
                                noOrders.setVisibility(View.GONE);
                            }
                            List<Order> rev = orderList;
                            Collections.reverse(rev);
                            adapter.setOrderList(rev);
                            adapter.notifyDataSetChanged();
                        }
                    });
        } else {
            App.getApi()
                    .getUsersOrders(HighFiveHttpClient.getTokenCookie().getValue(), profile.getUid(), status, offset, limit)
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
                            tmp = orderResponse.getResponse().items();
                            if (offset == 0) {
                                orderList = tmp;
                            } else {
                                orderList.addAll(tmp);
                            }
                            if (orderList.size() != 0) {
                                noOrders.setVisibility(View.GONE);
                            }
                            List<Order> rev = orderList;
                            Collections.reverse(rev);
                            adapter.setOrderList(rev);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }



    private void getSubjects() {
        String userType = "all";
        RequestParams params = new RequestParams();
        ArrayList<Subject> subjectList = new ArrayList<>();
        params.add("type", userType);


        HighFiveHttpClient.get("subjects", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    JSONArray subjArray = contents.getJSONArray("items");
                    Subject tmp = new Subject("Все предметы", "all", "all", "all");
                    subjectList.add(tmp);
                    for (int i = 0; i < contents.getInt("count"); i++) {
                        JSONObject current = (JSONObject) subjArray.get(i);
                        tmp = new Subject(
                                current.getString("name"),
                                current.getString("science"),
                                current.getString("difficultyLevel"),
                                current.getString("id")
                        );
                        subjectList.add(tmp);
                    }
                    SubjectList list = new SubjectList(subjectList);
                    Cache.getCacheManager().put("subjectList", list);
                    subList = list;
                    adapter.setSubjectList(list);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void getOrderTypes() {
        RequestParams params = new RequestParams();
        ArrayList<OrderType> typeList = new ArrayList<>();
        params.add("type", "all");
        HighFiveHttpClient.get("ordertypes", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    OrderType tmp = new OrderType("Любой тип", "all", "all");
                    typeList.add(tmp);
                    JSONObject contents = response.getJSONObject("response");
                    JSONArray subjArray = contents.getJSONArray("items");
                    for (int i = 0; i < contents.getInt("count"); i++) {
                        JSONObject current = (JSONObject) subjArray.get(i);
                        typeList.add(new OrderType(current.getString("name"),
                                current.getString("difficultyLevel"),
                                current.getString("id")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OrderTypeList tmpOrderTypeList = new OrderTypeList(typeList);
                Cache.getCacheManager().put("orderTypeList", tmpOrderTypeList);
                orderTypeList = tmpOrderTypeList;
                adapter.setOrderTypeList(orderTypeList);
                adapter.notifyDataSetChanged();

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

    private void getProfile() {
        App.getApi()
                .getUserById(HighFiveHttpClient.getUidCookie().getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Profile>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Response<Profile> orderResponse) {
                        profile = orderResponse.getResponse();
                        Cache.getCacheManager().put("profile", profile);

                    }
                });
    }
}
