package com.liken.dialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class ProcessDialog extends ProgressDialog {
String message;
	public ProcessDialog(Context context,String message) {
		super(context);
		this.message=message;
		
	}
	
	public void start(){	
		this.setMessage(message);
		this.setCancelable(false);
		this.show();	
	}
	
	public void end(){
		this.dismiss();
	}
	
}
