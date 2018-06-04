package com.example.abu.stikeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * created by abu on 2018/5/25 19:41
 * Describe:
 */
public class BazierView extends View {
    public BazierView(Context context) {
        super(context);
        init();
    }

    public BazierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BazierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private Path mSrcBezier=new Path();
    private Path mBezier=new Path();
    private Paint mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init(){
        Paint paint=mPaint;
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        //初始化源贝塞尔曲线
        mSrcBezier.cubicTo(200,700,500,1200,700,200);

        new Thread(){
            @Override
            public void run() {
                //初始化贝塞尔曲线
                initBazier();
            }
        }.start();
    }

    /**
     * 初始化贝塞尔曲线
     */
    private void initBazier(){
        float[] xPoints=new float[]{0,200,500,700,800,500,600,200};
        float[] yPoints=new float[]{0,700,1200,200,800,1300,600,1000};



        Path path=mBezier;

        int fps=1000;
        for (int i=0;i<=100;i++){
            float progress=i/(float)fps;
            float x=  calculateBezier(progress,xPoints);
            float y=  calculateBezier(progress,yPoints);
            path.lineTo(x,y);
            //刷新
            postInvalidate();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 计算某时刻的贝塞尔所处值(x或y)
     * @param t  (0~1)
     * @param values 贝塞尔点集合
     * @return 当前贝塞尔所处点
     */
    private float calculateBezier(float t,float...values){
        //采用双层循环
        final int len=values.length;
        for (int i=len-1;i>0;i--){
            for (int j=0;j<i;j++){
                values[j]=values[j] + (values[j+1]-values[j])*t;
            }
        }

        //运算结果保存在第一位
        return values[0];
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mBezier,mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawPath(mSrcBezier,mPaint);
    }

}
