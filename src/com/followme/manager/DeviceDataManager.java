package com.followme.manager;

import java.util.ArrayList;
import java.util.List;

import com.followme.object.Contact;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class DeviceDataManager {
	
	public static List<Contact> allContacts(Activity a)
	{
		
		String[] columns = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
		Cursor cursor = a.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, columns, null, null, null);

		int ColumeIndex_DISPLAY_NAME = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		int ColumeIndex_HAS_PHONE_NUMBER = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		String name;
		String has_phone;
		String phone_number;
		List<Contact> allContacts = new ArrayList<Contact>();
		
		while(cursor.moveToNext()) 
		{   
		    name = cursor.getString(ColumeIndex_DISPLAY_NAME);
		    has_phone = cursor.getString(ColumeIndex_HAS_PHONE_NUMBER);

		    if(!has_phone.endsWith("0")) 
		    {
		    	 phone_number = getPhoneNumber(a,name);
				 allContacts.add(new Contact(null, name, phone_number));
		    }
		}
		cursor.close();
		return allContacts;
	}
	
	
	public static String getPhoneNumber(Activity a,String name) 
	{
		ContentResolver cr = a.getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
		    "DISPLAY_NAME = '" + name + "'", null, null);
		if (cursor.moveToFirst()) 
		{
		    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		    Cursor phones = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contactId, null, null);
		        if (phones.moveToFirst()) 
		        {
		           String pn = phones.getString(phones.getColumnIndex(Phone.NUMBER));
		           pn = pn.replace("+39", "");
		           cursor.close();
		           return pn.replace(" ", "");
		           
		        }
		}
		cursor.close();
		return null;
	}

}
