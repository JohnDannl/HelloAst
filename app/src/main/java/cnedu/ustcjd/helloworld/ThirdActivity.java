package cnedu.ustcjd.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class ThirdActivity extends Activity {
    private static String TAG = "Animation";
    private View ivAnim;
    private int count = 0;
    Animation leftIn, rightOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ustc.jd.helloworld.R.layout.activity_third);
        TextView tv_msg = (TextView) findViewById(com.ustc.jd.helloworld.R.id.third_tv_msg);
        tv_msg.setText(this.toString()+"@"+this.getTaskId());

        Button btnToMain = (Button) findViewById(com.ustc.jd.helloworld.R.id.third_btn_to_main);
        btnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button btnToThird = (Button) findViewById(com.ustc.jd.helloworld.R.id.third_btn_to_third);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initAnimation() {
        ivAnim = findViewById(com.ustc.jd.helloworld.R.id.iv_anim);
        leftIn = AnimationUtils.loadAnimation(this, com.ustc.jd.helloworld.R.anim.slide_left_in);
        leftIn.setFillAfter(true);
        rightOut = AnimationUtils.loadAnimation(this, com.ustc.jd.helloworld.R.anim.slide_right_out);
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
}
