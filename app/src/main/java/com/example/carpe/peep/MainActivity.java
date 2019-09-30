package com.example.carpe.peep;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //키워드 리사이클러뷰
    private static String TAG = "recyclerview_keyword";

    public ArrayList<KeywordData> mArrayList;
    KeywordAdapter mAdapter;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    int count = 0;

    //페이지 리사이클러뷰
    ArrayList<WritingData> wArrayList;
    ReadingAdapter rAdapter;
    RecyclerView wRecyclerView;

    //뒤로가기 종료
    private BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // 핸들러를 이용해서, 일정 시간이 되면 Snackbar 를 보여주자.
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                String snackBarText = "관심 주제를 추가하고, 다른 사람의 생각을 훔쳐보세요.";
//                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), snackBarText, Snackbar.LENGTH_INDEFINITE);
//                String snackBarColor = "#EAEAEA"; // #9C79E6 앞에 95 을 붙여서 투명도를 조절.
//                View view = snackbar.getView();
//                view.setBackgroundColor(Color.parseColor(snackBarColor)); // background 컬러
//                String textColor = "#747474";
//                TextView text = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//                text.setTextColor(Color.parseColor(textColor)); // Text 컬러
//                snackbar.setAction("close", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        snackbar.dismiss();
//                    }
//                });
//                snackbar.show();
//
////                Toast.makeText(getApplicationContext(), "관심 주제를 추가하고, 다른 사람의 생각을 훔쳐보세요.", Toast.LENGTH_LONG).show();
////                Toast.makeText(getApplicationContext(), "주제에 맞는 자신의 생각을 기록해보세요.", Toast.LENGTH_LONG).show();
//
//            }
//        }, 8000);


        //키워드 리사이클러뷰
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_keyword_list);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
//        mArrayList = new ArrayList<>();

        loadKeywordData();

        mAdapter = new KeywordAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        ImageButton buttonInsert = (ImageButton) findViewById(R.id.button_keyword_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {

            // 오른쪽 플러스 버튼을 클릭하면,
            @Override
            public void onClick(View v) {

                // 레이아웃 파일 edit_box.xml 을 불러와서 화면에 다이얼로그를 보여준다.
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.edit_box, null, false);
                builder.setView(view);
                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_keyword_submit);
                final EditText editTextkeyword = (EditText) view.findViewById(R.id.edittext_Keyword);

                ButtonSubmit.setText("추가");

                final AlertDialog dialog = builder.create();

                // 다이얼로그에 있는 추가 버튼을 클릭하면,
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 사용자가 입력한 내용을 가져와서
                        String strKeyword = editTextkeyword.getText().toString();

                        // ArrayList에 추가하고
                        KeywordData kdata = new KeywordData(strKeyword);
                        mArrayList.add(kdata);

                        // 어댑터에서 RecyclerView에 반영한다.
                        mAdapter.notifyDataSetChanged();

                        saveKeywordData();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        //페이지 리사이클러뷰
        wRecyclerView = findViewById(R.id.recyclerview_page);
        wRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadContentData();

        rAdapter = new ReadingAdapter(this, wArrayList);
        wRecyclerView.setAdapter(rAdapter);

        //뒤로가기
        backPressCloseHandler = new BackPressCloseHandler(this);

    }

    private void loadContentData() {

        // 저장한 데이터 불러오기. ArrayList 가 null 이면 새로 생성.
        SharedPreferences sharedPreferences = getSharedPreferences("mypage writing", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("mypage", null);
        Type type = new TypeToken<ArrayList<WritingData>>() {}.getType();
        wArrayList = gson.fromJson(json, type);

    }

    private void saveKeywordData() {

        // SharedPreferences 이용 -> 저장할 수있는 editor 를 생성해서 Jason 형태로 Gson 을 이용해서 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("main data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        editor.putString("main keyword", json);
        editor.apply();

    }

    public void loadKeywordData() {

        // 저장한 데이터 불러오기. ArrayList 가 null 이면 새로 생성.
        SharedPreferences sharedPreferences = getSharedPreferences("main data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("main keyword", null);
        Type type = new TypeToken<ArrayList<KeywordData>>() {}.getType();
        mArrayList = gson.fromJson(json, type);

        if (mArrayList == null){
            mArrayList = new ArrayList<>();
        }

    }

    public void myClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MyActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {

        backPressCloseHandler.onBackPressed();
    }



    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences("main data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        editor.putString("main keyword", json);
        editor.apply();
    }
}


