package com.example.app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class WritePostActivity extends AppCompatActivity {

    EditText et_post_title, et_post_contents;
    Button btn_write;
    ImageButton btn_gallery;
    ImageView iv_post_image;

    private DatabaseReference root;
    private FirebaseAuth auth; //파이어베이스 인증
    private FirebaseDatabase database;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

    int postcount, updated_postcount;
    String postuser;

    private Uri imageUri;

    Spinner spinner;

    String spinner_place,spinner_result;

    EditText mEtAddress;
    String place_data;
    int x=0,y=0;
    int place_cnt,updated_place_cnt;

    String placepictureurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        et_post_title = findViewById(R.id.post_title);
        et_post_contents = findViewById(R.id.post_contents);
        btn_write = findViewById(R.id.post_write);
        btn_gallery = findViewById(R.id.btn_gallery);
        iv_post_image = findViewById(R.id.post_image);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser(); //로그인한 유저

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        root = database.getReference("Application"); //시작 경로

        //위치검색
        mEtAddress = findViewById(R.id.et_address);
        // 터치 제한
        mEtAddress.setFocusable(false);
        mEtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주소 검색 웹뷰 화면으로 이동
                Intent intent = new Intent(WritePostActivity.this,SearchActivity.class);
                getSearchResult.launch(intent);
            }
        });

        //Nickname 가져오기
        root.child("UserAccount").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    postuser = userAccount.getNickname();
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

        //카메라 버튼 클릭했을 때
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
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

                //작성 버튼 클릭했을 때
                btn_write.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //선택한 이미지가 있으면
                        if (imageUri != null && place_data != null){
                            String str_title = et_post_title.getText().toString();
                            String str_contents = et_post_contents.getText().toString();

                            //PostCunt 가져오기
                            root.child("PostCount").child(spinner_place).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        Post post = snapshot.getValue(Post.class);
                                        postcount = post.getPostCount();
                                        updated_postcount = postcount+1;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //PostPlaceCount 가져오기
                            root.child("PostedPlace").child(place_data).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        Post post = snapshot.getValue(Post.class);
                                        place_cnt = post.getPostPlaceCount();
                                        updated_place_cnt = place_cnt+1;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //여기부터
                            StorageReference fileRef = reference.child(firebaseUser.getUid()).child("Post").child(spinner_place).child("Post"+ updated_postcount + "." + getFileExtension(imageUri));
                            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //데이터 담기
                                            Post post = new Post();
                                            post.setPostuser(postuser);
                                            post.setPosttitle(str_title);
                                            post.setPostcontents(str_contents);
                                            post.setPostCount(updated_postcount);
                                            post.setPostimage(uri.toString());
                                            post.setPosttime(getTime);
                                            post.setPostCommentCount(0);
                                            post.setPostStarCount(1);
                                            post.setPostPlace(place_data);
                                            placepictureurl = uri.toString();

                                            //데이터 넣기
                                            root.child("Post").child(spinner_place).child(updated_postcount+"").setValue(post);

                                            //postCount 값 업데이트
                                            HashMap data = new HashMap();
                                            data.put("postCount",updated_postcount);
                                            root.child("PostCount").child(spinner_place).updateChildren(data).addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                }
                                            });

                                            //postPlaceCount 값 업데이트
                                            HashMap hashMap = new HashMap();
                                            hashMap.put("postPlaceCount",updated_place_cnt);
                                            hashMap.put("postPlace",place_data);
                                            hashMap.put("postPlacePictureUrl",placepictureurl);
                                            root.child("PostedPlace").child(place_data).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                }
                                            });

                                            //액티비티 이동
                                            Intent to_MainActivity = new Intent(WritePostActivity.this,PostListViewActivity.class);
                                            startActivity(to_MainActivity);
                                            finish();
                                        }
                                    });
                                }
                            });
                            //여기까지


                        }

                        //선택한 이미지가 없으면
                        else
                        {
                            if(x==0 && y==1) {
                                Toast.makeText(WritePostActivity.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            if(x==1 && y==0) {
                                Toast.makeText(WritePostActivity.this, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            if(x==0 && y==0)
                            {
                                Toast.makeText(WritePostActivity.this, "사진을 선택하고 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //여기까지


    }

    //위치검색 결과
    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //Search Activity로부터 결과 값이 이곳으로 전달됨
                if (result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        String data = result.getData().getStringExtra("data");
                        mEtAddress.setText(data);
                        place_data = data;
                        y=1;
                    }
                }
            }
    );

    //사진 가져오기
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        iv_post_image.setImageURI(imageUri);
                        x=1;
                    }
                }
            });

    //파일타입 가져오기
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}