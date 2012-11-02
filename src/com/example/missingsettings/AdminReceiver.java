package com.example.missingsettings;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

public class AdminReceiver extends DeviceAdminReceiver 
{
	static ComponentName admin_component_name = new ComponentName(AdminReceiver.class.getPackage().getName(),AdminReceiver.class.getCanonicalName());
	
	public static ComponentName getAdminComponentName()
	{
		return admin_component_name;
	}

	public static boolean isAdminAcquired(Context context)
	{
		DevicePolicyManager dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		return dpm.isAdminActive(admin_component_name);
	}
	
}
