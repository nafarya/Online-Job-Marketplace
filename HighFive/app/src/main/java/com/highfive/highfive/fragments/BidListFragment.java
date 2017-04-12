package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.BidListAdapter;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dan on 31.03.17.
 */

public class BidListFragment extends Fragment implements BidListAdapter.OnItemClickListener{

    private ArrayList<Bid> bidList;
    private Navigator navigator;

    @InjectView(R.id.fragment_bid_list_rv)      RecyclerView bidListRv;
    @InjectView(R.id.bid_list_no_bids_text)     TextView nobids;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bid_list, container, false);

        Bundle bundle = getArguments();
        bidList = bundle.getParcelableArrayList("bidList");
        ButterKnife.inject(this, v);
        if (bidList.size() != 0) {
            nobids.setVisibility(View.GONE);
            BidListAdapter adapter = new BidListAdapter(bidList, this);
            bidListRv.setAdapter(adapter);
        } else {
            bidListRv.setVisibility(View.GONE);
        }

        Type profileType = new TypeToken<Profile>(){}.getType();
        Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);





        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigator = (Navigator) getActivity();
    }


    @Override
    public void onItemClick(int item) {
        navigator.navigateToBidListComments(bidList.get(item).getBidComments(),
                bidList.get(item).getBidCreatorId());
    }
}
