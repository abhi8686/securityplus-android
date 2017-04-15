package com.android.securityapplication;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
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
import android.widget.Toast;

import com.android.securityapplication.helpers.Http;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import static android.view.View.GONE;

public class SignInActivity extends AppCompatActivity {
    HashMap<String, String> params;
    SharedPreferences preferences;
    String fbToken = "";
    JSONObject payload, sessioninfo;
    ProgressDialog progressDialog;
//    IabHelper mHelper;
//    String TAG = "GOOGLE_BILLING_MAIN";
//    Boolean mIsPremium;


//    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
//        {
//            if (result.isFailure()) {
//                // handle error
//                return;
//            }
//
//            String applePrice =
//                    inventory.getSkuDetails("agent").getPrice();
//
//            // update the UI
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TextView customView = (TextView)
                LayoutInflater.from(this).inflate(R.layout.actionbar_title,
                        null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER );

        customView.setText("Sign In");
//        customView.setTypeface(Typeface.createFromAsset(customView.getContext().getAssets(),"fonts/MavenPro-Regular.ttf"));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, layoutParams);

        setContentView(R.layout.activity_sign_in);
        params = new HashMap<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final EditText email_edittext = (EditText)findViewById(R.id.user_text);
        progressDialog = new ProgressDialog(this);



//        Pusher pusher = new Pusher("0896f51c5850ed48816c");
//        pusher.connect();
//        Channel channel = pusher.subscribe("public-A"+preferences.getString("id",null)+"_channel");
//        channel.bind("subscription_inactive", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, String data) {
//
//                Log.e("PUSHER-WEBSOCKET", data);
//                try {
//                    final String message = new JSONObject(data).getString("message");
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            LayoutInflater layoutInflater
//                                    = (LayoutInflater) getBaseContext()
//                                    .getSystemService(LAYOUT_INFLATER_SERVICE);
//                            View popupView = layoutInflater.inflate(R.layout.popup_warning, null);
//                            final Dialog popUpDialog = new Dialog(SignInActivity.this);
////                        ((LinearLayout) popupView.findViewById(R.id.layout)).setBackgroundColor(getResources().getColor(R.color.green));
////                        ((Button) popupView.findViewById(R.id.ok)).setBackgroundColor(getResources().getColor(R.color.green2));
//                            ((LinearLayout) popupView.findViewById(R.id.layout)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                            popUpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            popUpDialog.addContentView(popupView, popupView.getLayoutParams());
//                            WindowManager.LayoutParams wmlp = popUpDialog.getWindow().getAttributes();
//
//                            wmlp.gravity = Gravity.TOP | Gravity.LEFT;
//                            wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                            wmlp.x = 100;   //x position
//                            //y position
//
//
//                            popUpDialog.show();
//
//                            ((TextView) popupView.findViewById(R.id.title)).setText("OH SNAP!");
//                            ((TextView) popupView.findViewById(R.id.text)).setText(message);
//
//                            popupView.findViewById(R.id.cancel).setVisibility(GONE);
//                            Button btnDismiss = (Button) popupView.findViewById(R.id.ok);
//                            btnDismiss.setOnClickListener(new Button.OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//                                    // TODO Auto-generated method stub
//
////                    popupWindow.dismiss();
//                                    popUpDialog.dismiss();
//                                }
//                            });
////                        Intent intent = new Intent(SignInActivity.this, PaymentActivity.class);
////                        startActivity(intent);
//
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
////
//        ((Button)findViewById(R.id.cancel_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
//                startActivity(intent);
//            }
//        });
//        email_edittext.addTextChangedListener(new TextValidator(email_edittext) {
//            @Override
//            public void validate(TextView textView, String text) {
//                if (!isValidEmail(text)) {
//                    Drawable dr = getResources().getDrawable(widgetlibrary.companymapp.com.widgetlibrary.R.mipmap.ic_text_validation_cross_pink);
//                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
//                    email_edittext.setError(null, dr);
//                    email_edittext.setBackgroundResource(widgetlibrary.companymapp.com.widgetlibrary.R.drawable.edit_txt_border_inv_red);
//                }
//            }
//        });
//
//        final CompanyMappTextEditInverse pswd_edittext = (CompanyMappTextEditInverse)findViewById(R.id.psd_txt);
////        pswd_edittext.setText(preferences.getString("password"), null);
//        pswd_edittext.addTextChangedListener(new TextValidator(pswd_edittext) {
//            @Override
//            public void validate(TextView textView, String text) {
//                if(!isValidPassword(text)){
//                    Drawable dr = getResources().getDrawable(widgetlibrary.companymapp.com.widgetlibrary.R.mipmap.ic_text_validation_cross_pink);
//                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
//                    pswd_edittext.setError(null, dr);
//                    pswd_edittext.setBackgroundResource(widgetlibrary.companymapp.com.widgetlibrary.R.drawable.edit_txt_border_inv_red);
//                }
//            }
//        });
    }


    public void signin(View view){
        final EditText email_edittext = (EditText)findViewById(R.id.user_text);
        final EditText pswd_edittext = (EditText)findViewById(R.id.psd_txt);

        final String pswd = pswd_edittext.getText().toString();


        final String email = email_edittext.getText().toString();


            params.put("email",email);
            params.put("password",pswd);
            Http stringRequest = new Http(Request.Method.POST, "http://192.168.1.102:3000/" + "api/user/login",
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                Toast.makeText(getApplicationContext(),"Logged in successfully",Toast.LENGTH_LONG).show();
                                LayoutInflater layoutInflater
                                        = (LayoutInflater) getBaseContext()
                                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                                View popupView = layoutInflater.inflate(R.layout.popup_warning, null);
                                final Dialog popUpDialog = new Dialog(SignInActivity.this);
                                ((LinearLayout) popupView.findViewById(R.id.layout)).setBackgroundColor(getResources().getColor(R.color.green));
                                ((Button) popupView.findViewById(R.id.ok)).setBackgroundColor(getResources().getColor(R.color.green2));
                                ((LinearLayout) popupView.findViewById(R.id.layout)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                popUpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                popUpDialog.addContentView(popupView,popupView.getLayoutParams());
                                WindowManager.LayoutParams wmlp = popUpDialog.getWindow().getAttributes();

                                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                                wmlp.width= WindowManager.LayoutParams.MATCH_PARENT;
                                wmlp.x = 100;   //x position
                                //y position


                                popUpDialog.show();

                                ((TextView) popupView.findViewById(R.id.title)).setText("WELL DONE!");
                                ((TextView)popupView.findViewById(R.id.text)).setText("Login Successful!");
                                popupView.findViewById(R.id.cancel).setVisibility(GONE);
                                Button btnDismiss = (Button) popupView.findViewById(R.id.ok);
                                btnDismiss.setText("Awesome!");
                                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub

//                    popupWindow.dismiss();
                                        popUpDialog.dismiss();
                                    }
                                });

                                JSONObject sessioninfo = new JSONObject(response);
                                String payload = sessioninfo.getString("key");
                                Log.e("user info", sessioninfo.toString());
