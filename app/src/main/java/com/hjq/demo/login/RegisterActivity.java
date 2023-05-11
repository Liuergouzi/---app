package com.hjq.demo.login;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.RegisterApi;
import com.hjq.demo.manager.InputTextManager;
import com.hjq.demo.overall.SecureSginK;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.view.SubmitButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import okhttp3.Call;


public final class RegisterActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private static final String INTENT_KEY_PHONE = "phone";
    private static final String INTENT_KEY_PASSWORD = "password";

    @Log
    public static void start(BaseActivity activity, String phone, String password, OnRegisterListener listener) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(INTENT_KEY_PHONE, phone);
        intent.putExtra(INTENT_KEY_PASSWORD, password);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSucceed(data.getStringExtra(INTENT_KEY_PHONE), data.getStringExtra(INTENT_KEY_PASSWORD));
            } else {
                listener.onCancel();
            }
        });

    }

    private EditText mAccountView;
    private EditText mFirstPassword;
    private EditText mSecondPassword;
    private EditText mInvitation;
    private SubmitButton mCommitView;

    @Override
    protected int getLayoutId() {
        return R.layout.register_activity;
    }

    @Override
    protected void initView() {
        mAccountView = findViewById(R.id.et_register_phone);
        mFirstPassword = findViewById(R.id.et_register_password1);
        mSecondPassword = findViewById(R.id.et_register_password2);
        mInvitation = findViewById(R.id.et_register_Invitation);
        mCommitView = findViewById(R.id.btn_register_commit);

        setOnClickListener(mCommitView);

        mSecondPassword.setOnEditorActionListener(this);

        // 给这个 View 设置沉浸式，避免状态栏遮挡
        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title));

        InputTextManager.with(this)
                .addView(mAccountView)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .setMain(mCommitView)
                .build();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //此处可放 调用获取剪切板内容的代码
            mInvitation.setText(getClipboardContent());
        }, 1000);
    }

    public String getClipboardContent() {
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        // 返回数据
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData == null || clipData.getItemCount() <= 0) {
            return "";
        }
        ClipData.Item item = clipData.getItemAt(0);
        if (item == null || item.getText() == null) {
            return "";
        }
        return item.getText().toString();
    }

    @Override
    protected void initData() {
        // 自动填充手机号和密码
        mAccountView.setText(getString(INTENT_KEY_PHONE));
        mFirstPassword.setText(getString(INTENT_KEY_PASSWORD));
        mSecondPassword.setText(getString(INTENT_KEY_PASSWORD));
    }

    @SingleClick
    @Override
    public void onClick(View view) {

        if (view == mCommitView) {

            if (mAccountView.getText().toString().length() < 6 && mAccountView.getText().toString().length() > 18) {
                mAccountView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_phone_input_error);
                return;
            }

            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_password_input_unlike);
                return;
            }
            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());
            try {
                EasyHttp.post(this)
                        .api(new RegisterApi()
                                .setAccount(mAccountView.getText().toString())
                                .setPassword(mFirstPassword.getText().toString())
                                .setInvitation(mInvitation.getText().toString())
                                .setSgin(new SecureSginK().md5(mFirstPassword.getText().toString()))
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
                                    if (data.get("code").equals(200)) {
                                        postDelayed(() -> {
                                            mCommitView.showSucceed();
                                            postDelayed(() -> {
                                                setResult(RESULT_OK, new Intent()
                                                        .putExtra(INTENT_KEY_PHONE, mAccountView.getText().toString())
                                                        .putExtra(INTENT_KEY_PASSWORD, mFirstPassword.getText().toString()));
                                                finish();
                                            }, 500);
                                            toast(R.string.common_account_true);
                                        }, 500);
                                    } else if (data.get("code").equals(400)){
                                        toast(data.get("message"));
                                        mCommitView.showError(3000);
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
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 不要把整个布局顶上去
                .keyboardEnable(true);
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击注册按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    /**
     * 注册监听
     */
    public interface OnRegisterListener {

        /**
         * 注册成功
         *
         * @param phone    手机号
         * @param password 密码
         */
        void onSucceed(String phone, String password);

        /**
         * 取消注册
         */
        default void onCancel() {
        }
    }
}