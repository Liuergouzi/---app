package com.hjq.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.demo.R;

public class HintSingleDialog extends Dialog implements View.OnClickListener {

    private TextView mTitle, mContent;
    private TextView mCancel;

    private Context mContext;
    private String content;
    private OncloseListener listener;
    private String negativeName;
    private String title;

    public HintSingleDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public HintSingleDialog(@NonNull Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public HintSingleDialog(@NonNull Context context, int themeResId, OncloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    public HintSingleDialog(@NonNull Context context, int themeResId, String content, OncloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    protected HintSingleDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    /**
     * 设置弹框标题
     *
     * @param title 标题内容
     * @return
     */
    public HintSingleDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置弹框的提示内容
     *
     * @param content 弹框的提示内容
     * @return
     */
    public HintSingleDialog setContent(String content) {
        this.content = content;
        return this;
    }


    /**
     * 设置弹框取消键的内容
     *
     * @param name 取消键显示内容
     * @return
     */
    public HintSingleDialog setNegativeButton(String name) {
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hint_single_dialog);
        setCanceledOnTouchOutside(false);
        mTitle = findViewById(R.id.dialog_title_2);
        mTitle.setText(title);
        mContent = findViewById(R.id.dialog_content_2);
        mContent.setText(content);
        mCancel = findViewById(R.id.cancel_2);
        mCancel.setText(negativeName);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_2:
                if (listener != null) {
                    //Toast.makeText(v.getContext(),"唐山大哥一个回首掏便拿起啤酒瓶，当场向你砸了过来",Toast.LENGTH_SHORT).show();
                    listener.onClick(false);
                }
                this.dismiss();
                break;
        }
    }

    public interface OncloseListener {
        void onClick(boolean confirm);
    }
}
