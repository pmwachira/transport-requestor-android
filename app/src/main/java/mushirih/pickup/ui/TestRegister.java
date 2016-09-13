package mushirih.pickup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mushirih.pickup.R;

/**
 * Created by p-tah on 19/07/2016.
 * ACTIVITY USED TO TEST ALL LAYOUTS
 */
 public class TestRegister extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent neww=getIntent();
        if(neww!=null)
        {
            String xs=getIntent().getStringExtra("num");
            switch (xs){
                case "one":
                    setContentView(R.layout.register);
                    break;
                case "two":
                    setContentView(R.layout.cost_est);
                    break;
                case "three":
                    setContentView(R.layout.signage);
                    break;
                case "four":
                    setContentView(R.layout.activity_maps);
                    break;
            }
        }


    }


}