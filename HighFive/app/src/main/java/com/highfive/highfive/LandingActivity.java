package com.highfive.highfive;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.fragments.AddOrderFragment;
import com.highfive.highfive.fragments.BidListCommentFragment;
import com.highfive.highfive.fragments.BidListFragment;
import com.highfive.highfive.fragments.ChatFragment;
import com.highfive.highfive.fragments.ChatListFragment;
import com.highfive.highfive.fragments.HelpFragment;
import com.highfive.highfive.fragments.OrderDetailsFragment;
import com.highfive.highfive.fragments.OrderLentaFragment;
import com.highfive.highfive.fragments.OrderListRootFragment;
import com.highfive.highfive.fragments.PaymentFragment;
import com.highfive.highfive.fragments.ProfileFragment;
import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.BidComment;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Part;

public class LandingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Navigator {
    /*
    * avatar                        = 1
    * chatFile                      = 2
    * add file to order when create = 3
    */
    public static int FILE_CODE = 0;
    public static String userType = null;

    private Profile profile;
    private ArrayList<String> filePaths;
    private List<String> photoPaths;
    private List<String> docPaths;
    private Socket socket;


    @InjectView(R.id.toolbar)           Toolbar toolbar;
    @InjectView(R.id.drawer_layout)     DrawerLayout drawer;
    @InjectView(R.id.nvView)            NavigationView navigationView;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        userType = myPrefs.getString("userType", "teacher"); // return 0 if someValue doesn't exist
    }

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

        if (profile == null && userType == null) {
            SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
            userType = myPrefs.getString("userType", "teacher"); // return 0 if someValue doesn't exist
        }

        View headerView = navigationView.getHeaderView(0);
        if (profile != null) {
            Picasso.with(getApplicationContext()).load("https://yareshu.ru/" + profile.getAvatar()).
                    into((ImageView) headerView.findViewById(R.id.nav_header_avatar));
            TextView balance = (TextView) headerView.findViewById(R.id.nav_header_balance);
            balance.setText("Баланс: " + profile.getBalance() + " Руб");
            userType = profile.getType();

        }

        Menu menu = navigationView.getMenu();
        if (!LandingActivity.userType.equals("teacher")) {
            menu.findItem(R.id.nav_teacher_lenta).setVisible(false);
        } else {
            menu.findItem(R.id.nav_add_order).setVisible(false);
        }

        ImageButton addBalanceBtn = (ImageButton) headerView.findViewById(R.id.add_balance_button);
        addBalanceBtn.setOnClickListener(view -> {
            navigateToPayment();
            toolbar.setTitle("Пополнение баланса");
            drawer.closeDrawer(Gravity.LEFT);
        });
        
        navigateToChooseOrder();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_profile_fragment:
                navigateToProfile();
                break;
            case R.id.nav_teacher_lenta:
                navigateToLenta();
                break;
            case R.id.nav_add_order:
                navigateToAddOrder();
                break;
            case R.id.nav_order_list_fragment:
                navigateToChooseOrder();
                break;
            case R.id.nav_help_fragment:
                navigateToHelp();
                break;
            case R.id.nav_chat:
                navigateToChatList();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
