package com.hjq.demo.main.my;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hjq.demo.R;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.dialog.HintDialog;
import com.hjq.demo.http.api.MyFragmentAllApi;
import com.hjq.demo.http.api.MyFragmentDeleteApi;
import com.hjq.demo.image.ImageLookActivity;
import com.hjq.demo.main.MainActivity;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.GetScreenSize;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.demo.util.Picture_Util;
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

public class MyFragmentPicture extends TitleBarFragment<MainActivity> {

    private LinearLayout view_more;//加载
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    private Integer total,pageNum=2,width;
    private int i;
    List<Picture_Util> mUtilList = new ArrayList<>();

    public static MyFragmentPicture newInstance() {
        return new MyFragmentPicture();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.my_fragment_picture;
    }

    @Override
    protected void initView() {

        view_more = findViewById(R.id.view_more);
        view_more.setVisibility(View.GONE);
        mRecyclerView = findViewById(R.id.my_recyclerview);

        width = new GetScreenSize().getScreenWidth(getAttachActivity());

        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);


        RefreshLayout refreshLayout = findViewById(R.id.my_refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            mUtilList.clear();
            refreshlayout.finishRefresh(800/*,false*/);//传入false表示刷新失败
            try {
                getContentAll(true,1);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });

        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            refreshlayout.finishLoadMore(400/*,false*/);//传入false表示加载失败
                if (total >= pageNum) {
                    try {
                        getContentAll(false, pageNum);
                        pageNum++;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast("全部已加载完毕");
                }
        });

    }

    @Override
    protected void initData() {
        try {
            getContentAll(true,1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {

        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.my_fragment_relist, null);
            return new MyViewHoder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, @SuppressLint("RecyclerView") final int position) {
            Picture_Util util = mUtilList.get(position);
            setView(util, holder);
            holder.tx_love_click.setOnClickListener(v -> {
                if (i % 2 == 0) {
                    holder.tx_love_click.setBackgroundResource(R.drawable.ic_love_red);
                } else {
                    holder.tx_love_click.setBackgroundResource(R.drawable.ic_love_black);
                }
                i = i + 1;
            });

            holder.pic_click.setOnClickListener(v -> {
                String img_url;
                ArrayList<String> images = new ArrayList<>();
                if (util.account.length() > 18) {
                    img_url = util.account;
                    images.add(img_url);
                } else {
                    int i = util.imageSize - 1;
                    while (i >= 0) {
                        img_url = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_" + i + ".jpg";
                        images.add(img_url);
                        i--;
                    }
                }
                ImageLookActivity.start(getAttachActivity(), images, images.size() - 1, util);
            });
            holder.img_gift.setOnClickListener(v -> showDialogD(mUtilList.get(position).time, holder));

        }

        @Override
        public void onViewRecycled(@NonNull MyViewHoder hoder) {
            Glide.with(getAttachActivity()).clear(hoder.img_pic1);
            Glide.with(getAttachActivity()).clear(hoder.img_pic2);
            Glide.with(getAttachActivity()).clear(hoder.img_pic3);
            hoder.tx_pic3.setText("");
            hoder.tx_pic3.setBackgroundColor(Color.parseColor("#00000000"));
            super.onViewRecycled(hoder);
        }

        @Override
        public int getItemCount() {
            return mUtilList.size();
        }
    }


    class MyViewHoder extends RecyclerView.ViewHolder {
        ConstraintLayout rootview;
        LinearLayout pic_click;
        ImageView img_pic1, img_pic2, img_pic3, tx_conment_click, tx_love_click, img_gift;
        TextView tx_pic3, tx_text, tx_love, tx_conment, tx_time;

        public MyViewHoder(@NonNull View itemView) {
            super(itemView);
            rootview = itemView.findViewById(R.id.rootview);
            pic_click = itemView.findViewById(R.id.pic_click);
            img_pic1 = itemView.findViewById(R.id.recyler_pic1);
            img_pic2 = itemView.findViewById(R.id.recyler_pic2);
            img_pic3 = itemView.findViewById(R.id.recyler_pic3);
            img_gift = itemView.findViewById(R.id.recyler_gift);
            tx_pic3 = itemView.findViewById(R.id.recyler_pic3_text);
            tx_conment = itemView.findViewById(R.id.recyler_conment_text);
            tx_love = itemView.findViewById(R.id.recyler_love_text);
            tx_text = itemView.findViewById(R.id.recyler_text);
            tx_love_click = itemView.findViewById(R.id.recyler_love);
            tx_conment_click = itemView.findViewById(R.id.recyler_conment);
            tx_time = itemView.findViewById(R.id.recyler_time);
            img_pic1.getLayoutParams().width = width / 4 + width / 25;
            img_pic1.getLayoutParams().height = width / 4 + width / 25;
            img_pic2.getLayoutParams().width = width / 4 + width / 25;
            img_pic2.getLayoutParams().height = width / 4 + width / 25;
            img_pic3.getLayoutParams().width = width / 4 + width / 25;
            img_pic3.getLayoutParams().height = width / 4 + width / 25;
            tx_pic3.getLayoutParams().width = width / 4 + width / 25;
            tx_pic3.getLayoutParams().height = width / 4 + width / 25;
        }
    }

    @SuppressLint("SetTextI18n")
    public void setView(Picture_Util util, MyViewHoder holder) {
        if (!util.content.equals(""))
            holder.tx_text.setText(util.content);
        else
            holder.tx_text.setText("此用户太懒了，一句话都肯说");

        holder.tx_time.setText(util.time);

        if (util.commentCount == null)
            holder.tx_conment.setText("0");
        else
            holder.tx_conment.setText(String.valueOf(util.commentCount));

        if (util.loveCount == null)
            holder.tx_love.setText("0");
        else
            holder.tx_love.setText(String.valueOf(util.loveCount));

        int index = util.imageSize;
        if (index == 0) {
            holder.pic_click.setVisibility(View.GONE);
        } else if (index == 1) {
            String url = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_0.jpg?x-oss-process=image/resize,w_250";
            Glide.with(this).asBitmap().load(url).thumbnail(0.15f).centerCrop().override(250, 250).into(holder.img_pic1);
        } else if (index == 2) {
            String url1 = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_0.jpg?x-oss-process=image/resize,w_250";
            String url2 = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_1.jpg?x-oss-process=image/resize,w_250";
            Glide.with(this).asBitmap().load(url1).thumbnail(0.15f).centerCrop().override(250, 250).into(holder.img_pic1);
            Glide.with(this).asBitmap().load(url2).thumbnail(0.15f).centerCrop().override(250, 250).into(holder.img_pic2);
        } else if (index >= 3) {
            String url1 = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_0.jpg?x-oss-process=image/resize,w_250";
            String url2 = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_1.jpg?x-oss-process=image/resize,w_250";
            String url3 = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_2.jpg?x-oss-process=image/resize,w_250";
            Glide.with(this).asBitmap().load(url1).thumbnail(0.15f).centerCrop().override(250, 250).into(holder.img_pic1);
            Glide.with(this).asBitmap().load(url2).thumbnail(0.15f).centerCrop().override(250, 250).into(holder.img_pic2);
            Glide.with(this).asBitmap().load(url3).thumbnail(0.15f).centerCrop().override(250, 250).into(holder.img_pic3);
            if (index > 3) {
                holder.tx_pic3.setText("+" + (index - 3));
                holder.tx_pic3.setBackgroundColor(Color.parseColor("#73AAAAAA"));
            }
        }

    }


    public void getContentAll(Boolean is_view,Integer pageNum) throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new MyFragmentAllApi()
                        .setAccount(account)
                        .setPageNum(pageNum)
                        .setSgin(new SecureSgin().md5(account))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @Override
                    public void onStart(Call call) {
                        if (is_view)
                            view_more.setVisibility(View.VISIBLE);
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            total = (Integer) data.get("total");
                            JSONArray jsonArray = new JSONArray(String.valueOf(data.get("data")));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(i)));
                                Picture_Util util = new Picture_Util();
                                util.imageSize = jsonObject.getInt("imageSize");
                                util.commentCount = jsonObject.getInt("commentCount");
                                util.loveCount = jsonObject.getInt("loveCount");
                                util.name = jsonObject.getString("name");
                                util.head = jsonObject.getString("head");
                                util.time = jsonObject.getString("time");
                                util.account = jsonObject.getString("account");
                                util.content = jsonObject.getString("content");
                                mUtilList.add(util);
                            }
                            mMyAdapter.notifyDataSetChanged();
                            if (is_view) view_more.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            if (is_view) view_more.setVisibility(View.GONE);
                            try {
                                toast(data.get("message"));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        if (is_view)
                            view_more.setVisibility(View.GONE);
                    }
                });
    }

    public void showDialogD(String time, MyViewHoder hoder) {
        HintDialog dialog = new HintDialog(getAttachActivity(), R.style.hint_dialog,
                confirm -> {
                    if (confirm) {
                        // TODO:
                        hoder.rootview.removeAllViews();
                        try {
                            deleteContent(time);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                });
        dialog.setTitle("系统提示：");
        dialog.setContent("您确定要删除吗？删除后无法恢复");
        dialog.setPositiveButton("确定");
        dialog.setNegativeButton("取消");
        dialog.show();
    }

    /**
     * 删除
     */
    public void deleteContent(String time) throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new MyFragmentDeleteApi()
                        .setAccount(account)
                        .setTime(time)
                        .setSgin(new SecureSgin().md5(time))
                ).request(new HttpCallback<JSONObject>(this) {
                    @Override
                    public void onStart(Call call) {
                    }
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {
                            toast(data.get("message"));
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

}
