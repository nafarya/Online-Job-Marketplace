package com.highfive.highfive.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.highfive.highfive.fragments.OrderListFragment;

/**
 * Created by dan on 31.03.17.
 */

public class OrderListPagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;

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
                bundle.putString("key", "0");
                OrderListFragment tab1 = new OrderListFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                bundle = new Bundle();
                bundle.putString("key", "1");
                OrderListFragment tab2 = new OrderListFragment();
                tab2.setArguments(bundle);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
