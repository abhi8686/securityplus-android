package com.android.securityapplication.helpers;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class Http extends StringRequest {
    HashMap<String,String> mParams = new HashMap<>();
    HashMap<String,String> mHeaders = new HashMap<>();

    public Http(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, HashMap<String, String> headers, HashMap<String, String> params) {
        super(method, url, listener, errorListener);
        if(headers!=null)
            mHeaders =headers;
        if(params!=null)
            mParams = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

}
