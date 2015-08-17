package com.example.followme;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RequestDialogFragment extends DialogFragment
{
	private List<Request> requests;

	public RequestDialogFragment(List<Request> requests) 
	{
		super();
		this.requests = requests;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.request_dialog)
			   .setPositiveButton(R.string.show_request, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
				.setNegativeButton(R.string.ignore_request, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
		return builder.create();
	}
}
