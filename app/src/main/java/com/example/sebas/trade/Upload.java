package com.example.sebas.trade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Upload extends AppCompatActivity {

    private EditText itemName;
    private EditText itemDescription;

    private ImageButton image;

    private Button upload;

    private static final int CAMERA_REQUEST_CODE = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    private Uri imageUri;


    /*
       use surfaceview instead of picasso??

       https://developer.android.com/training/camera/cameradirect.html

       https://developer.android.com/guide/topics/media/camera.html

       https://developer.android.com/reference/android/view/SurfaceView.html

    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottomNavView_Bar_Upload);
        BottomNavigationHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            default:
                                break;
                        }



                        return false;
                    }
                });

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");

        itemName = (EditText) findViewById(R.id.itemName);
        itemDescription = (EditText) findViewById(R.id.itemDescription);

        image = (ImageButton) findViewById(R.id.imageButton);

//        upload = (Button) findViewById(R.id.uploadButton);

        mProgress = new ProgressDialog(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                startPosting();
//            }
//        });


    }

    private void startPosting() {

        mProgress.setMessage("Uploading Swap");


        final String itemTitle = itemName.getText().toString().trim();
        final String itemDesc = itemDescription.getText().toString().trim();

        if(!TextUtils.isEmpty(itemTitle) && !TextUtils.isEmpty(itemDesc) && imageUri != null) {
            mProgress.show();
            StorageReference filePath = mStorage.child("Photos").child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mDatabase.push();

                    newPost.child("title").setValue(itemTitle);
                    newPost.child("desc").setValue(itemDesc);
                    newPost.child("image").setValue(downloadUri.toString());

                    mProgress.dismiss();

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {



            imageUri = data.getData();

            Picasso.with(Upload.this).load(imageUri).fit().centerCrop().into(image);
/*
            StorageReference filePath = mStorage.child("Photos").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    Picasso.with(Upload.this).load(downloadUri).fit().centerCrop().rotate(90).into(image);

                    Toast.makeText(Upload.this, "Uploading Finished", Toast.LENGTH_LONG).show();
                }
            }); */
        }
    }

}