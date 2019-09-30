package com.example.carpe.peep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MyActivity extends AppCompatActivity {

    // My 페이지 키워드 리사이클러뷰
    private static String TAG = "recyclerview_my";

    private ArrayList<MyKeywordData> myArrayList;
    private MyKeywordAdapter myAdapter;
    private RecyclerView myRecyclerView;
    private int count = 0;

    private final static int GET_IMAGE = 3000;
    private ImageView myPageImg;
    private TextView myPageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        myPageImg = (ImageView) findViewById(R.id.myPageImage);
        myPageID = (TextView) findViewById(R.id.myPageID);

        // 저장한 Shared 불러와서 이미지뷰에 넣기.
        SharedPreferences sf = getSharedPreferences("profileSet", MODE_PRIVATE);
        String saveImage = sf.getString("profileSetImg","");
        String saveID = sf.getString("profileSetID", "");

        // 저장한 값이 비어있으면, 기본 이미지 보여주고, 아니면 저장한 이미지로 보여준다.
        if(saveImage.isEmpty() && saveID.isEmpty()) {

            myPageImg.setImageResource(R.drawable.image_default);
            myPageID.setText("My ID");

        } else {

            Uri uri = Uri.parse(saveImage);
            Glide.with(this).load(uri).into(myPageImg);

            myPageID.setText(saveID);

            Log.e("MyActivity", "프로필사진 uri 는 " + uri);
        }

        // my 페이지 키워드 리사이클러뷰
        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_mypage);
        myRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // 저장한 데이터를 불러오는 메소드 호출.
        loadData();

        myAdapter = new MyKeywordAdapter(this, myArrayList);
        myRecyclerView.setAdapter(myAdapter);

        ImageButton plusBtn = (ImageButton) findViewById(R.id.myPlusBtn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 레이아웃 파일 myedit_box.xml을 불러와서 화면에 다이얼로그를 보여준다.
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
                View view = LayoutInflater.from(MyActivity.this)
                        .inflate(R.layout.edit_box, null, false);
                builder.setView(view);

                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_keyword_submit);
                final EditText editTextMyKey = (EditText) view.findViewById(R.id.edittext_Keyword);
                ButtonSubmit.setText("추가");

                final AlertDialog dialog = builder.create();

                // 다이얼로그에 있는 추가 버튼을 클릭하면,
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 사용자가 입력한 내용을 가져와서
                        String keywordStr = editTextMyKey.getText().toString();

                        // ArrayList 에 추가하고
                        MyKeywordData mykData = new MyKeywordData(keywordStr);
                        myArrayList.add(mykData); //마지막에 삽입

                        // 어댑터에서 RecyclerView 에 반영.
                        myAdapter.notifyDataSetChanged();

                        // ArrayList 에 데이터를 저장하는 메소드 호출.
                        saveData();

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

    }

    private void saveData() {

        // SharedPreferences 이용 -> 저장할 수있는 editor 를 생성해서 Jason 형태로 Gson 을 이용해서 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("mypage keyword", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myArrayList);
        editor.putString("mykeyword", json);
        editor.apply();

    }

    private void loadData() {

        // 저장한 데이터 불러오기. ArrayList 가 null 이면 새로 생성.
        SharedPreferences sharedPreferences = getSharedPreferences("mypage keyword", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("mykeyword", null);
        Type type = new TypeToken<ArrayList<MyKeywordData>>() {}.getType();
        myArrayList = gson.fromJson(json, type);

        if (myArrayList == null){
            myArrayList = new ArrayList<>();
        }
    }

    // My 페이지에서 설정버튼을 선택했을때,
    public void editClick(View view) {

        Intent intent = new Intent(getApplicationContext(), EditActivity.class);

        // EditActivity 에서 결과를 받아오기 위해서 statActivityForResult 사용.
        startActivityForResult(intent, GET_IMAGE);
    }


    // EditActivity 에서 처리한 setResult 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE && resultCode == RESULT_OK) {

            String getImage = data.getStringExtra("imageUri");
            String getID = data.getStringExtra("userID");
            Uri uri = Uri.parse(getImage);
            Glide.with(this).load(uri).into(myPageImg);
            Log.e("MyActivity", "앨범에서 가져온 이미지 uri 는" + uri);

            myPageID.setText(getID);

            // imageUri 를 String 으로 Shared 에 저장
            SharedPreferences pref = getSharedPreferences("profileSet",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("profileSetImg",getImage);
            editor.putString("profileSetID",getID);
            editor.commit();

            Toast.makeText(this, "프로필 이미지를 수정했습니다.", Toast.LENGTH_LONG).show();

        }
    }

    // 홈 클릭,
    public void mainClick(View view) {

        Intent intent = new Intent(MyActivity.this, ProgressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences("mypage keyword", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myArrayList);
        editor.putString("mykeyword", json);
        editor.apply();

    }
}
