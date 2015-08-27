package com.followme.activity;

import java.util.ArrayList;

import com.followme.adapter.GalleryLayout;
import com.followme.object.CustomMarker;

import android.app.Activity;
import android.os.Bundle;

public class MediaGalleryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_media_gallery);
		GalleryLayout layout = (GalleryLayout) findViewById(R.id.galleryLayout);
		
		Object[] objects = (Object[]) getIntent().getSerializableExtra("media");
		ArrayList<CustomMarker> markers = new ArrayList<CustomMarker>();
		
		//cast from object to marker
		for(int i=0; i < objects.length; i++)
		{
			markers.add((CustomMarker) objects[i]);
		}
		
		//create a path and insert requests for the users receivers for that path
		int index = getIntent().getIntExtra("index",-1);
		
		layout.setFeatureItems(this, markers);
		layout.setActiveFeature(index);
	}
}
