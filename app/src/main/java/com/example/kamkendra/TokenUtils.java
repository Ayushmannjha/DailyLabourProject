package com.example.kamkendra;

import android.util.Log;

import com.auth0.android.jwt.JWT;

public class TokenUtils {
    public static void decodeJWT(String token) {
        try {
            // Create JWT object
            JWT jwt = new JWT(token);

            // Extract claims
            String email = jwt.getClaim("email").asString();
            String role = jwt.getClaim("role").asString();
            String name = jwt.getClaim("name").asString();
            int id = jwt.getClaim("id").asInt();

            // Log or use the claims
            Log.d("TokenUtils", "Email: " + email);
            Log.d("TokenUtils", "Role: " + role);
            Log.d("TokenUtils", "Name: " + name);
            Log.d("TokenUtils", "ID: " + id);

        } catch (Exception e) {
            Log.e("TokenUtils", "Failed to decode JWT: " + e.getMessage(), e);
        }
    }
}
