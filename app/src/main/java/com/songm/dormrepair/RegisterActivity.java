package com.songm.dormrepair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by SongM on 2017/9/19.
 */

public class RegisterActivity extends AppCompatActivity {

    private ImmersionBar immersionBar;
    private Button btn_register_sendPhoneCode;
    private AutoCompleteTextView et_register_id;
    private EditText et_register_phone, et_register_password, et_register_password1, et_register_phoneCode;
    private EventHandler eventHandler;
    private boolean isStu = true; // 是否为学生

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        btn_register_sendPhoneCode = (Button) findViewById(R.id.btn_register_sendPhoneCode);
        et_register_phone = (EditText) findViewById(R.id.et_register_phone);
        et_register_id = (AutoCompleteTextView) findViewById(R.id.et_register_id);
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        et_register_password1 = (EditText) findViewById(R.id.et_register_password1);
        et_register_phoneCode = (EditText) findViewById(R.id.et_register_phoneCode);

        // 学生或宿管单选监听
        ((RadioGroup) findViewById(R.id.rg_register_stuOrHmr)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbt_register_stu) {
                    isStu = true;
                } else if(checkedId == R.id.rbt_register_hmr) {
                    isStu = false;
                }
            }
        });

        // SMS监听
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                System.out.println("结束！---" + event + "---" + result + "---" + data.toString());
                if (data instanceof Throwable) {
                    final Throwable throwable = (Throwable)data;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(throwable.getMessage());
                                SnackbarUtil.ShortSnackbar(btn_register_sendPhoneCode, jsonObject.getString("detail"), SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        // 获取验证码成功
                        sendBtnWait(); // 设置按钮状态
                    } else if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        // 发送验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                registerCheck();
                            }
                        });
                    }
                }
            }
        };
        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    // 发送手机验证码
    public void click_register_sendPhoneCode(View view) {
        // 验证手机号码
        String phone = et_register_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone) || phone.length() != 11) {
            et_register_phone.setError("手机号码格式不对！");
            et_register_phone.requestFocus();
            return;
        }
        SMSSDK.getVerificationCode("86", phone); // 发送验证码
    }

    // 注册
    public void click_register(View view) {
        // 验证输入的参数
        String id = et_register_id.getText().toString().trim();
        if(TextUtils.isEmpty(id)) {
            et_register_id.setError("此字段不能为空！");
            et_register_id.requestFocus();
            return;
        }

        String password = et_register_password.getText().toString().trim();
        if(TextUtils.isEmpty(password)) {
            et_register_password.setError("此字段不能为空！");
            et_register_password.requestFocus();
            return;
        }

        String password1 = et_register_password1.getText().toString().trim();
        if(!password.equals(password1)) {
            et_register_password1.setError("两次密码不一致！");
            et_register_password1.requestFocus();
            return;
        }

        String phone = et_register_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone) || phone.length() != 11) {
            et_register_phone.setError("手机号码格式不对！");
            et_register_phone.requestFocus();
            return;
        }

        String phoneCode = et_register_phoneCode.getText().toString().trim();
        if(TextUtils.isEmpty(phoneCode)) {
            et_register_phoneCode.setError("验证码不能为空！");
            et_register_phoneCode.requestFocus();
            return;
        }

        // 验证验证码是否正确
        SMSSDK.submitVerificationCode("86", phone, phoneCode); // 验证输入的验证码
        //registerCheck();
    }

    // 注册验证
    private void registerCheck() {
        String url = UrlUtils.stuRegister;
        if(!isStu) {
            url = UrlUtils.hmrRegister;
        }
        // 网络请求
        PostRequest<String> tag = OkGo.<String>post(url).tag(this);
        // 设置请求参数
        final String id = et_register_id.getText().toString().trim();
        String password = et_register_password.getText().toString().trim();
        String phone = et_register_phone.getText().toString().trim();
        if(isStu) {
            tag.params("stuId", id);
            tag.params("stuPwd", password);
            tag.params("stuPhone", phone);
        } else {
            tag.params("hmrId", id);
            tag.params("hmrPwd", password);
            tag.params("hmrPhone", phone);
        }
        // 解析服务器响应的数据
        final ProgressDialog dialog = new ProgressDialog(this); // 加载框
        dialog.setTitle("注册中...");
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
                    // 获取注册状态
                    String register_state = jsonObject.getString("register");
                    if(register_state.equals("error")) {
                        SnackbarUtil.ShortSnackbar(btn_register_sendPhoneCode, jsonObject.getString("reason"), SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                    } else {
                        ts("注册成功！");
                        getFullInfo(id); // 调用获取详细信息
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    // 注册成功获取详细信息
    private void getFullInfo(String id) {
        String url = UrlUtils.stuInfoByStuIdOrPhone(id);
        if(!isStu) {
            url = UrlUtils.hmrInfoByHmrIdOrPhone(id);
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
            SnackbarUtil.ShortSnackbar(btn_register_sendPhoneCode, "获取信息失败！", SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
        }
    }

    // 按钮倒计时
    private void sendBtnWait() {
        btn_register_sendPhoneCode.setClickable(false); // 发送短信并屏蔽按钮
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 60; // 设置60秒计时
                Message msg = null; // 设置消息
                while (i > 0) {
                    msg = Message.obtain();
                    msg.obj = i + "秒后再次发送";
                    handler.sendMessage(msg);
                    SystemClock.sleep(1000);
                    i--;
                }
                Message message = Message.obtain();
                message.obj = "发送验证码";
                handler.sendMessage(message);
            }
        }).start();
        btn_register_sendPhoneCode.setClickable(true); // 发送短信按钮恢复
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            btn_register_sendPhoneCode.setText((String) msg.obj);
        }
    };

    // Toast
    private void ts(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
        super.onDestroy();
        immersionBar.destroy(); //必须调用该方法，防止内存泄漏
        SMSSDK.unregisterEventHandler(eventHandler);
    }

}
