package com.example.sebas.trade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class IndividualPost extends AppCompatActivity {


    DatabaseReference mDatabase;
    DatabaseReference mDatabaseUser;

    private ImageView mPostSingleImage, mPosterImage;
    private TextView mPostSingleTitle, mPostSingleDesc, mPostSingleUser;
    private EditText mMessage;
    private Button mMessageButton;

    private String mPost_key = null;

    private String post_Uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_post);

        mPost_key = getIntent().getExtras().getString("Post_id");


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");

        mPostSingleTitle = (TextView) findViewById(R.id.individualTitleField);
        mPostSingleUser = (TextView) findViewById(R.id.posterNameField);
        mPostSingleDesc = (TextView) findViewById(R.id.individualDescField);

        mPostSingleImage = (ImageView) findViewById(R.id.postImage);
        mPosterImage = (ImageView) findViewById(R.id.posterImage);

        mMessageButton = (Button) findViewById(R.id.message_Button);

        mMessage = (EditText) findViewById(R.id.messageField);


        //the use of three nested event listners was needed in order for all images in activity to show
        //maybe the listener higher up triggers the event for the one below it?
        //the picture for each ImageView only shows once it has been nested in another event listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String post_title = dataSnapshot.child("title").getValue().toString();
                        String post_desc = dataSnapshot.child("desc").getValue().toString();
                        String post_username = dataSnapshot.child("username").getValue().toString();
                        String post_image = dataSnapshot.child("image").getValue().toString();

                        post_Uid = dataSnapshot.child("uid").getValue().toString();

                        Picasso.with(IndividualPost.this).load(post_image).fit().centerCrop().rotate(90).into(mPostSingleImage);

                        if(post_Uid != null) {
                            mDatabaseUser.child(post_Uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String poster_image = dataSnapshot.child("image").getValue().toString();
                                    Picasso.with(IndividualPost.this).load(poster_image).fit().centerCrop().rotate(270).into(mPosterImage);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }

                        mPostSingleTitle.setText(post_title);
                        mPostSingleDesc.setText(post_desc);
                        mPostSingleUser.setText(post_username);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //send message directly to the person who made the post
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message_text = mMessage.getText().toString();

                if(!TextUtils.isEmpty(message_text)) {
                    mDatabaseUser.child(post_Uid).child("messages").push().setValue(message_text);
                    mMessage.setText("");
                } else {
                    Toast.makeText(IndividualPost.this, "No Message to Send", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
