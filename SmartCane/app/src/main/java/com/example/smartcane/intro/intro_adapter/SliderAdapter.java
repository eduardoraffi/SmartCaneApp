package com.example.smartcane.intro.intro_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.smartcane.R;

public class SliderAdapter extends PagerAdapter {

    private Context mContext;
    public static int mNumberOfPages = 3;

    private String[] slide_descriptions;
    private String[] slide_headings;
    private int[] slide_images = {
            R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3
    };

    public SliderAdapter(Context context, String[] descrption, String[] headings) {
        mContext = context;
        slide_descriptions = descrption;
        slide_headings = headings;
    }

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.slide_intro, container, false);

        ImageView iv_intro = view.findViewById(R.id.iv_intro);
        TextView tv_label = view.findViewById(R.id.tv_label);
        TextView tv_description = view.findViewById(R.id.tv_intro_description);

        iv_intro.setImageResource(slide_images[position]);
        tv_label.setText(slide_headings[position]);
        tv_label.setTextColor(mContext.getResources().getColor(R.color.colorWhite, null));
        tv_description.setText(slide_descriptions[position]);
        tv_label.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
