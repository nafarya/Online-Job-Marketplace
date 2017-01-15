package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highfive.highfive.R;

/**
 * Created by heat_wave on 15.01.17.
 */

public class ScreenSlidePageFragment extends Fragment {

    private String title;
    private int mode;

    public static ScreenSlidePageFragment newInstance(int mode, String title) {
        ScreenSlidePageFragment fragmentFirst = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt("mode", mode);
        args.putString("title", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getArguments().getInt("mode", 0);
        title = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        TextView titleText = (TextView) view.findViewById(R.id.modeText);
        titleText.setText(title);
        return view;
    }
}