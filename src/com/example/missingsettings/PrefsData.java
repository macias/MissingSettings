package com.example.missingsettings;

//TODO: new feature -- shutter sound on lock (stock feature is buggy, at least at Galaxy Ace 2 Android 2.3)

public class PrefsData 
{
	public Boolean selective_rotation_enabled = null;
	public Boolean proximity_lock_enabled = null;
	
	public static PrefsData create()
	{
		return new PrefsData();
	}
	public PrefsData enableSelectiveRotation(boolean val)
	{
		selective_rotation_enabled = val;
		return this;
	}
	public PrefsData enableProximityLock(boolean val)
	{
		proximity_lock_enabled = val;
		return this;
	}
}
