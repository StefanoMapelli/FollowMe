package com.followme.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ActionBarSuperClassActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_bar_superclass_layout);
		this.getSupportActionBar().setDisplayShowCustomEnabled(true);
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);

		LayoutInflater inflator = LayoutInflater.from(this);
		View v = inflator.inflate(R.layout.action_bar_superclass_layout, null);

		//if you need to customize anything else about the text, do it here.
		//I'm using a custom TextView with a custom font in my layout xml so all I need to do is set title
		((TextView)v.findViewById(R.id.title)).setText(this.getTitle());

		//assign the view to the actionbar
		this.getSupportActionBar().setCustomView(v);
	}
}
