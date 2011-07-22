package com.EFrame13;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class addPhotosOpenAlbum extends Activity{
	
	DBAdapter db = new DBAdapter(this); 
	//Session ss = new Session();
	String albumSelected="";
	Cursor cursor;
	int pos, k, total, i=0;
	Button done = null, search = null;
	 private int columnIndex;
	 EditText search_item = null;
	 TextView noOfPhotos = null;;
	 int j=0,l=0;
	 ArrayList<String> PhotoList = new ArrayList<String>();
	 ArrayList<String> PhotoList1 = new ArrayList<String>();
	 int search_flag=0;
	 private Runnable viewLocation;
	 private ProgressDialog m_ProgressDialog = null;
	 
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photos_open_album);
        
     // In order to do any transactions with database.. Need to open the database..
        db.open();
        
        // Get current album's name..
       // albumSelected = ss.getSessionAlbumName();
        
        Bundle extras = getIntent().getExtras();
        albumSelected = extras.getString("aname_e");
        
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener() 
		{ public void onClick (View v)
			{
			//if(db!=null)
				db.close();
			
			if(PhotoList.size()>0)
				PhotoList.clear();
			
			if(PhotoList1.size()>0)
				PhotoList1.clear();
				
				System.gc();	
			
				Intent i = new Intent(addPhotosOpenAlbum.this, OpenAlbum.class);
				i.putExtra("aname_e", albumSelected);
				startActivity(i);
				finish();
			}
		});
        
        done = (Button)findViewById(R.id.done);
        done.setOnClickListener(new Button.OnClickListener() 
    			{ public void onClick (View v)
    				{
    				k = db.getAlbumId(albumSelected);
    				int l = db.getFirstPhotoInAlbum(k);
    				if(l!=0)
    					db.updateAlbumAfterInsert(k, l);
    				
    			//	if(db!=null)
    					db.close();
    				
    				if(PhotoList.size()>0)
						PhotoList.clear();
					
					if(PhotoList1.size()>0)
						PhotoList1.clear();	
    				
				
    				Intent i = new Intent(addPhotosOpenAlbum.this, OpenAlbum.class);
    				i.putExtra("aname_e", albumSelected);
    		        startActivity(i);
    				}
    			});
        
        
        
        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new Button.OnClickListener() 
    			{ public void onClick (View v)
    				{
    					search_item = (EditText)findViewById(R.id.search_item);
    					String tag = search_item.getText().toString();
    					
    					if(!(tag.equals("")))
    						ConvertStringToArray(tag);
    					else
    						all_photos();
    				}
    			});
       
        all_photos();
    }

    private Bitmap decodeFile(File f) throws IOException{
        Bitmap b = null;
        try {
        	
        	int IMAGE_MAX_SIZE = 70;
        	
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
	Name: ConvertStringToArray
	Parameters: text entered for search by user (String)
	Return Type: -
	Date: 29/6/11
	Purpose: Convert the entered text into array of tags to be searched

*/
    void ConvertStringToArray(String tag)
	{
    	String str[] = new String[10];
    	
    	k=0;
    	j=0;
    	
		for(int i=0; i<tag.length(); i++)
		{
			if((tag.charAt(i) == ' ') && (i == 0))
			{
				while((tag.charAt(j) == ' ') || (tag.charAt(j) == ','))
				{
					j++;
				}
				i=j;
			}
			else if((tag.charAt(i) == ',') && (i == 0))
			{
				while((tag.charAt(j) == ' ') || (tag.charAt(j) == ','))
				{
					j++;
				}
				i=j;
			}
			else if(i == (tag.length()-1))
			{
				str[k] = tag.substring(j,i+1);
				k++;
			}
			else if(tag.charAt(i) == ',')
			{
					str[k] = tag.substring(j,i);
					k++;
				
				j=i+1;
				
				if((tag.charAt(j) == ' ') || (tag.charAt(j) == ','))
				{
					while((tag.charAt(j) == ' ') || (tag.charAt(j) == ','))
					{
						j++;
					}
				}
				i=j;
			}
			else if(tag.charAt(i) == ' ')
			{
					str[k] = tag.substring(j,i);
					k++;
				
				j=i+1;
				
				if((tag.charAt(j) == ' ') || (tag.charAt(j) == ','))
				{
					while((tag.charAt(j) == ' ') || (tag.charAt(j) == ','))
					{
						j++;
						
					}
				}
				i=j;
			}
		}
		
		String []str1 = new String[k];
		
		for(int i=0; i<str1.length; i++)
		{
			str1[i] = str[i];
		}
		
		search_Photos(str1);
	}

    /*
	Type: function
	Name: search_Photos
	Parameters: array of tags (String[])
	Return Type: -
	Date: 29/6/11
	Purpose: Get list of photos with the array of tags

*/    
    void search_Photos(String[] str)
    {
    	
    	search_flag=1;
    	if(PhotoList1.size()>0)
    		PhotoList1.clear();
    	
    	for(int i=0; i<PhotoList.size(); i++)
    	{
    		
    		String str5 = PhotoList.get(i);
    		String str5Lower = str5.toLowerCase();
    		for(int j=0; j<str.length; j++)
    		{
    			if(str5Lower.contains(str[j].toLowerCase()))
    				PhotoList1.add(str5);
       		}
    	}    	
    	
    	Cursor mCursor = db.getAndPhotoTag(str);
    	while(mCursor.moveToNext())
    	{
    		String str5 = mCursor.getString(0);
    		if(!(PhotoList1.contains(str5)))
    			PhotoList1.add(str5);
    		
    	}
    	//if(mCursor != null)
    		mCursor.close();
    	
    	noOfPhotos = (TextView)findViewById(R.id.noOfPhotos);
    	if(search_flag == 1)
    		noOfPhotos.setText("Add Photos["+PhotoList1.size()+"]");
    	else
    		noOfPhotos.setText("Add Photos["+PhotoList.size()+"]");
    	
    	GridView sdcardImages = (GridView) findViewById(R.id.sdcard);
        sdcardImages.setAdapter(new ImageAdapter(this));
        
        viewLocation = new Runnable(){
            public void run() {
                getLocation();
            }
        };
        Thread thread =  new Thread(null, viewLocation, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(addPhotosOpenAlbum.this,    
              "Elite PictureFrame", "Searching photos on device..", true);
    }
    
    /*
	Type: function
	Name: all_photos
	Parameters: -
	Return Type: -
	Date: 29/6/11
	Purpose: Get list of photos on SD Card 

*/    
    void all_photos()
    {
    	
    	try
        {
    		//Fire query in order to get List of all photos on SD Card
        	 String[] projection1 = {MediaStore.Images.Media.DATA};
             cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                     projection1, 
                     null,       
                     null,
                     MediaStore.Images.Thumbnails._ID);
             
             // Add those photos to Out ArrayList
        	for(int position=0; position<cursor.getCount(); position++)
        	{
        		columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        		cursor.moveToPosition(position);
        		PhotoList.add(cursor.getString(columnIndex));
        		
        	}
        	
       // 	if(cursor != null)
        		cursor.close();
        
        noOfPhotos = (TextView)findViewById(R.id.noOfPhotos);
        noOfPhotos.setText("Edit Photos["+PhotoList.size()+"]");
        
        // Attach retrieved data to the grid view
        GridView sdcardImages = (GridView) findViewById(R.id.sdcard);
        sdcardImages.setAdapter(new ImageAdapter(this));
        
        viewLocation = new Runnable(){
            public void run() {
                getLocation();
            }
        };
        Thread thread =  new Thread(null, viewLocation, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(addPhotosOpenAlbum.this,    
              "Elite PictureFrame", "Searching photos on device..", true);
        
        }
    	catch(Exception e)
    	{
    		Toast toast = Toast.makeText(addPhotosOpenAlbum.this, 
            		"Count: "+total+"\nProblem in creating photos list....",
            		Toast.LENGTH_LONG);
            toast.show();
    	}
    }

    // This function is used to provide delay... and nothing else...
    void getLocation()
    {
    	try
    	{
    		Thread.sleep(13000);
    	}
        catch (Exception e) 
        {
        	Log.e("BACKGROUND_PROC", e.getMessage());
        }
    	runOnUiThread(returnRes);
    	
    }

    
    private Runnable returnRes = new Runnable() {

        public void run() {
            m_ProgressDialog.dismiss();
            
            
            
        }
    };
    
    // Class to manage the view - photo with its details in grid view
    
    private class ImageAdapter extends BaseAdapter {

        private Context context;

        public ImageAdapter(Context localContext) {
            context = localContext;
        }

        public int getCount() {
        	if(search_flag == 1)
        		return PhotoList1.size();
        	else
        		return PhotoList.size();
        }
        
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
        	
        	View v;
            
                LayoutInflater li = getLayoutInflater();
                v = li.inflate(R.layout.add_photos_open_album_row, null);
                  try
                  {
                final String image;
                
                // Take Photo one by one from ArrayList and attach...
                if(search_flag == 1)
                	image = PhotoList1.get(position);
                else
                	image = PhotoList.get(position);  
                
                ImageView iv = (ImageView)v.findViewById(R.id.icon_image1);
                Bitmap bMap = decodeFile(new File(image));
                if(bMap!=null)	     
                {
                	//Bitmap newImage = Bitmap.createScaledBitmap(bMap, 80, 80, true);
                    iv.setImageBitmap(bMap);
                }
                else
                {
                	iv.setImageResource(R.drawable.icon);
                }

                iv.setOnClickListener(new Button.OnClickListener() 
    			{ public void onClick (View v)
    				{
    				
    				//ss.setSessionSelectedPhoto(image);
        			
    			//	if(db!=null)
    					db.close();
    				
    				if(PhotoList.size()>0)
						PhotoList.clear();
					
					if(PhotoList1.size()>0)
						PhotoList1.clear();	
    				System.gc();
									
    				Intent i = new Intent(addPhotosOpenAlbum.this, FullPhotoTag.class);
    				i.putExtra("pname_e", image);
    				i.putExtra("aname_e", albumSelected);
    				startActivity(i);
    				finish();
    				}
    			});
                
                final CheckBox check1 = (CheckBox)v.findViewById(R.id.check1);
                
                String date_time="";
                String place="";
                
                // Add details of photo to the grid view
                if((db.checkIfPhotoExist(image)) != 0)
                {	
                	Cursor c = db.getPhoto(db.getPhotoId(image));
                	if (c.moveToFirst())  
                	{
                		if(!(c.getString(3).equals("")))
                			date_time=c.getString(3);
                		                		
                		if(!(c.getString(8).equals("")))
                			place=c.getString(8);
                		
                		if(!(c.getString(7).equals("")))
                		{
                			if(!(place.equals("")))
                				place=place + ","+c.getString(7);
                			else
                				place=c.getString(7);
                		}
                		
                		if(!(c.getString(6).equals("")))
                		{
                			if(!(place.equals("")))
                				place=place + ","+c.getString(6);
                			else
                				place=c.getString(6);
                		}
                		
                		if(!(c.getString(5).equals("")))
                		{
                			if(!(place.equals("")))
                				place=place + ","+c.getString(5);
                			else
                				place=c.getString(5);
                		}
                		
                		if(!(c.getString(4).equals("")))
                		{
                			if(!(place.equals("")))
                				place=place + ","+c.getString(4);
                			else
                				place=c.getString(4);
                		}
                	}
                	
        //        	if(c!=null)
                		c.close();
                	
                	if((!(date_time.equals(""))) && (!(place.equals(""))))
                		check1.setText("Details:\nDate: "+date_time+"\nPlace: "+place);
                	else if((date_time.equals("")) && (!(place.equals(""))))
                		check1.setText("Details:\nPlace: "+place);
                	else if((!(date_time.equals(""))) && (place.equals("")))
                		check1.setText("Details:\nDate: "+date_time);
                	else
                		check1.setText("Details:\nImage: "+image);
                }
                else
                {
                	check1.setText("Details:\nImage: "+image);
                }
                check1.setId(position);
                
                check1.setOnCheckedChangeListener(new OnCheckedChangeListener()
                {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                    	String image1;
                    	if(search_flag == 1)
                    		image1 = PhotoList1.get(check1.getId());
                    	else 
                    		image1 = PhotoList.get(check1.getId());
                    	
                        if ( isChecked )
                        {
                        	//If check box is checked..
                        	//Insert query fired to add photo to album..
                        	Calendar calendar = Calendar.getInstance();
                        	java.util.Date now = calendar.getTime();
                       	  	java.sql.Timestamp ti = new java.sql.Timestamp(now.getTime());

                       	 	if((db.checkIfPhotoExist(image1)) == 0)
                       	 		db.insertPhoto(image1, "60MB",ti.toString(), "","","", "", "","", "");
                        	
                       	 	int j = db.getPhotoId(image1);
                        	k = db.getAlbumId(albumSelected);
                        	
                        	if((db.checkPhotoExistInAlbum(k, j)) == 0)
                        		db.insertAlbum_photo(k, j);
							                        	
                        }
                        else
                        {
                        	//If check box is unchecked..
                        	//Insert query fired to remove photo from album..
                        	int j = db.getPhotoId(image1);
                        	k = db.getAlbumId(albumSelected);
                        	
                        	if((db.checkPhotoExistInAlbum(k, j)) == 0)
                        		db.deletePhotoFromAlbum(j, k);
							                        	
                        }
                    }
                });
        }
    	catch(Exception e)
    	{
    		Toast toast = Toast.makeText(addPhotosOpenAlbum.this, 
            		"\nProblem in attaching photos....\nImage: "+position,
            		Toast.LENGTH_LONG);
            toast.show();
    	}
          
            return v;
            
        }
    }

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
