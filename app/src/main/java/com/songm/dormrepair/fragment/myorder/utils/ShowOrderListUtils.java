package com.songm.dormrepair.fragment.myorder.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.songm.dormrepair.OrderInfoActivity;
import com.songm.dormrepair.adapter.myorder.MyOrderAdapter;
import com.songm.dormrepair.R;
import com.songm.dormrepair.model.myorder.Order;
import com.songm.dormrepair.utils.UrlUtils;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Created by SongM on 2017/9/20.
 * 根据报修单状态码加载订单列表
 */

public class ShowOrderListUtils {

    private LayoutInflater inflater;
    private Context context;
    private int orderState;
    private PtrFrameLayout mPtrFrame;
    private ListView lv_myorderList;

    public ShowOrderListUtils(LayoutInflater inflater, int orderState) {
        this.inflater = inflater;
        this.orderState = orderState;
        this.context = inflater.getContext();
    }

    public View view() {
        View view = inflater.inflate(R.layout.fragment_myorder_list, null);

        // 绑定控件
        lv_myorderList = (ListView) view.findViewById(R.id.lv_myorderList);
        mPtrFrame = (PtrFrameLayout) view.findViewById(R.id.ptr);

        // 设置列表点击监听
        lv_myorderList.setOnItemClickListener(new ItemClick());

        // 自动加载订单列表
        getMyOrderList();

        /**
         * 经典 风格的头部实现
         */
        final PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(context);
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, 0);

        mPtrFrame.setHeaderView(header);

        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {

            //需要加载数据时触发
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMyOrderList();
                    }
                }, 0);
            }

            /**
             * 检查是否可以执行下来刷新，比如列表为空或者列表第一项在最上面时。
             */
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // 默认实现，根据实际情况做改动
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        return view;
    }

    // 加载我的报修单列表
    private void getMyOrderList() {
        // 取出我的信息
        SharedPreferences sharedPreferences = context.getSharedPreferences("info", context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", null);
        boolean stuOrHmr = sharedPreferences.getBoolean("stuOrHmr", true);
        // 设置url
        String url = UrlUtils.orderByStu(orderState, id);
        if(!stuOrHmr) {
            url = UrlUtils.orderByHmr(orderState, id);
        }
        // OkGo
        OkGo.<String>get(url)
                .tag(context)
                .execute(new StringCallback() {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mPtrFrame.refreshComplete();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        Order order = new Gson().fromJson(response.body(), Order.class);
                        if(order.getFind().equals("success")) {
                            lv_myorderList.setAdapter(new MyOrderAdapter(context, order.getOrderList()));
                        }
                    }
                });
    }

    // 内部类监听器
    class ItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 取出orderId
            Order.OrderListBean orderListBean = (Order.OrderListBean) parent.getItemAtPosition(position);
            int orderId = orderListBean.getOrderId();
            // 跳转到报修单详细页面
            Intent intent = new Intent(context, OrderInfoActivity.class);
            intent.putExtra("orderId", orderId);
            context.startActivity(intent);
        }
    }
}
