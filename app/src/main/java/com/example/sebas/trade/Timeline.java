package com.example.sebas.trade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class Timeline extends AppCompatActivity {

    private RecyclerView mTimeline;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(Timeline.this, RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");

        mTimeline = (RecyclerView) findViewById(R.id.timeline);
        mTimeline.setHasFixedSize(true);
        mTimeline.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottomNavView_Bar);
        BottomNavigationHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.ic_camera:
                                Intent uploadIntent = new Intent(Timeline.this, Upload.class);
                                startActivity(uploadIntent);
                                break;
                            case R.id.ic_timeline:
                                Intent timelineIntent = new Intent(Timeline.this, Timeline.class);
                                startActivity(timelineIntent);
                                break;
                            case R.id.ic_user_profile:
                                Intent profileIntent = new Intent(Timeline.this, Profile.class);
                                startActivity(profileIntent);
                                break;
                        }

                        return false;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(

                Post.class,
                R.layout.timeline_row,
                PostViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());

            }
        };

        mTimeline.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title) {


            TextView postTitle = (TextView) mView.findViewById(R.id.post_title);
            postTitle.setText(title);
        }

        public void setDesc(String desc) {

            TextView postDesc = (TextView) mView.findViewById(R.id.post_desc);
            postDesc.setText(desc);
        }

        public void setImage(Context context, String image) {

            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(context).load(image).fit().centerCrop().rotate(90).into(post_image);
        }

    }

}
