package com.hjq.demo.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.dialog.TipsDialog;
import com.hjq.demo.http.api.PasswordResetApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.manager.InputTextManager;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import java.security.NoSuchAlgorithmException;

/**
 *    author : Android 轮子哥
 *    time   : 2020/02/29
 *    desc   : 重置密码
 */
public final class PasswordResetActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private static final String INTENT_KEY_IN_PHONE = "phone";
    private static final String INTENT_KEY_IN_CODE = "code";

    @Log
    public static void start(Context context, String phone, String code) {
        Intent intent = new Intent(context, PasswordResetActivity.class);
        intent.putExtra(INTENT_KEY_IN_PHONE, phone);
        intent.putExtra(INTENT_KEY_IN_CODE, code);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private EditText mFirstPassword;
    private EditText mSecondPassword;
    private Button mCommitView;

    /** 手机号 */
    private String mPhoneNumber;
    /** 验证码 */
    private String mVerifyCode;

    @Override
    protected int getLayoutId() {
        return R.layout.password_reset_activity;
    }

    @Override
    protected void initView() {
        mFirstPassword = findViewById(R.id.et_password_reset_password1);
        mSecondPassword = findViewById(R.id.et_password_reset_password2);
        mCommitView = findViewById(R.id.btn_password_reset_commit);

        setOnClickListener(mCommitView);

        mSecondPassword.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        mPhoneNumber = getString(INTENT_KEY_IN_PHONE);
        mVerifyCode = getString(INTENT_KEY_IN_CODE);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCommitView) {

            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_password_input_unlike);
                return;
            }

            if (mFirstPassword.getText().length()<6||mFirstPassword.getText().length()>18){
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_password_length_unlike);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            // 重置密码
            try {
                String account = DataMessage.temp_read("account");
                EasyHttp.post(this)
                        .api(new PasswordResetApi()
                                .setAccount(account)
                                .setPassword(mFirstPassword.getText().toString())
                                .setSgin(new SecureSgin().md5(mFirstPassword.getText().toString()))
                        )

                        .request(new HttpCallback<HttpData<Void>>(this) {

                            @Override
                            public void onSucceed(HttpData<Void> data) {
                                new TipsDialog.Builder(getActivity())
                                        .setIcon(TipsDialog.ICON_FINISH)
                                        .setMessage(R.string.password_reset_success)
                                        .setDuration(2000)
                                        .addOnDismissListener(dialog -> finish())
                                        .show();
                            }

                            @Override
                            public void onFail(Exception e) {
                                super.onFail(e);
                                toast(R.string.common_api_error);
                            }
                        });
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击提交按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}