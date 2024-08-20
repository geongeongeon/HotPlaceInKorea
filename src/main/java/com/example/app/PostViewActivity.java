package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PostViewActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Comment> arrayList;

    TextView posttitle,postuser,posttime,postcontents,postplace;
    ImageView postimage;
    TextView commentcontents;
    Button btn_comment;

    String commentuser;
    int commentcount, updated_commentcount;

    Button btn_star, btn_report;

    int poststar, postreport;
    int updated_poststar, updated_postreport;
    public static Context context;

    Dialog dialog_star;

    String place;

    RatingBar mediumRatingBar;
    TextView tv_teststar;
    float star;
    float add_star;

    int cnt_star;

    TextView tv_starview,tv_reportview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        context = this;

        //게시글
        posttitle = findViewById(R.id.tv_posttitleview);
        postuser = findViewById(R.id.tv_postuserview);
        posttime = findViewById(R.id.tv_posttimeview);
        postcontents = findViewById(R.id.tv_postcontentsview);
        postimage = findViewById(R.id.iv_postimageview);
        postplace = findViewById(R.id.tv_postplace);

        //댓글
        commentcontents = findViewById(R.id.et_comment);
        btn_comment = findViewById(R.id.btn_comment);

        Intent postInfo = getIntent();

        String get_postnumber = postInfo.getStringExtra("postnumber");
        String get_posttitle = postInfo.getStringExtra("posttitle");
        String get_postuser = postInfo.getStringExtra("postuser");
        String get_posttime = postInfo.getStringExtra("posttime");
        String get_postcontents = postInfo.getStringExtra("postcontents");
        String get_postimage = postInfo.getStringExtra("postimage");
        String get_activity = postInfo.getStringExtra("activity");
        String get_postplace = postInfo.getStringExtra("postplace");

        posttitle.setText(get_posttitle);
        postuser.setText(get_postuser);
        posttime.setText(get_posttime);
        postcontents.setText(get_postcontents);
        postplace.setText(get_postplace);
        Glide.with(getApplicationContext()).load(get_postimage).into(postimage);

        postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_uri = get_postimage;
                Intent img_uri = new Intent(PostViewActivity.this, PostFullImageActivity.class);
                img_uri.putExtra("img_uri", str_uri);
                startActivity(img_uri);
            }
        });


        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Application");
        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        if(get_activity.equals("postlistviewactivity"))
        {
            place = ((PostListViewActivity)PostListViewActivity.context).spinner_place;
        }
        else if(get_activity.equals("mangeractivity"))
        {
            place = ((ManagerActivity)ManagerActivity.context).spinner_place;
        }

        tv_starview = findViewById(R.id.tv_starview);
        //별점 가져오기
        databaseReference.child("Post").child(place).child(get_postnumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Post post = snapshot.getValue(Post.class);
                    tv_starview.setText(post.getPostStarAvg()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tv_reportview = findViewById(R.id.tv_reportview);
        //신고 횟수 가져오기
        databaseReference.child("Post").child(place).child(get_postnumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Post post = snapshot.getValue(Post.class);
                    tv_reportview.setText(post.getPostReport()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //현재 시간 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = dateFormat.format(date);

        //유저닉네임 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    commentuser = userAccount.getNickname();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //commentCount 가져오기
        databaseReference.child("Post").child(place).child(get_postnumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Post post = snapshot.getValue(Post.class);
                    commentcount = post.getPostCommentCount();
                    updated_commentcount = commentcount+1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //버튼 누르면 파이어베이스에 데이터 저장
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment();
                comment.setCommentuser(commentuser);
                comment.setCommentcontents(commentcontents.getText().toString());
                comment.setCommenttime(getTime);
                comment.setCommentcount(updated_commentcount);

                //setValue : 데이터베이스에 넣기
                databaseReference.child("Post").child(place).child(get_postnumber).child("Comment").child(updated_commentcount+"").setValue(comment);

                //update commentcount
                HashMap data = new HashMap();
                data.put("postCommentCount",updated_commentcount);

                databaseReference.child("Post").child(place).child(get_postnumber).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                    }
                });
                //새로고침
                finish();//인텐트 종료
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Intent intent = getIntent(); //인텐트
                startActivity(intent); //액티비티 열기
                overridePendingTransition(0, 0);//인텐트 효과 없애기
            }
        });

        //리사이클러뷰
        recyclerView = findViewById(R.id.rcv_comment); //아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); //객체를 담을 어레이 리스트

        //댓글 데이터 가져와서 arrayList에 담기
        databaseReference.child("Post").child(place).child(get_postnumber).child("Comment").orderByChild("commentcount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //반복문으로 데이터 리스트 추출해냄
                    Comment comment = snapshot.getValue(Comment.class); //ShopItem 객체에 데이터를 담는다.
                    arrayList.add(comment); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //db를 가져오던 중 에러 발생 시
                Log.e("PostViewActivity"
                        , String.valueOf(error.toException())); //에러문 출력
            }
        });

        btn_star = findViewById(R.id.btn_star);
        btn_report = findViewById(R.id.btn_report);

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("신고");
                builder.setMessage("이 게시글을 신고하시겠습니까?");
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                //postReport 가져오기
                            databaseReference.child("Post").child(place).child(get_postnumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        Post post = snapshot.getValue(Post.class);
                                        postreport = post.getPostReport();
                            updated_postreport = postreport+1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // "네" 버튼 및 이벤트 생성
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //아래부터 데이터 업데이트 코드
                        HashMap data = new HashMap();
                        data.put("postReport",updated_postreport);
                        databaseReference.child("Post").child(place).child(get_postnumber).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(PostViewActivity.this, "신고 완료", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();//인텐트 종료
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                        Intent intent = getIntent(); //인텐트
                        startActivity(intent); //액티비티 열기
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                    }
                });

                // "아니요" 버튼 및 이벤트 생성
                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //리사이클러뷰 CommentAdapter 연동
        adapter = new CommentAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        dialog_star = new Dialog(PostViewActivity.this);       // Dialog 초기화
        dialog_star.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog_star.setContentView(R.layout.dialog_star); //xml 연결

        // 버튼: 커스텀 다이얼로그 띄우기
        btn_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(); // 아래 showDialog01() 함수 호출
            }
        });

    }

    public void showDialog(){

        dialog_star.show(); // 다이얼로그 띄우기

        Intent postInfo = getIntent();

        String get_postnumber = postInfo.getStringExtra("postnumber");

        place = ((PostListViewActivity)PostListViewActivity.context).spinner_place;

        //postStar 가져오기
        databaseReference.child("Post").child(place).child(get_postnumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Post post = snapshot.getValue(Post.class);
                    add_star = post.getPostStar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //postStarCount 가져오기
        databaseReference.child("Post").child(place).child(get_postnumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Post post = snapshot.getValue(Post.class);
                    cnt_star = post.getPostStarCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mediumRatingBar = dialog_star.findViewById(R.id.mediumRatingBar);
        tv_teststar = dialog_star.findViewById(R.id.tv_teststar);

        mediumRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                star = rating;
                tv_teststar.setText(star+"");
            }
        });
        // 아니오 버튼
        Button noBtn = dialog_star.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_star.dismiss(); // 다이얼로그 닫기
            }
        });
        // 네 버튼
        dialog_star.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아래부터 데이터 업데이트 코드
                HashMap data = new HashMap();
                data.put("postStar",add_star+star);
                data.put("postStarCount",cnt_star+1);
                data.put("postStarAvg",Math.round(((add_star+star)/cnt_star)*100)/100.0);
                databaseReference.child("Post").child(place).child(get_postnumber).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                    }
                });
                finish();//인텐트 종료
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Intent intent = getIntent(); //인텐트
                startActivity(intent); //액티비티 열기
                overridePendingTransition(0, 0);//인텐트 효과 없애기
            }
        });
    }

}