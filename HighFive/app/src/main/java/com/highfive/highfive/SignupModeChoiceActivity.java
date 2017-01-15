package com.highfive.highfive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.highfive.highfive.fragments.ScreenSlidePageFragment;
import com.robohorse.pagerbullet.PagerBullet;

public class SignupModeChoiceActivity extends AppCompatActivity {
    private static final String TAG = "SignupModeChoiceActivity";
    private final int NUM_PAGES = 3;
    private static final int REQUEST_SIGNUP = 0;
    private int currentPage = 0;
    private PagerBullet pager;
    private PagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_choice);

        // Instantiate a ViewPager and a PagerAdapter.
        pager = (PagerBullet) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void startSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("mode", currentPage);
        startActivityForResult(intent, REQUEST_SIGNUP);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ScreenSlidePageFragment.newInstance(0, "Школьник");
                case 1:
                    return ScreenSlidePageFragment.newInstance(1, "Студент");
                case 2:
                    return ScreenSlidePageFragment.newInstance(2, "Преподаватель");
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}