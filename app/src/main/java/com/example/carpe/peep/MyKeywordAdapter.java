package com.example.carpe.peep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


// My 페이지에 내가 작성할 키워드를 만드는 리사이클러뷰의 어댑터.
public class MyKeywordAdapter extends RecyclerView.Adapter<MyKeywordAdapter.MyKeyViewHolder> {

    private ArrayList<MyKeywordData> myArrayList;
    private Context myContext;

    public class MyKeyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener { // 리스너 추가

        protected TextView myKeyword;
        protected ImageView myKeyEdit;


        public MyKeyViewHolder(View view){
            super(view);

            this.myKeyword = (TextView) view.findViewById(R.id.recyclerview_Mykeyword);
            this.myKeyEdit = (ImageView) view.findViewById(R.id.dot);

            view.setOnCreateContextMenuListener(this); // 리스너 등록
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) { // 메뉴 추가

            MenuItem edit = menu.add(Menu.NONE,1001,1,"편집");
            MenuItem delete = menu.add(Menu.NONE,1002,2,"삭제");
            edit.setOnMenuItemClickListener(onEditMenu);
            delete.setOnMenuItemClickListener(onEditMenu);
        }

        // 컨텍스트 메뉴 클릭시 동작 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case 1001:

                        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
                        View view = LayoutInflater.from(myContext)
                                .inflate(R.layout.edit_box,null,false);
                        builder.setView(view);

                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_keyword_submit);
                        final EditText editTextMyKey = (EditText) view.findViewById(R.id.edittext_Keyword);

                        editTextMyKey.setText(myArrayList.get(getAdapterPosition()).getMyKey());

                        // 다이얼로그를 생성하고, 추가버튼을 눌렀을때,
                        final AlertDialog dialog = builder.create();
                        ButtonSubmit.setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                String keywordStr = editTextMyKey.getText().toString();

                                MyKeywordData mykData = new MyKeywordData(keywordStr);

                                myArrayList.set(getAdapterPosition(), mykData);
                                notifyItemChanged(getAdapterPosition());

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                    case 1002:

                        myArrayList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(),myArrayList.size());

                        Toast.makeText(myContext,"키워드를 삭제했습니다.",Toast.LENGTH_SHORT).show();

                        break;
                }
                return true;
            }
        };

    }

    public MyKeywordAdapter(Context context, ArrayList<MyKeywordData> list){
        myArrayList = list;
        myContext = context;
    }


    // 뷰홀더를 만들어주고,
    @NonNull
    @Override
    public MyKeyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_mykeyword, viewGroup, false);

        MyKeyViewHolder myKeyViewHolder = new MyKeyViewHolder(view);

        return myKeyViewHolder;
    }

    // 만들어진 뷰홀더에 데이터를 넣는다.
    @Override
    public void onBindViewHolder(@NonNull MyKeyViewHolder holder, final int position) {

        String strColor = "#9C79E6";

        holder.myKeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        holder.myKeyword.setGravity(Gravity.CENTER);
        holder.myKeyword.setTextColor(Color.parseColor(strColor));

        // MyKeywordData 가 들어있는 myArrayList 에서 선택한 포지션의 MyKey 를 가져와서 set.
        holder.myKeyword.setText(myArrayList.get(position).getMyKey());

        // myKeyword 를 클릭하면 Activity_my_page 로 연결
        holder.myKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Activity_my_page 에 선택된 해당 키워드를 보낸다.
                Intent intent = new Intent(myContext,MyPageActivity.class);
                intent.putExtra("Keyword", myArrayList.get(position).getMyKey());
                myContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != myArrayList ? myArrayList.size() : 0);
    }
}
