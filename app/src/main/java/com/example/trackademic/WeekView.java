package com.example.trackademic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

public class WeekView extends View {
    private static final int START_HOUR = 6;  // 6 AM
    private static final int END_HOUR = 22;   // 10 PM
    private static final int TOTAL_HOURS = END_HOUR - START_HOUR;
    private static final int HOUR_HEIGHT = 120; // pixels per hour
    private static final int TIME_COLUMN_WIDTH = 150;
    private static final int DAY_COLUMN_WIDTH = 200;
    private static final int HEADER_HEIGHT = 100;

    private Paint timeTextPaint;
    private Paint eventPaint;
    private Paint eventTextPaint;
    private Paint gridLinePaint;
    private Paint headerTextPaint;

    private List<ClassEvent> events;
    private Scroller scroller;
    private GestureDetector gestureDetector;
    private int[] eventColors;

    public static class ClassEvent {
        String name;
        int dayOfWeek;
        int startHour;
        int startMinute;
        int endHour;
        int endMinute;
        int colorIndex;

        public ClassEvent(String name, int dayOfWeek, int startHour, int startMinute,
                          int endHour, int endMinute, int colorIndex) {
            this.name = name;
            this.dayOfWeek = dayOfWeek;
            this.startHour = startHour;
            this.startMinute = startMinute;
            this.endHour = endHour;
            this.endMinute = endMinute;
            this.colorIndex = colorIndex;
        }
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        events = new ArrayList<>();
        scroller = new Scroller(getContext());
        gestureDetector = new GestureDetector(getContext(), new WeekViewGestureListener());

        timeTextPaint = new Paint();
        timeTextPaint.setTextSize(32);
        timeTextPaint.setColor(0xFF666666);
        timeTextPaint.setAntiAlias(true);

        eventPaint = new Paint();
        eventPaint.setAntiAlias(true);

        eventTextPaint = new Paint();
        eventTextPaint.setColor(0xFFFFFFFF);
        eventTextPaint.setTextSize(32);
        eventTextPaint.setAntiAlias(true);

        gridLinePaint = new Paint();
        gridLinePaint.setColor(0xFFEEEEEE);
        gridLinePaint.setStrokeWidth(2);

        headerTextPaint = new Paint();
        headerTextPaint.setTextSize(36);
        headerTextPaint.setColor(0xFF333333);
        headerTextPaint.setAntiAlias(true);

        eventColors = new int[]{
                0xFF4FC3F7, // Light Blue
                0xFFFFB74D, // Orange
                0xFF81C784, // Green
                0xFFE57373, // Red
                0xFF9575CD  // Purple
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw time column
        for (int i = 0; i <= TOTAL_HOURS; i++) {
            int currentHour = START_HOUR + i;
            float y = HEADER_HEIGHT + (i * HOUR_HEIGHT);
            canvas.drawLine(TIME_COLUMN_WIDTH, y, getWidth(), y, gridLinePaint);

            // Format time string
            String timeText;
            if (currentHour == 12) {
                timeText = "12:00 PM";
            } else if (currentHour == 0 || currentHour == 24) {
                timeText = "12:00 AM";
            } else if (currentHour > 12) {
                timeText = String.format("%d:00 PM", currentHour - 12);
            } else {
                timeText = String.format("%d:00 AM", currentHour);
            }

            float textWidth = timeTextPaint.measureText(timeText);
            float textX = TIME_COLUMN_WIDTH - textWidth - 15;
            float textY = y + 35;
            canvas.drawText(timeText, textX, textY, timeTextPaint);
        }

        // Draw day columns
        String[] days = {"Mon", "Tues", "Wed", "Thurs", "Fri"};
        for (int i = 0; i < days.length; i++) {
            float x = TIME_COLUMN_WIDTH + (i * DAY_COLUMN_WIDTH);
            canvas.drawText(days[i], x + 10, HEADER_HEIGHT - 20, headerTextPaint);
            canvas.drawLine(x, 0, x, getHeight(), gridLinePaint);
        }

        // Draw events
        for (ClassEvent event : events) {
            drawEvent(canvas, event);
        }
    }

    private void drawEvent(Canvas canvas, ClassEvent event) {
        float startY = HEADER_HEIGHT +
                ((event.startHour - START_HOUR + (event.startMinute / 60f)) * HOUR_HEIGHT);
        float endY = HEADER_HEIGHT +
                ((event.endHour - START_HOUR + (event.endMinute / 60f)) * HOUR_HEIGHT);
        float left = TIME_COLUMN_WIDTH + ((event.dayOfWeek - 1) * DAY_COLUMN_WIDTH) + 4;
        float right = left + DAY_COLUMN_WIDTH - 8;

        RectF eventRect = new RectF(left, startY, right, endY);

        eventPaint.setColor(eventColors[event.colorIndex % eventColors.length]);
        canvas.drawRoundRect(eventRect, 8, 8, eventPaint);

        // Draw event text
        canvas.drawText(event.name, left + 10, startY + 40, eventTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = TIME_COLUMN_WIDTH + (5 * DAY_COLUMN_WIDTH);
        int height = HEADER_HEIGHT + (TOTAL_HOURS * HOUR_HEIGHT);
        setMeasuredDimension(width, height);
    }

    public void addEvent(String name, int dayOfWeek, int startHour, int startMinute,
                         int endHour, int endMinute) {
        events.add(new ClassEvent(name, dayOfWeek, startHour, startMinute,
                endHour, endMinute, events.size()));
        invalidate();
    }

    public void removeAllEvents() {
        events.clear();
        invalidate();
    }

    private class WeekViewGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
            scrollBy((int)distanceX, (int)distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            scroller.fling(getScrollX(), getScrollY(), -(int)velocityX, -(int)velocityY,
                    0, computeHorizontalScrollRange() - getWidth(),
                    0, computeVerticalScrollRange() - getHeight());
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected int computeVerticalScrollRange() {
        return HEADER_HEIGHT + (TOTAL_HOURS * HOUR_HEIGHT);
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return TIME_COLUMN_WIDTH + (5 * DAY_COLUMN_WIDTH);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }
}