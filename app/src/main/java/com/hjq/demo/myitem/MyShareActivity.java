package com.hjq.demo.myitem;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.AppConfig;
import com.hjq.demo.dialog.ShareDialog;
import com.hjq.demo.http.api.PersonalSelectApi;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengClient;
import com.hjq.umeng.UmengShare;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public class MyShareActivity extends AppActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_myshare;
    }

    @Override
    protected void initView() {
        share_QRCode=findViewById(R.id.share_QRCode);
        share_invitation_code=findViewById(R.id.share_invitation_code);
        setOnClickListener(R.id.share_save, R.id.share_invitation, R.id.share_copy);
    }

    @Override
    protected void initData() {
        try {
            getInvitation();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private TextView share_invitation_code;
    private ImageView share_QRCode;
    private String QR_code;

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        assert QR_code != null;
        if (viewId== R.id.share_save){
            Bitmap bitmap = ((BitmapDrawable)share_QRCode.getDrawable()).getBitmap();
            Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
            toast("二维码已保存至相册");
        }else if (viewId== R.id.share_invitation){
            UmengClient.init(getApplication(), AppConfig.isLogEnable());
            UMWeb content = new UMWeb(QR_code);
            content.setTitle("轮子哥");
            content.setThumb(new UMImage(MyShareActivity.this, R.mipmap.logo_app));
            content.setDescription("轮子哥app");
            // 分享对话框
            new ShareDialog.Builder(MyShareActivity.this)
                    .setShareLink(content)
                    .setListener(new UmengShare.OnShareListener() {
                        @Override
                        public void onSucceed(Platform platform) {
                            toast("分享成功");
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
        }else if (viewId== R.id.share_copy){
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            assert clipboardManager != null;
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null,share_invitation_code.getText()));
            toast("复制成功");
        }


    }

    public void getInvitation() throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new PersonalSelectApi()
                        .setAccount(account)
                        .setSgin(new SecureSgin().md5(account))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(String.valueOf(data.get("data")));
                            share_invitation_code.setText(jsonObject.getString("ma"));
                            Resources res=getResources();
                            Bitmap logo= BitmapFactory.decodeResource(res, R.drawable.logo_app);
                            QR_code="http://ctrlc.cc/home/download/download.html?QR_code="+jsonObject.getString("ma");
                            Bitmap bitmap = CodeUtils.createImage(QR_code, 250, 250, logo);
                            share_QRCode.setImageBitmap(bitmap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 不使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }


}
