package com.hjq.demo.comfirm;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.ComfirmApi;
import com.hjq.demo.http.api.DmfSelectApi;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import okhttp3.Call;

public class ComfirmActivity extends AppActivity {

    private EditText phone_ed1, phone_ed2;
    private String phone;
    private LinearLayout linearLayout1, linearLayout2;
    private Button phone_btn, phone_back;
    private TextView phone_text;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comfirm;
    }

    @Override
    protected void initView() {
        phone_text = findViewById(R.id.phone_comfirm_text);
        phone_ed1 = findViewById(R.id.phone_comfirm_ed1);
        phone_ed2 = findViewById(R.id.phone_comfirm_ed2);
        phone_btn = findViewById(R.id.phone_comfirm_btn);
        phone_back = findViewById(R.id.phone_comfirm_back);
        linearLayout1 = findViewById(R.id.phone_comfirm_linear1);
        linearLayout2 = findViewById(R.id.phone_comfirm_linear2);
    }

    @Override
    protected void initData() {

        String isPhone = DataMessage.temp_read("isPhone");
        if (Objects.equals(isPhone, "true")) {
            phone = DataMessage.temp_read("phone");
            phone_text.setText(phone);
        }
        String isAuthentication = DataMessage.temp_read("isAuthentication");
        if (Objects.equals(isAuthentication, "true")) {
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.VISIBLE);
        } else {
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.GONE);
        }

        phone_btn.setOnClickListener(v -> {
            try {
                getDmfData();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
        phone_back.setOnClickListener(v -> finish());
    }


    public void getComfirmData(String phone, String cardname, String cardid) throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new ComfirmApi()
                        .setAccount(account)
                        .setPhone(phone)
                        .setcardname(cardname)
                        .setcardid(cardid)
                        .setSgin(new SecureSgin().md5(cardid))
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
                        switch (data) {
                            case "0":
                                toast("认证失败，该实名信息已被认证");
                                break;
                            case "2":
                                toast("认证失败，与手机号实名信息不一致，请仔细检查填写是否正确");
                                break;
                            case "1":
                                toast("认证成功");
                                DataMessage.temp_save("true","isAuthentication");
                                linearLayout1.setVisibility(View.GONE);
                                linearLayout2.setVisibility(View.VISIBLE);
                                break;
                            default:
                                toast("发生未知错误，请联系客服");
                                break;
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
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {

                        try {
                            if (data.getString("code").equals("200")) {
                                String e1 = phone_ed1.getText().toString();
                                String e2 = phone_ed2.getText().toString();
                                if (e1.equals("") || e2.equals(""))
                                    toast("输入不能为空");
                                else {
                                    try {
                                        getComfirmData(phone, e1, e2);
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                toast("请先完成支付");
                                startActivity(ComfirmPayActivity.class);
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
}
