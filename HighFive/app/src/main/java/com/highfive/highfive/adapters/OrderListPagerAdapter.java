package com.highfive.highfive.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.highfive.highfive.fragments.OrderListFragment;
import com.highfive.highfive.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 31.03.17.
 */

public class OrderListPagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;
    private ArrayList<Order> orderList;

    public OrderListPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.numOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle;
        switch (position) {
            case 0:
                bundle = new Bundle();
                bundle.putString("key", "active");
                bundle.putParcelableArrayList("orderList", orderList);
                OrderListFragment tab0 = new OrderListFragment();
                tab0.setArguments(bundle);
                return tab0;
            case 1:
                bundle = new Bundle();
                bundle.putString("key", "in work");
                bundle.putParcelableArrayList("orderList", orderList);
                OrderListFragment tab1 = new OrderListFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 2:
                bundle = new Bundle();
                bundle.putString("key", "in work");
                bundle.putParcelableArrayList("orderList", orderList);
                OrderListFragment tab2 = new OrderListFragment();
                tab2.setArguments(bundle);
                return tab2;
            case 3:
                bundle = new Bundle();
                bundle.putString("key", "waiting for author");
                bundle.putParcelableArrayList("orderList", orderList);
                OrderListFragment tab3 = new OrderListFragment();
                tab3.setArguments(bundle);
                return tab3;
            case 4:
                bundle = new Bundle();
                bundle.putString("key", "cancelled");
                bundle.putParcelableArrayList("orderList", orderList);
                OrderListFragment tab4 = new OrderListFragment();
                tab4.setArguments(bundle);
                return tab4;
            case 5:
                bundle = new Bundle();
                bundle.putString("key", "on guarantee");
                bundle.putParcelableArrayList("orderList", orderList);
                OrderListFragment tab5 = new OrderListFragment();
                tab5.setArguments(bundle);
                return tab5;
            case 6:
                bundle = new Bundle();
                bundle.putString("key", "in rework");
                bundle.putParcelableArrayList("orderList", orderList);
                OrderListFragment tab6 = new OrderListFragment();
                tab6.setArguments(bundle);
                return tab6;
            case 7:
                bundle = new Bundle();
                bundle.putString("key", "closed");
                bundle.putParcelableArrayList("orderList", orderList);
                OrderListFragment tab7 = new OrderListFragment();
                tab7.setArguments(bundle);
                return tab7;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }
}
