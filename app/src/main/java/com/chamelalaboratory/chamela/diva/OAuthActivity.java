package com.chamelalaboratory.chamela.diva;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

public class OAuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> {
                    Log.d("GoogleSignIn", "Connection failed");
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signIn();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInAccount account = Auth.GoogleSignInApi.getSignInResultFromIntent(data).getSignInAccount();
            if (account != null) {
                String displayName = account.getDisplayName();
                String email = account.getEmail();
                Toast.makeText(this, "Welcome, " + displayName, Toast.LENGTH_SHORT).show();
            } else {
                Log.e("GoogleSignIn", "Sign in failed");
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
