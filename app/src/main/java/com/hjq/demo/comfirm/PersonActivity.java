package com.hjq.demo.comfirm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.KeyApi;
import com.hjq.demo.http.api.PhoneApi;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnHttpListener;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import okhttp3.Call;

public class PersonActivity extends AppActivity implements NetStateChangeObserver {

    private TextView mTvPhone;
    private Button mVerifyBtn, mVerifyBtn2;
    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private boolean sdkAvailable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_person;
    }

    @Override
    protected void initView() {
        mTvPhone = findViewById(R.id.number_tv);
        mVerifyBtn = findViewById(R.id.verify_btn);
        mVerifyBtn2 = findViewById(R.id.verify_btn2);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {

        try {
            EasyHttp.post(this)
                    .api(new KeyApi()
                            .setName("phone")
                            .setSgin(new SecureSgin().md5("version_big_sb_560017dc94e8f9b65f4ca997c7feb326"))
                    )
                    .request(new HttpCallback<JSONObject>((OnHttpListener) this) {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onSucceed(JSONObject data) {
                            try {
                                if (data.getString("data").equals("null")) {
                                    toast("sdk错误");
                                } else {
                                    JSONObject jsonObject = new JSONObject(String.valueOf(data.get("data")));
                                    String key = jsonObject.getString("keyValue");
                                    sdkInit(key);
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String isPhone = DataMessage.temp_read("isPhone");
        if (Objects.equals(isPhone, "true")) {
            mVerifyBtn.setVisibility(View.INVISIBLE);
            String phone = DataMessage.temp_read("phone");
            if (phone !=null && phone.length()==11) {
                mTvPhone.setText("已绑定手机号: " + phone.substring(0, 3) + "******" + phone.substring(9, 11));
            }else {
                mTvPhone.setText("***");
                toast("数据库手机号格式错误");
            }

        }

        mVerifyBtn.setOnClickListener(v -> {
            String net = NetStateChangeReceiver.registerReceiver(PersonActivity.this);
            if (net.equals(NetworkType.NETWORK_WIFI.toString()) || net.equals(NetworkType.NETWORK_NO.toString())) {
                Toast.makeText(PersonActivity.this, "请先打开手机数据流量", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(PersonActivity.this, NumberAuthActivity.class);
                startActivityForResult(intent, 1001);
            }
            mPhoneNumberAuthHelper.setAuthListener(null);
        });
        mVerifyBtn2.setOnClickListener(v -> {
            toast("暂不支持换绑，如必须换绑请联系客服");
        });
    }

    public void sdkInit(String secretInfo) {
        TokenResultListener pCheckListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                sdkAvailable = true;
            }

            @Override
            public void onTokenFailed(String s) {
                sdkAvailable = false;
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(this, pCheckListener);
        mPhoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        mPhoneNumberAuthHelper.setAuthSDKInfo(secretInfo);
        mPhoneNumberAuthHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_AUTH);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            showRetDialog("本机号码校验失败");
        } else if (resultCode == 1) {
            switch (data.getStringExtra("msg")) {
                case "PASS":
                    String phoneNumber = data.getStringExtra("result");
                    try {
                        putPhoneData(phoneNumber);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    break;
                case "UNKNOWN":
                    showRetDialog("手机号无法验证，请联系客服");
                    break;
                case "REJECT":
                    showRetDialog("所填的手机号和当前手机里的手机号不一致");
                    break;
                case "phoneerror":
                    showRetDialog("手机号不存在，请使用国内手机号认证");
                    break;
                case "neterror":
                    showRetDialog("连接超时，请检查网络连接");
                    break;
                case "linkerror":
                    showRetDialog("服务开了个小差，导致连接失败，请稍后重试");
                    break;
            }
        }
        mPhoneNumberAuthHelper.setAuthListener(null);
    }

    private void showRetDialog(final String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void putPhoneData(String phone) throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new PhoneApi()
                        .setAccount(account)
                        .setPhone(phone)
                        .setSgin(new SecureSgin().md5(phone))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @Override
                    public void onStart(Call call) {
                    }

                    @Override
                    public void onEnd(Call call) {
                    }

                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            if (data.getString("code").equals("200")){
                                mTvPhone.setText("已绑定手机号: " + phone.substring(0, 3) + "******" + phone.substring(9, 11));
                                mVerifyBtn.setVisibility(View.INVISIBLE);
                                DataMessage.temp_save("true", "isPhone");
                                DataMessage.temp_save(phone, "phone");
                            }
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

    @Override
    public void onNetDisconnected() {

    }

    @Override
    public void onNetConnected(NetworkType networkType) {

    }


}
