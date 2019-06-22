package com.example.myandroidapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private JoystickListener joystick;
    private float interiorRadius; // the interior radius
    private float exteriorRadius; // the exterior radius
    private float dy;
    private float dx;

    private float height() {
        return (float) getHeight();
    }

    private float width() {
        return (float) getWidth();
    }

    public JoystickView(Context theContext, AttributeSet attributes, int style) {
        super(theContext, attributes, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (theContext instanceof JoystickListener)
            joystick = (JoystickListener) theContext;
    }

    public JoystickView(Context theContext, AttributeSet attributes) {
        super(theContext, attributes);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (theContext instanceof JoystickListener)
            joystick = (JoystickListener) theContext;
    }

    public JoystickView(Context theContext) {
        super(theContext);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (theContext instanceof JoystickListener)
            joystick = (JoystickListener) theContext;
    }

    private void drawJoystick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            Canvas thisCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            thisCanvas.drawColor(0xff33b5e5);
            //Draw the base first before shading
            colors.setARGB(255, 192, 192, 192);
            thisCanvas.drawCircle(dx, dy, exteriorRadius, colors);
            //draw the handle
            colors.setARGB(255, 0, 0, 0);
            thisCanvas.drawCircle(newX, newY, interiorRadius, colors);
            getHolder().unlockCanvasAndPost(thisCanvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        dx = width() / 2;
        dy = height() / 2;
        float min = Math.min(width(), height());
        exteriorRadius = min / 3;
        interiorRadius = min / 12;
        drawJoystick(dx, dy);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        //not necessary for our needs
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //not necessary for our needs
    }

    private float norm(float p, float cp) {
        return (p - cp) / exteriorRadius;
    }

    public boolean onTouch(View view, MotionEvent event) {
        // when there is a touch in the screen
        if (view.equals(this)) {
            // check if there is a touch
            if (event.getAction() != MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();
                // make sure joystick out of bounderies
                float distance = (float) Math.sqrt((Math.pow(x - dx, 2)) + Math.pow(y - dy, 2));
                if (distance < exteriorRadius) {
                    // draw joystick
                    drawJoystick(x, y);
                    // notify listeners
                    joystick.onJoystickMoved(norm(x, dx), norm(y, dy));
                } else {
                    // make sure the x and y are in the boundries
                    float ratio = exteriorRadius / distance;
                    float tx = dx + (x - dx) * ratio;  // bounded x
                    float ty = dy + (x - dy) * ratio;
                    drawJoystick(tx, ty);
                    // notify listeners
                    joystick.onJoystickMoved(norm(tx, dx), norm(ty, dy));
                }
            } else {
                // if there is no touch, return joystick to the middle
                drawJoystick(dx, dy);
                joystick.onJoystickMoved(0, 0);
            }
        }
        return true;
    }

    // interface for the joystick's listeners
    public interface JoystickListener {
        void onJoystickMoved(float x, float y);
    }
}