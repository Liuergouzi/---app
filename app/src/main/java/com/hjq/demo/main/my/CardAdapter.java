package com.hjq.demo.main.my;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.hjq.demo.R;
import com.hjq.demo.myitem.AboutActivity;
import com.hjq.demo.myitem.BalanceActivity;
import com.hjq.demo.myitem.BrowserActivity;
import com.hjq.demo.myitem.ClockActivity;
import com.hjq.demo.myitem.MyShareActivity;
import com.hjq.demo.myitem.MyShareItemActivity;
import com.hjq.demo.myitem.TaskActivity;

import java.util.List;

public class CardAdapter extends Adapter {
    private final Context mContext;
    private final List<String> mStrings;

    public CardAdapter(Context mContext, List<String> mStrings) {
        this.mContext = mContext;
        this.mStrings = mStrings;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_fragment_recycler, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        CardViewHolder myViewHolder = (CardViewHolder) viewHolder;

        if (Integer.parseInt(mStrings.get(i)) % 2 == 0) {
            myViewHolder.linearLayout2.setVisibility(View.GONE);
            myViewHolder.linearLayout1.setVisibility(View.VISIBLE);

            myViewHolder.mfm_1.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, TaskActivity.class);
                mContext.startActivity(intent);
            });

            myViewHolder.mfm_2.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ClockActivity.class);
                mContext.startActivity(intent);
            });

            myViewHolder.mfm_3.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, MyShareItemActivity.class);
                mContext.startActivity(intent);
            });

            myViewHolder.mfm_4.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, BalanceActivity.class);
                mContext.startActivity(intent);
            });

            myViewHolder.mfm_6.setOnClickListener(v -> {
                BrowserActivity.start(mContext, "http://ctrlc.cc/kefu/index.html");
            });

            myViewHolder.mfm_7.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, MyShareActivity.class);
                mContext.startActivity(intent);
            });

            myViewHolder.mfm_8.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, AboutActivity.class);
                mContext.startActivity(intent);
            });

        } else {
            myViewHolder.linearLayout2.setVisibility(View.VISIBLE);
            myViewHolder.linearLayout1.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return mStrings.size();
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout1;
        public LinearLayout linearLayout2;
        public ImageView mfm_1, mfm_2, mfm_3, mfm_4, mfm_5, mfm_6, mfm_7, mfm_8, mfm_9, mfm_10, mfm_11, mfm_12, mfm_13;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout1 = itemView.findViewById(R.id.my_fragment_recycler_linear1);
            linearLayout2 = itemView.findViewById(R.id.my_fragment_recycler_linear2);
            mfm_1 = itemView.findViewById(R.id.mfm_1);
            mfm_2 = itemView.findViewById(R.id.mfm_2);
            mfm_3 = itemView.findViewById(R.id.mfm_3);
            mfm_4 = itemView.findViewById(R.id.mfm_4);
            mfm_5 = itemView.findViewById(R.id.mfm_5);
            mfm_6 = itemView.findViewById(R.id.mfm_6);
            mfm_7 = itemView.findViewById(R.id.mfm_7);
            mfm_8 = itemView.findViewById(R.id.mfm_8);
            mfm_9 = itemView.findViewById(R.id.mfm_9);
            mfm_10 = itemView.findViewById(R.id.mfm_10);
            mfm_11 = itemView.findViewById(R.id.mfm_11);
            mfm_12 = itemView.findViewById(R.id.mfm_12);
            mfm_13 = itemView.findViewById(R.id.mfm_13);
        }
    }
}