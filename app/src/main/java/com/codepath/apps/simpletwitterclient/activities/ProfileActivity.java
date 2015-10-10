package com.codepath.apps.simpletwitterclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.models.User;
import com.squareup.picasso.Picasso;

import fragments.UserTimelineFragment;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User currentUser = getIntent().getParcelableExtra("user");
        String screenName = currentUser.getScreenName();
        
        populateProfileHeader(currentUser);

        getSupportActionBar().setTitle(screenName);

        if (savedInstanceState == null) {
            UserTimelineFragment userTimeline = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.flContainer, userTimeline);

            ft.commit();
        }
    }

    private void populateProfileHeader(User currentUser) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        tvName.setText(currentUser.getScreenName());
        tvTagline.setText(currentUser.getTagline());
        tvFollowing.setText(currentUser.getFollowingCount() + " Following");
        tvFollowers.setText(currentUser.getFollowersCount() + " Followers");
        Picasso.with(this)
                .load(currentUser.getProfileImageUrl())
                .into(ivProfileImage);
    }
}
