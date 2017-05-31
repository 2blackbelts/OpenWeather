package com.tafe.mcintosh.openweather;
// Leave your package name on line 1

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by McIntosh on 7/05/2017.
 */

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){
        return prefs.getString("city", "Sydney, AU");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
