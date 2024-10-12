package com.example.transportenligne.Models;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.transportenligne.JWTUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public static RequestQueue mQueue;

    public static JsonArrayRequest arrayRequest(int method, String path, JSONArray jsonData, Context context, final VolleyCallback callback) {
        JsonArrayRequest stringRequest = new JsonArrayRequest(method, path,jsonData,
                response -> callback.onSuccess(response.toString()),
                error -> callback.onError(error.networkResponse.statusCode,error.getMessage())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + JWTUtils.GetJWT(context));
                return headers;
            };};
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return stringRequest;
    }
    public static JsonObjectRequest objectRequest(int method, String path,JSONObject jsonData,Context context,  final VolleyCallback callback) {
        JsonObjectRequest stringRequest = new JsonObjectRequest(method, path,jsonData,
                response -> callback.onSuccess(response.toString()),
                error -> {
                    String responseBody = null;
                    int statusCode = 0;

                    try {
                        if(error.networkResponse!=null)
                        {
                            statusCode=error.networkResponse.statusCode;
                            responseBody = new String(error.networkResponse.data, "utf-8");
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    callback.onError(statusCode,responseBody);
                })  {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + JWTUtils.GetJWT(context));
                return headers;
            };};
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return stringRequest;
    }
    public static StringRequest stringRequest(int method, String path, JSONObject jsonData, Context context, final VolleyCallback callback) {
        StringRequest stringRequest = new StringRequest(method, path,
                response -> callback.onSuccess(response.toString()),
                error -> {
                    String responseBody = null;
                    int statusCode = 0;

                    try {
                        if(error.networkResponse!=null)
                        {
                            statusCode=error.networkResponse.statusCode;
                            responseBody = new String(error.networkResponse.data, "utf-8");
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    callback.onError(statusCode,responseBody);
                })  {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + JWTUtils.GetJWT(context));
                return headers;
            };};
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return stringRequest;
    }

    public static VolleyMultipartRequest multipartRequest(int method, String path, HashMap<String, String> params, Map<String, VolleyMultipartRequest.DataPart>fileparam,Context context, final VolleyCallback callback) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, path,
                response -> {callback.onSuccess(response.toString());
        }, error -> {
            String responseBody = null;
            int statusCode = 0;

            try {
                if(error.networkResponse!=null)
                {
                    statusCode=error.networkResponse.statusCode;
                    responseBody = new String(error.networkResponse.data, "utf-8");
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            callback.onError(statusCode,responseBody);
        }) {
            @Override
            protected Map<String, String> getParams() {

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + JWTUtils.GetJWT(context));
                return headers;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
               return  fileparam;
            }
        };
        return multipartRequest; }
}
