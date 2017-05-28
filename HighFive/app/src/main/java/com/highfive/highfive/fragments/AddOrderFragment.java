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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.App;
import com.highfive.highfive.LandingActivity;
import com.highfive.highfive.Navigator;
import com.highfive.highfive.R;
import com.highfive.highfive.model.AddFileObj;
import com.highfive.highfive.model.AddFileParams;
import com.highfive.highfive.model.ChatFileObj;
import com.highfive.highfive.model.OrderType;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.Subject;
import com.highfive.highfive.model.SubjectList;
import com.highfive.highfive.responseModels.Response;
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

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

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
    @InjectView(R.id.add_order_attach_file)     TextView attachFile;

    private Profile profile;
    private SubjectList subList;
    private List<Subject> subjects;
    private OrderTypeList orderTypeList;
    private List<OrderType> orderTypes;
    private Navigator navigator;
    private static JSONArray array = new JSONArray();
    private static List<JSONObject> arr = new ArrayList<>();
    private static AddFileObj obj1;

    private static List<AddFileObj> fileList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigator = (Navigator) getActivity();

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

        attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LandingActivity.FILE_CODE = 3;
                navigator.pickDocs();
            }
        });

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


                Gson gson = new Gson();
//                try {
//                    JSONObject obj1 = new JSONObject(gson.toJson(arr));
                if (array.length() != 0){
                    try {
                        params.put("file", array.getJSONObject(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                HighFiveHttpClient.post("orders", params, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                            JSONObject contents = response.getJSONObject("response"); //TODO: do something with response
//                            Toast.makeText(getContext(), "Заказ добавлен", Toast.LENGTH_SHORT).show();
//                            navigator.navigateToChooseOrder();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                        Toast.makeText(getContext(), "Ошибка добавления заказа", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        super.onFailure(statusCode, headers, responseString, throwable);
//                        Toast.makeText(getContext(), "Ошибка добавления заказа", Toast.LENGTH_SHORT).show();
//                    }
//                });

                AddFileParams paramObj = new AddFileParams();
                paramObj.setDeadline(new DateTime(Instant.now().plus(1_000_000L)).toString());
                paramObj.setDescription(addOrderDescription.getText().toString());
                paramObj.setTitle(addOrderTitle.getText().toString());
                paramObj.setOffer(addOrderOffer.getText().toString());
                paramObj.setSubject(subjects.get(subjectSpinner.getSelectedItemPosition()).getId());
                paramObj.setType(orderTypes.get(orderTypeSpinner.getSelectedItemPosition()).getId());
                paramObj.setFile(fileList);


                Call<Response> call = App.getApi().addOrder(HighFiveHttpClient.getTokenCookie().getValue(),
                        paramObj);
                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        int x = 1;
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        int x = 1;
                    }
                });

            }
        });
        return v;
    }

    private List<String> getSubjectNames() {
        if (LandingActivity.userType.equals("teacher") && subList != null) {
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

    public static void uploadFile(String path) {

        File file = new File(path);
        if (file.exists()) {

            RequestBody reqFile = RequestBody.create(MediaType.parse("file/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

            Call<Response> call = App.getApi().uploadFile(HighFiveHttpClient.getTokenCookie().getValue()
                    , body);
            call.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                    if (response.code() == 200) {
                        LinkedTreeMap<String, String> mp = new LinkedTreeMap<String, String>();
                        LinkedTreeMap<String, String> send = new LinkedTreeMap<String, String>();
                        mp = (LinkedTreeMap<String, String>) response.body().getResponse();
                        send.put("_id", mp.get("id"));
                        send.put("name", mp.get("name"));
                        send.put("path", mp.get("path"));
                        AddFileObj obj = new AddFileObj(mp.get("name"), mp.get("path"));
                        fileList.add(obj);

                        obj1 = obj;
                        Gson gson = new Gson();
                        try {
                            JSONObject obj1 = new JSONObject(gson.toJson(obj));
                            array = new JSONArray();
                            array.put(obj1);
                            arr.add(obj1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {

                }
            });
        }

    }
}
