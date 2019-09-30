package com.example.carpe.peep;

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
import android.widget.Button;
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

public class WritingEditActivity extends AppCompatActivity {

    private TextView editID;
    private ImageView editProfileImage;
    private EditText editWritingText;
    private ImageView editUploadImage;
    private TextView editKeyword;
    private Button editGallery;
    private Button editWriting;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Uri uri;

    final int EDIT_IMAGE_ALBUM = 5;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_IMAGE_ALBUM && resultCode == RESULT_OK){

            final Uri editUri = data.getData();
            editUploadImage.setImageURI(editUri);
            Log.e("WritingEditActivity", "바뀐 이미지 : " + editUri);

            editWriting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(WritingEditActivity.this, MyPageActivity.class);
                    intent.putExtra("editOkText", editWritingText.getText().toString());
                    intent.putExtra("editOkImage", editUri.toString());
                    intent.putExtra("editOkKeyword", editKeyword.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();

                }
            });
        } else {

            editWriting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(WritingEditActivity.this, MyPageActivity.class);
                    intent.putExtra("editOkText", editWritingText.getText().toString());
                    intent.putExtra("editOkImage", uri.toString());
                    intent.putExtra("editOkKeyword", editKeyword.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();

                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_edit);

        editID = (TextView) findViewById(R.id.editMyID);
        editProfileImage = (ImageView) findViewById(R.id.editProfileImage);
        editWritingText = (EditText) findViewById(R.id.editWritingText);
        editUploadImage = (ImageView) findViewById(R.id.editUploadImage);
        editKeyword = (TextView) findViewById(R.id.editKeyword);
        editGallery = (Button) findViewById(R.id.editGallery);
        editWriting = (Button) findViewById(R.id.editWriting);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                editID.setText(user.getUserID());

                if (user.getUserImage().equals("default")) {

                    editProfileImage.setImageResource(R.drawable.image_default);
                } else {

                    Glide.with(WritingEditActivity.this).load(user.getUserImage()).into(editProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 데이터 받아와서 넣기
        Intent intent = getIntent();
        editWritingText.setText(intent.getStringExtra("editListText"));

        String getImage = intent.getStringExtra("editListImage");
        uri = Uri.parse(getImage);
        editUploadImage.setImageURI(uri);

        String strColor = "#9C79E6";
        editKeyword.setTextColor(Color.parseColor(strColor));
        editKeyword.setGravity(Gravity.CENTER);
        editKeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        editKeyword.setText(intent.getStringExtra("editListKeyword"));

        // 이미지 수정하기 버튼을 누르면, 내파일 불러오기.
        editGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("Image/*");
                startActivityForResult(Intent.createChooser(intent1, "Edit Picture"), EDIT_IMAGE_ALBUM);

            }
        });

        editWriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WritingEditActivity.this, MyPageActivity.class);
                intent.putExtra("editOkText", editWritingText.getText().toString());
                intent.putExtra("editOkImage", uri.toString());
                intent.putExtra("editOkKeyword", editKeyword.getText().toString());
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        editKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 나오게 하기.
                AlertDialog.Builder builder = new AlertDialog.Builder(WritingEditActivity.this);
                View view1 = LayoutInflater.from(WritingEditActivity.this)
                        .inflate(R.layout.edit_box, null, false);
                builder.setView(view1);

                // 버튼 텍스트를 "입력" 으로 set.
                Button submit = (Button) view1.findViewById(R.id.button_keyword_submit);
                submit.setText("입력");

                final EditText textKeyword = (EditText) view1.findViewById(R.id.edittext_Keyword);
                textKeyword.setText(editKeyword.getText());
                final AlertDialog dialog = builder.create();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String strColor = "#9C79E6";

                        String strKeyword = textKeyword.getText().toString();
                        editKeyword.setText(strKeyword);
                        editKeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        editKeyword.setGravity(Gravity.CENTER);
                        editKeyword.setTextColor(Color.parseColor(strColor));

                        dialog.dismiss();

                    }
                });

                dialog.show();

            }
        });

    }

}
