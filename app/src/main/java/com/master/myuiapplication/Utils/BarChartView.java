package com.master.myuiapplication.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.master.myuiapplication.HomeFragment;
import com.master.myuiapplication.R;

import java.util.ArrayList;
import java.util.Calendar;

public class BarChartView extends View {
    private final int MINI_BAR_WIDTH;
    private final int BAR_SIDE_MARGIN;
    private final int BAR_STARTEND_MARGIN;
    private final int TEXT_TOP_MARGIN;
/*
    private final int TEXT_COLOR = Color.parseColor(String.valueOf(R.color.barChartText));
    private final int BACKGROUND_COLOR = Color.parseColor(String.valueOf(R.color.colorTextBright));
    private final int FOREGROUND_COLOR = Color.parseColor(String.valueOf(R.color.colorPrimaryDark));
*/

    private final int TEXT_COLOR = R.color.barChartText;
    private final int BACKGROUND_COLOR = R.color.colorTextBright;
    private final int FOREGROUND_COLOR = R.color.colorPrimaryDark;
    private final int FOREGROUND_TODAY_COLOR = R.color.colorPrimaryLight;
    private final int BACKGROUND_LINE_COLOR = R.color.barChartBackLine;
    private ArrayList<Float> percentList;
    private ArrayList<Float> targetPercentList;
    private Paint textPaint;
    private Paint bgPaint;
    private Paint fgPaint;
    private Paint fgTodayPaint;
    private Rect rect;
    private int barWidth;
    private int bottomTextDescent;
    private boolean autoSetWidth = true;
    private int topMargin;
    private int bottomTextHeight = 0;
    private ArrayList<String> bottomTextList = new ArrayList<String>();
    private ArrayList<Integer> yCoordinateList = new ArrayList<Integer>();
    private int dataOfAGird = 10;   // line between 2 values in Y-Axis
    private int mViewHeight;

    /*
      |  | ←topLineLength
    --+--+--+--+--+--+--
    --+--+--+--+--+--+--
     ↑sideLineLength
 */
    private int topLineLength = BarChartUtils.dip2px(getContext(), 12);
    private int sideLineLength = BarChartUtils.dip2px(getContext(), 45) / 3 * 2;

