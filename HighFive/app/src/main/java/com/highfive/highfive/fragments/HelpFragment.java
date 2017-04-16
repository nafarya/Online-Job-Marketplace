package com.highfive.highfive.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.highfive.highfive.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dan on 26.11.16.
 */

public class HelpFragment extends Fragment {

    @InjectView(R.id.fragment_help_ask_questions)       LinearLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.inject(this, v);
        layout.setOnClickListener(view -> {
            Intent mailer = new Intent(Intent.ACTION_SEND);
                            mailer.setType("text/plain");
                            mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{"yareshu-support@gmail.com"});
                            mailer.putExtra(Intent.EXTRA_SUBJECT, "Вопросы и предложения");
                            mailer.putExtra(Intent.EXTRA_TEXT, "Напишите ваш вопрос");
                            startActivity(Intent.createChooser(mailer, "Send email..."));
        });
        return v;
    }
}
