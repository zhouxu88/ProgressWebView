package com.example.zhouxu.progresswebview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WebView0Activity extends AppCompatActivity {

    private static final String TAG = "WebView0Activity";
    private WebView webView;
    private SlowlyProgressBar slowlyProgressBar;
    private ProgressBar progressBar;
    private TextView errorTv;
    private Context mContext;
    private boolean loadError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view0);


        mContext = this;
        webView = findViewById(R.id.webview1);
        progressBar = findViewById(R.id.ProgressBar);
        errorTv = findViewById(R.id.error_tv);

        String url = "https://test.yewyw.com/index.html?bmark=txjk&appid=88&flag=app&appUserId=xxx&appUserNickname=xxx&appHeadUrl=xxx.png#/Index";
        WebSettingHelper.init(webView);
        initWeb();
        webView.loadUrl(url);//加载url

        errorTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyUtils.isNetworkAvailable(mContext)) {
                    return;
                }
                webView.reload();
                errorTv.setVisibility(View.GONE);
                loadError = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RelativeLayout webParentView = (RelativeLayout) webView.getParent();
        webParentView.removeAllViews();
        webView.removeAllViews();
        webView.destroy();
        webView = null;
        slowlyProgressBar.destroy();
        slowlyProgressBar = null;
    }


    private void initWeb() {
        /** 第二种动画模式，another solution */
        slowlyProgressBar = new SlowlyProgressBar(progressBar);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(TAG, "onPageStarted: ");
                if (!MyUtils.isNetworkAvailable(mContext)) {
                    return;
                }
                slowlyProgressBar.onProgressStart();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished : " + loadError);

                if (loadError != true) {
                    webView.setVisibility(View.VISIBLE);
                }

            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                Log.i(TAG, "onReceivedError: ");
                errorTv.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                loadError = true;
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "shouldOverrideUrlLoading : " + url);
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //如果没有网络直接跳出方法
                if (!MyUtils.isNetworkAvailable(WebView0Activity.this)) {
                    return;
                }
                Log.i(TAG, "onProgressChanged : " + newProgress);
                slowlyProgressBar.onProgressChange(newProgress);
                super.onProgressChanged(view, newProgress);
            }

        });

    }


    //设置返回键动作（防止按返回键直接退出程序)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO 自动生成的方法存根
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {//当webview不是处于第一页面时，返回上一个页面
                webView.goBack();
                return true;
            } else {
                finish();
            }


        }
        return super.onKeyDown(keyCode, event);
    }
}
