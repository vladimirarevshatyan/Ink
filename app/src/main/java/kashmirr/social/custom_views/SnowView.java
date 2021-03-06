package kashmirr.social.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import kashmirr.social.utils.SharedHelper;
import kashmirr.social.utils.SnowFlake;

/**
 * Created by PC-Comp on 1/9/2017.
 */

public class SnowView extends View {
    private static final int NUM_SNOWFLAKES = 150;
    private static final int DELAY = 5;

    private SnowFlake[] snowflakes;
    private SharedHelper sharedHelper;
    private int oldWidth;
    private int oldHeight;

    public SnowView(Context context) {
        super(context);
        initSharedHelper(context);
    }

    private void initSharedHelper(Context context) {
        if (sharedHelper == null) {
            sharedHelper = new SharedHelper(context);
        }
    }

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSharedHelper(context);
    }

    public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSharedHelper(context);
    }

    protected void resize(int width, int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        snowflakes = new SnowFlake[NUM_SNOWFLAKES];
        for (int i = 0; i < NUM_SNOWFLAKES; i++) {
            snowflakes[i] = SnowFlake.create(width, height, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        oldWidth = w;
        oldHeight = h;
        if (sharedHelper.showSnow()) {
            if (w != oldw || h != oldh) {
                resize(w, h);
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sharedHelper.showSnow()) {
            if (snowflakes == null) {
                resize(oldWidth, oldHeight);
            }
            for (SnowFlake snowFlake : snowflakes) {
                snowFlake.draw(canvas);
            }
            getHandler().postDelayed(runnable, DELAY);
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
}