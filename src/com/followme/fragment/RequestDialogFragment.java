package com.followme.fragment;

import java.util.List;

import com.followme.activity.R;
import com.followme.activity.RequestsListActivity;
import com.followme.manager.ParseManager;
import com.followme.object.Request;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
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
				public void onClick(DialogInterface dialog, int which) 
				{
					Intent intent = new Intent(getActivity(),RequestsListActivity.class);
					intent.putExtra("incomingRequests", requests.toArray());
					startActivity(intent);
				}
			})
				.setNegativeButton(R.string.ignore_request, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						for(Request r : requests)
						{
							ParseManager.deleteRequest(getActivity(), r.getId());
						}
					}
				});
		return builder.create();
	}
}
