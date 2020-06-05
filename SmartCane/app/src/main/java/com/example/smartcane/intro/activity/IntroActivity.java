package com.example.smartcane.intro.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.smartcane.R;
import com.example.smartcane.intro.intro_adapter.SliderAdapter;
import com.example.smartcane.state.StateActivity;

import static com.example.smartcane.state.StateConstants.*;

public class IntroActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private SharedPreferences mSharedPreferences;

    private Context mContext;
    private ViewPager vp_intro;
    private LinearLayout ll_dots;
    private Button btn_skip_intro;
    private Button btn_next_intro;

    private int mCurrentPage;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSharedPreferences = getApplicationContext().getSharedPreferences(STATE_CONSTANTS_SP_NAME, MODE_PRIVATE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_intro);
        setupUi();
        addDotsIndicator(0);
        setupButtonClick();
    }

    private void setupUi() {
        mContext = this;

        vp_intro = findViewById(R.id.vp_intro);
        ll_dots = findViewById(R.id.ll_dots);
        btn_next_intro = findViewById(R.id.btn_next_intro);
        btn_skip_intro = findViewById(R.id.btn_skip_intro);

        btn_next_intro.setText(R.string.intro_next);
        btn_next_intro.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        btn_skip_intro.setText(R.string.intro_skip);
        btn_skip_intro.setBackgroundColor(getResources().getColor(R.color.colorTransparent));

        String[] description = {
                mContext.getResources().getString(R.string.intro_descritption1),
                mContext.getResources().getString(R.string.intro_descritption2),
                mContext.getResources().getString(R.string.intro_descritption3)
        };
        String[] heading = {
                mContext.getResources().getString(R.string.intro_header1),
                mContext.getResources().getString(R.string.intro_header2),
                mContext.getResources().getString(R.string.intro_header3)
        };

        SliderAdapter sliderAdapter = new SliderAdapter(mContext, description, heading);
        vp_intro.setAdapter(sliderAdapter);
        vp_intro.addOnPageChangeListener(this);
    }

    private void setupButtonClick() {
        btn_next_intro.setOnClickListener(v -> vp_intro.setCurrentItem(mCurrentPage + 1));

        btn_skip_intro.setOnClickListener(v -> {
            mSharedPreferences.edit().putBoolean(STATE_CONSTANTS_SP_SHOW_INTRO, false).apply();
            Intent intent = new Intent(mContext, StateActivity.class);
            startActivity(intent);
        });
    }

    private void addDotsIndicator(int position) {
        TextView[] tv_dots = new TextView[SliderAdapter.mNumberOfPages];
        ll_dots.removeAllViews();
        for (int i = 0; i < tv_dots.length; i++) {
            tv_dots[i] = new TextView(mContext);
            tv_dots[i].setText(Html.fromHtml("&#8226"));
            tv_dots[i].setTextSize(35);
            tv_dots[i].setTextColor(getResources().getColor(R.color.colorWhite,null));
            tv_dots[i].setFocusable(false);

            ll_dots.addView(tv_dots[i]);
        }
        if (tv_dots.length > 0) {
            tv_dots[position].setTextColor(getResources().getColor(R.color.colorBlue));
        }
        ll_dots.setFocusable(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        addDotsIndicator(position);
        mCurrentPage = position;
        if (position == 0 || position == 1) {
            btn_skip_intro.setVisibility(View.VISIBLE);
            btn_next_intro.setText(R.string.intro_next);
            btn_skip_intro.setText(R.string.intro_skip);
            setupButtonClick();
        }
        if (position == 2) {
            btn_skip_intro.setVisibility(View.INVISIBLE);
            btn_next_intro.setText(R.string.intro_finish);
            btn_next_intro.setOnClickListener(v -> {
                mSharedPreferences.edit().putBoolean(STATE_CONSTANTS_SP_SHOW_INTRO, false).apply();
                startActivity(new Intent(mContext, StateActivity.class));
            });
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
