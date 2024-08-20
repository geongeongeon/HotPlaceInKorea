package com.example.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HotplaceAdapter extends RecyclerView.Adapter<HotplaceAdapter.MyViewHolder> {
    private ArrayList<Post> list;
    private Context context;

    public HotplaceAdapter(Context context, ArrayList<Post> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HotplaceAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.hotpleace_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotplaceAdapter.MyViewHolder holder, int position) {

        holder.hotplace_tv.setText(list.get(position).getPostPlace());
        Glide.with(context).load(list.get(position).getPostPlacePictureUrl()).into(holder.hotplace_iv);

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView hotplace_iv;
        TextView hotplace_tv;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            hotplace_iv = itemView.findViewById(R.id.hotplace_iv);
            hotplace_tv = itemView.findViewById(R.id.hotplace_tv);

        }

    }

}
