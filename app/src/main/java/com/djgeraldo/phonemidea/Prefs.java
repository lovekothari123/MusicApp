package com.djgeraldo.phonemidea;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jaimin patel / jaimin2305@gmail.com on 10/16/2015.
 */
public class Prefs {

    //Single Instance object
    private static Prefs instance = null;

    private SharedPreferences sharedPreference = null;

    //Single Instance get
    public static Prefs getPrefInstance() {
        if (instance == null)
            instance = new Prefs();

        return instance;
    }

    @SuppressWarnings("static-access")
    private void openPrefs(Context context) {
        sharedPreference = context.getSharedPreferences(Const.PREFS_FILENAME,
                context.MODE_PRIVATE);
    }

    public void setValue(Context context, String key, String value) {

        openPrefs(context);

        SharedPreferences.Editor prefsEdit = sharedPreference.edit();

        prefsEdit.putString(key, value);
        prefsEdit.commit();

        prefsEdit = null;
        sharedPreference = null;
    }

    public String getValue(Context context, String key, String value) {

        openPrefs(context);

        String result = sharedPreference.getString(key, value);

        sharedPreference = null;

        return result;
    }

    public void remove(Context context, String key) {
        openPrefs(context);

        SharedPreferences.Editor prefsEditor = sharedPreference.edit();

        prefsEditor.remove(key).commit();

        prefsEditor = null;

        sharedPreference = null;
    }
}
