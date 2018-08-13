package cnedu.ustcjd.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class CustomPopupMenu {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private View mAnchorView;
    private View mContentView;
    private View mIndicatorView;
    private float mRawX;
    private float mRawY;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mPopupWindowWidth;
    private int mPopupWindowHeight;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mBackgroundCornerRadiusPixel;

    public CustomPopupMenu(Context context, View anchorView, View contentView, boolean isLand) {
        this.mContext = context;
        this.mContentView = contentView;
        this.mAnchorView = anchorView;
        this.mIndicatorView = getDefaultIndicatorView(dp2px(16), dp2px(8), 0x7000000);

        if (mScreenWidth == 0) {
            mScreenWidth = getScreenWidth();
        }
        if (mScreenHeight == 0) {
            mScreenHeight = getScreenHeight();
        }

        int[] anchorLocation = new int[2];
        mAnchorView.getLocationOnScreen(anchorLocation);
        mRawX = anchorLocation[0];
        mRawY = anchorLocation[1];
    }

    public void show() {
        if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
            return;
        }
        if (mPopupWindow == null) {
            LinearLayout contentView = new LinearLayout(mContext);
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentView.setOrientation(LinearLayout.VERTICAL);
            LinearLayout popupWindowContainer = new LinearLayout(mContext);
            popupWindowContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            popupWindowContainer.setOrientation(LinearLayout.HORIZONTAL);
            contentView.addView(popupWindowContainer);
            if (mIndicatorView != null) {
                LinearLayout.LayoutParams layoutParams;
                if (mIndicatorView.getLayoutParams() == null) {
                    layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                } else {
                    layoutParams = (LinearLayout.LayoutParams) mIndicatorView.getLayoutParams();
                }
                layoutParams.gravity = Gravity.CENTER;
                mIndicatorView.setLayoutParams(layoutParams);
                contentView.addView(mIndicatorView);
            }
            popupWindowContainer.addView(mContentView);
            if (mPopupWindowWidth == 0) {
                mPopupWindowWidth = getViewWidth(popupWindowContainer);
            }
            if (mIndicatorView != null && mIndicatorWidth == 0) {
                if (mIndicatorView.getLayoutParams().width > 0) {
                    mIndicatorWidth = mIndicatorView.getLayoutParams().width;
                } else {
                    mIndicatorWidth = getViewWidth(mIndicatorView);
                }
            }
            if (mIndicatorView != null && mIndicatorHeight == 0) {
                if (mIndicatorView.getLayoutParams().height > 0) {
                    mIndicatorHeight = mIndicatorView.getLayoutParams().height;
                } else {
                    mIndicatorHeight = getViewHeight(mIndicatorView);
                }
            }
            if (mPopupWindowHeight == 0) {
                mPopupWindowHeight = getViewHeight(popupWindowContainer) + mIndicatorHeight;
            }
            mPopupWindow = new PopupWindow(contentView, mPopupWindowWidth, mPopupWindowHeight, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        if (mIndicatorView != null) {
            float marginLeftScreenEdge = mRawX + getViewWidth(mAnchorView) / 2f;
            float marginRightScreenEdge = mScreenWidth - (mRawX + getViewWidth(mAnchorView) / 2);
            if (marginLeftScreenEdge < mPopupWindowWidth / 2f) {
                // in case of the draw of indicator out of Screen's bounds
                if (marginLeftScreenEdge < mIndicatorWidth / 2f + mBackgroundCornerRadiusPixel) {
                    mIndicatorView.setTranslationX(mIndicatorWidth / 2f + mBackgroundCornerRadiusPixel - mPopupWindowWidth / 2f);
                } else {
                    mIndicatorView.setTranslationX(marginLeftScreenEdge - mPopupWindowWidth / 2f);
                }
            } else if (marginRightScreenEdge < mPopupWindowWidth / 2f) {
                if (marginRightScreenEdge < mIndicatorWidth / 2f + mBackgroundCornerRadiusPixel) {
                    mIndicatorView.setTranslationX(mPopupWindowWidth / 2f - mIndicatorWidth / 2f - mBackgroundCornerRadiusPixel);
                } else {
                    mIndicatorView.setTranslationX(mPopupWindowWidth / 2f - marginRightScreenEdge);
                }
            } else {
                mIndicatorView.setTranslationX(0);
            }
        }

        int deltaX = (int) mRawX + getViewWidth(mAnchorView) / 2 - mPopupWindowWidth / 2;
        /*switch(mPosition) {
            case TOP_LEFT:
                deltaX = (int) mRawX + getViewWidth(mAnchorView) / 2 - mPopupWindowWidth;
                break;
            case TOP_RIGHT:
                deltaX = (int) mRawX + getViewWidth(mAnchorView) / 2;
                break;
        }*/
        mPopupWindow.showAtLocation(mAnchorView, Gravity.TOP | Gravity.START,
                deltaX,(int) mRawY - mPopupWindowHeight);
    }

    private boolean isLand() {
        return mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    private View getDefaultIndicatorView(final float widthPixel, final float heightPixel,
                                         final int color) {
        ImageView indicator = new ImageView(mContext);
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);
                path.moveTo(0f, 0f);
                path.lineTo(widthPixel, 0f);
                path.lineTo(widthPixel / 2, heightPixel);
                path.close();
                canvas.drawPath(path, paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }

            @Override
            public int getIntrinsicWidth() {
                return (int) widthPixel;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) heightPixel;
            }
        };
        indicator.setImageDrawable(drawable);
        return indicator;
    }

    private int dp2px(float value) {
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    private int getViewWidth(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }

    private int getViewHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public void dismiss() {
        if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
            return;
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }
}
