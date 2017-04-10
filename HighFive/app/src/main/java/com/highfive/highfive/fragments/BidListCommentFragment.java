package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.BidListAdapter;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by dan on 02.04.17.
 */

public class BidListCommentFragment extends Fragment {

    private ArrayList<String> comments;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bid_list_comments, container, false);

        //Bundle bundle = getArguments();
        //comments = bundle.getParcelableArrayList("comments");
        ButterKnife.inject(this, v);

        Type profileType = new TypeToken<Profile>(){}.getType();
        Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);

        return v;
    }

}
