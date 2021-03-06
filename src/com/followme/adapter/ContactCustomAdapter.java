package com.followme.adapter;

import com.followme.activity.R;
import com.followme.object.Contact;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ContactCustomAdapter extends ArrayAdapter<Contact>{

	private Contact[] contacts = null;
	private Context context;
	
	public ContactCustomAdapter(Context context, Contact[] resource) {
		super(context, R.layout.contact_list_item_layout, resource);
		
		this.context = context;
		this.contacts = resource;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		convertView = inflater.inflate(R.layout.contact_list_item_layout, parent, false);
		
		TextView name = (TextView) convertView.findViewById(R.id.name);
		CheckBox cb = (CheckBox) convertView.findViewById(R.id.chk);
		name.setText(contacts[position].getName());
		
		if(contacts[position].isChecked())
		{
			cb.setChecked(true);
		}
		else
		{
			cb.setChecked(false);
		}
		
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				contacts[position].setChecked(isChecked);
			}
        });
		
		return convertView;	
	}
}
