package mushirih.pickup.pdf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import mushirih.pickup.R;


public class MainActivity extends Activity {

    final static int cameraData=0;
   public static Paint mpaint;
    public static Bitmap mBitmap,confirmPic;
   Context contextt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        contextt = this;
       final DrawingView view= (DrawingView) findViewById(R.id.draw);
        Button button= (Button) findViewById(R.id.submitSign);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Bitmap b = view.getBitmap();
                                          if (b != null && confirmPic!=null) {
                                              try {
                                                  PDF pdf = new PDF(contextt, b,confirmPic);
                                                  view.setVisibility(View.GONE);
                                                  Toast.makeText(getApplicationContext(),"Sign Saved",Toast.LENGTH_LONG).show();
                                              } catch (IOException e) {
                                                  e.printStackTrace();
                                              }
                                          }else{
                                              Toast.makeText(contextt,"Nothing to submit",Toast.LENGTH_LONG).show();
                                          }
                                      }
                                  }
            );
         Button button1= (Button) findViewById(R.id.clearSign);
        button1.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {


                view.clear();
                                       }
                                   }
        );

            Button button2= (Button) findViewById(R.id.confirmSign);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //i.setPackage("com.android.camera");
                    if(i.resolveActivity(getPackageManager())!=null) {
                        startActivityForResult(i, cameraData);
                    }
                }
            });


            mpaint=new Paint();

            mpaint.setAntiAlias(true);
            mpaint.setDither(true);
            mpaint.setColor(Color.GREEN);
            mpaint.setStyle(Paint.Style.STROKE);
            mpaint.setStrokeJoin(Paint.Join.ROUND);
            mpaint.setStrokeCap(Paint.Cap.ROUND);
            mpaint.setStrokeWidth(12);

        }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            confirmPic = (Bitmap) extras.get("data");

        }
    }

        public static class DrawingView extends View {
        public int height;
        public int width;

        private Canvas mcanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;
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
        }


        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.WHITE);
            mcanvas = new Canvas(mBitmap);

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

}

