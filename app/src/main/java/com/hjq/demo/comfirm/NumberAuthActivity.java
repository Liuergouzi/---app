package com.hjq.demo.comfirm;


import static com.hjq.demo.comfirm.MockRequest.verifyNumber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.PreLoginResultListener;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;

public class NumberAuthActivity extends AppActivity {
    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private TokenResultListener mVerifyListener;
    private EditText mNumberEt;
    private String phoneNumber;
    private ProgressDialog mProgressDialog;
    private Button mAuthButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth;
    }

    @Override
    protected void initView() {
        mAuthButton = findViewById(R.id.auth_btn);
        mNumberEt = findViewById(R.id.et_number);
    }

    @Override
    protected void initData() {
        mAuthButton.setOnClickListener(v -> {
            phoneNumber = mNumberEt.getText().toString();
            //判断手机号是否合法
            if (!TextUtils.isEmpty(phoneNumber)) {
                showLoadingDialog("正在进行本机号码校验");
                numberAuth(5000);
            }
        });
        sdkInit();
        accelerateVerify(5000);
    }

    private void sdkInit() {
        mVerifyListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                try {
                    TokenRet pTokenRet = TokenRet.fromJson(s);
                    if (ResultCode.CODE_SUCCESS.equals(pTokenRet.getCode()) && !TextUtils.isEmpty(pTokenRet.getToken())) {
                        getResultWithToken(pTokenRet.getToken(), phoneNumber);
                    }
                    mPhoneNumberAuthHelper.setAuthListener(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(final String s) {
                NumberAuthActivity.this.runOnUiThread(() -> {
                    hideLoadingDialog();
                    setResult(2);
                    finish();
                });
                mPhoneNumberAuthHelper.setAuthListener(null);
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(getApplicationContext(), mVerifyListener);
    }


    /**
     * 加速校验
     * 进入输入手机号页面调用此接口，用户输入完手机号点击确定可以更快的获取token
     */
    public void accelerateVerify(int timeout) {
        mPhoneNumberAuthHelper.accelerateVerify(timeout, new PreLoginResultListener() {
            @Override
            public void onTokenSuccess(String vendor) {
                //成功时返回运营商简称
               // Log.i(TAG, "accelerateVerify：" + vendor);
            }

            @Override
            public void onTokenFailed(String vendor, String errorMsg) {
               // Log.e(TAG, "accelerateVerify：" + vendor + "， " + errorMsg);
            }
        });
    }

    public void numberAuth(int timeout) {
        mPhoneNumberAuthHelper.setAuthListener(mVerifyListener);
        mPhoneNumberAuthHelper.getVerifyToken(timeout);
    }

    public void getResultWithToken(final String token, final String phoneNumber) {
        ExecutorManager.run(() -> {
            final String msg = verifyNumber(token, phoneNumber);
            NumberAuthActivity.this.runOnUiThread(() -> {
                hideLoadingDialog();
                Intent pIntent = new Intent();
                pIntent.putExtra("result", phoneNumber);
                pIntent.putExtra("msg", msg);
                setResult(1, pIntent);
                finish();
            });
        });
    }

    public void showLoadingDialog(String hint) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.setMessage(hint);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void hideLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }


}
