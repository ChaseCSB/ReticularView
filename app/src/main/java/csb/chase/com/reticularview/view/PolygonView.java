package csb.chase.com.reticularview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CSB on 2018/9/5 14:35
 * E-Mail Address：
 * effect:
 */
public class PolygonView extends View {
    //边长的画笔
    private Paint mLinePaint;
    private int centerY;
    private int centerX;
    //边长
    private float borderLength;
    //边数
    private int borderNumber = 6;
    //多边形每个顶点角度
    private float angleSingle;
    //多边形旋转角度
    private float startAngle;
    private Rect mRect;
    //文字画笔
    private Paint mTextPaint;
    //最小值
    private double minValue = 0;
    //最大值
    private double maxValue = 100;
    private List<String>mTextData;
    //
    private List<Double>mValueData;
    //最外边的多边形每个顶点的坐标集合
    private List<Vertex>mPoints;
    private int measuredHeight;
    private int measuredWidth;
    private int insideNumber = 5;
    private Paint mValuePaint;
    //中心点是不是表示最小值   true  中心点是最小值   false中心点是最大值
    private boolean isCenterMin;
    private int mLineColor = 0xff333333;
    private int mTextColor = 0xff333333;
    private int mValueColor = 0x6600ff00;
    private int lineWidth = 4;

    public PolygonView(Context context) {
        super(context);
        initPaint();
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    private void initPaint(){
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(lineWidth);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(22);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);

        mValuePaint = new Paint();
        mValuePaint.setStrokeWidth(4);
        mValuePaint.setStyle(Paint.Style.FILL);
        mValuePaint.setColor(mValueColor);
        mValuePaint.setAntiAlias(true);


        angleSingle = getAngle(borderNumber);
        if (startAngle <= 0)startAngle = 90 - angleSingle / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mRect = new Rect(0,0,getWidth(),getHeight());
        centerY = mRect.centerY();
        centerX = mRect.centerX();
        mPoints = new ArrayList<>();
        float startY = mRect.top + 10 + getTextHeight();
        float startX = centerX;
        //最外层
        drawBorder(canvas,startX,startY);
        //里层
        drawInsideBorder(canvas);
        //每个顶点的文字
        drawText(canvas);
        drawValue(canvas);
        //中心点到顶点的线
        drawRadius(canvas);

    }

    private void drawRadius(Canvas canvas) {
        int size = mPoints.size();

        //外切圆圆心（中心点坐标）
        float[] centerCoordinate = getSectionStartCoordinate(mPoints.get(0).getX(), mPoints.get(0).getY(), mPoints.get(0).getAngle(), getCenterDistance());
        for (int i = 0;i < size;i++){
            Vertex vertex = mPoints.get(i);
            canvas.drawLine(centerCoordinate[0],centerCoordinate[1],vertex.getX(),vertex.getY(),mLinePaint);
        }
    }


    private void drawValue(Canvas canvas) {
        if (mValueData == null || mValueData.size() != mPoints.size())return;

        double differenceValue =  maxValue - minValue;
        float centerDistance = getCenterDistance();
        float averageValue = (float) (centerDistance/differenceValue);
        int size = mPoints.size();
        Path path = new Path();
        float distance = 0;
        float distanceNext = 0;
        for (int i = 0;i < size;i++){
            Vertex vertex = mPoints.get(i);
            Vertex vertexNext = mPoints.get((i+1)%size);
            double aDouble = mValueData.get(i);
            double aDoubleNext = mValueData.get((i+1)%size);
            if (isCenterMin){
                distance = (float) (centerDistance-averageValue*aDouble);
                distanceNext = (float) (centerDistance-averageValue*aDoubleNext);
            }else{
                distance = (float) (averageValue*aDouble);
                distanceNext = (float) (averageValue*aDoubleNext);
            }
            float[] valuePoint = getSectionStartCoordinate(vertex.getX(), vertex.getY(), vertex.getAngle(),distance );
            float[] valuePointNext = getSectionStartCoordinate(vertexNext.getX(), vertexNext.getY(), vertexNext.getAngle(), distanceNext);
            if (i==0)path.moveTo(valuePoint[0],valuePoint[1]);
            path.lineTo(valuePointNext[0],valuePointNext[1]);
        }
        canvas.drawPath(path,mValuePaint);
    }

