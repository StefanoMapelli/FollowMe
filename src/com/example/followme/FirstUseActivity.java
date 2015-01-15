package com.example.followme;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FirstUseActivity extends Activity {
	
	private EditText phoneNumberText;
	private Button okButton;
	private String phoneNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.first_use_layout);
		phoneNumberText = (EditText) findViewById(R.id.phoneNumberText);
		okButton = (Button) findViewById(R.id.okButton);
		
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				phoneNumber = phoneNumberText.getText().toString();
				PersonalDataManager.insertPhoneNumber(phoneNumber);
				Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
			} 
		});
	}

}
