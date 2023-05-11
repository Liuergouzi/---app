package com.hjq.demo.comfirm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;

import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.dialog.HintDialog;
import com.hjq.demo.dialog.HintSingleDialog;
import com.hjq.demo.http.api.DmfQrcodeApi;
import com.hjq.demo.http.api.DmfSelectApi;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;

public class ComfirmPayActivity extends AppActivity {

    private Button button;
    private static int sessionDepth = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comfirm_pay;
    }

    @Override
    protected void initView() {
        button = findViewById(R.id.comfirm_pay_btn);
    }

    @Override
    protected void initData() {
        button.setOnClickListener(v -> {
            try {
                getDmfQrcodeData();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (sessionDepth == 0) {
            // 从后台返回
            toast("...正在监测支付订单...");
            try {
                getDmfData();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            sessionDepth = 0;
        }
        sessionDepth++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionDepth > 0)
            sessionDepth--;
//        if (sessionDepth == 0) {
//            // 进入后台
//            //toast("进入支付宝支付");
//        }
    }

    public void getDmfQrcodeData() throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new DmfQrcodeApi()
                        .setAccount(account)
                        .setSgin(new SecureSgin().md5(account))
                )
                .request(new HttpCallback<String>(this) {
                    @Override
                    public void onStart(Call call) {
                    }
                    @Override
                    public void onEnd(Call call) {
                    }
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(String data) {
                        if (data != null) {
                            Intent intent;
                            try {
                                intent = Intent.parseUri(data, Intent.URI_INTENT_SCHEME);
                                startActivity(intent);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }


    public void getDmfData() throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new DmfSelectApi()
                        .setAccount(account)
                        .setMode("authentication")
                        .setSgin(new SecureSgin().md5(account))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @Override
                    public void onStart(Call call) {
                    }

                    @Override
                    public void onEnd(Call call) {
                    }
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            if (data.getString("code").equals("200")){
                                showDialogOne();
                            }else {
                                showDialog();
                            }
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


    public void showDialogOne() {
        HintSingleDialog dialog = new HintSingleDialog(ComfirmPayActivity.this, R.style.hint_dialog,
                confirm -> {
                });
        dialog.setTitle("支付状态：");
        dialog.setContent("支付成功");
        dialog.setNegativeButton("返回");
        dialog.show();
    }

    public void showDialog() {
        HintDialog dialog = new HintDialog(ComfirmPayActivity.this, R.style.hint_dialog,
                confirm -> {
                    if (confirm) {
                        try {
                            getDmfQrcodeData();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                });
        dialog.setTitle("支付状态：");
        dialog.setContent("订单未支付");
        dialog.setPositiveButton("重新支付");
        dialog.setNegativeButton("放弃");
        dialog.show();
    }

}
