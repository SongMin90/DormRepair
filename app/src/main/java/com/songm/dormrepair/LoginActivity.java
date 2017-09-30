package com.songm.dormrepair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.songm.dormrepair.model.login.User;
import com.songm.dormrepair.utils.SnackbarUtil;
import com.songm.dormrepair.utils.UrlUtils;

import org.json.JSONObject;

/**
 * 登录
 */
public class LoginActivity extends AppCompatActivity {

    private ImmersionBar immersionBar;
    // 控件
    private AutoCompleteTextView et_login_idOrPhone; // 学号/工号/手机号
    private EditText et_login_password; // 密码
    private RadioGroup rg_login_stuOrHmr; // 学生或宿管单选
    private boolean isStu = true; // 是否是学生

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.colorPrimary); // 状态栏颜色，不写默认透明色
        immersionBar.init();

        rg_login_stuOrHmr = (RadioGroup) findViewById(R.id.rg_login_stuOrHmr);
        et_login_idOrPhone = (AutoCompleteTextView) findViewById(R.id.et_login_idOrPhone);
        et_login_password = (EditText) findViewById(R.id.et_login_password);

        // 判断是学生还是宿管
        rg_login_stuOrHmr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbt_login_hmr) {
                    isStu = false;
                } else if(checkedId == R.id.rbt_login_stu) {
                    isStu = true;
                }
            }
        });
    }

    // 登录
    public void click_login(View view) {
        // 重置错误
        et_login_idOrPhone.setError(null);
        et_login_password.setError(null);

        // 类型强转
        final String user_idOrPhone = et_login_idOrPhone.getText().toString().trim();
        String user_password = et_login_password.getText().toString().trim();

        // 验证输入是否有空
        if (TextUtils.isEmpty(user_idOrPhone)) {
            et_login_idOrPhone.setError("此字段不能为空！");
            et_login_idOrPhone.requestFocus();
        } else if (TextUtils.isEmpty(user_password)) {
            et_login_password.setError("此字段不能为空！");
            et_login_password.requestFocus();
        } else {
            // 登录验证，网络请求
            String url = UrlUtils.stuLogin;
            if(!isStu) {
                url = UrlUtils.hmrLogin;
            }
            PostRequest<String> tag = OkGo.<String>post(url).tag(this);
            // 添加请求参数
            if(user_idOrPhone.length() == 11) {
                if(isStu) {
                    tag.params("stuPhone", user_idOrPhone);
                } else {
                    tag.params("hmrPhone", user_idOrPhone);
                }
            } else {
                if(isStu) {
                    tag.params("stuId", user_idOrPhone);
                } else {
                    tag.params("hmrId", user_idOrPhone);
                }
            }
            if(isStu) {
                tag.params("stuPwd", user_password);
            } else {
                tag.params("hmrPwd", user_password);
            }
            // 解析json
            final ProgressDialog dialog = new ProgressDialog(this); // 加载框
            dialog.setTitle("登录中...");
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
                        // 判断登录状态
                        String login_state = jsonObject.getString("login");
                        if(login_state.equals("error")) {
                            SnackbarUtil.ShortSnackbar(rg_login_stuOrHmr, jsonObject.getString("reason"), SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                        } else {
                            ts("登录成功！");
                            findFullInfo(user_idOrPhone);
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    // 登录成功获取学生或宿管详细信息（如寝室号等等），并将信息存入SharedPreferences
    private void findFullInfo(String user_idOrPhone) {
        String url = UrlUtils.stuInfoByStuIdOrPhone(user_idOrPhone);
        if(!isStu) {
            url = UrlUtils.hmrInfoByHmrIdOrPhone(user_idOrPhone);
        }
        final ProgressDialog dialog = new ProgressDialog(this); // 加载框
        dialog.setTitle("获取信息中...");
        OkGo.<String>get(url)
                .tag(this)
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
                        analysis(response.body());
                    }
                });
    }

    // 解析json
    private void analysis(String json) {
        User user = new Gson().fromJson(json, User.class);
        if(user.getFind().equals("success")) {
            //将登录数据存入sp
            SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("name", user.getName());
            edit.putString("id", user.getId());
            edit.putString("phone", user.getPhone());
            edit.putString("room", user.getRoom());
            edit.putBoolean("stuOrHmr", isStu);
            edit.commit();
            //跳转至主界面
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            SnackbarUtil.ShortSnackbar(rg_login_stuOrHmr, "获取信息失败！", SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
        }
    }

    // 注册
    public void click_register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    // 忘记密码？
    public void click_forgetPwd(View view) {
        startActivity(new Intent(this, ForgetPwdActivity.class));
    }

    // Toast
    private void ts(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        immersionBar.destroy(); //必须调用该方法，防止内存泄漏
        super.onDestroy();
    }

}

