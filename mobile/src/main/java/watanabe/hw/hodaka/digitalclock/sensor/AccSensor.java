package watanabe.hw.hodaka.digitalclock.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by hodaka on 2016/10/03.
 */

public class AccSensor {

    private final Context mContext;
    private final SensorManager mManager;

    private SensorEventConverter mListener;

    public AccSensor(Context context) {
        mContext = context;
        mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void registerListener(final AccEventListener listener) {
        if (mListener != null) {
            mManager.unregisterListener(mListener);
        }
        mListener = new SensorEventConverter(listener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Handler handler = new Handler();
                Sensor sensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mManager.registerListener(mListener, sensor, 10, handler);
                Looper.loop();
                Log.d("acc", "registerListener");
            }
        }).start();
    }

    public void unregisterListener() {
        if (mListener != null) {
            mManager.unregisterListener(mListener);
        }
    }

    private static class SensorEventConverter implements SensorEventListener {

        private final AccEventListener mListener;

        SensorEventConverter(AccEventListener listener) {
            mListener = listener;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            mListener.onAccChanged(-event.values[0], event.values[1], event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
