package com.lznb.sidenavigation.smth_client;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-5-19
 * Time: 上午11:03
 * To change this template use File | Settings | File Templates.
 */
public class webViewActivity extends Activity {

    private AdView adView;
    public static final String EXTRA_WEBURL = "com.devspark.sidenavigation.yeeyanAndroid.extra.weburl";

    public ProgressBar progressBar = null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        // 创建 adView
        adView = new AdView(this, AdSize.SMART_BANNER, "a15195f21aafd4d");

        // 查找 LinearLayout，假设其已获得
        // 属性 android:id="@+id/mainLayout"
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.myRelateLayout);

        // 在其中添加 adView
        layout.addView(adView);

        // 启动一般性请求并在其中加载广告
        adView.loadAd(new AdRequest());



        progressBar = (ProgressBar)findViewById(R.id.webview_progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                .detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        if (getIntent().hasExtra(EXTRA_WEBURL)) {
            String title = getIntent().getStringExtra(EXTRA_WEBURL);
            //setTitle(title);
            WebView uiwebview = (WebView)findViewById(R.id.webView);
            uiwebview.setWebViewClient(new Callback());
            uiwebview.getSettings().setBuiltInZoomControls(true);
            uiwebview.getSettings().setJavaScriptEnabled(true);
            uiwebview.getSettings().setUseWideViewPort(true);
            uiwebview.getSettings().setLoadWithOverviewMode(true);



            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int mDensity = metrics.densityDpi;
            if (mDensity == 240) {
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            } else if (mDensity == 160) {
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
            } else if(mDensity == 120) {
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
            }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            }else if (mDensity == DisplayMetrics.DENSITY_TV){
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            }

            uiwebview.loadUrl(title);

            //uiwebview.getUrl();
            Log.v("huangzf", "the url is "+ title);
        }

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){
//            view.loadUrl(url);
            return false;
        }
        @Override
        public void onPageStarted(android.webkit.WebView view, java.lang.String url, android.graphics.Bitmap favicon){

            progressBar.setVisibility(View.VISIBLE);
            Log.d("CuzyAdSDK","started " + url);
        }
        @Override
        public void onPageFinished(android.webkit.WebView view, java.lang.String url) {
            progressBar.setVisibility(View.INVISIBLE);
            Log.d("CuzyAdSDK","finished " + url);
        }
        @Override
        public void onReceivedError(android.webkit.WebView view, int errorCode, java.lang.String description, java.lang.String failingUrl){
            progressBar.setVisibility(View.INVISIBLE);
            Log.d("CuzyAdSDK","error " + failingUrl + " " + description);
        }
//        @Override
//        public void onLoadResource(android.webkit.WebView view, java.lang.String url) {
//
//        }
//        @Override
//        public android.webkit.WebResourceResponse shouldInterceptRequest(android.webkit.WebView view, java.lang.String url) {
//
//        }
    }

    @Override
    public void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

}