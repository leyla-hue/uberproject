package com.example.mynewproject;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    //Session Name
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    //User session variables
    private static final String IS_LOGIN= "isLoggedIn";
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_PASSWORD = "password";

    //Remember me variables
    private static final String IS_REMEMBERME ="IsRememberMe";
    public static final String KEY_SESSIONFULLNAME = "fullname";
    public static final String KEY_SESSIONPASSWORD = "password";

    public SessionManager(Context _context, String sessionName){
        context = _context;
        usersSession = context.getSharedPreferences(sessionName,Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    public void createLoginSession(String fullname, String password){
        editor.putBoolean(IS_LOGIN,true);

        editor.putString(KEY_FULLNAME,fullname);
        editor.putString(KEY_PASSWORD,password);

        editor.apply();

    }

    public void createRememberMeSession(String fullname, String password){
        editor.putBoolean(IS_REMEMBERME,true);

        editor.putString(KEY_SESSIONFULLNAME,fullname);
        editor.putString(KEY_SESSIONPASSWORD,password);

        editor.apply();

    }

    public HashMap<String,String> getRememberMeDetailFromSession(){
        HashMap<String,String> userData = new HashMap<String,String>();
        userData.put(KEY_SESSIONFULLNAME,usersSession.getString(KEY_SESSIONFULLNAME,null));
        userData.put(KEY_SESSIONPASSWORD,usersSession.getString(KEY_SESSIONPASSWORD,null));

        return userData;
    }

    public HashMap<String,String> getUserDetailFromSession(){
        HashMap<String,String> userData = new HashMap<String,String>();
        userData.put(KEY_FULLNAME,usersSession.getString(KEY_FULLNAME,null));
        userData.put(KEY_PASSWORD,usersSession.getString(KEY_PASSWORD,null));

        return userData;
    }

    public Boolean checkLogin(){
        if(usersSession.getBoolean(IS_LOGIN,false)){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean checkRememberMe(){
        if(usersSession.getBoolean(IS_REMEMBERME,false)){
            return true;
        }
        else{
            return false;
        }
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }
}
