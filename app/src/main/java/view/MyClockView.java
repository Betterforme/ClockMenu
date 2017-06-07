package view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/31 0031.
 */


public class MyClockView extends View {


    //上下文
    Context context;
    //计时动画
    ValueAnimator anim;
    //当前时分秒
    int cur_second,cur_min,cur_hour;
    //屏幕宽
    private int width;
    //屏幕高
    private int height;
    //半径，圆心坐标,内部圆半径
    private float r,x,y,r1;

    //时间文字
    private String [] time={"12","3","6","9"};
    //内圆背景色
    public final  static int COLOR_BG_GREY_INNER_CIRCLE = Color.parseColor("#30344e");
    //时钟背景色
    public final  static int COLOR_BG_GREY_CIRCLE = Color.parseColor("#2b2e43");
    //预计完成时间背景色
    public final static int COLOR_BG_BLUE_OVA = Color.parseColor("#3a3e5c");
    //预计完成时间默认颜色
    public final static int COLOR_BG_GREY_OVA = Color.parseColor("#333752");
    //文字颜色，3,6,9,12
    public final static int COLOR_TEXT_GREY_OVA = Color.parseColor("#7379a5");
    //秒针的颜色
    public final static int COLOR_PIN_GREY_SECOND = Color.parseColor("#8389b7");
    //时,分针的颜色
    public final static int COLOR_PIN_GREY_HOUR_MIN = Color.parseColor("#3a3e5c");

    //蓝色
    public final static int COLOR_BG_BLUE1_OVA = Color.parseColor("#4ac5ff");

    //椭圆间隙的角度
    private  static int gap_degreen = 3;
    //椭圆角度
    private static int avg_degreen = 12;
    //绘制圆周的宽度
    private static int arc_width = 26;
    //圆弧到大圆的边距
    private static int arc_to_board = 34;
    //时间文字到圆的边距
    private static int time_to_board = 24;
    //时间文字大小
    private static int time_text_size = 36;
    //时针的宽度
    private static int hour_width = 12;
    //分钟的宽度
    private static int minus_width = 9;
    //秒钟的宽度
    private static int second_width = 6;
    //各种画笔
    private Paint p1circle,p2Arc,p1Arc,p3cicle,p4Ring,p5Min,p6Hour,p7Second,p8CenterCircle,p9Text,pBlue;
    //绘制外圆弧的矩形
    private  RectF mRectF;

    //动画旋转的角度
    private float rolate = 0;

    //是否旋转翻转
    private Boolean reversal = false;
    public MyClockView(Context context){
         this(context,null);
    }
    public MyClockView(Context context, AttributeSet attr){
        this(context,attr,0);
    }
    public MyClockView(Context context, AttributeSet attr, int type){
        super(context,attr,type);
        this.context = context;
        initPaint();
    }
    public void initPaint(){
        p1circle = new Paint();
        p1circle.setAntiAlias(true);
        p1circle.setColor(COLOR_BG_GREY_CIRCLE);

        p2Arc = new Paint();
        p2Arc.setAntiAlias(true);
        p2Arc.setStyle(Paint.Style.STROKE);
        p2Arc.setColor(COLOR_BG_GREY_OVA);
        p2Arc.setStrokeWidth(arc_width);

        p1Arc = new Paint();
        p1Arc.setAntiAlias(true);
        p1Arc.setStyle(Paint.Style.STROKE);
        p1Arc.setColor(COLOR_BG_GREY_CIRCLE);
        p1Arc.setStrokeWidth(arc_width+4);


        p3cicle = new Paint();
        p3cicle.setAntiAlias(true);
        p3cicle.setColor(COLOR_BG_GREY_INNER_CIRCLE);

        p4Ring = new Paint();
        p4Ring.setAntiAlias(true);
        p4Ring.setColor(COLOR_BG_BLUE_OVA);

        p8CenterCircle = new Paint();
        p8CenterCircle.setColor(COLOR_PIN_GREY_SECOND);
        p8CenterCircle.setAntiAlias(true);

        p9Text = new Paint();
        p9Text.setColor(COLOR_TEXT_GREY_OVA);
        p9Text.setAntiAlias(true);
        p9Text.setTextSize(time_text_size);

        p7Second = new Paint();
        p7Second.setColor(COLOR_PIN_GREY_SECOND);
        p7Second.setAntiAlias(true);
        p7Second.setStrokeWidth(second_width);

        p5Min = new Paint();
        p5Min.setColor(COLOR_PIN_GREY_HOUR_MIN);
        p5Min.setAntiAlias(true);
        p5Min.setStrokeWidth(minus_width);

        p6Hour = new Paint();
        p6Hour.setColor(COLOR_PIN_GREY_HOUR_MIN);
        p6Hour.setAntiAlias(true);
        p6Hour.setStrokeWidth(hour_width);

        pBlue = new Paint();
        pBlue.setStyle(Paint.Style.STROKE);
        pBlue.setColor(COLOR_BG_BLUE1_OVA);
        pBlue.setStrokeWidth(arc_width);
        p6Hour.setAntiAlias(true);

        //开启时间动画
        startOneSAnima();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //抗锯齿设置
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        //绘制四个背景圆
        drawCircle(canvas);
        //绘制圆环
        drawOva(canvas);
        //绘制文字
        drawText(canvas);
        //绘制指针
        drawPin(canvas);
    }

