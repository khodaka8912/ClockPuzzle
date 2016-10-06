package watanabe.hw.hodaka.digitalclock.sensor;

/**
 * Created by hodaka on 2016/10/03.
 */

public interface AccEventListener {

    void onAccChanged(float accX, float accY, float accZ);
}
