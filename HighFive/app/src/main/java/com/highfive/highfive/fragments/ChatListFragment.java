package com.highfive.highfive.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import retrofit2.Call;
import retrofit2.Callback;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dan on 21.04.17.
 */

public class ChatListFragment extends Fragment implements OrderListAdapter.OnItemClickListener{

    @InjectView(R.id.chat_list_rv_id)           RecyclerView chatRv;
    @InjectView(R.id.chat_list_no_orders)       TextView noOrders;

    private List<Order> orderList = new ArrayList<>();
    private OrderListAdapter adapter;

    private Navigator navigator;
    private SubjectList subList;
    private List<Subject> subjects = new ArrayList<>();
    private Profile profile;
    private OrderTypeList orderTypeList;
    private ArrayList<OrderType> orderTypes = new ArrayList<>();
    private List<Order> tmp = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        ButterKnife.inject(this, v);

        adapter = new OrderListAdapter(orderList, this, subList, orderTypeList, "chat", profile);
        chatRv.setAdapter(adapter);
        getUsersOrders("in work", 0);


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


    private void getUsersOrders(String status, int offset) {
        int limit = offset + 50;
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
                        adapter.setOrderList(orderList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(int item) {
        navigator.navigateToChat(orderList.get(item));
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
                                getUsersOrders("in work", 0);
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

    }

    @Override
    public void addReview(int item) {

    }
}
