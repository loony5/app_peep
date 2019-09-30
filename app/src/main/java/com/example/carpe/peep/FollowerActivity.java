package com.example.carpe.peep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class FollowerActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);


        mRecyclerView = findViewById(R.id.recyclerview_follower);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<FollowerData> followerDataArrayList = new ArrayList<>();
        followerDataArrayList.add(new FollowerData(R.drawable.image_default, "UserID - A"));
        followerDataArrayList.add(new FollowerData(R.drawable.image_default, "UserID - B"));
        followerDataArrayList.add(new FollowerData(R.drawable.image_default, "UserID - C"));
        followerDataArrayList.add(new FollowerData(R.drawable.image_default, "UserID - D"));
        followerDataArrayList.add(new FollowerData(R.drawable.image_default, "UserID - E"));
        followerDataArrayList.add(new FollowerData(R.drawable.image_default, "UserID - F"));

        FollowerAdapter followerAdapter = new FollowerAdapter(followerDataArrayList);

        mRecyclerView.setAdapter(followerAdapter);

    }
}
