package com.example.carpe.peep;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FollowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class FollowingViewHolder extends RecyclerView.ViewHolder{

        ImageView userImage;
        TextView userID;

        FollowingViewHolder(View view){

            super(view);
            userImage = view.findViewById(R.id.userImage);
            userID = view.findViewById(R.id.userID);

        }

    }

    private ArrayList<FollowingData> followingDataArrayList;
    FollowingAdapter(ArrayList<FollowingData> followingDataArrayList){
        this.followingDataArrayList = followingDataArrayList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following, parent, false);

        return new FollowingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        FollowingViewHolder followingViewHolder = (FollowingViewHolder) holder;

        followingViewHolder.userImage.setImageResource(followingDataArrayList.get(position).userImageDrawable);
        followingViewHolder.userID.setText(followingDataArrayList.get(position).userIDString);


    }

    @Override
    public int getItemCount() {

        return followingDataArrayList.size();
    }
}
