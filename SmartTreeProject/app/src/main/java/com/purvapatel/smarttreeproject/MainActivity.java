package com.purvapatel.smarttreeproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.purvapatel.smarttreeproject.Modules.AppConfig;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
/**
 * Created by purvapatel on 9/4/17.
 */

public class MainActivity extends AppCompatActivity {

    ListView list;

    // listview item name
    String[] itemname ={
            "About",
            "Interact",
            "Photo/Video",
            "Share",
            "Nearby",
            "Comments",
            "Add User Profile"
    };

    // listview icon references
    Integer[] imgid={
            R.mipmap.about,
            R.mipmap.interact,
            R.mipmap.image,
            R.mipmap.share,
            R.mipmap.nearby,
            R.mipmap.comments,
            R.mipmap.user_profile
    };

    final Context c = this;

    // url for api call
    String BASE_URL = "https://cmpe235.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set default view to the activity
        setContentView(R.layout.activity_main);

        // List adapter for list view
        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid);
        //get the reference of the list view from activity_main
        list=(ListView)findViewById(R.id.list);
        //set adapterview with listview
        list.setAdapter(adapter);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // set icon on action bar
        getSupportActionBar().setIcon(R.drawable.sjsu);
        // set text on action bar
        getSupportActionBar().setTitle("    Smart Tree App");

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // TODO Auto-generated method stub
                // get the id of the selected list item.
                String Selecteditem= itemname[+position];
                //Toast.makeText(getApplicationContext(), Selecteditem, Toast.LENGTH_SHORT).show();

                Intent intent;

                switch (Selecteditem){
                    // send control to the barcode activity.
                    case "About" :
                        intent = new Intent(getApplicationContext(), BarcodeActivity.class);
                        intent.putExtra("data"," ");
                        startActivity(intent);
                        break;
                    //send control to the barcode activity.
                    case "Interact" :
                        intent = new Intent(getApplicationContext(), BarcodeActivity.class);
                        intent.putExtra("data"," ");
                        startActivity(intent);
                        break;
                    //send control to the map activity.
                    case "Nearby" :
                        intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                        break;

                    //share data with the social applications like mail, whatsapp, facebook.
                    case "Share" :
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("audio/mp3");
                        startActivity(Intent.createChooser(intent,"Share using"));
                        break;

                    //access the camara of the phone to capture videos or photos.
                    case "Photo/Video" :
                        intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,0);
                        break;

                    //Comment dialog box with the rating stars.
                    case "Comments" :

                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);

                        //provide custom dialog view.
                        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);

                        // set view to the current context.
                        alertDialogBuilderUserInput.setView(mView);

                        final EditText name = (EditText) mView.findViewById(R.id.comment_name);
                        final EditText comment = (EditText) mView.findViewById(R.id.comment_comment);
                        final Button retrive_btn = (Button) mView.findViewById(R.id.btn_feedback);

                        //when user clicks on retrive feedback button
                        retrive_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // fetch comment information from the database
                                // set that info into edit text
                                RestAdapter.Builder builder = new RestAdapter.Builder()
                                        .setEndpoint(BASE_URL) //Setting the Root URL
                                        .setClient(new OkClient(new OkHttpClient()));

                                RestAdapter adapter = builder.build();

                                //get reference if the interface
                                AppConfig.getcomment api = adapter.create(AppConfig.getcomment.class);

                                // call api method
                                api.get_comment(name.getText().toString(), new Callback<Response>() {

                                    @Override
                                    public void success(Response result, Response response) {
                                        // The network call was a success and we got a response
                                        // TODO: use the repository list and display it

                                        try {

                                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                            String resp;
                                            resp = reader.readLine();
                                            Log.d("success", "" + resp);

                                            //retrive data from json response
                                            JSONArray jObj = new JSONArray(resp);
                                            JSONObject obj = (JSONObject) jObj.get(0);
                                            String comm = obj.getString("comment");

                                            if(comm != null){
                                                comment.setText(comm);

                                            } else{
                                                //if data not available
                                                Toast.makeText(getApplicationContext(), "Feedback data not available", Toast.LENGTH_SHORT).show();
                                            }


                                        } catch (IOException e) {
                                            Log.d("Exception", e.toString());
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "Feedback data not available", Toast.LENGTH_SHORT).show();
                                            Log.d("JsonException", e.toString());
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        // the network call was a failure or the server send an error
                                        // TODO: handle error
                                    }
                                });
                            }
                        });

                        // store feedback information into database
                        alertDialogBuilderUserInput
                                .setCancelable(false)
                                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        // ToDo get user input here

                                        RestAdapter.Builder builder = new RestAdapter.Builder()
                                                .setEndpoint(BASE_URL) //Setting the Root URL
                                                .setClient(new OkClient(new OkHttpClient()));

                                        RestAdapter adapter = builder.build();

                                        //get reference of the interface
                                        AppConfig.addcomment api = adapter.create(AppConfig.addcomment.class);

                                        //map for data
                                        HashMap<String, Object> map = new HashMap<String, Object>();
                                        map.put("name", name.getText().toString());
                                        map.put("comment", comment.getText().toString());
                                        Log.d("check map", map.toString());

                                        // call method to store data
                                        // pass map and Callback as a parameter
                                        api.add_comment(
                                                map,
                                                new Callback<Response>() {
                                                    @Override
                                                    public void success(Response result, Response response) {

                                                        try {

                                                            //retrive json response
                                                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                                            String resp;
                                                            resp = reader.readLine();

                                                            // get the value of success fron json response
                                                            Log.d("success", "" + resp);

                                                            JSONObject jObj = new JSONObject(resp);
                                                            int success = jObj.getInt("success");

                                                            if(success == 1){

                                                                Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();;

                                                            } else{
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
                                                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                        );

                                    }
                                })

                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.cancel();
                                            }
                                        });

                        //create dialog box.
                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();

                        //display dialog on current context.
                        alertDialogAndroid.show();
                        break;

                    case "Add User Profile" :

                        // user registration activity
                        intent = new Intent(getApplicationContext(), AddUserProfileActivity.class);
                        intent.putExtra("mobile","");
                        intent.putExtra("email","");
                        intent.putExtra("name","");
                        startActivity(intent);
                        break;
                }


            }
        });


    }
}
