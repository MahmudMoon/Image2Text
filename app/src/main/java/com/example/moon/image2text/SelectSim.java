package com.example.moon.image2text;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

public class SelectSim extends AppCompatActivity {

    RadioGroup radioGroup;
    String finalUSSD;
    Intent intent;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sim);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Netword");
        init_views();
        init_variales();
        init_functions();
        init_listeners();
    }

    private void init_views() {
        radioGroup = (RadioGroup)findViewById(R.id.rg_sims);
    }

    private void init_variales() {
        intent = getIntent();
        number = intent.getStringExtra("number");
    }

    private void init_functions() {

    }

    private void init_listeners() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_airtel:
                        finalUSSD = "*121*"+number+ Uri.encode("#");
                        makeCall(finalUSSD);
                        break;
                    case R.id.rb_banglalink:
                        finalUSSD = "*121*"+number+ Uri.encode("#");
                        makeCall(finalUSSD);
                        break;
                    case R.id.rb_gp:
                        finalUSSD = "*555*"+number+ Uri.encode("#");
                        makeCall(finalUSSD);
                        break;
                    case R.id.rb_robi:
                        finalUSSD = "*111*"+number+ Uri.encode("#");
                        makeCall(finalUSSD);
                        break;
                    case R.id.rb_teletalk:
                        finalUSSD = "*121*"+number+ Uri.encode("#");
                        makeCall(finalUSSD);
                        break;
                }
            }
        });
    }

    private void makeCall(String finalUSSD) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + finalUSSD));
            startActivity(intent);
        }

}
