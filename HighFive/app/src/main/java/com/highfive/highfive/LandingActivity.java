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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.fragments.AddOrderFragment;
import com.highfive.highfive.fragments.BidListCommentFragment;
import com.highfive.highfive.fragments.BidListFragment;
import com.highfive.highfive.fragments.ChatFragment;
import com.highfive.highfive.fragments.HelpFragment;
import com.highfive.highfive.fragments.OrderDetailsFragment;
import com.highfive.highfive.fragments.OrderListFragment;
import com.highfive.highfive.fragments.OrderListRootFragment;
import com.highfive.highfive.fragments.ProfileFragment;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.BidComment;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class LandingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Navigator {
    private static final String TAG = "LandingActivity";
    public static final int FILE_CODE = 11;

    private StudentOrderLoaderTask task;
    private Profile profile;

    @InjectView(R.id.toolbar)           Toolbar toolbar;
    @InjectView(R.id.drawer_layout)     DrawerLayout drawer;
    @InjectView(R.id.nvView)            NavigationView navigationView;

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

        Type profileType = new TypeToken<Profile>(){}.getType();
        profile = (Profile) Cache.getCacheManager().get("profile", Profile.class, profileType);
        if (profile.getType().equals("student")) {
            task = new StudentOrderLoaderTask(this);
            task.execute(profile.getStudentOrderIdList());
        }


        /*View headerView = navigationView.getHeaderView(0);
        if (!StringUtil.isBlank(profile.getAvatar())) {
            Picasso.with(getApplicationContext()).load("https://yareshu.ru/" + profile.getAvatar()).
                    into((ImageView) headerView.findViewById(R.id.nav_header_avatar));
        }*/

        navigateToChooseOrder();

    }

    public void updateChooseOrder(List<Order> list) {

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_profile_fragment:
                navigateToProfile();
                break;
            case R.id.nav_order_list_fragment:
                navigateToChooseOrder();
                break;
            case R.id.nav_help_fragment:
                navigateToHelp();
                break;
            case R.id.nav_chat:
                navigateToChat();
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

        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
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
    public void navigateToProfile() {
        Fragment fragment = new ProfileFragment();
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
        OrderListRootFragment chooseOrderFragment = new OrderListRootFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, chooseOrderFragment).commit();
    }

    @Override
    public void navigateToOrderDetail(Order order) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("orderId", order.getOrderdId());
        bundle.putString("theme", order.getTheme());
        bundle.putString("description", order.getDescription());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void navigateToBidsList(ArrayList<Bid> bids) {
        BidListFragment fragment = new BidListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("bidList", bids);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();

    }

    @Override
    public void navigateToBidListComments(ArrayList<BidComment> bidComments, String creatorId) {
        BidListCommentFragment fragment = new BidListCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("bidCommentsList", bidComments);
        bundle.putString("creatorId", creatorId);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void navigateToAddOrder() {
        AddOrderFragment fragment = new AddOrderFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();
    }
}