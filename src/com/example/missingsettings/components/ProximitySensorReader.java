package com.example.missingsettings.components;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ProximitySensorReader 
{
	private float read_value;
	private SensorManager sensor_manager;
	private Sensor proximity_sensor;
    private SensorEventListener sensor_listener;
    Context context;
    Action onSensorChange;
	
    public ProximitySensorReader(float initial)
    {
    	this(initial,null);
    }
    public ProximitySensorReader(float initial,Action on_sensor_change)
    {
    	read_value = initial;
    	onSensorChange = on_sensor_change;
    }
	public void startReading(Context ctx)
	{
		if (sensor_listener!=null)
			return;
		
		context = ctx;
		
		sensor_manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        proximity_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensor_listener = new SensorEventListener()
        {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }

            @Override
            public void onSensorChanged(SensorEvent event) 
            {
            	synchronized (ProximitySensorReader.this)
            	{
            		read_value = event.values[0];
            	}
            	
            	if (onSensorChange!=null)
            		onSensorChange.apply();
            }

         };
	
         sensor_manager.registerListener(sensor_listener,proximity_sensor,SensorManager.SENSOR_DELAY_FASTEST);
	}
	public void stopReading()
	{
		if (sensor_listener!=null)
		{
			sensor_manager.unregisterListener(sensor_listener,proximity_sensor);
			sensor_listener = null;
		}
	}
	public synchronized float getLastRead()
	{
		return read_value;
	}
}
