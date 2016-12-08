package com.highfive.highfive.services.auth;

import android.app.Activity;
import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

/**
 * Created by heat_wave on 11/25/16.
 */

public class Authenticator {

    private static final String TAG = "Auth";
    public static final int LOGIN_SCREEN_REQUEST_CODE = 11;

    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    private Authenticator() {
    }

    public static boolean isLoginNeeded() {
        return auth.getCurrentUser() == null;
    }

    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static void auth(Context context) {
        if (isLoginNeeded()) {
            ((Activity)context).startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            //.setTosUrl("https://superapp.example.com/terms-of-service.html")
                            //.setTheme(R.style.?)
                            .build(), LOGIN_SCREEN_REQUEST_CODE);
        }
    }

    public static void logout(Activity activity) {
        AuthUI.getInstance().signOut(activity);
        // will have to throw user back to login screen
    }
}
