package com.followme.adapter;

import java.util.List;

import com.followme.activity.R;
import com.followme.manager.PersonalDataManager;
import com.followme.object.Request;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class RequestCustomAdapter extends ArrayAdapter<Request>{
	
	private List<Request> requests= null;
	private Context context;

	public RequestCustomAdapter(Context context, List<Request> resource) 
	{		
		super(context, R.layout.request_list_item_layout, resource);
		
		this.context = context;
		this.requests = resource;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		convertView = inflater.inflate(R.layout.request_list_item_layout, parent, false);
		
		TextView typeRequest = (TextView) convertView.findViewById(R.id.requestType);
		TextView senderRequest = (TextView) convertView.findViewById(R.id.requestSender);
		
		//per ogni elemento della lista setto il testo della text view type con il tipo di richiesta
		//per ogni elemento della lista setto il testo della text view sender con il mittente
		typeRequest.setText("Type: "+ requests.get(position).getType());
		senderRequest.setText("Sender: "+ 
				PersonalDataManager.getNameOfContact(
				requests.get(position).getSender().getPhoneNumber()));
		
		if(requests.get(position).getType().compareTo("condivisione")==0 || requests.get(position).getType().compareTo("percorso")==0)
		{
			ImageButton buttonShowDetails = (ImageButton) convertView.findViewById(R.id.showDetailsRequestButton);
			buttonShowDetails.setVisibility(View.INVISIBLE);
		}
		return convertView;	
	}

}
