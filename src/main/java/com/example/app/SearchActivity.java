package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new BridgeInterface(),"Android");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Android -> Javascript 호출
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });
        //최초 웹뷰 로드
        webView.loadUrl("https://galleryapp-17111.web.app");
    }

    private class BridgeInterface {
        @JavascriptInterface
        public void processDATA(String data){
            // 카카오 주소 검색 API 결과 값이 브릿지 통로르 통해 전달 받음
            Intent intent = new Intent();
            intent.putExtra("data",data);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}