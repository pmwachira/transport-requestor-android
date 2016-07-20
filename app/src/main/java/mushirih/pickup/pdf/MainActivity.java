package mushirih.pickup.pdf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import mushirih.pickup.R;


public class MainActivity extends Activity {

    final static int cameraData=0;

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



        }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            confirmPic = (Bitmap) extras.get("data");

        }
    }

}

