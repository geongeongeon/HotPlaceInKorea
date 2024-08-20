package com.example.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    //툴바
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //지도 변수 선언
    private LinearLayout linearLayoutTmap;
    private Context context;
    private TMapView tMapView;

    //검색창 관련 변수 선언
    private EditText inputStart_editText;
    private EditText inputEnd_editText;
    private ListView searchResult_listView;
    private List<String> list_data;
    private ArrayAdapter<String> adapter;
    private boolean display_listView = false;
    private boolean startLocation_finish = false;

    private Button findPath_btn;
    private Button change_btn;

    private int findPath_case = 0; // default => 보행자

    //TMap API 변수 선언
    private TMapData tMapData;

    //Marker 관련 변수 선언
    private Bitmap bitmap;
    private TMapPoint tMapPoint;
    private Marker startMarker;
    private Marker endMarker;

    //경로 안내 detail info 관련 변수 선언
    private Button detailInfo_path_btn;
    private Element root;

    //현위치 표시 관련 변수 선언
    private Button showCurPosition_btn;
    Bitmap bitmap2;
    TMapGpsManager tMapGpsManager;
    TMapPoint curPosition;

    String usernickname, usergrade, userimageurl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Application");
        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
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
                        Intent intent_gallery = new Intent(NavigationActivity.this,ImageActivity.class);
                        startActivity(intent_gallery);
                        break;
                    case R.id.item_post:
                        Intent intent_post = new Intent(NavigationActivity.this,PostListViewActivity.class);
                        startActivity(intent_post);
                        break;
                    case R.id.item_profile:
                        Intent intent_profile = new Intent(NavigationActivity.this,NavigationActivity.class);
                        startActivity(intent_profile);
                        break;
                    case R.id.item_main:
                        Intent intent_home = new Intent(NavigationActivity.this,EditProfileActivity.class);
                        startActivity(intent_home);
                        break;

                    case R.id.item_navigation:
                        drawerLayout.closeDrawer(navigationView); //네비게이션뷰 닫기
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
                                        Intent intent_manager = new Intent(NavigationActivity.this,ManagerActivity.class);
                                        startActivity(intent_manager);
                                    }
                                    else
                                    {
                                        Toast.makeText(NavigationActivity.this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;
                    case R.id.item_logout:
                        Toast.makeText(NavigationActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        Intent intent_logout = new Intent(NavigationActivity.this,LoginActivity.class);
                        startActivity(intent_logout);
                        finish();
                        break;
                }

                //Drawer를 닫기...
                drawerLayout.closeDrawer(navigationView);

                return false;
            }
        });

        //검색창 변수 설정
        inputStart_editText = (EditText) findViewById(R.id.inputStart_editText);
        inputEnd_editText = (EditText) findViewById(R.id.inputEnd_editText);
        searchResult_listView = (ListView)findViewById(R.id.searchResultList);
        list_data = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_data);
        searchResult_listView.setAdapter(adapter);

        findPath_btn = (Button)findViewById(R.id.findPath_btn);
        change_btn = (Button)findViewById(R.id.change_btn);

        //지도 변수 설정
        linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        context = this;
        tMapView = new TMapView(context);

        //TMap API 변수 설정
        tMapData = new TMapData();

        //Marker 관련 변수 설정
        startMarker = new Marker();
        endMarker = new Marker();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_dot);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.poi_star);
        tMapPoint = new TMapPoint(0,0);

        //경로 안내 detail info 관련 변수 설정
        detailInfo_path_btn = (Button)findViewById(R.id.showDetailInfo_Path);
        detailInfo_path_btn.setVisibility(View.INVISIBLE);

        //현위치 표시 관련 변수 설정
        showCurPosition_btn = (Button)findViewById(R.id.showCurPosition_Btn);
        tMapGpsManager = new TMapGpsManager(context);
        tMapGpsManager.setMinTime(1000);
        tMapGpsManager.setMinDistance(5);
        tMapGpsManager.setProvider(tMapGpsManager.NETWORK_PROVIDER);

        //지도 설정
        context = this;
        tMapView = new TMapView(context);
        tMapView.setHttpsMode(true);
        tMapView.setSKTMapApiKey( "l7xx71d0f56b9dd94407a77a7a6ca24e4eef" );
        linearLayoutTmap.addView( tMapView );

        //검색창 입력 이벤트 설정
        inputStart_editText.addTextChangedListener(new TextWatcher() {
            String input_locationPOI;

            //입력하기 전 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            //입력할 때  이벤트
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == null) return;

                if(!display_listView){
                    startLocation_finish = false;
                    linearLayoutTmap.setVisibility(View.INVISIBLE);
                    searchResult_listView.setVisibility(View.VISIBLE);
                    display_listView = true;
                }
                //ListView 내용 초기화
                list_data.clear();

                if(s.toString().length() < 2){ //2개 미만은 검색 X
                    adapter.notifyDataSetChanged();
                    return;
                }

                input_locationPOI = s.toString();

                tMapData.findAllPOI(input_locationPOI, 7, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        if(!arrayList.isEmpty()){
                            for(int i = 0; i < arrayList.size(); i++) {
                                TMapPOIItem item = arrayList.get(i);
                                list_data.add(item.getPOIName());

                            }
                            //새로운 Thread 생성
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }).start();

                        }
                    }

                });
            }

            //입력 후 이벤트
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputEnd_editText.addTextChangedListener(new TextWatcher() {
            String input_locationPOI;

            //입력하기 전 이벤트
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //입력할 때  이벤트
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == null) return;

                //ListView 내용 초기화
                list_data.clear();
                if(!display_listView){
                    startLocation_finish = true;
                    linearLayoutTmap.setVisibility(View.INVISIBLE);
                    searchResult_listView.setVisibility(View.VISIBLE);
                    display_listView = true;
                }




                if(s.toString().length() < 2){ //2개 미만은 검색 X
                    adapter.notifyDataSetChanged();
                    return;
                }


                input_locationPOI = s.toString();

                tMapData.findAllPOI(input_locationPOI, 7, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        if(!arrayList.isEmpty()){
                            for(int i = 0; i < arrayList.size(); i++) {
                                TMapPOIItem item = arrayList.get(i);
                                list_data.add(item.getPOIName());

                            }
                            //새로운 Thread 생성
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }

                });


            }

            //입력 후 이벤트
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //검색창 클릭 이벤트 설정
        searchResult_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            int index = 0;
            Marker marker;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tMapData.findAllPOI(list_data.get(position), 1, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        TMapPOIItem item;
                        item = arrayList.get(0);
                        marker = new Marker(item.getPOIName(), item.getPOIAddress(), "None",item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());


                        if (!startLocation_finish) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    inputStart_editText.setText(marker.getName());
                                }
                            });
                            startMarker = marker;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    inputEnd_editText.setText(marker.getName());
                                }
                            });
                            endMarker = marker;
                        }


                        //카메라 이동
                        tMapView.setCenterPoint(marker.getLongitude(),marker.getLatitude());

                        //Point 좌표 설정
                        tMapPoint.setLongitude(marker.getLongitude());
                        tMapPoint.setLatitude(marker.getLatitude());

                        TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();


                        tMapMarkerItem.setIcon(bitmap); // 마커 아이콘 지정
                        tMapMarkerItem.setCanShowCallout(true);
                        tMapMarkerItem.setAutoCalloutVisible(true);
                        tMapMarkerItem.setTMapPoint(tMapPoint); //마커 좌표 설정

                        //마커 Title & SubTitle 지정
                        tMapMarkerItem.setCalloutTitle(marker.getName());
                        tMapMarkerItem.setCalloutSubTitle(marker.getAddress().trim());

                        if(!startLocation_finish){
                            startMarker.setMarker_id("markerItem_1");
                            tMapView.addMarkerItem(startMarker.getMarker_id(), tMapMarkerItem); // 지도에 마커 추가
                        }
                        else{
                            endMarker.setMarker_id("markerItem_2");
                            tMapView.addMarkerItem(endMarker.getMarker_id(), tMapMarkerItem); // 지도에 마커 추가
                        }


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        // 해당 작업을 처리함
                                        linearLayoutTmap.setVisibility(View.VISIBLE);
                                        searchResult_listView.setVisibility(View.INVISIBLE);
                                        display_listView = false;
                                    }
                                });
                            }
                        }).start();


                    }
                });



            }

        });

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //endMaker & startMarker 정보 swap
                Marker marker_instance = startMarker;
                startMarker = endMarker;
                endMarker = marker_instance;

                //editText text 내용 swap
                inputStart_editText.setText(startMarker.getName());
                inputEnd_editText.setText(endMarker.getName());

                //listView 안 보이게 설정
                startLocation_finish = true;
                linearLayoutTmap.setVisibility(View.VISIBLE);
                searchResult_listView.setVisibility(View.INVISIBLE);
                display_listView = false;


            }
        });

        findPath_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //카메라 중심정 이동
                TMapPoint tMapPoint_instance = new TMapPoint((startMarker.getLatitude() + endMarker.getLatitude())/2 ,
                        (startMarker.getLongitude() + endMarker.getLongitude())/2);
                tMapView.setCenterPoint(tMapPoint_instance.getLongitude(),tMapPoint_instance.getLatitude(),true);
                tMapView.setZoomLevel(14);


                //경로 안내
                switch (findPath_case){
                    case 0:     //자동차
                        FindPath_Car();
                        break;

                }
            }
        });

        ///현위치 표시 버튼 클릭 이벤트

        showCurPosition_btn.setOnClickListener(v -> {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                }
                return;
            }

            tMapGpsManager.OpenGps();

            TMapPoint curPosition = tMapGpsManager.getLocation();

            if (curPosition != null) {
                tMapView.setCenterPoint(curPosition.getLongitude(), curPosition.getLatitude(), true);
                tMapView.setZoomLevel(17);
                TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
                tMapMarkerItem.setIcon(bitmap2);
                tMapMarkerItem.setCanShowCallout(true);
                tMapMarkerItem.setAutoCalloutVisible(true);
                tMapMarkerItem.setTMapPoint(curPosition);
                tMapMarkerItem.setCalloutTitle("현위치");

                tMapView.addMarkerItem("marker_curPosition", tMapMarkerItem);
                tMapView.setCenterPoint(curPosition.getLongitude(), curPosition.getLatitude(), false);

                System.out.println(curPosition.getLatitude() + " " + curPosition.getLongitude());
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


    // 경로 찾는 함수

    public void FindPath_Car(){
        TMapPoint startpoint = new TMapPoint(startMarker.getLatitude(),startMarker.getLongitude());
        TMapPoint endpoint = new TMapPoint(endMarker.getLatitude(),endMarker.getLongitude());


        tMapData.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tMapView.addTMapPath(tMapPolyLine);
            }
        });

        tMapData.findPathDataAllType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        root = document.getDocumentElement();
                    }
                }
        );
    }




}