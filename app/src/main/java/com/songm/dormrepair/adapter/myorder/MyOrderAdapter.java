package com.songm.dormrepair.adapter.myorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.disklrucache.Util;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;
import com.songm.dormrepair.R;
import com.songm.dormrepair.model.myorder.Order;
import com.songm.dormrepair.utils.UrlUtils;
import com.wenhuaijun.easyimageloader.imageLoader.EasyImageLoader;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by SongM on 2017/9/20.
 * 我的报修单列表适配器
 */

public class MyOrderAdapter extends BaseAdapter {
    private Context context;
    private List<Order.OrderListBean> list;

    public MyOrderAdapter(Context context, List<Order.OrderListBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView != null) {
            view = convertView;
        } else {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_myorder, null);
        }

        // 绑定控件
        final ImageView item_myorder_orderImg = (ImageView) view.findViewById(R.id.item_myorder_orderImg);
        TextView item_myorder_orderInfo = (TextView) view.findViewById(R.id.item_myorder_orderInfo);
        TextView item_myorder_orderScort = (TextView) view.findViewById(R.id.item_myorder_orderScort);
        TextView item_myorder_orderRoom = (TextView) view.findViewById(R.id.item_myorder_orderRoom);
        TextView item_myorder_orderStartDate = (TextView) view.findViewById(R.id.item_myorder_orderStartDate);

        // 取到订单信息
        Order.OrderListBean orderListBean = list.get(position);

        // 加载网络图片
        /*OkGo.<Bitmap>get(UrlUtils.HOST + orderListBean.getListImgUrl())
                .tag(context)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Response<Bitmap> response) {
                        item_myorder_orderImg.setImageBitmap(response.body());
                    }
                });*/
        // 根据图片url给imageView加载图片，自动本地缓存、内存缓存，注意Context需使用ApplicationContext，否则会导致内存泄露
        EasyImageLoader.getInstance(context).bindBitmap(UrlUtils.HOST + orderListBean.getListImgUrl(), item_myorder_orderImg);

        // 设置订单信息
        item_myorder_orderInfo.setText(orderListBean.getOrderInfo());
        item_myorder_orderRoom.setText(orderListBean.getOrderRoom());
        String orderStartDate = new SimpleDateFormat("yyyy/MM/dd").format(orderListBean.getOrderStartTime());
        item_myorder_orderStartDate.setText(orderStartDate);
        String orderScort = "";
        switch (orderListBean.getOrderSort()) {
            case 1:
                orderScort = "普通";
                break;
            case 2:
                orderScort = "急";
                break;
            case 3:
                orderScort = "加急";
                break;
            default:
                orderScort = "未评定";
                break;
        }
        item_myorder_orderScort.setText(orderScort);

        return view;
    }
}
