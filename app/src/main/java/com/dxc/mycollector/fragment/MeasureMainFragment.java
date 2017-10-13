/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.fragment;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dxc.mycollector.R;
import com.dxc.mycollector.fragment.subfragment.FinishedFragment;
import com.dxc.mycollector.fragment.subfragment.NoFinishFragment;
import com.dxc.mycollector.fragment.subfragment.NoUploadFragment;
import com.dxc.mycollector.fragment.subfragment.SlidingTabLayout;
import com.dxc.mycollector.fragment.subfragment.UploadedFragment;
import com.dxc.mycollector.fragment.subfragment.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About Login
 */
public class MeasureMainFragment extends Fragment {


    private View viewContent;

    private ViewPager viewPager;
    private Toolbar toolbar;
    private List<Fragment> list;
    private SlidingTabLayout tabLayout;

    public static MeasureMainFragment newInstance(String s) {
        MeasureMainFragment homeFragment = new MeasureMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS, s);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置ActionBar名称
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_plus);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) getActivity().findViewById(R.id.title_name)).setText(getString(R.string.item_measure));
        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            actionBar.setElevation(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //解决ViewPager里的Fragment之间切换的时候，Fragment空白的问题
        if (viewContent != null) {
            ViewGroup parent = (ViewGroup) viewContent.getParent();
            if (parent != null) {
                parent.removeView(viewContent);
            }
            return viewContent;
        }
        viewContent = inflater.inflate(R.layout.measure_main_fragment, container, false);
//        Bundle bundle = getArguments();
//        String s = bundle.getString(Constants.ARGS);
//        TextView textView = (TextView) viewContent.findViewById(R.id.fragment_text_view);
//        textView.setText(s);

        initView();
        initDate();
        return viewContent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        viewPager = (ViewPager) viewContent.findViewById(R.id.viewPager);
        toolbar = (Toolbar) viewContent.findViewById(R.id.toolbar);
        tabLayout = (SlidingTabLayout) viewContent.findViewById(R.id.tabLayout);
    }

    private void initDate() {
        list = new ArrayList<>();
        list.add(new NoFinishFragment());
        list.add(new FinishedFragment());
        list.add(new NoUploadFragment());
        list.add(new UploadedFragment());
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
