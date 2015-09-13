package com.followme.activity;

import com.followme.activity.R;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
				
				if(phoneNumber.length()==10)
				{				
					//scrivo su db parse e db locale il nuovo utente
					Boolean phoneNumberExists = ParseManager.phoneNumberExists(FirstUseActivity.this, phoneNumber);
					if(phoneNumberExists==null)
					{
						Toast.makeText(FirstUseActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
						if(!phoneNumberExists)
						{
							ParseManager.insertPhoneNumber(FirstUseActivity.this, phoneNumber);
						}
						String id = ParseManager.getId(FirstUseActivity.this, phoneNumber);
						if(id == null)
						{
							Toast.makeText(FirstUseActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
						}
						else
						{
							PersonalDataManager.insertUser(phoneNumber, id);
							
							//scrivo sms di conferma
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(phoneNumber, null, "A new account for the Follow Me Android app has been created with this id: "+id+". Please send an email to followmeappinfo@gmail.com with this id if you are not the user who asks for this request.", null, null);
							
							Intent intent = new Intent();
							intent.putExtra("id", id);
							setResult(RESULT_OK, intent);
			            	finish();
						}
					}													
				}
			} 
		});
	}
}
