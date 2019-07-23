package com.example.localflix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;

public class VlcPlayer extends Activity implements IVideoPlayer {

    private static final String TAG = "lakhinsu";

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    private SurfaceView mSurfaceView;
    private FrameLayout mSurfaceFrame;
    private SurfaceHolder mSurfaceHolder;
    private Surface mSurface = null;

    ImageButton imageButton;
    SeekBar seekBar;
    TextView totalTimeview;

    MediaController controller;

    private LibVLC mLibVLC;

    private String mMediaUrl;

    Handler handlerSeekbar,handlerOverlay;
    Runnable runnableSeekbar,runnableOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vlc_player);

        Log.d(TAG, "VideoVLC -- onCreate -- START ------------");

        imageButton=findViewById(R.id.playButton);
        seekBar=findViewById(R.id.seekBar);
        totalTimeview=findViewById(R.id.videoctime);

        imageButton.setImageDrawable(this.getDrawable(R.drawable.ic_pause_black_24dp));

        mSurfaceView = (SurfaceView) findViewById(R.id.player_surface);
        mSurfaceHolder = mSurfaceView.getHolder();

        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        mMediaUrl = getIntent().getExtras().getString("url");

       hideSystemUI();





        handlerOverlay = new Handler();
        runnableOverlay = new Runnable() {
            @Override
            public void run() {
                //vlcOverlay.setVisibility(View.GONE);
                imageButton.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
                totalTimeview.setVisibility(View.GONE);
                hideSystemUI();
            }
        };
        final long timeToDisappear = 3000;
        handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
        mSurfaceFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vlcOverlay.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);
                seekBar.setVisibility(View.VISIBLE);
                totalTimeview.setVisibility(View.VISIBLE);
                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLibVLC.isPlaying()){
                    mLibVLC.pause();
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                }
                else
                {
                    mLibVLC.play();
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp));
                }
            }
        });

        try {
            mLibVLC=new LibVLC();

            mLibVLC.setAout(mLibVLC.AOUT_AUDIOTRACK);
            mLibVLC.setVout(mLibVLC.VOUT_ANDROID_SURFACE);
            mLibVLC.setHardwareAcceleration(LibVLC.DEV_HW_DECODER_OMX);


            mLibVLC.init(getApplicationContext());
        } catch (LibVlcException e){
            Log.e(TAG, e.toString());
        }

        mSurface = mSurfaceHolder.getSurface();

        mLibVLC.attachSurface(mSurface, VlcPlayer.this);
        mLibVLC.setHardwareAcceleration(LibVLC.DEV_HW_DECODER_OMX);
        mLibVLC.setHttpReconnect(true);
        Log.d("lakhinsu",""+mLibVLC.getAudioTrack());
        mLibVLC.playMRL(mMediaUrl);
        mLibVLC.setFrameSkip(true);


        handlerSeekbar = new Handler();
        runnableSeekbar = new Runnable() {
            @Override
            public void run() {
                if (mLibVLC != null) {
                    long curTime = mLibVLC.getTime();
                    long totalTime = (long) (curTime / mLibVLC.getPosition());
                    int minutes = (int) (curTime / (60 * 1000));
                    int seconds = (int) ((curTime / 1000) % 60);
                    int endMinutes = (int) (totalTime / (60 * 1000));
                    int endSeconds = (int) ((totalTime / 1000) % 60);
                    String duration = String.format("%02d:%02d / %02d:%02d", minutes, seconds, endMinutes, endSeconds);
                    seekBar.setProgress((int) (mLibVLC.getPosition() * 100));
                    totalTimeview.setText(duration);
                }
                handlerSeekbar.postDelayed(runnableSeekbar, 1000);

            }

        };
        runnableSeekbar.run();



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.v("NEW POS", "pos is : " + i);
                if(b) {
                    if (i != 0)
                        mLibVLC.setPosition(((float) i / 100.0f));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // MediaCodec opaque direct rendering should not be used anymore since there is no surface to attach.
        mLibVLC.stop();
    }

    @Override
    public void onBackPressed() {
        Log.d("lakhinsu","here");
        //controller.hide();
        super.onBackPressed();
    }

    public void eventHardwareAccelerationError() {
        Log.e(TAG, "eventHardwareAccelerationError()!");
        return;
    }

    @Override
    public void setSurfaceLayout(final int width, final int height, int visible_width, int visible_height, final int sar_num, int sar_den){
        Log.d(TAG, "setSurfaceSize -- START");
        if (width * height == 0)
            return;

        // store video size
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;

        Log.d(TAG, "setSurfaceSize -- mMediaUrl: " + mMediaUrl + " mVideoHeight: " + mVideoHeight + " mVideoWidth: " + mVideoWidth + " mVideoVisibleHeight: " + mVideoVisibleHeight + " mVideoVisibleWidth: " + mVideoVisibleWidth + " mSarNum: " + mSarNum + " mSarDen: " + mSarDen);
    }

    @Override
    public int configureSurface(android.view.Surface surface, int i, int i1, int i2){
        return -1;
    }


}
