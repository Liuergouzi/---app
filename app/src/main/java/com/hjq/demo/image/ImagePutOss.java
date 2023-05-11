package com.hjq.demo.image;

import static com.hjq.demo.app.AppConfig.getPackageName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ImagePutOss {
    /**
     * 将图片上传到oss
     */
    private final List<File> file_delete = new ArrayList<>();

    public void PutOss(Context context, String urlName, List<Bitmap> fileList) {
        String endpoint = "http://oss-cn-heyuan.aliyuncs.com";
        // 填写STS应用服务器地址。
        String stsServer = "";
        // 推荐使用OSSAuthCredentialsProvider。token过期可以及时更新。
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        // 配置类如果不设置，会有默认配置。
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒。
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒。
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个。
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次。
        OSS oss = new OSSClient(context, endpoint, credentialProvider);
        for (int i = 0; i < fileList.size(); i++) {
            String objectKey = urlName + i + ".jpg";
            Uri uri = compressImage(context, fileList.get(i), i);
            PutObjectRequest put = new PutObjectRequest("47image", objectKey, uri);
            try {
                oss.putObject(put);
            } catch (ClientException e) {
                // 客户端异常，例如网络异常等。
                Toast.makeText(context, "发布失败，相关权限未允许", Toast.LENGTH_SHORT).show();
            } catch (ServiceException e) {
                // 服务端异常。
                Toast.makeText(context, "服务异常发布失败，请联系客服", Toast.LENGTH_SHORT).show();
            }
        }
        for (File file : file_delete) {
            Boolean aBoolean = file.delete();
        }
    }

    public void PutOss(Context context, String urlName, Uri uri) {
        String endpoint = "http://oss-cn-heyuan.aliyuncs.com";
        // 填写STS应用服务器地址。
        String stsServer = "";
        // 推荐使用OSSAuthCredentialsProvider。token过期可以及时更新。
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        // 配置类如果不设置，会有默认配置。
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒。
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒。
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个。
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次。
        OSS oss = new OSSClient(context, endpoint, credentialProvider);
        PutObjectRequest put = new PutObjectRequest("47image", urlName, uri);
        try {
            oss.putObject(put);
        } catch (ClientException e) {
            // 客户端异常，例如网络异常等。
            Toast.makeText(context, "发布失败，相关权限未允许", Toast.LENGTH_SHORT).show();
        } catch (ServiceException e) {
            // 服务端异常。
            Toast.makeText(context, "服务异常发布失败，请联系客服", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri compressImage(Context context, Bitmap bitmap, Integer i) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 300) {  //循环判断如果压缩后图片是否大于150kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        //图片名
        String filename = format.format(date);
        ///storage/emulated/0/Android/data/com.hjq.demo/files/Documents/
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename + "_" + i + ".png");
        file_delete.add(file);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
            uri = FileProvider.getUriForFile(context, getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

}


