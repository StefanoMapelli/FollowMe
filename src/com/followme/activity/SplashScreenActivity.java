package com.followme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen_layout);
		
		new SplashActivity().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
	}
	

	private class SplashActivity extends AsyncTask<Void, Integer, String>
	{
		@Override
		protected String doInBackground(Void... params) 
		{
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);										
			startActivity(intent);
			finish();
			return null;
		}
	}
}
