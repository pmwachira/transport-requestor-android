package mushirih.pickup.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.ImageView;

import mushirih.pickup.R;

/**
 * Created by p-tah on 21/06/2016.
 */
public class Move extends Activity{
    private static final int RightToLeft=1;
    private static final int LeftToRight=2;
    private static final int duration=5000;

    private ValueAnimator animator;
    private final Matrix matrix=new Matrix();
private ImageView imageView;
    private float scaleFactor=2;
    private int direction=RightToLeft;
    private RectF rectF=new RectF();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);
        imageView= (ImageView) findViewById(R.id.imageview);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                scaleFactor = imageView.getHeight() /imageView.getDrawable().getIntrinsicHeight();
                matrix.postScale(scaleFactor, scaleFactor);
                imageView.setImageMatrix(matrix);
                animate(rectF.left, rectF.left - (rectF.right - imageView.getWidth()));
            }
        });
    }

    private void animate() {
        updateDisplayRect();
        if(direction==RightToLeft){
            animate(rectF.left,rectF.left-(rectF.right - imageView.getWidth()));
        }else{
            animate(rectF.left,0.0f);
        }

    }

    private void animate( float from,float to) {
        animator=ValueAnimator.ofFloat(from,to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                matrix.reset();
                matrix.postScale(scaleFactor,scaleFactor);
                matrix.postTranslate(value,0);

                imageView.setImageMatrix(matrix);

            }
        });
        animator.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(direction == RightToLeft){
                    direction=LeftToRight;
                }else{
                    direction=RightToLeft;
                }
                animate();
            }
        });
        animator.start();
    }

    private void updateDisplayRect() {
   rectF.set(0,0,imageView.getDrawable().getIntrinsicWidth(),imageView.getDrawable().getIntrinsicHeight());
        matrix.mapRect(rectF);
    }

}

