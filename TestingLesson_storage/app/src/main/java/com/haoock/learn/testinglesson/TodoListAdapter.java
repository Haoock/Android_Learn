package com.haoock.learn.testinglesson;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.MessagePattern;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.TreeMap;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder>{
    private List<Todo_Item> mTodo_List;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private MyDatabaseHelper dbHelper;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView context_text;
        TextView date_text;
        TextView cancle_textView;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context_text = itemView.findViewById(R.id.main_text);
            date_text = itemView.findViewById(R.id.sub_text);
            cancle_textView = itemView.findViewById(R.id.cancel_view);
            checkBox = itemView.findViewById(R.id.checkbox);

        }
    }

    public void notifyItems(@NonNull List<Todo_Item> items){
        mTodo_List = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Todo_Item item = mTodo_List.get(position);
        System.out.println("这个状态为："+item.getState());
        if (item.getState().equals("0")){
            holder.checkBox.setChecked(false);
        }else{
            holder.checkBox.setChecked(true);
        }
        holder.context_text.setText(item.getContent());
        holder.date_text.setText(item.getDate());
        if (holder.checkBox.isChecked()){
            //如果是选中状态
            new Thread(new Runnable() {
                @Override
                public void run() {
                    holder.context_text.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
                    holder.context_text.setTextColor(Color.parseColor("#FF4F4F4F"));
                }
            }).start();

        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    holder.context_text.getPaint().setFlags(0); //中划线
                    holder.context_text.setTextColor(Color.BLACK);
                }
            }).start();
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    System.out.println("选中");
                    dbHelper = new MyDatabaseHelper(buttonView.getContext(), "TodoList.db", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("state", "1");
                    db.update("Table_List",values, "id = ?",new String[]{String.valueOf(item.getId())});
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            holder.context_text.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
                            holder.context_text.setTextColor(Color.parseColor("#FF4F4F4F"));
                        }
                    }).start();

                } else{
                    System.out.println("未选中");
                    dbHelper = new MyDatabaseHelper(buttonView.getContext(), "TodoList.db", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("state", "0");
                    db.update("Table_List",values, "id = ?",new String[]{String.valueOf(item.getId())});
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            holder.context_text.getPaint().setFlags(0); //中划线
                            holder.context_text.setTextColor(Color.BLACK);
                        }
                    }).start();

                }
            }
        });
        holder.cancle_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(v.getContext());
                alert = builder.setTitle("提示：")
                        .setMessage("确认删除数据吗？")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            //点击确认之后
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper = new MyDatabaseHelper(v.getContext(), "TodoList.db", null, 1);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.delete("Table_List", "id = ?", new String[]{String.valueOf(item.getId())});
                                mTodo_List.remove(item);
                               notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alert.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTodo_List.size();
    }

    public TodoListAdapter(List<Todo_Item> mTodo_List) {

        this.mTodo_List = mTodo_List;
    }
}
