package com.nissawinagong.sosscreen;

import com.nissawinagong.sosscreen.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SOSActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    /**
     * True if the background is dark.
     */
    private boolean mDark = false;

    private FrameLayout mFullFrame;
    private TextView    mFrameText;
    private static final int mDotMS = 300;
    private static final int mDashMS = 800;
    private static final int mSpaceMS = 800;
    private static final int mCycleMS = 3000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //hide the title bar

        setContentView(R.layout.activity_sos);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        SOSFlash();
    }

    private void SOSFlash() {
        mFullFrame = (FrameLayout) findViewById(R.id.fullscreen);
        mFrameText = (TextView)    findViewById(R.id.fullscreen_content);

        if(!mDark)
        {
            swapColors();
        }

        new Thread(new Runnable() { public void run() {

            try {Thread.sleep(1000);} catch (Exception e){}
            while (true) {

                threeX(mDotMS);
                try {Thread.sleep(mSpaceMS);} catch (Exception e){}
                threeX(mDashMS);
                threeX(mDotMS);

                try {Thread.sleep(mSpaceMS);} catch (Exception e){}
                runOnUiThread(new Runnable() { @Override public void run() { //stuff that updates ui
                    mFrameText.setTextColor(Color.YELLOW);
                }});

                try {Thread.sleep(mCycleMS);} catch (Exception e){}

                runOnUiThread(new Runnable() { @Override public void run() { //stuff that updates ui
                    mFrameText.setTextColor(Color.BLACK);
                }});
                try {Thread.sleep(mSpaceMS);} catch (Exception e){}

            }

        }}).start();
    }

    private void threeX(int signalTime) {
        for (int i = 0; i < 3; i++) {
            swapColors();
            try { Thread.sleep(signalTime);} catch (Exception e) {}

            swapColors();
            try { Thread.sleep(signalTime);} catch (Exception e) {}
        }
    }

    private void swapColors(/* final FrameLayout fullFrame, final TextView frameText*/ ) {
        if (mDark)
        {
            runOnUiThread(new Runnable() { @Override public void run() { //stuff that updates ui
                mFullFrame.setBackgroundColor(Color.WHITE);
                mFrameText.setTextColor(Color.RED);
            }});
            mDark = false;
        }
        else {
            runOnUiThread(new Runnable() { @Override public void run() { //stuff that updates ui
                mFullFrame.setBackgroundColor(Color.BLACK);
                mFrameText.setTextColor(Color.BLACK);
            }});
            mDark = true;
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
