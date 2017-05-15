package com.highfive.highfive.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.highfive.highfive.App;
import com.highfive.highfive.R;
import com.highfive.highfive.adapters.BidCommentsAdapter;
import com.highfive.highfive.adapters.BidListAdapter;
import com.highfive.highfive.model.BidComment;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.responseModels.Items;
import com.highfive.highfive.responseModels.Response;
import com.highfive.highfive.util.Cache;
import com.highfive.highfive.util.HighFiveHttpClient;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dan on 02.04.17.
 */

public class BidListCommentFragment extends Fragment {

    private ArrayList<BidComment> comments;
    private String creatorId;
    private String bidId;
    private List<Profile> profileList;
    private BidCommentsAdapter adapter;

    @InjectView(R.id.bid_comment_list_rv_id)                RecyclerView commentRv;
    @InjectView(R.id.bid_list_comments_nocomments_text)     TextView nocomments;
    @InjectView(R.id.comment_text)                          EditText commentText;
    @InjectView(R.id.send_comment_button)                   ImageButton sendCommentButton;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bid_list_comments, container, false);
        ButterKnife.inject(this, v);

        Bundle bundle = getArguments();
        comments = bundle.getParcelableArrayList("bidCommentsList");
        creatorId = bundle.getString("creatorId");
        bidId = bundle.getString("bidId");

        adapter =  new BidCommentsAdapter(comments, creatorId);
        commentRv.setAdapter(adapter);

        if (comments.size() > 0) {
            nocomments.setVisibility(View.GONE);
            //getCommentProfiles();
        } else {
            commentRv.setVisibility(View.GONE);
        }

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comments.size() == 0) {
                    nocomments.setVisibility(View.GONE);
                    commentRv.setVisibility(View.VISIBLE);
                }

                BidComment bidComment = new BidComment();
                bidComment.setText(commentText.getText().toString());
                bidComment.setOwner(creatorId);
                bidComment.set_id("");
                bidComment.setCreatedAt(new DateTime(Instant.now().plus(1_000_000L)).toString());
                comments.add(bidComment);
                adapter.setComments(comments);
                adapter.notifyDataSetChanged();
                sendComment(commentText.getText().toString());

                commentText.setText("");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                commentRv.scrollToPosition(comments.size()-1);

            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comments = new ArrayList<>();
    }

    private void sendComment(String comment) {
        Call<Response> call = App.getApi().addComment(HighFiveHttpClient.getTokenCookie().getValue(),
                bidId, comment);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                int x = 0;
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                int x = 0;
            }
        });
    }

    private void getCommentProfiles() {
        List<Observable<Response<Profile>>> observables = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            String profileId = comments.get(i).getOwner();
            observables.add(App.getApi().getUserById(profileId).subscribeOn(Schedulers.io()).retry(5).onErrorReturn(t -> null));
        }
        Observable.zip(observables, profileResponseObjects -> {
            List<Profile> profiles = new ArrayList<>();
            for (Object obj : profileResponseObjects) {
                if (obj != null) {
                    profiles.add(((Response<Profile>) obj).getResponse());
                }
            }
            return profiles;
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Profile>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Profile> profiles) {
                profileList = profiles;
                adapter.setCommentProfiles(profileList);
                adapter.notifyDataSetChanged();
            }
        });

    }

}
