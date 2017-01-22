package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderTeacherListAdapter;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;


/**
 * Created by dan on 22.01.17.
 */

public class OrderTeacherListFragment extends Fragment {

    private Profile profile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_teacher_list, container, false);
        RecyclerView orderList = (RecyclerView) v.findViewById(R.id.order_teacher_list_rv_id);

        addOrders();
        OrderTeacherListAdapter adapter = new OrderTeacherListAdapter(profile.getAllOrders());
        orderList.setAdapter(adapter);
        return v;
    }

    void addOrders() {
        if (profile == null) {
            profile = new Profile("test", "test");
        }
        Order order = new Order("Инфа для препода", "Завтра будет кр, 8 класс");
        Order order1 = new Order("Русский язык", "Подстраховать на диктанте");
        Order order2 = new Order("География", "проверочная работа");
        Order order3 = new Order("Геометрия", "Контрльная работа, подстраховать");
        Order order4 = new Order("Английский", "Помочь с домашкой");
        profile.addOrder(order);
        profile.addOrder(order1);
        profile.addOrder(order2);
        profile.addOrder(order3);
        profile.addOrder(order4);

    }
}
