package com.hjq.demo.main.my;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CardLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 自定义LayoutManager核心是摆放控件，所以onLayoutChildren方法是我们要改写的核心
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //缓存
        detachAndScrapAttachedViews(recycler);

        //获取所有item(包括不可见的)个数
        int count = getItemCount();
        //由于我们是倒序摆放，所以初始索引从后面开始
        int initIndex = count - CardConfig.SHOW_MAX_COUNT;
        if (initIndex < 0) {
            initIndex = 0;
        }

        for (int i = initIndex; i < count; i++) {
            //从缓存中获取view
            View view = recycler.getViewForPosition(i);
            //添加到recyclerView
            addView(view);
            //测量一下view
            measureChild(view, 0, 0);

            //居中摆放，getDecoratedMeasuredWidth方法是获取带分割线的宽度，比直接使用view.getWidth()精确
            int realWidth = getDecoratedMeasuredWidth(view);
            int realHeight = getDecoratedMeasuredHeight(view);
            int widthPadding = (int) ((getWidth() - realWidth) / 2f);
            int heightPadding = (int) ((getHeight() - realHeight) / 2f);

            //摆放child
            layoutDecorated(view, widthPadding, heightPadding,
                    widthPadding + realWidth, heightPadding + realHeight);
            //根据索引，来位移和缩放child
            int level = count - i - 1;
            //level范围（CardConfig.SHOW_MAX_COUNT-1）- 0
            // 最下层的不动和最后第二层重叠
            if (level == CardConfig.SHOW_MAX_COUNT - 1) {
                level--;
            }
            view.setTranslationY(level * CardConfig.TRANSLATION_Y);
            view.setScaleX(1 - level * CardConfig.SCALE);
            view.setScaleY(1 - level * CardConfig.SCALE);
        }
    }
}
