package com.example.followme;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ShareActivity extends ActionBarActivity {

	private Contact[] contacts = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_layout);
		Object[] objects = (Object[]) getIntent().getSerializableExtra("selectedContacts");
		contacts = new Contact[objects.length];
		
		for(int i=0; i < objects.length; i++)
		{
			contacts[i] = (Contact) objects[i];
		}
		
		String userId = getIntent().getStringExtra("userId");
		
		for(int i=0; i<contacts.length; i++)
		{
			ParseManager.insertRequest(this, "condivisione", userId, contacts[i].getId());
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
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
