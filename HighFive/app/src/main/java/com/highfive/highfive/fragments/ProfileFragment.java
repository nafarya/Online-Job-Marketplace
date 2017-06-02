package com.highfive.highfive.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.App;
import com.highfive.highfive.LandingActivity;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.ProfileCommentsAdapter;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.ProfileComment;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dan on 26.11.16.
 */

public class ProfileFragment extends Fragment {
    @InjectView(R.id.fragment_profile_rating_bar)       RatingBar profileRating;
    @InjectView(R.id.fragment_profile_negative_rating)  TextView profileNegativeRating;
    @InjectView(R.id.fragment_profile_positive_rating)  TextView profilePositiveRating;
    @InjectView(R.id.fragment_profile_teacher_login)    TextView profileLogin;
    @InjectView(R.id.fragment_profile_about)            TextView profileAbout;
    @InjectView(R.id.donut_progress)                    DonutProgress donutProgress;
    @InjectView(R.id.avatar)                            ImageView avatar;
    @InjectView(R.id.profile_comments_rv)               RecyclerView rv;
    @InjectView(R.id.fragment_profile_teacher_completed_orders)     TextView completedOrders;
    @InjectView(R.id.fragment_profile_teacher_inprogress_orders)    TextView inprogressOrders;

    private ProfileCommentsAdapter adapter;
    private Profile profile;
    private List<ProfileComment> comments = new ArrayList<>();
    private List<Profile> commentAuthors = new ArrayList<>();
    private Navigator navigator;
    private List<String> photoPaths;
    private ArrayList<String> filePaths;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (HighFiveHttpClient.getUidCookie() != null) {
            Type profileType = new TypeToken<Profile>(){}.getType();
            Profile myProfile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
            if (myProfile != null && myProfile.getUid().equals(HighFiveHttpClient.getUidCookie().getValue())) {
                profile = myProfile;
                comments = profile.getAllComments();
            } else {
                //regenerateProfile();
                //show a profile stub maybe?
            }
        }
        navigator = (Navigator) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        filePaths = new ArrayList<>();
        ButterKnife.inject(this, v);

        avatar.setOnClickListener(view -> {
            LandingActivity.FILE_CODE = 1;
            navigator.pickPhoto();

        });
        fillProfileData();
        return v;
    }

    private void fillProfileData() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        Collections.reverse(comments);

        adapter = new ProfileCommentsAdapter(comments, getContext());
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(false);
        parseCommentsAuthorsInfo();

        inprogressOrders.setText(String.valueOf(profile.getActiveOrders()));
        Picasso.with(getContext()).load("https://yareshu.ru/" + profile.getAvatar()).into(avatar);

        float rating = (float)profile.getProfileRating();
        if (rating != 0.0) {
            float rat;
            if (profile.getRating().getPositive() >= profile.getRating().getNegative()) {
                rat = (float)(1 - profile.getRating().getNegative() /profile.getRating().getPositive());
                donutProgress.setText(String.format("%.1f", rat * 100) + "%");
            } else {
                rat = (float)(1 - profile.getRating().getPositive()/profile.getRating().getNegative());
            }
            donutProgress.setProgress(rat * 100);
            donutProgress.setTextColor(Color.rgb(69,90, 100));
            donutProgress.setUnfinishedStrokeColor(Color.rgb(246, 102, 92));
            donutProgress.setFinishedStrokeColor(Color.rgb(159, 214, 102));
        } else {
            donutProgress.setText("N/A");
        }

        LayerDrawable drawable = (LayerDrawable) profileRating.getProgressDrawable();
        Drawable progress = drawable.getDrawable(2);
        DrawableCompat.setTint(progress, getResources().getColor(R.color.gold));
        progress = drawable.getDrawable(1);
        DrawableCompat.setTintMode(progress, PorterDuff.Mode.DST_ATOP);
        DrawableCompat.setTint(progress, getResources().getColor(R.color.gold));
        DrawableCompat.setTintMode(progress, PorterDuff.Mode.SRC_ATOP);
        DrawableCompat.setTint(progress, getResources().getColor(R.color.darkGrey));
        progress = drawable.getDrawable(0);
        DrawableCompat.setTint(progress, getResources().getColor(R.color.darkGrey));

        profilePositiveRating.setText(String.valueOf((int)profile.getRating().getPositive()));
        profileNegativeRating.setText(String.valueOf((int)profile.getRating().getNegative()));
        profileRating.setRating((float)profile.getProfileRating());
        profileLogin.setText(profile.getUsername());
        profileAbout.setText(profile.getDescription());
    }

    private void parseCommentsAuthorsInfo() {
        List<rx.Observable<Response<Profile>>> observables = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            String profileId = comments.get(i).getAuthor();
            observables.add(App.getApi().getUserById(profileId).subscribeOn(Schedulers.io()).retry(5).onErrorReturn(t -> null));
        }
        rx.Observable.zip(observables, profileResponseObjects -> {
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
                commentAuthors = profiles;
                Collections.reverse(commentAuthors);
                adapter.setProfileList(commentAuthors);
                adapter.notifyDataSetChanged();
            }
        });
    }



}
