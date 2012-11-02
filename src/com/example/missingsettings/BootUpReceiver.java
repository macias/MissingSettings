package com.example.missingsettings;

import com.example.missingsettings.components.ProximityLockService;
import com.example.missingsettings.components.SelectiveRotationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		PrefsData prefs = Preferences.loadPreferences(context);
		
		if (prefs.selective_rotation_enabled)
		{
			context.startService(new Intent(context, SelectiveRotationService.class));
		}
		if (prefs.proximity_lock_enabled && AdminReceiver.isAdminAcquired(context))
		{
			context.startService(new Intent(context, ProximityLockService.class));
		}
	}
}