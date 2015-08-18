package com.example.followme;

import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;


public class ChooseContactsForSharingActivity extends ActionBarActivity
{
	private ListView lv;
	private Contact[] contactsItems;
	
	private Button contactSelectButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final String userId = getIntent().getStringExtra("userId");
		setContentView(R.layout.choose_contacts_for_sharing_layout);
		contactSelectButton = (Button) findViewById(R.id.contactSelectButton);		
		lv = (ListView) this.findViewById(R.id.contactList);
	
		List<Contact> contacts = Utils.phoneContactsOnParse(DeviceDataManager.allContacts(this), ParseManager.allContactsOnParse(this));
		
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
		
		contactSelectButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				//intent per l'attività di share
				Intent intent = new Intent(ChooseContactsForSharingActivity.this,ShareActivity.class);
				//passaggio parametri all'intent
				List<Contact> selectedContacts = Utils.selectedContacts(contactsItems);
				intent.putExtra("selectedContacts", selectedContacts.toArray());
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
			
		});
	}

}
