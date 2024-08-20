package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Model> list;
    ImageAdapter adapter;
    private FirebaseAuth auth; //파이어베이스 인증

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Application");

    private Spinner spinner;

    Button btn_addimage;
    String spinner_place,spinner_result;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String usernickname, usergrade, userimageurl;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser(); //로그인한 유저

        // 툴바
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
        root.child("UserAccount").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        root.child("UserAccount").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        root.child("UserAccount").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        drawerLayout.closeDrawer(navigationView); //네비게이션뷰 닫기

                        break;
                    case R.id.item_post:
                        Intent intent_post = new Intent(ImageActivity.this,PostListViewActivity.class);
                        startActivity(intent_post);
                        break;
                    case R.id.item_profile:
                        Intent intent_profile = new Intent(ImageActivity.this,EditProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                    case R.id.item_main:
                        Intent intent_main = new Intent(ImageActivity.this,MainActivity.class);
                        startActivity(intent_main);
                        break;
                    case R.id.item_navigation:
                        Intent intent_navigation = new Intent(ImageActivity.this,NavigationActivity.class);
                        startActivity(intent_navigation);
                        break;
                    case R.id.item_manager:

                        root.child("UserAccount").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                                    usergrade = userAccount.getGrade();
                                    if(usergrade.equals("관리자"))
                                    {
                                        Intent intent_manager = new Intent(ImageActivity.this,ManagerActivity.class);
                                        startActivity(intent_manager);
                                    }
                                    else
                                    {
                                        Toast.makeText(ImageActivity.this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;
                    case R.id.item_logout:
                        Toast.makeText(ImageActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                        Intent intent_logout = new Intent(ImageActivity.this,LoginActivity.class);
                        startActivity(intent_logout);
                        finish();
                        break;
                }

                //Drawer를 닫기...
                drawerLayout.closeDrawer(navigationView);

                return false;
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        list = new ArrayList<>();

        adapter = new ImageAdapter(ImageActivity.this, list);

        recyclerView.setAdapter(adapter);

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

                list.clear(); //초기화
                root.child("Gallery").child(firebaseUser.getUid()).child(spinner_place).child("Image").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Model model = dataSnapshot.getValue(Model.class);
                            list.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //addimage 버튼 클릭
                btn_addimage = findViewById(R.id.btn_addimage);
                btn_addimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_gallryActivity = new Intent(ImageActivity.this, GalleryActivity.class);
                        intent_gallryActivity.putExtra("place", spinner_place);
                        startActivity(intent_gallryActivity);
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