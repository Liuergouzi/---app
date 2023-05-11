package com.hjq.demo.overall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class DataMessage {
    /**
     * 1.
     * 通过file文件保存用户信息
     *
     */
    public static boolean saveInfo( String username, String width,String height,String pwd, String phone, String cardstatu) {
        String info = username + "##" + width + "##" + pwd + "##" + height+"##phone"+phone+"##card_status"+cardstatu;
        File file = new File("data/data/com.hjq.demo/info.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //将字符串写入到文件中
            fos.write(info.getBytes());
            //关闭数据流
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 1.
     * 读取保存再.txt文件中的用户名、密码
     *
     */
    public static String[] readInfo() {
        try {
            File file = new File("data/data/com.hjq.demo/info.txt");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String temp = reader.readLine();
            String[] result;
            result = temp.split("##");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addInfo(String text) {
        File file = new File("data/data/com.hjq.demo/info.txt");
        String info = "##" + text;
        try {
            FileOutputStream fos = new FileOutputStream(file,true);
            //将字符串写入到文件中
            fos.write(info.getBytes());
            //关闭数据流
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean tempSave(String context) {
        try {
            File file = new File("data/data/com.hjq.demo/temp_save.txt");
            FileOutputStream fos = new FileOutputStream(file);
            //将字符串写入到文件中
            fos.write(context.getBytes());
            //关闭数据流
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String tempRead() {
        try {
            File file = new File("data/data/com.hjq.demo/temp_save.txt");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean temp_save(String context,String fileName) {
        try {
            File file = new File("data/data/com.hjq.demo/temp_"+fileName+".txt");
            FileOutputStream fos = new FileOutputStream(file);
            //将字符串写入到文件中
            fos.write(context.getBytes());
            //关闭数据流
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String temp_read(String fileName) {
        try {
            File file = new File("data/data/com.hjq.demo/temp_"+fileName+".txt");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean temp_save_phone(String context) {
        String info = context + "##";
        File file = new File("data/data/com.hjq.demo/temp_phone.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //将字符串写入到文件中
            fos.write(info.getBytes());
            //关闭数据流
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String[] temp_read_phone() {
        File file = new File("data/data/com.hjq.demo/temp_phone.txt");
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String temp = reader.readLine();
            return temp.split("##");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
