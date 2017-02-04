package com.highfive.highfive.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.highfive.highfive.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dan on 03.02.17.
 */

public class ProfileStudentFragment extends Fragment {

    @InjectView(R.id.fragment_profile_balance)          TextView profileBalance;
    @InjectView(R.id.fragment_profile_student_rating_bar)       RatingBar profileRating;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_student, container, false);

        ButterKnife.inject(this, v);

        Drawable progress = profileRating.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.rgb(255,215,0));
        return v;
    }
}
