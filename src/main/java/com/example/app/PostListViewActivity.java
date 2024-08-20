package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostListViewActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Post> arrayList;

    private Spinner spinner;

    public String spinner_place,spinner_result;

    public static Context context;

    TextView filter;

    Dialog dialog_filter;

    String filter_result;

    TextView filter_time,filter_star;

    int cnt;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String usernickname, usergrade, userimageurl;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list_view);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        context = this;

        mFirebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_topost).setOnClickListener(this);

        recyclerView = findViewById(R.id.postrecycler_view); //아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //객체를 담을 어레이 리스트

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Application"); //DB 테이블 연결

        adapter = new PostAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        dialog_filter = new Dialog(PostListViewActivity.this);       // Dialog 초기화
        dialog_filter.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog_filter.setContentView(R.layout.dialog_filter); //xml 연결

        filter = findViewById(R.id.filter);

        filter_time = dialog_filter.findViewById(R.id.filter_time);
        filter_star = dialog_filter.findViewById(R.id.filter_star);

        //툴바
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24); //왼쪽 상단 버튼 아이콘 지정

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        //헤더
        View nav_header_view = navigationView.getHeaderView(0);
        TextView nav_header_nickname = (TextView) nav_header_view.findViewById(R.id.tv_header_nickname);
        TextView nav_header_grade = (TextView) nav_header_view.findViewById(R.id.tv_header_grade);
        ImageView nav_header_image = (ImageView) nav_header_view.findViewById(R.id.iv_header_image);

        //유저닉네임 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    usernickname = userAccount.getNickname();

                    //헤더 값 변경
                    nav_header_nickname.setText(usernickname+" 님");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //유저등급 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    usergrade = userAccount.getGrade();

                    //헤더 값 변경
                    nav_header_grade.setText(usergrade);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //프로필 이미지 url 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    userimageurl = userAccount.getImageUrl();

                    //헤더 값 변경
                    Glide.with(getApplicationContext()).load(userimageurl).into(nav_header_image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //네비게이션뷰의 아이템을 클릭했을때
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.item_gallery:
                        Intent intent_gallery = new Intent(PostListViewActivity.this,ImageActivity.class);
                        startActivity(intent_gallery);
                        break;
                    case R.id.item_post:
                        drawerLayout.closeDrawer(navigationView); //네비게이션뷰 닫기
                        break;
                    case R.id.item_profile:
                        Intent intent_profile = new Intent(PostListViewActivity.this,EditProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                    case R.id.item_main:
                        Intent intent_main = new Intent(PostListViewActivity.this,MainActivity.class);
                        startActivity(intent_main);
                        break;
                    case R.id.item_navigation:
                        Intent intent_navigation = new Intent(PostListViewActivity.this,NavigationActivity.class);
                        startActivity(intent_navigation);
                        break;
                    case R.id.item_manager:

                        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                                    usergrade = userAccount.getGrade();
                                    if(usergrade.equals("관리자"))
                                    {
                                        Intent intent_manager = new Intent(PostListViewActivity.this,ManagerActivity.class);
                                        startActivity(intent_manager);
                                    }
                                    else
                                    {
                                        Toast.makeText(PostListViewActivity.this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;
                    case R.id.item_logout:
                        Toast.makeText(PostListViewActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        Intent intent_logout = new Intent(PostListViewActivity.this,LoginActivity.class);
                        startActivity(intent_logout);
                        finish();
                        break;
                }

                //Drawer를 닫기...
                drawerLayout.closeDrawer(navigationView);

                return false;
            }
        });

        if(cnt==0)
        {
            filter_result="postCount";
            cnt=1;
        }
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        //스피너
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_result = parent.getItemAtPosition(position).toString();
                switch(spinner_result){
                    case  "경기도" :
                        spinner_place = "Gyeonggi-do";
                        break;
                    case  "강원도" :
                        spinner_place = "Gangwon-do";
                        break;
                    case  "충청도" :
                        spinner_place = "Chungcheong-do";
                        break;
                    case  "전라도" :
                        spinner_place = "Jeolla-do";
                        break;
                    case  "경상도" :
                        spinner_place = "Gyeongsang-do";
                        break;
                    case  "제주도" :
                        spinner_place = "Jeju-do";
                        break;
                }
                databaseReference.child("Post").child(spinner_place).orderByChild(filter_result).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                        arrayList.clear(); //초기화
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //반복문으로 데이터 리스트 추출해냄
                            Post post = snapshot.getValue(Post.class); //ShopItem 객체에 데이터를 담는다.
                            arrayList.add(0,post); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                        }
                        adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //db를 가져오던 중 에러 발생 시
                        Log.e("PostListViewActivity", String.valueOf(error.toException())); //에러문 출력
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //스크롤하면 새로고침
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(false);
                finish();//인텐트 종료
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Intent intent = getIntent(); //인텐트
                startActivity(intent); //액티비티 열기
                overridePendingTransition(0, 0);//인텐트 효과 없애기

            }
        });

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, WritePostActivity.class));
    }

    public void showDialog(){

        dialog_filter.show(); // 다이얼로그 띄우기

        //시간순
        filter_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_filter.dismiss();
                filter.setText("시간순");
                filter_result = "postCount";
                databaseReference.child("Post").child(spinner_place).orderByChild(filter_result).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                        arrayList.clear(); //초기화
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //반복문으로 데이터 리스트 추출해냄
                            Post post = snapshot.getValue(Post.class); //ShopItem 객체에 데이터를 담는다.
                            arrayList.add(0,post); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                        }
                        adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //db를 가져오던 중 에러 발생 시
                        Log.e("PostListViewActivity", String.valueOf(error.toException())); //에러문 출력
                    }
                });
            }
        });

        //별점순
        filter_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_filter.dismiss();
                filter.setText("별점순");
                filter_result = "postStarAvg";
                databaseReference.child("Post").child(spinner_place).orderByChild(filter_result).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                        arrayList.clear(); //초기화
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //반복문으로 데이터 리스트 추출해냄
                            Post post = snapshot.getValue(Post.class); //ShopItem 객체에 데이터를 담는다.
                            arrayList.add(0,post); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                        }
                        adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //db를 가져오던 중 에러 발생 시
                        Log.e("PostListViewActivity", String.valueOf(error.toException())); //에러문 출력
                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}