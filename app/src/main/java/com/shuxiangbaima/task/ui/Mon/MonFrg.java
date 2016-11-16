package com.shuxiangbaima.task.ui.Mon;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.jaeger.library.StatusBarUtil;
import com.shuxiangbaima.task.R;
import com.shuxiangbaima.task.interfaces.Profit;
import com.shuxiangbaima.task.ui.mine.PaiAty;
import com.shuxiangbaima.task.ui.mine.YqAty;
import com.shuxiangbaima.task.view.MySwiperefreshlayout;
import com.shuxiangbaima.task.view.TopScrollView;
import com.shuxiangbaima.task.view.a.CycleView;
import com.shuxiangbaima.task.view.a.MyMarkerView;
import com.toocms.dink5.mylibrary.app.Config;
import com.toocms.dink5.mylibrary.base.BaseFragment;
import com.toocms.dink5.mylibrary.commonutils.StatusBarUtil2;
import com.toocms.dink5.mylibrary.commonwidget.LoadingTip;
import com.toocms.dink5.mylibrary.net.ApiListener;
import com.toocms.dink5.mylibrary.commonutils.utils.JSONUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MonFrg extends BaseFragment implements ApiListener, LoadingTip.onReloadListener {


    @ViewInject(R.id.chart11)
    private LineChart mChart;
    @ViewInject(R.id.ll_point_group1)
    private LinearLayout lin_group;
    @ViewInject(R.id.mon_vp1)
    private ViewPager mon_vp;
    @ViewInject(R.id.loadedTip)
    private LoadingTip loadedTip;
    @ViewInject(R.id.mon_top)
    private RelativeLayout top;
    @ViewInject(R.id.swipeLayout)
    private MySwiperefreshlayout swipeLayout;
    @ViewInject(R.id.scrollview)
    private TopScrollView scrollview;
    @ViewInject(R.id.line_chart)
    private LineChartView lineChart;

    private CycleView v1_cp, v11_cp;
    private CycleView v2_cp, v22_cp;
    private TextView tv_today_profit, tv_today_profit2;                         //今日收益
    private TextView tv_remainder, tv_remainder_t;                            //账户余额
    private TextView tv_assist_profit, tv_assist_profit2;                        //帮赚收益
    private TextView tv_remainder2, tv_remainder22;                           //账户余额
    private TextView tv_pai;
    private TextView tv_pai2;

    private ArrayList<View> list;
    private int previousSelectedPosition = 0;
    private View v_1, v_11;
    private View v_2, v_22;
    private ArrayList<Integer> localImages_list;
    private MyAdapter2 myAdapter2;
    private Profit profit;
    private ArrayList<Map<String, String>> recent_profit;
    private ArrayList<String> date_list;
    private YAxis leftAxis;
    private boolean isEmptyData = true;


    String[] date = {"10-22", "11-22", "12-22", "1-22", "6-22", "5-23", "5-22"};//X轴的标注
    int[] score = {50, 42, 90, 33, 10, 74, 22};//图表的数据点
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    private void newLine() {
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化
    }

    /**
     * 设置X 轴的显示
     */
    private void getAxisXLables() {
        for (int i = 0; i < date_list.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date_list.get(i)));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints() {
        for (int i = 0; i < recent_profit.size(); i++) {
            mPointValues.add(new PointValue(i, Float.valueOf(recent_profit.get(i).get("profit"))));
        }
    }

    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLUE);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
