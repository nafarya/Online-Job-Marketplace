package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderListAdapter;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;

import java.util.Date;

/**
 * Created by dan on 26.11.16.
 */

public class OrderListFragment extends Fragment {
    RecyclerView orderList;
    Profile profile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);
        orderList = (RecyclerView) v.findViewById(R.id.order_list_rv_id);
        profile = new Profile("Koko", "Kokoko");
        Order order = new Order("Mathematics", "HELP ME in Mathematics");
        profile.addOrder(order);
        OrderListAdapter adapter = new OrderListAdapter(profile.getAllOrders());
        orderList.setAdapter(adapter);
        return v;
    }
}