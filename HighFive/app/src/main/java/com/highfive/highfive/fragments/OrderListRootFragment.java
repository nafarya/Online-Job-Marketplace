package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highfive.highfive.R;
import com.highfive.highfive.adapters.OrderListPagerAdapter;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 31.03.17.
 */

public class OrderListRootFragment extends Fragment {

    private OrderListPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list_root, container, false);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("В аукционе"));
        tabLayout.addTab(tabLayout.newTab().setText("В работе"));
        tabLayout.addTab(tabLayout.newTab().setText("Ждет подтверждения"));
        tabLayout.addTab(tabLayout.newTab().setText("Отменены"));
        tabLayout.addTab(tabLayout.newTab().setText("На гарантии"));
        tabLayout.addTab(tabLayout.newTab().setText("На доработке"));
        tabLayout.addTab(tabLayout.newTab().setText("Завершены"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) v.findViewById(R.id.pager);
        adapter = new OrderListPagerAdapter
                (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

}

