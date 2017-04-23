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
import com.highfive.highfive.App;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.BidListAdapter;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.util.Cache;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dan on 31.03.17.
 */

public class BidListFragment extends Fragment implements BidListAdapter.OnItemClickListener{

    private List<Bid> bidList;
    private List<Profile> profileList;
    private Navigator navigator;
    private BidListAdapter adapter;
    private String orderId;

    @InjectView(R.id.fragment_bid_list_rv)      RecyclerView bidListRv;
    @InjectView(R.id.bid_list_no_bids_text)     TextView nobids;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bid_list, container, false);

        Bundle bundle = getArguments();
        bidList = new ArrayList<>();
        bidList = bundle.getParcelableArrayList("bidList");
        orderId = bundle.getString("orderId");
        ButterKnife.inject(this, v);
        if (bidList.size() != 0) {
            nobids.setVisibility(View.GONE);
            adapter = new BidListAdapter(bidList, this, getContext());
            bidListRv.setAdapter(adapter);
            parseBidsProfileInfo();

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

    @Override
    public void onChooseButtonClick(int item, String newStatus) {
        navigator.navigateToStatusChangeDialog(orderId, newStatus, bidList.get(item).getBidId());
        adapter.notifyDataSetChanged();
    }

    private void parseBidsProfileInfo() {
        List<Observable<Response<Profile>>> observables = new ArrayList<>();
        for (int i = 0; i < bidList.size(); i++) {
            String profileId = bidList.get(i).getBidCreatorId();
            observables.add(App.getApi().getUserById(profileId).subscribeOn(Schedulers.io()).retry(5).onErrorReturn(t -> null));
        }
        Observable.zip(observables, profileResponseObjects -> {
            List<Profile> profiles = new ArrayList<>();
            for (Object obj : profileResponseObjects) {
                if (obj != null) {
                    profiles.add(((Response<Profile>) obj).getResponse());
                }
            }
            return profiles;
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Profile>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Profile> profiles) {
                profileList = profiles;
                adapter.setProfileList(profileList);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
