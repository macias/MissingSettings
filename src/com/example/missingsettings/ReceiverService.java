package com.example.missingsettings;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ReceiverService<REC extends BroadcastReceiver & ReceiverEvents> extends Service
{
	Class<REC> recClass;

    REC receiver = null;

    private boolean isReceiverOn()
    {
    	return receiver!=null;
    }
    
    public ReceiverService(Class<REC> rec_class)
    {
    	super();
    	this.recClass = rec_class;
    }
    
    @Override
    public void onCreate() {
		super.onCreate();

        if (!isReceiverOn())
        {
        	try
        	{
        		receiver = recClass.newInstance();
        	}
        	catch (IllegalAccessException ex)
        	{
        	}
        	catch (InstantiationException ex)
        	{
        	}
        	
        	if (isReceiverOn())
        	{
        		IntentFilter filter = new IntentFilter();
        		for (String event : receiver.getEvents())
        			filter.addAction(event);
        
        		registerReceiver(receiver, filter);
        	}
        }
    }
    
    @Override 
    public void onDestroy()
    {
    	if (isReceiverOn())
    		unregisterReceiver(receiver);
    	
    	receiver = null;
    	
    	super.onDestroy();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


}
