package com.nissawinagong.sosscreen;

import com.nissawinagong.sosscreen.util.SystemUiHider;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
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
    private String      mOnlyHelp;
    private float       mTextSizeLandscape;
    private float       mTextSizePortrait;

    private static final int mDotMS = 300;
    private static final int mDashMS = 800;
    private static final int mSpaceMS = 800;
    private static final int mCycleMS = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //hide the title bar

        setContentView(R.layout.activity_sos);

        final TextView contentView = (TextView)findViewById(R.id.fullscreen_content);
        mFrameText = contentView;
        mFullFrame = (FrameLayout) findViewById(R.id.fullscreen);
        mOnlyHelp = mFrameText.getText().toString().
                substring(0, mFrameText.getText().toString().indexOf('\n'));
        mTextSizePortrait =
                mTextSizeLandscape = mFrameText.getTextSize();

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
        new Thread(new Runnable() { public void run() {
            runOnUiThread(new Runnable() { @Override public void run() { //stuff that updates ui
                mFrameText.setText(mOnlyHelp);
            }});

            try {Thread.sleep(mCycleMS);} catch (Exception e){}
            sizeText();
            runOnUiThread(new Runnable() { @Override public void run() { //stuff that updates ui
                mFrameText.setTextColor(Color.YELLOW);
            }});

            try {Thread.sleep(mCycleMS);} catch (Exception e){}
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

    private void swapColors() {

        sizeText();
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

    private boolean sizeText() {
        boolean sizeChanged = false;
        float textSize;
        float textWidth = mFrameText.getPaint().measureText(mOnlyHelp);
        final float frameWidth =  mFrameText.getWidth();
        final float textScale = mFrameText.getTextScaleX();

        while (
                ( textWidth
                      > frameWidth        )
//                ||
//                ( mFrameText.getTextSize() * 2
//                      >         mFrameText.getHeight() )
              )
        {
            sizeChanged = true;
            textSize = mFrameText.getTextSize();
            textSize *= textScale;
            textSize /= 2;
            textSize -= 1;
            final float newTextSize = textSize;

            runOnUiThread(new Runnable() { @Override public void run() { //stuff that updates ui
//                mFrameText.setTextColor(mFullFrame.getSolidColor());
                mFrameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, newTextSize);
            }});
            try {Thread.sleep(2);} catch (Exception e){}

            textWidth = mFrameText.getPaint().measureText(mOnlyHelp);

//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                mTextSizeLandscape = mFrameText.getTextSize();
//                mTextSizePortrait = mFrameText.getTextSize();
//            }
//            else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//            {
//                mTextSizePortrait = mFrameText.getTextSize();
//            }
        }
        return sizeChanged;
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

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mFrameText.setTextSize(mTextSizeLandscape);
//        }
//        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
//        {
//            mFrameText.setTextSize(mTextSizePortrait);
//        }
//    }
}
