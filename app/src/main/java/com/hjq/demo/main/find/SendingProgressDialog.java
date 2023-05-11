package com.hjq.demo.main.find;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;

/**
 * @author sang
 * @version 创建时间：2014-7-10 上午9:25:00 类说明
 */
public class SendingProgressDialog {
    public ProgressDialog dialog;
    private final String message = "正在发送中,请稍候...";

    public SendingProgressDialog(Context paramContext) {
        this(paramContext, null);
    }

    public SendingProgressDialog(Context paramContext, String paramString) {

        dialog = new ProgressDialog(paramContext){

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if(this.isShowing()){
                    return false;
                }
                return super.onKeyDown(keyCode, event);
            }

        };
        this.dialog.setCancelable(true);
        if (paramString == null) {
            this.dialog.setMessage(message);
        } else {
            this.dialog.setMessage(paramString);
        }
    }

    public void start() {
        try {
            this.dialog.show();
        } catch (Exception e) {
        }

    }

    public void stop() {
        try {
            if (this.dialog != null) {
                this.dialog.dismiss();
                this.dialog.cancel();
            }
        } catch (Exception e) {
        }
    }
}
