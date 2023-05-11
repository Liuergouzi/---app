package com.hjq.demo.main.my;

import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.dialog.AddressDialog;
import com.hjq.demo.dialog.EditDialog;
import com.hjq.demo.http.api.PersonalApi;
import com.hjq.demo.http.api.PersonalSelectApi;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.image.ImageCropActivity;
import com.hjq.demo.image.ImagePreviewActivity;
import com.hjq.demo.image.ImagePutOss;
import com.hjq.demo.image.ImageSelectActivity;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.model.FileContentResolver;
import com.hjq.widget.layout.SettingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

/**
 * author : Android 轮子哥
 * 
 * time   : 2019/04/20
 * desc   : 个人资料
 */
public final class MyPersonalEditActivity extends AppActivity {

    private ViewGroup mAvatarLayout;
    private ImageView mAvatarView,master_head;
    private TextView master_id,master_name;
    private SettingBar mIdView;
    private SettingBar mNameView;
    private SettingBar mAddressView;

    /**
     * 省
     */
    private String mProvince = "北京市";
    /**
     * 市
     */
    private String mCity = "北京市";
    /**
     * 区
     */
    private String mArea = "东城区";

    /**
     * 头像地址
     */
    private Uri mAvatarUrl;

    private String account;

    @Override
    protected int getLayoutId() {
        return R.layout.my_personal_edit_activity;
    }

    @Override
    protected void initView() {
        mAvatarLayout = findViewById(R.id.fl_person_data_avatar);
        mAvatarView = findViewById(R.id.iv_person_data_avatar);
        mIdView = findViewById(R.id.sb_person_data_id);
        mNameView = findViewById(R.id.sb_person_data_name);
        mAddressView = findViewById(R.id.sb_person_data_address);
        master_head =findViewById(R.id.my_fragment_master_head);
        master_id = findViewById(R.id.my_fragment_master_id);
        master_name =findViewById(R.id.my_fragment_master_name);
        setOnClickListener(mAvatarLayout, mAvatarView, mNameView, mAddressView);
    }

