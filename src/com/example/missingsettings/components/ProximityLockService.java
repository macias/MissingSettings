package com.example.missingsettings.components;

import com.example.missingsettings.ReceiverService;

public class ProximityLockService extends ReceiverService<ProximityLockReceiver> 
{ 
	public ProximityLockService() 
	{
		super(ProximityLockReceiver.class);
	}
	
    @Override
    public void onCreate() 
    {
		super.onCreate();
		ProximityLockReceiver.proximityReader.startReading(this);
    }
    
    @Override 
    public void onDestroy()
    {
    	ProximityLockReceiver.proximityReader.stopReading();
    	super.onDestroy();
    }

}
