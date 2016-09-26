package com.closeli.draggableviewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jd5737 on 2016/8/29.
 */
public class GridItemView {
    private Context mContext;
    private String mName;
    private View rootView;
    private TextView textView;
    private static final int UPDATE_TIME_LINE = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_TIME_LINE:
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());
                    String msg = sdf.format(new Date());
                    textView.setText(mName + ":" + msg);
                    sendEmptyMessageDelayed(UPDATE_TIME_LINE, 1000);
                    break;
                default:
                    break;
            }
        }
    };

    public GridItemView(Context context, String itemName) {
        mContext = context;
        mName = itemName;
    }

    private void initViews() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.my_text_view, null, false);
        textView = (TextView) rootView.findViewById(R.id.info_text);
        mHandler.sendEmptyMessageDelayed(UPDATE_TIME_LINE, 1000);
    }

    public View getView() {
        if (rootView == null) {
            initViews();
        }
        return rootView;
    }
}
