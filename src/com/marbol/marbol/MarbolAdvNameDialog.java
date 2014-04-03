package com.marbol.marbol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class MarbolAdvNameDialog extends DialogFragment {
	private View rootView;
	public interface MarbolAdvNameDialogHandler {
        public void onDialogOkClick(MarbolAdvNameDialog dialog);
        public void onDialogCancelClick(MarbolAdvNameDialog dialog);
	}
	
	MarbolAdvNameDialogHandler mListener;
	
	public MarbolAdvNameDialog(Fragment hostFragment) {
		mListener = (MarbolAdvNameDialogHandler) hostFragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflator = getActivity().getLayoutInflater();

	    final MarbolAdvNameDialog thisDialog = this;
	    
	    rootView = inflator.inflate(R.layout.marbol_adventure_name_dialog, null);
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(rootView).setPositiveButton(R.string.ok_string, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   // this is a bit hacky but it gets the job done for now
	            	   mListener.onDialogOkClick(thisDialog);
	               }
	           })
	           .setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   // also super hacky but ok for now
	            	   mListener.onDialogCancelClick(thisDialog);
	               }
	           });      
	    return builder.create();
	}
	
	public String getAdventureName() {
		EditText text = (EditText)rootView.findViewById(R.id.dialog_adventure_name_edit);
		return text.getText().toString();	
	}
}
