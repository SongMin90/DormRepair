package com.songm.dormrepair;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gyf.barlibrary.ImmersionBar;
import com.songm.dormrepair.utils.CommandExecutionUtils;
import com.wenhuaijun.easyimageloader.imageLoader.ImageDiskLrucache;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ImmersionBar immersionBar;
    private ListView lv_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.colorPrimary); // 状态栏颜色，不写默认透明色
        immersionBar.init();

        // 标题返回
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 绑定控件
        lv_setting = (ListView) findViewById(R.id.lv_setting);
        // 设置监听
        lv_setting.setOnItemClickListener(this);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).toString().equals("清除缓存")) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("是否清除缓存？")
                    .setConfirmText("立即清除")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // 获取缓存路径
                            String absolutePath = new ImageDiskLrucache(getApplicationContext()).getDidkCacheDir(getApplicationContext(), "bitmap").getAbsolutePath();
                            // 执行shell命令，清空缓存目录
                            CommandExecutionUtils.execCommand("rm -rf " + absolutePath + "/*", false);
                            // ok
                            sDialog.setTitleText("清除成功！")
                                    .setContentText("APP所有图片的缓存清理成功")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .show();
        }
    }
}
