package com.android.securityapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.securityapplication.adapters.ThreadAdapter;
import com.android.securityapplication.helpers.DatabaseHelper;
import com.android.securityapplication.helpers.Http;
import com.android.securityapplication.helpers.Message;

import com.android.securityapplication.helpers.MultiPartRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class ChatActivity extends AppCompatActivity {
    RecyclerView listview;
    Context ctx = this;
    //    ChatAdapter adapter;
    ThreadAdapter adapter;
    HashMap<String, String> params, headers;
    SharedPreferences preferences;
    public final static int ACTIVITY_SELECT_IMAGE = 1046;
    private static final String TEMP_IMAGE_NAME = "tempImage";
    private ArrayList<Message> messages;
    LinearLayoutManager layoutManager;
    public static final int PLACE_PICKER_REQUEST = 1;
    String agent_image = "";
    ProgressDialog progressDialog;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte[] encrypted_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.loader_pink));
        progressDialog.show();


        messages = new ArrayList<>();
        listview = (RecyclerView) findViewById(R.id.chat_list_view);
        listview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        listview.setLayoutManager(layoutManager);
//        Typeface typeface = Typeface.createFromAsset(((Button) findViewById(R.id.button)).getContext().getAssets(), "fonts/MavenPro-Medium.ttf");
//        ((Button) findViewById(R.id.button)).setTypeface(typeface);
        fetch_message();
