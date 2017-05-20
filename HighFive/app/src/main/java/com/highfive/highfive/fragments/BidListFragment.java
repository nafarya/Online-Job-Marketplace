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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.App;
import com.highfive.highfive.LandingActivity;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.BidListAdapter;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.Profile;
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
    private String bidStatus;
    private Profile profile;

    @InjectView(R.id.fragment_bid_list_rv)      RecyclerView bidListRv;
    @InjectView(R.id.bid_list_no_bids_text)     TextView nobids;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bid_list, container, false);

        Bundle bundle = getArguments();
        bidList = new ArrayList<>();
        bidList = bundle.getParcelableArrayList("bidList");
        orderId = bundle.getString("orderId");
        bidStatus = bundle.getString("bidStatus");
        ButterKnife.inject(this, v);
        if (bidList.size() != 0) {
            nobids.setVisibility(View.GONE);
            adapter = new BidListAdapter(bidList, this, getContext(), bidStatus, LandingActivity.userType);
            bidListRv.setAdapter(adapter);
            parseBidsProfileInfo();

        } else {
            bidListRv.setVisibility(View.GONE);
        }





        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigator = (Navigator) getActivity();
    }


    @Override
    public void onItemClick(int item) {
        navigator.navigateToBidListComments(bidList.get(item).getBidComments(),
                bidList.get(item).getBidCreatorId(), bidList.get(item).getBidId());
    }

    @Override
    public void onChooseButtonClick(int item) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Call<Response> call = App.getApi().chooseBidForOrder(HighFiveHttpClient.getTokenCookie().getValue(),
                                orderId, bidList.get(item).getBidId());
                        call.enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(getContext(), "Исполнитель выбран", Toast.LENGTH_SHORT).show();
                                    navigator.navigateToChooseOrder();
                                }
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
