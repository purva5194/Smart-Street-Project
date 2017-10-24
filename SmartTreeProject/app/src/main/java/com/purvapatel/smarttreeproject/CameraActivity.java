package com.purvapatel.smarttreeproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


    }

    // capture image using camera api
    public void image(View v) {

        //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, 0);
    }


    //capture video using camera api
    public void video(View v){

    }

    // when clicks on Cancel button
    public void cancel(View view){
        // destroy that activity
        finish();
        // call main activity using intent.
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
