package com.songm.dormrepair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.songm.dormrepair.fragment.HomeFragment;
import com.songm.dormrepair.fragment.MyOrderFragment;
import com.songm.dormrepair.fragment.NewOrderFragment;
import com.songm.dormrepair.utils.UserIconUtils;

/**
 * 主页面
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImmersionBar immersionBar;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private Toolbar toolbar;
    private NewOrderFragment newOrderFragment;
    private MyOrderFragment myOrderFragment;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.colorPrimary); // 状态栏颜色，不写默认透明色
        immersionBar.init();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 设置用户名字
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_userName)).setText(getSharedPreferences("info", MODE_PRIVATE).getString("name", ""));
        // 设置用户头像
        ImageView iv_nav_header_main_userIcon = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_nav_header_main_userIcon);
        UserIconUtils.show(getApplicationContext(), iv_nav_header_main_userIcon);

        // 默认设置首页
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        newOrderFragment = new NewOrderFragment();
        myOrderFragment = new MyOrderFragment();
        fragmentManager.beginTransaction().replace(R.id.fl_main, homeFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                myOrderFragment.revertPager(); // 将我的报修列表的intem设为首条
                toolbar.setTitle("首页");
                fragmentManager.beginTransaction().replace(R.id.fl_main, homeFragment).commit();
                drawer.closeDrawer(navigationView);
                break;
            case R.id.nav_neworder:
                myOrderFragment.revertPager(); // 将我的报修列表的intem设为首条
                toolbar.setTitle("新增报修");
                fragmentManager.beginTransaction().replace(R.id.fl_main, newOrderFragment).commit();
                drawer.closeDrawer(navigationView);
                break;
            case R.id.nav_myorder:
//                (bug已修复)
                toolbar.setTitle("我的报修");
                fragmentManager.beginTransaction().replace(R.id.fl_main, myOrderFragment).commit();
                drawer.closeDrawer(navigationView);
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 用户详细信息
    public void click_userInfo(View view) {
        startActivity(new Intent(this, UserInfoActivity.class));
    }

    // 点击两次返回键退出
    private long mExitTime; // 退出时的时间
    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onRestart() {
        // 设置用户头像
        ImageView iv_nav_header_main_userIcon = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_nav_header_main_userIcon);
        UserIconUtils.show(getApplicationContext(), iv_nav_header_main_userIcon);
        super.onRestart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        immersionBar.destroy(); //必须调用该方法，防止内存泄漏
        super.onDestroy();
    }
}
