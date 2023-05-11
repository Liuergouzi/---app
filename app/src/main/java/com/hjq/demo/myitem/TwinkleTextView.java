package com.hjq.demo.myitem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class TwinkleTextView extends TextView {

    // 线性渐变渲染
    private LinearGradient mLinearGradient;
    // 渲染矩阵
    private Matrix mGradientMatrix;
    private int mViewWidth = 0;
    private int mViewHeight = 0;

    private int mTranslateX = 0;

    public TwinkleTextView(Context context) {
        super(context);
    }

    public TwinkleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TwinkleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0 || mViewHeight==0) {
            mViewWidth = getMeasuredWidth();
            mViewHeight=getMeasuredHeight();
            if (mViewWidth > 0 ||  mViewHeight>0 ) {
                // 画笔
                Paint mPaint = getPaint();
                // 创建RadialGradient对象
                // 第一个,第二个参数表示渐变圆中心坐标
                // 第三个参数表示半径
                // 第四个,第五个,第六个与线性渲染相同
                //环形渐变渲染
//                RadialGradient mRadialGradient = new RadialGradient(50, 50, 30, new int[]{
//                        0x115E5D5D, 0xffFFFFFF, 0x33FFFFFF}, new float[]{0f,
//                        0.5f, 1f}, Shader.TileMode.CLAMP);

                // 创建LinearGradient对象
                // 起始点坐标（-mViewWidth, 0） 终点坐标（0，0）
                // 第一个,第二个参数表示渐变起点 可以设置起点终点在对角等任意位置
                // 第三个,第四个参数表示渐变终点
                // 第五个参数表示渐变颜色
                // 第六个参数可以为空,表示坐标,值为0-1
                // 如果这是空的，颜色均匀分布，沿梯度线。
                // 第七个表示平铺方式
                // CLAMP重复最后一个颜色至最后
                // MIRROR重复着色的图像水平或垂直方向已镜像方式填充会有翻转效果
                // REPEAT重复着色的图像水平或垂直方向
                mLinearGradient = new LinearGradient(-mViewWidth, 0, 0, mViewHeight,
                        new int[] { 0xD3F6F074, 0xffFFFFFF , 0xff0FF3DC },
                        new float[] { 0, 0.5f, 1f }, Shader.TileMode.MIRROR);
                mPaint.setShader(mLinearGradient);
                mPaint.setColor(Color.parseColor("#ffffffff"));
                //设置字体阴影效果
                //第一个参数代表阴影的半径
                //第二个参数代表阴影在X方向的延伸像素
                //第三个参数代表阴影在Y方向的延伸像素

                mPaint.setShadowLayer(3, 2, 2, 0xFFFFFFFF);
                mGradientMatrix = new Matrix();
            }
        }
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ( mGradientMatrix != null) {
            mTranslateX += mViewWidth / 10;
            if (mTranslateX > 2 * mViewWidth ) {
                mTranslateX = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslateX, 0);

            // mLinearGradient.setLocalMatrix(mGradientMatrix);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(100);
        }
    }

}
