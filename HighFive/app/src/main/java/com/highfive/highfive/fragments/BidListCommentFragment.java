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
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.BidCommentsAdapter;
import com.highfive.highfive.adapters.BidListAdapter;
import com.highfive.highfive.model.BidComment;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dan on 02.04.17.
 */

public class BidListCommentFragment extends Fragment {

    private ArrayList<BidComment> comments;
    private String creatorId;

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
            BidCommentsAdapter adapter =  new BidCommentsAdapter(comments, creatorId);
            commentRv.setAdapter(adapter);
        } else {
            commentRv.setVisibility(View.GONE);
        }



        Type profileType = new TypeToken<Profile>(){}.getType();
        Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);

        return v;
    }

}
