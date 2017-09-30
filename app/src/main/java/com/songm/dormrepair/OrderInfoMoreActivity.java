package com.songm.dormrepair;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.songm.dormrepair.model.orderinfo.Author;
import com.songm.dormrepair.model.orderinfo.Repairer;
import com.songm.dormrepair.utils.UrlUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by SongM on 2017/9/16.
 * 报修订单创建人及维修员信息
 */

public class OrderInfoMoreActivity extends AppCompatActivity {

    private ImmersionBar immersionBar;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info_more);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.colorPrimary); // 状态栏颜色，不写默认透明色
        immersionBar.init();

        // 标题返回
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 取到创建人及维修员ID
        Intent intent = getIntent();
        String authorId = intent.getStringExtra("authorId");
        if(authorId.indexOf(".") > 0) { // 去除小数点后的数
            authorId = authorId.substring(0, authorId.indexOf("."));
        }
        String repairerId = intent.getStringExtra("repairerId");
        boolean isStu = intent.getBooleanExtra("isStu", true);

        // 取到创建人信息
        OkGo.<String>get(UrlUtils.authorFindByAuthorId(isStu, authorId)).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Author author = new Gson().fromJson(response.body(), Author.class);
                if (author.getFind().equals("success")) {
                    ((TextView) findViewById(R.id.order_info_more_authorName)).setText(author.getName());
                    ((TextView) findViewById(R.id.order_info_more_authorPhone)).setText(author.getPhone());
                }
            }
        });

        // 取到维修员信息
        if (repairerId != null) {
            // 将字符串转为整形
            double id = Double.parseDouble(repairerId);
            int idOrPhone = (int) id;
            OkGo.<String>get(UrlUtils.repairerFindByRepairerId(idOrPhone)).tag(this).execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    Repairer repairer = new Gson().fromJson(response.body(), Repairer.class);
                    if (repairer.getFind().equals("success")) {
                        ((TextView) findViewById(R.id.order_info_more_repairerName)).setText(repairer.getRepairerName());
                        ((TextView) findViewById(R.id.order_info_more_repairerPhone)).setText(repairer.getRepairerPhone());
                    }
                }
            });
        }
    }

    // 点击号码拨号
    public void callphone(View view) {
        final TextView tv = (TextView) view;
        if (!tv.getText().equals("无")) {
            sweetAlertDialog =  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("是否拨打此号码?")
                    .setCancelText("否")
                    .setConfirmText("是")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Uri uri = Uri.parse("tel:" + tv.getText()); //设置要操作的路径
                            Intent it = new Intent();
                            it.setAction(Intent.ACTION_DIAL);  //设置要操作的Action
                            it.setData(uri); //要设置的数据
                            startActivity(it);    //执行跳转
                        }
                    });
            sweetAlertDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            sweetAlertDialog.dismiss();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        immersionBar.destroy(); //必须调用该方法，防止内存泄漏
        super.onDestroy();
    }
}
