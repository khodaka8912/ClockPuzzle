package watanabe.hw.hodaka.digitalclock.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import watanabe.hw.hodaka.digitalclock.R;

/**
 * Created by hodaka on 2016/10/02.
 */

public final class ClockView extends RelativeLayout {

    private static final String TAG = ClockView.class.getSimpleName();

    private static final int FRAME_SIZE = 300;
    private int mX;
    private int mY;
    private int mPosition;

    private final TextView mClockText;
    private final TextView mNumberText;
    private final View mRoot;
    private OnClickListener mListener;


    public ClockView(Context context) {
        this(context, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mListener != null) mListener.onClick(this);
        }
        return super.dispatchTouchEvent(event);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = inflater.inflate(R.layout.view_clock, this);
        mClockText = ViewUtils.findViewAsSubType(mRoot, R.id.clock, TextView.class);
        mNumberText = ViewUtils.findViewAsSubType(mRoot, R.id.number, TextView.class);
    }

    public void setBounds(int x, int y) {
        mX = x;
        mY = y;
    }

    public void setFramePosition(int position) {
        mPosition = position;
        mNumberText.setText(Integer.toString(position));
        mX = FRAME_SIZE * (position % 3);
        mY = FRAME_SIZE * (position / 3);
    }

    public int getPosition() {
       return mPosition;
    }

    public void setClockPosition(float x, float y) {
        mClockText.setTranslationX(x - mX);
        mClockText.setTranslationY(y - mY);
    }

    public void setClock(String clockText) {
        mClockText.setText(clockText);
    }

    public void setFont(Typeface t, int color) {
        mClockText.setTypeface(t);
        mClockText.setTextColor(color);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mListener = l;
    }
}
