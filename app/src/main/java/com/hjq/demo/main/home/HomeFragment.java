package com.hjq.demo.main.home;


import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.main.MainActivity;
import com.hjq.demo.widget.XCollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends TitleBarFragment<MainActivity>
        implements HomeTabAdapter.OnTabListener, ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {

    private XCollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mAddressView;
    private TextView mHintView;
    private AppCompatImageView mSearchView;
    private ViewPager mViewPager;
    private HomeTabAdapter mTabAdapter;

    private FrameLayout mBanana;
    private ViewPager mViewPaper;
    private List<ImageView> images;
    //下方指示点
    private List<View> dots;
    private int currentItem;
    //记录上一次点的位置
    private int oldPosition = 0;
    //存放图片的id
    private String[] imageIds;
    //存放图片的标题，可以设为空
    private final String[] titles = new String[]{"1", "2", "3"};
    private TextView title;
    //定时调度机制
    private ScheduledExecutorService scheduledExecutorService;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initView() {
        mBanana =findViewById(R.id.host_banana);
        mCollapsingToolbarLayout = findViewById(R.id.host_bar);
        Toolbar mToolbar = findViewById(R.id.host_title);
        mAddressView = findViewById(R.id.host_address);
        mHintView = findViewById(R.id.host_hint);
        mSearchView = findViewById(R.id.host_search);
        RecyclerView mTabView = findViewById(R.id.host_tab);
        mViewPager = findViewById(R.id.host_pager);
        FragmentPagerAdapter<AppFragment<?>> mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(HomeFragmentPicture.newInstance(), String.valueOf(R.string.home_left_tap));
        mPagerAdapter.addFragment(HomeFragmentPicture.newInstance(), String.valueOf(R.string.home_left_tap));
//        mPagerAdapter.addFragment(BrowserFragment.newInstance("https://github.com/getActivity"), "视频");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mTabAdapter = new HomeTabAdapter(getAttachActivity());
        mTabView.setAdapter(mTabAdapter);
        // 给这个 ToolBar 设置顶部内边距，才能和 TitleBar 进行对齐
        ImmersionBar.setTitleBar(getAttachActivity(), mToolbar);
        //设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);
        imageIds = new String[]{"https://47image.oss-cn-heyuan.aliyuncs.com/a.png", "", "https://47image.oss-cn-heyuan.aliyuncs.com/c.png"};
        setView();
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem("图文");
        mTabAdapter.addItem("视频");
        mTabAdapter.setOnTabListener(this);
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public boolean isStatusBarDarkFont() {
        return mCollapsingToolbarLayout.isScrimsShown();
    }

    /**
     * {@link HomeTabAdapter.OnTabListener}
     */

    @Override
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * CollapsingToolbarLayout 渐变回调
     * <p>
     * {@link XCollapsingToolbarLayout.OnScrimsListener}
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {
        mBanana.setVisibility(shown ? View.GONE : View.VISIBLE);
        getStatusBarConfig().statusBarDarkFont(shown).init();
        mAddressView.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black : R.color.white));
        mHintView.setBackgroundResource(shown ? R.drawable.bg_home_search_bar_gray : R.drawable.bg_home_search_bar_transparent);
        mHintView.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black60 : R.color.white60));
        mSearchView.setSupportImageTintList(ColorStateList.valueOf(getColor(shown ? R.color.common_icon_color : R.color.white)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }

    //UI界面的更新
    private void setView() {
        mViewPaper = findViewById(R.id.viewpager);
        //显示的图片
        images = new ArrayList<>();
        for (String imageId : imageIds) {
            ImageView imageView = new ImageView(getContext());
            GlideApp.with(this)
                    .load(imageId)
                    .transform(new RoundedCorners((int) getResources().getDimension(R.dimen.dp_10)))
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(imageView);
            images.add(imageView);
        }
        //显示的小点
        dots = new ArrayList<>();
        dots.add(findViewById(R.id.dot_0));
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));

        title = findViewById(R.id.title);
        title.setText(titles[0]);

        ViewPagerAdapter adapter = new ViewPagerAdapter();
        mViewPaper.setAdapter(adapter);

        mViewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                title.setText(titles[position]);
                dots.get(position).setBackgroundResource(R.drawable.guide_indicator_selected);
                dots.get(oldPosition).setBackgroundResource(R.drawable.guide_indicator_unselected);
                oldPosition = position;
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });
    }

    /*定义的适配器*/
    public class ViewPagerAdapter extends PagerAdapter {
        //返回当前有效视图的个数。
        @Override
        public int getCount() {
            return images.size();
        }

        //判断instantiateItem函数所返回来的Key与一个页面视图是否是代表的同一个视图
        @Override
        public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, @NonNull Object object) {
            view.removeView(images.get(position));
        }

        //创建指定位置的页面视图，也就是将一张图片放到容器中的指定位置
        @NonNull
        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(images.get(position));
            return images.get(position);
        }

    }

    /**
     * 利用线程池定时执行动画轮播
     */
    @Override
    public void onStart() {
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //每隔2s时间，固定执行轮播任务。
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPageTask(), 2, 2, TimeUnit.SECONDS);
    }

    /**
     * 图片轮播任务,发送轮播消息
     */
    private class ViewPageTask implements Runnable {
        @Override
        public void run() {
            currentItem = (currentItem + 1) % imageIds.length;
            mHandler.sendEmptyMessage(0);   //发送轮播消息
        }
    }

    /**
     * 接收子线程传递过来的数据
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            //这里不是具体的数据，而是一个轮播信号，目的是切换下一张图片
            mViewPaper.setCurrentItem(currentItem);
        }
    };

    //当切换到其他界面时，关闭后台轮播
    @Override
    public void onStop() {
        super.onStop();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }


}





