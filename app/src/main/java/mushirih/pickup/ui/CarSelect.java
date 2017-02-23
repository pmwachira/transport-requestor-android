package mushirih.pickup.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mushirih.pickup.R;


public class CarSelect extends AppCompatActivity  {
    @InjectView(R.id.rg)
    RadioGroup rg;
    @InjectView(R.id.one)
    ImageView one;
    @InjectView(R.id.two)
    ImageView two;
    @InjectView(R.id.three)
    ImageView three;
    @InjectView(R.id.four)
    ImageView four;
    @InjectView(R.id.five)
    ImageView five;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_select);
        ButterKnife.inject(this);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rg.check(R.id.radioButton1);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rg.check(R.id.radioButton2);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rg.check(R.id.radioButton3);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rg.check(R.id.radioButton4);
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rg.check(R.id.radioButton5);
            }
        });

    }
}
