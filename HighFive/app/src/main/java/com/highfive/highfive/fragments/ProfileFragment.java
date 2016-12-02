package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highfive.highfive.R;
import com.highfive.highfive.adapters.ProfileCommentsAdapter;
import com.highfive.highfive.model.Profile;

/**
 * Created by dan on 26.11.16.
 */

public class ProfileFragment extends Fragment {
    private RecyclerView rv;
    private ProfileCommentsAdapter adapter;
    private Profile profile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        rv = (RecyclerView) v.findViewById(R.id.profile_comments_rv);
        profile = new Profile("Lev", "Prosorov");
        profile.addComment("comment1");
        profile.addComment("comment2");
        profile.addComment("comment3");
        profile.addComment("comment4");
        adapter = new ProfileCommentsAdapter(profile.getAllComments());
        rv.setAdapter(adapter);
        return v;
    }
}
