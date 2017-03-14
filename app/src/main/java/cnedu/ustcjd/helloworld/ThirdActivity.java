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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import cnedu.ustcjd.widget.AudioTalkBgView;
import cnedu.ustcjd.widget.GuideView;

public class ThirdActivity extends Activity {
    private static String TAG = "ThirdActivity";
    private View ivAnim;
    private AudioTalkBgView atbv_audio_talk;
    private int count = 0;
    Animation leftIn, rightOut;

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
                Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button btnToThird = (Button) findViewById(cnedu.ustcjd.helloworld.R.id.third_btn_to_third);
        btnToThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThirdActivity.this, ThirdActivity.class);
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
                Log.d(TAG, String.format("pixel success:%x", pixel));
                btnToThird.setBackgroundColor(pixel);
            }

            @Override
            public void onError() {
                Bitmap bitMap = ((BitmapDrawable) ivPicasso.getDrawable()).getBitmap();
                int pixel = bitMap.getPixel(0, 0);
                Log.d(TAG, String.format("pixel error:%x", pixel));
                btnToThird.setBackgroundColor(pixel);
            }
        });
        //showNewbieTips();
        atbv_audio_talk = (AudioTalkBgView) findViewById(R.id.atbv_audio_talk);
        atbv_audio_talk.setBgColor(getResources().getColor(R.color.clr_white));
        atbv_audio_talk.setStrokeColor(getResources().getColor(R.color.clr_stroke_grey));
        atbv_audio_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
                if (v.isSelected()) {
                    atbv_audio_talk.setBgColor(getResources().getColor(R.color.clr_bg_red));
                    atbv_audio_talk.setStrokeColor(getResources().getColor(R.color.clr_stroke_red));
                } else {
                    atbv_audio_talk.setBgColor(getResources().getColor(R.color.clr_white));
                    atbv_audio_talk.setStrokeColor(getResources().getColor(R.color.clr_stroke_grey));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    Log.d(TAG, String.format("x:%d,y:%d", (int)ivAnim.getX(), (int)ivAnim.getY()));
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
                Log.d(TAG, "network connected change");
            } else if (action != null && action.equals(NetworkManager.Network_State_Changed)) {
                boolean isConnected = intent.getBooleanExtra(NetworkManager.Netwodk_Current_State, false);
                Log.d(TAG, "network current state:" + isConnected);
            }
        }
    };
    private void showNewbieTips() {
        ImageView tipViw = new ImageView(this);
        tipViw.setImageResource(R.drawable.duplex_audio_talk_newbie_tips);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tipViw.setLayoutParams(params);
        View targetView = ivAnim;
        final GuideView guideView = GuideView.Builder.newInstance(this)
                .setTargetView(targetView)
                .setCustomGuideView(tipViw)
                .setDirection(GuideView.Direction.RIGHT_BOTTOM)
                .setShape(GuideView.MyShape.CIRCULAR)
                .setBgColor(getResources().getColor(R.color.clr_guide_view_shadow))
                .build();
        guideView.show();
        /*mIntroduceDuplexAudioTalkDialog = new Dialog(this, R.style.FullscreenDialog);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.introduce_duplex_audiotalk_dialog, null);
        mIntroduceDuplexAudioTalkDialog.setContentView(view);
        mIntroduceDuplexAudioTalkDialog.show();

        TextView btn_exit = (TextView) view.findViewById(R.id.hemu_dialog_introduce_duplex_audiotalk_tv_i_know);*/
        tipViw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //preference.putBoolean(Common.Show_DuplexAudioTalkIntroduce, false).commit();

                /*mIntroduceDuplexAudioTalkDialog.dismiss();
                mShowingIntroduceDuplexAudioTalk = false;*/
                guideView.hide();
            }
        });
    }
    public void showTips(View v) {
        showNewbieTips();
    }
}
