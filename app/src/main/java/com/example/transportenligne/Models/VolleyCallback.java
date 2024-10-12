package com.example.transportenligne.Models;

public interface VolleyCallback {
    void onSuccess(String result);
    void onError(int statusCode,String message);
}