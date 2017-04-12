package com.highfive.highfive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.highfive.highfive.model.OrderType;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.Subject;
import com.highfive.highfive.model.SubjectList;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by heat_wave on 25.12.16.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private Profile profile;
    private ArrayList<Subject> subjectList;
    private ArrayList<OrderType> typeList;

    @InjectView(R.id.input_email)       EditText emailText;
    @InjectView(R.id.input_password)    EditText passwordText;
    @InjectView(R.id.btn_login)         Button loginButton;
    @InjectView(R.id.link_signup)       TextView signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        HighFiveHttpClient.initCookieStore(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupModeChoiceActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        if (HighFiveHttpClient.getTokenCookie() != null) {
            onLoginSuccess();
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Проверяем данные...");
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);

        HighFiveHttpClient.post("auth", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    String id = contents.getString("id");
                    String token = contents.getString("token");
                    HighFiveHttpClient.addUidCookie(id);
                    HighFiveHttpClient.addTokenCookie(token);

                    progressDialog.dismiss();
                    LoginActivity.this.onLoginSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                progressDialog.dismiss();
                LoginActivity.this.onLoginFailed();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        final Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
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
                                    contents.getJSONObject("rating").getDouble("negative"),
                                    contents.getJSONObject("rating").getDouble("positive"),
                                    contents.getString("firstName"),
                                    contents.getString("secondName"),
                                    contents.getString("type").toLowerCase());
                            profile.setNeutralRating(contents.getJSONObject("rating").getDouble("neutral"));
                            profile.setAvatar(contents.getString("avatar"));
                            if (profile.getType().equals("student")) {
                                JSONArray array = contents.getJSONArray("orders");
                                ArrayList<String> tmp = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    tmp.add(array.getString(i));
                                }
                                profile.setStudentOrderIdList(tmp);
                            }

                            Cache.getCacheManager().put("profile", profile);

                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        startActivity(intent);
                        finish();
                    }
                });

        getSubjects();
        getOrderTypes();

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Ошибка входа", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        emailText.setText(emailText.getText().toString().replace(" ", ""));

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("введите корректный email");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("от 4 до 10 букв и/или цифр");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    private void getSubjects() {
        String userType = "all";
        RequestParams params = new RequestParams();
        subjectList = new ArrayList<>();
        params.add("type", userType);


        HighFiveHttpClient.get("subjects", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    JSONArray subjArray = contents.getJSONArray("items");
                    Subject tmp = new Subject("Все предметы", "all", "all", "allSubjects");
                    subjectList.add(tmp);
                    for (int i = 0; i < contents.getInt("count"); i++) {
                        JSONObject current = (JSONObject) subjArray.get(i);
                        tmp = new Subject(
                                current.getString("name"),
                                current.getString("science"),
                                current.getString("difficultyLevel"),
                                current.getString("id")
                        );
                        subjectList.add(tmp);
                    }
                    SubjectList list = new SubjectList(subjectList);
                    Cache.getCacheManager().put("subjectList", list);

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

    private void getOrderTypes() {
        RequestParams params = new RequestParams();
        typeList = new ArrayList<>();
        params.add("type", "all");
        HighFiveHttpClient.get("ordertypes", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    OrderType tmp = new OrderType("Любой тип", "all", "all");
                    typeList.add(tmp);
                    JSONObject contents = response.getJSONObject("response");
                    JSONArray subjArray = contents.getJSONArray("items");
                    for (int i = 0; i < contents.getInt("count"); i++) {
                        JSONObject current = (JSONObject) subjArray.get(i);
                        typeList.add(new OrderType(current.getString("name"),
                                current.getString("difficultyLevel"),
                                current.getString("id")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OrderTypeList orderTypeList = new OrderTypeList(typeList);
                Cache.getCacheManager().put("orderTypeList", orderTypeList);

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