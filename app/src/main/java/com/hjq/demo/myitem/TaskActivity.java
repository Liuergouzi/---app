package com.hjq.demo.myitem;


import android.annotation.SuppressLint;
import android.view.View;

import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.dialog.HintDialog;
import com.hjq.demo.dialog.HintSingleDialog;
import com.hjq.demo.http.api.TaskPackApi;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public class TaskActivity extends AppActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_taskpack;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.taskpack_btn1, R.id.taskpack_btn2, R.id.taskpack_btn3, R.id.taskpack_btn4, R.id.taskpack_btn5);
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.taskpack_btn1) {
            showDialog("1");
        } else if (viewId == R.id.taskpack_btn2) {
            showDialog("2");
        } else if (viewId == R.id.taskpack_btn3) {
            showDialog("3");
        } else if (viewId == R.id.taskpack_btn4) {
            showDialog("4");
        } else if (viewId == R.id.taskpack_btn5) {
            showDialog("5");
        }

    }

    public void showDialog(String type) {
        HintDialog dialog = new HintDialog(this, R.style.hint_dialog,
                confirm -> {
                    if (confirm) {
                        try {
                            putTaskPack(type);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                });
        dialog.setTitle("提示：");
        dialog.setContent("您确定要兑换吗?");
        dialog.setPositiveButton("确定");
        dialog.setNegativeButton("取消");
        dialog.show();
    }


    public void putTaskPack(String type) throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new TaskPackApi()
                        .setAccount(account)
                        .setType(type)
                        .setSgin(new SecureSgin().md5(account))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            if (data.getString("code").equals("200")){
                                showDialogOne();
                            }else {
                                toast(data.getString("message"));
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
        HintSingleDialog dialog = new HintSingleDialog(getActivity(), R.style.hint_dialog,
                confirm -> {
                });
        dialog.setTitle("系统提示");
        dialog.setContent("兑换成功");
        dialog.setNegativeButton("确定");
        dialog.show();
    }

}