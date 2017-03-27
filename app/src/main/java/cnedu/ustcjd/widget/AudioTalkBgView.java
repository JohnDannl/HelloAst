package cnedu.ustcjd.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import cnedu.ustcjd.helloworld.R;

/**
 * Created by jd5737 on 2017/3/10.
 */

public class AudioTalkBgView extends ImageView {
    private static final String TAG = "AudioTalkBgView";
    private Paint bgPaint;
    private Paint strokePaint;
    private static final float defaultOffsetY = 21F;
    private static final float defaultShadowRadius = 15F;
    private static final float defaultStrokeWidth = 1F;
    private float shadowRadius;
    private float offsetY;
    private float strokeWidth;
    private int bgColor;
    private int shadowColor;
    private int strokeColor;

    private boolean touched = false;
    private IAudioTalkViewListener touchListener;

    public AudioTalkBgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AudioTalkBgView,
                0, 0
        );
        try {
            shadowRadius = typedArray.getDimension(R.styleable.AudioTalkBgView_shadowRadius, defaultShadowRadius);
            offsetY = typedArray.getDimension(R.styleable.AudioTalkBgView_shadowOffsetY, defaultOffsetY);
            bgColor = typedArray.getColor(R.styleable.AudioTalkBgView_backgroundColor, getResources().getColor(R.color.clr_bg_red));
            shadowColor = typedArray.getColor(R.styleable.AudioTalkBgView_shadowColor, getResources().getColor(R.color.clr_shadow_red));
            strokeColor = typedArray.getColor(R.styleable.AudioTalkBgView_strokeColor, getResources().getColor(R.color.clr_stroke_grey));
            strokeWidth = typedArray.getDimension(R.styleable.AudioTalkBgView_strokeWidth, defaultStrokeWidth);
        } finally {
            typedArray.recycle();
        }
        init();
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);
        bgPaint.setStrokeWidth(3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, bgPaint);
        }
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制阴影，param1：模糊半径；param2：x轴偏移：param3：y轴偏移；param4：阴影颜色
        bgPaint.setShadowLayer(shadowRadius, 0F, offsetY, shadowColor);
        int radius = (getWidth() > getHeight() ? getHeight() : getWidth()) / 2;
        Log.d(TAG, "radius:" + radius + ", shadowRadius:" + shadowRadius);
        if (radius > (shadowRadius + offsetY)) {
            radius -= (int) (shadowRadius + offsetY);
        }
        Log.d(TAG, "radius:" + radius);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, bgPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, strokePaint);
        super.onDraw(canvas);
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        bgPaint.setColor(bgColor);
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        strokePaint.setColor(strokeColor);
        invalidate();
    }

    public interface IAudioTalkViewListener {
        void onTouch();
        void onRelease();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchCenterBitmap(event.getX(), event.getY())) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!touched) {
                        touched = true;
                        notifyTouchChanged(touched);
                    }
                    super.onTouchEvent(event);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (touched) {
                        touched = false;
                        notifyTouchChanged(touched);
                    }
                    break;
            }
        } else {
            if (touched) {
                touched = false;
                notifyTouchChanged(touched);
            }
        }
        return super.onTouchEvent(event);
    }

    public void setAudioTalkViewListener(final IAudioTalkViewListener l) {
        touchListener = l;
    }

    private boolean touchCenterBitmap(final float x, final float y) {
        if (Math.sqrt(Math.pow((x - getWidth() / 2), 2) + Math.pow((y - getHeight() / 2), 2)) <= getWidth() / 2) {
            return true;
        }
        return false;
    }

    private void notifyTouchChanged(final boolean touched) {

        if (touched) {
            if (touchListener != null) {
                touchListener.onTouch();
            }
        } else {
            if (touchListener != null) {
                touchListener.onRelease();
            }
        }
    }
}