//        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextColor(Color.BLUE);
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        axisY.setHasLines(true);
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        lineChart.setCurrentViewport(v);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.frg_mon;

    }

    @Override
    public void initPresenter() {
    }

    @Override
    protected void initView() {
        localImages_list = new ArrayList<>();
        localImages_list.add(1);
        localImages_list.add(1);
        myAdapter2 = new MyAdapter2();
        profit = new Profit();
        recent_profit = new ArrayList<>();
        date_list = new ArrayList<>();
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Config.isLogin()) {
            loadedTip.setLoadingTip(LoadingTip.LoadStatus.loading);
            profit.recent_profit(getActivity(), this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeLayout.setScrollview(scrollview);
        v_1 = getLayoutInflater(savedInstanceState).inflate(R.layout.frg_mondetails, null);
        v_11 = getLayoutInflater(savedInstanceState).inflate(R.layout.frg_mondetails1, null);
        tv_today_profit = (TextView) v_1.findViewById(R.id.mon_tv_today_profit);
        tv_today_profit2 = (TextView) v_11.findViewById(R.id.mon_tv_today_profit);
        tv_remainder = (TextView) v_1.findViewById(R.id.mon_tv_remainder);
        tv_remainder_t = (TextView) v_11.findViewById(R.id.mon_tv_remainder);
        TextView tv_yq = (TextView) v_1.findViewById(R.id.mondetails_tv_yq);
        v1_cp = (CycleView) v_1.findViewById(R.id.arc);
        v11_cp = (CycleView) v_11.findViewById(R.id.arc11);
        tv_pai = (TextView) v_1.findViewById(R.id.mondetails_tv_pai);
        tv_pai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PaiAty.class, null);
            }
        });
        tv_yq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(YqAty.class, null);
            }
        });

        v_2 = getLayoutInflater(savedInstanceState).inflate(R.layout.frg_mondetails2, null);
        v_22 = getLayoutInflater(savedInstanceState).inflate(R.layout.frg_mondetails22, null);
        TextView tv_yq2 = (TextView) v_2.findViewById(R.id.mondetails2_tv_yq);
        v2_cp = (CycleView) v_2.findViewById(R.id.arc2);
        v22_cp = (CycleView) v_22.findViewById(R.id.arc22);
        tv_assist_profit = (TextView) v_2.findViewById(R.id.mon2_tv_assist_profit);
        tv_assist_profit2 = (TextView) v_22.findViewById(R.id.mon2_tv_assist_profit);
        tv_remainder2 = (TextView) v_2.findViewById(R.id.mon2_tv_remainder);
        tv_remainder22 = (TextView) v_22.findViewById(R.id.mon2_tv_remainder);
        tv_pai2 = (TextView) v_2.findViewById(R.id.mondetails2_tv_pai);
        tv_pai2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PaiAty.class, null);
            }
        });
        tv_yq2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(YqAty.class, null);
            }
        });
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view, true, date_list);
        mChart.setMarkerView(mv);
        setData(7, 10);
        mChart.setPinchZoom(false);
        mChart.setTouchEnabled(true);
        mChart.setDescription("最近7天收益");
        mChart.setDescriptionColor(0x00000000);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
//        mChart.setMaxVisibleValueCount(2);
//        mChart.setVisibleYRangeMinimum(10, YAxis.AxisDependency.LEFT);
        leftAxis = mChart.getAxisLeft();
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextSize(12f);
        rightAxis.setTextSize(0f);
        rightAxis.setTextColor(0x00ffffff);
//        leftAxis.setAxisMinValue(0f);
        leftAxis.resetAxisMaxValue();
        leftAxis.resetAxisMinValue();
        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                return date_list.get(index) + "日";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        initview();
        initAdapter();
        loadedTip.setOnReloadListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusBarHeight = StatusBarUtil2.getStatusBarHeight(getActivity());
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) top.getLayoutParams();
            layoutParams.topMargin = statusBarHeight;
            top.setLayoutParams(layoutParams);
        }
//        new AsyncTask<Integer, Integer, Integer>() {
//            @Override
//            protected Integer doInBackground(Integer... params) {
//                for (int i = 0; i <= 100; i++) {
//                    publishProgress(i);
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onProgressUpdate(Integer... values) {
//                super.onProgressUpdate(values);
//                v1_cp.setProgress(values[0]);
//                v2_cp.setProgress(values[0]);
//                v11_cp.setProgress(values[0]);
//                v22_cp.setProgress(values[0]);
//            }
//        }.execute(0);
        swipeLayout.setProgressViewEndTarget(true, AutoUtils.getPercentHeightSize(300));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Config.isLogin()) {
                    profit.recent_profit(getActivity(), MonFrg.this);
                }
            }
        });
        scrollview.setTopScrollViewListen(new TopScrollView.ScrollowListen() {
            @Override
            public void top() {
                swipeLayout.setEnabled(true);
            }

            @Override
            public void center() {
                swipeLayout.setEnabled(false);
            }
        });
