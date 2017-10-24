package com.purvapatel.smarttreeproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ShareActivity extends AppCompatActivity {

    // for image
    private static final int REQUEST_CODE = 1;

    // for video
    private static final int PICK_FROM_GALLERY=1;

    private Bitmap bitmap;
    private ImageView imageView;
    private VideoView videoView;
    private ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        imageView = (ImageView) findViewById(R.id.share_image);
        videoView = (VideoView) findViewById(R.id.share_video);
        btn = (ImageButton) findViewById(R.id.btn_btn);

        // by default visibility for video view is null
        videoView.setVisibility(View.INVISIBLE);
        btn.setVisibility(View.INVISIBLE);

        // set media controller with video view
        MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // share image with long press event on image view
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                // share image using chooser
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent,"Share using"));
                return true;
            }
        });

        //share button for video
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // share video using chooser
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("video/*");
                startActivity(Intent.createChooser(intent,"Share using"));

            }
        });

    }

    // fetch all images form the gallery
    public void share_image(View v){

        //create intent for images using ACTION_GET_CONTENT
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
        videoView.setVisibility(View.INVISIBLE);
        btn.setVisibility(View.INVISIBLE);

    }

    // fetch all the videos from the gallery
    public void share_video(View v){

        //create intent for videos using ACTION_GET_CONTENT
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
        videoView.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;

        // for images
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                // set data to the image view
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);

                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        if (resultCode != RESULT_OK) return;

        // for videos
        if (requestCode == PICK_FROM_GALLERY) {

            // set uri for video view
            Uri mVideoURI = data.getData();
            videoView.setVideoURI(mVideoURI);
            videoView.start();
        }
    }
}
