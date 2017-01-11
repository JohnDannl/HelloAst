package com.closeli.jd5737.roundedimageview;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private int location = 0;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView)findViewById(R.id.camera_model_iv_image);
        Picasso.with(this).load(R.drawable.onepiece_1).resize(64, 96).transform(new RoundedTransformation(0, 0)).into(imageView);
        final Drawable icon = getResources().getDrawable(R.drawable.camera_type_hemu_c11);
        mTextView = ((TextView)findViewById(R.id.tv_img));
        mTextView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        findViewById(R.id.btn_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location ++;
                if (location == 4) {
                    location = 0;
                }
                switch (location) {
                    case 0:
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                        break;
                    case 1:
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
                        break;
                    case 2:
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
                        break;
                    case 3:
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon);
                        break;
                    default:
                        break;
                }
            }
        });
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        //animation.setInterpolator(new LinearInterpolator());
        View iv = findViewById(R.id.img_rotate);
        iv.clearAnimation();
        iv.startAnimation(animation);
    }
}
class RoundedTransformation implements com.squareup.picasso.Transformation {
    private final int radius;
    private final int margin; // dp

    // radius is corner radii in dp
    // margin is the board in dp
    public RoundedTransformation(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth()
                - margin, source.getHeight() - margin), radius, radius, paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}