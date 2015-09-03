package com.followme.fragment;

import com.followme.activity.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SavePathDialogFragment extends DialogFragment
{
	private SavePathDialogListener mListener;
	private View view; 
	
	public interface SavePathDialogListener {
        public void onDialogPositiveClick(SavePathDialogFragment dialog);
    }
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SavePathDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
	public SavePathDialogFragment() 
	{
		super();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.save_path_dialog_layout, null);
		builder.setView(view)
			   .setMessage(R.string.save_path_dialog)
			   .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// Send the positive button event back to the host activity
                    mListener.onDialogPositiveClick(SavePathDialogFragment.this);
				}
			})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						SavePathDialogFragment.this.getDialog().cancel();
					}
				});
		return builder.create();
	}

	public EditText getTitleText() {
		return (EditText) view.findViewById(R.id.savePathTitle);
	}

}
