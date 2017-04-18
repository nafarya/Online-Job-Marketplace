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
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.BidCommentsAdapter;
import com.highfive.highfive.adapters.BidListAdapter;
import com.highfive.highfive.model.BidComment;
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
 * Created by dan on 02.04.17.
 */

public class BidListCommentFragment extends Fragment {

    private ArrayList<BidComment> comments;
    private String creatorId;
    private List<Profile> profileList;
    private BidCommentsAdapter adapter;

    @InjectView(R.id.bid_comment_list_rv_id)                RecyclerView commentRv;
    @InjectView(R.id.bid_list_comments_nocomments_text)     TextView nocomments;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bid_list_comments, container, false);
        ButterKnife.inject(this, v);

        Bundle bundle = getArguments();
        comments = bundle.getParcelableArrayList("bidCommentsList");
        creatorId = bundle.getString("creatorId");

        if (comments.size() > 0) {
            nocomments.setVisibility(View.GONE);
            adapter =  new BidCommentsAdapter(comments, creatorId);
            commentRv.setAdapter(adapter);
            //getCommentProfiles();
        } else {
            commentRv.setVisibility(View.GONE);
        }

        return v;
    }

    private void getCommentProfiles() {
        List<Observable<Response<Profile>>> observables = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            String profileId = comments.get(i).getOwner();
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
                adapter.setCommentProfiles(profileList);
                adapter.notifyDataSetChanged();
            }
        });

    }

}
