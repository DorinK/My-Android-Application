package com.example.myandroidapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Designing the Joystick visibility.
 */
public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private static final float RADIUS = 1.3f;

    private JoystickListener joystickCallback;

    float innerCircleRadius;
    float outerCircleRadius;

    // The x and y coordinates of the Joystick's knob.
    float xKnob;
    float yKnob;

    boolean isPressedInBase = false;

    /**
     * Constructor of the JoystickView class.
     *
     * @param context derives the activity.
     */
    public JoystickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener) {
            this.joystickCallback = (JoystickListener) context;
        }
    }

    /**
     * Initialize the parameters according to the device's sizes.
     */
    void setupView() {
        this.xKnob = (float) getWidth() / 2;
        this.yKnob = (float) getHeight() / 2;
        this.innerCircleRadius = (float) Math.min(getWidth(), getHeight()) / 3;
        this.outerCircleRadius = (float) Math.min(getWidth(), getHeight()) / 10;
    }

    /**
     * Draws the joystick base and the joystick knob according to given parameters which changes
     * interactively by the user.
     *
     * @param newX the x axis of which joystick knob should be.
     * @param newY the y axis of which joystick knob should be.
     */
    private void drawJoystick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint color = new Paint();
            // Filling the background with the chosen color.
            myCanvas.drawColor(0xFF67A6C7);
            // Drawing the outer circle.
            color.setARGB(255, 192, 192, 192);
            myCanvas.drawCircle(this.xKnob, this.yKnob, this.innerCircleRadius, color);
            // Drawing the inner circle - the Joystick's knob.
            color.setARGB(255, 0, 0, 0);
            myCanvas.drawCircle(newX, newY, this.outerCircleRadius, color);
            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    /**
     * Called immediately after the surface is first created.
     *
     * @param surfaceHolder the painter.
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // Setup the view and drawing the Joystick.
        setupView();
        drawJoystick(this.xKnob, this.yKnob);
    }

    /**
     * Updating the parameters according to the changes of the surface.
     * Called immediately after any structural changes (format / size) have been made to the surface
     *
     * @param surfaceHolder the painter.
     * @param format        the format.
     * @param width         the width.
     * @param height        the height.
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        // Re setup of the view and drawing the Joystick.
        setupView();
        drawJoystick(this.xKnob, this.yKnob);
    }

    /**
     * Called immediately before a surface is being destroyed.
     *
     * @param surfaceHolder the painter.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    /**
     * Called when a touch event is dispatched to a view
     *
     * @param view        The view the touch event has been dispatched to.
     * @param motionEvent The information about the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float displacement = (float) Math.sqrt(Math.pow(motionEvent.getX() - this.xKnob, 2)
                + Math.pow(motionEvent.getY() - this.yKnob, 2));
        float ratio = this.innerCircleRadius / displacement;

        if (displacement < (this.innerCircleRadius / RADIUS)) {
            this.isPressedInBase = true;

            if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
                drawJoystick(motionEvent.getX(), motionEvent.getY());
                this.joystickCallback.onJoystickMoved((motionEvent.getX()
                        - this.xKnob) / this.innerCircleRadius, (motionEvent.getY()
                        - this.yKnob) / this.innerCircleRadius);
            } else {
                this.isPressedInBase = false;
                drawJoystick(this.xKnob, this.yKnob);
                this.joystickCallback.onJoystickMoved(0, 0);
            }

        } else {
            if (this.isPressedInBase && motionEvent.getAction() != MotionEvent.ACTION_UP) {

                float constrainedX = this.xKnob + (motionEvent.getX() - this.xKnob) * ratio / RADIUS;
                float constrainedY = this.yKnob + (motionEvent.getY() - this.yKnob) * ratio / RADIUS;
                drawJoystick(constrainedX, constrainedY);
                this.joystickCallback.onJoystickMoved((constrainedX - this.xKnob)
                                / this.innerCircleRadius * (RADIUS),
                        (constrainedY - this.yKnob) / this.innerCircleRadius * (RADIUS));
            }

            if (this.isPressedInBase && motionEvent.getAction() == MotionEvent.ACTION_UP) {
                this.isPressedInBase = false;
                drawJoystick(this.xKnob, this.yKnob);
                this.joystickCallback.onJoystickMoved(0, 0);
            }
        }
        return true;
    }

    /**
     * Functional interface.
     * Allows to listen to the movement of the joystick's knob.
     */
    public interface JoystickListener {

        /**
         * Defines what happens when a movement is encountered.
         *
         * @param xCoordinate the x location of the knob
         * @param yCoordinate the y location of the knob
         */
        void onJoystickMoved(float xCoordinate, float yCoordinate);
    }
}