package com.highfive.highfive;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.fragments.ChatFragment;
import com.highfive.highfive.fragments.HelpFragment;
import com.highfive.highfive.fragments.OrderDetailsFragment;
import com.highfive.highfive.fragments.OrderListFragment;
import com.highfive.highfive.fragments.OrderTeacherListFragment;
import com.highfive.highfive.fragments.ProfileStudentFragment;
import com.highfive.highfive.fragments.ProfileTeacherFragment;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class LandingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Navigator {
    private static final String TAG = "LandingActivity";
    public static final int FILE_CODE = 11;

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout drawer;
    @InjectView(R.id.nvView) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.inject(this);
        HighFiveHttpClient.initCookieStore(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = chooseOrdersFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
//        Fragment fragment = null;
        switch (id) {
            case R.id.nav_profile_fragment:
                navigateToProfile();
//                fragment = new ProfileTeacherFragment();
                break;
            case R.id.nav_order_list_fragment:
                navigateToChooseOrder();
//                fragment = chooseOrdersFragment();
                break;
            case R.id.nav_help_fragment:
                navigateToHelp();
//                fragment = new HelpFragment();
                break;
            case R.id.nav_chat:
                navigateToChat();
//                fragment = new ChatFragment();
                break;
            case R.id.nav_to_exit:
                HighFiveHttpClient.delete("auth/" +  HighFiveHttpClient.getUidCookie().getValue(), null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        HighFiveHttpClient.clearCookies();
                        Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(getBaseContext(), "Ошибка выхода", Toast.LENGTH_LONG).show();
                        //clearing credentials anyway since this token is invalid
                        HighFiveHttpClient.clearCookies();
                        Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                return true;
            default:
                return true;
        }

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @NonNull
    private Fragment chooseOrdersFragment() {
        Fragment fragment;
        Type profileType = new TypeToken<Profile>(){}.getType();
        Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        if (profile == null || profile.getType().equals("student") || profile.getType().equals("pupil")) {
            fragment = new OrderListFragment();
        } else {
            fragment = new OrderTeacherListFragment();
        }
        return fragment;
    }

    public void uploadAvatar(View view) {
        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        startActivityForResult(intent, FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            ClipData clip = data.getClipData();
            Uri uri = null;
            if (clip != null) {
                for (int i = 0; i < clip.getItemCount(); i++) {
                    uri = clip.getItemAt(i).getUri();
                }
            } else {
                uri = data.getData();
            }
            if (uri != null) {
                String mimeType = getMimeType(uri);
                if (mimeType != null && mimeType.startsWith("image") &&
                        (mimeType.endsWith("jpeg") || mimeType.endsWith("png") || mimeType.endsWith("gif"))) {
                    RequestParams params = new RequestParams();
                    try {
                        params.put("file", new File(uri.getPath()));
                        HighFiveHttpClient.post("users/" + HighFiveHttpClient.getUidCookie().getValue() + "/avatar",
                                params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        super.onFailure(statusCode, headers, responseString, throwable);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                    }
                                });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Файл не найден!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    @Override
    public void navigateToTeacherProfile() {
        ProfileTeacherFragment fragment = new ProfileTeacherFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void navigateToStudentProfile() {
        ProfileStudentFragment fragment= new ProfileStudentFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void navigateToProfile() {
        Fragment fragment;
        Type profileType = new TypeToken<Profile>(){}.getType();
        Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        if (profile == null || profile.getType().equals("student") || profile.getType().equals("pupil")) {
            fragment = new ProfileStudentFragment();
        } else {
            fragment = new ProfileTeacherFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void navigateToChat() {
        ChatFragment fragment = new ChatFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void navigateToHelp() {
        HelpFragment fragment = new HelpFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void navigateToChooseOrder() {
        Fragment fragment;
        Type profileType = new TypeToken<Profile>(){}.getType();
        Profile profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        if (profile == null || profile.getType().equals("student") || profile.getType().equals("pupil")) {
            fragment = new OrderListFragment();
        } else {
            fragment = new OrderTeacherListFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void navigateToOrderDetail(Order order) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("orderId", order.getOrderdId());
        bundle.putString("theme", order.getTheme());
        bundle.putString("description", order.getDescription());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }
}