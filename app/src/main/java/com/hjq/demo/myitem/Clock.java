package com.hjq.demo.myitem;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class Clock extends View {

    private final Paint mPaint;

    private final Paint whitePaint;   //白色画笔
    private final Paint blackPaint;
    private final Paint outlinePaint;   //轮廓画笔
    float mWidth;                 //控件宽
    float mHeight;                //控件高
    float mOutlineR1;
    float mOutlineR2;
    float mOutlineR3;
    float mOutlineR4;
    float mOutlineR5;
    private final String[][] listR1;
    private final String[] listR2;
    private final String[] listR3;
    private final String[] listR4;
    private final String[] listR5;
    private float degrees_tai = 0;          //旋转角度


    public Clock(Context context) {
        this(context, null);
    }

    public Clock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Clock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //路径
        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setColor(Color.WHITE);

        blackPaint = new Paint();
        blackPaint.setAntiAlias(true);
        blackPaint.setColor(Color.BLACK);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        outlinePaint = new Paint();
        outlinePaint.setAntiAlias(true);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setColor(Color.parseColor("#000000"));

        listR1 = new String[][]{{"|","|","|",""},{"¦","¦","¦",""},{"|","¦","¦",""},
                                {"¦","|","¦",""},{"¦","|","|",""},{"¦","¦","|",""},
                                {"|","¦","|",""},{"|","|","¦",""},{"","","",""}, {"☰","☷","☳","☵","☴","☶","☲","☱",""}};

        listR2 = new String[]{"申", "庚", "酉", "辛", "戌", "乾", "亥", "壬", "子", "癸", "丑", "艮",
                "寅", "甲", "卯", "乙", "辰", "巽", "巳", "丙", "午", "丁", "未", "坤", ""};
        listR3 = new String[]{"天厨","天市","天梧","天苑","天命","天宫","天","太乙","天屏","太徽","天广","南极"
                             ,"天常","天境","天开","天汉","少徽","天乙","天魁","天廊","天皇","天铺","天叠","阴光",""};
        listR4 = new String[]{"夬", "乾", "姤", "大过", "鼎", "恒", "巽", "井", "蛊", "升", "诉", "困",
                            "未济", "解", "涣", "坎", "蒙", "师", "遯", "咸", "旅", "小过", "渐", "蹇", "艮",
                            "谦","否","萃","晋","豫","观","比","剥","坤","蕧","颐","屯",
                            "益","震","嘘咳","随","无安","明夷","贵","既济","家人","丰","难","革",
                            "同人","临","损","节","中孚","归妹","睽","兑","履","泰","大畜","需",
                            "小畜","大壮","否","萃","晋","豫","观","比","剥","坤","大有","离",""};
        listR5 = new String[]{"水","木","水","木","土","金","火","土","水","木","土","金",
                              "火","水","木","土","金","火","金","木","水","火","火","土",
                              "火","水","木","土","金","火","金","木","水","火","火","土",
                              "水","木","土","金","火","金","木","水","火","火","土","火",
                              "土","水","木","土","金","火","金","木","水","火","土","火",""};

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    }

    private int measureHeight(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result + getPaddingTop() + getPaddingBottom();
    }

    private int measureWidth(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result + getPaddingLeft() + getPaddingRight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mOutlineR1 = mWidth * 0.17f;
        mOutlineR2 = mWidth * 0.24f;
        mOutlineR3 = mWidth * 0.307f;
        mOutlineR4 = mWidth * 0.371f;
        mOutlineR5 = mWidth * 0.432f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas == null)
            return;
        canvas.drawColor(Color.parseColor("#fff7f9fa"));//填充背景
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);//原点移动到中心

        //绘制各元件，后文会涉及到
        drawCenterInfo(canvas);
        drawOutline(canvas);

    }

    private void drawOutline(Canvas canvas) {
        outlinePaint.setStrokeWidth(mWidth/350);

        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(255);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mWidth * 0.06f);
        mPaint.setColor(Color.parseColor("#000000"));

        //第一层
        canvas.save();
        canvas.rotate(-degrees_tai/4);
        canvas.drawCircle(0, 0, mOutlineR1, outlinePaint);
        for (int i = 0; i <= 8; i++) {
            double angle = 2 * Math.PI / 8 * i+Math.PI / 8 ;
            canvas.drawLine((float) StrictMath.cos(angle) * mWidth / 10, (float) StrictMath.sin(angle) * mWidth / 10,
                    (float) StrictMath.cos(angle) * mOutlineR1, (float) StrictMath.sin(angle) * mOutlineR1, outlinePaint);
            canvas.save();
            float iDeg1 = 360 / 8f * i;
            canvas.rotate(iDeg1,0,0);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            for (int j=0;j<=2;j++)
            canvas.drawText(listR1[i][j],  mOutlineR1*(0.9f-j*0.1f), getCenteredY()+mOutlineR1*0.28f, mPaint);
            canvas.restore();
        }
        canvas.restore();

        //第二层
        mPaint.setTextSize(mWidth * 0.03f);
        canvas.save();
        canvas.rotate(degrees_tai/6);
        canvas.drawCircle(0, 0, mOutlineR2, outlinePaint);
        for (int i = 0; i <= 24; i++) {
            double angle = 2 * Math.PI / 24f * i+5;
            canvas.drawLine((float) StrictMath.cos(angle) * mOutlineR1, (float) StrictMath.sin(angle) * mOutlineR1,
                    (float) StrictMath.cos(angle) * mOutlineR2, (float) StrictMath.sin(angle) * mOutlineR2, outlinePaint);
            canvas.save();
            float iDeg1 = 360 / 24f * i;
            canvas.rotate(iDeg1);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(listR2[i],  mOutlineR2*0.9f, getCenteredY(), mPaint);
            canvas.restore();
        }
        canvas.restore();

        //第三层
        mPaint.setTextSize(mWidth * 0.028f);
        canvas.save();
        canvas.rotate(-degrees_tai/6);
        canvas.drawCircle(0, 0, mOutlineR3, outlinePaint);
        for (int i = 0; i <= 24; i++) {
            double angle = 2 * Math.PI / 24f * i+5;
            canvas.drawLine((float) StrictMath.cos(angle) * mOutlineR2, (float) StrictMath.sin(angle) * mOutlineR2,
                    (float) StrictMath.cos(angle) * mOutlineR3, (float) StrictMath.sin(angle) * mOutlineR3, outlinePaint);
            canvas.save();
            float iDeg1 = 360 / 24f * i;
            canvas.rotate(iDeg1);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(listR3[i],  mOutlineR3*0.965f, getCenteredY(), mPaint);
            canvas.restore();
        }
        canvas.restore();


        //第四层
        mPaint.setTextSize(mWidth * 0.02f);
        canvas.save();
        canvas.rotate(degrees_tai/18);
        canvas.drawCircle(0, 0, mOutlineR4, outlinePaint);
        for (int i = 0; i <= 72; i++) {
            double angle = 2 * Math.PI / 72f * i+2;
            canvas.drawLine((float) StrictMath.cos(angle) * mOutlineR3, (float) StrictMath.sin(angle) * mOutlineR3,
                    (float) StrictMath.cos(angle) * mOutlineR4, (float) StrictMath.sin(angle) * mOutlineR4, outlinePaint);

            canvas.save();
            float iDeg1 = 360 / 72f * i;
            canvas.rotate(iDeg1);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(listR4[i],  mOutlineR4*0.95f, getCenteredY(), mPaint);
            canvas.restore();
        }
        canvas.restore();

        //第五层
        mPaint.setTextSize(mWidth * 0.025f);
        canvas.save();
        canvas.rotate(-degrees_tai/15);
        canvas.drawCircle(0, 0, mOutlineR5, outlinePaint);
        for (int i = 0; i <= 60; i++) {
            double angle = 2 * Math.PI / 60f * i+2;
            canvas.drawLine((float) StrictMath.cos(angle) * mOutlineR4, (float) StrictMath.sin(angle) * mOutlineR4,
                    (float) StrictMath.cos(angle) * mOutlineR5, (float) StrictMath.sin(angle) * mOutlineR5, outlinePaint);

            canvas.save();
            float iDeg1 = 360 / 60f * i;
            canvas.rotate(iDeg1);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(listR5[i],  mOutlineR5*0.95f, getCenteredY(), mPaint);
            canvas.restore();
        }
        canvas.restore();

    }

    //绘制太极
    private void drawCenterInfo(Canvas canvas) {
        canvas.save();
        canvas.rotate(degrees_tai);
        //绘制两个半圆
        int radius = (int) (Math.min(mWidth, mHeight) / 10);             //太极半径
        RectF rect = new RectF(-radius, -radius, radius, radius);   //绘制区域
        canvas.drawArc(rect, 90, 180, true, blackPaint);            //绘制黑色半圆
        canvas.drawArc(rect, -90, 180, true, whitePaint);           //绘制白色半圆
        //绘制两个小圆
        int smallRadius;   //小圆半径为大圆的一般
        smallRadius = radius / 2;
        canvas.drawCircle(0, -smallRadius, smallRadius, blackPaint);
        canvas.drawCircle(0, smallRadius, smallRadius, whitePaint);
        //绘制鱼眼（两个更小的圆）
        canvas.drawCircle(0, -smallRadius, smallRadius >> 2, whitePaint);
        canvas.drawCircle(0, smallRadius, smallRadius >> 2, blackPaint);
        canvas.restore();
    }


    public void setRotate(float degrees_tai) {
        this.degrees_tai = degrees_tai;
        invalidate();                   //重绘界面
    }

    /**
     * 中间对齐
     *
     */
    private float getCenteredY() {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        return mPaint.getFontSpacing() / 2 - 4*fontMetrics.bottom;
    }


}


