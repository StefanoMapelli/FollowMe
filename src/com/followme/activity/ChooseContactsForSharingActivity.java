package com.followme.activity;

import java.util.Iterator;
import java.util.List;

import com.followme.activity.R;
import com.followme.adapter.ContactCustomAdapter;
import com.followme.manager.DeviceDataManager;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.manager.Utils;
import com.followme.object.Contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

public class ChooseContactsForSharingActivity extends ActionBarActivity
{
	private ListView lv;
	private List<Contact> contacts;
	private Contact[] contactsItems;
	private String userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		userId = getIntent().getStringExtra("userId");
		setContentView(R.layout.choose_contacts_for_sharing_layout);		
		lv = (ListView) this.findViewById(R.id.contactList);
	
		contacts = PersonalDataManager.getAllContacts();
		
		if(contacts.isEmpty())
		{
			contacts = Utils.phoneContactsOnParse(
					DeviceDataManager.allContacts(this), 
					ParseManager.allContactsOnParse(this));
			
			for (Contact c : contacts)
			{
				PersonalDataManager.insertContact(c);
			}
		}
						
		Iterator<Contact> i = contacts.iterator();
		Contact c;
		int j=0;
		contactsItems = new Contact[contacts.size()];
		
		while(i.hasNext())
		{
			c = i.next();
			
			contactsItems[j] = c;
			j++;
		}
		
		ContactCustomAdapter adapter = new ContactCustomAdapter(this, contactsItems);
		lv.setAdapter(adapter);	
	}
	
	public void selectContactForSharingOnClickHandler(View v)
	{
		//intent per l'attività di share
		Intent intent = new Intent(ChooseContactsForSharingActivity.this,ShareActivity.class);
		//passaggio parametri all'intent
		List<Contact> selectedContacts = Utils.selectedContacts(contactsItems);
		intent.putExtra("selectedContacts", selectedContacts.toArray());
		intent.putExtra("userId", userId);
		startActivity(intent);
	}

	public void refreshContactsForSharingOnClickHandler(View v)
	{
		contacts = Utils.phoneContactsOnParse(
				DeviceDataManager.allContacts(this), 
				ParseManager.allContactsOnParse(this));
		
		PersonalDataManager.deleteAllContacts();
		for (Contact c : contacts)
		{
			PersonalDataManager.insertContact(c);
		}
		
		Iterator<Contact> i = contacts.iterator();
		Contact c;
		int j=0;
		contactsItems = new Contact[contacts.size()];
		
		while(i.hasNext())
		{
			c = i.next();
			
			contactsItems[j] = c;
			j++;
		}
		
		ContactCustomAdapter adapter = new ContactCustomAdapter(this, contactsItems);
		lv.setAdapter(adapter);	
	}
}
