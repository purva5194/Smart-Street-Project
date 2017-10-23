package com.purvapatel.smarttreeproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.Result;
import com.purvapatel.smarttreeproject.Modules.AppConfig;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class AddUserProfileActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    Button reg, scan;
    EditText name, email, mobile;

    //URL for api call
    String BASE_URL = "https://cmpe235.herokuapp.com/";

    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_profile);

        //create reference of the components form .xml file
        reg = (Button) findViewById(R.id.btn_register);
        scan = (Button) findViewById(R.id.btn_scan_reg);
        name = (EditText) findViewById(R.id.profile_name);
        email = (EditText) findViewById(R.id.profile_email);
        mobile = (EditText) findViewById(R.id.profile_mobile);

        if(getIntent()!= null) {

            // get user infomations from the bundle
            Bundle extras = getIntent().getExtras();
            String data_name = extras.getString("name");
            String data_mobile = extras.getString("mobile");
            String data_email = extras.getString("email");

            // set user info into respective edittext
            name.setText(data_name);
            mobile.setText(data_mobile);
            email.setText(data_email);
        }
    }

    // when user clicks on register button
    public void registration(View v) {

        // set up the connection with BASE_URL
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .setClient(new OkClient(new OkHttpClient()));

        // get the reference of the adapter
        RestAdapter adapter = builder.build();

        //get reference of adduserprofile interface
        AppConfig.adduserprofile api = adapter.create(AppConfig.adduserprofile.class);

        // create hashmap for the user info like name, mobile number, email id
        // to store into database
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", name.getText().toString());
        map.put("mobile", mobile.getText().toString());
        map.put("email", email.getText().toString());

        // call method to insert user registration info
        // pass map and callback object as a parameter
        api.add_user_profile(
                map,
                new Callback<Response>() {

                    // if api called successfully
                    @Override
                    public void success(Response result, Response response) {

                        try {

                            // to get response in form of json
                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String resp;
                            resp = reader.readLine();
                            Log.d("success", "" + resp);

                            JSONObject jObj = new JSONObject(resp);
                            int success = jObj.getInt("success");


                            if(success == 1){
                                // if data stored successfully
                                Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();

                                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                //startActivity(intent);

                            } else{
                                // if failure happened during insertion of data
                                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                            }


                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    // when clicks on Scan button
    public void scan_reg(View view){

        // barcode reader view
        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        //start camera
        zXingScannerView.startCamera();

    }

    @Override
    public void handleResult(Result result) {

        //to get the values of name, email and mobile number separately
        StringTokenizer token = new StringTokenizer(result.getText(),";");

        String[] res = new String[3];
        int i=0;

        while(token.hasMoreElements())
        {
            String s = token.nextToken();

            int index = s.lastIndexOf(":");
            res[i] = s.substring(index+1);
            i++;
            //Toast.makeText(getApplicationContext(),res[i-1], Toast.LENGTH_LONG).show();
        }

        // data from the barcode



        // after barcodoe captured, redirect to the AddUserProfileActivity.java.
        Intent intent = new Intent(getApplicationContext(), AddUserProfileActivity.class);

        // send user data to the AddUserProfileActivity activity
        intent.putExtra("mobile",res[0]);
        intent.putExtra("email",res[1]);
        intent.putExtra("name",res[2]);

        //start activity
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop the camera in pause position of the activity.
        zXingScannerView.stopCamera();
    }
}
