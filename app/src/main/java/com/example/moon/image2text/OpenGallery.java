package com.example.moon.image2text;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class OpenGallery extends AppCompatActivity {

    ImageView iv_show;
    TextRecognizer textRecognizer;
    ImageButton imageButton,ibtn_crop;
    EditText et_number;
    ImageButton nextPaze;
    File mydirc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gallery);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        init_view();
        init_variables();
        init_functions();
        init_listeners();
        openGallery();

    }

    private void init_view() {
        iv_show = (ImageView)findViewById(R.id.image_v);
        imageButton = (ImageButton)findViewById(R.id.ibtn_reloadImage);
        et_number = (EditText)findViewById(R.id.et_number);
        nextPaze = (ImageButton) findViewById(R.id.ibtn_nextPaze);
        ibtn_crop = (ImageButton)findViewById(R.id.btn_crop);
    }

    private void init_variables() {
        mydirc = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"");
        mydirc.mkdirs();
        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
    }

    private void init_functions() {
        nextPaze.setVisibility(View.INVISIBLE);
    }

    private void init_listeners() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        ibtn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        nextPaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(OpenGallery.this,new String[]{Manifest.permission.CALL_PHONE},3);
                }else {
                    Intent intent = new Intent(OpenGallery.this, SelectSim.class);
                    intent.putExtra("number", et_number.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

    private void openGallery() {

            Crop.pickImage(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==Crop.REQUEST_PICK){
                Uri src = data.getData();
                Uri des = Uri.fromFile(new File(getCacheDir(),"Cropped"));
                Crop.of(src,des).withAspect(1000,200).start(this);
                iv_show.setImageURI(Crop.getOutput(data));
            }else if(requestCode == Crop.REQUEST_CROP){
                iv_show.setImageURI(Crop.getOutput(data));
                String number  =  findTextFromImage();
                et_number.setText(number);
                if(TextUtils.isEmpty(number)){
                    Toast.makeText(getApplicationContext(),"No Number found",Toast.LENGTH_SHORT).show();
                    nextPaze.setVisibility(View.INVISIBLE);


                }else {
                    Toast.makeText(getApplicationContext(), number, Toast.LENGTH_SHORT).show();
                    nextPaze.setVisibility(View.VISIBLE);
                }

            }else if(requestCode==Crop.RESULT_ERROR){
                Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cropImage() {
        Uri des = Uri.fromFile(new File(getCacheDir(),"Croppedd"));
        Uri src = Uri.fromFile(new File(mydirc+"/temp.jpg"));
        Crop.of(src,des).withAspect(800,150).start(this);
        iv_show.setImageURI(des);
    }

    private String findTextFromImage() {
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
           Drawable drawable = iv_show.getDrawable();
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
}
