package com.nissawinagong.sosscreen;

import com.nissawinagong.sosscreen.util.SystemUiHider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
     * The number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;

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

        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, SystemUiHider.FLAG_HIDE_NAVIGATION);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    @Override
                    public void onVisibilityChange(boolean visible) {
                        if (visible) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSystemUiHider.toggle();
            }
        });
    }

    private void SOSFlash() {
        mFullFrame = (FrameLayout) findViewById(R.id.fullscreen);
        mFrameText = (TextView)    findViewById(R.id.fullscreen_content);

        new Thread(new Runnable() { public void run() {
            try {Thread.sleep(mSpaceMS*2);} catch (Exception e){}
            mDark = false;
            swapColors();
            try {Thread.sleep(mSpaceMS);} catch (Exception e){}

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

        SOSFlash();
    }

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
