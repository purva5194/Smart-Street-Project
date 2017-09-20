package com.purvapatel.smarttreeproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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
            "Comments"
    };

    // listview icon references
    Integer[] imgid={
            R.mipmap.about,
            R.mipmap.interact,
            R.mipmap.image,
            R.mipmap.share,
            R.mipmap.nearby,
            R.mipmap.comments
    };

    final Context c = this;

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

                        // set title and buttons to the dialog box.
                        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                        alertDialogBuilderUserInput
                                .setCancelable(false)
                                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        // ToDo get user input here
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
                }


            }
        });


    }
}
