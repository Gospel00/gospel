/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * @desc 水波纹效果
 * Created by Gospel on 2017/9/6 14:54
 * DXC technology
 */
public class ShuiBoWenView extends LinearLayout {

    //被点击到的目标控件，就是要绘制水波纹的控件
    private View targetView;

    //要绘制水波纹的区域，即为被点击的控件的范围
    private RectF rectF;

    //要绘制的水波纹的最大半径
    private int radio;
    //水波纹的中心
    private int centerX;
    private int centerY;

    private Paint mPaint;

    private int currentRadio;

    //父控件的绝对坐标
    private int[] loaction;

    //是否进行绘制
    private boolean isDraw = false;

    public ShuiBoWenView(Context context) {
        super(context);
        initView();
    }


    public ShuiBoWenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ShuiBoWenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        //抗锯齿
        mPaint.setAntiAlias(true);
        //实心
        mPaint.setStyle(Paint.Style.FILL);
        //颜色
        mPaint.setColor(Color.BLUE);
        mPaint.setAlpha(100);
    }


    //重写触摸事件，每次down事件中，开始测量绘制
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //down事件中,开始绘制
                isDraw = true;

                currentRadio = 0;
                //点击的位置，也是水波纹的圆心
                int downX = (int) event.getRawX();
                int downY = (int) event.getRawY();

                centerX = downX;
                centerY = downY;

                //父控件的位置


                //找到对应的子控件
                targetView = findTargerView(downX, downY);


                //如果没有子控件被点击，就直接结束
                if (targetView != null) {

                    //寻找圆的半径，要铺满整个子控件，那么半径必须是距离控件边距最大的那个值
                    int left = (int) (centerX - rectF.left);
                    int right = (int) (rectF.right - centerX);
                    int top = (int) (rectF.top - centerY);
                    int bottom = (int) (rectF.bottom - centerY);

                    //获取半径
                    radio = Math.max(Math.max(left, right), Math.max(top, bottom));

                    //矫正绘制区域
                    clipRectf();

                    //重新绘制
                    postInvalidate();
                }
                break;
            default:
                isDraw = false;
                currentRadio = 0;
                postInvalidate();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    //划定区域
    private void clipRectf() {
        loaction = new int[2];
        getLocationOnScreen(loaction);
        rectF.top = rectF.top - loaction[1];
        rectF.bottom = rectF.bottom - loaction[1];
        rectF.left -= loaction[0];
        rectF.right -= loaction[0];

        centerX -= loaction[0];
        centerY -= loaction[1];


    }

    //在draw中被调用的方法，用来绘制子控件
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //子控件绘制完成后，开始绘制水波纹
        canvas.save();

        if (targetView == null) return;


        if (isDraw) {

            //限制绘制区域
            canvas.clipRect(rectF);
            canvas.drawCircle(centerX, centerY, currentRadio, mPaint);
            currentRadio += 10;
            postInvalidateDelayed(30);


        } else {
            canvas.clipRect(rectF);
            canvas.drawCircle(centerX, centerY, currentRadio, mPaint);
        }
        canvas.restore();
    }


    /**
     * 找到被点击的子控件
     *
     * @param downX
     * @param downY
     * @return
     */
    private View findTargerView(int downX, int downY) {
        ArrayList<View> touchables = getTouchables();//此方法会返回所有被点击的控件
        View touchable = null;

        if (touchables == null) {
            return touchable;
        }

        for (View child :
                touchables) {
            int[] location = new int[2];
            //获取该子控件在屏幕上的位置
            child.getLocationOnScreen(location);

            int left = location[0];
            int top = location[1];
            int right = left + child.getMeasuredWidth();
            int bottom = top + child.getMeasuredHeight();
            //获取该控件所在区域的矩形
            RectF rectF = new RectF(left, top, right, bottom);

            //如果这个子控件的位置包含点击的位置，说明点击到该控件了
//            if (rectF.contains(downX, downY)) {
            touchable = child;
            this.rectF = rectF;
//            }

        }
        return touchable;
    }
}
