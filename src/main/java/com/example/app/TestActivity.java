package com.example.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TestActivity extends AppCompatActivity {

    EditText mEtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mEtAddress = findViewById(R.id.et_address);
        // block touch
        mEtAddress.setFocusable(false);
        mEtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주소 검색 웹뷰 화면으로 이동
                Intent intent = new Intent(TestActivity.this,SearchActivity.class);
                getSearchResult.launch(intent);
            }
        });

    }
    
    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //Search Activity로부터 결과 값이 이곳으로 전달됨
                if (result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        String data = result.getData().getStringExtra("data");
                        mEtAddress.setText(data);
                    }
                }
            }
    );
    
}