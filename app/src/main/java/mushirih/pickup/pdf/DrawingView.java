package mushirih.pickup.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by p-tah on 20/07/2016.
 */
public  class DrawingView extends View {
    public static Paint mpaint;
    public int height;
    public int width;

    private Canvas mcanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    public static Bitmap mBitmap;
    //  SeekBar seekbar;



    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        context = context;
        mPath = new Path();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        mpaint=new Paint();

        mpaint.setAntiAlias(true);
        mpaint.setDither(true);
        mpaint.setColor(Color.BLUE);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeJoin(Paint.Join.ROUND);
        mpaint.setStrokeCap(Paint.Cap.ROUND);
        mpaint.setStrokeWidth(12);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.WHITE);
        mcanvas = new Canvas(mBitmap);
//        mcanvas.drawText("Draw Here",0,0,circlePaint);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mpaint);
        canvas.drawPath(circlePath, circlePaint);

    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CCW);

        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();

        mcanvas.drawPath(mPath, mpaint);
        mPath.reset();
    }
    public boolean onTouchEvent(MotionEvent event){
        float x=event.getX();
        float y=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touch_start(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                touch_move(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }

        return true;

    }
    public Bitmap getBitmap()
    {

        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);


        return bmp;
    }



    public void clear(){
        mBitmap.eraseColor(Color.WHITE);

        invalidate();
        System.gc();

    }

}