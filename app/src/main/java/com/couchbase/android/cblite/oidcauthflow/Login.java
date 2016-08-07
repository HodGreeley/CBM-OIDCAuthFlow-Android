package com.couchbase.android.cblite.oidcauthflow;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;

public class Login extends AppCompatActivity {
    private static final String TAG = Login.class.getCanonicalName();

    public static final String LOGIN_URL_KEY = "login";
    public static final String REDIRECT_URL_KEY = "redirect";

    private WebView web;
    private URL loginURL;
    private URL redirectURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        loginURL = (URL) intent.getSerializableExtra(LOGIN_URL_KEY);
        redirectURL = (URL) intent.getSerializableExtra(REDIRECT_URL_KEY);

        setContentView(R.layout.activity_login);

        web = (WebView) findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new LoginWebClient());
        web.loadUrl(loginURL.toString());
    }

    private class LoginWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);

            if (uri.getHost().contentEquals(redirectURL.getHost())) {
                Intent response = new Intent();
                response.setData(uri);
                setResult(RESULT_OK, response);
                finish();

                return(true);  // true => application will handle this
            }

            return(false);  // Webview will load url
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
