package com.hjq.demo.main.home;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hjq.demo.R;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.dialog.HintDialog;
import com.hjq.demo.http.api.HostFragmentApi;
import com.hjq.demo.http.api.HostPicRandApi;
import com.hjq.demo.http.api.articleLoveUpdateApi;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.image.ImageLookActivity;
import com.hjq.demo.main.MainActivity;
import com.hjq.demo.overall.CurrentTime;
import com.hjq.demo.overall.DataMessage;
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
import java.util.Objects;

import okhttp3.Call;


public class HomeFragmentPicture extends TitleBarFragment<MainActivity> {

    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    List<Picture_Util> mUtilList = new ArrayList<>();

    private Integer total,pageNum=2;
    private int i;
    private Boolean dataIsNull=false;
    private LinearLayout view_more;

    public static HomeFragmentPicture newInstance() {
        return new HomeFragmentPicture();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment_picture;
    }

    @Override
    protected void initView() {
        view_more = findViewById(R.id.view_more);
        view_more.setVisibility(View.GONE);

        mRecyclerView = findViewById(R.id.home_recyclerview);
        mMyAdapter = new MyAdapter();
        //mMyAdapter.setHasStableIds(true);
        //mRecyclerView.setAdapter(mMyAdapter);
        //把recyclerView转成瀑布流布局
        //第1个参数表示一行的列数,第2个参数表示方向
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);


        ((DefaultItemAnimator) Objects.requireNonNull(mRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mRecyclerView.setAdapter(mMyAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                staggeredGridLayoutManager.invalidateSpanAssignments();//防止第一行到顶部有空白
            }
        });


