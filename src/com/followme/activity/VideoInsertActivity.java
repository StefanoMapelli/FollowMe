package com.followme.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoInsertActivity extends ActionBarSuperClassActivity {

	private VideoView videoView;
	private EditText titleEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_insert);
		videoView = (VideoView) findViewById(R.id.videoViewToSave);
		titleEditText = (EditText) findViewById(R.id.inputTitleVideo);
		
		Uri videoUri = Uri.parse(getIntent().getStringExtra("videoUri"));				
		videoView.setVideoURI(videoUri);
	 	
		MediaController mc = new MediaController(VideoInsertActivity.this);
        videoView.setMediaController(mc);
        mc.setAnchorView(videoView);
           
        DisplayMetrics dm=new DisplayMetrics();            
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        videoView.setMinimumWidth(width);       
        videoView.start();
	}

	public void saveOnClickHandler(View v)
	{
		Intent intent = new Intent();
		intent.putExtra("title", titleEditText.getText().toString());
		setResult(Activity.RESULT_OK, intent);
        finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_insert, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
