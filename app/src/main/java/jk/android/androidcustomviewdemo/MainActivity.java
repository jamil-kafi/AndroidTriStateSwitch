package jk.android.androidcustomviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jk.android.tristateswitch.TriStateSwitch;

public class MainActivity extends AppCompatActivity {

    private TriStateSwitch twoSidedSwitch;
    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoSidedSwitch = (TriStateSwitch) findViewById(R.id.tss);
        button = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);

        twoSidedSwitch.setCallback(new TriStateSwitch.ICallback() {
            @Override
            public void onSideChangeEnded(TriStateSwitch.SIDE side) {
                textView.setText(side.name());
            }

            @Override
            public void onSideChangeStarted(TriStateSwitch.SIDE side) {
                super.onSideChangeStarted(side);
                // do something when side starts to change
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // twoSidedSwitch.setThumbColor(getResources().getColor(R.color.green));

                // twoSidedSwitch.setSide(TwoSidedSwitch.SIDE.LEFT);

                if (twoSidedSwitch.getThumbShape() == TriStateSwitch.THUMB_SHAPE_RECTANGLE) {
                    twoSidedSwitch.setThumbShape(TriStateSwitch.THUMB_SHAPE_CIRCLE, true);
                } else {
                    twoSidedSwitch.setThumbShape(TriStateSwitch.THUMB_SHAPE_RECTANGLE, true);
                }

                /*if (twoSidedSwitch.getVisibility() == View.VISIBLE) {
                    twoSidedSwitch.setVisibility(View.GONE);
                } else {
                    twoSidedSwitch.setVisibility(View.VISIBLE);
                }*/
            }
        });
    }
}
