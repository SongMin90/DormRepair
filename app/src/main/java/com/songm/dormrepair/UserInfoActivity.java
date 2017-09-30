package com.songm.dormrepair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nanchen.compresshelper.CompressHelper;
import com.songm.dormrepair.utils.SnackbarUtil;
import com.songm.dormrepair.utils.UrlUtils;
import com.songm.dormrepair.utils.UserIconUtils;

import org.json.JSONObject;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 用户信息页面
 */
public class UserInfoActivity extends AppCompatActivity {

    private ImmersionBar immersionBar;
    private ImageView iv_user_info_userIcon;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.colorPrimary); // 状态栏颜色，不写默认透明色
        immersionBar.init();

        // 标题返回
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 获取用户信息
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        boolean stuOrHmr = sp.getBoolean("stuOrHmr", true);
        if(stuOrHmr) {
            ((TextView) findViewById(R.id.tv_user_info_userId)).setText("学号：" + sp.getString("id", ""));
        } else {
            ((TextView) findViewById(R.id.tv_user_info_userId)).setText("工号：" + sp.getString("id", ""));
        }
        ((TextView) findViewById(R.id.tv_user_info_userName)).setText("姓名：" + sp.getString("name", ""));
        ((TextView) findViewById(R.id.tv_user_info_userPhone)).setText("手机号码：" + sp.getString("phone", ""));
        ((TextView) findViewById(R.id.tv_user_info_userRoom)).setText("楼层和寝室：" + sp.getString("room", ""));

        // 设置用户头像
        iv_user_info_userIcon = (ImageView) findViewById(R.id.iv_user_info_userIcon);
        UserIconUtils.show(getApplicationContext(), iv_user_info_userIcon);
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

    // 退出登录
    public void click_logout(View view) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("是否退出登录?")
                .setCancelText("否")
                .setConfirmText("是")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // 清空登录信息
                        SharedPreferences sp = getSharedPreferences("info", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("name");
                        edit.remove("id");
                        edit.remove("phone");
                        edit.remove("room");
                        edit.remove("stuOrHmr");
                        edit.commit();
                        // 跳转至登录界面
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "已安全退出！", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    // 用户头像
    public void clicl_userIcon(View view) {
        /* 开启Pictures画面Type设定为image *//* 使用Intent.ACTION_GET_CONTENT这个Action *//* 取得相片后返回本画面 */
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("是否更换头像?")
                .setCancelText("否")
                .setConfirmText("是")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent intent = new Intent();
				        /* 开启Pictures画面Type设定为image */
                        intent.setType("image/*");
				        /* 使用Intent.ACTION_GET_CONTENT这个Action */
                        intent.setAction(Intent.ACTION_PICK);
				        /* 取得相片后返回本画面 */
                        startActivityForResult(intent, 1);
                    }
                });
        sweetAlertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        sweetAlertDialog.dismiss();
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = getRealPathFromURI(uri);
            if(path != null) {
                // 修改头像
                iv_user_info_userIcon.setImageBitmap(BitmapFactory.decodeFile(path));
                // 取出用户信息
                SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                String id = sharedPreferences.getString("id", "");
                boolean stuOrHmr = sharedPreferences.getBoolean("stuOrHmr", true);
                String profession = "stu";
                if(!stuOrHmr) {
                    profession = "hmr";
                }
                // 压缩原图片
                File newFile = CompressHelper.getDefault(getApplicationContext()).compressToFile(new File(path));
                // 上传头像
                OkGo.<String>post(UrlUtils.updateUserIcon)
                        .tag(getApplicationContext())
                        .params("userId", id)
                        .params("profession", profession)
                        .params("icon", newFile)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    String set_state = jsonObject.getString("set");
                                    if(set_state.equals("success")) {
                                        SnackbarUtil.ShortSnackbar(iv_user_info_userIcon, "头像设置成功！", SnackbarUtil.Confirm).setActionTextColor(Color.WHITE).show();
                                    } else {
                                        SnackbarUtil.ShortSnackbar(iv_user_info_userIcon, jsonObject.getString("reason"), SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                                    }
                                } catch (Exception e) {

                                }
                            }
                        });
            } else {
                SnackbarUtil.ShortSnackbar(iv_user_info_userIcon, "无法选取此图片！", SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 取出图片全路径
    private String getRealPathFromURI(Uri contentUri) { // 传入图片uri地址
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
