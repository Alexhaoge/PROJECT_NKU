package com.kongx.nkuassistant;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class DeveloperFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ViewPager mImageViewPager = (ViewPager) view.findViewById(R.id.pager);
        mImageViewPager.setAdapter(new ScreenSlidePagerAdapter(((AppCompatActivity)getActivity()).getSupportFragmentManager()));

        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mImageViewPager, true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewGroup vg = ((ViewGroup) tabLayout.getChildAt(0));
                vg.setAlpha(0.8f);
                vg.animate()
                        .alpha(0.4f)
                        .setStartDelay(600)
                        .setDuration(400)
                        .setInterpolator(new LinearOutSlowInInterpolator())
                        .start();

                ViewGroup vgTab = (ViewGroup) vg.getChildAt(tab.getPosition());
                vgTab.setScaleX(0.8f);
                vgTab.setScaleY(0.8f);
                vgTab.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .setDuration(450)
                        .start();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            if(position==1) return new PageAboutFoxFragment();
            return new PageAboutKongxFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