//                                JSONObject userdetails = sessioninfo.getJSONObject("agent_details");
                                preferences.edit().putString("key", payload).apply();
//                                preferences.edit().putString("email", userdetails.getString("email")).apply();
//                                preferences.edit().putString("phone", userdetails.getString("phone_number")).apply();
//                                preferences.edit().putString("username", userdetails.getString("user_name")).apply();
//                                preferences.edit().putString("id", payload.getString("id")).apply();
//                                preferences.edit().putBoolean("active", sessioninfo.getBoolean("is_active_iap")).apply();

                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                finishAffinity();
                                startActivity(intent);


                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LayoutInflater layoutInflater
                                    = (LayoutInflater) getBaseContext()
                                    .getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupView = layoutInflater.inflate(R.layout.popup_warning, null);
                            final Dialog popUpDialog = new Dialog(SignInActivity.this);
                            //((LinearLayout) popupView.findViewById(R.id.layout)).setBackgroundColor(getResources().getColor(R.color.green));
                            //((Button) popupView.findViewById(R.id.ok)).setBackgroundColor(getResources().getColor(R.color.green2));
                            ((LinearLayout) popupView.findViewById(R.id.layout)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            popUpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            popUpDialog.addContentView(popupView,popupView.getLayoutParams());
                            WindowManager.LayoutParams wmlp = popUpDialog.getWindow().getAttributes();
//
                            wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                            wmlp.width= WindowManager.LayoutParams.MATCH_PARENT;
                            wmlp.x = 100;   //x position
                            //y position


                            popUpDialog.show();

                            ((TextView) popupView.findViewById(R.id.title)).setText("OH SNAP!");
                            ((TextView)popupView.findViewById(R.id.text)).setText("Looks like you missed something!");
                            popupView.findViewById(R.id.cancel).setVisibility(GONE);
                            Button btnDismiss = (Button) popupView.findViewById(R.id.ok);
                            btnDismiss.setOnClickListener(new Button.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub

//                    popupWindow.dismiss();
                                    popUpDialog.dismiss();
                                }
                            });
                            Toast.makeText(getApplicationContext(),"Email or Password incorrect",Toast.LENGTH_LONG).show();
                        }
                    }, null, params);

            Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        }
}
