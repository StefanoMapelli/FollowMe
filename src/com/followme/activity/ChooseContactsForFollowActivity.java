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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ChooseContactsForFollowActivity extends ActionBarActivity {
		
	private ListView lv;
	private Contact[] contactsItems;
	private String userId;
	private List<Contact> contacts;
	private ProgressDialog progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_contacts_for_follow_layout);
		userId = getIntent().getStringExtra("userId");
		lv = (ListView) this.findViewById(R.id.contactListFollow);
	
		contacts = PersonalDataManager.getAllContacts();
		
		if(contacts.isEmpty())
		{
			refreshContactsForFollowOnClickHandler(null);
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

	public void selectContactForFollowOnClickHandler(View v)
	{
		//intent per l'attività di share
		Intent intent = new Intent(ChooseContactsForFollowActivity.this,FollowActivity.class);
		//passaggio parametri all'intent
		List<Contact> selectedContacts = Utils.selectedContacts(contactsItems);
		intent.putExtra("selectedContacts", selectedContacts.toArray());
		intent.putExtra("userId", userId);
		startActivity(intent);
		finish();
	}
	
	private class RefreshFollowContactListThread extends AsyncTask<Void, Integer, String>
	{

		@Override
		protected void onProgressUpdate (Integer...progress)
		{
		
			if(progress[0]==0)
			{
				progressBar = new ProgressDialog(ChooseContactsForFollowActivity.this);
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
				ContactCustomAdapter adapter = new ContactCustomAdapter(ChooseContactsForFollowActivity.this, contactsItems);
				lv.setAdapter(adapter);
				progressBar.dismiss();				
			} 
			else if(progress[0] == 3)
			{
				Toast.makeText(ChooseContactsForFollowActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected void onPreExecute() {

			publishProgress(0);
		}

		@Override
		protected void onPostExecute(String result) {

			publishProgress(2);

		}

		@Override
		protected String doInBackground(Void... params) 
		{
			List<Contact> list = ParseManager.allContactsOnParse(ChooseContactsForFollowActivity.this);
			
			if(list == null)
			{
				publishProgress(3);
				return null;
			}
			else
			{
				contacts = Utils.phoneContactsOnParse(
						DeviceDataManager.allContacts(ChooseContactsForFollowActivity.this), 
						list);
				
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
	}
	
	
	public void refreshContactsForFollowOnClickHandler(View v)
	{
		new RefreshFollowContactListThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_contacts_for_follow, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here.
		return super.onOptionsItemSelected(item);
	}
}
