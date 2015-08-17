package com.example.followme;


import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FirstUseActivity extends Activity {
	
	private EditText phoneNumberText;
	private Button okButton;
	private String phoneNumber;
	
	private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	
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
				
					//scrivo sms di conferma con codice random
					SmsManager smsManager = SmsManager.getDefault();
					String rdmCode = getRandomString(); 
					smsManager.sendTextMessage(phoneNumber, null, rdmCode, null, null);
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//cancella il messaggio inviato dalla cronologia
					deleteLastSMS(rdmCode);
					
					//controllo del code inviato con SMS
					Intent intentControlCode = new Intent(FirstUseActivity.this, FirstUseCodeControlActivity.class);
					intentControlCode.putExtra("rdmCode", rdmCode);
					startActivityForResult(intentControlCode, 0);
				}
			} 
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		//sta ritornando l'attività first use
		if(requestCode==0)
		{
			if(resultCode==RESULT_OK)
			{
				//scrivo su db parse e db locale il nuovo utente
				
				if(!ParseManager.phoneNumberExists(FirstUseActivity.this, phoneNumber))
				{
					ParseManager.insertPhoneNumber(FirstUseActivity.this, phoneNumber);
				}
				String id = ParseManager.getId(FirstUseActivity.this, phoneNumber);
				PersonalDataManager.insertUser(phoneNumber, id);
				
				Intent intent = new Intent();
				intent.putExtra("id", id);
				setResult(RESULT_OK, intent);
            	finish();
			}
		}
	}
	
	//numero casuale
	private int getRandomNumber() {
        int randomInt = 0;
        Random random=new Random();
        randomInt = random.nextInt(_CHAR.length());
        if (randomInt - 1 == -1) {
              return randomInt;
        } else {
              return randomInt - 1;
        }
  }

	//stringa casuale
	public String getRandomString()
	{

        StringBuffer randStr = new StringBuffer();

        for (int i = 0; i < 5; i++) {

              int number = getRandomNumber();
              char ch = _CHAR.charAt(number);
              randStr.append(ch);
        }
        return randStr.toString();
	}
	
	public void deleteLastSMS(String code)
	{
		/*Uri deleteUri = Uri.parse("content://sms/sent");
		Cursor m_cCursor=this.getContentResolver().query(deleteUri, null, null,null, null);
		m_cCursor.moveToFirst();
		String body;*/
		ContentValues cv = new ContentValues();
	    cv.put("body", "Code Request");
		int n1= getContentResolver().update(Uri.parse("content://sms"), cv, "body = ?", new String[] {code});
		/*do
		{
			body=m_cCursor.getString(12);
			if(body.compareTo(code)==0)
			{
				int id=m_cCursor.getInt(0);
				deleteSMS(String.valueOf(id));
				break;
			}
		}
		while(m_cCursor.moveToNext());*/
		
	}
	
	public boolean deleteSMS(String smsId) {
	    boolean isSmsDeleted = false;
	    ContentValues cv = new ContentValues();
	    cv.put("body", "Code Request");
	    try {
	        int n=this.getContentResolver().update(Uri.parse("content://sms/sent"), cv ,  null, null);
	        isSmsDeleted = true;

	    } catch (Exception ex) {
	        isSmsDeleted = false;
	    }
	    return isSmsDeleted;
	}
	
}
