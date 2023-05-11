package com.hjq.demo.main.put;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.demo.R;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.api.PutFragmentOssApi;
import com.hjq.demo.image.ImagePreviewActivity;
import com.hjq.demo.image.ImagePutOss;
import com.hjq.demo.image.ImageSelectActivity;
import com.hjq.demo.main.MainActivity;
import com.hjq.demo.main.home.HomeFragment;
import com.hjq.demo.overall.CurrentTime;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.GetScreenSize;
import com.hjq.demo.overall.SecureSginK;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class PutFragment extends TitleBarFragment<MainActivity> {

    List<Bitmap> fileList = new ArrayList<>();
    List<Uri> fileList_preview = new ArrayList<>();
    int width;
    int height;
    int num = 100;//限制的最大字数
    private LinearLayout Osslist1, Osslist2, Osslist3;
    private EditText et_content;
    TextView tv_num;// 用来显示剩余字数


    public static PutFragment newInstance() {
        return new PutFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.put_fragment;
    }

    @Override
    protected void initView() {

        Osslist1 = findViewById(R.id.oss_list1);
        Osslist2 = findViewById(R.id.oss_list2);
        Osslist3 = findViewById(R.id.oss_list3);
        Button button_add = findViewById(R.id.oss_add);//打开相册按钮
        Button button_put = findViewById(R.id.oss_put);
        et_content = findViewById(R.id.oss_et);
        tv_num = findViewById(R.id.tv_num);
        tv_num.setText(String.valueOf(num));

        button_add.setOnClickListener(v->{
            if(fileList.size()<9) {
                ImageSelectActivity.start(getAttachActivity(), 9-fileList.size(), data -> {
                    for (String s : data) {
                        Uri uri = getMediaUriFromPath(getAttachActivity(), s);
                        fileList_preview.add(uri);
                        Bitmap bitmap = null;
                        try {
                            bitmap = getBitmapFromUri(uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        fileList.add(bitmap);
                    }
                    DisplayPic();
                });
            }else {
                toast("最多添加9张图片");
            }
        });

        button_put.setOnClickListener(v -> {
            //判断是否为空
            if (!(et_content.getText().toString().equals("") || fileList.size() == 0)) {
                try {
                    getOssData();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(v.getContext(), "发布失败，图片或标题未添加", Toast.LENGTH_SHORT).show();
            }
        });

        et_content.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
                System.out.println("s=" + s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = num - s.length();
                tv_num.setText(String.valueOf(number));
                int selectionStart = et_content.getSelectionStart();
                int selectionEnd = et_content.getSelectionEnd();
                if (temp.length() > num) {
                    s.delete(selectionStart - 1, selectionEnd);
                    et_content.setText(s);
                    et_content.setSelection(selectionStart);//设置光标在最后
                }
            }
        });

    }

    @Override
    protected void initData() {
    }

    private void DisplayPic() {
        width = new GetScreenSize().getScreenWidth(getAttachActivity());
        height = new GetScreenSize().getScreenHeight(getAttachActivity());

        Osslist1.removeAllViews();
        Osslist2.removeAllViews();
        Osslist3.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = width / 4;
        params.height = width / 4;
        params.setMargins(20, 20, 20, 20);

        LinearLayout.LayoutParams params_small = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params_small.width = width / 15;
        params_small.height = width / 15;
        params_small.setMargins(width / 5, 20, 20, 20);

        for (int i = 0; i < fileList.size(); i++) {
            RelativeLayout relativeLayout = new RelativeLayout(getAttachActivity());
            ImageView imageView_small = new ImageView(getAttachActivity());
            ImageView imageView = new ImageView(getAttachActivity());

            Bitmap bitmap = fileList.get(i);

            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            imageView.setLayoutParams(params);

            imageView_small.setLayoutParams(params_small);
            imageView_small.setBackgroundResource(R.drawable.ic_tips_delete);

            relativeLayout.addView(imageView);
            relativeLayout.addView(imageView_small);

            ClickPic(imageView, i);
            Remove(relativeLayout, i, imageView_small, fileList.get(i));
            if (i <= 2)
                Osslist1.addView(relativeLayout);
            if (i > 2 && i <= 5)
                Osslist2.addView(relativeLayout);
            if (i > 5 && i <= 8)
                Osslist3.addView(relativeLayout);

        }
    }

    /***
     * 将指定路径的图片转uri
     */
    public static Uri getMediaUriFromPath(Context context, String path) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
                new String[]{path.substring(path.lastIndexOf("/") + 1)},
                null);
        Uri uri = null;
        if (cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri,
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        }
        cursor.close();
        return uri;
    }

    /**
     * Uri转化为Bitmap
     */
    private Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException {
        //先对bitmap压缩，防止返回的bitmap过大
        ContentResolver contentResolver = getAttachActivity().getContentResolver();
        //创建 options 实例
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //设置为true时，获取到的bitmap为null，只是存放宽高值
        Bitmap bitmap;
        // 计算宽高缩放比例
        int bitmap_width = options.outWidth / 680;
        int bitmap_height = options.outHeight / 680;
        // 最终取大的那个为缩放比例，这样才能适配
        // 设置缩放比例
        options.inSampleSize = Math.max(bitmap_width, bitmap_height);
        options.inJustDecodeBounds = false; // 一定要记得将inJustDecodeBounds设为false，否则Bitmap为null
        bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
        //以上为尺寸压缩，接着再执行一次质量压缩
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] picData = bos.toByteArray();
        Bitmap result = BitmapFactory.decodeByteArray(picData, 0, picData.length);
        bos.reset();
        return result;
    }

    public void getOssData() throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        String time = new CurrentTime().getTimeNone();
        String ed_text = et_content.getText().toString();
        EasyHttp.post(this)
                .api(new PutFragmentOssApi()
                        .setAccount(account)
                        .setTime(time)
                        .setContent(ed_text)
                        .setImageSize(fileList.size())
                        .setSgin(new SecureSginK().md5(time))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            if (data.get("code").equals(200)) {
                                String urlName = "images/" + account + "/" + time + "_";
                                new ImagePutOss().PutOss(getAttachActivity(), urlName, fileList);
                                MainActivity.start(getActivity(), HomeFragment.class);
                                toast("发布成功");
                            }else {
                                toast(data.get("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        fileList.clear();
                        fileList_preview.clear();
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        fileList.clear();
                        fileList_preview.clear();
                    }
                });
    }

    private void ClickPic(ImageView imageView, int i) {
        imageView.setOnClickListener(v -> ImagePreviewActivity.start(getActivity(), String.valueOf(fileList_preview.get(i))));
    }

    private void Remove(RelativeLayout relativeLayout, int i, ImageView imageView_small, Bitmap bitmap) {
        imageView_small.setOnClickListener(v -> {
            //删除图片
            if (i <= 2)
                Osslist1.removeView(relativeLayout);
            if (i > 2 && i <= 5)
                Osslist2.removeView(relativeLayout);
            if (i > 5 && i <= 8)
                Osslist3.removeView(relativeLayout);
            for (int j = 0; j < fileList.size(); j++) {
                if (fileList.get(j) == bitmap) {
                    fileList.remove(j);
                    fileList_preview.remove(j);
                    DisplayPic();
                }
            }
        });
    }

}
