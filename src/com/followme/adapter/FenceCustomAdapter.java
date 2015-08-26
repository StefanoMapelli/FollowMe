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
import com.followme.object.Fence;
import com.followme.object.Request;

public class FenceCustomAdapter extends ArrayAdapter<Fence>{

	private List<Fence> fence= null;
	private Context context;

	public FenceCustomAdapter(Context context, List<Fence> resource) 
	{		
		super(context, R.layout.request_list_item_layout, resource);

		this.context = context;
		this.fence = resource;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		convertView = inflater.inflate(R.layout.fence_list_item_layout, parent, false);

		TextView userText=(TextView) convertView.findViewById(R.id.userInTheFence);
		TextView centerText = (TextView) convertView.findViewById(R.id.centerOfTheFence);
		TextView radiusText = (TextView) convertView.findViewById(R.id.radiusOfTheFence);
		TextView statusText = (TextView) convertView.findViewById(R.id.statusOfTheFence);

		userText.setText("User: "+ fence.get(position).getUser().getName());
		centerText.setText("Center: "+ fence.get(position).getCenter().latitude+ " - " +fence.get(position).getCenter().longitude);
		radiusText.setText("Radius: "+ fence.get(position).getRadius());
		if(fence.get(position).isInTheFence())
		{
			statusText.setText("Status: User in the fence");
			statusText.setTextColor(Color.GREEN);
		}
		else
		{
			statusText.setText("Status: User run away");
			statusText.setTextColor(Color.RED);
		}
		
		return convertView;	
	}

}

