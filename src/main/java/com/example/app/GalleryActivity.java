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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private DatabaseReference root;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth auth; //파이어베이스 인증
    private FirebaseDatabase database;

    int imagenumber;
    int updated_imagenumber;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser(); //로그인한 유저

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        root = database.getReference("Application");

        Intent place = getIntent();
        String get_place = place.getStringExtra("place");

        //컴포넌트 객체에 담기
        Button uploadBtn = findViewById(R.id.upload_btn);
        progressBar = findViewById(R.id.progress_View);
        imageView = findViewById(R.id.image_view);

        //프로그래스바 숨기기
        progressBar.setVisibility(View.INVISIBLE);

        //이미지 클릭 이벤트
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        //업로드 버튼 클릭 이벤트
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //선택한 이미지가 있다면
                if (imageUri != null) {
                    //이미지 번호 가져오기
                    root.child("Gallery").child(firebaseUser.getUid()).child(get_place).child("ImageNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                long intvalue = (long) snapshot.getValue();
                                imagenumber = (int) intvalue;
                                updated_imagenumber = imagenumber+1;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //여기부터
                    StorageReference fileRef = reference.child("Gallery").child(firebaseUser.getUid()).child(get_place).child("Image"+updated_imagenumber + "." + getFileExtension(imageUri));
                    fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //키로 아이디 생성
                                    String modelId = root.push().getKey();

                                    //이미지 번호 가져오기
                                    root.child("Gallery").child(firebaseUser.getUid()).child(get_place).child("ImageNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                long intvalue = (long) snapshot.getValue();
                                                imagenumber = (int) intvalue;
                                                updated_imagenumber = imagenumber+1;
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    root.child("Gallery").child(firebaseUser.getUid()).child(get_place).child("ImageNumber").setValue(updated_imagenumber);

                                    //이미지 모델에 담기
                                    Model model = new Model();
                                    model.setImageUri(uri.toString());
                                    model.setImageNumber(updated_imagenumber);

                                    //데이터 넣기
                                    root.child("Gallery").child(firebaseUser.getUid()).child(get_place).child("Image").child(modelId).setValue(model);

                                    //프로그래스바 숨김
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(GalleryActivity.this, "업로드를 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                    imageView.setImageResource(R.drawable.ic_add_photo);
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            //프로그래스바 보여주기
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //프로그래스바 숨김
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(GalleryActivity.this,"업로드를 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    //여기까지
                } else {
                    Toast.makeText(GalleryActivity.this,"사진을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //갤러리에서 사진 가져오기
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
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