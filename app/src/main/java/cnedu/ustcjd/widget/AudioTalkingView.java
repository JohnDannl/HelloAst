package cnedu.ustcjd.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by jd5737 on 2017/3/15.
 */

public class AudioTalkingView extends View {
    private int Color_Circle_Light = 0x1AE40077;
    private int Color_Circle_Decile = 0xFF8FC31F;
    private int Color_Circle_Center = 0xFFE40077;

    private final Paint mLightCirclePaint = new Paint();
    private final Paint mDecileCirclePaint = new Paint();
    private final Paint mCenterCirclePaint = new Paint();

    private float Max_Decile = 100;
    private float mDensity = 1;
    /**
     * 外圆原线的宽度
     */
    private float mStrokeWidth = 4;
    /**
     * 外圆和内圆的半径差
     */
    private float mRadiusDiff = 15;
    private boolean mDecileAnimationEnabled = true;

    private RectF mDecileRectF = new RectF();
    private float mCurrentDecile = Max_Decile / 3;

    public AudioTalkingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mDensity = getResources().getDisplayMetrics().density;
        mStrokeWidth = mDensity * 2;
        mRadiusDiff *= mDensity;

        /*Color_Circle_Light = 0xFFABECD5;
        Color_Circle_Decile = 0xFF2AD998;
        Color_Circle_Center = 0xFFDBFFF2;*/

        mCenterCirclePaint.setColor(Color_Circle_Center);
        mCenterCirclePaint.setStyle(Paint.Style.FILL);

        mDecileCirclePaint.setColor(Color_Circle_Decile);
        mDecileCirclePaint.setStyle(Paint.Style.STROKE);
        mDecileCirclePaint.setStrokeWidth(mStrokeWidth);

        mLightCirclePaint.setColor(Color_Circle_Light);
        mLightCirclePaint.setStyle(Paint.Style.STROKE);
        mLightCirclePaint.setStrokeWidth(mStrokeWidth);

    }

    public void setDecile(final float decile) {
        if (mCurrentDecile != decile) {
            float toDecile = mCurrentDecile;
            if (decile < 0) {
                toDecile = 0;
            } else if (decile > Max_Decile) {
                toDecile = Max_Decile;
            } else {
                toDecile = decile;
            }
            animateToDecile(mCurrentDecile, toDecile);
        }
    }

    private void animateToDecile(final float from, final float to) {
        cancelDecileAnimation();

        final Animation mToDecileAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                if (mDecileAnimationEnabled) {
                    mCurrentDecile = from + (to - from) * interpolatedTime;
                    invalidate();
                }
            }
        };
        mToDecileAnimation.setDuration(300);
        mToDecileAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        startAnimation(mToDecileAnimation);
    }

    public void enableDecileAnimation(final boolean enable) {
        mDecileAnimationEnabled = enable;
        if (!mDecileAnimationEnabled) {
            cancelDecileAnimation();
        }
    }

    private void cancelDecileAnimation() {
        final Animation animation = getAnimation();
        if (animation != null) {
            animation.cancel();
            clearAnimation();
        }
    }

    @Override
    public void onDraw(final Canvas canvas) {
        float lightCircleRadius = ((getWidth() < getHeight() ? getWidth() : getHeight()) - mStrokeWidth) / 2;
        float centerCircleRadius = lightCircleRadius;
        if (getPaddingLeft() > mRadiusDiff) {
            centerCircleRadius -= mRadiusDiff;
        }

        final float centerX = getWidth() / 2;
        final float centerY = getHeight() /2;
        if (lightCircleRadius > centerCircleRadius) {
            canvas.drawCircle(centerX, centerY, lightCircleRadius, mLightCirclePaint);

            final float sweepAngle = 360.0f * mCurrentDecile / Max_Decile;
            final float startAngle = 90 - sweepAngle / 2;
            mDecileRectF.set(centerX - lightCircleRadius, centerY - lightCircleRadius, centerX + lightCircleRadius, centerY + lightCircleRadius);
            canvas.drawArc(mDecileRectF, startAngle, sweepAngle, false, mDecileCirclePaint);
        }
        canvas.drawCircle(centerX, centerY, centerCircleRadius, mCenterCirclePaint);

        super.onDraw(canvas);
    }
}
