package com.example.followme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract;

public class ChooseContactActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_contact_layout);
		
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, 1);
		
	}
	
	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data)
	{
		super.onActivityResult(reqCode, resultCode, data);
		
		if(reqCode==1 && resultCode==Activity.RESULT_OK)
		{
			Uri contactData = data.getData();
			Cursor c = getContentResolver().query(contactData, null, null, null, null);
			
			if(c.moveToFirst())
			{
				String primaColonna;
				String secondaColonna;
				while(!c.isAfterLast())
				{
					primaColonna = c.getString(1);
					secondaColonna = c.getString(2);
				}
			}
		}
	}
}
