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

import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private int mode;

    @InjectView(R.id.input_name)        EditText nameText;
    @InjectView(R.id.input_email)       EditText emailText;
    @InjectView(R.id.input_password)    EditText passwordText;
    @InjectView(R.id.btn_signup)        Button signupButton;
    @InjectView(R.id.link_login)        TextView loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

        ButterKnife.inject(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Создаем учетную запись...");
        progressDialog.show();

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        HighFiveHttpClient.initCookieStore(this);

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("username", name);
        HighFiveHttpClient.post("users?type=" + (mode == 2 ? "teacher" : "student"), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject contents = response.getJSONObject("response");
                    String id = contents.getString("id");
                    String token = contents.getString("token");
                    HighFiveHttpClient.addUidCookie(id);
                    HighFiveHttpClient.addTokenCookie(token);

                    progressDialog.dismiss();
                    SignupActivity.this.onSignupSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                progressDialog.dismiss();
                SignupActivity.this.onSignupFailed();
            }
        });
    }

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Ошибка регистрации", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("не менее 3 символов");
            valid = false;
        } else {
            nameText.setError(null);
        }

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
}