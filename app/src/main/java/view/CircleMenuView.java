package view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

/**
 * Created by SJ on 2017/6/6.
 */

public class CircleMenuView extends View{

    //padding =margin的值
    private int margin;
    //惯性旋转的速率；值越大旋转越慢
    private  int rate = 20;
    //惯性1秒内旋转的像素
    private  int endAngle;
    //扇形的宽度
    private float ARC_WIDTH ;
    //每个圆弧的角度
    private float avg_angle;
    //手指旋转的角度
    private double singer_angle=0,current_angle=0,total_angle = 0;
    //圆形的位置x,y,半径r
    private float x,y,r,w,h;
    //画圆环的外矩形
    private RectF mRect;
    //获取滑动的速度
    private VelocityTracker velocityTracker;
    //扇形区域中的数据
    public ArrayList<ItemBean> data = new ArrayList<>();
    //各种画笔
    private Paint paintArc,whitePaint,textPaint,traiganglePaint;
    //圆环的颜色
    private static final int GREEN = Color.parseColor("#43c562");
    private static final int GREY = Color.parseColor("#959595");
    private static final int TRAIGANGLE_BLUE = Color.parseColor("#24bbfd");
    //文字的大小
    private static final int TEXT_SIZE = 30;
    public CircleMenuView(Context context){
        this(context,null);
    }

    public CircleMenuView(Context context, AttributeSet attributes){
        this(context,attributes,0);
    }
    public CircleMenuView(Context context, AttributeSet attributes, int flag){
        super(context,attributes,flag);
        initData();

    }

