package com.example.app1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Home on 19-07-2017.
 */
public class BookmarkFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bookmark, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(buildAdapter());


        //Initializing the tablayout
        tabLayout = (TabLayout) v.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }
    private PagerAdapter buildAdapter() {
        return null;
        //return(new SampleAdapter(getActivity(), getChildFragmentManager()));
    }
}