        try {
            getContent(true,1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        RefreshLayout refreshLayout = findViewById(R.id.home_refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            mUtilList.clear();
            try {
                getContent(false,1);
                dataIsNull=false;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            refreshlayout.finishRefresh(500/*,false*/);//传入false表示刷新失败
        });

        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            refreshlayout.finishLoadMore(400/*,false*/);//传入false表示加载失败
            if (!dataIsNull) {
                if (total >= pageNum) {
                    try {
                        getContent(false, pageNum);
                        pageNum++;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        getContentRand();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    dataIsNull=true;
                }
            }else {
                try {
                    getContentRand();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHoder> {

        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.home_recycler_item, null);
            return new MyViewHoder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, @SuppressLint("RecyclerView") final int position) {
            Picture_Util util = mUtilList.get(position);
            setView(util, holder);


            setOnClickListener(holder.tx_love_click, holder.img_pic1);

            holder.tx_love_click.setOnClickListener(v -> {
                if (i % 2 == 0) {
                    updateLoveCount(util.id,"app.s"+util.account+"s", DataMessage.temp_read("account"));
                    holder.tx_love_click.setBackgroundResource(R.drawable.ic_love_red);
                } else {
                    holder.tx_love_click.setBackgroundResource(R.drawable.ic_love_black);
                }
                i = i + 1;
            });

            holder.img_pic1.setOnClickListener(v -> {
                String img_url;
                ArrayList<String> images = new ArrayList<>();

                int i = util.imageSize - 1;
                while (i >= 0) {
                    if (util.account.length() > 16) {
                        img_url = util.account;
                    }else {
                        img_url = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_" + i + ".jpg";
                    }
                    images.add(img_url);
                    i--;
                }
                ImageLookActivity.start(getAttachActivity(), images, images.size() - 1, util);
            });

        }

        @Override
        public void onViewRecycled(@NonNull MyViewHoder holder) {

            GlideApp.with(HomeFragmentPicture.this).clear(holder.img_pic1);
            holder.tx_text.setText("");
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {
            return mUtilList.size();
        }

        class MyViewHoder extends RecyclerView.ViewHolder {

            ImageView img_head, img_pic1, tx_love_click;
            TextView tx_name, tx_text, tx_love, tx_time;

            public MyViewHoder(View itemView) {
                super(itemView);
                img_head = itemView.findViewById(R.id.recyler_head);
                img_pic1 = itemView.findViewById(R.id.recyler_pic1);
                tx_name = itemView.findViewById(R.id.recyler_name);
                tx_love = itemView.findViewById(R.id.recyler_love_text);
                tx_text = itemView.findViewById(R.id.recyler_text);
                tx_love_click = itemView.findViewById(R.id.recyler_love);
                tx_time = itemView.findViewById(R.id.recyler_time);
            }
        }
    }


    @SuppressLint("SetTextI18n")
    public void setView(Picture_Util util, MyAdapter.MyViewHoder holder) {
        if (!util.content.equals(""))
            holder.tx_text.setText(util.content);
        else
            holder.tx_text.setText("此用户太懒了，一句话都肯说");

        holder.tx_time.setText(util.time.substring(5, 16));

        if (util.head.equals(""))
            holder.img_head.setBackgroundResource(R.drawable.ic_boy);
        else
            Glide.with(HomeFragmentPicture.this).asBitmap().load(util.head).into(holder.img_head);

        if (util.name.equals(""))
            holder.tx_name.setText("未命名用户");
        else
            holder.tx_name.setText(util.name);

        holder.tx_love.setText(util.loveCount.toString());

        String url;
        if (util.account.length() > 16) {
            url = util.account;
        }else {
            url = "https://47image.oss-cn-heyuan.aliyuncs.com/images/" + util.account + "/" + util.time + "_0.jpg";
        }
        imageWidth(url, holder.img_pic1);

    }


    public void imageWidth(final String imageUrl, final ImageView imageView) {

        //手机宽高
        Display display = getAttachActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = width / 3 + width / 10;
        imageView.setLayoutParams(params);

        Glide.with(this)
                .load(imageUrl)
                .transform(new RoundedCorners((int) getResources().getDimension(R.dimen.dp_5)))
                .thumbnail(0.4f)
                .override(300, 300)
//                .skipMemoryCache(true) // 不使用内存缓存
//                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(imageView);

    }


    public void showDialog() {
        HintDialog dialog = new HintDialog(getAttachActivity(), R.style.hint_dialog,
                confirm -> {
                    if (!confirm) {
                        // TODO:
                        toast("呦吼~");
                        try {
                            getContentRand();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                });
        dialog.setTitle("轮子哥提示：");
        dialog.setContent("没有更多了~");
        dialog.setPositiveButton("确定");
        dialog.setNegativeButton("我可不信你的鬼话！");
        dialog.show();
    }


    /**
     * 获取数据
     */
    public void getContent(Boolean is_view, Integer pageNum) throws NoSuchAlgorithmException {
        EasyHttp.post(this)
                .api(new HostFragmentApi()
                        .setPageNum(pageNum)
                        .setSgin(new SecureSgin().md5("APPLE_HA"))
                )
                .request(
                        new HttpCallback<JSONObject>(this) {
                            @Override
                            public void onStart(Call call) {
                                if (is_view) view_more.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onEnd(Call call) {
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
                                        util.id=jsonObject.getInt("id");
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
                                if (is_view) view_more.setVisibility(View.GONE);
                            }
                        });
    }


    public void getContentRand() throws NoSuchAlgorithmException {
        EasyHttp.get(this)
                .api(new HostPicRandApi())
                .request(new HttpCallback<String>(this) {
                    @Override
                    public void onStart(Call call) {
                    }

                    @Override
                    public void onEnd(Call call) {
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(String data) {
                        try {
                            JSONObject json1 = new JSONObject(data);
                            JSONObject json2 = new JSONObject(String.valueOf(json1.get("result")));
                            JSONArray json3 = new JSONArray(String.valueOf(json2.get("list")));
                            for (i = 0; i < 10; i++) {
                                JSONObject json4 = new JSONObject(String.valueOf(json3.getJSONObject(i)));
                                Picture_Util util = new Picture_Util();
                                util.imageSize = 1;
                                util.commentCount = 0;
                                util.loveCount = 0;
                                util.name = "";
                                util.head = "";
                                util.time = new CurrentTime().getTimeNone();
                                util.account = (String) json4.get("url");
                                util.content = (String) json4.get("title");
                                mUtilList.add(util);
                            }
                            mMyAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            toast("没有更多了~");
                            e.printStackTrace();
                        }
//                        mMyAdapter.notifyItemRangeInserted(10,10);
                    }
                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }

    public void updateLoveCount(Integer id,String account,String name){
        EasyHttp.post(this)
                .api(new articleLoveUpdateApi()
                        .setId(id)
                        .setAccount(account)
                        .setName(name)
                )
                .request(new HttpCallback<String>(this) {
                    @Override
                    public void onStart(Call call) {
                    }

                    @Override
                    public void onEnd(Call call) {
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSucceed(String data) {

                    }
                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }

    @Override
    protected void initData() {
    }

}