//        registerPusher();
    }

    public void show_more(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.buttons);
        if (layout.getVisibility() == View.INVISIBLE)
            layout.setVisibility(View.VISIBLE);
        else
            layout.setVisibility(View.INVISIBLE);
    }

    public void openCamera(View view) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhotoIntent, ACTIVITY_SELECT_IMAGE);
    }

    public void pickPhoto(View view){
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent, ACTIVITY_SELECT_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                try {
                    Bitmap bitmap = getImageFromResult(this, resultCode, data);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public Bitmap getImageFromResult(Context context, int resultCode,
                                     Intent imageReturnedIntent) throws InvalidKeySpecException {
        Bitmap bm = null;
        File imageFile = getTempFile(context);
        Uri selectedImage;

        if (resultCode == RESULT_OK) {
            boolean isCamera = (imageReturnedIntent == null ||
                    imageReturnedIntent.getData() == null  ||
                    imageReturnedIntent.getData().toString().contains(imageFile.toString()));
            if (isCamera) {     /** CAMERA **/
//                selectedImage = Uri.fromFile(imageFile);
//                selectedImage= imageReturnedIntent.getData();
                bm = (Bitmap)imageReturnedIntent.getExtras().get("data");
                int nh = (int) (bm.getHeight() * (512.0 / bm.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bm, 512, nh, true);
                progressDialog = new ProgressDialog(ChatActivity.this);
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.loader_pink));
                progressDialog.show();
                send("image", scaled, Uri.fromFile(imageFile));
            } else {            /** ALBUM **/
                selectedImage = imageReturnedIntent.getData();


                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    int nh = (int) (bm.getHeight() * (512.0 / bm.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(bm, 512, nh, true);
                    progressDialog = new ProgressDialog(ChatActivity.this);
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.loader_pink));
                    progressDialog.show();
                    send("image", scaled, selectedImage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
// photo.setImageBitmap(bitmap);

        }
        return bm;
    }

    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    public void send(View view) throws InvalidKeySpecException {
        send("text", null, null);
    }

    public void send( String type, final Bitmap bm, Uri selectedImage) throws InvalidKeySpecException {
        params = new HashMap<>();
        headers = new HashMap<>();

        headers.put("Token",preferences.getString("key", null));
        params.put("message_type", type);

        params.put("user_id", getIntent().getStringExtra("id"));
        if(type.equals("text")) {
            try {
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                db.createMessages(getIntent().getStringExtra("id"), ((TextView) findViewById(R.id.message_box)).getText().toString(), null);
                Log.e("===Encrypting===", getIntent().getStringExtra("id"));
                encrypted_message = RSAEncrypt(((TextView) findViewById(R.id.message_box)).getText().toString(),getIntent().getStringExtra("id") );
                params.put("content", bytesToString(encrypted_message));

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }


        }
        else if(type.equals("image")) {
//            params.put("image", selectedImage.getPath().toString());
            params.put("content", "image");
        }

        Log.e("image", params.toString());

        MultiPartRequest stringRequest = new MultiPartRequest(Request.Method.POST, "http://192.168.1.102:3000/" + "api/user/message/new",
                new Response.Listener<NetworkResponse>(){

                    @Override
                    public void onResponse(NetworkResponse response) {
                        ((EditText)findViewById(R.id.message_box)).setText("");

                        fetch_message_last();
                        if(progressDialog != null)
                            progressDialog.dismiss();
                        findViewById(R.id.buttons).setVisibility(View.GONE);
//                        adapter.notifyDataSetChanged();
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override

            protected Map<String, DataPart> getByteData() throws AuthFailureError {

                HashMap<String, DataPart> data = new HashMap<>();
//                data.put("content", new DataPart("tempImage",encrypted_message));

                return data;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    public byte[] getByteImage(Bitmap bmp){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }


    public  String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public  byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }

    private void fetch_message_last(){
        params = new HashMap<>();
        headers = new HashMap<>();
        listview = (RecyclerView) findViewById(R.id.chat_list_view);
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Cursor cursor = db.getMessages(getIntent().getStringExtra("id"));

//        if(cursor.getCount() == 0){
//            KeyPairGenerator kpg = null;
//            try {
//                kpg = KeyPairGenerator.getInstance("RSA");
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//            kpg.initialize(1024);
//            KeyPair kp = kpg.genKeyPair();
//            PublicKey publicKey = kp.getPublic();
//            PrivateKey privateKey = kp.getPrivate();
//            String public_key = db.getPublicKey(getIntent().getStringExtra("id"));
//            if(public_key.equals("null")){
//
//            }
//        }
        if(cursor.moveToFirst()){
            Log.e("====", cursor.getString(0) + cursor.getString(1) + cursor.getString(2));
            do{
                String from = null;
                if(cursor.getString(0) == null){
                    from = "Agent";
                }else{
                    from = "User";
                }
                Message messagObject = new Message(Integer.parseInt(cursor.getString(1)), cursor.getString(2), "", from, "text", null, "Agent");
                messages.add(messagObject);
            }while(cursor.moveToNext());

            adapter = new ThreadAdapter(ChatActivity.this, messages, 0);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }


    private void fetch_message(){
        params = new HashMap<>();
        headers = new HashMap<>();
        listview = (RecyclerView) findViewById(R.id.chat_list_view);
//        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        if(getIntent().getStringExtra("id").equals("null")) {
//            params.put("user_id", );
            Http stringRequest = new Http(Request.Method.POST, "http://192.168.1.102:3000/" + "api/user/user_profile"
                    ,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }, null, null);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        }
        else {
            headers.put("Token", preferences.getString("key", null));
            progressDialog.dismiss();
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            Cursor cursor = db.getMessages(getIntent().getStringExtra("id"));

            if(cursor.moveToFirst()){
                Log.e("====", cursor.getString(0) + cursor.getString(1) + cursor.getString(2));
                do{
                    String from = null;
                    if(cursor.getString(2) == null){
                        from = "User";
                    }else{
                        from = "Agent";
                    }
                    Message messagObject = new Message(Integer.parseInt(cursor.getString(1)), cursor.getString(2), "", from, "text", null, "Agent");
                    messages.add(messagObject);
                }while(cursor.moveToNext());

                adapter = new ThreadAdapter(ChatActivity.this, messages, 0);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

        }
    }

    public byte[] RSAEncrypt(final String plain, String user_id) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.getPublicKey(user_id);
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] publicBytes = Base64.decode(db.getPublicKey(user_id).getBytes(), Base64.NO_WRAP);
        Log.e("====Public key+====", db.getPublicKey(user_id));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] encryptedBytes = cipher.doFinal(plain.getBytes());
//        System.out.println("EEncrypted?????" + org.apache.commons.codec.binary.Hex.encodeHexString(encryptedBytes));
        return encryptedBytes;
    }

}
