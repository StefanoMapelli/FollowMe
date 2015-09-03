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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
	private ProgressDialog progressBar;

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
			refreshContactsForSharingOnClickHandler(null);
		}
		else
		{
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

	public void selectContactForSharingOnClickHandler(View v)
	{
		//intent per l'attivit� di share
		Intent intent = new Intent(ChooseContactsForSharingActivity.this,ShareActivity.class);
		//passaggio parametri all'intent
		List<Contact> selectedContacts = Utils.selectedContacts(contactsItems);
		intent.putExtra("selectedContacts", selectedContacts.toArray());
		intent.putExtra("userId", userId);
		startActivity(intent);
	}

	private class RefreshSharingContactListThread extends AsyncTask<Void, Integer, String>
	{
		@Override
		protected void onProgressUpdate (Integer...progress)
		{
			if(progress[0]==0)
			{
				progressBar = new ProgressDialog(ChooseContactsForSharingActivity.this);
				progressBar.setCancelable(false);
				progressBar.setMessage("Searching for new contacts...");
				progressBar.setMax(100);
				progressBar.show();
			}
			else if(progress[0]==1)
			{
				progressBar.setMessage("Refreshing your conacts list...");
			}
			else if(progress[0]==2)	
			{
				ContactCustomAdapter adapter = new ContactCustomAdapter(ChooseContactsForSharingActivity.this, contactsItems);
				lv.setAdapter(adapter);
				progressBar.dismiss();
			}
		}

		@Override
		protected void onPreExecute()
		{
			publishProgress(0);
		}

		@Override
		protected void onPostExecute(String result) 
		{
			publishProgress(2);
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			contacts = Utils.phoneContactsOnParse(
					DeviceDataManager.allContacts(ChooseContactsForSharingActivity.this), 
					ParseManager.allContactsOnParse(ChooseContactsForSharingActivity.this));

			publishProgress(1);

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
			return null;
		}

	}

	public void refreshContactsForSharingOnClickHandler(View v)
	{
		new RefreshSharingContactListThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
