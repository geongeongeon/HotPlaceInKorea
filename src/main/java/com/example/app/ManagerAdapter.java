package com.example.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.CustomViewHolder>{

    private ArrayList<Post> arrayList;
    private Context context;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public ManagerAdapter(ArrayList<Post> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ManagerAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        ManagerAdapter.CustomViewHolder holder = new ManagerAdapter.CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerAdapter.CustomViewHolder holder, int position) {

        holder.tv_postnumber.setText(arrayList.get(position).getPostCount()+"");
        holder.tv_posttitle.setText(arrayList.get(position).getPosttitle());
        holder.tv_postuser.setText(arrayList.get(position).getPostuser());
        holder.tv_posttime.setText(arrayList.get(position).getPosttime());
        Glide.with(context).load(arrayList.get(position).getPostimage()).into(holder.iv_postimage);
        holder.tv_poststar.setText(arrayList.get(position).getPostStarAvg()+"");
        holder.tv_postreport.setText(arrayList.get(position).getPostReport()+"");
        holder.tv_postplace.setText(arrayList.get(position).getPostPlace());

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

        String place;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            databaseReference = FirebaseDatabase.getInstance().getReference("Application");
            database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

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
                    postInfo.putExtra("activity","mangeractivity");
                    postInfo.putExtra("postplace", post.getPostPlace());

                    context.startActivity(postInfo);

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int currentPos = getAdapterPosition();
                    Post post = arrayList.get(currentPos);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("삭제");
                    builder.setMessage("이 게시글을 삭제하시겠습니까?");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);

                    place = ((ManagerActivity)ManagerActivity.context).spinner_place;

                    // "삭제" 버튼 및 이벤트 생성
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //게시글 삭제하기
                            databaseReference.child("Post").child(place).child(post.getPostCount()+"").getRef().removeValue();
                            //화면 새로고침
                            Intent intent = ((Activity)context).getIntent();
                            ((Activity)context).finish(); //현재 액티비티 종료 실시
                            ((Activity)context).overridePendingTransition(0, 0); //효과 없애기
                            ((Activity)context).startActivity(intent); //현재 액티비티 재실행 실시
                            ((Activity)context).overridePendingTransition(0, 0); //효과 없애기
                            Toast.makeText(context,"삭제되었습니다.",Toast.LENGTH_SHORT).show();

                        }
                    });

                    // "초기화" 버튼 및 이벤트 생성
                    builder.setNegativeButton("초기화", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //아래부터 데이터 업데이트 코드
                            HashMap data = new HashMap();
                            data.put("postReport",0);
                            databaseReference.child("Post").child(place).child(post.getPostCount()+"").updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    //화면 새로고침
                                    Intent intent = ((Activity)context).getIntent();
                                    ((Activity)context).finish(); //현재 액티비티 종료 실시
                                    ((Activity)context).overridePendingTransition(0, 0); //효과 없애기
                                    ((Activity)context).startActivity(intent); //현재 액티비티 재실행 실시
                                    ((Activity)context).overridePendingTransition(0, 0); //효과 없애기
                                    Toast.makeText(context,"초기화되었습니다.",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                    // "취소" 버튼 및 이벤트 생성
                    builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;

                }
            });

        }

    }

}