    /**
     * 画背景圆
     * @param canvas
     */
    private void drawCircle(Canvas canvas){
        canvas.drawCircle(x,y,r,p1circle);
        canvas.drawCircle(x,y,r1+10,p4Ring);
        canvas.drawCircle(x,y,r1,p3cicle);
    }

    /**
     * 画大圆中的圆环
     * @param canvas
     */
    private void drawOva(final Canvas canvas){
        for (int i = 0; i < 24; i++) {
            canvas.drawArc(mRectF,i*15+gap_degreen,avg_degreen,false,p2Arc);
        }
        if(reversal)
            canvas.drawArc(mRectF,-90,rolate,false,pBlue);
        else
            canvas.drawArc(mRectF,-90,-rolate,false,pBlue);
        for (int i = 0; i < 24; i++) {
            canvas.drawArc(mRectF,i*15,gap_degreen,false,p1Arc);
        }
    }

    /**
     * 绘制时间文本3,6,9,12
     * @param canvas
     */
    private void drawText(Canvas canvas){
        canvas.drawText(time[0],x-p9Text.measureText(time[0])/2,x-r1+time_to_board+getTextWH(time[0]).height(),p9Text);
        canvas.drawText(time[1],x+r1-time_to_board-getTextWH(time[1]).width(),x+p9Text.measureText(time[1])/2,p9Text);
        canvas.drawText(time[2],x-p9Text.measureText(time[2])/2,x+r1-time_to_board,p9Text);
        canvas.drawText(time[3],x-r1+time_to_board,x+p9Text.measureText(time[3])/2,p9Text);
    }

    /**
     * 绘制时分秒指针
     * @param canvas
     */
    private void drawPin(Canvas canvas){
        setCurTime();
        drawH(canvas);
        drawM(canvas);
        drawS(canvas);
        canvas.drawCircle(r,r,12,p8CenterCircle);
    }

    /**
     * 绘制秒针
     * @param canvas
     */
    private void drawS(Canvas canvas){
        canvas.save();
        canvas.rotate(6*cur_second,x,y);
        canvas.drawLine(x,y,x,r-r1+r1*2/5,p7Second);
        canvas.drawCircle(x,r-r1+r1*2/5,second_width/2,p7Second);
        canvas.restore();
    }

    /**
     * 绘制分针
     * @param canvas
     */
    private void drawM(Canvas canvas){
        canvas.save();
        canvas.rotate(cur_min*6+cur_second*0.1f,x,y);
        canvas.drawLine(x,y,x,r-r1+r1*45/100,p5Min);
        canvas.drawCircle(x,r-r1+r1*45/100,minus_width/2,p5Min);
        canvas.restore();
    }

    /**
     * 绘制时针
     * @param canvas
     */
    private void drawH(Canvas canvas){
        canvas.save();
        canvas.rotate(30*cur_hour+cur_min*0.5f,x,y);
        canvas.drawLine(x,y,x,r-r1+r1*55/100,p6Hour);
        canvas.drawCircle(x,r-r1+r1*55/100,minus_width/2,p6Hour);
        canvas.restore();
    }

    /**
     * 获取文本的框高
     * @param text
     * @return
     */
    private Rect getTextWH(String text){
        Rect rect = new Rect();
        p9Text.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if(mode == MeasureSpec.EXACTLY){
            this.width = width;
            this.height = width;
            this.r = width/2;
            this.x = width/2;
            this.y = width/2;
            this.r1 = width/3;
            setMeasuredDimension(width,width);
            mRectF = new RectF(arc_to_board,arc_to_board,width-arc_to_board,width-arc_to_board);
        }else {
            setMeasuredDimension(400,400);
            mRectF = new RectF(arc_to_board,arc_to_board,400-arc_to_board,400-arc_to_board);
            this.width = 400;
            this.height = 400;
            this.r = 200;
            this.x = 200;
            this.y = 200;
            this.r1 = 133;
        }
    }

    /**
     * 每秒读一次系统时间设置钟表时间
     */
    private void startOneSAnima(){
        anim = ValueAnimator.ofFloat(0,360f);
        anim.setDuration(5000);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setInterpolator(new CycleInterpolator(1));
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                reversal = !reversal;
            }
        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rolate = (float)((ValueAnimator)animation).getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    /**
     * 停止动画计时
     */
    public void stopAnimation(){
        anim.cancel();
        anim = null;
        clearAnimation();
    }

    /**
     * 设置当前钟表时间时间
     */
    public void setCurTime(){
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        String time = df.format(new Date());
        cur_hour = Integer.valueOf(time.split(":")[0]);
        cur_min = Integer.valueOf(time.split(":")[1]);
        cur_second = Integer.valueOf(time.split(":")[2]);
        Log.i("curtime", time+" "+cur_hour+cur_min+cur_second);
        new LruCache<String,Bitmap>(100){

        };
    }
    float downX=0,downY=0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                x = x+event.getX()-downX;
                y = y+event.getY() -downY;
                r1 = x*2/3;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        downX = event.getX();
        downY = event.getY();
        return true;
    }
}
