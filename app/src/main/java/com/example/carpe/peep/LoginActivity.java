package com.example.carpe.peep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail;
    EditText loginPassword;
    ImageButton buttonSignup;
    ImageButton buttonLogin;
    ImageButton about;
    ImageView logo;

    ImageView idImage;
    ImageView pwImage;

    FirebaseAuth firebaseAuth;

    Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        idImage = findViewById(R.id.idImage);
        pwImage = findViewById(R.id.pwImage);

        firebaseAuth = FirebaseAuth.getInstance();

        // 로그인 화면 애니메이션
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {

                    idImage.setAnimation(shake);
                } else {
                    idImage.clearAnimation();
                }
            }
        });

        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {

                    pwImage.setAnimation(shake);
                } else {
                    pwImage.clearAnimation();
                }
            }
        });

        buttonSignup = (ImageButton) findViewById(R.id.buttonSignup);
        buttonLogin = (ImageButton) findViewById(R.id.buttonLogin);
        about = (ImageButton) findViewById(R.id.aboutPeep);

        logo = (ImageView) findViewById(R.id.logo);

    }

    public void onClick(View view){

        // 가입 선택시,
        if(view == buttonSignup){

            Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
            startActivity(intent);
            finish();

            // 로그인 선택시, userLogin 메소드로
        } else if(view == buttonLogin){

            userLogin();

            // aboutPeep 선택시
        } else if(view == about){

            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        }
    }


    private void userLogin() {

        String loginEmailStr = loginEmail.getText().toString().trim();
        String loginPasswordStr = loginPassword.getText().toString().trim();

        // 둘 중에 빈칸이 있을때,
        if (TextUtils.isEmpty(loginEmailStr) || TextUtils.isEmpty(loginPasswordStr)) {

            Toast.makeText(this, "입력이 바르지 않습니다.", Toast.LENGTH_SHORT).show();

            // 바르게 입력했을때,
        } else {
            firebaseAuth.signInWithEmailAndPassword(loginEmailStr, loginPasswordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    // firebase 계정이 있으면 메인으로 이동.
                    if (task.isSuccessful()) {

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        Toast.makeText(getApplicationContext(), "PEEP을 시작합니다.", Toast.LENGTH_LONG).show();

//                        // 핸들러를 이용해서, 일정 시간이 되면 Snackbar 를 보여주자.
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                String snackBarText = "관심 주제를 추가하고, 다른 사람의 생각을 훔쳐보세요.";
//                                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), snackBarText, Snackbar.LENGTH_INDEFINITE);
//                                String snackBarColor = "#959C79E6"; // #9C79E6 앞에 95 을 붙여서 투명도를 조절.
//                                View view = snackbar.getView();
//                                view.setBackgroundColor(Color.parseColor(snackBarColor)); // background 컬러
//                                TextView text = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//                                text.setTextColor(Color.WHITE); // Text 컬러
//                                snackbar.setActionTextColor(Color.WHITE).setAction("close", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        snackbar.dismiss();
//                                    }
//                                });
//                                snackbar.show();
//
//                Toast.makeText(getApplicationContext(), "관심 주제를 추가하면, 다른 사람의 기록을 볼 수있습니다.", Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "주제에 맞는 자신의 생각을 기록해보세요.", Toast.LENGTH_LONG).show();
//
//                            }
//                        }, 10000);

                        // 계정이 없거나, 이메일, 비밀번호가 맞지않으면,
                    } else {

                        Toast.makeText(getApplicationContext(), "Email과 Password를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}
