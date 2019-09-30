package com.example.carpe.peep;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private ArrayList<CommentData> mArrayList;
    private CommentAdapter mAdapter;
    private RecyclerView mRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        addcomment = findViewById(R.id.addcomment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);

        // 현재 로그인한 사용자 가져오기
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        // 로그인한 사용자 프로필 이미지 보여주기
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user.getUserImage().equals("default")) {

                    image_profile.setImageResource(R.drawable.image_default);
                } else {

                    Glide.with(CommentActivity.this).load(user.getUserImage()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mRecyclerview = (RecyclerView) findViewById(R.id.commentsRecyclerview);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        loadComment();
//        mArrayList = new ArrayList<>();

        mAdapter = new CommentAdapter(this, mArrayList);
        mRecyclerview.setAdapter(mAdapter);

        // 댓글 작성 후 게시 버튼을 누르면 리사이클러뷰에 반영
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommentData commentData = new CommentData(addcomment.getText().toString());
                mArrayList.add(commentData);
                mAdapter.notifyDataSetChanged();

                saveComment();

                addcomment.setText("");

            }
        });

    }

    // 댓글 저장
    private void saveComment() {

        SharedPreferences sharedPreferences = getSharedPreferences("Comments", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        editor.putString("comment", json);
        editor.apply();

    }

    // 댓글 불러오기
    private void loadComment() {

        SharedPreferences sharedPreferences = getSharedPreferences("Comments", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("comment", null);
        Type type = new TypeToken<ArrayList<CommentData>>() {}.getType();
        mArrayList = gson.fromJson(json, type);

        if(mArrayList == null){
            mArrayList = new ArrayList<>();
        }

    }

}
