package com.example.carpe.peep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<CommentData> mList;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private AlertDialog dialog;


    public class ViewHolder extends RecyclerView.ViewHolder{

        protected ImageView image_profile;
        protected TextView userID, comment;
        protected ImageView option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.image_profile = itemView.findViewById(R.id.image_profile);
            this.userID = itemView.findViewById(R.id.userID);
            this.comment = itemView.findViewById(R.id.comment);
            this.option = itemView.findViewById(R.id.option);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    userID.setText(user.getUserID());

                    if (user.getUserImage().equals("default")) {

                        image_profile.setImageResource(R.drawable.image_default);
                    } else {

                        Glide.with(mContext).load(user.getUserImage()).into(image_profile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public CommentAdapter(Context context, ArrayList<CommentData> list) {

        mList = list;
        mContext = context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, viewGroup, false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        viewHolder.comment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        viewHolder.comment.setText(mList.get(position).getComment());

        viewHolder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, mList.size());

                            }
                        }). setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                dialog = builder.create();
                dialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {

        return mList.size();
    }

}
