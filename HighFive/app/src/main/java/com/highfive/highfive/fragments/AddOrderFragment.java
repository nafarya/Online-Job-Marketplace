package com.highfive.highfive.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.R;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 08.12.16.
 */

public class AddOrderFragment extends DialogFragment {

    @InjectView(R.id.subject_spinner)           Spinner subjectSpinner;
    @InjectView(R.id.job_type_spinner)          Spinner jobTypeSpinner;
    @InjectView(R.id.add_order_title)           EditText addOrderTitle;
    @InjectView(R.id.add_order_description)     EditText addOrderDescription;
    @InjectView(R.id.add_order_date)            EditText addOrderDate;
    @InjectView(R.id.add_order_offer)           EditText addOrderOffer;
    @InjectView(R.id.add_order_button_id)       Button addOrderButton;

    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> subjectIds = new ArrayList<>();
    private ArrayList<String> jobTypes = new ArrayList<>();
    private ArrayList<String> jobTypeIds = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Type profileType = new TypeToken<Profile>(){}.getType();
        Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        String userType = profile != null ? profile.getType().toLowerCase() : "student";
        RequestParams params = new RequestParams();
        params.add("type", userType);

        HighFiveHttpClient.get("subjects", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    JSONArray subjArray = contents.getJSONArray("items");
                    for (int i = 0; i < contents.getInt("count"); i++) {
                        JSONObject current = (JSONObject) subjArray.get(i);
                        subjects.add(current.getString("name"));
                        subjectIds.add(current.getString("id"));
                    }
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>
                            (getActivity(), android.R.layout.simple_spinner_item, subjects);
                    if (subjectSpinner != null) {
                        subjectSpinner.setAdapter(subjectAdapter);
                    }
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
        HighFiveHttpClient.get("ordertypes", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    JSONArray subjArray = contents.getJSONArray("items");
                    for (int i = 0; i < contents.getInt("count"); i++) {
                        JSONObject current = (JSONObject) subjArray.get(i);
                        jobTypes.add(current.getString("name"));
                        jobTypeIds.add(current.getString("id"));
                    }
                    ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter<String>
                            (getActivity(), android.R.layout.simple_spinner_item, jobTypes);
                    if (jobTypeSpinner != null) {
                        jobTypeSpinner.setAdapter(jobTypeAdapter);
                    }
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
                params.add("subject", subjectIds.get((subjectSpinner.getSelectedItemPosition())));
                params.add("type", jobTypeIds.get(jobTypeSpinner.getSelectedItemPosition()));
                params.add("offer", addOrderOffer.getText().toString());
                params.add("deadline", new DateTime(Instant.now().plus(1_000_000L)).toString()); //TODO: proper reading of date
                HighFiveHttpClient.post("orders", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject contents = response.getJSONObject("response"); //TODO: do something with response
                            Toast.makeText(getContext(), "Заказ добавлен", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getContext(), "Ошибка добавления заказа", Toast.LENGTH_SHORT).show();
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
