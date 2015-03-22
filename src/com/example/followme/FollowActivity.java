package com.example.followme;

import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class FollowActivity extends ActionBarActivity{
	
private List<Contact> contacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_activity_layout);
	
		contacts = ParseManager.selectContacts(this, DeviceDataManager.allContacts(this));
	}

}
