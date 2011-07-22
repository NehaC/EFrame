package com.EFrame13;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutUs extends Activity{  
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        final Dialog dialog = new Dialog(AboutUs.this);
			dialog.setContentView(R.layout.about_us_dialog);
			dialog.setTitle("Elite PictureFrame");
			dialog.setCancelable(true);
			TextView slideshowDialog = (TextView) dialog.findViewById(R.id.slideshowDialog);
			slideshowDialog.setText("Version: 1.5\n\n\nCompany: http://blueplanetsolutions.com/");
			
			Button ok = (Button) dialog.findViewById(R.id.ok);
			 ok.setOnClickListener(new OnClickListener() {

             public void onClick(View v) {
                     dialog.dismiss();
                     
                     Intent i = new Intent(AboutUs.this, home.class);
 					 startActivity(i);
                     
                 }
             });
             dialog.show();
	        
	 }

}
