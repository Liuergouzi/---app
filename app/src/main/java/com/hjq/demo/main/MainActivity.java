package com.hjq.demo.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.app.DoubleClickHelper;
import com.hjq.demo.main.find.FindFragment;
import com.hjq.demo.main.home.HomeFragment;
import com.hjq.demo.main.my.MyFragment;
import com.hjq.demo.main.notice.NoticeFragment;
import com.hjq.demo.main.put.PutFragment;
import com.hjq.demo.manager.ActivityManager;
import com.hjq.demo.notify.NotificationService;

/**
 * author : Android 轮子哥
 * 
 * time   : 2018/10/18
 * desc   : 首页界面
 */
public final class MainActivity extends AppActivity implements MainAdapter.OnNavigationListener {

    private static final String INTENT_KEY_IN_FRAGMENT_INDEX = "fragmentIndex";
    private static final String INTENT_KEY_IN_FRAGMENT_CLASS = "fragmentClass";

    private ViewPager mViewPager;
    private RecyclerView mNavigationView;

    private MainAdapter mNavigationAdapter;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    public static void start(Context context) {
        start(context, HomeFragment.class);
    }

    public static void start(Context context, Class<? extends AppFragment<?>> fragmentClass) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(INTENT_KEY_IN_FRAGMENT_CLASS, fragmentClass);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.main_pager);
        mNavigationView = findViewById(R.id.main_navigation);

        mNavigationAdapter = new MainAdapter(this);
        mNavigationAdapter.addItem(new MainAdapter.MenuItem(getString(R.string.home_nav_home),
                ContextCompat.getDrawable(this, R.drawable.ic_main_home_selector)));
        mNavigationAdapter.addItem(new MainAdapter.MenuItem(getString(R.string.home_nav_find),
                ContextCompat.getDrawable(this, R.drawable.ic_main_find_selector)));
        mNavigationAdapter.addItem(new MainAdapter.MenuItem(getString(R.string.home_nav_put),
                ContextCompat.getDrawable(this, R.drawable.ic_main_put_selector)));
        mNavigationAdapter.addItem(new MainAdapter.MenuItem(getString(R.string.home_nav_notice),
                ContextCompat.getDrawable(this, R.drawable.ic_main_notice_selector)));
        mNavigationAdapter.addItem(new MainAdapter.MenuItem(getString(R.string.home_nav_my),
                ContextCompat.getDrawable(this, R.drawable.ic_main_my_selector)));
        mNavigationAdapter.setOnNavigationListener(this);
        mNavigationView.setAdapter(mNavigationAdapter);
    }

    @Override
    protected void initData() {
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(HomeFragment.newInstance());
        mPagerAdapter.addFragment(FindFragment.newInstance());
        mPagerAdapter.addFragment(PutFragment.newInstance());
        mPagerAdapter.addFragment(NoticeFragment.newInstance());
        mPagerAdapter.addFragment(MyFragment.newInstance());
        mViewPager.setAdapter(mPagerAdapter);
        onNewIntent(getIntent());
        Intent intent = new Intent(this.getContext(), NotificationService.class);
        startService(intent); //启动监听消息推送的服务
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switchFragment(mPagerAdapter.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前 Fragment 索引位置
        outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, mViewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX));
    }

    private void switchFragment(int fragmentIndex) {
        if (fragmentIndex == -1) {
            return;
        }

        switch (fragmentIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                mViewPager.setCurrentItem(fragmentIndex);
                mNavigationAdapter.setSelectedPosition(fragmentIndex);
                break;
            default:
                break;
        }
    }

    /**
     * {@link MainAdapter.OnNavigationListener}
     */

    @Override
    public boolean onNavigationItemSelected(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                mViewPager.setCurrentItem(position);
                return true;
            default:
                return false;
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            toast(R.string.home_exit_hint);
            return;
        }

        // 移动到上一个任务栈，避免侧滑引起的不良反应
        moveTaskToBack(false);
        postDelayed(() -> {
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities();
            // 销毁进程（注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
            // System.exit(0);
        }, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mNavigationView.setAdapter(null);
        mNavigationAdapter.setOnNavigationListener(null);
    }



}