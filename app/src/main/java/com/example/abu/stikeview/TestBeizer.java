package com.example.abu.stikeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * created by abu on 2018/5/25 18:57
 * Describe:
 */
public class TestBeizer extends View {
    public TestBeizer(Context context) {
        super(context);
        init();
    }

    public TestBeizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestBeizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath=new Path();
    private void init() {

        Paint paint=mPaint;
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        //一阶贝塞尔曲线
        Path path=mPath;
        path.moveTo(100,100);
        path.lineTo(300,300);

        //二阶贝塞尔曲线
        //path.quadTo(500,-200,700,300);
        //二阶贝塞尔曲线，相对于上次的点
        path.rQuadTo(200,-300,400,0);

        //三阶贝塞尔曲线
        path.moveTo(400,800);
        path.cubicTo(500,600,700,1000,800,800);
       // path.rCubicTo(100,-200,300,400,400,0);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPaint);


    }
}
