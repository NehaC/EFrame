package com.EFrame13;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FullPhoto extends Activity{

	String selectedPhoto, selectedAlbumName;
	DBAdapter db = new DBAdapter(this);
//	Session ss = new Session();
	BitmapFactory.Options options;
	Button viewDetails=null; 
	Button editDetails=null;
	Button setAsWallpaper=null;
	int flag=0;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
     // In order to do any transactions with database.. Need to open the database..
        db.open();
      //  selectedPhoto = ss.getSessionSelectedPhoto();
             
        Bundle extras = getIntent().getExtras();
        selectedAlbumName = extras.getString("aname_e");
        selectedPhoto = extras.getString("pname_e");
        
        setContentView(R.layout.full_photo);
        
        final Button back = (Button)findViewById(R.id.back);
        final Button mailToFriend = (Button)findViewById(R.id.mailToFriend);
        viewDetails = (Button)findViewById(R.id.viewDetails);
        editDetails = (Button)findViewById(R.id.editDetails);
        setAsWallpaper = (Button)findViewById(R.id.setAsWallpaper);
		ImageView fullPhoto = (ImageView)findViewById(R.id.image_view);
		
		//options = new BitmapFactory.Options();
		//options.inSampleSize = 1;
		Bitmap bm;
		try {
			bm = decodeFile(new File(selectedPhoto));
			fullPhoto.setImageBitmap(bm);
			fullPhoto.setOnClickListener(new Button.OnClickListener() 
			{ public void onClick (View v)
				{
					if(flag==0)
					{
						flag=1;
						back.setVisibility(View.VISIBLE);
						mailToFriend.setVisibility(View.VISIBLE);
						viewDetails.setVisibility(View.VISIBLE);
						editDetails.setVisibility(View.VISIBLE);
						setAsWallpaper.setVisibility(View.VISIBLE);
					}
					else if(flag==1)
					{
						flag=0;
						back.setVisibility(View.GONE);
						mailToFriend.setVisibility(View.GONE);
						viewDetails.setVisibility(View.GONE);
						editDetails.setVisibility(View.GONE);
						setAsWallpaper.setVisibility(View.GONE);
					}
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	
		
		
		
        back.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
		//	if(db!=null)
				db.close();
System.gc();
				//ss.setSessionSelectedPhoto(selectedPhoto);
				Intent i = new Intent(FullPhoto.this, OpenAlbum.class);
				i.putExtra("aname_e", selectedAlbumName);
				startActivity(i);
finish();
			}
		});
		
        
        mailToFriend.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
			//if(db!=null)
				db.close();
			System.gc();
				//ss.setSessionSelectedPhoto(selectedPhoto);
				Intent i = new Intent(FullPhoto.this, SendMail.class);
				i.putExtra("aname_e", selectedAlbumName);
				i.putExtra("pname_e", selectedPhoto);
				startActivity(i);
finish();
			}
		});
        
        setAsWallpaper.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
				Bitmap bitmap = null;
				try {
					bitmap = decodeFile(new File(selectedPhoto));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			        try {
			        	if(bitmap!=null)
			        	{
			        		getApplicationContext().setWallpaper(bitmap);
			        	}
			        } catch (IOException e) {
			        e.printStackTrace();
			        }
			}
		});        
        
		viewDetails.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
				final Dialog dialog = new Dialog(FullPhoto.this);
				dialog.setContentView(R.layout.view_photo_details_dialog);
				dialog.setCancelable(true);

				// Get photo details from db.. and display..
				// Else display only photo link..
				TextView viewDetailsDialog = (TextView) dialog.findViewById(R.id.viewDetailsDialog);
				if((db.checkIfPhotoExist(selectedPhoto)) == 0)
				{
					viewDetailsDialog.setText("\nImage: "+selectedPhoto);
				}
				else
				{
					int pid;
					String imagePath1;
					String size = "Info not available";
					String country = "Info not available";
					String state = "Info not available";
					String city = "Info not available";
					String place = "Info not available";
					String area = "Info not available";
					String tag = "Info not available";
					String date_time = "Info not available";
					String frame = "Info not available";
					Cursor c = db.getPhoto(db.getPhotoId(selectedPhoto));
					if (c.moveToFirst())  
					{
						pid = c.getInt(0);
			    	 	imagePath1 = c.getString(1);
			    	 	if(!(c.getString(2).equals("")))
			    	 		size = c.getString(2);
			    	 	if(!(c.getString(3).equals("")))
			    	 		date_time = c.getString(3);
			    	 	if(!(c.getString(4).equals("")))	
			    	 		country = c.getString(4);
			    	 	if(!(c.getString(5).equals("")))
			    	 		state= c.getString(5);
			    	 	if(!(c.getString(6).equals("")))
			    	 		city = c.getString(6);
			    	 	if(!(c.getString(7).equals("")))
			    	 		area = c.getString(7);
			    	 	if(!(c.getString(8).equals("")))
			    	 		place = c.getString(8);
			    	 	if(!(c.getString(9).equals("")))
			    	 		tag = c.getString(9);
			    	 	if(!(c.getString(10).equals("")))
			    	 		frame = c.getString(10);
					}
				//	if(c!=null)
					c.close();
					
					viewDetailsDialog.setText("\nImage: "+selectedPhoto+
							"\nDate: "+date_time+
							"\nPlace: "+place+
							"\nArea: "+area+
							"\nCity: "+city+
							"\nState: "+state+
							"\nCountry: "+country+
							"\nTag: "+tag);
				
				}
				
				/*Button cancel = (Button) dialog.findViewById(R.id.cancel);
				cancel.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                        dialog.dismiss();
                    }
                });*/
				
				 Button ok = (Button) dialog.findViewById(R.id.ok);
				 ok.setOnClickListener(new OnClickListener() {

                 public void onClick(View v) {
                         dialog.dismiss();
                     }
                 });
                 dialog.show();
			}
		});
		
		
		editDetails.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
				db.close();
System.gc();
			//	ss.setSessionSelectedPhoto(selectedPhoto);
			//	ss.setSessionCurrentClass("FullPhoto");
				Intent i = new Intent(FullPhoto.this, EditPhotoOpenAlbum.class);
				i.putExtra("aname_e", selectedAlbumName);
				i.putExtra("pname_e", selectedPhoto);
				startActivity(i);
finish();
			}
		});
        
    }
    
    private Bitmap decodeFile(File f) throws IOException{
        Bitmap b = null;
        try {
        	
        	int IMAGE_MAX_SIZE = 300;
        	
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
        }
        return b;
    }

	
}
