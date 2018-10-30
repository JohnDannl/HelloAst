package cnedu.ustcjd.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import cnedu.ustcjd.util.DeviceInfo;
import cnedu.ustcjd.widget.AudioTalkBgView;
import cnedu.ustcjd.widget.AudioTalkingView;
import cnedu.ustcjd.widget.GuideView;

import static android.util.Log.d;

public class GuideViewActivity extends Activity {
    private static String TAG = "ThirdActivity";
    private View ivAnim;
    private AudioTalkBgView talkView, talkBgView2;
    private AudioTalkingView mAudioTalkingView;
    private int count = 0;
    Animation leftIn, rightOut;
    private GuideView guideView;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cnedu.ustcjd.helloworld.R.layout.activity_third);
        final TextView tv_msg = (TextView) findViewById(cnedu.ustcjd.helloworld.R.id.third_tv_msg);
        tv_msg.setText(this.toString()+"@"+this.getTaskId());

        Button btnToMain = (Button) findViewById(cnedu.ustcjd.helloworld.R.id.third_btn_to_main);
        btnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button btnToThird = (Button) findViewById(cnedu.ustcjd.helloworld.R.id.third_btn_to_third);
        btnToThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideViewActivity.this, GuideViewActivity.class);
                startActivity(intent);
            }
        });

        initAnimation();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetworkManager.Network_Connected_Changed);
        filter.addAction(NetworkManager.Network_State_Changed);
        registerReceiver(receiver, filter);

        final ImageView ivPicasso = (ImageView) findViewById(R.id.iv_picasso);
        //String picUrl = "https://hoa.and-home.cn:8043/upload/module/background_rest_on.png";
        String picUrl = "https://hoa.and-home.cn:8043/upload/module/background_at_on.png";
        Drawable defaultDrawable = getResources().getDrawable(R.drawable.al_switch_on);
        Picasso.with(this).load(picUrl).error(defaultDrawable).placeholder(defaultDrawable).resize(500, 180).into(ivPicasso, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitMap = ((BitmapDrawable) ivPicasso.getDrawable()).getBitmap();
                int pixel = bitMap.getPixel(5, 5);
                d(TAG, String.format("pixel success:%x", pixel));
                btnToThird.setBackgroundColor(pixel);
            }

            @Override
            public void onError() {
                Bitmap bitMap = ((BitmapDrawable) ivPicasso.getDrawable()).getBitmap();
                int pixel = bitMap.getPixel(0, 0);
                d(TAG, String.format("pixel error:%x", pixel));
                btnToThird.setBackgroundColor(pixel);
            }
        });

        talkView = (AudioTalkBgView) findViewById(R.id.atbv_audio_talk);
        talkView.setBgColor(getResources().getColor(R.color.clr_white));
        talkView.setStrokeColor(getResources().getColor(R.color.clr_stroke_grey));
        talkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d(TAG, "audio talk on click");
                if (v.isSelected()) {
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
                if (v.isSelected()) {
                    talkView.setBgColor(getResources().getColor(R.color.clr_bg_red));
                    talkView.setStrokeColor(getResources().getColor(R.color.clr_stroke_red));
                } else {
                    talkView.setBgColor(getResources().getColor(R.color.clr_white));
                    talkView.setStrokeColor(getResources().getColor(R.color.clr_stroke_grey));
                }
                getScreenSizeInDp(GuideViewActivity.this);
            }
        });
        talkView.setAudioTalkViewListener(new AudioTalkBgView.IAudioTalkViewListener() {
            @Override
            public void onTouch() {
                Toast.makeText(GuideViewActivity.this, "on Touch", Toast.LENGTH_SHORT).show();
                d(TAG, "audio talk on touch");
                animationShowAudioTalk();
            }

            @Override
            public void onRelease() {
                Toast.makeText(GuideViewActivity.this, "on Release", Toast.LENGTH_SHORT).show();
                d(TAG, "audio talk on release");
                animationHideAudioTalk();
            }
        });
        float[] display = getScreenSizeInDp(this);
        if (display[0] <= 600) { //大屏可以到640
            int smallPadding = 28 * 3;
            int bigMargin = 8 * 3;
            talkView.setPadding(smallPadding, smallPadding, smallPadding, smallPadding);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) talkView.getLayoutParams();
            mlp.setMargins(bigMargin, 0 ,bigMargin, 0);
            talkView.setLayoutParams(mlp);
        }
        mAudioTalkingView = (AudioTalkingView) findViewById(R.id.player1bar_bottom_atv_talking);

        talkBgView2 = (AudioTalkBgView) findViewById(R.id.atbv_audio_talk2);
        //showNewbieTips();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showComplexNewbieTips();
            }
        }, 500);
        Log.d(TAG, "device Info:" + DeviceInfo.getDeviceInfo(this));
        //fileTest();
        deviceInfoTest();
    }

    private void deviceInfoTest() {
        Log.d(TAG + "Y", DeviceInfoUtils.getIPAddress(true));
        Log.d(TAG + "Y", DeviceInfoUtils.getIPAddress(false));
        Log.d(TAG + "Y", "eth0:" + DeviceInfoUtils.getMACAddress("eth0"));
        Log.d(TAG + "Y", "wlan0:" + DeviceInfoUtils.getMACAddress("wlan0"));
        Log.d(TAG + "Y", "pid:" + DeviceInfoUtils.getMyProcessPid());
        Log.d(TAG + "Y", "hex ip:" + DeviceInfoUtils.getHexIPAddress(DeviceInfoUtils.getIPAddress(true)));
    }
    private void fileTest() {
        String fileDir = getApplicationContext().getFilesDir().getAbsolutePath();
        Log.d(TAG + "X", "getApplicationContext().getFilesDir().getAbsolutePath():" + fileDir);
        String extDir = getApplicationContext().getExternalFilesDir("").getAbsolutePath();
        Log.d(TAG + "X", "getApplicationContext().getExternalFilesDir(\"\").getAbsolutePath():" + extDir);
        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d(TAG + "X", "Environment.getExternalStorageDirectory().getAbsolutePath():" + sdDir);
        final File recDir = new File(Environment.getExternalStorageDirectory(),  "CloseliTestbed/Video/");
        Log.d(TAG + "X", "new File(Environment.getExternalStorageDirectory(),  \"CloseliTestbed/Video/\"):" + recDir.exists());
        final File tmpDir = new File(Environment.getExternalStorageDirectory(),  "CloseliTestbed/Tmp/");
        Log.d(TAG + "X", "tmpDir exist:" + tmpDir.exists());
        boolean suc = tmpDir.mkdir();
        Log.d(TAG + "X", "tmpDir mkdir:" + suc);
        final File testFile = new File(Environment.getExternalStorageDirectory(),  "CloseliTestbed/Tmp/test.txt");
        Log.d(TAG + "X", "test File exist:" + testFile.exists());
        Log.d(TAG + "X", "test File Parent exists:" + new File(testFile.getParent()).exists());
        if (!new File(testFile.getParent()).exists()) {
            Log.d(TAG + "X", "test create Parent:" + new File(testFile.getParent()).mkdir());
        }
        try {
            Log.d(TAG + "X", "test create File:" + testFile.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG + "X", "test File exist:" + testFile.exists());
        Log.d(TAG + "X", "test del File:" + testFile.delete());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (guideView != null && guideView.isShowing()) {
            guideView.hide();
        }
        unregisterReceiver(receiver);
    }

    private void initAnimation() {
        ivAnim = findViewById(cnedu.ustcjd.helloworld.R.id.iv_anim);
        leftIn = AnimationUtils.loadAnimation(this, cnedu.ustcjd.helloworld.R.anim.slide_left_in);
        leftIn.setFillAfter(true);
        rightOut = AnimationUtils.loadAnimation(this, cnedu.ustcjd.helloworld.R.anim.slide_right_out);
        rightOut.setFillAfter(true);
    }

    public void onClickBtnAnim(View v) {
        switch(count) {
            case 0:
                ivAnim.startAnimation(leftIn);
                count += 1;
                break;
            case 1:
                ivAnim.startAnimation(rightOut);
                count += 1;
                break;
            case 2:
                ivAnim.clearAnimation();
                count = 0;
                break;
        }
    }
    private StatisticThread posThread = new StatisticThread();
    private class StatisticThread extends Thread {
        private volatile boolean paused = false;
        public void run() {
            while(true) {
                if (ivAnim != null && !paused) {
                    d(TAG, String.format("x:%d,y:%d", (int)ivAnim.getX(), (int)ivAnim.getY()));
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void pause() {
            this.paused = true;
        }
        public void restart() {
            this.paused = false;
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(NetworkManager.Network_Connected_Changed)) {
                d(TAG, "network connected change");
            } else if (action != null && action.equals(NetworkManager.Network_State_Changed)) {
                boolean isConnected = intent.getBooleanExtra(NetworkManager.Netwodk_Current_State, false);
                d(TAG, "network current state:" + isConnected);
            }
        }
    };
    private void showNewbieTips() {
        ImageView tipViw = new ImageView(this);
        tipViw.setImageResource(R.drawable.duplex_audio_talk_newbie_tips);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tipViw.setLayoutParams(params);
        guideView = GuideView.Builder.newInstance(this)
                .setTargetView(talkView)
                .setCustomGuideView(tipViw)
                .setDirection(GuideView.Direction.TOP)
                .setShape(GuideView.MyShape.CIRCULAR)
                .setBgColor(getResources().getColor(R.color.clr_guide_view_shadow))
                .setStripStatusBar(true)
                .build();
        guideView.show();

        tipViw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideView.hide();
            }
        });
    }
    private void showComplexNewbieTips() {
        View tipView = LayoutInflater.from(this).inflate(R.layout.dialog_tip_hot, null);
        guideView = GuideView.Builder.newInstance(this)
                .setTargetView(talkView)
                .addAnotherTargetView(talkBgView2)
                .setCustomGuideView(tipView)
                .setDirection(GuideView.Direction.BOTTOM)
                .setOffset(0, getResources().getDimensionPixelSize(R.dimen.al_tip_hot_activity_offSetY))
                .setShape(GuideView.MyShape.CIRCULAR)
                .setBgColor(getResources().getColor(R.color.clr_guide_view_shadow))
                .setStripStatusBar(true)
                .build();
        guideView.show();

        tipView.findViewById(R.id.iv_i_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideView.hide();
            }
        });
    }
    public void showTips(View v) {
        //showNewbieTips();
        showComplexNewbieTips();
    }
    private void animationShowAudioTalk() {
        final View talkingView = findViewById(R.id.player1bar_bottom_rl_talking);
        if (talkingView.getVisibility() == View.VISIBLE) {
            return;
        } else {
            mAudioTalkingView.enableDecileAnimation(true);
            talkingView.clearAnimation();
            talkingView.setVisibility(View.VISIBLE);
            talkingView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out_to_normal));
        }

        final View talkView = findViewById(R.id.player1bar_bottom_rl_talk);
        final Animation zoomSmallAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out_to_small);
        zoomSmallAnimation.setFillAfter(true);
        zoomSmallAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //				talkView.setVisibility(View.GONE);
            }
        });
        talkView.startAnimation(zoomSmallAnimation);

    }

    private void animationHideAudioTalk() {
        final View talkingView = findViewById(R.id.player1bar_bottom_rl_talking);
        if (talkingView.getVisibility() == View.VISIBLE) {
            mAudioTalkingView.enableDecileAnimation(false);
            final Animation zoomLargeAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_to_large);
            zoomLargeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    talkingView.setVisibility(View.GONE);
                }
            });
            talkingView.clearAnimation();
            talkingView.setVisibility(View.VISIBLE);
            talkingView.startAnimation(zoomLargeAnimation);
        } else {
            return;
        }
        final View talkView = findViewById(R.id.player1bar_bottom_rl_talk);
        talkView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_to_normal));
    }

    private float[] getScreenSizeInDp(Context context) {
        DisplayMetrics dspMtr = context.getResources().getDisplayMetrics();
        float width = dspMtr.widthPixels / dspMtr.density;
        float height = dspMtr.heightPixels / dspMtr.density;
        d(TAG, String.format("density:%s,pixel:(%s,%s),dp:(%s,%s)",
                dspMtr.density, dspMtr.heightPixels, dspMtr.widthPixels, height, width));
        return new float[] {height, width};
    }

 }
