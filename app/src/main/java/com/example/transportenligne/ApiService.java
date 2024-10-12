package com.example.transportenligne;
import com.mysql.cj.xdevapi.Client;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("clients/")
    Call<Client> createClient(@Body Client client);
}
