package dev.hana.hanatestapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayContentActivity extends AppCompatActivity {
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_content);

        webView();
    }

    private void webView(){
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl("https://3sps.app.link/lqnwoA1xFob");
    }

    @Override public void onBackPressed() {
        if(myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.i("BranchSDK URL", url);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("BranchSDK URL", url);
            if (url.isEmpty()) {
                return false;
            } else {
                if (URLUtil.isNetworkUrl(url)) {
                    return false;
                } else {
                    try {
                        if(url.startsWith("intent")){
                            // fetching the part that starts with http // https
                            int startIndex,endIndex;
                            startIndex=url.indexOf("=")+1;
                            endIndex=url.indexOf("#");
                            url=url.substring(startIndex,endIndex); // this url will open the playStore but wait !

                            // the url we formed still contains some problem at "details?id%3Dcom" bcoz it must be "details?id=com"
                            url=url.replace("%3D","=");
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        return false;
                    }
                }
            }
            finish();
            return true;
        }
    }
}
