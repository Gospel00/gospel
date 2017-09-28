/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.dxc.mycollector.R;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by gospel on 2017/8/18.
 * About Login
 */
public class NavigationFragment extends Fragment implements BottomNavigationBar.OnTabSelectedListener {


    private BottomNavigationBar mBottomNavigationBar;
    private MeasureMainFragment measureMainFragment;
    private SecurityFragment securityFragment;
    private GasFragment gasFragment;
    private CADFragment cadFragment;
    private TextView mTextView;
    Context context;

    public static NavigationFragment newInstance(String s) {
        NavigationFragment navigationFragment = new NavigationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS, s);
        navigationFragment.setArguments(bundle);
        return navigationFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_bottom_navigation_bar, container, false);
        mBottomNavigationBar = (BottomNavigationBar) view.findViewById(R.id.bottom_navigation_bar);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        BadgeItem numberBadgeItem = new BadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.actionbar_bg_color)
                .setText("5");
//                .setHideOnSelect(autoHide.isChecked());

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.total, getString(R.string.item_measure)).setInactiveIconResource(R.mipmap.total1).setActiveColorResource(R.color.actionbar_bg_color).setInActiveColorResource(R.color.black_1).setBadgeItem(numberBadgeItem))
                .addItem(new BottomNavigationItem(R.mipmap.safe2, getString(R.string.item_security)).setInactiveIconResource(R.mipmap.safe1).setActiveColorResource(R.color.actionbar_bg_color).setInActiveColorResource(R.color.black_1))
                .addItem(new BottomNavigationItem(R.mipmap.gas1, getString(R.string.item_gas)).setInactiveIconResource(R.mipmap.gas).setActiveColorResource(R.color.actionbar_bg_color).setInActiveColorResource(R.color.black_1))
                .addItem(new BottomNavigationItem(R.mipmap.cad, getString(R.string.item_cad)).setInactiveIconResource(R.mipmap.cad2).setActiveColorResource(R.color.actionbar_bg_color).setInActiveColorResource(R.color.black_1))
                .setFirstSelectedPosition(0)
                .initialise();

        mBottomNavigationBar.setTabSelectedListener(this);

        setDefaultFragment();
        return view;
    }

    /**
     * set the default fagment
     * <p>
     * the content id should not be same with the parent content id
     */
    private void setDefaultFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MeasureMainFragment homeFragment = measureMainFragment.newInstance(getString(R.string.item_measure));
        transaction.replace(R.id.sub_content, homeFragment).commit();

    }

    @Override
    public void onTabSelected(int position) {
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();

        switch (position) {
            case 0:
                if (measureMainFragment == null) {
                    measureMainFragment = MeasureMainFragment.newInstance(getString(R.string.item_measure));
                }
                beginTransaction.replace(R.id.sub_content, measureMainFragment);
                break;
            case 1:
                if (securityFragment == null) {
                    securityFragment = SecurityFragment.newInstance(getString(R.string.item_security));
                    //设置ActionBar名称
//                    android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//                    actionBar.setCustomView(R.layout.actionbar_plus);
//                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
//                    actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
//                    ((TextView) getActivity().findViewById(R.id.title_name)).setText(getString(R.string.item_security));
//                    //以下代码用于去除阴影
//                    if (Build.VERSION.SDK_INT >= 21) {
//                        actionBar.setElevation(0);
//                    }
                }
                beginTransaction.replace(R.id.sub_content, securityFragment);
                break;
            case 2:
                if (gasFragment == null) {
                    gasFragment = GasFragment.newInstance(getString(R.string.item_gas));
                    //设置ActionBar名称
//                    android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//                    actionBar.setCustomView(R.layout.actionbar_plus);
//                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
//                    actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
//                    ((TextView) getActivity().findViewById(R.id.title_name)).setText(getString(R.string.item_gas));
//                    //以下代码用于去除阴影
//                    if (Build.VERSION.SDK_INT >= 21) {
//                        actionBar.setElevation(0);
//                    }
                }
                beginTransaction.replace(R.id.sub_content, gasFragment);
                break;
            case 3:
                if (cadFragment == null) {
                    cadFragment = CADFragment.newInstance(getString(R.string.item_cad));
                }
                beginTransaction.replace(R.id.sub_content, cadFragment);
                break;
        }
        beginTransaction.commit();

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}
