/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.fragment.subfragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dxc.mycollector.R;
import com.dxc.mycollector.fragment.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About Login
 */
public class HomeFragment extends Fragment {


    private View viewContent;

    private ViewPager viewPager;
    private Toolbar toolbar;
    private List<Fragment> list;
    private SlidingTabLayout tabLayout;

    public static HomeFragment newInstance(String s) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS, s);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewContent = inflater.inflate(R.layout.fragment_parent1, container, false);
//        Bundle bundle = getArguments();
//        String s = bundle.getString(Constants.ARGS);
//        TextView textView = (TextView) viewContent.findViewById(R.id.fragment_text_view);
//        textView.setText(s);

        initView();

        return viewContent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDate();
    }

    private void initView() {
        viewPager = (ViewPager) viewContent.findViewById(R.id.viewPager);
        toolbar = (Toolbar) viewContent.findViewById(R.id.toolbar);
        tabLayout = (SlidingTabLayout) viewContent.findViewById(R.id.tabLayout);
    }

    private void initDate() {
        list = new ArrayList<>();
        list.add(new FragmentChild1_1());
        list.add(new FragmentChild1_2());
        list.add(new FragmentChild1_3());
        list.add(new FragmentChild1_4());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager(), list, viewContent.getContext());
        viewPager.setAdapter(adapter);

        tabLayout.setViewPager(viewPager);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
    }
}
