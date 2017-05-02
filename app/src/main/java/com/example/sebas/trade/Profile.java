package com.example.sebas.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    Button mLogout;

    ImageView mProfilePicture;
    TextView mUsername, mUserEmail;

    LinearLayout mPostsLayout, mMessagesLayout;

    DatabaseReference mDatabaseUser;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mPostsLayout = (LinearLayout) findViewById(R.id.posts_Layout);
        mMessagesLayout = (LinearLayout) findViewById(R.id.messages_Layout);

        mProfilePicture = (ImageView) findViewById(R.id.profilePicture);

        mUsername = (TextView) findViewById(R.id.profile_username);
        mUserEmail = (TextView) findViewById(R.id.profile_email);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        //if user clicks logout button, send them to login page
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(Profile.this, Login.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };


        //the double use of a value event listener seems redundant, but in order for the picture to show...
        //two listeners were needed to trigger the event... not sure why
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabaseUser.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        String user_Pic = dataSnapshot.child("image").getValue().toString();
                        String user_Name = dataSnapshot.child("name").getValue().toString();
                        String user_Email = mUser.getEmail();

                        Picasso.with(Profile.this).load(user_Pic).fit().centerCrop().rotate(270).into(mProfilePicture);
                        mUsername.setText(user_Name);
                        mUserEmail.setText(user_Email);





                        for(DataSnapshot Snapshot : dataSnapshot.getChildren()) {

                            if(Snapshot.getKey().toString().equals("posts")){

           //                     TextView post

                            } else if(Snapshot.getKey().toString().equals("messages")) {

                                for(final DataSnapshot messageSnapShot : Snapshot.getChildren()) {

                                    String messageText = messageSnapShot.getValue().toString();

                                    TextView message = new TextView(getApplicationContext());
                                    message.setText("- " + messageText);
                                    message.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                                    message.setPadding(50, 5, 20, 5);


                                    mMessagesLayout.addView(message);

                                    message.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {

                                            mDatabaseUser.child(mUser.getUid()).child("messages")
                                                    .child(messageSnapShot.getKey().toString()).removeValue();


                                            //when the item is deleted, it posts repeats of the messages until the view has been
                                            Intent restartActivityIntent = new Intent(Profile.this, Profile.class);
                                            restartActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(restartActivityIntent);

                                            return false;

                                        }
                                    });

                                }


                            }


                        }
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


        mLogout = (Button) findViewById(R.id.logoutButton);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    //sign out of firebase and app
    private void logout() {
        mAuth.signOut();
    }
}