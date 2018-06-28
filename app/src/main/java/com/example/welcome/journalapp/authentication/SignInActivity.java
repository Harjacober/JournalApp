package com.example.welcome.journalapp.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.welcome.journalapp.MainActivity;
import com.example.welcome.journalapp.R;
import com.example.welcome.journalapp.utils.NetworkUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends Activity {
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        Button retry = findViewById(R.id.retry);
        RelativeLayout rel = findViewById(R.id.relative_layo);

        if (isUSerLoggedIn()){
            launchCorrespondingActivity();
            finish();
        }else {
            if (NetworkUtils.isConnected(this)) {
                launchFirebaseUi();
            }else{
                setContentView(R.layout.activity_sign_in);
            }
        }
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isConnected(SignInActivity.this)){
                    launchFirebaseUi();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "login success", Toast.LENGTH_SHORT).show();
                launchCorrespondingActivity();
            }
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "login failed", Toast.LENGTH_SHORT).show();
                launchFirebaseUi();
            }
        }else{
            Toast.makeText(this, "unknown error", Toast.LENGTH_SHORT).show();
            launchFirebaseUi();
        }
    }
    private void launchCorrespondingActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isUSerLoggedIn(){
        if (mAuth.getCurrentUser() != null){
            return true;
        }
        return false;
    }

    public  void launchFirebaseUi(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers;
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosUrl("")
                .setIsSmartLockEnabled(true)
                .build(), RC_SIGN_IN);
    }
}