//        mon_vp.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        swipeLayout.setEnabled(false);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        swipeLayout.setEnabled(true);
//                        break;
//                }
//                return false;
//            }
//        });
        swipeLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.yellow, R.color.green);
    }


    @Event(value = {R.id.mon_tv_applay})
    private void onTestBaidulClick(View view) {
        switch (view.getId()) {
            case R.id.mon_tv_applay:
                startActivity(ApplayAty.class, null);
                break;
        }
    }

    @Override
    public void onCancelled(Callback.CancelledException var1) {

    }

    @Override
    public void onComplete(RequestParams var1, String var2) {
        loadedTip.setLoadingTip(LoadingTip.LoadStatus.finish);
        swipeLayout.setRefreshing(false);
        Map<String, String> map = JSONUtils.parseKeyAndValueToMap(var2);
        if (var1.getUri().contains("recent_profit")) {
            if (map.get("status").equals("200")) {
                Map<String, String> data_map = JSONUtils.parseDataToMap(var2);
                tv_remainder.setText("余额：" + data_map.get("remainder") + "元");
                tv_remainder_t.setText("余额：" + data_map.get("remainder") + "元");
                tv_remainder2.setText("余额：" + data_map.get("remainder") + "元");
                tv_remainder22.setText("余额：" + data_map.get("remainder") + "元");
                tv_today_profit.setText("￥" + data_map.get("today_profit"));
                tv_today_profit2.setText("￥" + data_map.get("today_profit"));
                tv_assist_profit.setText("￥" + data_map.get("assist_profit"));
                tv_assist_profit2.setText("￥" + data_map.get("assist_profit"));
                recent_profit = JSONUtils.parseKeyAndValueToMapList(data_map.get("recent_profit"));
                date_list = new ArrayList<>();
                for (int i = 0; i < recent_profit.size(); i++) {
                    String earn_date = recent_profit.get(i).get("earn_date");
                    earn_date = earn_date.substring(earn_date.length() - 2, earn_date.length());
                    date_list.add(earn_date);
                    if (Float.valueOf(recent_profit.get(i).get("profit")) > 0) {
                        isEmptyData = false;
                    }
                    newLine();
                }
                if (!isEmptyData) {
                    MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view, false, date_list);
                    mChart.setMarkerView(mv);
                }
                setData(7, 10);
                mChart.invalidate();
            }
        }

    }

    @Override
    public void onError(Map<String, String> var1, RequestParams var2) {
    }

    @Override
    public void onException(Throwable var1, RequestParams params) {
        swipeLayout.setRefreshing(false);
        loadedTip.setLoadingTip(LoadingTip.LoadStatus.finish);
        showNetError();
    }

    private void initview() {
        list = new ArrayList<>();
        View pointView;
        LinearLayout.LayoutParams layoutParams;
        for (int i = 0; i < 2; i++) {
            pointView = new View(getActivity());
            pointView.setBackgroundResource(R.drawable.selector_page_point);
            layoutParams = new LinearLayout.LayoutParams(10, 10);
            if (i != 0)
                layoutParams.leftMargin = 10;
            pointView.setEnabled(false);
            lin_group.addView(pointView, layoutParams);
        }
        list.add(v_22);
        list.add(v_1);
        list.add(v_2);
        list.add(v_11);
    }

    private void initAdapter() {
        lin_group.getChildAt(0).setEnabled(true);
        lin_group.getChildAt(1).setEnabled(false);
        previousSelectedPosition = 0;
        mon_vp.setAdapter(myAdapter2);
        mon_vp.addOnPageChangeListener(new PageListener());
        mon_vp.setOffscreenPageLimit(4);
        mon_vp.setCurrentItem(1);
    }

    private void getData() {
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String day = df.format(new Date());
        int day_today = Integer.parseInt(day);
        for (int i = 0; i < 7; i++) {
            if (day_today < 10) {
                date_list.add("0" + day_today);
            } else {
                date_list.add(day_today + "");
            }
            day_today++;
        }
    }


    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            if (!isEmptyData) {
                Float profit = (Float.valueOf(recent_profit.get(i).get("profit")));
                values.add(new Entry(i, profit));
            } else {
                float val = (float) (Math.random() * range) + 3;
                if (i == 1) {
                    values.add(new Entry(i, 8));
                } else {
                    values.add(new Entry(i, 0));
                }
            }
        }
        LineDataSet set1;
        set1 = new LineDataSet(values, "");
        set1.enableDashedLine(10f, 5f, 0f);
        if (isEmptyData) {
            set1.setColor(0x00EC6A6A);
            set1.setCircleColor(0x00EC6A6A);
            set1.setHighLightColor(0x00EC6A6A);
            set1.setDrawValues(false);
        } else {
            set1.setColor(0xffEC6A6A);
            set1.setCircleColor(0xffEC6A6A);
            set1.setHighLightColor(0xffff0000);
            set1.setDrawValues(true);
        }
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setHighlightEnabled(true);
        if (Utils.getSDKInt() >= 18) {
            if (isEmptyData) {
                set1.setFillColor(0xfff5f5f5);
            } else {
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
        } else {
            set1.setFillColor(0x00ffffff);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        mChart.setData(data);
    }

    @Override
    public void reload() {
        if (Config.isLogin()) {
            loadedTip.setLoadingTip(LoadingTip.LoadStatus.loading);
            profit.recent_profit(getActivity(), this);
        }
    }


    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mon_vp.setCurrentItem(list.size() - 2, false);
                    }
                }, 500);
            } else if (position == list.size() - 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mon_vp.setCurrentItem(1, false);
                    }
                }, 500);
            }

            if (mon_vp.getCurrentItem() == 1) {
                lin_group.getChildAt(0).setEnabled(true);
                lin_group.getChildAt(1).setEnabled(false);
            } else if (mon_vp.getCurrentItem() == 2) {
                lin_group.getChildAt(0).setEnabled(false);
                lin_group.getChildAt(1).setEnabled(true);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    }

    private class MyAdapter2 extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = list.get(position % list.size());
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }


}
