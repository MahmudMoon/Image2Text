package com.example.moon.image2text;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.lang.reflect.Method;

public class OpenCamera extends AppCompatActivity {

    ImageView iv;
    ImageButton btn,goToNExt,btncrop;
    TextView tv_show;
    TextRecognizer textRecognizer;
    File file;
    File mydirc;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        init_views();
        init_variables();
        init_listeners();
        init_functions();
    }

    private void init_functions() {
          goToNExt.setVisibility(View.INVISIBLE);
    }

    private void init_listeners() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(file);
            }
        });

        btncrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        goToNExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(OpenCamera.this,new String[]{Manifest.permission.CALL_PHONE},3);
                }else {
                    Intent intent = new Intent(OpenCamera.this, SelectSim.class);
                    intent.putExtra("number", tv_show.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

    private void takePhoto(File file) {
            try {
                Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                startActivityForResult(i, 100);
            } catch (ActivityNotFoundException e) {
                // showImagePickerError(activity);
            }

//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mydirc.toString()+"/temp.jpg")));
//        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode == 100){
            Bitmap photo = BitmapFactory.decodeFile(mydirc + "/temp.jpg");
            Bitmap.createBitmap(photo);
            iv.setImageBitmap(photo);


        }

        if(resultCode==RESULT_OK && requestCode == Crop.REQUEST_CROP){
            iv.setImageURI(Crop.getOutput(data));
            String number = getTextFromImage();
            tv_show.setText(number);
            if(TextUtils.isEmpty(number)){
                goToNExt.setVisibility(View.INVISIBLE);
            }else
                goToNExt.setVisibility(View.VISIBLE);

        }
    }

    private void cropImage() {
        Uri des = Uri.fromFile(new File(getCacheDir(),"Croppedd"));
        Uri src = Uri.fromFile(new File(mydirc+"/temp.jpg"));
        Crop.of(src,des).withAspect(800,150).start(this);
        iv.setImageURI(des);
    }



    private String getTextFromImage() {
        StringBuilder stringBuilder = new StringBuilder();
        if(!textRecognizer.isOperational()){
            Toast.makeText(getApplicationContext(),"Not Operational",Toast.LENGTH_SHORT).show();
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this,"Need to clear some space and reinstall", Toast.LENGTH_LONG).show();
            }
            return null;
        }else {
            Drawable drawable = iv.getDrawable();
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            if (items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    TextBlock block = items.valueAt(i);
                    String value = block.getValue();
                    stringBuilder.append(value);
                }
                return stringBuilder.toString();
            }else {
                Toast.makeText(getApplicationContext(),"No text Found",Toast.LENGTH_SHORT).show();
                return null;
            }

        }
    }

    private void init_variables() {
         mydirc = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"");
         mydirc.mkdirs();
         textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
         file = new File(mydirc.toString()+"/temp.jpg");
         count = 0;
    }

    private void init_views() {

        iv = (ImageView)findViewById(R.id.iv_takePhoto);
        btn = (ImageButton) findViewById(R.id.btn_photo);
        tv_show = (TextView)findViewById(R.id.tv_number);
        goToNExt = (ImageButton)findViewById(R.id.ibtn_ok);
        btncrop = (ImageButton)findViewById(R.id.btn_crop);
    }
}
