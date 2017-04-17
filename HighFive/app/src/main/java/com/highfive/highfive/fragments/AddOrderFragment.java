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
import com.highfive.highfive.model.OrderType;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.Subject;
import com.highfive.highfive.model.SubjectList;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by dan on 08.12.16.
 */

public class AddOrderFragment extends DialogFragment {

    @InjectView(R.id.subject_spinner)           SearchableSpinner subjectSpinner;
    @InjectView(R.id.job_type_spinner)          SearchableSpinner orderTypeSpinner;
    @InjectView(R.id.add_order_title)           EditText addOrderTitle;
    @InjectView(R.id.add_order_description)     EditText addOrderDescription;
    @InjectView(R.id.add_order_date)            EditText addOrderDate;
    @InjectView(R.id.add_order_offer)           EditText addOrderOffer;
    @InjectView(R.id.add_order_button_id)       Button addOrderButton;

    private Profile profile;
    private SubjectList subList;
    private List<Subject> subjects;
    private OrderTypeList orderTypeList;
    private List<OrderType> orderTypes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        Type subListType = new TypeToken<SubjectList>(){}.getType();
        subList = (SubjectList) Cache.getCacheManager().get("subjectList", SubjectList.class, subListType);
        Type orderTypeListType = new TypeToken<OrderTypeList>(){}.getType();
        orderTypeList = (OrderTypeList) Cache.getCacheManager().get("orderTypeList", OrderTypeList.class, orderTypeListType);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_order, container, false);
        ButterKnife.inject(this, v);

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, getSubjectNames());


        subjectSpinner.setAdapter(subjectAdapter);
        subjectSpinner.setTitle("Выберите предмет");
        subjectSpinner.setPositiveButton("OK");

        ArrayAdapter<String> ordeTypeAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, getOrderTypeNames());


        orderTypeSpinner.setAdapter(ordeTypeAdapter);
        orderTypeSpinner.setTitle("Выберите тип работы");
        orderTypeSpinner.setPositiveButton("OK");


        addOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.add("title", addOrderTitle.getText().toString());
                params.add("description", addOrderDescription.getText().toString());
                params.add("subject", subjects.get(subjectSpinner.getSelectedItemPosition()).getId());
                params.add("type", orderTypes.get(orderTypeSpinner.getSelectedItemPosition()).getId());
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

    private List<String> getSubjectNames() {
        if (profile.getType().equals("teacher") && subList != null) {
            subjects = subList.getSubjectList();
        } else {
            if (subList != null) {
                subjects = subList.getStudentSubjectList();
            }
        }

        List<String> list = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            list.add(subjects.get(i).getName());
        }
        return list;
    }

    private List<String> getOrderTypeNames() {
        orderTypes = orderTypeList.getorderTypelist();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < orderTypes.size(); i++) {
            names.add(orderTypes.get(i).getName());
        }
        return names;
    }
}
