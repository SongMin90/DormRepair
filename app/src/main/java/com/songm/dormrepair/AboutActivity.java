package com.songm.dormrepair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.songm.dormrepair.model.about.App;
import com.songm.dormrepair.utils.SnackbarUtil;
import com.songm.dormrepair.utils.UrlUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AboutActivity extends AppCompatActivity {

    private ImmersionBar immersionBar;
    private TextView tv_about_appVersion;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.colorPrimary); // 状态栏颜色，不写默认透明色
        immersionBar.init();

        // 标题返回
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tv_about_appVersion = (TextView) findViewById(R.id.tv_about_appVersion);

        // 获取app版本号
        try {
            PackageManager pm = this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            String versionName = pi.versionName;
            int versioncode = pi.versionCode;
            tv_about_appVersion.setText("当前版本\n   " + versioncode + " - " + versionName);
        } catch (Exception e) {

        }
    }

    // 检查更新
    public void click_about_appUpdate(View view) {
        final ProgressDialog dialog = new ProgressDialog(this); // 加载框
        dialog.setTitle("检查中...");
        OkGo.<String>get(UrlUtils.appBaoxiu)
                .tag(getApplicationContext())
                .execute(new StringCallback() {
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
                        App app = new Gson().fromJson(response.body(), App.class);
                        PackageManager pm = getApplicationContext().getPackageManager();
                        try {
                            String versionName = pm.getPackageInfo(getPackageName(), 0).versionName;
                            double v = Double.parseDouble(versionName); // 当前版本
                            double version = app.getVersion(); // 最新版本
                            if(version > v) {
                                update(app.getUpdateInfo());
                            } else {
                                SnackbarUtil.ShortSnackbar(tv_about_appVersion, "已经是最新版本！", SnackbarUtil.Confirm).setActionTextColor(Color.WHITE).show();
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 更新APP
    private void update(String updateInfo) {
        sweetAlertDialog =  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("是否更新?")
                .setContentText(updateInfo)
                .setCancelText("否")
                .setConfirmText("是")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri parse = Uri.parse(UrlUtils.HOST + "apk/Baoxiu.apk");
                        intent.setData(parse);
                        startActivity(intent);
                    }
                });
        sweetAlertDialog.show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
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
