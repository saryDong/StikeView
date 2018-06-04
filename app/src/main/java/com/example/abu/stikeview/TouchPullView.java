package com.example.abu.stikeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/**
 * created by abu on 2018/5/25 17:39
 * Describe:
 */
public class TouchPullView extends View {
    //圆的画笔
    private Paint mCirclePaint;
    //圆的半径
    private float mCicleRadius=50;
    //圆心
    private float mCirclePaintX,mCirclePaintY;
    //进度值
    private float mProgress;
    //可拖动的高度
    private int mDragHeight=300;
    //目标宽度
    private int mTargetWidth=400;

    //贝塞尔曲线的路径及其画笔
    private Path mPath=new Path();
    private  Paint mPathPaint;
    //重心点最终高度，决定控制点的Y坐标
    private int mTargetGravityHeight;
    //角度变换0-135度
    private  int mTangentAngle=100;
    private Interpolator mProgressInterpolator=new DecelerateInterpolator();
    private Interpolator mTanentAngleInterpolator;
    private Drawable mContent=null;
    private int mContentMargin=0;
    public TouchPullView(Context context) {
        super(context);
        init(null);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 初始化画笔
     */
    private void init(AttributeSet attrs) {

        final Context context=getContext();
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.TouchPullView,0,0);

        int color=array.getColor(R.styleable.TouchPullView_pColor,0x20000000);
        mCicleRadius=array.getDimension(R.styleable.TouchPullView_pRadius,mCicleRadius);
        mDragHeight=array.getDimensionPixelOffset(R.styleable.TouchPullView_pDragHeight,mDragHeight);
        mTangentAngle=array.getInteger(R.styleable.TouchPullView_pTangentAngle,100);
        mTargetWidth=array.getDimensionPixelOffset(R.styleable.TouchPullView_pTangentWidth,mTargetWidth);
        mTargetGravityHeight=array.getDimensionPixelOffset(R.styleable.TouchPullView_pTargetGravityHeight,mTargetGravityHeight);

        mContent=array.getDrawable(R.styleable.TouchPullView_pContentDrawable);
        mContentMargin=array.getDimensionPixelOffset(R.styleable.TouchPullView_pContentDrawableMargin,0);

        array.recycle();

        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置抗锯齿
        p.setAntiAlias(true);
        //设置防抖动
        p.setDither(true);
        //设置为填充模式
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFFFF4081);
        mCirclePaint=p;

        //初始化路径部分画笔
        p=new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置抗锯齿
        p.setAntiAlias(true);
        //设置防抖动
        p.setDither(true);
        //设置为填充模式
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFFFF4081);
        mPathPaint=p;

        //切角路径插值器
        mTanentAngleInterpolator= android.support.v4.view.animation.PathInterpolatorCompat.create(
                (mCicleRadius*2.0f)/mDragHeight,
                90.0f/mTangentAngle
        );

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int count=canvas.save();
        float transX=(getWidth()-getValueByLaLine(getWidth(),mTargetWidth,mProgress))/2;

        canvas.translate(transX,0);
        //画贝塞尔曲线
        canvas.drawPath(mPath,mPathPaint);

        //绘制圆
        canvas.drawCircle(mCirclePaintX,mCirclePaintY,mCicleRadius,mCirclePaint);

        Drawable drawable=mContent;
        if (drawable!=null){
            canvas.save();
            //剪切矩形区域
            canvas.clipRect(drawable.getBounds());
            //绘制Drawable
            drawable.draw(canvas);
            canvas.restore();
        }

        canvas.restoreToCount(count);
    }

    /**
     * 测量时调用
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      //  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽度模式与宽度
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int width=MeasureSpec.getSize(widthMeasureSpec);
        //获取宽度模式与高度
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);

        int iHeight=(int)((mDragHeight*mProgress+0.5f)+getPaddingLeft()+getPaddingRight());
        int iWidth= (int) (2*mCicleRadius+getPaddingLeft()+getPaddingRight());

        int measureWidth;
        int measureHeight;
        if (widthMode==MeasureSpec.EXACTLY){
            //确切的
            measureWidth=width;
        }else if (widthMode==MeasureSpec.AT_MOST){
            //最多
            measureWidth=Math.min(iWidth,width);
        }else {
            measureWidth=iWidth;
        }

        if (heightMode==MeasureSpec.EXACTLY){
            //确切的
            measureHeight=height;
        }else if (heightMode==MeasureSpec.AT_MOST){
            //最多
            measureHeight=Math.min(iHeight,height);
        }else {
            measureHeight=iHeight;
        }

        //设置测量的高度
        setMeasuredDimension(measureWidth,measureHeight);

    }


    /**
     * 当大小改变时触发即高度和宽度
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePathLayout();
    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(float progress){
        mProgress=progress;
        //请求重新进行测量
        requestLayout();
    }

    /**
     * 更新路径相关操作
     */
    private void updatePathLayout(){
        //获取进度
        final float progress=mProgressInterpolator.getInterpolation(mProgress);


        //获取可绘制区域高度宽度
        final float w=getValueByLaLine(getWidth(),mTargetWidth,mProgress);
        final float h=getValueByLaLine(0,mDragHeight, mProgress);
        //x对称轴的参数，圆的中心点x坐标
        final  float cPointX=w/2.0f;
        //圆的半径
        final float cRadius=mCicleRadius;
        //圆的中心点y坐标
        final float cPointY=h-cRadius;
        //控制点结束y值
        final float endControlY=mTargetGravityHeight;

        //更新圆的坐标
        mCirclePaintX=cPointX;
        mCirclePaintY=cPointY;

        //路径
        final Path path=mPath;
        path.reset();
        path.moveTo(0,0);

        float lEndPointX,lEndPointY;
        float lControlPointX ,lControlPointY;
        //获取当前切线弧度
        float angle=mTangentAngle*mTanentAngleInterpolator.getInterpolation(progress);
        double radian=Math.toRadians(angle);
        float x= (float) (Math.sin(radian)*cRadius);
        float y= (float) (Math.cos(radian)*cRadius);

        lEndPointX=cPointX-x;
        lEndPointY=cPointY+y;
         //控制点的Y坐标变化
        lControlPointY=getValueByLaLine(0,endControlY,progress);
        //控制点与结束点之间的高度
        float tHeight=lEndPointY-lControlPointY;
        //控制点与x的坐标距离
        float tWidth= (float) (tHeight/Math.tan(radian));
        lControlPointX=lEndPointX-tWidth;

        //贝塞尔曲线
        path.quadTo(lControlPointX,lControlPointY,lEndPointX,lEndPointY);
        //链接到右边
        path.lineTo(cPointX+(cPointX-lEndPointX),lEndPointY);
        //画右边的贝塞尔曲线
        path.quadTo(cPointX+cPointX-lControlPointX,lControlPointY,w,0);

        updateContentLayout(cPointX,cPointY,cRadius);

    }
    private void updateContentLayout(float cx,float cy,float radius){
      Drawable drawable=mContent;
      if (drawable!=null){
          int margin=mContentMargin;
          int l= (int) (cx-radius+margin);
          int r= (int) (cx+radius-margin);
          int t= (int) (cy-radius+margin);
          int b= (int) (cx+radius-margin);
          drawable.setBounds(l,t,r,b);
      }

    }

    /**
     * 当前进度值
     * @param start 起始值
     * @param end 结束值
     * @param progress 进度
     * @return 当前进度的值
     */
    private float getValueByLaLine(float start,float end,float progress){
        return start+(end-start)*progress;
    }
}
