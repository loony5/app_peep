package com.example.carpe.peep;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }

    // back 버튼 누른 후 2초 후 showGuide() 메소드 호출, 2초가 지나면 액티비티 종료
    public void onBackPressed(){
        if (System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000){
            activity.finish();
            toast.cancel();
        }
    }

    // 종료 안내 메세지 표시
    public void showGuide() {
        toast = Toast.makeText(activity,
                "'뒤로가기'버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
