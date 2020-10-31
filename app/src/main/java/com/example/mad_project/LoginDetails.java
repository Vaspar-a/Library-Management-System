package com.example.mad_project;

import android.util.Log;

public class LoginDetails {
    private static String username, email, profileImage;
    private static boolean librarian;

    public static boolean isLibrarian() {
        return librarian;
    }

    public static String getUsername() {
        return LoginDetails.username;
    }

    public LoginDetails() {
    }

    public static String getEmail() {
        return LoginDetails.email;
    }

    public static String getProfileImage() {
        return LoginDetails.profileImage;
    }

    public LoginDetails(String username, String email, String profileImage) {
        LoginDetails.username = username;
        LoginDetails.email = email;
        LoginDetails.profileImage = profileImage;
        LoginDetails.librarian = email.equals("vaspudie@gmail.com");

        Log.w("SIGN DETAILS", LoginDetails.username + "\n" + LoginDetails.email + "\n" + LoginDetails.profileImage);
    }
}
