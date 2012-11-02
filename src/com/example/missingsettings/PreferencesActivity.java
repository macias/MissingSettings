package com.example.missingsettings;

import com.example.missingsettings.components.ProximityLockService;
import com.example.missingsettings.components.ProximitySensorReader;
import com.example.missingsettings.components.SelectiveRotationService;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;

public class PreferencesActivity extends Activity {

	Intent selective_rotation_service;
	Intent proximity_lock_service;
	ProximitySensorReader reader;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        
		Button button = ((Button)findViewById(R.id.uninstallButton));
		
		// how not to love Android? Easy, straightforward binding right from IDE
		button.setOnLongClickListener(new OnLongClickListener() { 
	        @Override
	        public boolean onLongClick(View v) {
	        	uninstallClicked(v);
	            return true;
	        }
	    });


    	selective_rotation_service = new Intent(this, SelectiveRotationService.class);
   		proximity_lock_service = new Intent(this, ProximityLockService.class);
        
        PrefsData prefs = loadPreferences();
        
        if (prefs.selective_rotation_enabled)
        	startSelectiveRotationService();
        if (prefs.proximity_lock_enabled)
        	startProximityLockService();
        
		SensorManager sensor_manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor proximity_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximity_sensor == null)
        {
        	proximityLockSwitch().setEnabled(false);
        }
        else
        {
        	proximityLockSwitch().setEnabled(true);
        	reader = new ProximitySensorReader(0); // just for "safety", let's assume the sensor is obscured
        	reader.startReading(this);
        }
    }
    
	CheckedTextView selectiveRotationSwitch()
	{
		return (CheckedTextView)findViewById(R.id.selectiveRotationSwitch);
	}
	CheckedTextView proximityLockSwitch()
	{
		return (CheckedTextView)findViewById(R.id.proximityLockSwitch);
	}
    
    
    PrefsData loadPreferences()
    {
    	PrefsData prefs = Preferences.loadPreferences(this);
    	
    	selectiveRotationSwitch().setChecked(prefs.selective_rotation_enabled);
    	proximityLockSwitch().setChecked(prefs.proximity_lock_enabled);
	    
	    return prefs;
    }
    PrefsData savePreferences()
    {
	    return Preferences.savePreferences(this,PrefsData.create()
        		.enableSelectiveRotation(selectiveRotationSwitch().isChecked())
        		.enableProximityLock(proximityLockSwitch().isChecked()));
    }
    
    PrefsData toggleAndSave(CheckedTextView view)
    {
    	view.toggle();
    	return savePreferences();
    }
    public void selectiveRotationClicked(View view)
    {
	    if (toggleAndSave(selectiveRotationSwitch()).selective_rotation_enabled)
	    	startSelectiveRotationService();
	    else
	    {
	    	stopService(selective_rotation_service);
	    }
	}

    public void proximityLockClicked(View view)
    {
    	if (proximityLockSwitch().isChecked()) // we are about to turn it off
    	{
    		toggleAndSave(proximityLockSwitch());
	    	stopService(proximity_lock_service);
    	}
    	else if (reader.getLastRead()==0)
    	{
    		AlertDialog dialog = new AlertDialog.Builder(this).create();
    		dialog.setMessage(getString(R.string.proximity_lock_denied));
    		dialog.setButton(AlertDialog.BUTTON_NEUTRAL,getString(R.string.ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dlg,int id) {
					dlg.dismiss();
				}
			  });
    		
    		dialog.show();
    	}
    	else if (AdminReceiver.isAdminAcquired(this))
		{
    		toggleAndSave(proximityLockSwitch());
        	startProximityLockService();
		}
    	else
    	{
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, AdminReceiver.getAdminComponentName());
			startActivityForResult(intent, ADMIN_REQUEST);
    	}
    }
    

    public void uninstallClicked(View view)
    {
		if (proximityLockSwitch().isChecked())
		{
			proximityLockSwitch().setChecked(false);
			stopService(proximity_lock_service);
		}
		if (selectiveRotationSwitch().isChecked())
		{
			selectiveRotationSwitch().setChecked(false);
			stopService(selective_rotation_service);
		}

		if (AdminReceiver.isAdminAcquired(this))
    	{
    		DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
    		dpm.removeActiveAdmin(AdminReceiver.getAdminComponentName());
    	}
		
		if (reader!=null)
	    	reader.stopReading();

 	    savePreferences();

 	    Uri packageURI = Uri.parse("package:"+PreferencesActivity.class.getPackage().getName());
    	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
    	startActivity(uninstallIntent);
    }
    
	final static int ADMIN_REQUEST = 1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) 
	{
        if (requestCode != ADMIN_REQUEST)
        	super.onActivityResult(requestCode, resultCode, data);
        else if (resultCode == RESULT_OK)
        {
    		toggleAndSave(proximityLockSwitch());
        	startProximityLockService();
        }
    }	
    
    void startProximityLockService()
    {
    	startService(proximity_lock_service);
    }
    void startSelectiveRotationService()
    {
    	startService(selective_rotation_service);
    }

}
