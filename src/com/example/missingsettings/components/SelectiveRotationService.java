package com.example.missingsettings.components;

import com.example.missingsettings.ReceiverService;

public class SelectiveRotationService extends ReceiverService<SelectiveRotationReceiver> 
{ 
	public SelectiveRotationService() 
	{
		super(SelectiveRotationReceiver.class);
	}

}
