package com.example.carpe.peep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WritingAdapter extends RecyclerView.Adapter<WritingAdapter.WritingViewHolder> {


    private ArrayList<WritingData> wList;
    private Context wContext;
    private AlertDialog dialog; // 삭제버튼 누를 때,

    private OnMenuItemClickListener listener;

    private int itemPosition;

    public class WritingViewHolder extends RecyclerView.ViewHolder {

        protected TextView myID;
        protected ImageView myImage;
        protected TextView myContent;
        protected ImageView myTopicImage;
        protected TextView myKeyword;
        protected ImageView myOption;
        protected TextView answer;
//        LinearLayout linearLayout;

        FirebaseUser firebaseUser;
        DatabaseReference reference;


        // 뷰홀더 id 지정.
        public WritingViewHolder(View view) {

            super(view);

//            this.linearLayout = (LinearLayout) view.findViewById(R.id.pageItem);

            this.myID = (TextView) view.findViewById(R.id.myWritingID);
            this.myImage = (ImageView) view.findViewById(R.id.myProfileImage);
            this.myContent = (TextView) view.findViewById(R.id.myContent);
            this.myTopicImage = (ImageView) view.findViewById(R.id.myTopicImage);
            this.myKeyword = (TextView) view.findViewById(R.id.myKeyword);

            // 편집, 삭제 할 수 있는 버튼
            this.myOption = (ImageView) view.findViewById(R.id.myEditMenu);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    myID.setText(user.getUserID());

                    if (user.getUserImage().equals("default")) {

                        myImage.setImageResource(R.drawable.image_default);
                    } else {

                        Glide.with(wContext).load(user.getUserImage()).into(myImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // 댓글달기 버튼 클릭했을때,
           this.answer = (TextView) view.findViewById(R.id.comments);
            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(wContext, CommentActivity.class);
                    wContext.startActivity(intent);

                }
            });

        }
    }


    public WritingAdapter(Context context, ArrayList<WritingData> list) {

        wList = list;
        wContext = context;

    }

    // 뷰홀더 만들기
    @Override
    public WritingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_mypagelist, viewGroup, false);

        WritingViewHolder viewHolder = new WritingViewHolder(view);

        return viewHolder;
    }

    // 뷰홀더에 data set
    @Override
    public void onBindViewHolder(@NonNull final WritingViewHolder holder, final int position) {

        holder.myContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        holder.myContent.setText(wList.get(position).getWriting());

        // 현재 이미지데이터가 String 으로 저장이 되어있기때문에, Uri 로 바꿔준 후 뷰홀더에 넣는다.
        final Uri uri = Uri.parse(wList.get(position).getUpImageUri());
        holder.myTopicImage.setImageURI(uri);

        String strColor = "#9C79E6";

        holder.myKeyword.setTextColor(Color.parseColor(strColor));
        holder.myKeyword.setGravity(Gravity.CENTER);
        holder.myKeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.myKeyword.setText(wList.get(position).getWritingKeyword());

//         MyPageActivity 에 있는 setTopKeyword 메소드를 불러와서 리턴값인 keyword 를 getKeyword 에 담는다.
        ((MyPageActivity) wContext).setTopKeyword();
        String getKeyword = ((MyPageActivity) wContext).keyword;
        Log.e("WritingAdapter", "getKeyword : " + getKeyword);

        if (!getKeyword.equals(holder.myKeyword.getText().toString())) {

            // 가져온 키워드와 아이템에 있는 키워드가 같지 않으면,
            // 아이템을 GONE 처리해준다.
            holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
            holder.itemView.setVisibility(View.GONE);
            Log.d("WritingAdapter", "holder.myKeyword.toString : " + holder.myKeyword.getText().toString());

        }


        holder.myOption.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                Log.e("WritingAdapter", "메뉴버튼클릭했을때");

                // 팝업메뉴를 만들고 menu inflating
                final PopupMenu popupMenu = new PopupMenu(wContext, holder.myOption);
                popupMenu.inflate(R.menu.writing_edit_menu);

                // 팝업메뉴 클릭했을때,
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {

                        switch (item.getItemId()) {

                            // 편집을 누르면,
                            case R.id.edit:

                                itemPosition = holder.getAdapterPosition();

                                // 편집 리스너 만들기
                                if (listener != null && itemPosition != RecyclerView.NO_POSITION) {
                                    listener.onMenuItemClick(wList.get(position), itemPosition);
                                }


                                // intent 에 선택한 position 데이터를 담아서 글편집 액티비티를 호출한다.
//                                Intent intent = new Intent(wContext, WritingEditActivity.class);
//                                intent.putExtra("editListText", wList.get(position).getWriting());
//                                intent.putExtra("editListImage", wList.get(position).getUpImageUri());
//                                intent.putExtra("editListKeyword", wList.get(position).getWritingKeyword());
//
//                                wContext.startActivity(intent);

//                                Activity activity = (Activity)wContext;
//                                activity.startActivityForResult(new Intent(wContext, WritingEditActivity.class), EDIT_DATA);

                                // Activity 에 LayoutInflater 해서 사용했을때, 문제 :
                                // 일단 finish 로 닫으면 MyPageActivity 가 닫힌다.
                                // 두번째, 사진을 가져오기 위한 메소드 -> onActivityResult 를 사용하지못한다.

//                                final Activity activity = (Activity)wContext;
//                                View view = LayoutInflater.from(wContext)
//                                        .inflate(R.layout.activity_writing_edit, null, false);
//
//                                activity.setContentView(view);
//
//                                // activity 에 담긴 뷰 id 지정.
//                                final ImageView editUploadImage =view.findViewById(R.id.editUploadImage);
//                                final EditText editWritingText = view.findViewById(R.id.editWritingText);
//                                Button editGallery = view.findViewById(R.id.editGallery);
//                                Button editWriting = view.findViewById(R.id.editWriting);
//
//                                // ImageView, EditText 에 불러온 값을 넣어준다.
//                                final Uri uri1 = Uri.parse(wList.get(position).getUpImageUri());
//                                editUploadImage.setImageURI(uri1);
//                                editWritingText.setText(wList.get(position).getWriting());
//
//                                // 사진 수정하기 버튼을 클릭하면,
//                                editGallery.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                        intent.setType("Image/*");
//                                        activity.startActivityForResult(Intent.createChooser(intent, "edit Picture"),EDIT_ALBUM);
//                                    }
//                                });
//
//                                editWriting.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                        String editText = editWritingText.getText().toString();
//                                        String editImage = editUploadImage.toString();
//
//                                        WritingData wData = new WritingData(editText, editImage);
//                                        wList.set(position, wData);
//                                        notifyItemChanged(position);
//
//                                        activity.finish();
//
//                                    }
//                                });

                                return true;

                            // 삭제를 누르면,
                            case R.id.delete:

                                AlertDialog.Builder builder = new AlertDialog.Builder(wContext);
                                builder.setMessage("정말 삭제하시겠습니까?")
                                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                // ArrayList 에서 삭제하고, item 삭제
                                                wList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, wList.size());

                                                Toast.makeText(wContext, "기록을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });

                                dialog = builder.create();
                                dialog.show();

                                return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }


    // 리스너에 WritingData, position 을 파라미터로 넣어서
    public interface OnMenuItemClickListener {
        void onMenuItemClick(WritingData writingData, int position);
    }

    // 리사이클러뷰 액티비티에 리스너 구현을 위해
    public void setListener(OnMenuItemClickListener listener) {
        this.listener = listener;
    }


    @Override
    public int getItemCount() {
        return wList.size();
//        return (null != wList ? wList.size() : 0);
    }
}