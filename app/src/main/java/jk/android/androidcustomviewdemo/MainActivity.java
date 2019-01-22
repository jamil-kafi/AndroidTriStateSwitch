package jk.android.androidcustomviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jk.android.twosidedswitch.TwoSidedSwitch;

public class MainActivity extends AppCompatActivity {

    private TwoSidedSwitch twoSidedSwitch;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoSidedSwitch = (TwoSidedSwitch) findViewById(R.id.tss);
        button = (Button) findViewById(R.id.btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twoSidedSwitch.setThumbColor(getResources().getColor(R.color.green));
            }
        });
    }
}
