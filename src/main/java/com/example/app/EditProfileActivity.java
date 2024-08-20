package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    String usernickname, userid, usergrade;

    TextView box_id, box_grade;
    EditText box_nickname;
    Button btn_update;
    ImageView profileImage;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String userimageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Application");
        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        box_id=(TextView)findViewById(R.id.box_id);
        box_nickname=(EditText)findViewById(R.id.box_nickname);
        box_grade=(TextView)findViewById(R.id.box_grade);

        storageReference = FirebaseStorage.getInstance().getReference();

        profileImage = findViewById(R.id.profile_image);

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
                        Intent intent_gallery = new Intent(EditProfileActivity.this,ImageActivity.class);
                        startActivity(intent_gallery);
                        break;
                    case R.id.item_post:
                        Intent intent_post = new Intent(EditProfileActivity.this,PostListViewActivity.class);
                        startActivity(intent_post);
                        break;
                    case R.id.item_profile:

                        drawerLayout.closeDrawer(navigationView); //네비게이션뷰 닫기
                        break;
                    case R.id.item_main:
                        Intent intent_home = new Intent(EditProfileActivity.this,EditProfileActivity.class);
                        startActivity(intent_home);
                        break;
                    case R.id.item_navigation:
                        Intent intent_navigation = new Intent(EditProfileActivity.this,NavigationActivity.class);
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
                                        Intent intent_manager = new Intent(EditProfileActivity.this,ManagerActivity.class);
                                        startActivity(intent_manager);
                                    }
                                    else
                                    {
                                        Toast.makeText(EditProfileActivity.this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;
                    case R.id.item_logout:
                        Toast.makeText(EditProfileActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        Intent intent_logout = new Intent(EditProfileActivity.this,LoginActivity.class);
                        startActivity(intent_logout);
                        finish();
                        break;
                }

                //Drawer를 닫기...
                drawerLayout.closeDrawer(navigationView);

                return false;
            }
        });

        //여기부터 프로필 이미지
        StorageReference profileRef = storageReference.child("userProfiles/"+mFirebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        //유저 닉네임 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    usernickname = userAccount.getNickname();
                    box_nickname.setText(usernickname);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //유저 id 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    userid = userAccount.getEmailId();
                    box_id.setText(userid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //유저 등급 가져오기
        databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    usergrade = userAccount.getGrade();
                    box_grade.setText(usergrade);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_update=(Button)findViewById(R.id.profile_update);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아래부터 데이터 업데이트 코드
                HashMap data = new HashMap();
                data.put("nickname",box_nickname.getText().toString());
                databaseReference = database.getReference("Application");
                databaseReference.child("UserAccount").child(mFirebaseAuth.getUid()).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                        Toast.makeText(EditProfileActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent_main = new Intent(EditProfileActivity.this,MainActivity.class);
                        startActivity(intent_main);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }
    private void uploadImageToFirebase(Uri uri) {
        //파이어베이스 스토리지에 이미지 업로드
        String key = mFirebaseAuth.getUid();
        final StorageReference fileRef = storageReference.child("userProfiles/"+mFirebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String profileUrl = uri.toString();
                        //프로필 이미지 업데이트
                        HashMap data = new HashMap();
                        data.put("imageUrl",profileUrl);
                        //databaseReference.child("UserAccount").child(key).setValue(model);
                        databaseReference.child("UserAccount").child(key).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                            }
                        });
                        Picasso.get().load(uri).into(profileImage);
                        Toast.makeText(getApplicationContext(), "성공하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}