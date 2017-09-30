package com.songm.dormrepair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.songm.dormrepair.adapter.orderinfo.EvalAdapter;
import com.songm.dormrepair.model.orderinfo.Eval;
import com.songm.dormrepair.model.orderinfo.Img;
import com.songm.dormrepair.model.orderinfo.Order;
import com.songm.dormrepair.utils.SnackbarUtil;
import com.songm.dormrepair.utils.UrlUtils;
import com.wenhuaijun.easyimageloader.imageLoader.EasyImageLoader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by SongM on 2017/9/16.
 * 订单信息
 */

public class OrderInfoActivity extends AppCompatActivity {

    private ImmersionBar immersionBar;
    private TextView tv_order_info_orderRoom, tv_order_info_orderId, tv_order_info_dateTime, tv_order_info_orderState, tv_order_info_orderInfo;
    private Button btn_order_info_evalOrder;
    private LinearLayout ll_order_info_orderImg;
    private Order.OrderBean orderBean;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.colorPrimary); // 状态栏颜色，不写默认透明色
        immersionBar.init();

        // 标题返回
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 绑定所有控件
        tv_order_info_orderRoom = (TextView) findViewById(R.id.tv_order_info_orderRoom);
        tv_order_info_orderId = (TextView) findViewById(R.id.tv_order_info_orderId);
        tv_order_info_dateTime = (TextView) findViewById(R.id.tv_order_info_dateTime);
        tv_order_info_orderState = (TextView) findViewById(R.id.tv_order_info_orderState);
        tv_order_info_orderInfo = (TextView) findViewById(R.id.tv_order_info_orderInfo);
        btn_order_info_evalOrder = (Button) findViewById(R.id.btn_order_info_evalOrder);

        // 取到订单ID
        orderId = getIntent().getIntExtra("orderId", 0);

        gohttp();
    }

    // 网络请求
    private void gohttp() {
        // 清空图片缓存
        try {
            ll_order_info_orderImg.removeAllViews();
        } catch (Exception e) {

        }

        // 查询订单详细信息GET请求
        OkGo.<String>get(UrlUtils.orderFindByOrderId(orderId)) // 请求方式和请求url
                .tag(this) // 请求的 tag, 主要用于取消对应的请求
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response.code() == 200) {
                            Order order = new Gson().fromJson(response.body(), Order.class);
                            if (order.getFind().equals("success")) {
                                orderBean = order.getOrder();
                                tv_order_info_orderRoom.setText(orderBean.getOrderRoom());
                                tv_order_info_orderId.setText("单号：" + orderBean.getOrderId());
                                tv_order_info_dateTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(orderBean.getOrderStartTime())));
                                String orderState = "";
                                switch (orderBean.getOrderState()) {
                                    case 1:
                                        orderState = "正在审核";
                                        break;
                                    case 2:
                                        orderState = "审核通过";
                                        break;
                                    case 3:
                                        orderState = "审核失败";
                                        btn_order_info_evalOrder.setVisibility(View.VISIBLE); // 将评价按钮激活
                                        break;
                                    case 11:
                                        orderState = "正在维修";
                                        break;
                                    case 12:
                                        orderState = "维修完成";
                                        btn_order_info_evalOrder.setVisibility(View.VISIBLE); // 将评价按钮激活
                                        break;
                                    case 13:
                                        orderState = "维修失败";
                                        btn_order_info_evalOrder.setVisibility(View.VISIBLE); // 将评价按钮激活
                                        break;
                                }
                                tv_order_info_orderState.setText(orderState);
                                tv_order_info_orderInfo.setText(orderBean.getOrderInfo());
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Toast.makeText(getApplicationContext(), "请求出错了！", Toast.LENGTH_SHORT).show();
                        super.onError(response);
                    }
                });

        // 获取报修订单的图片
        OkGo.<String>get(UrlUtils.imgFindByOrderId(orderId)) // 请求方式和请求url
                .tag(this) // 请求的 tag, 主要用于取消对应的请求
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response.code() == 200) {
                            Img img = new Gson().fromJson(response.body(), Img.class);
                            if (img.getFind().equals("success")) {
                                List<Img.ImgListBean> imgList = img.getImgList();
                                for (int i = 0; i < imgList.size(); i++) {
                                    String imgUrl = UrlUtils.HOST + imgList.get(i).getImgUrl();
                                    setImg(imgUrl);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Toast.makeText(getApplicationContext(), "请求出错了！", Toast.LENGTH_SHORT).show();
                        super.onError(response);
                    }
                });

        // 获取报修订单的评价
        getEvalList();
    }

    // 获取报修订单的评价
    private void getEvalList() {
        OkGo.<String>get(UrlUtils.evalFindByOrderId(orderId)).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                ListView lv_order_info_orderEval = (ListView) findViewById(R.id.lv_order_info_orderEval);
                Eval eval = new Gson().fromJson(response.body(), Eval.class);
                lv_order_info_orderEval.setAdapter(new EvalAdapter(getApplicationContext(), eval.getEvalList()));
                setListViewHeightBasedOnChildren(((TextView) findViewById(R.id.tv_order_info_evalNum)), lv_order_info_orderEval);

                // 设置item点击查看事件
                lv_order_info_orderEval.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Eval.EvalListBean evalListBean = (Eval.EvalListBean) parent.getItemAtPosition(position);
                        Intent intent = new Intent(getApplicationContext(), OrderEvalInfoActivity.class);
                        intent.putExtra("evalName", evalListBean.getEvalName());
                        intent.putExtra("evalContent", evalListBean.getEvalContent());
                        startActivity(intent);
                    }
                });
            }
        });
    }

    /**
     * 动态设置ListView的高度，及评论数
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(TextView evalNum, ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        // 设置评价数
        evalNum.setText("评价(" + listAdapter.getCount() + ")");

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    // 获取并加载图片
    private void setImg(final String imgUrl) {
        /*OkGo.<Bitmap>get(imgUrl)
                .tag(this)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Response<Bitmap> response) {
                        if (response.code() == 200) {
                            // 将图片添加到布局中
                            ll_order_info_orderImg = (LinearLayout) findViewById(R.id.ll_order_info_orderImg);
                            ImageView imageView = new ImageView(getApplicationContext());
                            // 设置图片大小及间距
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(250, 500);
                            lp.setMargins(15, 0, 15, 0);
                            imageView.setLayoutParams(lp);
                            imageView.setImageBitmap(response.body());
                            ll_order_info_orderImg.addView(imageView);

                            // 图片点击事件
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), LookImageActivity.class);
                                    intent.putExtra("imgUrl", imgUrl);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });*/
        // 将图片添加到布局中
        ll_order_info_orderImg = (LinearLayout) findViewById(R.id.ll_order_info_orderImg);
        ImageView imageView = new ImageView(getApplicationContext());
        // 设置图片大小及间距
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(250, 500);
        lp.setMargins(15, 0, 15, 0);
        imageView.setLayoutParams(lp);
        // 根据图片url给imageView加载图片，自动本地缓存、内存缓存，注意Context需使用ApplicationContext，否则会导致内存泄露
        EasyImageLoader.getInstance(getApplicationContext()).bindBitmap(imgUrl, imageView);
        ll_order_info_orderImg.addView(imageView);

        // 图片点击事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LookImageActivity.class);
                intent.putExtra("imgUrl", imgUrl);
                startActivity(intent);
            }
        });
    }

    // orderInfoMore
    public void click_orderInfoMore(View view) {
        Intent intent = new Intent(this, OrderInfoMoreActivity.class);
        // 取到创建人ID
        if (orderBean.getStuId() != null) {
            intent.putExtra("authorId", orderBean.getStuId());
            intent.putExtra("isStu", true);
        } else {
            intent.putExtra("authorId", orderBean.getHmrId().toString());
            intent.putExtra("isStu", false);
        }
        // 取到维修员ID
        String repairerId = null;
        if (orderBean.getRepairerId() != null) {
            repairerId = orderBean.getRepairerId().toString();
        }
        intent.putExtra("repairerId", repairerId);
        startActivity(intent);
    }

    // 评价
    public void click_evalOrder(View view) {
        new CircleDialog.Builder(OrderInfoActivity.this)
                .setCanceledOnTouchOutside(false)
                .setCancelable(true)
                .setTitle("评价")
                .setInputHint("请输入评价的内容")
                .configInput(new ConfigInput() {
                    @Override
                    public void onConfig(InputParams params) {
                        // params.inputBackgroundResourceId = R.drawable.bg_input;
                    }
                })
                .setNegative("取消", null)
                .setPositiveInput("提交", new OnInputClickListener() {
                    @Override
                    public void onClick(final String text, View v) {
                        if (text != null && !text.equals("")) {
                            // 取到用户信息
                            SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
                            boolean stuOrHmr = sp.getBoolean("stuOrHmr", true);
                            String id = sp.getString("id", null);
                            // OkGo
                            PostRequest<String> tag = OkGo.<String>post(UrlUtils.addEval).tag(this);
                            // 设置请求参数
                            if(stuOrHmr) {
                                tag.params("profession", "stu");
                            } else {
                                tag.params("profession", "hmr");
                            }
                            tag.params("id", id);
                            tag.params("orderId", orderId);
                            tag.params("evalContent", text);
                            // 发送
                            final ProgressDialog dialog = new ProgressDialog(OrderInfoActivity.this); // 加载框
                            dialog.setTitle("提交中...");
                            tag.execute(new StringCallback() {
                                @Override
                                public void onStart(Request<String, ? extends Request> request) {
                                    super.onStart(request);
                                    dialog.show();
                                }

                                @Override
                                public void onFinish() {
                                    super.onFinish();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onSuccess(Response<String> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        String add_state = jsonObject.getString("add");
                                        if (add_state.equals("error")) {
                                            SnackbarUtil.ShortSnackbar(tv_order_info_orderState, jsonObject.getString("reason"), SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                                        } else {
                                            SnackbarUtil.ShortSnackbar(tv_order_info_orderState, "评价成功！", SnackbarUtil.Confirm).setActionTextColor(Color.WHITE).show();
                                            getEvalList();
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        } else {
                            SnackbarUtil.ShortSnackbar(tv_order_info_orderState, "内容不能为空！", SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                        }
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
            case R.id.action_refresh:
                try {
                    ll_order_info_orderImg.removeAllViews(); // 清空已加载的图片
                } catch (Exception e) {}
                gohttp(); // 重新调用网络请求
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        immersionBar.destroy(); //必须调用该方法，防止内存泄漏
        super.onDestroy();
    }
}
