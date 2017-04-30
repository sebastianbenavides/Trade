package com.example.sebas.trade;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottomNavView_Bar);
        BottomNavigationHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ic_camera:
                                Intent uploadIntent = new Intent(ProfileActivity.this, Upload.class);
                                startActivity(uploadIntent);
                                break;
                            case R.id.ic_timeline:
                                Intent timelineIntent = new Intent(ProfileActivity.this, Timeline.class);
                                startActivity(timelineIntent);
                                break;
                            case R.id.ic_user_profile:
                                Intent profileIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                                startActivity(profileIntent);
                                break;
                        }

                        return false;
                    }
                });
    }
}