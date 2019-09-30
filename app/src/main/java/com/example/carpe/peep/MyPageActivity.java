package com.example.carpe.peep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class MyPageActivity extends AppCompatActivity {


    // 상단 keyword 텍스트뷰
    private TextView topKeyword;
    public String keyword;

    // 리사이클러뷰
    private static String TAG = "recyclerview_mypage";

    private ArrayList<WritingData> wArrayList;
    private WritingAdapter wAdapter;
    private RecyclerView wRecyclerView;
    private int count = 0;

    private int item;

    final static int GET_DATA = 3;
    final static int EDIT_DATA = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        topKeyword = (TextView) findViewById(R.id.topKeyword);

        // 상단 키워드 텍스트뷰에 Keyword 를 가져와서 set.
        setTopKeyword();

        String strColor = "#9C79E6";
        topKeyword.setText(keyword);
        topKeyword.setTextColor(Color.parseColor(strColor));
        topKeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        wRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_Mywrinting);
        wRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
//        wArrayList = new ArrayList<>();


        wAdapter = new WritingAdapter(this, wArrayList);
        wRecyclerView.setAdapter(wAdapter);

        // 하단 플러스 버튼 클릭하면, WritingBoxActivity 로 이동.
        ImageButton writingBtn = (ImageButton) findViewById(R.id.writingPlusBtn);
        writingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyPageActivity.this, WritingBoxActivity.class);
                startActivityForResult(intent, GET_DATA);

            }

        });

        // 어댑터 리스너 가져와서 인텐트로 데이터를 WritingEditActivity 에 보낸다.
        wAdapter.setListener(new WritingAdapter.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(WritingData writingData, int position) {

                item = position;

                Intent intent1 = new Intent(MyPageActivity.this, WritingEditActivity.class);
                intent1.putExtra("editListText", writingData.getWriting());
                intent1.putExtra("editListImage", writingData.getUpImageUri());
                intent1.putExtra("editListKeyword", writingData.getWritingKeyword());
                Log.e("MypageActivity", "WritingEditActivity 로 이미지 넘기기" + writingData.getUpImageUri());
                startActivityForResult(intent1, EDIT_DATA);

            }
        });

    }

    public String setTopKeyword() {

        Intent intent = getIntent();
        keyword = intent.getExtras().getString("Keyword");
        Log.d("MyPageActivity","keyword : " + keyword);
        return keyword;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // onActivityResult () 안에를 잘 살펴보자. Intent data 로 선언이 되어있기때문에,
        // 데이터를 가져올 때, Intent intent = new intent, intent.getStringExtra("") 가 아니고
        // data.getStringExtra("") 로 가져온다. -> 이 부분에서 헤맴. 코드를 잘 보자!!!!

        if (requestCode == GET_DATA && resultCode == RESULT_OK) {

            // WritingBoxActivity 에서 데이터를 가져와서 ArrayList 에 넣고 어댑터에 반영.
            // Log 를 찍어서 데이터를 잘 가져왔는지 확인해보자.
            String content = data.getStringExtra("content");
            String image = data.getStringExtra("uploadImage");
            String keyword = data.getStringExtra("writingKeyword");
            Log.e("MyPageActivity","content : " + content);
            Log.e("MyPageActivity","uploadImage : " + image);

            WritingData writingData = new WritingData(content, image, keyword);
            wArrayList.add(0, writingData);

            saveData();

            wAdapter.notifyDataSetChanged();

        } else if (requestCode == EDIT_DATA && resultCode == RESULT_OK) {

            String content = data.getStringExtra("editOkText");
            String image = data.getStringExtra("editOkImage");
            String keyword = data.getStringExtra("editOkKeyword");
            Log.e("MyPageActivity", "editImage : " + image);

            WritingData writingData = new WritingData(content, image, keyword);
            wArrayList.set(item, writingData);

            saveData();

            wAdapter.notifyDataSetChanged();

        }
    }

    private void saveData() {

        // SharedPreferences 이용 -> 저장할 수있는 editor 를 생성해서 Jason 형태로 Gson 을 이용해서 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("mypage writing", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(wArrayList);
        editor.putString("mypage", json);
        editor.apply();

    }

    private void loadData() {

        // 저장한 데이터 불러오기. ArrayList 가 null 이면 새로 생성.
        SharedPreferences sharedPreferences = getSharedPreferences("mypage writing", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("mypage", null);
        Type type = new TypeToken<ArrayList<WritingData>>() {}.getType();
        wArrayList = gson.fromJson(json, type);


        if (wArrayList == null){
            wArrayList = new ArrayList<>();
        }
    }

    public void back_btn(View view){

        Intent intent = new Intent(getApplicationContext(), MyActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // SharedPreferences 이용 -> 저장할 수있는 editor 를 생성해서 Jason 형태로 Gson 을 이용해서 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("mypage writing", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(wArrayList);
        editor.putString("mypage", json);
        editor.apply();

    }
}
