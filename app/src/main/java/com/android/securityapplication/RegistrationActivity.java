package com.android.securityapplication;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.securityapplication.helpers.DatabaseHelper;
import com.android.securityapplication.helpers.Http;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

import static android.view.View.GONE;

public class RegistrationActivity extends AppCompatActivity {
    HashMap<String, String> params = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void register(View view){

        final String email = ((EditText)findViewById(R.id.email_txt)).getText().toString();
        final String usrname = ((EditText)findViewById(R.id.user_txt)).getText().toString();
        final String pass = ((EditText)findViewById(R.id.psd_txt)).getText().toString();


        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(1024);
        KeyPair kp = kpg.genKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        String publicK = Base64.encodeToString(publicKey.getEncoded(), Base64.NO_WRAP);
        String privateK = Base64.encodeToString(privateKey.getEncoded(), Base64.NO_WRAP);
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.createGlobalKeys(publicK, privateK, email);


        params.put("public_key", publicK);
        params.put("email",email);
        params.put("full_name", usrname);
        params.put("password", pass);
        Log.e("params", params.toString());
        Http stringRequest = new Http(Request.Method.PUT, "http://192.168.1.102:3000/" + "api/user/register",
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
//                        progressDialog.dismiss();
                        LayoutInflater layoutInflater
                                = (LayoutInflater) getBaseContext()
                                .getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = layoutInflater.inflate(R.layout.popup_warning, null);
                        final Dialog popUpDialog = new Dialog(RegistrationActivity.this);
                        ((LinearLayout) popupView.findViewById(R.id.layout)).setBackgroundColor(getResources().getColor(R.color.green));
                        ((Button) popupView.findViewById(R.id.ok)).setBackgroundColor(getResources().getColor(R.color.green2));
                        ((TextView)popupView.findViewById(R.id.title)).setText("WELL DONE!");
                        ((TextView)popupView.findViewById(R.id.text)).setText("You are registered Successfully");
                        ((LinearLayout) popupView.findViewById(R.id.layout)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        popUpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        popUpDialog.addContentView(popupView,popupView.getLayoutParams());
                        WindowManager.LayoutParams wmlp = popUpDialog.getWindow().getAttributes();
                        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                        wmlp.width= WindowManager.LayoutParams.MATCH_PARENT;
                        wmlp.x = 100;   //x position
                        popUpDialog.show();

                        ((TextView)popupView.findViewById(R.id.text)).setText("AWESOME");
                        popupView.findViewById(R.id.cancel).setVisibility(GONE);
                        Button btnDismiss = (Button) popupView.findViewById(R.id.ok);
                        btnDismiss.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                popUpDialog.dismiss();
                            }
                        });

                        Intent intent = new Intent(getApplication(), SignInActivity.class);
//                        intent.putExtra("email", email);
//                        intent.putExtra("phone_number", phone);
//                        intent.putExtra("user_name", usrname);
//                        intent.putExtra("password", pass);
//                        intent.putExtra("gender", idx==0 ? "male":"female");
//                        intent.putExtra("birth_date", birthdateEditText.getText().toString());
                        startActivity(intent);
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
                LayoutInflater layoutInflater
                        = (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_warning, null);
                final Dialog popUpDialog = new Dialog(RegistrationActivity.this);
                ((TextView) popupView.findViewById(R.id.title)).setText("OH SNAP!");
                ((LinearLayout) popupView.findViewById(R.id.layout)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                popUpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popUpDialog.addContentView(popupView,popupView.getLayoutParams());
                WindowManager.LayoutParams wmlp = popUpDialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.BOTTOM | Gravity.LEFT;
                wmlp.width= WindowManager.LayoutParams.MATCH_PARENT;
                wmlp.x = 100;   //x position
                popUpDialog.show();

                Log.e("===", error.toString());
                NetworkResponse networkResponse = error.networkResponse;
//                try {
//                    JSONObject errorObj = new JSONObject(new String(networkResponse.data));
//                    Log.e("Error",errorObj.getJSONArray("errors").getString(0));
//                    ((TextView)popupView.findViewById(R.id.text)).setText(errorObj.getJSONArray("errors").getString(0));
//                    popupView.findViewById(R.id.cancel).setVisibility(GONE);
//                    Button btnDismiss = (Button) popupView.findViewById(R.id.ok);
//                    btnDismiss.setOnClickListener(new Button.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            popUpDialog.dismiss();
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        }, null, params);

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }
}
