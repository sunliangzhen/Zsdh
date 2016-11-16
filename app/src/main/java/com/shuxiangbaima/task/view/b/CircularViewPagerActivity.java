package com.shuxiangbaima.task.view.b;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/23.
 */

public class CircularViewPagerActivity extends FragmentActivity {
    /**
     * 首末位扩展后fragments的大小，等于导航条的size加2
     */
    private int size ;
    protected ViewPager viewPager ;//可以在子类中直接调用
    private ViewPager.OnPageChangeListener listener ;

    /**
     * @param viewPagerId ViewPager的id
     * @param fragments 首末位扩展的后fragments
     */
    protected void initViewPager(int viewPagerId,ArrayList<Fragment> fragments){
        size = fragments.size() ;
        viewPager = (ViewPager)findViewById(viewPagerId) ;
//        viewPager.setAdapter(new CircularFragmentPagerAdapter(getSupportFragmentManager(), fragments)) ;
        //启动ViewPager时从position=1的item开始，而不是显示首位添加的Fragment
        viewPager.setCurrentItem(1);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 循环控制全在此处控制 ：
             * 循环原理在此监听方法中实现，当即将要显示首位扩展的item时，通过调用viewPager.setCurrentItem(position,false)
             *  以无动画的方式跳转到相应的item上
             */
            @Override
            public void onPageSelected(int position) {//position表示在扩展Fragments中即将要显示的Fragment的索引
                if(position == 0){//首位扩展的item
                    //延迟执行才能看到动画
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(size-2,false) ;//跳转到末位
                        }
                    }, 50) ;
                }else if(position ==size-1){//末位扩展的item
                    //延迟执行才能看到动画
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(1,false) ;//跳转到首位
                        }
                    }, 50) ;

                }

                if(null != listener){
                    listener.onPageSelected(position) ;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                if(null != listener){
                    listener.onPageScrolled(position, positionOffset, positionOffsetPixels) ;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(null != listener){
                    listener.onPageScrollStateChanged(state) ;
                }
            }
        });
    }

    /**
     *  通过扩展fragments中元素的索引来计算对应扩展前fragments集合中的索引
     * @param broadenPosition 扩展fragments中的position
     * @param size 扩展前fragments的大小，等于导航条的大小，等于扩展后fragments的大小减2
     * @return 标准fragments集合中的position
     */
    protected int getRealPosition(int broadenPosition,int size){
        int position ;
        if(broadenPosition==0){
            position = size-1 ;
        }else if(broadenPosition==size+1){
            position = 0 ;
        }else{
            position=broadenPosition-1 ;
        }

        return position ;
    }

    /**
     * 给ViewPager设置OnPageChangeListener监听器
     */
    protected void setOnPageChangeListener(ViewPager.OnPageChangeListener l){
        listener = l ;
    }

}
