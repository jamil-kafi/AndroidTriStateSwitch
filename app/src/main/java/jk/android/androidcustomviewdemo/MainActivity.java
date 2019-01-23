package jk.android.androidcustomviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import jk.android.twosidedswitch.TwoSidedSwitch;

public class MainActivity extends AppCompatActivity {

    private TwoSidedSwitch twoSidedSwitch;
    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoSidedSwitch = (TwoSidedSwitch) findViewById(R.id.tss);
        button = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);

        twoSidedSwitch.setCallback(new TwoSidedSwitch.ICallback() {
            @Override
            public void onSideChangeEnded(TwoSidedSwitch.SIDE side) {
                textView.setText(side.name());
            }

            @Override
            public void onSideChangeStarted(TwoSidedSwitch.SIDE side) {
                super.onSideChangeStarted(side);
                // do something when side starts to change
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // twoSidedSwitch.setThumbColor(getResources().getColor(R.color.green));
                twoSidedSwitch.setSide(TwoSidedSwitch.SIDE.LEFT);
            }
        });
    }
}
