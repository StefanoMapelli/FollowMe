package com.followme.activity;

import com.followme.activity.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FollowActivity extends ActionBarSuperClassActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_activity_layout);
	}

	//onClickHandler quando viene selezionata la modalità fence
	public void startFenceOnClickHandler(View v) 
	{
		Intent intent = new Intent(FollowActivity.this,FenceSettingActivity.class);
		intent.putExtra("selectedContacts", getIntent().getSerializableExtra("selectedContacts"));
		intent.putExtra("userId", getIntent().getStringExtra("userId"));
		startActivity(intent);
		finish();
	}


	//onClickHandler quando viene selezionata la modalità di destination
	public void startDestinationOnClickHandler(View v) 
	{
		Intent intent = new Intent(FollowActivity.this,DestinationSettingActivity.class);
		intent.putExtra("selectedContacts", getIntent().getSerializableExtra("selectedContacts"));
		intent.putExtra("userId", getIntent().getStringExtra("userId"));
		startActivity(intent);
		finish();
	}


	//onClickHandler quando viene selezionata la modalità di follow path
	public void startPathOnClickHandler(View v) 
	{
		//Intent che manda alla ricezione del percorso del followato
		Intent intent = new Intent(FollowActivity.this,PathControlActivity.class);
		intent.putExtra("selectedContacts", getIntent().getSerializableExtra("selectedContacts"));
		intent.putExtra("userId", getIntent().getStringExtra("userId"));
		startActivity(intent);
		finish();
	}


}