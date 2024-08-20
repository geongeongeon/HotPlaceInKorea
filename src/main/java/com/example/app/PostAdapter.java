package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.CustomViewHolder>{

    private ArrayList<Post> arrayList;
    private Context context;

    public PostAdapter(ArrayList<Post> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        PostAdapter.CustomViewHolder holder = new PostAdapter.CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.CustomViewHolder holder, int position) {

        holder.tv_postnumber.setText(arrayList.get(position).getPostCount()+"");
        holder.tv_posttitle.setText(arrayList.get(position).getPosttitle());
        holder.tv_postuser.setText(arrayList.get(position).getPostuser());
        holder.tv_posttime.setText(arrayList.get(position).getPosttime());
        Glide.with(context).load(arrayList.get(position).getPostimage()).into(holder.iv_postimage);
        holder.tv_poststar.setText(arrayList.get(position).getPostStarAvg()+"");
        holder.tv_postplace.setText(arrayList.get(position).getPostPlace());
        holder.tv_postreport.setText(arrayList.get(position).getPostReport()+"");

    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_postnumber;
        TextView tv_posttitle;
        TextView tv_postuser;
        TextView tv_posttime;
        ImageView iv_postimage;
        TextView tv_poststar;
        TextView tv_postreport;
        TextView tv_postplace;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_postnumber = itemView.findViewById(R.id.tv_postnumber);
            this.tv_posttitle = itemView.findViewById(R.id.tv_posttitle);
            this.tv_postuser = itemView.findViewById(R.id.tv_postuser);
            this.tv_posttime = itemView.findViewById(R.id.tv_posttime);
            this.iv_postimage = itemView.findViewById(R.id.iv_postimage);
            this.tv_poststar = itemView.findViewById(R.id.tv_poststar);
            this.tv_postreport = itemView.findViewById(R.id.tv_postreport);
            this.tv_postplace = itemView.findViewById(R.id.tv_postplace);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentPos = getAdapterPosition();
                    Post post = arrayList.get(currentPos);

                    Intent postInfo = new Intent(context, PostViewActivity.class);
                    postInfo.putExtra("postnumber", post.getPostCount()+"");
                    postInfo.putExtra("posttitle", post.getPosttitle());
                    postInfo.putExtra("postuser", post.getPostuser());
                    postInfo.putExtra("posttime", post.getPosttime());
                    postInfo.putExtra("postcontents", post.getPostcontents());
                    postInfo.putExtra("postimage", post.getPostimage());
                    postInfo.putExtra("postplace", post.getPostPlace());
                    postInfo.putExtra("activity","postlistviewactivity");
                    context.startActivity(postInfo);

                }
            });

        }

    }

}