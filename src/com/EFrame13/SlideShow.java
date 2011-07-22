package com.EFrame13;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SlideShow extends Activity{
	
	String selectedAlbumName;
	String photosInSelectedAlbum[]=null;
	ImageView jpgView=null;
	DBAdapter db = new DBAdapter(this);
	//Session ss = new Session();
	private Handler handler = new Handler();
	 int i=0;
	int time = 3000;
	String music="";
	MediaPlayer mMediaPlayer = new MediaPlayer();
	int flag=0;
	int current_i;
	int length;
	Button viewDetails=null;
	Button editDetails=null;
	Button setAsWallpaper=null;
	int coming_back=0;
	Button mailToFriend=null;
	int flag1 =0;
	Bitmap bm;
	int noOfPhotos;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow);
        
     // In order to do any transactions with database.. Need to open the database..
        db.open();
         
        // Get selected Album..
        //selectedAlbumName = ss.getSessionAlbumName();
        
        Bundle extras = getIntent().getExtras();
        selectedAlbumName = extras.getString("aname_e");
        
        System.out.println("Album: "+selectedAlbumName);
        
        // Get Album id..
        int aid = db.getAlbumId(selectedAlbumName);
        System.out.println("Album ID: "+aid);
        
        // Get slideshow time transition..
        time = db.getSlideshowTime(aid);
        System.out.println("Album time: "+time);
        // Get music for that album...
        music = db.getMusicLink(aid);
        System.out.println("Album music: "+music);
        // Get photos in selected album...
        photosInSelectedAlbum = db.getphotoLocationOfAlbum(selectedAlbumName);
        System.out.println("Photos count: "+photosInSelectedAlbum.length);
        
        jpgView = (ImageView)findViewById(R.id.jpgview);
        viewDetails = (Button)findViewById(R.id.viewDetails);
        setAsWallpaper = (Button)findViewById(R.id.setAsWallpaper);
          	
        noOfPhotos = db.getnoOfPhotos(selectedAlbumName);
        
        startSlideShow();
        
        jpgView.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
				// when tapped on photo for the once.. Buttons are displyed... and slide show is paused..
			// music is paused..
				if(flag==0)
				{
					flag=1;
					viewDetails.setVisibility(View.VISIBLE);
					setAsWallpaper.setVisibility(View.VISIBLE);
					
					if(mMediaPlayer.isPlaying())
					{
						mMediaPlayer.pause();
						length=mMediaPlayer.getCurrentPosition();
					}
					
				}
				// when tapped on photo for the next time.. Buttons are diappeared and slide show continues..
				//music also continues
				else if(flag==1)
				{
					flag=0;
					
					viewDetails.setVisibility(View.GONE);
					setAsWallpaper.setVisibility(View.GONE);
					
					if(!(music.equals("")))
					{
						mMediaPlayer.seekTo(length);
						mMediaPlayer.start();
					}
					startSlideShow();
					
				}
			}
		});
               
        setAsWallpaper.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
				//bm = BitmapFactory.decodeFile(photosInSelectedAlbum[i-1]);

			        try {
			        	bm =	decodeFile(new File(photosInSelectedAlbum[i-1]));
			        getApplicationContext().setWallpaper(bm);
			        } catch (IOException e) {
			        e.printStackTrace();
			        }
			        
			}
		});        
        
		
		viewDetails.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
				final Dialog dialog = new Dialog(SlideShow.this);
				dialog.setContentView(R.layout.view_photo_details_dialog);
				dialog.setTitle("         		Photo Details");
				dialog.setCancelable(true);
				
				String photo = photosInSelectedAlbum[i-1];

				// Get photo details from db.. if photo is in db...
				
				TextView viewDetailsDialog = (TextView) dialog.findViewById(R.id.viewDetailsDialog);
				if((db.checkIfPhotoExist(photo)) == 0)
				{
					viewDetailsDialog.setText("\nImage: "+photosInSelectedAlbum[i]);
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
					Cursor c = db.getPhoto(db.getPhotoId(photo));
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
					
					viewDetailsDialog.setText("\nImage: "+photo+
							"\nDate: "+date_time+
							"\nPlace: "+place+
							"\nArea: "+area+
							"\nCity: "+city+
							"\nState: "+state+
							"\nCountry: "+country+
							"\nTag: "+tag);
				}
				
				 Button ok = (Button) dialog.findViewById(R.id.ok);
				 ok.setOnClickListener(new OnClickListener() {

                 public void onClick(View v) {
                         dialog.dismiss();
                     }
                 });
                 dialog.show();
			}
		});
		
		
	
    }
    
    private Bitmap decodeFile(File f) throws IOException{
        Bitmap b = null;
        try {
        	
        	int IMAGE_MAX_SIZE = 250;
        	
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
    
    /*
	Type: function
	Name: startSlideShow
	Parameters: -
	Return Type: -
	Date: 29/6/11
	Purpose: Here actual slide show is shown...

*/
    void startSlideShow()
    {
    	 final Runnable r = new Runnable()
         {
    		 
             public void run() 
             {
            	 
            	 try 
            	 {
            		 // Media player started.. background music starts...
               	  	  mMediaPlayer.setDataSource(music);
               		  mMediaPlayer.prepare();
               		  mMediaPlayer.start();
                 	               	 
				 } catch (Exception e) {

					}
            	 
                  
                  // Always check for no. of photos remaining in the slide show...
             	if(i<noOfPhotos)
             	{
             		
             		if(flag == 0)
             		{
             			
             		
             		try {
						bm = decodeFile(new File(photosInSelectedAlbum[i]));
						if(bm!=null)		                
	             			jpgView.setImageBitmap(bm);
	 	                else
	 	                	jpgView.setImageResource(R.drawable.moved_photo);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
             		
 	                
             		
             		
             		handler.postDelayed(this, time);
             		
             		i++;
             		}
             		
             		
             	}  
             	else
             	{
             		i=0;
             		flag=0;
             		
             		// If 0 photos left..
             		// music stops..
             		if (mMediaPlayer.isPlaying()) {
             	      	 mMediaPlayer.stop();
             	      	mMediaPlayer.release();  
             	      }
             		
             		  
             		
             		db.close();
             		
             		System.gc();
             		Intent i1 = new Intent(SlideShow.this, OpenAlbum.class);
             		i1.putExtra("aname_e", selectedAlbumName);
     				startActivity(i1);
     				System.out.println("Finish...");
    				finish();
                 }
             }
              
         };
       
         handler.postDelayed(r, 2000); 
         
    }
   
   
}
