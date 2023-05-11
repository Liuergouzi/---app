package com.hjq.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.demo.R;

public class HintDialog extends Dialog implements View.OnClickListener {

    private TextView mTitle, mContent;
    private TextView mConfirm, mCancel;

    private Context mContext;
    private String content;
    private OncloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    public HintDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public HintDialog(@NonNull Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public HintDialog(@NonNull Context context, int themeResId, OncloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    public HintDialog(@NonNull Context context, int themeResId, String content, OncloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    protected HintDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    /**
     * 设置弹框标题
     *
     * @param title 标题内容
     * @return
     */
    public HintDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置弹框的提示内容
     *
     * @param content 弹框的提示内容
     * @return
     */
    public HintDialog setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置弹框确认键的内容
     *
     * @param name 确认键显示内容
     * @return
     */
    public HintDialog setPositiveButton(String name) {
        this.positiveName = name;
        return this;
    }

    /**
     * 设置弹框取消键的内容
     *
     * @param name 取消键显示内容
     * @return
     */
    public HintDialog setNegativeButton(String name) {
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hint_dialog);
        setCanceledOnTouchOutside(false);
        mTitle = findViewById(R.id.dialog_title);
        mTitle.setText(title);
        mContent = findViewById(R.id.dialog_content);
        mContent.setText(content);
        mConfirm = findViewById(R.id.confirm);
        mConfirm.setText(positiveName);
        mCancel = findViewById(R.id.cancel);
        mCancel.setText(negativeName);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                if (listener != null) {
                    listener.onClick(true);
                }
                this.dismiss();
                break;
            case R.id.cancel:
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
