package com.hjq.demo.myitem;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.BalanceApi;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SecureSgin;
import com.hjq.demo.util.Balance_Util;
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

public class BalanceActivity extends AppActivity {

    PieChart pie_chart1;
    private TextView balance_twink, balance_date;
    float count_date = 0;
    List<Balance_Util> mUtilList = new ArrayList<>();
    MyAdapter mMyAdapter;
    RecyclerView mRecyclerView;
    private LinearLayout view_more;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_balance;
    }

    @Override
    protected void initView() {
        view_more = findViewById(R.id.balance_view_more);
        view_more.setVisibility(View.GONE);
        //饼状图实心
        pie_chart1 = findViewById(R.id.pie_chat1);
        show_pie_chart_1();

        mMyAdapter = new MyAdapter();
        mRecyclerView = findViewById(R.id.recyclerview_balance);
        mRecyclerView.setAdapter(mMyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        balance_twink = findViewById(R.id.balance_twink);
        balance_date = findViewById(R.id.balance_countdate);
    }

    @Override
    protected void initData() {
        try {
            getBalanceAll(true,1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        RefreshLayout refreshLayout = findViewById(R.id.balance_refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            mUtilList.clear();
            count_date = 0;
            refreshlayout.finishRefresh(500/*,false*/);//传入false表示刷新失败
            try {
                getBalanceAll(true,1);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
    }

    //设置数据
    private void show_pie_chart_1() {
        //设置每份所占数量
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(8.2f, "任务包"));
        yvals.add(new PieEntry(1.8f, "邀请奖励"));
        // 设置每份的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#56C4FF"));
        colors.add(Color.parseColor("#D3F6F074"));
        PieChartManagger pieChartManagger = new PieChartManagger(pie_chart1);
        pieChartManagger.showRingPieChart(yvals, colors);
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {
        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_balance, parent, false);//解决宽度不能铺满
            return new MyViewHoder(view);


        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, @SuppressLint("RecyclerView") final int position) {
            Balance_Util util = mUtilList.get(position);
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

        private final TextView balance_source;
        private final TextView balance_message;
        private final TextView balance_time;
        private final TextView balance_count;

        public MyViewHoder(@NonNull View itemView) {
            super(itemView);
            balance_source = itemView.findViewById(R.id.balance_source);
            balance_message = itemView.findViewById(R.id.balance_message);
            balance_time = itemView.findViewById(R.id.balance_time);
            balance_count = itemView.findViewById(R.id.balance_count);
        }
    }

    public void setView(Balance_Util util, MyViewHoder holder) {
        holder.balance_count.setText(util.count);
        holder.balance_source.setText(util.source);
        holder.balance_time.setText(util.time);
        holder.balance_message.setText(util.message);
    }


    public void getBalanceAll(Boolean IS_VIEW,Integer pageNum) throws NoSuchAlgorithmException {
        String account = DataMessage.temp_read("account");
        EasyHttp.post(this)
                .api(new BalanceApi()
                        .setAccount(account)
                        .setPageNum(pageNum)
                        .setSgin(new SecureSgin().md5(account))
                )
                .request(new HttpCallback<JSONObject>(this) {
                    @Override
                    public void onStart(Call call) {
                        if (IS_VIEW)
                            view_more.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onEnd(Call call) {
                    }
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onSucceed(JSONObject data) {
                        JSONArray jsonArray = null;
                        try {
//                            DecimalFormat bcc = new DecimalFormat("#.00");
                            balance_twink.setText( data.getString("balance"));
                            jsonArray = new JSONArray(String.valueOf(data.get("data")));
                            for (int i=0; i <jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(i)));
                                Balance_Util util = new Balance_Util();
                                util.count = jsonObject.getString("count");
                                util.message = jsonObject.getString("message");
                                util.source = jsonObject.getString("source");
                                util.time = jsonObject.getString("time");
                                mUtilList.add(util);
                            }
                            mMyAdapter.notifyDataSetChanged();
                            balance_date.setText("今日获得 "+data.getString("dayBalance"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (IS_VIEW) view_more.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        if (IS_VIEW) view_more.setVisibility(View.GONE);
                    }
                });
    }

}