    private final int bottomTextTopMargin = BarChartUtils.sp2px(getContext(), 5);
    private final int bottomLineLength = BarChartUtils.sp2px(getContext(), 22);
    private static final int MIN_VERTICAL_GRID_NUM = 4;
    private int backgroundGridWidth = BarChartUtils.dip2px(getContext(), 45);
    private final int MIN_HORIZONTAL_GRID_NUM = 1;


    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean needNewFrame = false;
            for (int i = 0; i < targetPercentList.size(); i++) {
                if (percentList.get(i) < targetPercentList.get(i)) {
                    percentList.set(i, percentList.get(i) + 0.02f);
                    needNewFrame = true;
                } else if (percentList.get(i) > targetPercentList.get(i)) {
                    percentList.set(i, percentList.get(i) - 0.02f);
                    needNewFrame = true;
                }
                if (Math.abs(targetPercentList.get(i) - percentList.get(i)) < 0.02f) {
                    percentList.set(i, targetPercentList.get(i));
                }
            }
            if (needNewFrame) {
                postDelayed(this, 5);
            }
            invalidate();
        }
    };

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(getResources().getColor(BACKGROUND_COLOR));
        fgPaint = new Paint(bgPaint);
        fgPaint.setColor(getResources().getColor(FOREGROUND_COLOR));
        fgTodayPaint = new Paint(fgPaint);
        fgTodayPaint.setColor(getResources().getColor(FOREGROUND_TODAY_COLOR));
        rect = new Rect();
        topMargin = BarChartUtils.dip2px(context, 2);
        int textSize = BarChartUtils.sp2px(context, 15);
        barWidth = BarChartUtils.dip2px(context, 22);
        MINI_BAR_WIDTH = BarChartUtils.dip2px(context, 30);
        BAR_SIDE_MARGIN = BarChartUtils.dip2px(context, 10);
        BAR_STARTEND_MARGIN = BarChartUtils.dip2px(context, 22);
        TEXT_TOP_MARGIN = BarChartUtils.dip2px(context, 5);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(TEXT_COLOR));
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        percentList = new ArrayList<Float>();
    }

    /**
     * dataList will be reset when called is method.
     *
     * @param bottomStringList The String ArrayList in the bottom.
     */
    public void setBottomTextList(ArrayList<String> bottomStringList) {
        //        this.dataList = null;
        this.bottomTextList = bottomStringList;
        Rect r = new Rect();
        bottomTextDescent = 0;
        barWidth = MINI_BAR_WIDTH;
        for (String s : bottomTextList) {
            textPaint.getTextBounds(s, 0, s.length(), r);
            if (bottomTextHeight < r.height()) {
                bottomTextHeight = r.height();
            }
            if (autoSetWidth && (barWidth < r.width())) {
                barWidth = r.width();
            }
            if (bottomTextDescent < (Math.abs(r.bottom))) {
                bottomTextDescent = Math.abs(r.bottom);
            }
        }
        setMinimumWidth(2);
        postInvalidate();
    }

    /**
     * @param list The ArrayList of Integer with the range of [0-max].
     */
    public void setDataList(ArrayList<Integer> list, int max) {
        targetPercentList = new ArrayList<Float>();
        if (max == 0) max = 1;

        for (Integer integer : list) {
            targetPercentList.add(1 - (float) integer / (float) max);
        }

        // Make sure percentList.size() == targetPercentList.size()
        if (percentList.isEmpty() || percentList.size() < targetPercentList.size()) {
            int temp = targetPercentList.size() - percentList.size();
            for (int i = 0; i < temp; i++) {
                percentList.add(1f);
            }
        } else if (percentList.size() > targetPercentList.size()) {
            int temp = percentList.size() - targetPercentList.size();
            for (int i = 0; i < temp; i++) {
                percentList.remove(percentList.size() - 1);
            }
        }
        setMinimumWidth(2);
        removeCallbacks(animator);
        post(animator);
    }

    private void drawBackgroundLines(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(BarChartUtils.dip2px(getContext(), 1f));
        paint.setColor(getResources().getColor(BACKGROUND_LINE_COLOR));
        PathEffect effects = new DashPathEffect(new float[]{10, 5, 10, 5}, 1);

/*
        //draw vertical lines
        for (int i = 0; i < xCoordinateList.size(); i++) {
            canvas.drawLine(xCoordinateList.get(i), 0, xCoordinateList.get(i),
                    mViewHeight - bottomTextTopMargin - bottomTextHeight - bottomTextDescent,
                    paint);
        }
*/

        //draw dotted lines
        paint.setPathEffect(effects);
        Path dottedPath = new Path();
        for (int i = 0; i < yCoordinateList.size(); i++) {
//            if ((yCoordinateList.size() - 1 - i) % dataOfAGird == 0) {
//                dottedPath.moveTo(0, yCoordinateList.get(i));
            dottedPath.moveTo(BAR_SIDE_MARGIN, yCoordinateList.get(i));
//                dottedPath.lineTo(getWidth(), yCoordinateList.get(i));
//            dottedPath.lineTo(getWidth() - 2 * BAR_SIDE_MARGIN, yCoordinateList.get(i));
            dottedPath.lineTo((BAR_SIDE_MARGIN + barWidth) * 7 + 60, yCoordinateList.get(i));
            canvas.drawPath(dottedPath, paint);
//            }
// draw Y - axis values
/*
            canvas.drawText(String.valueOf((yCoordinateList.size() - i) * HomeFragment.multBarData),
                    getWidth() - 2 * BAR_SIDE_MARGIN + 30,
                    yCoordinateList.get(i), textPaint);
*/

            canvas.drawText(String.valueOf((yCoordinateList.size() - i) * HomeFragment.multBarData),
                    (BAR_SIDE_MARGIN + barWidth) * 7 + 80,
                    yCoordinateList.get(i), textPaint);

        }

//        if (!drawDotLine) {
        //draw solid lines
/*
            for (int i = 0; i < HomeFragment.barDataList.size(); i++) {
                if ((HomeFragment.barDataList.size() - 1 - i) % dataOfAGird == 0) {
                    canvas.drawLine(0, HomeFragment.barDataList.get(i), getWidth(),
                            HomeFragment.barDataList.get(i), paint);
                }
            }
*/
//        }


    }

    private void refreshYCoordinateList(int verticalGridNum) {
        yCoordinateList.clear();
/*
        for (int i = 0; i < (verticalGridNum + 1); i++) {
            yCoordinateList.add(topLineLength + ((mViewHeight
                    - topLineLength
                    - bottomTextHeight
                    - bottomTextTopMargin
                    - bottomLineLength
                    - bottomTextDescent) * i / (verticalGridNum)));
        }
*/

// views are not built yet in onCreate(), onStart(), or onResume(). their dimensions are 0.
// if called from @ onDraw , values are available ! NOT from @ onMeasure
        int bottom = this.getHeight() - bottomTextHeight - TEXT_TOP_MARGIN;
/*
        Log.d("refreshYCoordinate ", " bottom " +bottom +" getHeight() " +getHeight()
            +" bottomTextHeight " +bottomTextHeight +" TEXT_TOP_MARGIN " +TEXT_TOP_MARGIN
        +" barChartHeight " +HomeFragment.barChartHeight +" homeViewHeight " +HomeFragment.homeViewHeight);
*/
        for (int i = 0; i < (verticalGridNum); i++) {
            yCoordinateList.add((bottom / verticalGridNum) * i);
        }

/*
        yCoordinateList.add(0);
        yCoordinateList.add(100);
        yCoordinateList.add(200);
        yCoordinateList.add(300);
        yCoordinateList.add(400);
        yCoordinateList.add(500);
        yCoordinateList.add(600);
        yCoordinateList.add(700);
        yCoordinateList.add(800);
        yCoordinateList.add(900);
        yCoordinateList.add(1000);
*/
/*
        for(int i = 0; i <= HomeFragment.maxBarData; i++){
            yCoordinateList.add(i*10*HomeFragment.maxBarData);
        }
*/
/*
        for(int i = 0; i <= verticalGridNum+1; i++){
            yCoordinateList.add(i*100);
        }
*/
/*
        for(int i = 0; i <= 10; i++){
            yCoordinateList.add(i*100);
        }
*/
        Log.d("refreshYCoordinateList ", " yCoordinateList " + yCoordinateList);
    }

    private void refreshAfterDataChanged() {
        int verticalGridNum = getVerticalGridlNum();
        refreshYCoordinateList(verticalGridNum);
    }

    public static int getVerticalGridlNum() {
        int verticalGridNum = MIN_VERTICAL_GRID_NUM;
        if (HomeFragment.barDataList != null && !HomeFragment.barDataList.isEmpty()) {
            for (Integer i : HomeFragment.barDataList) {
                verticalGridNum = Math.max(verticalGridNum, (i / HomeFragment.multBarData + 1) * HomeFragment.multBarData);
//                    verticalGridNum = Math.max(verticalGridNum, (i+1) );
            }
        }
        verticalGridNum = verticalGridNum / HomeFragment.multBarData;
        Log.d(" getVerticalGridlNum() ", " verticalGridNum " + verticalGridNum);
        return verticalGridNum;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i = 1;

        refreshAfterDataChanged();

        drawBackgroundLines(canvas);

//        Log.d("getHeight()", " getHeight " +getHeight() +" getWidth " +getWidth());

        if (percentList != null && !percentList.isEmpty()) {
            for (Float f : percentList) {
                rect.set(BAR_SIDE_MARGIN * i + barWidth * (i - 1), topMargin,
                        (BAR_SIDE_MARGIN + barWidth) * i,
                        getHeight() - bottomTextHeight - TEXT_TOP_MARGIN);
                canvas.drawRect(rect, bgPaint);
                /*rect.set(BAR_SIDE_MARGIN*i+barWidth*(i-1),
                        topMargin+(int)((getHeight()-topMargin)*percentList.get(i-1)),
                        (BAR_SIDE_MARGIN+barWidth)* i,
                        getHeight()-bottomTextHeight-TEXT_TOP_MARGIN);*/
                /**
                 * The correct total height is "getHeight()-topMargin-bottomTextHeight-TEXT_TOP_MARGIN",not "getHeight()-topMargin".
                 */
                rect.set(BAR_SIDE_MARGIN * i + barWidth * (i - 1), topMargin + (int) ((getHeight()
                                - topMargin
                                - bottomTextHeight
                                - TEXT_TOP_MARGIN) * percentList.get(i - 1)),
                        (BAR_SIDE_MARGIN + barWidth) * i,
                        getHeight() - bottomTextHeight - TEXT_TOP_MARGIN);
                canvas.drawRect(rect, fgPaint);
                i++;
            }
        }

        if (bottomTextList != null && !bottomTextList.isEmpty()) {
            i = 1;
            for (String s : bottomTextList) {
                canvas.drawText(s, BAR_SIDE_MARGIN * i + barWidth * (i - 1) + barWidth / 2,
                        getHeight() - bottomTextDescent, textPaint);
// different color for Today in the bar
                if(s.equalsIgnoreCase(HomeFragment.dayToday)) {
                    rect.set(BAR_SIDE_MARGIN * i + barWidth * (i - 1), topMargin + (int) ((getHeight()
                                    - topMargin
                                    - bottomTextHeight
                                    - TEXT_TOP_MARGIN) * percentList.get(i - 1)),
                            (BAR_SIDE_MARGIN + barWidth) * i,
                            getHeight() - bottomTextHeight - TEXT_TOP_MARGIN);
                    canvas.drawRect(rect, fgTodayPaint);
                }

                i++;
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mViewWidth = measureWidth(widthMeasureSpec);
        mViewHeight = measureHeight(heightMeasureSpec);

        refreshAfterDataChanged();
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

/*
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mViewWidth = measureWidth(widthMeasureSpec);
        mViewHeight = measureHeight(heightMeasureSpec);
//        refreshAfterDataChanged();
        setMeasuredDimension(mViewWidth, mViewHeight);
    }
*/

    private int measureWidth(int measureSpec) {
        int preferred = 0;
        if (bottomTextList != null) {
            preferred = bottomTextList.size() * (barWidth + BAR_SIDE_MARGIN);
        }
/*
        int horizontalGridNum = getHorizontalGridNum();
        preferred = backgroundGridWidth * horizontalGridNum + sideLineLength * 2;
*/

        return getMeasurement(measureSpec, preferred);
    }

    private int getHorizontalGridNum() {
        int horizontalGridNum = bottomTextList.size() - 1;
        if (horizontalGridNum < MIN_HORIZONTAL_GRID_NUM) {
            horizontalGridNum = MIN_HORIZONTAL_GRID_NUM;
        }
        return horizontalGridNum;
    }

    private int measureHeight(int measureSpec) {
        int preferred = 222;
//        int preferred = 0;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred) {
        int specSize = MeasureSpec.getSize(measureSpec);
        int measurement;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                measurement = specSize;
                break;
            case MeasureSpec.AT_MOST:
                measurement = Math.min(preferred, specSize);
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                measurement = preferred;
                break;
        }
        return measurement;
    }
}
