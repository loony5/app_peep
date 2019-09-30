package com.example.carpe.peep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class FollowingActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        mRecyclerView = findViewById(R.id.recyclerview_following);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<FollowingData> followingDataArrayList = new ArrayList<>();
        followingDataArrayList.add(new FollowingData(R.drawable.image_default, "userID - A"));
        followingDataArrayList.add(new FollowingData(R.drawable.image_default, "userID - B"));
        followingDataArrayList.add(new FollowingData(R.drawable.image_default, "userID - C"));
        followingDataArrayList.add(new FollowingData(R.drawable.image_default, "userID - D"));
        followingDataArrayList.add(new FollowingData(R.drawable.image_default, "userID - E"));
        followingDataArrayList.add(new FollowingData(R.drawable.image_default, "userID - F"));

        FollowingAdapter followingAdapter = new FollowingAdapter(followingDataArrayList);
        mRecyclerView.setAdapter(followingAdapter);

    }
}
