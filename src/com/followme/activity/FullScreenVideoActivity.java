package com.followme.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.MediaController;
import android.widget.VideoView;

public class FullScreenVideoActivity extends Activity {

	private VideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_video);
		videoView = (VideoView) findViewById(R.id.fullScreenVideo);
		
		Uri videoUri = Uri.parse(getIntent().getStringExtra("fileName"));				
		videoView.setVideoURI(videoUri);
	 	
		MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        mc.setAnchorView(videoView);
           
        DisplayMetrics dm=new DisplayMetrics();            
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        videoView.setMinimumWidth(width);
        
        videoView.start();
	}
}
