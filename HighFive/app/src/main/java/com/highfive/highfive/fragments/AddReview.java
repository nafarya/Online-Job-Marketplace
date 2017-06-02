package com.highfive.highfive.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.highfive.highfive.App;
import com.highfive.highfive.R;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.util.HighFiveHttpClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by dan on 02.06.17.
 */

public class AddReview extends Fragment{

    @InjectView(R.id.negative)      ImageButton negative;
    @InjectView(R.id.neutral)       ImageButton neutral;
    @InjectView(R.id.positive)      ImageButton positive;
    @InjectView(R.id.review)        EditText review;

    private String id;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_review, container, false);
        ButterKnife.inject(this, v);
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        negative.setOnClickListener(view -> sendReview("negative"));
        neutral.setOnClickListener(view -> sendReview("neutral"));
        positive.setOnClickListener(view -> sendReview("positive"));

        return v;
    }

    private void sendReview(String type) {
        Call<Response> call = App.getApi().addReview(HighFiveHttpClient.getTokenCookie().getValue(),
                id, type, String.valueOf(review.getText()));
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Отзыв добавлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Вы уже добавляли отзыв", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                int x = 0;
            }
        });
    }
}
