package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostFullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_full_image);

        PhotoView full_Image = findViewById(R.id.full_image);

        Intent img_uri = getIntent();
        String get_uri = img_uri.getStringExtra("img_uri");

        //Toast.makeText(FullImageActivity.this, "uri:"+get_uri, Toast.LENGTH_SHORT).show();

        Glide.with(getApplicationContext())
                .load(get_uri)
                .into(full_Image);

    }
}