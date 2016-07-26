package mushirih.pickup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import mushirih.pickup.R;

/**
 * Created by p-tah on 25/07/2016.
 */

public class TestScreen extends AppCompatActivity {
    Button one,two,three,four;
    String choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_screen);
        final Intent neww=new Intent(getBaseContext(),Register.class);
        one= (Button) findViewById(R.id.one);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice="one";

                neww.putExtra("num",choice);
                startActivity(neww);
            }
        });
        two= (Button) findViewById(R.id.two);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice="two";

                neww.putExtra("num",choice);
                startActivity(neww);
            }
        });
        three= (Button) findViewById(R.id.three);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice="three";

                neww.putExtra("num",choice);
                startActivity(neww);
            }
        });
       four= (Button) findViewById(R.id.four);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice="four";

                neww.putExtra("num",choice);
                startActivity(neww);
            }
        });



    }
}
