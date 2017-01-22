package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.ProfileCommentsAdapter;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.iainconnor.objectcache.CacheManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 26.11.16.
 */

public class ProfileFragment extends Fragment {
    private RecyclerView rv;
    private ProfileCommentsAdapter adapter;
    private Profile profile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (HighFiveHttpClient.getUidCookie() != null) {
            final CacheManager cacheManager = Cache.getCacheManager();
            Type profileType = new TypeToken<Profile>(){}.getType();
            Profile myProfile = (Profile) cacheManager.get("profile", Profile.class, profileType);
            if (myProfile != null && myProfile.getUid().equals(HighFiveHttpClient.getUidCookie().getValue())) {
                profile = myProfile;
            } else {
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
                                    cacheManager.put("profile", profile);
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
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        rv = (RecyclerView) v.findViewById(R.id.profile_comments_rv);
        ButterKnife.inject(this, v);

        profile = new Profile("Lev", "Prosorov");
        createComments();

        adapter = new ProfileCommentsAdapter(profile.getAllComments());
        rv.setAdapter(adapter);
        return v;
    }

    void createComments() {
        for (int i = 0; i < 10; i++) {
            profile.addComment("Comment" + i + " bla bla bla");
        }
    }
}
