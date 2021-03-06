package com.followme.activity;

import java.util.ArrayList;

import com.followme.adapter.PathCustomAdapter;
import com.followme.manager.PersonalDataManager;
import com.followme.object.Path;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SavedPathListActivity extends ActionBarSuperClassActivity {
	
	private ArrayList<Path> pathList;
	private PathCustomAdapter adapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_path_list_layout);
		
		listView = (ListView) this.findViewById(R.id.savedPathListView);		
		pathList=PersonalDataManager.getAllPaths();
		
		//set the adapter
		adapter = new PathCustomAdapter(this, pathList);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			
   			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
   				Intent intent = new Intent(SavedPathListActivity.this, LoadPathActivity.class);
   				intent.putExtra("path", pathList.get((int)id));
   				startActivity(intent);
			}
        });
		
		if(pathList.isEmpty())
		{
			new AlertDialog.Builder(this)
		    .setTitle("Attention")
		    .setMessage("There no saved paths on this device!")
		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	finish();
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .show();
		}
	}
	
	
	//onClickHandler quando viene eliminato un path
	public void deleteSavedPathOnClickHandler(View v) {
		
		View parentRow = (View) v.getParent();
		int position = listView.getPositionForView(parentRow);
		Path itemToRemove = pathList.get(position);
		
		//eliminare il path dal db locale
		PersonalDataManager.deletePath(itemToRemove.getId());
		//reset the arraylist
		int i = 0;
		for (Path r : pathList)
		{
			if(r.getId().compareTo(itemToRemove.getId())==0)
			{
				pathList.remove(i);
				break;
			}
			i++;
		}
		if(pathList.isEmpty())
		{
			finish();
		}
		//rimuovere dalla listview
		adapter.remove(itemToRemove);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.saved_path_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