    private void drawInsideBorder(Canvas canvas) {
        float averageDistance = getCenterDistance()/insideNumber;
        Path mPath = new Path();
        for(int i = 1;i < insideNumber;i++){
            int size = mPoints.size();
            for (int j = 0;j < size;j++){
                Vertex vertex = mPoints.get(j);
                Vertex vertexNext = mPoints.get((j+1)%size);
                float[] sectionStartCoordinate = getSectionStartCoordinate(vertex.getX(), vertex.getY(), vertex.getAngle(), averageDistance*i);
                float[] sectionStartCoordinateNext = getSectionStartCoordinate(vertexNext.getX(), vertexNext.getY(), vertexNext.getAngle(), averageDistance*i);
                mPath.moveTo(sectionStartCoordinate[0],sectionStartCoordinate[1]);
                mPath.lineTo(sectionStartCoordinateNext[0],sectionStartCoordinateNext[1]);
            }
        }
//        mPath.close();
        canvas.drawPath(mPath,mLinePaint);

    }

    private void drawText(Canvas canvas) {
        if (mPoints == null || mPoints.size() <= 0 || mTextData == null || mTextData.size() <= 0)return;
        int size = mPoints.size();
        for (int i = 0;i < size;i++){
            Vertex vertex = mPoints.get(i);
            float xMove = 0;
            float yMove = 0;
            if (vertex.getAngle() < 20){
                yMove = -5;
            }else if(vertex.getAngle() < 100){
                xMove = 3;
            }else if( vertex.getAngle() < 150){
                xMove = 5;
                yMove = 10;
            }else if(vertex.getAngle() < 180){
                yMove = getTextHeight();
            }else if(vertex.getAngle() == 180){
                yMove = getTextHeight()+5;
                xMove = -getTextWidth(i)/2;
            }else if (vertex.getAngle() < 235){
                yMove = getTextHeight();
                xMove = - getTextWidth(i)-3;
            }else if(vertex.getAngle() < 270){
                xMove = -getTextWidth(i)-7;
            }else{
                xMove = -getTextWidth(i)-5;
            }

            canvas.drawText(mTextData.get(i),vertex.getX()+xMove,vertex.getY()+yMove,mTextPaint);
        }
    }

