package com.highfive.highfive.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.ProfileCommentsAdapter;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 26.11.16.
 */

public class ProfileFragment extends Fragment {
    private RecyclerView rv;
    private ProfileCommentsAdapter adapter;
    private Profile profile;

    @InjectView(R.id.fragment_profile_balance)          TextView profileBalance;
    @InjectView(R.id.fragment_profile_rating_bar)       RatingBar profileRating;
    @InjectView(R.id.fragment_profile_tasks_sent)       TextView profileTasksSent;
    @InjectView(R.id.fragment_profile_tasks_solved)     TextView profileTasksSolved;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (HighFiveHttpClient.getUidCookie() != null) {
            Type profileType = new TypeToken<Profile>(){}.getType();
            Profile myProfile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
            if (myProfile != null && myProfile.getUid().equals(HighFiveHttpClient.getUidCookie().getValue())) {
                profile = myProfile;
            } else {
                //regenerateProfile();
                //show a profile stub maybe?
            }
        }
    }

    private void regenerateProfile() {
        HighFiveHttpClient.get("users/" + HighFiveHttpClient.getUidCookie().getValue(), null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            JSONObject contents = (JSONObject) response.get("response");
                            profile = new Profile(contents.getString("email"),
                                    contents.getString("id"),
                                    contents.getString("username"),
                                    contents.getString("balance"),
                                    contents.getJSONObject("rating").getInt("negative"),
                                    contents.getJSONObject("rating").getInt("positive"),
                                    contents.getString("firstName"),
                                    contents.getString("secondName"),
                                    contents.getString("type"));
                            Cache.getCacheManager().put("profile", profile);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
    }

    private void fillProfileData() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        rv = (RecyclerView) v.findViewById(R.id.profile_comments_rv);
        ButterKnife.inject(this, v);


        fillProfileData();

        createComments();
        adapter = new ProfileCommentsAdapter(profile.getAllComments());
        rv.setAdapter(adapter);


        Drawable progress = profileRating.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.rgb(255,215,0));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        createComments();
    }

    void createComments() {
        if (profile == null) {
            profile = new Profile("test", "test");
        }
        for (int i = 0; i < 5; i++) {
            profile.addComment("Comment" + i + " bla bla bla");
        }
    }
}
