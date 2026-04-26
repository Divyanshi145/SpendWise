package com.example.expensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "spendwise_session";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_NAME = "name";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveLoginSession(int userId, String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getLoggedInUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getLoggedInUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getLoggedInUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}