    private void drawBorder(Canvas canvas,float x,float y){
        float startX = x;
        float startY = y;
        float startAngle = this.startAngle;
        Path mPath = new Path();
        mPath.moveTo(startX,startY);
        addPoint(startAngle,startX,startY);
        for (int i = 0;i < borderNumber;i++){
            if(i>0) startAngle += 180 - angleSingle;
            float xMove = (float) (borderLength*Math.cos(getRadian(startAngle)));
            float yMove = (float) (borderLength*Math.sin(getRadian(startAngle)));
            float nextX = startX + xMove;
            float nextY = startY + yMove;
            mPath.lineTo(nextX,nextY);
            if (i>0)addPoint(startAngle,startX,startY);
            startY = nextY;
            startX = nextX;
            if(startX > mRect.right - getTextHeight() -10 || startY > mRect.bottom - getTextHeight() - 10 || startX < mRect.left + getTextHeight() + 10){
                borderLength -= 10;
                invalidate();
                return;
            }
        }
        mPath.lineTo(x,y);
        canvas.drawPath(mPath, mLinePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();
        measuredBorderLength();
    }
    private float getAngle(int n){
        return 180*(n-2)/n;
    }
    private float getTextHeight(){
        if (mTextData == null || mTextData.size() <= 0)return 0;
        Rect rect = new Rect();
        String str = mTextData.get(0);
        mTextPaint.getTextBounds(str,0,str.length(),rect);
        return rect.height();
    }
    private float getTextWidth(int pos){
        if (mTextData == null || mTextData.size() <= pos)return 0;
        Rect rect = new Rect();
        String str = mTextData.get(pos);
        mTextPaint.getTextBounds(str,0,str.length(),rect);
        return rect.width();
    }
    private float getRadian(float angle){
        return (float) (Math.PI*angle/180);
    }
    public void setStartAngle(float startAngle){
        this.startAngle = startAngle;
        measuredBorderLength();
    }
    public float getBorderLength(){
        return borderLength;
    }
    public void setMaxValue(double maxValue){
        this.maxValue = maxValue;
    }
    public void setMinValue(double minValue){
        this.minValue = minValue;
    }

    public void setValueRange(double maxValue,double minValue){
        this.maxValue = maxValue;
        this.minValue = minValue;
    }
    /**
     * 值
     * @param listValue
     */
    public void setValueData(List<Double>listValue){
        this.mValueData = listValue;
    }
    /**
     * The side length will be reduced automatically when the drawing exceeds the boundary.So don't set too much impact on performance.
     * Default is the minimum boundary 1/4.
     * English translation is from Baidu.
     */
    public void setBorderLength(float borderLength){
        this.borderLength = borderLength;
    }

    /**
     * center point is min_value
     * @param centerIsMin  true:center point is min value   false:center point is max value  default is true
     */
    public void setCenterIsMin(boolean centerIsMin){
        isCenterMin = centerIsMin;
    }
    private void addPoint(float angle,float x,float y){
        if (mPoints == null )return;
        Vertex vertex = new Vertex();
        vertex.setAngle(angle);
        vertex.setX(x);
        vertex.setY(y);
        mPoints.add(vertex);
    }
    public void setTextColor(int color){
        this.mTextColor = color;
    }
    public void setLineColor(int color){
        this.mLineColor = color;
    }
    public void setValueColor(int color){
        this.mValueColor = color;
    }
    /**
     * The data written next to the vertex.
     * @param data
     */
    public void setData(List<String>data){
        if (data == null){
            return;
        }
        if (data.size() < 3){
            return;
//            throw new RuntimeException("The data size should not be less than 3");
        }
        this.mTextData = data;
        borderNumber = data.size();
        angleSingle = getAngle(borderNumber);
        measuredBorderLength();
    }
    private void measuredBorderLength(){
        borderLength = Math.min(measuredHeight, measuredWidth)/2;
    }

    /**
     * The angle between the first edge and the X axis.
     * @return
     */
    public float getStartAngle(){
        return startAngle;
    }

    /**
     * 中心点到顶点距离
     * @return
     */
    private float getCenterDistance(){
        return (float) (borderLength/2/Math.cos(getRadian(angleSingle/2)));
    }

    /**
     * 里面的多边形个数
     * @param insideNumber
     */
    public void setInsideNumber(int insideNumber){
        this.insideNumber = insideNumber;
    }
    /**
     *
     * @param x
     * @param y
     * @param angle  当前顶点与下个顶点形成的边与x轴的夹角
     * @param distance
     * @return 当前顶点到中心点方向上指定距离的点的坐标
     */
    private float[] getSectionStartCoordinate(float x,float y,float angle,float distance){
        float[]point = new float[2];
        float pointAngle = angle + angleSingle/2;
        point[0] = (float) (x + distance * Math.cos(getRadian(pointAngle)));
        point[1] = (float) (y + distance * Math.sin(getRadian(pointAngle)));
        return point;
    }

    /**
     * If the radius of the set exceeds the boundary of view, the radius will be reduced to the boundary.
     * @param radius  Note: the radius you set may not be the final result.
     *                You can call the "getRadius ()" method to get the final radius after the drawing is completed.
     */
    public void setRadius(float radius){
        borderLength = (float) (radius * Math.cos(getRadian(angleSingle/2)) * 2);
    }

    /**
     * You decide to keep a few decimal places.
     * @return
     */
    public float getRadius(){
        return (float) (borderLength / 2 / (Math.cos(getRadian(angleSingle/2))));
    }

    /**
     * 边线的宽度
     * @param lineWidth
     */
    public void setLineWidth(int lineWidth){
        this.lineWidth = lineWidth;
        mLinePaint.setStrokeWidth(lineWidth);
    }
}
