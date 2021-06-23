package com.app.ybazzarseller.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.activity.LoginActivity;

public class Session {

    public static final String PREFER_NAME = "eCart_Admin_App";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String id, String fcmId, String user, String store_name, String email, String password,String balance, String customer_privacy, String logo) {
        editor.putBoolean(Constant.IS_USER_LOGIN, true);
        editor.putString(Constant.FCM_ID, fcmId);
        editor.putString(Constant.ID, id);
        editor.putString(Constant.NAME, user);
        editor.putString(Constant.STORE_NAME, store_name);
        editor.putString(Constant.EMAIL, email);
        editor.putString(Constant.PASSWORD, password);
        editor.putString(Constant.BALANCE, balance);
        editor.putString(Constant.CUSTOMER_PRIVACY, customer_privacy);
        editor.putString(Constant.LOGO, logo);
        editor.commit();
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public boolean getReadMark(String id) {
        return pref.getBoolean(id, false);
    }

    public void setReadMark(String id, boolean val) {
        editor.putBoolean(id, val);
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(Constant.IS_USER_LOGIN, false);
    }


    public void logoutUserConfirmation(final Activity activity) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
        // Setting Dialog Message
        alertDialog.setTitle(R.string.logout);
        alertDialog.setMessage(R.string.logout_msg);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.ic_logout);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                editor.clear();
                editor.commit();

                Intent i = new Intent(activity, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                activity.finish();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();

    }
}
