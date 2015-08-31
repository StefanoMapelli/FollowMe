package com.followme.activity;

import java.util.ArrayList;

import com.followme.adapter.PathCustomAdapter;
import com.followme.adapter.RequestCustomAdapter;
import com.followme.manager.PersonalDataManager;
import com.followme.object.Path;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SavedPathListActivity extends ActionBarActivity {
	
	ArrayList<Path> pathList;
	PathCustomAdapter adapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_path_list_layout);
		
		listView = (ListView) this.findViewById(R.id.savedPathListView);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
   			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
   				Intent intent = new Intent(SavedPathListActivity.this, LoadPathActivity.class);
   				intent.putExtra("pathLocalId", pathList.get((int)id).getId());
   				startActivity(intent);
			}
        });
		
		pathList=PersonalDataManager.getAllPaths();
		
		//set the adapter
		adapter = new PathCustomAdapter(this, pathList);
		listView.setAdapter(adapter);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.saved_path_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
