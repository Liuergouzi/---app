package com.hjq.demo.main.notice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.demo.R;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.api.NoticeFragmentApi;
import com.hjq.demo.main.MainActivity;
import com.hjq.demo.overall.GetScreenSize;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.demo.util.Notice_Util;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class NoticeFragment extends TitleBarFragment<MainActivity> {

    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    List<Notice_Util> mUtilList = new ArrayList<>();
    int width;
    private final int maxLine = 3;
    private SpannableString elipseString;//收起的文字
    private SpannableString notElipseString;//展开的文字

    public static NoticeFragment newInstance() {
        return new NoticeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.notice_fragment;
    }

    @Override
    protected void initView() {

        mRecyclerView = findViewById(R.id.notice_recyclerview);

        //设置间距
        width = new GetScreenSize().getScreenWidth(getAttachActivity());
        // mRecyclerView.setPadding(width / 15, 0, width / 15, 0);


        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);


        RefreshLayout refreshLayout = findViewById(R.id.notice_refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            mUtilList.clear();
            refreshlayout.finishRefresh(500/*,false*/);//传入false表示刷新失败

        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            refreshlayout.finishLoadMore(400/*,false*/);//传入false表示加载失败

        });
    }

    @Override
    protected void initData() {
        try {
            getNoticeData();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {
        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view1 = View.inflate(getContext(), R.layout.notice_recycler_item, null);
            return new MyViewHoder(view1);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, @SuppressLint("RecyclerView") final int position) {
            Notice_Util util = mUtilList.get(position);
            if (position == 0) holder.notice_title_re.setText("最新");
            setView(util, holder);
        }

        @Override
        public void onViewRecycled(@NonNull MyViewHoder holder) {
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {
            return mUtilList.size();
        }
    }

    static class MyViewHoder extends RecyclerView.ViewHolder {

        private final TextView notice_title;
        private final TextView notice_content;
        private final TextView notice_time;
        private final TextView notice_title_re;

        public MyViewHoder(@NonNull View itemView) {
            super(itemView);
            notice_title = itemView.findViewById(R.id.notice_title);
            notice_content = itemView.findViewById(R.id.notice_content);
            notice_time = itemView.findViewById(R.id.notice_time);
            notice_title_re = itemView.findViewById(R.id.notice_title_re);
        }
    }

    public void setView(Notice_Util util, MyViewHoder holder) {
        holder.notice_title.setText(util.title);
        holder.notice_time.setText(util.time);
        holder.notice_content.setText(util.content);

        text(holder.notice_content, util.content);

    }

    /**
     * 异步任务
     */
    public void getNoticeData() throws NoSuchAlgorithmException {
        EasyHttp.post(this)
                .api(new NoticeFragmentApi()
                        .setPageNum(1)
                        .setSgin(new SecureSgin().md5("net_index8"))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @Override
                    public void onStart(Call call) {
                    }

                    @Override
                    public void onEnd(Call call) {
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            JSONArray jsonArray = new JSONArray(String.valueOf(data.get("data")));
                            for (int i=0; i <jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(i)));
                                Notice_Util util = new Notice_Util();
                                util.title =jsonObject.getString("title");
                                util.content = jsonObject.getString("content");
                                util.time = jsonObject.getString("time");
                                mUtilList.add(util);
                            }
                            mMyAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            toast("加载错误");
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }


    protected void text(TextView tv, String content) {

        //获取TextView的画笔对象
        TextPaint paint = tv.getPaint();
        //每行文本的布局宽度
        //int width =tv.getWidth();
        int width = getResources().getDisplayMetrics().widthPixels - dip2px(requireContext(), 60);
        //实例化StaticLayout 传入相应参数
        StaticLayout staticLayout = new StaticLayout(content, paint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        //判断content是行数是否超过最大限制行数3行
        if (staticLayout.getLineCount() > maxLine) {

            //定义展开后的文本内容
            String string1 = content + "    收起";
            notElipseString = new SpannableString(string1);
            //给收起两个字设成蓝色
            notElipseString.setSpan(new ForegroundColorSpan(Color.parseColor("#4FA2D1")), string1.length() - 2, string1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //获取到第三行最后一个文字的下标
            int index = staticLayout.getLineStart(maxLine) - 1;
            //定义收起后的文本内容
            String substring = content.substring(0, index - 2) + "......" + "  更多";
            elipseString = new SpannableString(substring);
            //给查看全部设成蓝色
            elipseString.setSpan(new ForegroundColorSpan(Color.parseColor("#4FA2D1")), substring.length() - 5, substring.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //设置收起后的文本内容
            tv.setText(elipseString);


            tv.setOnClickListener(v -> {
                //   if (v.getId() == R.id.notice_content) {
                if (v.isSelected()) {
                    //如果是收起的状态
                    //定义展开后的文本内容
                    String string11 = content + "    收起";
                    notElipseString = new SpannableString(string11);
                    //给收起两个字设成蓝色
                    notElipseString.setSpan(new ForegroundColorSpan(Color.parseColor("#4FA2D1")), string11.length() - 2, string11.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tv.setText(notElipseString);
                    tv.setSelected(false);
                } else {
                    //如果是展开的状态
                    //获取到第三行最后一个文字的下标
                    int index1 = staticLayout.getLineStart(maxLine) - 1;
                    //定义收起后的文本内容
                    String substring1 = content.substring(0, index1 - 2) + "......" + "  更多";
                    elipseString = new SpannableString(substring1);
                    //给查看全部设成蓝色
                    elipseString.setSpan(new ForegroundColorSpan(Color.parseColor("#4FA2D1")), substring1.length() - 5, substring1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    //设置收起后的文本内容
                    tv.setText(elipseString);

                    tv.setText(elipseString);
                    tv.setSelected(true);
                }
                //   }
            });
            //将textview设成选中状态 true用来表示文本未展示完全的状态,false表示完全展示状态，用于点击时的判断
            tv.setSelected(true);
        } else {
            //没有超过 直接设置文本
            tv.setText(content);
            tv.setOnClickListener(null);
        }

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context mContext, float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}






