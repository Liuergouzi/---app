package com.hjq.demo.myitem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.AppConfig;
import com.hjq.demo.dialog.ShareDialog;
import com.hjq.demo.http.api.ClockPutApi;
import com.hjq.demo.http.api.ClockSelectApi;
import com.hjq.demo.overall.CurrentTime;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengClient;
import com.hjq.umeng.UmengShare;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;


public class ClockActivity extends AppActivity {

    private Clock mClock;

    private String task_count, surplus, quota;

    private ImageView img1;
    private TextView text1, text2, text3, text4;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        private float degrees = 0;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mClock.setRotate(degrees += msg.what);
            this.sendEmptyMessageDelayed(msg.what, 50);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.clock_layout;
    }

    @Override
    protected void initView() {
        mClock = findViewById(R.id.mTextClock);
        img1 = findViewById(R.id.clock_img1);
        text1 = findViewById(R.id.clock_text1);
        text2 = findViewById(R.id.clock_text2);
        text3 = findViewById(R.id.clock_text3);
        text4 = findViewById(R.id.clock_text4);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void initData() {

        try {
            getClockData();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        text1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        String temp_date = DataMessage.temp_read("share");
        if (temp_date != null && temp_date.substring(0, 10).equals(new CurrentTime().getTimeNone().substring(0,10))) {
            text4.setText("1/1");
        }

        mClock.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            handler.removeMessages(2);
            handler.sendEmptyMessageDelayed(30, 30);
            handler.postDelayed(() -> {
                handler.removeCallbacksAndMessages(null);
                handler.removeMessages(30);
                handler.sendEmptyMessageDelayed(2, 30);
            }, 2000);
            String temp_dates = DataMessage.temp_read("share");
            if (temp_dates != null && temp_dates.substring(0, 10).equals(new CurrentTime().getTimeNone().substring(0,10))) {
                try {
                    putClockData();
                    getClockData();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {
                toast("任务未完成");
            }
        });

        img1.setOnClickListener(v -> {

            // 友盟统计：https://developer.umeng.com/docs/66632/detail/101814#h1-u521Du59CBu5316u53CAu901Au7528u63A5u53E32
            UmengClient.init(getApplication(), AppConfig.isLogEnable());
            UMWeb content = new UMWeb("https://47image.oss-cn-heyuan.aliyuncs.com/%E5%85%B1%E4%BA%AB%E6%95%B0%E5%88%9B.apk");
            content.setTitle("轮子哥");
            content.setThumb(new UMImage(ClockActivity.this, R.mipmap.logo_app));
            content.setDescription("轮子哥app");

            // 分享对话框
            new ShareDialog.Builder(ClockActivity.this)
                    .setShareLink(content)
                    .setListener(new UmengShare.OnShareListener() {

                        @Override
                        public void onSucceed(Platform platform) {
                            toast("分享成功");
                            text4.setText("1/1");
                            DataMessage.temp_save(new CurrentTime().getTimeNone(), "share");
                        }

                        @Override
                        public void onError(Platform platform, Throwable t) {
                            toast(t.getMessage());
                        }

                        @Override
                        public void onCancel(Platform platform) {
                            toast("分享取消");
                        }
                    })
                    .show();
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 友盟回调
        UmengClient.onActivityResult(this, requestCode, resultCode, data);
    }

    public void getClockData() throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new ClockSelectApi()
                        .setAccount(account)
                        .setSgin(new SecureSgin().md5(account))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(data.get("data")));
                            surplus = jsonObject.getString("allTotal");
                            quota = jsonObject.getString("dayTotal");
                            task_count = jsonObject.getString("total");
                            text1.setText(task_count + " 个");
                            text2.setText(surplus);
                            text3.setText(quota);
                            handler.sendEmptyMessageDelayed(2, 30);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }

    public void putClockData() throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new ClockPutApi()
                        .setAccount(account)
                        .setSgin(new SecureSgin().md5(String.valueOf(account)))
                )
                .request(new HttpCallback<JSONObject>(this) {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            toast(data.get("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        toast("系统繁忙");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        finish();
    }

}

