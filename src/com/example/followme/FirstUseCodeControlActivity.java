package com.example.followme;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FirstUseCodeControlActivity extends Activity {
	
	private EditText codeText;
	private Button okCodeButton;
	private String code;
	private TextView codeLabel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.first_use_code_control_layout);
		
		code=getIntent().getStringExtra("rdmCode");
		
		codeText = (EditText) findViewById(R.id.codeText);
		okCodeButton = (Button) findViewById(R.id.codeButton);
		codeLabel = (TextView) findViewById(R.id.codeLabel);
		
		
		okCodeButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				if(codeText.getText().toString().compareTo(code)==0)
				{
					Intent intent = new Intent();
					setResult(RESULT_OK, intent);
                	finish();
				}
				else
				{
					codeLabel.setText("ERROR Insert code again");
				}
			}
		});
	}

}
