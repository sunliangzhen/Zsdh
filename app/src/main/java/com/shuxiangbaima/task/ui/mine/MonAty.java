package com.shuxiangbaima.task.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shuxiangbaima.task.R;
import com.shuxiangbaima.task.interfaces.Profit;
import com.shuxiangbaima.task.ui.page.LoadMore;
import com.toocms.dink5.mylibrary.base.BasAty;
import com.toocms.dink5.mylibrary.commonutils.utils.JSONUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by Administrator on 2016/8/10.
 */
public class MonAty extends BasAty {

    @ViewInject(R.id.mon_ptr_frame)
    private PtrFrameLayout mon_refush;
    @ViewInject(R.id.mon_load_more)
    private LoadMoreListViewContainer mon_load_more;
    @ViewInject(R.id.mon_lv)
    private ListView mon_lv;
    @ViewInject(R.id.mon_01)
    private TextView tv_01;
    @ViewInject(R.id.mon_02)
    private TextView tv_02;
    private Profit profit;
    private MyAdapter myAdapter;
    private int next_offset = 0;

    private ArrayList<Map<String, String>> friends_list;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_mon;
    }

    @Override
    protected void requestData() {
        showProgressContent();
        profit.friends(next_offset, this, this);
    }

    @Override
    protected void initialized() {
        profit = new Profit();
        friends_list = new ArrayList<>();
        myAdapter = new MyAdapter();
    }

    @Override
    public void onComplete(RequestParams var1, String var2) {
        super.onComplete(var1, var2);
        removeProgressContent();
        Map<String, String> map = JSONUtils.parseKeyAndValueToMap(var2);
        if (var1.getUri().contains("friends")) {
            if (map.get("status").equals("200")) {
                Map<String, String> map1 = JSONUtils.parseDataToMap(var2);
                if (next_offset == 0) {
                    friends_list = JSONUtils.parseKeyAndValueToMapList(map1.get("friends"));
                    if (friends_list.size() > 0) {
                        tv_01.setVisibility(View.GONE);
                        tv_02.setVisibility(View.GONE);
                        mon_load_more.setVisibility(View.VISIBLE);
                    } else {
                        tv_01.setVisibility(View.VISIBLE);
                        tv_02.setVisibility(View.VISIBLE);
                        mon_load_more.setVisibility(View.GONE);
                    }
                    mon_load_more.loadMoreFinish(true, true);
                } else {
                    friends_list.addAll(JSONUtils.parseKeyAndValueToMapList(map1.get("friends")));
                    if (Integer.parseInt(map1.get("next_offset")) == -1) {
                        //第一个参数是：数据是否为空；第二个参数是：是否还有更多数据
                        mon_load_more.loadMoreFinish(true, false);
                    } else {
                        mon_load_more.loadMoreFinish(true, true);
                    }
                }
                next_offset = Integer.parseInt(map1.get("next_offset"));
                mon_refush.refreshComplete();
                myAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mon_lv.setAdapter(myAdapter);

        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        PtrFrameLayout.LayoutParams params2 = new PtrFrameLayout.LayoutParams(-1, -2);
        params2.topMargin = AutoUtils.getPercentHeightSize(130);
        header.setLayoutParams(params2);
        header.setPadding(0, AutoUtils.getPercentHeightSize(0), 0, AutoUtils.getPercentHeightSize(0));
        header.setPtrFrameLayout(mon_refush);

        mon_refush.setLoadingMinTime(1000);
        mon_refush.setDurationToCloseHeader(1500);
        mon_refush.setHeaderView(header);
        mon_refush.addPtrUIHandler(header);
        mon_refush.setDurationToClose(100);
        mon_refush.setPinContent(true);
        //下拉刷新的组件
        mon_refush.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mon_lv, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                next_offset = 0;
                profit.friends(next_offset, MonAty.this, MonAty.this);
            }
        });

        //加载更多的组件
        mon_load_more.setAutoLoadMore(true);//设置是否自动加载更多
        LoadMore footerView = new LoadMore(this);
        footerView.setVisibility(View.GONE);
        mon_load_more.setLoadMoreView(footerView);
        mon_load_more.setLoadMoreUIHandler(footerView);
        mon_load_more.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                profit.friends(next_offset, MonAty.this, MonAty.this);
            }
        });

    }

    @Override
    public void initPresenter() {

    }


    @Event(value = {R.id.mon_imgv_back, R.id.mon_tv_more})
    private void onTestBaidulClick(View view) {
        switch (view.getId()) {
            case R.id.mon_imgv_back:
                finish();
                break;
            case R.id.mon_tv_more:
                startActivity(YqAty.class, null);
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return friends_list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(MonAty.this).inflate(R.layout.item_mon, parent, false);
                x.view().inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_nickname.setText(friends_list.get(position).get("nickname"));
            viewHolder.tv_phone.setText(friends_list.get(position).get("username"));
            viewHolder.tv_money.setText(friends_list.get(position).get("profit") + "元");
            return convertView;
        }

        private class ViewHolder {
            @ViewInject(R.id.item_mon_tv_nickname)
            private TextView tv_nickname;
            @ViewInject(R.id.item_mon_tv_phone)
            private TextView tv_phone;
            @ViewInject(R.id.item_mon_tv_money)
            private TextView tv_money;
        }

    }

}
