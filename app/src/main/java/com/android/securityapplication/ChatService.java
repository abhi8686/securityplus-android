package com.android.securityapplication;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.securityapplication.helpers.DatabaseHelper;
import com.android.securityapplication.helpers.Http;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by rashmiagarwal on 15/04/17.
 */
public class ChatService extends Service {
    SharedPreferences prefs;
    HashMap<String, String> headers = new HashMap<>();
    PrivateKey privateKey;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        headers.put("Token", prefs.getString("key", null));
        Http stringRequest = new Http(Request.Method.GET, "http://192.168.1.102:3000/" + "api/user/messages/check",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject payload = new JSONObject(response);
                            JSONArray messages = payload.getJSONArray("messages");
                            Log.e("messages===== ", Integer.toString(messages.length()));
                            for(int i= 0; i < messages.length(); i++) {
                                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                                JSONObject message = messages.getJSONObject(i);
                                Log.e("====", message.toString());
//                                byte[] privateBytes = Base64.decode(db.getPrivateKey(), Base64.NO_WRAP);
                                Log.e("===Decrypting====", message.getString("recevier_id"));
                                byte[] privateBytes = Base64.decode(db.getPrivateKey(message.getString("sender_id")), Base64.NO_WRAP);
                                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
                                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                                privateKey = keyFactory.generatePrivate(keySpec);
                                byte[] content = stringToBytes(message.getString("content"));
                                String decrypted_message = RSADecrypt(content);
                                Log.e("======", decrypted_message);
                                db.createMessages(message.getString("sender_id"),decrypted_message,message.getString("sender_id"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, headers, null);
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

        return 1;
    }

    public  byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }


    public String RSADecrypt(final byte[] encryptedBytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher1.doFinal(encryptedBytes);
        String decrypted = new String(decryptedBytes);
        System.out.println("DDecrypted?????" + decrypted);
        return decrypted;
    }
}
