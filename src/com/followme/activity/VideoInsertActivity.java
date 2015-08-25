package com.followme.activity;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoInsertActivity extends ActionBarActivity {

	private VideoView videoView;
	private Button saveButton;
	private EditText titleEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_insert);
		videoView = (VideoView) findViewById(R.id.videoViewToSave);
		saveButton = (Button) findViewById(R.id.saveButtonVideo);
		titleEditText = (EditText) findViewById(R.id.inputTitleVideo);
		
		Uri videoUri = Uri.parse(getIntent().getStringExtra("imageUri"));				
		videoView.setVideoURI(videoUri);
	 	
		MediaController mc = new MediaController(VideoInsertActivity.this);
        videoView.setMediaController(mc);
        mc.setAnchorView(videoView);
           
        DisplayMetrics dm=new DisplayMetrics();            
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        videoView.setMinimumWidth(width);
        
        videoView.start();
		
		saveButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{							
				Intent intent = new Intent();
				intent.putExtra("title", titleEditText.getText().toString());
				setResult(Activity.RESULT_OK, intent);
                finish();
			}
			
		}); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_insert, menu);
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
