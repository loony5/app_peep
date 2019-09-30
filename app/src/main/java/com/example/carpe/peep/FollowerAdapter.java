package com.example.carpe.peep;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FollowerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class FollowerViewHolder extends RecyclerView.ViewHolder{

        ImageView userImage;
        TextView userID;

        FollowerViewHolder(View view){

            super(view);
            userImage = view.findViewById(R.id.userImage);
            userID = view.findViewById(R.id.userID);

        }

    }

    private ArrayList<FollowerData> followerDataArrayList;
    FollowerAdapter(ArrayList<FollowerData> followerDataArrayList){

        this.followerDataArrayList = followerDataArrayList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);

        return new FollowerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        FollowerViewHolder followerViewHolder = (FollowerViewHolder) holder;

        followerViewHolder.userImage.setImageResource(followerDataArrayList.get(position).userImageDrawable);
        followerViewHolder.userID.setText(followerDataArrayList.get(position).userIDString);

    }

    @Override
    public int getItemCount() {
        return followerDataArrayList.size();
    }
}
