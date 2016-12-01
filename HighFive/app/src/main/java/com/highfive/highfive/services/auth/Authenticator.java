package com.highfive.highfive.services.auth;

import android.app.Activity;
import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by heat_wave on 11/25/16.
 */

public class Authenticator {

    private static final String TAG = "Landing";
    //private static Authenticator instance = null;
    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    private Authenticator() {
    }
//
//    public static Authenticator getInstance() {
//        if (instance == null) {
//            instance = new Authenticator();
//        }
//        return instance;
//    }

    public static boolean isLoginNeeded() {
        return auth.getCurrentUser() == null;
    }

    public void authenticate(Context context) {

    }

    public static void logout(Activity activity) {
        AuthUI.getInstance().signOut(activity);
//        Intent gatewayIntent = new Intent(activity, GatewayActivity.class);
//        activity.startActivity(gatewayIntent);
//        activity.finish();
        // will have to throw user back to login screen
    }
}