//            ClipData clip = data.getClipData();
//            Uri uri = null;
//            if (clip != null) {
//                for (int i = 0; i < clip.getItemCount(); i++) {
//                    uri = clip.getItemAt(i).getUri();
//                }
//            } else {
//                uri = data.getData();
//            }
//            if (uri != null) {
//                String mimeType = getMimeType(uri);
//                if (mimeType != null && mimeType.startsWith("image") &&
//                        (mimeType.endsWith("jpeg") || mimeType.endsWith("png") || mimeType.endsWith("gif"))) {
//                    RequestParams params = new RequestParams();
//                    try {
//                        params.put("file", new MyFile(uri.getPath()));
//                        HighFiveHttpClient.post("users/" + HighFiveHttpClient.getUidCookie().getValue() + "/avatar",
//                                params, new JsonHttpResponseHandler() {
//                                    @Override
//                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                        super.onSuccess(statusCode, headers, response);
//                                    }
//
//                                    @Override
//                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                        super.onFailure(statusCode, headers, responseString, throwable);
//                                    }
//
//                                    @Override
//                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                                    }
//                                });
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                        Toast.makeText(this, "Файл не найден!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }
//    }

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
        while(getSupportFragmentManager().popBackStackImmediate());
        ProfileFragment fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void navigateToChat(Order order) {
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void navigateToHelp() {
        while(getSupportFragmentManager().popBackStackImmediate());
        HelpFragment fragment = new HelpFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void navigateToChooseOrder() {
        while(getSupportFragmentManager().popBackStackImmediate());
        OrderListRootFragment chooseOrderFragment = new OrderListRootFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, chooseOrderFragment).commit();
    }

    @Override
    public void navigateToOrderDetail(Order order) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("orderId", order.getId());
        bundle.putString("theme", order.getTheme());
        bundle.putString("description", order.getDescription());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void navigateToBidsList(ArrayList<Bid> bids, String orderId, String bidStatus) {
        BidListFragment fragment = new BidListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("bidList", bids);
        bundle.putString("orderId", orderId);
        bundle.putString("bidStatus", bidStatus);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();

    }

    @Override
    public void navigateToBidListComments(ArrayList<BidComment> bidComments, String creatorId, String bidId) {
        BidListCommentFragment fragment = new BidListCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("bidCommentsList", bidComments);
        bundle.putString("creatorId", creatorId);
        bundle.putString("bidId", bidId);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void navigateToAddOrder() {
        AddOrderFragment fragment = new AddOrderFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void navigateToPayment() {
        while(getSupportFragmentManager().popBackStackImmediate());
        PaymentFragment fragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", profile.getUid());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

    }

    @Override
    public void navigateToLenta() {
        while(getSupportFragmentManager().popBackStackImmediate());
        OrderLentaFragment fragment = new OrderLentaFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void navigateToChatList() {
        while(getSupportFragmentManager().popBackStackImmediate());
        ChatListFragment fragment = new ChatListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void pickPhoto() {
        filePaths = new ArrayList<>();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.FilePicker)
                .pickPhoto(this);
    }

    @Override
    public void pickDocsForChat(Socket socket) {
        filePaths = new ArrayList<>();
        this.socket = socket;
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.FilePicker)
                .pickFile(this);

    }

    @Override
    public void pickDocs() {
        filePaths = new ArrayList<>();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.FilePicker)
                .pickFile(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK && data!=null) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && FILE_CODE == 1) {
                uploadAvatar(data);
            }
            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {

                docPaths = new ArrayList<>();
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                if (FILE_CODE == 2) {
                    ChatFragment.uploadFile(docPaths.get(0));
                }
                if (FILE_CODE == 3) {
                    AddOrderFragment.uploadFile(docPaths.get(0));
                }

//                MyFile file = new MyFile(docPaths.get(0));
//                if (file.exists()) {
//                    Gson gson = new Gson();
//
//// 1. Java object to JSON, and save into a file
//                    try {
//                        gson.toJson(file, new FileWriter(file.getPath()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//// 2. Java object to JSON, and assign to a String
//                    String jsonInString = gson.toJson(file);
//                    socket.emit("file", file);
//                }
//                    try {
//                        FileInputStream fis = new FileInputStream(file);
//                        byte imgByte[] = new byte[(int) file.length()];
//                        fis.read(imgByte);
//
//                        //convert byte array to base64 string
////                        String img64 = Base64.encodeBase64URLSafeString(imgByte);
//                        //send img64 to socket.io servr
//                    } catch (Exception e) {
//                        //
//                    }
//

//                RequestBody reqFile = RequestBody.create(MediaType.parse("file/*"), file);
//                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
//
//                Call<Response> call = App.getApi().uploadAvatar(HighFiveHttpClient.getTokenCookie().getValue(),
//                        profile.getUid(), body);
//                call.enqueue(new Callback<Response>() {
//                    @Override
//                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//                        if (response.code() == 200) {
//                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT);
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Response> call, Throwable t) {
//                    }
//                });
            }
        }

        FILE_CODE = 0;

        switch (requestCode)
        {
            case FilePickerConst.REQUEST_CODE_DOC:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
        }
//        addThemToView(photoPaths,docPaths);
    }


    private void uploadAvatar(Intent data) {
        photoPaths = new ArrayList<>();
        photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
        if (photoPaths.size() != 0) {
            File imgFile = new File(photoPaths.get(0));
            if (imgFile.exists()) {
                ImageView myImage = (ImageView) findViewById(R.id.avatar);
                myImage.setImageURI(Uri.fromFile(imgFile));
                View headerView = navigationView.getHeaderView(0);
                profile.setAvatar(photoPaths.get(0));
                Picasso.with(getApplicationContext()).load("https://yareshu.ru/" + profile.getAvatar()).
                        into((ImageView) headerView.findViewById(R.id.nav_header_avatar));

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", imgFile.getName(), reqFile);

                Call<Response> call = App.getApi().uploadAvatar(HighFiveHttpClient.getTokenCookie().getValue(),
                        profile.getUid(), body);
                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT);

                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                    }
                });
            }
        }
    }

    private void uploadChatFile() {

    }


}