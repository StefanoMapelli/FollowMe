package com.followme.activity;

import java.util.List;

import com.followme.activity.R;
import com.followme.manager.DeviceDataManager;
import com.followme.manager.ParseManager;
import com.followme.manager.Utils;
import com.followme.object.Contact;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class FollowActivity extends ActionBarActivity{
	
private List<Contact> contacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_contacts_for_sharing_layout);
	
		contacts = Utils.phoneContactsOnParse(DeviceDataManager.allContacts(this), ParseManager.allContactsOnParse(this));
	}

}
