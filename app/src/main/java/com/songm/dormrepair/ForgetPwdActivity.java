package com.songm.dormrepair;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.songm.dormrepair.utils.SnackbarUtil;
import com.songm.dormrepair.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPwdActivity extends AppCompatActivity {

    private ImmersionBar immersionBar;
    private EditText et_forget_pwd_phone, et_forget_pwd_phoneCode, et_forget_pwd_password, et_forget_pwd_password1;
    private boolean isStu = true; // 是否为学生
    private EventHandler eventHandler;
    private Button btn_forget_pwd_sendPhoneCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
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
        et_forget_pwd_phone = (EditText) findViewById(R.id.et_forget_pwd_phone);
        et_forget_pwd_phoneCode = (EditText) findViewById(R.id.et_forget_pwd_phoneCode);
        et_forget_pwd_password = (EditText) findViewById(R.id.et_forget_pwd_password);
        et_forget_pwd_password1 = (EditText) findViewById(R.id.et_forget_pwd_password1);
        btn_forget_pwd_sendPhoneCode = (Button) findViewById(R.id.btn_forget_pwd_sendPhoneCode);

        // 学生或宿管单选监听
        ((RadioGroup) findViewById(R.id.rg_forget_pwd_stuOrHmr)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbt_forget_pwd_stu) {
                    isStu = true;
                } else if(checkedId == R.id.rbt_forget_pwd_hmr) {
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
                                Toast.makeText(getApplicationContext(), jsonObject.getString("detail"), Toast.LENGTH_SHORT).show();
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
                                updatePwdCheck();
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
    public void click_forget_pwd_sendPhoneCode(View view) {
        // 验证手机号码
        String phone = et_forget_pwd_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone) || phone.length() != 11) {
            et_forget_pwd_phone.setError("手机号码格式不对！");
            et_forget_pwd_phone.requestFocus();
            return;
        }
        SMSSDK.getVerificationCode("86", phone); // 发送验证码
    }

    // 重置密码
    public void click_forget_pwd(View view) {
        // 验证输入的参数
        String phone = et_forget_pwd_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone) || phone.length() != 11) {
            et_forget_pwd_phone.setError("手机号码格式不对！");
            et_forget_pwd_phone.requestFocus();
            return;
        }

        String phoneCode = et_forget_pwd_phoneCode.getText().toString().trim();
        if(TextUtils.isEmpty(phoneCode)) {
            et_forget_pwd_phoneCode.setError("验证码不能为空！");
            et_forget_pwd_phoneCode.requestFocus();
            return;
        }

        String password = et_forget_pwd_password.getText().toString().trim();
        if(TextUtils.isEmpty(password)) {
            et_forget_pwd_password.setError("此字段不能为空！");
            et_forget_pwd_password.requestFocus();
            return;
        }

        String password1 = et_forget_pwd_password1.getText().toString().trim();
        if(!password.equals(password1)) {
            et_forget_pwd_password1.setError("两次密码不一致！");
            et_forget_pwd_password1.requestFocus();
            return;
        }

        // 验证验证码是否正确
        SMSSDK.submitVerificationCode("86", phone, phoneCode); // 验证输入的验证码
    }

    // 重置密码验证
    public void updatePwdCheck() {
        String url = UrlUtils.stuUpdatePwd;
        if(!isStu) {
            url = UrlUtils.hmrUpdatePwd;
        }
        String phone = et_forget_pwd_phone.getText().toString().trim();
        String newPwd = et_forget_pwd_password.getText().toString().trim();
        final ProgressDialog dialog = new ProgressDialog(this); // 加载框
        dialog.setTitle("重置中...");
        OkGo.<String>post(url)
                .tag(this)
                .params("phone", phone)
                .params("newPwd", newPwd)
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String update_state = jsonObject.getString("update");
                            if(update_state.equals("error")) {
                                SnackbarUtil.ShortSnackbar(btn_forget_pwd_sendPhoneCode, jsonObject.getString("reason"), SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                            } else {
                                ts("重置密码成功！");
                                finish();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    // 按钮倒计时
    private void sendBtnWait() {
        btn_forget_pwd_sendPhoneCode.setClickable(false); // 发送短信并屏蔽按钮
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
        btn_forget_pwd_sendPhoneCode.setClickable(true); // 发送短信按钮恢复
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            btn_forget_pwd_sendPhoneCode.setText((String) msg.obj);
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
