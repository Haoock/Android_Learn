package com.haoocker.learn.testinglesson1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.TextViewHolder> implements Filterable {
    @NonNull
//    private List<String> mItems = new ArrayList<>();
    //一个是过滤之前的列表，一个是过滤之后的列表
    private List<String> mSourceList = new ArrayList<>();
    private List<String> mFilterList = new ArrayList<>();

    //这个内部类在下面onCreateViewHolder重写函数中使用到
    static class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text);
        }

        public void bind(String text) {
            mTextView.setText(text);
        }
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_text,parent,false);
        final TextViewHolder holder = new TextViewHolder(view);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String item = mFilterList.get(position);
                Toast.makeText(v.getContext(), "you clicked view " + item, Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        //过滤后的list
        holder.bind(mFilterList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }
    //初始化的操作
    public void notifyItems(@NonNull List<String> items){
        mSourceList = items;
        mFilterList = items;
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            //执行过滤操作
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = mSourceList;
                } else {
                    List<String> filteredList = new ArrayList<>();
                    for (String str : mSourceList) {
                        //这里根据需求，添加匹配规则
                        if (str.contains(charString)) {
                            filteredList.add(str);
                        }
                    }

                    mFilterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterList;
                return filterResults;
            }
            //把过滤后的值返回出来
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterList = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
