package com.couchbase.android.cblite.oidcauthflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;

import com.couchbase.android.App;
import com.couchbase.android.cblite.Runtime;
import com.couchbase.android.cblite.LocalhostRuntime;

import com.couchbase.lite.Database;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.OIDCLoginCallback;
import com.couchbase.lite.auth.OIDCLoginContinuation;
import com.couchbase.lite.auth.OpenIDConnectAuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final int LOGIN_REQUEST = 1;

    private Replication pull;
    private OIDCLoginContinuation loginContinuation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareToReplicate();

        Button authCodeSignInButton = (Button) findViewById(R.id.authCodeSignInButton);
        authCodeSignInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        pull.stop();

        if (view.getId() == R.id.authCodeSignInButton) {
            pull.start();
        }
        else {
            pull.clearAuthenticationStores();
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }
    }

    private void prepareToReplicate()  {
        Runtime runtime = App.getRuntime();
        Database db = runtime.getDb();

        try {
            pull = db.createPullReplication(new URL(runtime.getSyncUrl()));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        pull.setContinuous(true);

        Authenticator authenticator = OpenIDConnectAuthenticatorFactory
                .createOpenIDConnectAuthenticator(new OIDCLoginCallback() {
            @Override
            public void callback(URL loginURL, URL redirectURL, OIDCLoginContinuation loginContinuation) {
                MainActivity.this.loginContinuation = loginContinuation;

                Intent loginIntent = new Intent(getApplicationContext(), Login.class);

                loginIntent.putExtra(Login.LOGIN_URL_KEY, loginURL);
                loginIntent.putExtra(Login.REDIRECT_URL_KEY, redirectURL);
                startActivityForResult(loginIntent, LOGIN_REQUEST);
            }
        }, new AndroidContext(getApplicationContext()));

        pull.setAuthenticator(authenticator);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LOGIN_REQUEST == requestCode) {
            URL url = null;
            Exception error = null;

            if (RESULT_OK == resultCode) {
                try {
                    String response = data.getData().toString();
                    response = response.replaceFirst(LocalhostRuntime.HOST, LocalhostRuntime.IP);
                    url = new URL(response);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
            else if (RESULT_CANCELED != resultCode) {
                error = new Exception("Login failed.");
            }

            // url = auth redirect => success
            // url = null, ex = error => error
            // url = null, ex = null => canceled
            loginContinuation.callback(url, error);
        }
    }
}
