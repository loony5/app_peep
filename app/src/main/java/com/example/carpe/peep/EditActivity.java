package com.example.carpe.peep;


import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class EditActivity extends AppCompatActivity {

    // 프로필 사진 변경할때
    private static final int PICK_IMAGE_ALBUM = 1;
    private Uri imageUri;
    private ImageView selectImage;
    private Button imageEdit;

    // 계정삭제, 로그아웃 할때
    Button buttonDeleteAccount;
    Button buttonLogout;

    // 표시할 계정 정보
    TextView myEmail;
    TextView myName;
    EditText myID;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    StorageReference storageReference;
    StorageTask uploadTask;

    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        imageEdit = (Button) findViewById(R.id.imageEdit);

        buttonDeleteAccount = (Button) findViewById(R.id.buttonDeleteAccount);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        myEmail = (TextView) findViewById(R.id.myEmail);
        myName = (TextView) findViewById(R.id.myName);
        myID = (EditText) findViewById(R.id.myID);

        selectImage = (ImageView) findViewById(R.id.myImage);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // 파이어베이스 인증, 데이터베이스에서 가져와서 보여주기.
        user = firebaseAuth.getCurrentUser();

        // 프로필 사진 담을 스토리지 생성.
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

//        String userid = user.getUid();
//        databaseReference.getDatabase().getReference().child(userid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                User user = dataSnapshot.getValue(User.class);
//
//                if (user.getUserID().equals("default")) {
//
//                    myID.setText("My ID");
//                } else if (user.getUserImage().equals("default")) {
//
//                    selectImage.setImageResource(R.drawable.image_default);
//                } else {
//
//                    myID.setText(user.getUserID());
//                    Glide.with(EditActivity.this).load(user.getUserImage()).into(selectImage);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        myEmail.setText(user.getEmail());
        myName.setText(user.getDisplayName());

        // 저장한 Shared 불러오기
        SharedPreferences sf = getSharedPreferences("setUser", MODE_PRIVATE);
        String saveImage = sf.getString("setImg", "");
        String saveID = sf.getString("setID", "");

        // 저장한 값이 비어있으면, 기본 이미지 보여주고, 아니면 저장한 이미지로 보여준다.
        if (saveImage.isEmpty() && saveID.isEmpty()) {

            selectImage.setImageResource(R.drawable.image_default);
            myID.setText("My ID");

        } else {
            Uri uri = Uri.parse(saveImage);
            Glide.with(this).load(uri).into(selectImage);

            myID.setText(saveID);
        }

    }

    public void onClick(View view) {

        // 프로필 사진바꾸기 선택하면, 갤러리 open
        if (view == imageEdit) {

//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // 내 파일로 이동하는 intent
//            intent.setType("image/*");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_ALBUM);

            // 회원탈퇴 선택하면, 다이얼로그 보여주기.
        } else if (view == buttonDeleteAccount) {

            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder.setMessage("정말 계정을 삭제 할까요?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {

                // 확인을 선택하면 파이어베이스 계정에서 삭제하고, 다시 로그인페이지로
                @Override
                public void onClick(DialogInterface dialog, int which) {

//                    userid = user.getUid();
//
//                    DatabaseReference dUser = FirebaseDatabase.getInstance().getReference("Users").child(userid);
//                    DatabaseReference dPost = FirebaseDatabase.getInstance().getReference("Posts").child(userid);
//
//                    dUser.removeValue();
//                    dPost.removeValue();

                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(getApplicationContext(), "계정이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            // 새로운 task 를 만들어서 root activity 가 된다.
                            // back 으로 돌아가면 종료.
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
            });
            // 취소를 선택하면, Toast
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();

            // 로그아웃 선택하면 파이어베이스에서 signout. 로그인페이지로 이동
        } else if (view == buttonLogout) {
            firebaseAuth.signOut();
            Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            // 새로운 task 를 만들어서 root activity 가 된다.
            // back 으로 돌아가면 종료.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

//    private void deleteUser(String userid) {
//
//        DatabaseReference dUser = FirebaseDatabase.getInstance().getReference("Users").child(userid);
//        DatabaseReference dPost = FirebaseDatabase.getInstance().getReference("Posts").child(userid);
//
//        dUser.removeValue();
//        dPost.removeValue();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 이미지 선택했을때, 경로 가져오기
        if (requestCode == PICK_IMAGE_ALBUM && resultCode == RESULT_OK) {

            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(selectImage);

            // 완료 버튼 클릭했을때,
            Button completion = (Button) findViewById(R.id.completion);
            completion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    profileUpdates();
                    uploadImage();

                    // imageUri 를 String 으로 Shared 에 저장
                    SharedPreferences pref = getSharedPreferences("setUser", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("setImg", imageUri.toString());
                    editor.putString("setID", myID.getText().toString());
                    editor.commit();

                    // Uri 값을 setResult();, finish(); 로 돌려준다.
                    Intent intent = new Intent();
                    intent.putExtra("imageUri", imageUri.toString());
                    intent.putExtra("userID", myID.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }

            });

        }
    }

    public String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public void uploadImage() {

        if (imageUri != null) {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        userid = user.getUid();

                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("userImage", mUri);
                        map.put("userID", myID.getText().toString());
                        databaseReference.updateChildren(map);

                    } else {
                        Toast.makeText(getApplicationContext(), "실패!", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "선택한 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    public void profileUpdates() {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d("EditActivity", "User profile updated.");

                        }
                    }
                });
    }



}
