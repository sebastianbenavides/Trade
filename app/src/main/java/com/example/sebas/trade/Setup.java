package com.example.sebas.trade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class Setup extends AppCompatActivity {


    private ImageView mSetupImage;
    private EditText mSetupName;
    private Button mSetupSubmit;

    private String mCurrentPhotoPath;

    private Uri imageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        mProgress = new ProgressDialog(this);

        mSetupImage = (ImageView) findViewById(R.id.userImageView);
        mSetupName = (EditText) findViewById(R.id.setupNameField);
        mSetupSubmit = (Button) findViewById(R.id.setupSubmitButton);

        mSetupSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSetupAccount();
            }
        });


        mSetupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Intent galleryIntent = new Intent();
                galleryIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;

                try {
                    photoFile = createPhotoFile();
                }
                catch(IOException IOE) {
                    IOE.printStackTrace();
                }

                if (photoFile != null) {
                    imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", photoFile);
                    galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }

            }
        });

    }

    private void startSetupAccount() {

        final String name = mSetupName.getText().toString().trim();

        final String user_id = mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && imageUri != null) {

            mProgress.setMessage("Finishing Setup...");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUsers.child(user_id).child("name").setValue(name);
                    mDatabaseUsers.child(user_id).child("image").setValue(downloadUri);

                    mProgress.dismiss();

                    Intent timelineIntent = new Intent(Setup.this, Timeline.class);
                    timelineIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(timelineIntent);
                }
            });


        }
    }

    //set photo as user picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Picasso.with(Setup.this).load(imageUri).fit().centerCrop().rotate(270).into(mSetupImage);
        }
    }


    //make file for fileprovider
    private File createPhotoFile() throws IOException {
        String imageFileName = "tempPhoto_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }
}
