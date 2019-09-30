package com.example.carpe.peep;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class WritingBoxActivity extends AppCompatActivity {

    // 앨범에서 이미지 선택하고 불러오는 멤버변수
    final static int ALBUM = 2;
    private Uri uploadImageUri;
    private ImageView pickImage;

    private EditText writingText;
    private TextView writingKeyword;

    private ImageView profileImage;
    private TextView myID;
    String profileUrl;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    StorageTask uploadTask;
    StorageReference storageReference;

    String myUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_box);

        writingText = (EditText)findViewById(R.id.writingText);
        pickImage = (ImageView)findViewById(R.id.uploadImage);
        writingKeyword = (TextView) findViewById(R.id.writingKeyword);

        profileImage = (ImageView) findViewById(R.id.profileImage);
        myID = (TextView) findViewById(R.id.myID);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        // 파이어베이스에서 아이디랑 프로필사진 가져오자.
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                myID.setText(user.getUserID());

                if(user.getUserImage().equals("default")) {
                    profileImage.setImageResource(R.drawable.image_default);
                } else {
                    Glide.with(WritingBoxActivity.this).load(user.getUserImage()).into(profileImage);
                    profileUrl = user.getUserImage();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 저장한 Shared 불러와서 이미지뷰에 넣기.
//        SharedPreferences sf = getSharedPreferences("profileSet", MODE_PRIVATE);
//        String saveImage = sf.getString("profileSetImg","");
//        String saveID = sf.getString("profileSetID", "");
//
//        // 저장한 값이 비어있으면, 기본 이미지 보여주고, 아니면 저장한 이미지로 보여준다.
//        if(saveImage.isEmpty() && saveID.isEmpty()) {
//
//            profileImage.setImageResource(R.drawable.image_default);
//            myID.setText("My ID");
//
//        } else {
//
//            // 이미지 안보임!!!!!!!!!!!!!!!!!!!!
//            Uri uri = Uri.parse(saveImage);
//            Glide.with(this).load(uri).into(profileImage);
//
//            myID.setText(saveID);
//
//            Log.e("WritingBoxActivity", "프로필사진 uri 는 " + uri);
//        }

    }


    // 글쓰기 키워드 클릭하면,
    public void writingKeyword (View view){

        // 다이얼로그 나오게 하기.
        AlertDialog.Builder builder = new AlertDialog.Builder(WritingBoxActivity.this);
        View view1 = LayoutInflater.from(WritingBoxActivity.this)
                .inflate(R.layout.edit_box, null, false);
        builder.setView(view1);

        // 버튼 텍스트를 "입력" 으로 set.
        Button submit = (Button) view1.findViewById(R.id.button_keyword_submit);
        submit.setText("입력");

        final EditText textKeyword = (EditText) view1.findViewById(R.id.edittext_Keyword);
        final AlertDialog dialog = builder.create();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strColor = "#9C79E6";

                String strKeyword = textKeyword.getText().toString();
                writingKeyword.setText(strKeyword);

                writingKeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                writingKeyword.setGravity(Gravity.CENTER);
                writingKeyword.setTextColor(Color.parseColor(strColor));

                dialog.dismiss();

            }
        });

        dialog.show();

    }

    // 이미지올리기 버튼 클릭하면, 갤러리로 이동.
    public void galleryImage (View view){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // 내 파일로 이동하는 intent
        intent.setType("Image/*");
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(Intent.createChooser(intent, "Upload Picture"), ALBUM);

    }

    // 갤러리에서 선택한 이미지 ImageView 에 넣기.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ALBUM && resultCode == RESULT_OK){

            uploadImageUri = data.getData();
            Glide.with(this).load(uploadImageUri).into(pickImage);

        }
    }

    // 글올리기 버튼을 클릭하면, 이미지랑 텍스트를 MyPageActivity 로 보낸다.
    public void writingOk(View view){

        // Share 에 저장 -> 페이지에 올라간 아이템 편집 선택시 불러와서 수정할 용도.
//        SharedPreferences sharedPreferences = getSharedPreferences("writing", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("writingText", writingText.getText().toString());
//        editor.putString("writingImg", uploadImageUri.toString());
//        editor.commit();

        uploadImage();


        Intent intent = new Intent();
//        intent.putExtra("userID", myID.getText().toString());
//        intent.putExtra("userImage", profileUrl);
        intent.putExtra("content", writingText.getText().toString());
        intent.putExtra("uploadImage", uploadImageUri.toString());
        intent.putExtra("writingKeyword", writingKeyword.getText().toString());
        Log.e("WritingBoxActivity", "userImage : " + profileImage.toString());
        Log.e("WritingBoxActivity","content : " + writingText.getText().toString());
        Log.e("WritingBoxActivity","uploadImage : " + uploadImageUri.toString());
        setResult(RESULT_OK, intent);
        finish();

    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {

        if (writingText != null && writingKeyword != null && pickImage != null) {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            +"."+getFileExtension(uploadImageUri));

            uploadTask = fileReference.putFile(uploadImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){

                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        myUrl = downloadUri.toString();

                        String userid = firebaseUser.getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postKeyword", writingKeyword.getText().toString());
                        hashMap.put("postID", postid);
                        hashMap.put("userID", myID.getText().toString());
                        hashMap.put("userImage", profileUrl);
                        hashMap.put("postImage", myUrl);
                        hashMap.put("description", writingText.getText().toString());
                        hashMap.put("userUID", userid);

                        reference.child(userid).child(postid).setValue(hashMap);

                    } else {

                        Toast.makeText(WritingBoxActivity.this, "실패!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(WritingBoxActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(WritingBoxActivity.this, "선택한 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

}
