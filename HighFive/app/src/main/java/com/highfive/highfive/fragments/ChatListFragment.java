package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.App;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderListAdapter;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.OrderType;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.Subject;
import com.highfive.highfive.model.SubjectList;
import com.highfive.highfive.responseModels.Items;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dan on 21.04.17.
 */

public class ChatListFragment extends Fragment implements OrderListAdapter.OnItemClickListener{

    @InjectView(R.id.chat_list_rv_id)           RecyclerView chatRv;

    private List<Order> orderList = new ArrayList<>();
    private OrderListAdapter adapter;

    private Navigator navigator;
    private SubjectList subList;
    private List<Subject> subjects = new ArrayList<>();
    private Profile profile;
    private OrderTypeList orderTypeList;
    private ArrayList<OrderType> orderTypes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        ButterKnife.inject(this, v);

        adapter = new OrderListAdapter(orderList, this, subList, orderTypeList, "in work");
        chatRv.setAdapter(adapter);
        getUsersOrders("in work");

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

    @Override
    public void onItemClick(int item) {
        navigator.navigateToChat(orderList.get(item));
    }
}
