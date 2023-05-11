package com.hjq.demo.overall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class GetPic {

    public List<String> GetImagesPath(Context context){
        List<String> paths=new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //获取图片的名称
            //@SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            // 获取图片的绝对路径
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(column_index);
            paths.add(path);
        }
//        List<Map<String, Object>> Items = new ArrayList<>();
//        for (int i = 0; i < paths.size(); i++) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("name", paths.get(i));
//            Items.add(map);
//        }
        return paths;
    }
}
