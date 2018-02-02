package com.example.repair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.repair.app.AppController;
import com.example.repair.pojo.RepairItem;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Welcome extends AppCompatActivity {


    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private String userType;
    private String shopID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        sharedPreferences = getApplicationContext().getSharedPreferences("REPAIRAPP",0);
        editor = sharedPreferences.edit();


        userType = sharedPreferences.getString("USER_TYPE", null);
        shopID = sharedPreferences.getString("SHOP_ID", null);

        if(hasNewtworkConnection()) {
            login();
        } else {
            setContentView(R.layout.no_internet);
        }

    }





    private void getUserType() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_style);




        String URL = "https://repair-c8047.firebaseio.com/SELLER.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener <JSONObject> () {

            @Override
            public void onResponse(JSONObject response) {

                Iterator<String> keys = response.keys();
                String number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                boolean flag = false;

                while(keys.hasNext()) {
                    String seller_number = keys.next();
                    if(number.equals("+91"+seller_number)) {
                        flag = true;
                        number = seller_number;
                        break;
                    }
                }

                if(flag) {
                    try {
                        editor.putString("USER_TYPE","SELLER");
                        editor.putString("SHOP_ID",response.getString(number));
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    editor.putString("USER_TYPE","CUSTOMER");
                    editor.commit();
                }
                progressDialog.dismiss();
                openBasedOnUser();
            }
        },null);

        AppController.getInstance().addToRequestQueue(request);
    }




    public void openBasedOnUser() {
        userType = sharedPreferences.getString("USER_TYPE", null);
        switch(userType) {
            case "SELLER":
                //start shop page
                Toast.makeText(this,userType,Toast.LENGTH_LONG).show();
                break;
            case "CUSTOMER":
                Intent homepage = new Intent(this,HomeScreen.class);
                startActivity(homepage);
                finish();
                break;
        }
    }

    public void login() {
        if(auth.getCurrentUser() != null) {
            openBasedOnUser();
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
                getUserType();
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
