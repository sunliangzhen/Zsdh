package com.shuxiangbaima.task.view.a;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shuxiangbaima.task.R;
import com.shuxiangbaima.task.ui.main.bean.BannerBean;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/24.
 */
public class DisallowParentTouchViewPager extends ViewPager {
    private List<BannerBean.DataEntity.AdListEntity> list;
    private Context context;
    private ImageOptions imageOptions;
    private int previousSelectedPosition = 0;
    private LinearLayout ll_point_container;
    private int margin = 30;
    private int time = 3000;
    private Handler mHandler;
    private MyAdapter2 myAdapter2;
    private ViewpagerListener mviewpagerListener;
    private float p;

    public DisallowParentTouchViewPager(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DisallowParentTouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }


    private void init() {
        myAdapter2 = new MyAdapter2();
        imageOptions = new ImageOptions.Builder().setImageScaleType(ImageView.ScaleType.FIT_XY).setUseMemCache(true).build();
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mviewpagerListener.viewpagetitemclick(getCurrentItem() % list.size());
//            }
//        });
    }

    public void setData(List<BannerBean.DataEntity.AdListEntity> list, LinearLayout ll_point_container, int margin, int time, ViewpagerListener mviewpagerListener) {
        this.mviewpagerListener = mviewpagerListener;
        this.list = list;
        this.ll_point_container = ll_point_container;
        this.margin = margin;
        this.time = time;
        if (list.size() > 0) {
            initView();
            initAdapter();
        }
    }


    private void initView() {
        View pointView;
        if (ll_point_container.getChildCount() > 0) {
            ll_point_container.removeAllViews();
        }
        LinearLayout.LayoutParams layoutParams;
        for (int i = 0; i < list.size(); i++) {
            pointView = new View(context);
            pointView.setBackgroundResource(R.drawable.selector_page_point);
            layoutParams = new LinearLayout.LayoutParams(AutoUtils.getPercentWidthSize(15), AutoUtils.getPercentWidthSize(15));
            if (i != 0)
                layoutParams.leftMargin = AutoUtils.getPercentWidthSize(20);
            pointView.setEnabled(false);
            ll_point_container.addView(pointView, layoutParams);
        }
    }

    private void initAdapter() {
        if (list.size() > 0) {
            ll_point_container.getChildAt(0).setEnabled(true);
        }
        previousSelectedPosition = 0;
        setAdapter(myAdapter2);
        setOffscreenPageLimit(3);
        setPageMargin(AutoUtils.getPercentWidthSize(margin));
        addOnPageChangeListener(new PageListener());
        int i = 3000000 % list.size();
        if (i == 0) {
            setCurrentItem(3000000);
        } else {
            setCurrentItem(3000000 - i);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = 0;
        float x = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = event.getY();
                x = event.getX();
                stopPlay();
//                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                mHandler.sendEmptyMessageDelayed(0, time);
                break;
            case MotionEvent.ACTION_UP:
                if (event.getY() - y > 0 && Math.abs(event.getX() - x) < 10) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (p == 0) {
                    mviewpagerListener.viewpagetitemclick(list.get(getCurrentItem() % list.size()).getAdLink());
                }
                mHandler.sendEmptyMessageDelayed(0, time);
                break;
            case MotionEvent.ACTION_MOVE:
//                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void startPaly() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                setCurrentItem(getCurrentItem() + 1);
                mHandler.sendEmptyMessageDelayed(0, time);

            }
        };
        mHandler.sendEmptyMessageDelayed(0, time);
    }


    public void stopPlay() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            p = positionOffset;

        }

        @Override
        public void onPageSelected(int position) {
            if (list.size() > 0) {
                int newPosition = position % list.size();
                ll_point_container.getChildAt(previousSelectedPosition).setEnabled(false);
                ll_point_container.getChildAt(newPosition).setEnabled(true);
                previousSelectedPosition = newPosition;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    }

    private class MyAdapter2 extends PagerAdapter {


        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = null;
            if (list.size() > 0) {
                int newPosition = position % list.size();
                imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                x.image().bind(imageView, list.get(newPosition).getAdImg());
                container.addView(imageView);
            }
//            imageView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    stopPlay();
//                    mviewpagerListener.viewpagetitemclick(position % list.size());
//                }
//            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public interface ViewpagerListener {
        void viewpagetitemclick(String link);
    }

}
