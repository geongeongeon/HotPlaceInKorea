package com.example.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    Context context;
    ArrayList<Model> list = new ArrayList<>();

    public ImageAdapter(Context context, ArrayList<Model> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImageUri()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class  MyViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;
        ImageView full_image;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            mImageView = itemView.findViewById(R.id.m_image);
            this.full_image = itemView.findViewById(R.id.m_image);

            full_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPos = getAdapterPosition();
                    Model model = list.get(currentPos);

                    String str_uri = model.getImageUri()+"";
                    Intent img_uri = new Intent(context, FullImageActivity.class);
                    img_uri.putExtra("img_uri", str_uri);
                    context.startActivity(img_uri);
                }
            });
        }
    }
}
