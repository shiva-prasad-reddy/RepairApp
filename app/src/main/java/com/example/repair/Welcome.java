package com.example.repair;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class Welcome extends AppCompatActivity {


    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if(hasNewtworkConnection()) {
            startNextProcess();
        } else {
            setContentView(R.layout.no_internet);
        }

    }
    private void startNextProcess() {



        login();

/*

        Intent homepage = new Intent(getBaseContext(), HomeScreen.class);
        startActivity(homepage);
        finish();
      /*  Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               // login();
                Intent homepage = new Intent(getBaseContext(),HomeScreen.class);
                startActivity(homepage);
                finish();
            }
        },1L);
        */
    }

    public void login() {
        if(auth.getCurrentUser() != null) {
            Intent homepage = new Intent(this,HomeScreen.class);
            startActivity(homepage);
            finish();
            return;

        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Intent homepage = new Intent(this,HomeScreen.class);
                startActivity(homepage);
                finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    private boolean hasNewtworkConnection() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =  cm.getActiveNetworkInfo();
        connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        return connected;
    }


    private void showSnackbar(int msg) {
        Toast.makeText(this,getString(msg),Toast.LENGTH_LONG).show();
    }
}
