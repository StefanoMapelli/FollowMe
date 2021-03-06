package com.followme.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.followme.activity.R;
import com.followme.object.Destination;

public class DestinationCustomAdapter extends ArrayAdapter<Destination>{

	private List<Destination> destination= null;
	private Context context;

	public DestinationCustomAdapter(Context context, List<Destination> resource) 
	{		
		super(context, R.layout.destination_list_item_layout, resource);

		this.context = context;
		this.destination = resource;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		convertView = inflater.inflate(R.layout.destination_list_item_layout, parent, false);

		TextView userText=(TextView) convertView.findViewById(R.id.userToDestination);
		TextView centerText = (TextView) convertView.findViewById(R.id.centerOfTheDestination);
		TextView radiusText = (TextView) convertView.findViewById(R.id.radiusOfTheDestination);
		TextView statusText = (TextView) convertView.findViewById(R.id.statusOfTheDestination);

		userText.setText("User: "+ destination.get(position).getUser().getName());
		centerText.setText("Center: "+ Double.toString(destination.get(position).getCenter().latitude).substring(0, 9)+ " - " +Double.toString(destination.get(position).getCenter().longitude).substring(0, 9));
		radiusText.setText("Radius: "+ destination.get(position).getRadius()+"m");

		if(destination.get(position).getStatusAccepted() == null)
		{
			statusText.setText("Status: Pending...");
			statusText.setTextColor(Color.GRAY);
		}
		else if(!destination.get(position).getStatusAccepted())
		{
			statusText.setText("Status: Request declined");
			statusText.setTextColor(Color.GRAY);
		}
		else
		{

			if(destination.get(position).isInTheDestination())
			{
				statusText.setText("Status: User Is In The Destination Zone");
				statusText.setTextColor(Color.GREEN);
			}
			else
			{
				statusText.setText("Status: User Is Not Arrived");
				statusText.setTextColor(Color.RED);
			}
		}
		
		return convertView;	
	}

}

