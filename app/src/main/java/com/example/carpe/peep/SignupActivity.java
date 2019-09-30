package com.example.carpe.peep;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignupActivity extends AppCompatActivity {

    EditText newEmail;
    EditText newName;
    EditText newPassword;
    EditText newConfPassword;

    ImageView email_box;
    ImageView name_box;
    ImageView pw_box;
    ImageView conf_box;

    ImageButton buttonJoin;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    Animation animation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        firebaseAuth = FirebaseAuth.getInstance();

        newEmail = (EditText) findViewById(R.id.newEmail);
        newEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){

                    email_box.setAnimation(animation);
                } else {
                    email_box.clearAnimation();
                }

            }
        });

        newName = (EditText) findViewById(R.id.newName);
        newName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){
                    name_box.setAnimation(animation);
                }else {
                    name_box.clearAnimation();
                }
            }
        });
        newPassword = (EditText) findViewById(R.id.newPassword);
        newPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){
                    pw_box.setAnimation(animation);
                } else {
                    pw_box.clearAnimation();
                }
            }
        });
        newConfPassword = (EditText) findViewById(R.id.newConfPassword);
        newConfPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){
                    conf_box.setAnimation(animation);
                }else {
                    conf_box.clearAnimation();
                }
            }
        });

        email_box = (ImageView)findViewById(R.id.email_box);
        name_box = (ImageView)findViewById(R.id.name_box);
        pw_box = (ImageView)findViewById(R.id.password_box);
        conf_box = (ImageView)findViewById(R.id.confirm_box);

        buttonJoin = (ImageButton) findViewById(R.id.buttonJoin);

        // Signup 버튼 클릭하면,
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 해당 메소드 호출
                registerUser();
            }
        });
    }

    private void registerUser() {


        final String newEmailStr = newEmail.getText().toString().trim();
        final String newNameStr = newName.getText().toString().trim();
        String newPasswordStr = newPassword.getText().toString().trim();
        String newConfPasswordStr = newConfPassword.getText().toString().trim();


        // 값을 입력하지 않았을때,
        if (TextUtils.isEmpty(newEmailStr) || TextUtils.isEmpty(newNameStr) || TextUtils.isEmpty(newPasswordStr) || TextUtils.isEmpty(newConfPasswordStr)) {
            Toast.makeText(this, "입력이 바르지 않습니다.", Toast.LENGTH_SHORT).show();

            // 비밀번호가 확인비밀번호와 같지 않을때,
        } else if (newEmailStr != null && newNameStr != null && !newPasswordStr.equals(newConfPasswordStr)) {

            Toast.makeText(this, "비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();

            // 모두 바르게 입력했을때,
        } else if (newEmailStr != null && newNameStr != null && newPasswordStr.equals(newConfPasswordStr)){


            firebaseAuth.createUserWithEmailAndPassword(newEmailStr, newPasswordStr)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // 파이어베이스에 등록됐을때,
                            if (task.isSuccessful()) {

                                FirebaseUser firebaseUser = task.getResult().getUser();

                                // 해당 메소드에 이메일, 이름 값 넣기.
//                                writeNewUser();
                                String email = newEmail.getText().toString().trim();
                                String name = newName.getText().toString().trim();

                                String userid = firebaseUser.getUid();

                                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

//                                databaseReference.child(id).child(firebaseUser.getUid()).setValue(user);
                                databaseReference.child(userid);

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("userUID", userid);
                                hashMap.put("userEmail", email);
                                hashMap.put("userName", name);
                                hashMap.put("userID", "My ID");
                                hashMap.put("userImage", "default");

                                databaseReference.setValue(hashMap);
                                profileUpdates();

                                // 로그인 페이지로 이동.
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();

                                // 이 외에,
                            } else {

                                Toast.makeText(getApplicationContext(), "이미 가입되어있습니다.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

    }

    // 이메일, 이름을 데이터베이스에 넣기.
//    public void writeNewUser() {
//
//        String email = newEmail.getText().toString().trim();
//        String name = newName.getText().toString().trim();
//
//        String id = databaseReference.push().getKey();
//
//        // User 클래스 인스턴스.
//        User user = new User(email, name);
//
//        databaseReference.child(id).setValue(user);
//    }

    public void profileUpdates() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String name = newName.getText().toString().trim();

        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Log.d("SignupActivity","User profile updated.");
                }

            }
        });

    }

}