    @Override
    protected void initData() {
        account = DataMessage.temp_read("account");
        try {
            EasyHttp.post(this)
                    .api(new PersonalSelectApi()
                            .setAccount(account)
                            .setSgin(new SecureSgin().md5(account))
                    )
                    .request(new HttpCallback<JSONObject>(this) {
                        @Override
                        public void onSucceed(JSONObject data) {
                            try {
                                JSONObject jsonObject = new JSONObject(String.valueOf(data.get("data")));
                                GlideApp.with(getActivity())
                                        .load(jsonObject.getString("head"))
                                        .placeholder(R.drawable.ic_boy)
                                        .error(R.drawable.ic_boy)
                                        .skipMemoryCache(true) // 不使用内存缓存
                                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                                        .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                                        .into(mAvatarView);
                                mIdView.setRightText(jsonObject.getString("ma"));
                                mNameView.setRightText(jsonObject.getString("name"));
                                mAddressView.setRightText(jsonObject.getString("address"));
                                master_id.setText(jsonObject.getString("invitation"));
                                if (jsonObject.getString("master").equals("exist")){
                                    GlideApp.with(getActivity())
                                            .load(jsonObject.getString("masterHead"))
                                            .placeholder(R.drawable.ic_boy)
                                            .error(R.drawable.ic_boy)
                                            .skipMemoryCache(true) // 不使用内存缓存
                                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                                            .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                                            .into(master_head);
                                    master_name.setText(jsonObject.getString("masterName"));
                                }
                                if (jsonObject.getString("master").equals("dataEmpty")){
                                    master_head.setBackgroundResource(R.drawable.ic_boy);
                                    master_name.setText("未命名用户");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mAvatarLayout) {
            ImageSelectActivity.start(this, data -> {
                // 裁剪头像
                cropImageFile(new File(data.get(0)));
            });
        } else if (view == mAvatarView) {
            if (mAvatarUrl != null) {
                // 查看头像
                ImagePreviewActivity.start(getActivity(), mAvatarUrl.toString());
            } else {
                // 选择头像
                onClick(mAvatarLayout);
            }
        } else if (view == mNameView) {
            new EditDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle(getString(R.string.personal_data_name_hint))
                    .setContent(mNameView.getRightText())
                    .setListener((dialog, content) -> {
                        if (!mNameView.getRightText().equals(content)) {
                            if (content.length() <= 6) {
                                mNameView.setRightText(content);
                                try {
                                    EasyHttp.post(this)
                                            .api(new PersonalApi()
                                                    .setAccount(account)
                                                    .setValue(content)
                                                    .setMode("name")
                                                    .setSgin(new SecureSgin().md5(account))
                                            )
                                            .request(new HttpCallback<JSONObject>(this) {
                                                @Override
                                                public void onSucceed(JSONObject data) {
                                                    try {
                                                        toast(data.get("message"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                toast("昵称最多6位");
                            }
                        }
                    }).show();
        } else if (view == mAddressView) {
            new AddressDialog.Builder(this)
                    //.setTitle("选择地区")
                    // 设置默认省份
                    .setProvince(mProvince)
                    // 设置默认城市（必须要先设置默认省份）
                    .setCity(mCity)
                    // 不选择县级区域
                    //.setIgnoreArea()
                    .setListener((dialog, province, city, area) -> {
                        String address = province + city + area;
                        if (!mAddressView.getRightText().equals(address)) {
                            mProvince = province;
                            mCity = city;
                            mArea = area;
                            try {
                                EasyHttp.post(this)
                                        .api(new PersonalApi()
                                                .setAccount(account)
                                                .setValue(address)
                                                .setMode("address")
                                                .setSgin(new SecureSgin().md5(account))
                                        )
                                        .request(new HttpCallback<JSONObject>(this) {
                                            @Override
                                            public void onSucceed(JSONObject data) {
                                                try {
                                                    toast(data.get("message"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            mAddressView.setRightText(address);
                        }
                    })
                    .show();
        }
    }

    /**
     * 裁剪图片
     */
    private void cropImageFile(File sourceFile) {
        ImageCropActivity.start(this, sourceFile, 1, 1, new ImageCropActivity.OnCropListener() {

            @Override
            public void onSucceed(Uri fileUri, String fileName) {
                File outputFile;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    outputFile = new FileContentResolver(getActivity(), fileUri, fileName);
                } else {
                    try {
                        outputFile = new File(new URI(fileUri.toString()));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        outputFile = new File(fileUri.toString());
                    }
                }
                updateCropImage(outputFile, true);
            }

            @Override
            public void onError(String details) {
                // 没有的话就不裁剪，直接上传原图片
                // 但是这种情况极其少见，可以忽略不计
                updateCropImage(sourceFile, false);
            }
        });
    }

    /**
     * 上传裁剪后的图片
     */
    private void updateCropImage(File file, boolean deleteFile) {
            if (file instanceof FileContentResolver) {
                mAvatarUrl = ((FileContentResolver) file).getContentUri();
            } else {
                mAvatarUrl = Uri.fromFile(file);
            }
            GlideApp.with(getActivity())
                    .load(mAvatarUrl)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(mAvatarView);
            String urlName="https://47image.oss-cn-heyuan.aliyuncs.com/head/"+account+".jpg";
            new ImagePutOss().PutOss(getContext(),"head/"+account+".jpg",mAvatarUrl);
            try {
                EasyHttp.post(this)
                        .api(new PersonalApi()
                                .setAccount(account)
                                .setValue(urlName)
                                .setMode("head")
                                .setSgin(new SecureSgin().md5(account))
                        )
                        .request(new HttpCallback<HttpData<JSONObject>>(this) {
                            @Override
                            public void onSucceed(HttpData<JSONObject> data) {

                            }
                        });
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
    }
}