package com.hjq.demo.login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.KeyboardWatcher;
import com.hjq.demo.http.api.LoginApi;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.main.MainActivity;
import com.hjq.demo.main.home.HomeFragment;
import com.hjq.demo.manager.InputTextManager;
import com.hjq.demo.overall.Cache;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengClient;
import com.hjq.umeng.UmengLogin;
import com.hjq.widget.view.SubmitButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * 
 * time   : 2019/10/18
 * desc   : 登录界面
 */
public final class LoginActivity extends AppActivity
        implements UmengLogin.OnLoginListener,
        KeyboardWatcher.SoftKeyboardStateListener,
        TextView.OnEditorActionListener {

    private static final String INTENT_KEY_IN_PHONE = "phone";
    private static final String INTENT_KEY_IN_PASSWORD = "password";
    private String STATUS, PHONE, CARD;
    private int WIDTH, HEIGHT;

    @Log
    public static void start(Context context, String phone, String password) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(INTENT_KEY_IN_PHONE, phone);
        intent.putExtra(INTENT_KEY_IN_PASSWORD, password);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private ImageView mLogoView;

    private ViewGroup mBodyLayout;
    private EditText mPhoneView;
    private EditText mPasswordView;

    private View mForgetView;
    private SubmitButton mCommitView;

    private View mOtherView;
    private View mQQView;
    private View mWeChatView;

    /**
     * logo 缩放比例
     */
    private final float mLogoScale = 0.8f;
    /**
     * 动画时间
     */
    private final int mAnimTime = 300;

    @Override
    protected int getLayoutId() {
        return R.layout.login_activity;
    }

    @Override
    protected void initView() {
        mLogoView = findViewById(R.id.iv_login_logo);
        mBodyLayout = findViewById(R.id.ll_login_body);
        mPhoneView = findViewById(R.id.et_login_phone);
        mPasswordView = findViewById(R.id.et_login_password);
        mForgetView = findViewById(R.id.tv_login_forget);
        mCommitView = findViewById(R.id.btn_login_commit);
        mOtherView = findViewById(R.id.ll_login_other);
        mQQView = findViewById(R.id.iv_login_qq);
        mWeChatView = findViewById(R.id.iv_login_wechat);

        setOnClickListener(mForgetView, mCommitView, mQQView, mWeChatView);

        mPasswordView.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mPasswordView)
                .setMain(mCommitView)
                .build();
    }

    @SuppressLint("HardwareIds")
    @Override
    protected void initData() {

        if (Cache.getCachedToken(this.getContext())!=null){
            MainActivity.start(getContext(), HomeFragment.class);
            finish();
        }

        postDelayed(() -> KeyboardWatcher.with(LoginActivity.this)
                .setListener(LoginActivity.this), 500);

        // 判断用户当前有没有安装 QQ
        if (!UmengClient.isAppInstalled(this, Platform.QQ)) {
            mQQView.setVisibility(View.GONE);
        }

        // 判断用户当前有没有安装微信
        if (!UmengClient.isAppInstalled(this, Platform.WECHAT)) {
            mWeChatView.setVisibility(View.GONE);
        }

        // 如果这两个都没有安装就隐藏提示
        if (mQQView.getVisibility() == View.GONE && mWeChatView.getVisibility() == View.GONE) {
            mOtherView.setVisibility(View.GONE);
        }

        // 自动填充手机号和密码
        mPhoneView.setText(getString(INTENT_KEY_IN_PHONE));
        mPasswordView.setText(getString(INTENT_KEY_IN_PASSWORD));

//        String a=Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    @Override
    public void onRightClick(View view) {
        // 跳转到注册界面
        RegisterActivity.start(this, mPhoneView.getText().toString(),
                mPasswordView.getText().toString(), (phone, password) -> {
                    // 如果已经注册成功，就执行登录操作
                    mPhoneView.setText(phone);
                    mPasswordView.setText(password);
                    mPasswordView.requestFocus();
                    mPasswordView.setSelection(mPasswordView.getText().length());
                    onClick(mCommitView);
                });
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mForgetView) {
            toast("关闭了");
            //startActivity(PasswordForgetActivity.class);
            return;
        }

        if (view == mCommitView) {
            if (mPhoneView.getText().toString().length() < 6 && mPhoneView.getText().toString().length() > 18) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_network_error);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            try {
                Cache.cacheToken(this,null);
                EasyHttp.post(this)
                        .api(new LoginApi()
                                .setSgin(new SecureSgin().md5(mPasswordView.getText().toString()))
                                .setAccount(mPhoneView.getText().toString())
                                .setPassword(mPasswordView.getText().toString())
                        )
                        .request(new HttpCallback<JSONObject>(this) {
                            @Override
                            public void onStart(Call call) {
                                mCommitView.showProgress();
                            }

                            @Override
                            public void onEnd(Call call) {
                            }

                            @Override
                            public void onSucceed(JSONObject data) {
                                try {
                                    if (data.get("code").equals(400)){
                                        toast(data.get("message"));
                                        mCommitView.showError(3000);
                                    }
                                    if (data.get("code").equals(200)){
                                        postDelayed(() -> {
                                            mCommitView.showSucceed();
                                            postDelayed(() -> {
                                                DataMessage.temp_save(mPhoneView.getText().toString(),"account");
                                                try {
                                                    Cache.cacheToken(getContext(),  data.getString("token"));
                                                    JSONObject jsonObject = new JSONObject(String.valueOf(data.get("data")));
                                                    DataMessage.temp_save(jsonObject.getString("phone"),"phone");
                                                    if (!jsonObject.getString("phone").equals("null") ) {
                                                        DataMessage.temp_save("true", "isPhone");
                                                        DataMessage.temp_save("true", "phone");
                                                    }
                                                    if (!jsonObject.getString("cardId").equals("null")) {
                                                        DataMessage.temp_save("true", "isAuthentication");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                MainActivity.start(getContext(), HomeFragment.class);
                                                finish();

                                            }, 500);
                                        }, 500);
                                    }
                                } catch (JSONException e) {
                                    toast(R.string.common_data_error);
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                                super.onFail(e);
                                postDelayed(() -> mCommitView.showError(3000), 1000);
                            }
                        });
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return;
        }

        if (view == mQQView || view == mWeChatView) {

            //Platform platform;
            if (view == mQQView) {
                //platform = Platform.QQ;
                toast(R.string.login_qq);
            } else {
                //platform = Platform.WECHAT;
                toast(R.string.login_wei);
            }
            //UmengClient.login(this, platform, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 友盟回调
        UmengClient.onActivityResult(this, requestCode, resultCode, data);
    }

    /**
     * 授权成功的回调
     *
     * @param platform 平台名称
     * @param data     用户资料返回
     */
    @Override
    public void onSucceed(Platform platform, UmengLogin.LoginData data) {
        if (isFinishing() || isDestroyed()) {
            // Glide：You cannot start a load for a destroyed activity
            return;
        }

        // 判断第三方登录的平台
        switch (platform) {
            case QQ:
            case WECHAT:
            default:
        }

        GlideApp.with(this)
                .load(data.getAvatar())
                .circleCrop()
                .into(mLogoView);

        toast("昵称：" + data.getName() + "\n" +
                "性别：" + data.getSex() + "\n" +
                "id：" + data.getId() + "\n" +
                "token：" + data.getToken());
    }

    /**
     * 授权失败的回调
     *
     * @param platform 平台名称
     * @param t        错误原因
     */
    @Override
    public void onError(Platform platform, Throwable t) {
        toast("第三方登录出错：" + t.getMessage());
    }

    /**
     * {@link KeyboardWatcher.SoftKeyboardStateListener}
     */

    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", 0, -mCommitView.getHeight());
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        // 执行缩小动画
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", 1f, mLogoScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", 1f, mLogoScale);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", 0f, -mCommitView.getHeight());
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

    @Override
    public void onSoftKeyboardClosed() {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", mBodyLayout.getTranslationY(), 0f);
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        if (mLogoView.getTranslationY() == 0) {
            return;
        }

        // 执行放大动画
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", mLogoScale, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", mLogoScale, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", mLogoView.getTranslationY(), 0f);
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击登录按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}