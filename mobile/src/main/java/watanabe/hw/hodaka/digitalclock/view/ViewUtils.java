package watanabe.hw.hodaka.digitalclock.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by hodaka on 2016/10/03.
 */

public class ViewUtils {

    public static <T> T findViewAsSubType(View view, int resId, Class<T> type) {
        return type.cast(view.findViewById(resId));
    }

    public static <T> T findViewAsSubType(Activity activity, int resId, Class<T> type) {
        return type.cast(activity.findViewById(resId));
    }
}
