package com.example.carpe.peep;

import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.content.Context;
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
import android.widget.TextView;

import java.util.ArrayList;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {

    private ArrayList<KeywordData> myArrayList;
    private Context mContext;


    // 컨텍스트 메뉴를 사용하려면 RecyclerView.ViewHolder 를 상속받은 클래스에서 OnCreateContextMenuListener 를 구현해야한다.
    // 리스너 생성.
    public class KeywordViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        protected TextView mkeyword;

        public KeywordViewHolder(View view){

            super(view);
            this.mkeyword = (TextView) view.findViewById(R.id.recyclerview_keyword);

            // 리스너를 현재 클래스에 구현한다고 설정.
            view.setOnCreateContextMenuListener(this);
        }

        // 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해준다. ID 1001, 1002로 어떤메뉴 선택했는지 구분.
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        // 항목 클리시 동작을 설정.
        public final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    // 편집 선택시,
                    case 1001:

                        // 다이얼로그를 보여주기위해 edit_box.xml 파일을 사용한다.
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        View view = LayoutInflater.from(mContext)
                                .inflate(R.layout.edit_box, null, false);
                        builder.setView(view);
                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_keyword_submit);
                        final EditText editTextKeyword = (EditText) view.findViewById(R.id.edittext_Keyword);

                        // 입력되어 있던 데이터를 불러와서 다이얼로그에 보여준다.
                        editTextKeyword.setText(myArrayList.get(getAdapterPosition()).getKeyword());

                        final AlertDialog dialog = builder.create();
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {

                            // 수정 버튼을 클릭하면 현재 UI 에 입력되어 있는 내용으로
                            @Override
                            public void onClick(View v) {

                                String strKeyword = editTextKeyword.getText().toString();

                                // ArrayList 에 있는 데이터를 변경하고,
                                KeywordData kdata = new KeywordData(strKeyword);
                                myArrayList.set(getAdapterPosition(), kdata);

                                // 어댑터에서 RecyclerView 에 반영하도록 한다.
                                notifyItemChanged(getAdapterPosition());

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                        // 삭제 항목 선택시,
                    case 1002:

                        // ArrayList 에서 해당 데이터를 삭제하고,
                        myArrayList.remove(getAdapterPosition());

                        // 어댑터에서 RecyclerView 에 반영한다.
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), myArrayList.size());

                        break;

                }
                return true;
            }
        };

    }

    public KeywordAdapter(Context context, ArrayList<KeywordData> list) {

        myArrayList = list;
        mContext = context;

    }

    // 뷰홀더 만들어주고,
    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_keyword, viewGroup, false);

        KeywordViewHolder viewHolder = new KeywordViewHolder(view);
        return viewHolder;
    }

    // 뷰홀더에 데이터를 넣어주기.
    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder viewHolder, int position) {

        String strColor = "#9C79E6";

        viewHolder.mkeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        viewHolder.mkeyword.setTextColor(Color.parseColor(strColor));
        viewHolder.mkeyword.setGravity(Gravity.CENTER);
        viewHolder.mkeyword.setText(myArrayList.get(position).getKeyword());

    }

    @Override
    public int getItemCount() {
        return (null != myArrayList ? myArrayList.size() : 0);
    }
}
