package com.example.sebas.trade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class Upload extends AppCompatActivity {

    private EditText itemName;
    private EditText itemDescription;

    private RelativeLayout uploadLayout;

    private ImageView image;

    private Button upload;

    private static final int CAMERA_REQUEST_CODE = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mUser;

    private ProgressDialog mProgress;

    private Uri imageUri;

    private String mCurrentPhotoPath;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;



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
                            case R.id.ic_upload:
                                startPosting();
                            case R.id.ic_timeline:
                                Intent timelineIntent = new Intent(Upload.this, Timeline.class);
                                startActivity(timelineIntent);
                                break;
                            case R.id.ic_user_profile:
                                Intent profileIntent = new Intent(Upload.this, Profile.class);
                                startActivity(profileIntent);
                                break;
                            default:
                                break;
                        }


                        return false;
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mUser = FirebaseDatabase.getInstance().getReference().child("Users");

        itemName = (EditText) findViewById(R.id.itemName);
        itemDescription = (EditText) findViewById(R.id.itemDescription);

        image = (ImageView) findViewById(R.id.imageButton);

        mProgress = new ProgressDialog(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;

                try {
                    photoFile = createPhotoFile();
                }
                catch(IOException IOE) {
                    IOE.printStackTrace();
                }

                if (photoFile != null) {
                    imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }

            }
        });


    }


    //if all fields have been entered, post to database
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

                    final Uri downloadUri = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabase.push();
     //               final DatabaseReference keyPost = mDatabaseUser;
                    final String postKey = newPost.getKey();

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("title").setValue(itemTitle);
                            newPost.child("desc").setValue(itemDesc);
                            newPost.child("image").setValue(downloadUri.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        itemName.setText("");
                                        itemDescription.setText("");
                                        image.setImageResource(R.drawable.ic_add_a_photo_white_24dp);


                                    } else {
                                        Toast.makeText(Upload.this, "Error Posting", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mDatabaseUser.child("posts").push().setValue(postKey);
                    mProgress.dismiss();




                }
            });
        }
    }

    //after photo has been taken, post to firebase storage and download to show to user
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

      //      Picasso.with(Upload.this).load(imageUri).fit().centerCrop().into(image);

            StorageReference filePath = mStorage.child("Photos").child(imageUri.getLastPathSegment());

      //      StorageReference filePath = mStorage.child("Photos");

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    Picasso.with(Upload.this).load(downloadUri).fit().centerCrop().rotate(90).into(image);

                    Toast.makeText(Upload.this, "Uploading Finished", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //helper for fileprovider for taking photo
    private File createPhotoFile() throws IOException {
        String imageFileName = "tempPhoto_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }
}

