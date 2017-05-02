package com.example.sebas.trade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import static com.example.sebas.trade.R.id.post_image;

public class IndividualPost extends AppCompatActivity {


    DatabaseReference mDatabase;
    DatabaseReference mDatabaseUser;

    private ImageView mPostSingleImage;
    private TextView mPostSingleTitle, mPostSingleDesc, mPostSingleUser;
    private EditText mSendMessage;

    private String mPost_key = null;

    private String post_Uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_post);

        mPost_key = getIntent().getExtras().getString("Post_id");

        Toast.makeText(IndividualPost.this, mPost_key, Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");

        mPostSingleUser = (TextView) findViewById(R.id.posterNameField);

        mPostSingleDesc = (TextView) findViewById(R.id.individualDescField);
        mPostSingleImage = (ImageView) findViewById(R.id.postImage);

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title = dataSnapshot.child("title").getValue().toString();
                String post_desc = dataSnapshot.child("desc").getValue().toString();
                String post_username = dataSnapshot.child("username").getValue().toString();

                post_Uid = dataSnapshot.child("uid").getValue().toString();

//                mPostSingleTitle.setText(post_title);
//                mPostSingleDesc.setText(post_desc);
                Picasso.with(IndividualPost.this).load(post_image).fit().centerCrop().rotate(90).into(mPostSingleImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(post_Uid != null) {
            mDatabaseUser.child(post_Uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String post_image = dataSnapshot.child("image").getValue().toString();
                    Picasso.with(IndividualPost.this).load(post_image).fit().centerCrop().rotate(90).into(mPostSingleImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
}
