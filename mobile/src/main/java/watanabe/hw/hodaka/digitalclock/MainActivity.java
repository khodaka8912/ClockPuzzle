package watanabe.hw.hodaka.digitalclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import watanabe.hw.hodaka.digitalclock.view.ClockView;
import watanabe.hw.hodaka.digitalclock.view.PhysicalCalculator;
import watanabe.hw.hodaka.digitalclock.view.ViewUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss");

    private static final Map<String, Integer> COLORS = new HashMap<String, Integer>();

    private ClockView[] mClockViews;
    private View mFrame;
    private View mClearText;

    private Timer mClockTimer;
    private Timer mPositionTimer;
    private PhysicalCalculator mCalc;
    private SharedPreferences mPreferences;

    private boolean mStarted;

    private int[] frameMap = {0,1,2,3,4,5,6,7,8};

    static {
        COLORS.put("Black", Color.BLACK);
        COLORS.put("White", Color.WHITE);
        COLORS.put("Red", Color.RED);
        COLORS.put("Blue", Color.BLUE);
        COLORS.put("Green", Color.GREEN);
        COLORS.put("Yellow", Color.YELLOW);
        COLORS.put("Gray", Color.GRAY);
        COLORS.put("Cyan", Color.CYAN);
        COLORS.put("Magenta", Color.MAGENTA);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mClockViews = new ClockView[9];
        mClockViews[0] = ViewUtils.findViewAsSubType(this, R.id.clock_view1, ClockView.class);
//        mClockViews[0].setBounds(0,0);
        mClockViews[1] = ViewUtils.findViewAsSubType(this, R.id.clock_view2, ClockView.class);
//        mClockViews[1].setBounds(200,0);
        mClockViews[2] = ViewUtils.findViewAsSubType(this, R.id.clock_view3, ClockView.class);
//        mClockViews[2].setBounds(400,0);
        mClockViews[3] = ViewUtils.findViewAsSubType(this, R.id.clock_view4, ClockView.class);
//        mClockViews[3].setBounds(0,200);
        mClockViews[4] = ViewUtils.findViewAsSubType(this, R.id.clock_view5, ClockView.class);
//        mClockViews[4].setBounds(200,200);
        mClockViews[5] = ViewUtils.findViewAsSubType(this, R.id.clock_view6, ClockView.class);
//        mClockViews[5].setBounds(400,200);
        mClockViews[6] = ViewUtils.findViewAsSubType(this, R.id.clock_view7, ClockView.class);
//        mClockViews[6].setBounds(0,400);
        mClockViews[7] = ViewUtils.findViewAsSubType(this, R.id.clock_view8, ClockView.class);
//        mClockViews[7].setBounds(200,400);
        mClockViews[8] = ViewUtils.findViewAsSubType(this, R.id.clock_view9, ClockView.class);
//        mClockViews[8].setBounds(400,400);
        mFrame= findViewById(R.id.frame);
        mClearText = findViewById(R.id.clear);
        for (int i = 0; i < 9; i++) {
            mClockViews[i].setOnClickListener(this);
            mClockViews[i].setFramePosition(i);
        }
        mCalc = new PhysicalCalculator(getApplicationContext());
        updatePosition();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View clockText = mClockViews[0].findViewById(R.id.clock);
        int tH = clockText.getHeight();
        int tW = clockText.getWidth();
        int vH = mFrame.getHeight();
        int vW = mFrame.getWidth();
        mCalc.notifyViewSize(tH, tW, vH, vW);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        String fontFamlly = mPreferences.getString("font_type", "DEFAULT");
        boolean bold = mPreferences.getBoolean("font_bold", false);
        boolean italic = mPreferences.getBoolean("font_italic", false);
        int style = bold && italic ? Typeface.BOLD_ITALIC
                : bold ? Typeface.BOLD
                : italic ? Typeface.ITALIC
                : Typeface.NORMAL;
        Typeface typeface = Typeface.create(fontFamlly, style);
        int fontColor = COLORS.get(mPreferences.getString("font_color", "Black"));
        int bgColor = COLORS.get(mPreferences.getString("bg_color", "White"));
        int size = mPreferences.getInt("font_size", 50);
        for (ClockView v : mClockViews) {
            v.setFont(typeface, fontColor, size);
            v.setBackgroundColor(bgColor);
        }
        if (mClockTimer == null) {
            mClockTimer = new Timer();
            mClockTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateClock();
                }
            }, 0, 1000);
        }
        if (mPreferences.getBoolean("gravity", true)) {
            if (mPositionTimer == null) {
                mPositionTimer = new Timer();
                mPositionTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        updatePosition();
                    }
                }, 0, 10);
            }
            mCalc.start();
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        if (mClockTimer != null) {
            mClockTimer.cancel();
            mClockTimer = null;
        }
        if (mPositionTimer != null) {
            mPositionTimer.cancel();
            mPositionTimer = null;
        }
        mCalc.stop();
    }

    private void updatePosition() {
        mCalc.calc();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float x = mCalc.getX();
                float y = mCalc.getY();
                for (ClockView view : mClockViews) {
                    if (view != null) {
                        view.setClockPosition(x, y);
                    }
                }
            }
        });
    }


    private void updateClock() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String clockText = CLOCK_FORMAT.format(new Date());
                for (ClockView view : mClockViews) {
                    if (view != null) {
                        view.setClock(clockText);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        if (v instanceof ClockView) {
            ClockView clicked = (ClockView) v;
            if (!isMovable(clicked)){
               Log.v(TAG, "notMovable");
                return;
            }
            swap(getFramePos(clicked), getSpacePosition());
        }
    }

    private int getSpacePosition() {
        for (int i = 0; i < 9; i++) {
            if (frameMap[i] == 8) {
                return i;
            }
        }
        return 0;
    }

    private void swap(final int frame1, final int frame2) {
        final int pos1 = mClockViews[frame1].getPosition();
        final int pos2 = mClockViews[frame2].getPosition();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mClockViews[frame1].setFramePosition(pos2);
                mClockViews[frame2].setFramePosition(pos1);
                updatePosition();
                frameMap[frame1] = pos2;
                frameMap[frame2] = pos1;
                if (isCompleted() && mStarted) {
                    mClockViews[frame1].setVisibility(View.VISIBLE);
                    mClockViews[frame2].setVisibility(View.VISIBLE);
                    mClearText.setVisibility(View.VISIBLE);
                    for (ClockView v : mClockViews) {
                        v.findViewById(R.id.number).setVisibility(View.INVISIBLE);
                    }
                    mStarted = false;
                    return;
                } else {
                    mClearText.setVisibility(View.INVISIBLE);
                }
                if (pos1 == 8) {
                    mClockViews[frame1].setVisibility(View.VISIBLE);
                    mClockViews[frame2].setVisibility(View.INVISIBLE);
                }
                if (pos2 == 8) {
                    mClockViews[frame1].setVisibility(View.INVISIBLE);
                    mClockViews[frame2].setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean isCompleted() {
        for (int i = 0; i < 9; i++) {
            if (frameMap[i] != i) {
                return false;
            }
        }
        return true;
    }

    private int getFramePos(ClockView v) {
        switch (v.getId()) {
            case R.id.clock_view1:
                return 0;
            case R.id.clock_view2:
                return 1;
            case R.id.clock_view3:
                return 2;
            case R.id.clock_view4:
                return 3;
            case R.id.clock_view5:
                return 4;
            case R.id.clock_view6:
                return 5;
            case R.id.clock_view7:
                return 6;
            case R.id.clock_view8:
                return 7;
            case R.id.clock_view9:
                return 8;
            default:
                return 0;
        }
    }

    private boolean isMovable(ClockView v) {
        if (!mStarted) {
            return false;
        }
        int spacePos = getSpacePosition();
        switch (v.getId()) {
            case R.id.clock_view1:
                return spacePos == 1 || spacePos == 3;
            case R.id.clock_view2:
                return spacePos == 0 || spacePos == 2 || spacePos == 4;
            case R.id.clock_view3:
                return spacePos == 1 || spacePos == 5;
            case R.id.clock_view4:
                return spacePos == 0 || spacePos == 4 || spacePos == 6;
            case R.id.clock_view5:
                return spacePos == 1 || spacePos == 3 || spacePos == 5 || spacePos == 7;
            case R.id.clock_view6:
                return spacePos == 2 || spacePos == 4 || spacePos == 8;
            case R.id.clock_view7:
                return spacePos == 3 || spacePos == 7;
            case R.id.clock_view8:
                return spacePos == 4 || spacePos == 6 || spacePos == 8;
            case R.id.clock_view9:
                return spacePos == 5 || spacePos == 7;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preferences:
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.menu_reset:
                reset();
                break;
            case R.id.menu_start:
                shuffle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reset() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 9; i++) {
                    frameMap[i] = i;
                    mClockViews[i].setFramePosition(i);
                    mClockViews[i].setVisibility(View.VISIBLE);
                    mClearText.setVisibility(View.INVISIBLE);
                    mCalc.reset();
                    updatePosition();
                }
            }
        });
    }

    private void shuffle() {
        Random random = new Random();
        for (int i = 8; i > 0; i--) {
            int r = random.nextInt(i + 1);
            int tmp = frameMap[r];
            frameMap[r] = frameMap[i];
            frameMap[i] = tmp;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 9; i++) {
                    mClockViews[i].setFramePosition(frameMap[i]);
                    if (frameMap[i] == 8) {
                        mClockViews[i].setVisibility(View.INVISIBLE);
                    } else {
                        mClockViews[i].setVisibility(View.VISIBLE);
                    }
                    if (!mPreferences.getBoolean("gravity", true)) {
                        mClockViews[i].findViewById(R.id.number).setVisibility(View.VISIBLE);
                    }
                }
                mClearText.setVisibility(View.INVISIBLE);
            }
        });
        mStarted = true;
    }
}
