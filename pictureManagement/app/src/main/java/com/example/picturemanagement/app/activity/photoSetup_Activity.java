package com.example.picturemanagement.app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.example.picturemanagement.app.R;

public class photoSetup_Activity extends AppCompatActivity {

    private CheckBox cb_sgd;
    private Button btn_fanhui;
    private Boolean isSGD = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_setup_main);
        cb_sgd = (CheckBox) findViewById(R.id.cb_sgd);
        cb_sgd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    isSGD = true;
                }
                else {
                    isSGD = false;
                }
            }
        });
        btn_fanhui = (Button) findViewById(R.id.btn_fanhui);
        btn_fanhui.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("isSGD", isSGD);
                setResult(0, intent);
                finish();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            Intent intent = new Intent();
            intent.putExtra("isSGD", cb_sgd.isSelected());
            setResult(0, intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
