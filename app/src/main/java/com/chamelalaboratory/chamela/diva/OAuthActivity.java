package com.chamelalaboratory.chamela.diva;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OAuthActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "H43VFY5RFB3UGTRUTUGV4T3TI3GTO43BG";
    private static final String REDIRECT_URI = "chamelalaboratory.com";
    private static final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/auth";
    private static final String TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
    private static final String SCOPE = "chamela@gmail.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        // Initiate OAuth flow
        initiateOAuth();
    }

    private void initiateOAuth() {
        Uri.Builder builder = Uri.parse(AUTHORIZATION_URL).buildUpon();
        builder.appendQueryParameter("client_id", CLIENT_ID);
        builder.appendQueryParameter("redirect_uri", REDIRECT_URI);
        builder.appendQueryParameter("scope", SCOPE);
        builder.appendQueryParameter("response_type", "code");

        Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            handleOAuthResponse(uri);
        }
    }

    private void handleOAuthResponse(Uri uri) {
        String code = uri.getQueryParameter("code");
        if (code != null) {
            Toast.makeText(this, "Authorization code: " + code, Toast.LENGTH_SHORT).show();
        } else {
            String error = uri.getQueryParameter("error");
            if (error != null) {
                Log.e("OAuth", "Authorization error: " + error);
                Toast.makeText(this, "Authorization error: " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
