package com.hjq.demo.main.my;

import static com.hjq.demo.comfirm.MockRequest.verifyNumber;
import static com.hjq.http.EasyUtils.postDelayed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.action.ToastAction;
import com.hjq.demo.app.AppConfig;
import com.hjq.demo.comfirm.ExecutorManager;
import com.hjq.demo.comfirm.NetStateChangeReceiver;
import com.hjq.demo.comfirm.NetworkType;
import com.hjq.demo.dialog.EditDialog;
import com.hjq.demo.dialog.PayPasswordDialog;
import com.hjq.demo.dialog.UpdateDialog;
import com.hjq.demo.dialog.WaitDialog;
import com.hjq.demo.http.api.KeyApi;
import com.hjq.demo.http.api.LogoutApi;
import com.hjq.demo.http.api.PayPasswordApi;
import com.hjq.demo.http.api.VersionApi;
import com.hjq.demo.manager.CacheDataManager;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnHttpListener;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.PreLoginResultListener;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class MyFragmentDrawer implements ToastAction {

    private BaseDialog mWaitDialog;
    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private TokenResultListener mVerifyListener;
    private LifecycleOwner activity;
    private Context context;
    private Class goClass;
    private Class goClass_one;
    private Class goClass_two;
    private String type;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    pay_dialog_one();
                    break;
                case 2:
                    pay_dialog_two();
                    break;
            }
        }
    };

    public MyFragmentDrawer() {
    }

    public MyFragmentDrawer(LifecycleOwner activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public MyFragmentDrawer(Context context, Class goClass_one, Class goClass_two) {
        this.context = context;
        this.goClass_one = goClass_one;
        this.goClass_two = goClass_two;
    }

    public MyFragmentDrawer(LifecycleOwner activity, Context context, Class goClass) {
        this.activity = activity;
        this.context = context;
        this.goClass = goClass;
        edit_dialog();
    }


    public MyFragmentDrawer(LifecycleOwner activity, Context context, String type) {
        this.activity = activity;
        this.context = context;
        this.type = type;
        edit_dialog();
    }

    public void edit_dialog() {
        new EditDialog.Builder(context)
                .setTitle("需要验证手机号保证账号安全")
                .setHint("请输入已绑定的手机号")
                .setConfirm(R.string.common_confirm)
                .setCancel(R.string.common_cancel)
                .setListener((dialog, content) -> {
                            try {
                                checkPhone(activity, context, goClass, type, content);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                ).show();
    }

    public void pay_dialog_one() {
        new PayPasswordDialog.Builder(context)
                .setTitle(R.string.pay_title)
                .setSubTitle("用于账号安全验证")
                .setMoney("设置支付密码")
                //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                .setListener((dialog, password) -> {
                    try {
                        putPayData(password);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                })
                .show();
    }

    public void pay_dialog_two() {
        new PayPasswordDialog.Builder(context)
                .setTitle(R.string.pay_title)
                .setSubTitle("请牢记您的支付密码")
                .setMoney("修改支付密码")
                .setListener((dialog, password) -> {
                    try {
                        putPayData(password);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                })
                .show();
    }

    public void checkPhone(LifecycleOwner activity, Context context, Class goClass, String type, String phone) throws NoSuchAlgorithmException {
        EasyHttp.post(activity)
                .api(new KeyApi()
                        .setName("phone")
                        .setSgin(new SecureSgin().md5("version_big_sb_560017dc94e8f9b65f4ca997c7feb326"))
                )
                .request(new HttpCallback<JSONObject>((OnHttpListener) activity) {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            if (data.getString("data").equals("null")) {
                                toast("sdk错误");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(String.valueOf(data.get("data")));
                            String key = jsonObject.getString("keyValue");
                            sdkInitCheck(context, key);
                            sdkGetCheck(context, goClass, type, phone);
                            accelerateVerify(5000);
                            String net = NetStateChangeReceiver.registerReceiver(context);
                            if (net.equals(NetworkType.NETWORK_WIFI.toString()) || net.equals(NetworkType.NETWORK_NO.toString())) {
                                toast("请先打开手机数据流量");
                            } else {
                                if (mWaitDialog == null) {
                                    mWaitDialog = new WaitDialog.Builder(context)
                                            // 消息文本可以不用填写
                                            .setMessage(R.string.common_loading)
                                            .create();
                                }
                                if (!mWaitDialog.isShowing()) {
                                    mWaitDialog.show();
                                }
                                numberAuth(5000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        toast("加载错误");
                    }
                });
    }

    public void sdkInitCheck(Context context, String secretInfo) {
        TokenResultListener pCheckListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {

            }

            @Override
            public void onTokenFailed(String s) {
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(context, pCheckListener);
        mPhoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        mPhoneNumberAuthHelper.setAuthSDKInfo(secretInfo);
        mPhoneNumberAuthHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_AUTH);
    }

    private void sdkGetCheck(Context context, Class goClass, String type, String phone) {
        mVerifyListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                try {
                    TokenRet pTokenRet = TokenRet.fromJson(s);
                    if (ResultCode.CODE_SUCCESS.equals(pTokenRet.getCode()) && !TextUtils.isEmpty(pTokenRet.getToken())) {
                        getResultWithToken(context, goClass, type, pTokenRet.getToken(), phone);
                    }
                    mPhoneNumberAuthHelper.setAuthListener(null);
                } catch (Exception e) {
                    postDelayed(mWaitDialog::dismiss, 200);
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(final String s) {
                toast("获取失败");
                postDelayed(mWaitDialog::dismiss, 200);
                mPhoneNumberAuthHelper.setAuthListener(null);
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(context.getApplicationContext(), mVerifyListener);
    }

    public void getResultWithToken(Context context, Class goClass, String type, final String token, final String phoneNumber) {
        ExecutorManager.run(() -> {
            String result = verifyNumber(token, phoneNumber);
            postDelayed(mWaitDialog::dismiss, 200);
            if (result.equals("PASS")) {
                if (goClass != null) {
                    Intent intent = new Intent(context, goClass);
                    context.startActivity(intent);
                } else {
                    Message message = new Message();
                    switch (type) {
                        case "pay_one":
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        case "pay_two":
                            message.what = 2;
                            handler.sendMessage(message);
                            break;
                    }
                }
            } else {
                toast("手机号验证不符合");
            }
        });
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

    public void logout() {
        EasyHttp.post(activity)
                .api(new LogoutApi()
                )
                .request(new HttpCallback<JSONObject>((OnHttpListener) activity) {
                    @Override
                    public void onSucceed(JSONObject data) {
                        toast("退出成功");
                    }
                    @Override
                    public void onFail(Exception e) {
                        toast("退出成功");
                    }
                });
    }


    public void getVersion() {
        try {
            EasyHttp.post(activity)
                    .api(new VersionApi()
                            .setSgin(new SecureSgin().md5("version_big_sb_560017dc94e8f9b65f4ca997c7feb326"))
                    )
                    .request(new HttpCallback<JSONObject>((OnHttpListener) activity) {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onSucceed(JSONObject data) {
                            if (data != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(String.valueOf(data.get("data")));
                                    // 本地的版本码和服务器的进行比较
                                    if (Float.parseFloat(jsonObject.get("versionCode").toString()) > AppConfig.getVersionCode()) {
                                        new UpdateDialog.Builder(context)
                                                .setVersionName(jsonObject.get("versionCode").toString())
                                                .setForceUpdate(Boolean.parseBoolean(jsonObject.get("choice").toString()))
                                                .setUpdateLog(jsonObject.get("log").toString())
                                                .setDownloadUrl(jsonObject.get("url").toString())
                                                //.setFileMd5("560017dc94e8f9b65f4ca997c7feb326")
                                                .show();
                                    } else {
                                        toast("当前已是最新版本");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            toast("加载错误");
                        }
                    });

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void goActivity() {
        String isPhone = DataMessage.temp_read("isPhone");
        if (Objects.equals(isPhone, "true")) {
            Intent intent = new Intent(context, goClass_one);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, goClass_two);
            context.startActivity(intent);
            toast("您还未绑定手机号，请先绑定手机号");
        }
    }

    @SuppressLint("StaticFieldLeak")
    static class MainAsyncTaskCache extends AsyncTask<String, Integer, String> implements ToastAction {
        private final Context context;

        public MainAsyncTaskCache(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                CacheDataManager.clearAllCache(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            toast("已清除缓存" + CacheDataManager.getTotalCacheSize(context));
        }
    }


    public void putPayData(String pay) throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(activity)
                .api(new PayPasswordApi()
                        .setAccount(account)
                        .setPayPassword(pay)
                        .setSgin(new SecureSgin().md5(pay))
                )
                .request(new HttpCallback<JSONObject>((OnHttpListener) activity) {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            toast(data.getString("message"));
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

}
