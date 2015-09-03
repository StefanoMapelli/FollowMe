package com.followme.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.followme.activity.R;
import com.followme.object.Path;

public class PathCustomAdapter extends ArrayAdapter<Path>{
	
	
	private List<Path> path= null;
	private Context context;
	
	public PathCustomAdapter(Context context, List<Path> path) 
	{		
		super(context, R.layout.saved_path_list_item_layout, path);
		
		this.context = context;
		this.path = path;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		convertView = inflater.inflate(R.layout.saved_path_list_item_layout, parent, false);
		
		TextView pathTitle = (TextView) convertView.findViewById(R.id.savedPathTitle);
		TextView pathOwner = (TextView) convertView.findViewById(R.id.savedPathOwner);
		TextView pathDate= (TextView) convertView.findViewById(R.id.savedPathDate);
		
		pathTitle.setText("Title: "+ path.get(position).getTitle());
		pathOwner.setText("Owner: "+ path.get(position).getOwner());	
		pathDate.setText("Date: "+ path.get(position).getDate());
		
		return convertView;	
	}

}
