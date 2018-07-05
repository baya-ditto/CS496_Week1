package com.example.q.cs496_week1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<RecyclerItem> mList;
    private int itemLayout;
    CustomItemClickListener listener;



    public RecyclerAdapter(List<RecyclerItem> items, int itemLayout, CustomItemClickListener listener) {
        this.mList = items;
        this.itemLayout = itemLayout;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        final ViewHolder mViewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                listener.onItemClick(v, mViewHolder.getAdapterPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        RecyclerItem item = mList.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.img.setBackgroundResource(item.getImage());
        viewHolder.select_box.setChecked(item.getSelected());
        if (PhonebookFragment.select_flag)
            viewHolder.select_box.setVisibility(View.VISIBLE);
        else
            viewHolder.select_box.setVisibility(View.GONE);

        viewHolder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView name;
        public CheckBox select_box;

        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.contact_pic);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            select_box = (CheckBox) itemView.findViewById(R.id.contact_select);
        }
    }
}
