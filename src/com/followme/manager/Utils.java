package com.followme.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.followme.object.Contact;

public class Utils {
	
	/**
	 * This method create the list of contacts that are in both lists
	 * allContacts and allUsers. 
	 * @param allContacts : all the contacts of the user
	 * @param allUsers : all the phone numbers that are present in parse db
	 * @return
	 */
	public static List<Contact> phoneContactsOnParse(List<Contact> allContacts, List<Contact> allUsers)
	{
		List<Contact> output = new ArrayList<Contact>();
		Iterator<Contact> i = allContacts.iterator();
		Iterator<Contact> j = allUsers.iterator();
		Contact c;
		Contact number;
		
		while(i.hasNext())
		{
			c = i.next();
			j = allUsers.iterator();
			
			while(j.hasNext())
			{
				number = j.next();
				
				if(c.getPhoneNumber().compareTo(number.getPhoneNumber()) == 0)
				{
					c.setId(number.getId());
					output.add(c);
				}
			}
		}
		
		return output;
	}
	
	/**
	 * This method create the list of contact that are checked.
	 * @param contacts : starting input array
	 * @return
	 */
	public static List<Contact> selectedContacts(Contact[] contacts)
	{
		List<Contact> output = new ArrayList<Contact>();
		for(int i=0; i<contacts.length; i++)
		{
			if(contacts[i].isChecked())
			{
				output.add(contacts[i]);
			}
		}
		
		return output;
	}

}
