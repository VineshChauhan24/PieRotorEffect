package rotor.pierotoreffect;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by harneev on 5/4/2015.
 */
public class CircleLayout extends View {

    private Context mContext;

    private int mCircleX, mCircleY;
    private int mRadius = 0;

    private float mInitialX = 0, mInitialY = 0;
    private double mStartAngle;

    private Paint mPaint;

    private boolean mFingerRotating = false;

    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCircleX = getWidth() / 2;
        mCircleY = getHeight() / 2;

        mRadius = getWidth() / 8;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);

        if (mFingerRotating) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.RED);
            RectF rectF = new RectF(mCircleX - mRadius, mCircleY
                    - mRadius, mCircleX + mRadius, mCircleY + mRadius);

            float rot = -((float) mStartAngle - (float) getAngle(mInitialX,
                    mInitialY));

            if (rot < 0)
                rot = rot + 360;

            canvas.drawArc(rectF, -(float) getAngle(mInitialX, mInitialY), rot,
                    true, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialX = event.getX();
                mInitialY = event.getY();


                if (isInCircle(mInitialX, mInitialY)) {
                    mFingerRotating = true;
                    mStartAngle = getAngle(mInitialX, mInitialY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isInCircle(event.getX(), event.getY())) {
                    mFingerRotating = true;
                    mStartAngle = getAngle(event.getX(), event.getY());
                }
                
                break;
            case MotionEvent.ACTION_UP:
                mFingerRotating = false;
                break;
        }
        invalidate();
        return true;
    }

    private boolean isInCircle(float x, float y) {
        if (Math.pow(x - mCircleX, 2) + Math.pow(y - mCircleY, 2) <= Math.pow(mRadius, 2)) {
            return true;
        }

        return false;
    }

    public double getAngle(double touchX, double touchY) {
        double x = touchX - mCircleX;
        double y = mCircleY - touchY;

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

            case 2:
            case 3:
                return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);

            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

            default:
                return 0;
        }
    }

    static int getQuadrant(double x, double y) {
        // Log.e(TAG, "(" + x + ", " + y + ")");
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        // smooths
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(15.0f);
        mPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
    }
}