   public void initData(){
       for (int i = 0; i < 12; i++) {
           ItemBean item = new ItemBean();
           item.setHw_item("黄冈"+i);
           data.add(item);
       }
       avg_angle = 360/data.size();

       whitePaint = new Paint();
       whitePaint.setAntiAlias(true);
       whitePaint.setColor(Color.WHITE);
       whitePaint.setStrokeWidth(8);

       paintArc = new Paint();
       paintArc.setAntiAlias(true);
       paintArc.setStyle(Paint.Style.STROKE);
       paintArc.setColor(GREEN);

       textPaint = new Paint();
       textPaint.setAntiAlias(true);
       textPaint.setColor(Color.WHITE);
       textPaint.setTextSize(TEXT_SIZE);


       traiganglePaint = new Paint();
       traiganglePaint.setAntiAlias(true);
       traiganglePaint.setColor(TRAIGANGLE_BLUE);
   }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate((float) singer_angle,x,y);
        drawArc(canvas);
        drawText(canvas);
        canvas.restore();
        drawTriangle(canvas);
    }

    /**
     * 画顶部三角形
     * @param canvas
     */
    private void drawTriangle(Canvas canvas){
        Path path = new Path();
        path.moveTo(x-15+margin,margin);
        path.lineTo(x+margin,20+margin);
        path.lineTo(x+15+margin,margin);
        path.close();
        canvas.drawPath(path,traiganglePaint);
    }

    /**
     * 绘制文本内容
     * @param canvas
     */
    private void drawText(Canvas canvas){
        canvas.save();
        for (int j = 0; j < data.size(); j++) {
            String text = data.get(j).getHw_item();
            for (int i = 0; i < text.length(); i++) {
                String s = text.substring(i,i+1);
                if(i == 0)
                    canvas.drawText(s,x-getTextWidth(s)/2,y/7*(i+1),textPaint);
                else
                    canvas.drawText(s,x-getTextWidth(s)/2,y/7+getTextHeight()*i,textPaint);
            }
            canvas.rotate(avg_angle,x,y);
        }
        canvas.restore();
    }

    /**
     * 获取文本的高度
     * @return
     */
    private float getTextHeight(){
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return (float)Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 文字的宽度
     * @param s
     * @return
     */
    private float getTextWidth(String s){
        return textPaint.measureText(s);
    }

    /**
     *
     * 绘制扇形圆弧
     * @param canvas
     */
    private void drawArc(Canvas canvas){
        paintArc.setStrokeWidth(ARC_WIDTH);
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i).getIsChoose() == 0)
                canvas.drawArc(mRect,-90+avg_angle*i,avg_angle*(i+1),false,paintArc);
            else{
                paintArc.setColor(Color.parseColor("#e0f5ff"));
                canvas.drawArc(mRect,-90+avg_angle*i,avg_angle*(i+1),false,paintArc);
            }
        }
        canvas.save();
        canvas.rotate(-avg_angle/2,x,y);
        for (int i = 0; i < data.size(); i++) {
            canvas.drawLine(x,ARC_WIDTH,x,0,whitePaint);
            canvas.rotate(avg_angle,x,y);
        }
        canvas.restore();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       int width = MeasureSpec.getSize(widthMeasureSpec);
       int mode = MeasureSpec.getMode(widthMeasureSpec);
       margin = getPaddingBottom();
       if(mode == MeasureSpec.AT_MOST){
           setMeasuredDimension(500,500);
           x = 250;
           y = 250;
           r = 250;
           w = 500;
           h = 500;
       }else {
           setMeasuredDimension(width,width);
           x = width/2;
           y = width/2;
           r = width/2;
           w = width;
           h = width;
           ARC_WIDTH = x/2;
       }
        mRect = new RectF(0+ARC_WIDTH/2,0+ARC_WIDTH/2,width-ARC_WIDTH/2,width-ARC_WIDTH/2);
    }

    /**
     * 获取旋转的角度
     */
    private double getAngle(double x, double y) {
        x = x - (w / 2d);
        y = h - y - (h / 2d);

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    /**
     * get the quadrant of the wheel which contains the touch point (x,y)
     *
     * @return quadrant 1,2,3 or 4
     */
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(velocityTracker == null){
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                current_angle = ((360-getAngle(event.getX(),event.getY()))+90)%360;
                setDataAllUnChoose();
                break;
            case MotionEvent.ACTION_MOVE:
                singer_angle = total_angle+(((360-getAngle(event.getX(),event.getY()))+90)%360-current_angle)%360;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                total_angle =total_angle+ ((360-getAngle(event.getX(),event.getY()))+90)%360-current_angle;
                Log.e("total_angle",(int)total_angle%360+"");
                velocityTracker.computeCurrentVelocity(1000);
                if(Math.abs(velocityTracker.getYVelocity())>500 ){
                    endAngle = (int)velocityTracker.getYVelocity();
                    startAnmima();
                }else {
                    getChooseItem();
                }
                break;
        }
        return true;
    }


    /**
     * 当按下时设置都为选中状态
     */
    public void setDataAllUnChoose(){
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setIsChoose(0);
        }
    }

    /**
     * 释放velocityTracker
     */
    private void releaseVelocityTracker() {
        if(null != velocityTracker) {
            velocityTracker.clear();
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }


    private float max_value = 0;
    /**
     * 开启惯性旋转动画
     */
    private void startAnmima(){
        max_value = 0;
        //旋转得角度控制在10800以内，旋转时间在3S以内
        if(endAngle>10800){
            endAngle = 10800;
        }else if(endAngle <-10800){
            endAngle = -10800;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,endAngle);
        valueAnimator.setDuration(Math.abs(endAngle)/3);
        valueAnimator.setInterpolator(new DecelerateInterpolator(2f));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                singer_angle = total_angle+(int)(animation.getAnimatedValue())/rate%360;
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                total_angle = total_angle+endAngle/rate%360;
                getChooseItem();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    /**
     * 获取被选中的数据
     * @return
     */
    public int getChooseItem(){
        int ret;
        int positon = Math.round((Math.round(total_angle)%360)/avg_angle);
        if(positon<0){
             ret = -positon;
        }else
             ret = data.size()-positon;
        if(ret == data.size())
            ret = 0;
        data.get(ret).setIsChoose(1);
        invalidate();
        return ret;
    }

    private  StopPositionItem spi;
    interface StopPositionItem{
        public void ChooseItem(ItemBean ib);
    }

}
