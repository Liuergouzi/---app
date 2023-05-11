package com.hjq.demo.myitem;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.InvitationItemApi;
import com.hjq.demo.overall.CurrentTime;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.demo.util.MyShare_Util;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MyShareItemActivity extends AppActivity {

    MyShareAdapter mMyAdapter;
    RecyclerView mRecyclerView;
    List<MyShare_Util> mUtilList = new ArrayList<>();
    private String TOADY_TIME;
    private TextView today_count;
    private TextView all_count;
    private int pageNum=2,total;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_myshare_item;
    }

    @Override
    protected void initView() {

        mMyAdapter = new MyShareAdapter();
        mRecyclerView = findViewById(R.id.my_share_recyclerview);
        mRecyclerView.setAdapter(mMyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        today_count=findViewById(R.id.my_share_today);
        all_count=findViewById(R.id.my_share_all);

        RefreshLayout refreshLayout = findViewById(R.id.my_shareItem_refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            mUtilList.clear();
            try {
                getShareAll(1);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            refreshlayout.finishRefresh(500/*,false*/);//传入false表示刷新失败
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            refreshlayout.finishLoadMore(400/*,false*/);//传入false表示加载失败
            if (total>=pageNum){
                try {
                    getShareAll(pageNum);
                    pageNum++;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }else {
                toast(R.string.common_no_more_data);
            }
        });
    }

    @Override
    protected void initData() {
        TOADY_TIME=new CurrentTime().getTimeNone().substring(0,10);
        try {
            getShareAll(1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    class MyShareAdapter extends RecyclerView.Adapter<MyShareAdapterHolder> {
        @NonNull
        @Override
        public MyShareAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myshare_recycler, parent, false);//解决宽度不能铺满
            return new MyShareAdapterHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull MyShareAdapterHolder holder, @SuppressLint("RecyclerView") final int position) {
            MyShare_Util util = mUtilList.get(position);
            setView(util, holder);
        }

        @Override
        public void onViewRecycled(@NonNull MyShareAdapterHolder holder) {
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {
            return mUtilList.size();
        }
    }

    static class MyShareAdapterHolder extends RecyclerView.ViewHolder {

        private final ImageView head;
        private final TextView name;
        private final TextView time;
        private final TextView invitation;

        public MyShareAdapterHolder(@NonNull View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.my_share_head);
            name = itemView.findViewById(R.id.my_share_name);
            time = itemView.findViewById(R.id.my_share_time);
            invitation = itemView.findViewById(R.id.my_share_invitation);
        }
    }

    public void setView(MyShare_Util util, MyShareAdapterHolder holder) {

        if (!util.head.equals("null"))
            Glide.with(this).asBitmap().load(util.head).into(holder.head);
        else
            Glide.with(this).asBitmap().load(R.drawable.ic_boy).into(holder.head);

        if (!util.name.equals("null"))
            holder.name.setText(util.name);
        else
            holder.name.setText("未命名用户");

        holder.invitation.setText("好友id："+util.invitation);
        holder.time.setText(util.time);
    }


    public void getShareAll(int pageNum) throws NoSuchAlgorithmException {
        String account=DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new InvitationItemApi()
                        .setPageNum(pageNum)
                        .setAccount(account)
                        .setSgin(new SecureSgin().md5(account))
                )
                .request(new HttpCallback<JSONObject>(this) {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(JSONObject data) {
                        try {

                            JSONArray jsonArray = new JSONArray(String.valueOf(data.get("data")));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(i)));
                                MyShare_Util util = new MyShare_Util();
                                util.head = jsonObject.getString("head");
                                util.name = jsonObject.getString("name");
                                util.invitation = jsonObject.getString("ma");
                                util.time = jsonObject.getString("dateTime");
                                mUtilList.add(util);
                            }
                            mMyAdapter.notifyDataSetChanged();
                            total=data.getInt("total");
                            today_count.setText(data.getString("dayTotal"));
                            all_count.setText(data.getString("invitationTotal"));
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
