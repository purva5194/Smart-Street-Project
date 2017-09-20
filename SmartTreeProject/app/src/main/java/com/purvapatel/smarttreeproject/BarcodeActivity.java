package com.purvapatel.smarttreeproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by purvapatel on 9/4/17.
 */
public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView zXingScannerView;
    public TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout to the activity
        setContentView(R.layout.activity_barcode);

        // extract barcode data value from the bundle.
        Bundle extras = getIntent().getExtras();
        String dataValue = extras.getString("data");

        data = (TextView) findViewById(R.id.codedata);
        data.setText(dataValue);
    }

    // when clicks on Scan button
    public void scan(View view){
        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

    }

    // when clicks on Cancel button
    public void cancel(View view){
        // destroy that activity
        finish();
        // call main activity using intent.
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop the camera in pause position of the activity.
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        // after barcodoe captured, display data on the BarcodeActivity.java.
        Intent intent = new Intent(getApplicationContext(), BarcodeActivity.class);
        // send barcode data to the BarcodeActity.java.
        intent.putExtra("data",result.getText());
        startActivity(intent);


    }
}
