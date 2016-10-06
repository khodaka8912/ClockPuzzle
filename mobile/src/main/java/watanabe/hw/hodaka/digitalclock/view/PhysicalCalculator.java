package watanabe.hw.hodaka.digitalclock.view;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import watanabe.hw.hodaka.digitalclock.sensor.AccEventListener;
import watanabe.hw.hodaka.digitalclock.sensor.AccSensor;

/**
 * Created by hodaka on 2016/10/05.
 */

public class PhysicalCalculator implements AccEventListener {
    public static final double BOUND = 0.9;

    private volatile float mX;
    private volatile float mY;
//    private volatile float mRotation;
    private volatile float mSpeedX;
    private volatile float mSpeedY;
    private volatile float mAccX;
    private volatile float mAccY;
    private static int mInterval = 10;
    private static float mScale = 11.0f;
    private volatile int mTextHeight;
    private volatile int mTextWidth;
    private volatile int mViewHeight;
    private volatile int mViewWidth;

    private Timer mTimer;
    private final AccSensor mAccSensor;

    public PhysicalCalculator(Context context) {
        mAccSensor = new AccSensor(context);
    }

    public void start() {
        mAccSensor.registerListener(this);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                calc();
            }
        }, 0, mInterval);
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mAccSensor.unregisterListener();
    }


    public void reset() {
        mSpeedX = mSpeedY = 0f;
        mX = (mViewWidth - mTextWidth) / 2f;
        mY = (mViewHeight - mTextHeight) / 2f;
    }

    public void calc() {
        mX += (float)(mSpeedX * mInterval / 1000d * mScale);
        mY += (float)(mSpeedY * mInterval / 1000d * mScale);
        if (mY < 0) {
            mY = 0;
            mSpeedY = -(float)(mSpeedY * BOUND);
        } else if (mY > mViewHeight - mTextHeight) {
            mY = mViewHeight - mTextHeight;
            mSpeedY = -(float)(mSpeedY * BOUND);
        }
        if (mX < 0) {
            mX = 0;
            mSpeedX = -(float)(mSpeedX * BOUND);
        } else if (mX + mTextWidth > mViewWidth) {
            mX = mViewWidth - mTextWidth;
            mSpeedX = -(float)(mSpeedX * BOUND);
        }
        mSpeedX = (float)(mSpeedX * 0.99 + mAccX * mInterval / 1000d * mScale);
        mSpeedY = (float)(mSpeedY * 0.99 + mAccY * mInterval / 1000d * mScale);

    }

    public void notifyViewSize(int textHeight, int textWidth, int viewHeight,int viewWidth) {
        mTextHeight = textHeight;
        mTextWidth = textWidth;
        mViewHeight = viewHeight;
        mViewWidth = viewWidth;
    }


    @Override
    public void onAccChanged(float accX, float accY, float accZ) {
//        Log.d(TAG, "accChanged");
        mAccX = accX;
        mAccY = accY;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }
}
