package com.followme.activity;

import java.util.Iterator;
import java.util.List;

import com.followme.adapter.ContactCustomAdapter;
import com.followme.manager.DeviceDataManager;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.manager.Utils;
import com.followme.object.Contact;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ChooseContactsForFollowActivity extends ActionBarActivity {
		
	private ListView lv;
	private Contact[] contactsItems;
	private String userId;
	private List<Contact> contacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_contacts_for_follow_layout);
		userId = getIntent().getStringExtra("userId");
		lv = (ListView) this.findViewById(R.id.contactListFollow);
	
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

	public void selectContactForFollowOnClickHandler(View v)
	{
		//intent per l'attività di share
		Intent intent = new Intent(ChooseContactsForFollowActivity.this,FollowActivity.class);
		//passaggio parametri all'intent
		List<Contact> selectedContacts = Utils.selectedContacts(contactsItems);
		intent.putExtra("selectedContacts", selectedContacts.toArray());
		intent.putExtra("userId", userId);
		startActivity(intent);
	}
	
	public void refreshContactsForFollowOnClickHandler(View v)
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_contacts_for_follow, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
