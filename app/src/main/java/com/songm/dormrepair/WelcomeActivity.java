package com.songm.dormrepair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        waitTime();
    }

    private void waitTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg;
                int i = 3;
                while (i > 0) {
                    msg = new Message();
                    msg.obj = "等待" + i + "秒";
                    handler.sendMessage(msg);
                    SystemClock.sleep(1000);
                    i--;
                }
                forward();
            }
        }).start();
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ((TextView) findViewById(R.id.tv_welcome_waitTime)).setText((String) msg.obj);
        }
    };

    // 取出登录信息，并判断跳转
    private void forward() {
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        String name = sp.getString("name", null);
        String id = sp.getString("id", null);
        String phone = sp.getString("phone", null);
        String room = sp.getString("room", null);
        if (name != null && id != null && phone != null && room != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        finish(); // 不允许下个页面返回此页面
    }
}
