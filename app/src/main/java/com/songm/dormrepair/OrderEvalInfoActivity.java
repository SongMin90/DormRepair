package com.songm.dormrepair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

/**
 * Created by SongM on 2017/9/17.
 * 评价详细信息
 */

public class OrderEvalInfoActivity extends AppCompatActivity {

    private ImmersionBar immersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_eval_info);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.colorPrimary); // 状态栏颜色，不写默认透明色
        immersionBar.init();

        // 标题返回
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String evalName = intent.getStringExtra("evalName");
        String evalContent = intent.getStringExtra("evalContent");

        ((TextView) findViewById(R.id.tv_order_eval_info_evalName)).setText(evalName);
        ((TextView) findViewById(R.id.tv_order_eval_info_evalContent)).setText(evalContent);
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
