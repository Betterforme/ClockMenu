package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by SJ on 2017/6/6.
 */

public class CircleMenuView extends View{

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
    public ArrayList<String> data = new ArrayList<>();
    //各种画笔
    private Paint paintArc,whitePaint;
    //圆环的颜色
    private static final int GREEN = Color.parseColor("#43c562");
    private static final int GREY = Color.parseColor("#959595");

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
       data.add("黄冈小状元");
       data.add("听说读写");
       avg_angle = 360/data.size();

       whitePaint = new Paint();
       whitePaint.setAntiAlias(true);
       whitePaint.setColor(GREY);
       whitePaint.setStrokeWidth(4);

       paintArc = new Paint();
       paintArc.setAntiAlias(true);
       paintArc.setStyle(Paint.Style.STROKE);
       paintArc.setColor(GREEN);

   }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("ver",singer_angle+"");
        canvas.rotate((float) singer_angle,x,y);
        drawArc(canvas);
    }

    private void drawArc(Canvas canvas){
        paintArc.setStrokeWidth(ARC_WIDTH);
        for (int i = 0; i < data.size(); i++) {
            canvas.drawArc(mRect,-90+avg_angle*i,avg_angle*(i+1),false,paintArc);
        }
        canvas.save();
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
           ARC_WIDTH = x/3;
       }
        mRect = new RectF(0+ARC_WIDTH/2,0+ARC_WIDTH/2,width-ARC_WIDTH/2,width-ARC_WIDTH/2);
    }

    /**
     * get the angle of a touch event.
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
                Log.i("ver","Down");
                current_angle = getAngle(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("current_angle",current_angle+"");
                Log.e("getAngle",getAngle(event.getX(),event.getY())+"");
                Log.e("total_angle",total_angle+"");
                Log.e("X",event.getX()+"");
                Log.e("Y",event.getY()+"");

                singer_angle = total_angle+(current_angle-getAngle(event.getX(),event.getY()))%360;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                total_angle = current_angle-getAngle(event.getX(),event.getY());
                velocityTracker.computeCurrentVelocity(500);
                Log.i("vertical",velocityTracker.getXVelocity()+";    "+velocityTracker.getYVelocity());
                break;
        }
        return true;
    }

    private void releaseVelocityTracker() {
        if(null != velocityTracker) {
            velocityTracker.clear();
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }
}
