package com.example.followme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

public class Utils {
	
	public static List<Contact> phoneContactsOnParse(Context context, List<Contact> allContacts, List<String> allUsers)
	{
		List<Contact> output = new ArrayList<Contact>();
		Iterator<Contact> i = allContacts.iterator();
		Iterator<String> j = allUsers.iterator();
		Contact c;
		String number;
		
		while(i.hasNext())
		{
			c = i.next();
			j = allUsers.iterator();
			
			while(j.hasNext())
			{
				number = j.next();
				
				if(c.getPhoneNumber().compareTo(number) == 0)
				{
					output.add(c);
				}
			}
		}
		
		return output;
	}

}
