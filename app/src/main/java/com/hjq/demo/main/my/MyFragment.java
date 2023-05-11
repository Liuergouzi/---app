package com.hjq.demo.main.my;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.navigation.NavigationView;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.comfirm.ComfirmActivity;
import com.hjq.demo.comfirm.PersonActivity;
import com.hjq.demo.http.api.PersonalSelectApi;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.login.LoginActivity;
import com.hjq.demo.login.PasswordResetActivity;
import com.hjq.demo.main.MainActivity;
import com.hjq.demo.overall.Cache;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MyFragment extends TitleBarFragment<MainActivity> {
    RecyclerView rv;
    List<String> mStrings = new ArrayList<>();

    private DrawerLayout drawerLayout;//滑动菜单

    private TextView my_fragment_name_text;

    private ImageView my_fragment_confirm, my_fragment_head_icon;

    private NavigationView navigationview;

    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.my_fragment;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initData() {

        getUserData();

        String isAuthentication = DataMessage.temp_read("isAuthentication");
        if (Objects.equals(isAuthentication, "true")) {
            my_fragment_confirm.setBackgroundResource(R.drawable.ic_confirm_yes);
        }

        navigationview.setNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.navigation_1:
                            new MyFragmentDrawer(this, getContext(), PasswordResetActivity.class);
                            break;
                        case R.id.navigation_2:
                            startActivity(PersonActivity.class);
                            break;
                        case R.id.navigation_3:
                            new MyFragmentDrawer(this, getContext(), "pay_one");
                            break;
                        case R.id.navigation_4:
                            new MyFragmentDrawer(this, getContext(), "pay_two");
                            break;
                        case R.id.navigation_5:
                            new MyFragmentDrawer(getContext(), ComfirmActivity.class, PersonActivity.class).goActivity();
                            break;
                        case R.id.navigation_6:
                            new MyFragmentDrawer.MainAsyncTaskCache(getContext()).execute();
                            break;
                        case R.id.navigation_7:
                            new MyFragmentDrawer(this,getContext()).getVersion();
                            break;
                        case R.id.navigation_8:
                            new MyFragmentDrawer(this,getContext()).logout();
                            DataMessage.temp_save(null,"account");
                            DataMessage.temp_save(null,"isPhone");
                            DataMessage.temp_save(null,"isAuthentication");
                            DataMessage.temp_save(null,"phone");
                            DataMessage.temp_save(null,"share");
                            Cache.cacheToken(getContext(),null);
                            startActivity(LoginActivity.class);
                            finish();
                            break;
                    }
                    return false;
                }
        );
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initView() {
        getAttachActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        ViewPager mViewPager = findViewById(R.id.my_pager);
        FragmentPagerAdapter<AppFragment<?>> mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(MyFragmentPicture.newInstance(), String.valueOf(R.string.home_left_tap));
        mViewPager.setAdapter(mPagerAdapter);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new CardLayoutManager());
        mStrings.clear();
        for (int i = 1; i < 3; i++) {
            mStrings.add(String.valueOf(i));
        }
        CardAdapter myAdapter = new CardAdapter(this.getActivity(), mStrings);
        rv.setAdapter(myAdapter);
        ItemTouchHelperCallback itemTouchHelperCallback = new ItemTouchHelperCallback(mStrings, myAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        my_fragment_head_icon = findViewById(R.id.my_fragment_head_icon);
        my_fragment_name_text = findViewById(R.id.my_fragment_name_text);
        my_fragment_confirm = findViewById(R.id.my_fragment_confirm);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationview = findViewById(R.id.navigation_view);

        setOnClickListener(R.id.toolbar, R.id.my_fragment_edit);

    }


    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.toolbar) {
            drawerLayout.openDrawer(GravityCompat.END);
        } else if (viewId == R.id.my_fragment_edit) {
            startActivity(MyPersonalEditActivity.class);
        }
    }

    /**
     * 获取数据
     */
    public void getUserData() {
        String account = DataMessage.temp_read("account");
        try {
            EasyHttp.post(this)
                    .api(new PersonalSelectApi()
                            .setAccount(account)
                            .setSgin(new SecureSgin().md5(account))
                    )
                    .request(new HttpCallback<JSONObject>(this) {
                        @Override
                        public void onSucceed(JSONObject data) {
                            try {
                                JSONObject jsonObject = new JSONObject(String.valueOf(data.get("data")));
                                GlideApp.with(getAttachActivity())
                                        .load(jsonObject.getString("head"))
                                        .placeholder(R.drawable.ic_boy)
                                        .error(R.drawable.ic_boy)
                                        .skipMemoryCache(true) // 不使用内存缓存
                                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                                        .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                                        .into(my_fragment_head_icon);
                                my_fragment_name_text.setText(jsonObject.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
