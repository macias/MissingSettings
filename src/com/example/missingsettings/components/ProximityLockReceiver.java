package com.example.missingsettings.components;

import java.util.Arrays;
import java.util.List;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.missingsettings.ReceiverEvents;

public class ProximityLockReceiver extends BroadcastReceiver implements ReceiverEvents
{
	// this is ugly -- exposing sensor to receiver directly -- if you know anything better, let me know 
	static ProximitySensorReader proximityReader = new ProximitySensorReader(-1, // we can assume the sensor is not obscured
			// on sensor change
			new Action(){

		@Override
		public void apply() {
			conditionalLock();
		}}); 
	
	// the assumption for "false" is either the receiver was activated on boot (so screen was on) or by activity (on as well)
	private static boolean is_screen_off = false;
	
	@Override
	public List<String> getEvents() 
	{
		return Arrays.asList(Intent.ACTION_SCREEN_ON,Intent.ACTION_SCREEN_OFF);
	}
	
	private static void conditionalLock()
	{
		if (is_screen_off)
			return;
		
		KeyguardManager km = (KeyguardManager) proximityReader.context.getSystemService(Context.KEYGUARD_SERVICE);
    	if (km.inKeyguardRestrictedInputMode() && proximityReader.getLastRead()==0)
        {
            DevicePolicyManager dpm = (DevicePolicyManager)proximityReader.context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            dpm.lockNow();
        }
	}
	
    @Override 
    public void onReceive(Context context, Intent intent) 
	{
	    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
	    {
	    	is_screen_off = true;
	    }
	    else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
	    {
	    	is_screen_off = false;
	    	
	    	conditionalLock();
	    }

	}
}

	


