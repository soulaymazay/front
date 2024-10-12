package com.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DragLinearLayout extends LinearLayout {
    private int initialX;
    private int initialY;
    private int initialTouchX;
    private int initialTouchY;

    public DragLinearLayout(Context context) {
        super(context);
    }

    public DragLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = getLeft();
                initialY = getTop();
                initialTouchX = (int) event.getRawX();
                initialTouchY = (int) event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) event.getRawX() - initialTouchX;
                int deltaY = (int) event.getRawY() - initialTouchY;
                setX(initialX + deltaX);
                setY(initialY + deltaY);
                return true;
        }
        return super.onTouchEvent(event);
    }
}
