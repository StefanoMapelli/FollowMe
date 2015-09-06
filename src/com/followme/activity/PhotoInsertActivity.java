package com.followme.activity;

import com.followme.manager.Utils;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;

public class PhotoInsertActivity extends ActionBarActivity {

	private ImageView imageView;
	private EditText titleEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_insert);
		imageView = (ImageView) findViewById(R.id.imageViewToSave);
		titleEditText = (EditText) findViewById(R.id.inputTitlePhoto);
		
		final String fileName = getIntent().getStringExtra("fileName");
		
		ViewTreeObserver vto = imageView.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    public boolean onPreDraw() {
		    	imageView.getViewTreeObserver().removeOnPreDrawListener(this);
		        int finalHeight = imageView.getMeasuredHeight();
		        int finalWidth = imageView.getMeasuredWidth();
		        Utils.setPic(finalWidth, finalHeight, imageView, fileName);
		    		        
		        return true;
		    }
		});	
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
		getMenuInflater().inflate(R.menu.photo_insert, menu);
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
