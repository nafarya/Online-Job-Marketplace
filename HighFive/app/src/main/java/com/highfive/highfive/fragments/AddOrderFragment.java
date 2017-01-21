package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.highfive.highfive.R;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 08.12.16.
 */

public class AddOrderFragment extends DialogFragment {

    @InjectView(R.id.spinner)                   Spinner subjectSpinner;
    @InjectView(R.id.add_order_title)           EditText addOrderTitle;
    @InjectView(R.id.add_order_description)     EditText addOrderDescription;
    @InjectView(R.id.add_order_date)            EditText addOrderDate;
    @InjectView(R.id.add_order_button_id)       Button addOrderButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_order, container, false);
        ButterKnife.inject(this, v);

        addOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.add("title", addOrderTitle.getText().toString());
                params.add("description", addOrderDescription.getText().toString());
                params.add("subjects", Integer.toString(subjectSpinner.getSelectedItemPosition()));
                params.add("type", Integer.toString(0)); //TODO: job type selection
                params.add("deadline", new DateTime(Instant.now().plus(1_000_000L)).toString()); //TODO: proper reading of date
                boolean isStudent = false; //TODO: pick up the order type // where is shkololo?
                HighFiveHttpClient.post("orders" + (isStudent ? "student" : "teacher"), params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject contents = response.getJSONObject("response");
                            Toast.makeText(getContext(), "Заказ добавлен", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getContext(), "Ошибка добавления заказа", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        return v;
    }
}
