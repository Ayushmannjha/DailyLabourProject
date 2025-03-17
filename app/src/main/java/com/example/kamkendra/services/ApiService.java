package com.example.kamkendra.services;

import com.google.gson.Gson;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static ApiService instance;
    private Retrofit retrofit;

    // Private constructor to prevent instantiation
    private ApiService() {
        initializeRetrofit();
    }

    // Method to initialize Retrofit

    private void initializeRetrofit() {
        try {

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.102.130:8070")
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
    }

    // Public method to get the instance of ApiService
    public static synchronized ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    // Public method to get the Retrofit instance
    public Retrofit getRetrofit() {
        if (retrofit == null) {
            initializeRetrofit(); // Ensure Retrofit is initialized
        }
        return retrofit;
    }
}
