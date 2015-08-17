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
		setContentView(R.layout.choose_contacts_for_sharing_layout);
	
		contacts = Utils.phoneContactsOnParse(DeviceDataManager.allContacts(this), ParseManager.allPhoneNumbers(this));
	}

}
