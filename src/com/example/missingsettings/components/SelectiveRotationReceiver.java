package com.example.missingsettings.components;

import java.util.Arrays;
import java.util.List;

import com.example.missingsettings.ReceiverEvents;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class SelectiveRotationReceiver extends BroadcastReceiver implements ReceiverEvents
{
	// static, because receiver is stateless
	static Integer before_screen_off = null;
	
	@Override
	public List<String> getEvents() 
	{
		return Arrays.asList(Intent.ACTION_USER_PRESENT,Intent.ACTION_SCREEN_ON,Intent.ACTION_SCREEN_OFF);
	}

    @Override public void onReceive(Context context, Intent intent) 
	{
    	// screen off does not mean the screen is locked as well!
	    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
	    {
	    	try
	    	{
	    		before_screen_off = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
	    	}
	    	catch (Settings.SettingNotFoundException ex)
	    	{
	    		
	    	}
	    	
		    // since the screen is off, no hard feelings -- turn rotation off
		    Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION , 0);
	    }
	    else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
	    {
        	KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        	boolean locked = km.inKeyguardRestrictedInputMode();

        	if (locked)
        		before_screen_off = null;
        	else if (before_screen_off!=null)
        	{
        		// it might be going back from screen-off/lock-off state --> set rotation back as it was
           		Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION , before_screen_off);
        	}
	    }
	    // screen is ON and unlocked
        else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT))
        {
       		Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION , 1);
    		before_screen_off = null;
        }
	}

	

}
