package com.example.missingsettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences 
{
	private class Keys
	{
		static final String selective_rotation = "selective_rotation";
		static final String proximity_lock = "proximity_lock";
	}
	
	public static PrefsData loadPreferences(Context context)
	{
	    SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);    	
	    
	    PrefsData data = PrefsData.create()
	    		.enableSelectiveRotation(prefs.getBoolean(Keys.selective_rotation, false))		
	    		.enableProximityLock(prefs.getBoolean(Keys.proximity_lock, false));		
        
        return data;
	}
	
	public static PrefsData savePreferences(Context context,PrefsData data)
	{
	    SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);    	
        SharedPreferences.Editor editor = prefs.edit();

        if (data.selective_rotation_enabled!=null)
        	editor.putBoolean(Keys.selective_rotation, data.selective_rotation_enabled);
        if (data.proximity_lock_enabled!=null)
        	editor.putBoolean(Keys.proximity_lock, data.proximity_lock_enabled);

        editor.commit();
        
        return data;
	}

}
