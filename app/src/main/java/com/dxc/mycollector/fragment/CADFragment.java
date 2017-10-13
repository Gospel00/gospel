///*
// * copyright (c)2018-8-15
// * DXC technology
// */
//
//package com.dxc.mycollector.fragment;
//
//import android.app.ActionBar;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.dxc.mycollector.R;
//import com.dxc.mycollector.cadutils.LocalFilesActivity;
//
///**
// * Created by gospel on 2017/8/18.
// * About Login
// */
//public class CADFragment extends Fragment {
//
//    Context context;
//
//    public static CADFragment newInstance(String s) {
//        CADFragment homeFragment = new CADFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.ARGS, s);
//        homeFragment.setArguments(bundle);
//        return homeFragment;
//    }
//
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        context = getActivity();
//        //设置ActionBar名称
//        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.setCustomView(R.layout.actionbar_plus);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
//        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
//        ((TextView) getActivity().findViewById(R.id.title_name)).setText(getString(R.string.item_cad));
//        //以下代码用于去除阴影
//        if (Build.VERSION.SDK_INT >= 21) {
//            actionBar.setElevation(0);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.gas_fragment, container, false);
////        startActivity(new Intent(context, LocalFilesActivity.class));
////                Toast.makeText(PersonAcitvity.this, "敬请期待......", Toast.LENGTH_LONG).show();
////        Bundle bundle = getArguments();
////        String s = bundle.getString(Constants.ARGS);
////        TextView textView = (TextView) view.findViewById(R.id.fragment_text_view);
////        textView.setText(s);
//        return view;
//    }
//}
