package com.hjq.demo.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.RecyclerPagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.util.Picture_Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * author : Android 轮子哥
 * 
 * time   : 2019/03/05
 * desc   : 查看大图
 */
public final class ImageLookActivity extends AppActivity
        implements ViewPager.OnPageChangeListener,
        BaseAdapter.OnItemClickListener {

    private static final String INTENT_KEY_IN_IMAGE_LIST = "imageList";
    private static final String INTENT_KEY_IN_IMAGE_INDEX = "imageIndex";
    private static Picture_Util UTIL ;

    public static void start(Context context, String url, Picture_Util Util) {
        ArrayList<String> images = new ArrayList<>(1);
        images.add(url);
        start(context, images, Util);
    }

    public static void start(Context context, List<String> urls, Picture_Util Util) {
        start(context, urls, 0,Util);
    }

    @Log
    public static void start(Context context, List<String> urls, int index, Picture_Util Util) {
        UTIL=Util;
        if (urls == null || urls.isEmpty()) {
            return;
        }
        Intent intent = new Intent(context, ImageLookActivity.class);
        if (urls.size() > 2000) {
            // 请注意：如果传输的数据量过大，会抛出此异常，并且这种异常是不能被捕获的
            // 所以当图片数量过多的时候，我们应当只显示一张，这种一般是手机图片过多导致的
            // 经过测试，传入 3121 张图片集合的时候会抛出此异常，所以保险值应当是 2000
            // android.os.TransactionTooLargeException: data parcel size 521984 bytes
            urls = Collections.singletonList(urls.get(index));
        }

        if (urls instanceof ArrayList) {
            intent.putExtra(INTENT_KEY_IN_IMAGE_LIST, (ArrayList<String>) urls);
        } else {
            intent.putExtra(INTENT_KEY_IN_IMAGE_LIST, new ArrayList<>(urls));
        }
        intent.putExtra(INTENT_KEY_IN_IMAGE_INDEX, index);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private ViewPager mViewPager;
    private ImagePreviewAdapter mAdapter;

    /**
     * 文本指示器
     */
    private TextView mTextIndicatorView;

    @Override
    protected int getLayoutId() {
        return R.layout.image_look_activity;
    }

    ImageView img_head,tx_love_click;
    TextView tx_name, tx_text, tx_love,tx_time;

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_image_look_pager);
        mTextIndicatorView = findViewById(R.id.tv_image_look_indicator);
        img_head = findViewById(R.id.look_head);
        tx_name = findViewById(R.id.look_name);
        tx_love = findViewById(R.id.look_love_text);
        tx_text = findViewById(R.id.look_text);
        tx_time = findViewById(R.id.look_time);
        tx_love_click = findViewById(R.id.look_love);

        GlideApp.with(this)
                .load(UTIL.head)
                .placeholder(R.drawable.ic_boy)
                .error(R.drawable.ic_boy)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(img_head);

        tx_text.setText(UTIL.content);

        if (UTIL!=null)
            tx_name.setText(UTIL.name);
        else
            tx_name.setText("未命名用户");

        tx_time.setText(UTIL.time);
        tx_love.setText(String.valueOf(UTIL.loveCount));

    }

    @Override
    protected void initData() {
        ArrayList<String> images = getStringArrayList(INTENT_KEY_IN_IMAGE_LIST);
        if (images == null || images.isEmpty()) {
            finish();
            return;
        }
        mAdapter = new ImagePreviewAdapter(this);
        mAdapter.setData(images);
        mAdapter.setOnItemClickListener(this);
        mViewPager.setAdapter(new RecyclerPagerAdapter(mAdapter));
        mTextIndicatorView.setVisibility(View.VISIBLE);
        mViewPager.addOnPageChangeListener(this);
        onPageSelected(0);

    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 隐藏状态栏和导航栏
                .hideBar(BarHide.FLAG_HIDE_BAR);
    }

    @Override
    public boolean isStatusBarDarkFont() {
        return false;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onPageSelected(int position) {
        mTextIndicatorView.setText((position + 1) + "/" + mAdapter.getCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(this);
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView RecyclerView 对象
     * @param itemView     被点击的条目对象
     * @param position     被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        // 单击图片退出当前的 Activity
        finish();
    }
}