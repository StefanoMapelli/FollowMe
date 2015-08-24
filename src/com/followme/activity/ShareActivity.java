package com.followme.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.followme.activity.R;
import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.Utils;
import com.followme.object.Contact;
import com.followme.object.Media;
import com.followme.object.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem; 
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ShareActivity extends ActionBarActivity {

	private Contact[] contacts = null;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location location = null;
	private Position lastPosition = null;
	private ParseObject path;
	private int counter;
	private GoogleMap map;
	private Button photoButton;
	private Button videoButton;
	private String fileName;
	private ArrayList<Media> photos = new ArrayList<Media>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("CREATE", "FATTA CREATE");
		setContentView(R.layout.share_layout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.shareMap)).getMap();
		photoButton = (Button) findViewById(R.id.photoButton);
		videoButton = (Button) findViewById(R.id.videoButton);
		
		if (savedInstanceState != null)
			  fileName = savedInstanceState.getString("fileName");
		
		counter = 0;
		Object[] objects = (Object[]) getIntent().getSerializableExtra("selectedContacts");
		contacts = new Contact[objects.length];
		
		//cast from object to contact
		for(int i=0; i < objects.length; i++)
		{
			contacts[i] = (Contact) objects[i];
		}
		
		//create a path and insert requests for the users receivers for that path
		String userId = getIntent().getStringExtra("userId");
		String pathId = ParseManager.insertPath(this);
		path = ParseManager.getPathbyId(this, pathId);
		for(int i=0; i<contacts.length; i++)
		{
			ParseManager.insertRequest(this, "condivisione", userId, contacts[i].getId(), pathId, null, null);
		}
		
		if (Utils.displayGpsStatus(this)) 
		{
			locationListener = new MyLocationListener(); 
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager  
		    .GPS_PROVIDER, 5000, 10,locationListener); 
			
			location = locationManager.getLastKnownLocation(LocationManager  
		    .GPS_PROVIDER);
			
			if(location != null)
			{
				Log.i("GPS", "FIRST LOCATION");
				String id = ParseManager.insertPosition(ShareActivity.this, path, location.getLatitude(), location.getLongitude(), counter);
				 
				lastPosition = new Position(location.getLatitude(),location.getLongitude(), counter);
				lastPosition.setId(id);
				 
				counter++;
				 
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(location.getLatitude(),location.getLongitude()))
				.zoom(18)
				.bearing(0)           
				.tilt(0)             
				.build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
			
			map.setMyLocationEnabled(true);
		} 
		else
		{
			Log.i("GPS", "gps not enabled");
		}
		
		photoButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{			
				Date dt = new Date();
				File tempFile = new File(Environment.getExternalStorageDirectory(),
	                      dt.toString()+".jpg");
				fileName = tempFile.getAbsolutePath();
				Uri uri = Uri.fromFile(tempFile);
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent,0);
			}
			
		});
		
		videoButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//intent per l'attivit� di video
				File tempFile = null;
				try {
					tempFile = File.createTempFile("my_video", ".mp4");
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileName = tempFile.getAbsolutePath();
				Uri uri = Uri.fromFile(tempFile);
				Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent,1);
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    //ACTIVITY PHOTO
		if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent	        					
				Intent intent = new Intent(ShareActivity.this, PhotoInsertActivity.class);
				intent.putExtra("position",lastPosition);
				intent.putExtra("fileName", fileName);	        	
				startActivityForResult(intent,2);
	        }
	    }
		//ACTIVITY VIDEO
	    if (requestCode == 1) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	        	Uri videoUri = data.getData();
	        	InputStream iStream=null;
				try {
					iStream = getContentResolver().openInputStream(videoUri);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	        	byte[] inputData=null;
				try {
					inputData = IOUtils.toByteArray(iStream); 
				} catch (IOException e) {
					e.printStackTrace();
				}
	        		
	        	String encodedVideo = Base64.encodeToString(inputData, Base64.DEFAULT);	        	
	        }
	    }
	    //ACTIVITY PHOTOINSERT
	    if (requestCode == 2) {
	        if (resultCode == RESULT_OK)
	        {  	
				byte[] image = Utils.getSmallBitmap(fileName).toByteArray();
				
				File file = new File(fileName);
				file.delete();
				
	    		Media photo = new Media(image, data.getStringExtra("title"), lastPosition);
	        	ParseManager.insertPhoto(this, photo);
	        	photos.add(photo);
	        }
	    }
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle)
	{
	 super.onSaveInstanceState(bundle);
	 bundle.putString("fileName", fileName);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*----------Listener class to get coordinates ------------- */  
	 private class MyLocationListener implements LocationListener 
	 {  
		 @Override  
	     public void onLocationChanged(Location loc) 	     
		 {  				 
			 if((Math.abs(location.getLongitude() - loc.getLongitude()) > 0.00001 ||
				  Math.abs(location.getLatitude() - loc.getLatitude()) > 0.00001) &&
				  loc.getSpeed() > 1 && loc.getAccuracy() < 50)
				 {
					Toast.makeText(ShareActivity.this, "speed: "+loc.getSpeed()+" accuracy: "+loc.getAccuracy(), Toast.LENGTH_LONG).show();
					Log.i("GPS", "LOCATION FOUND");
					    
					String id = ParseManager.insertPosition(ShareActivity.this, path, loc.getLatitude(), loc.getLongitude(), counter);
					    
					lastPosition = new Position(location.getLatitude(),location.getLongitude(), counter);
					lastPosition.setId(id);
					    
					counter++;
					    
					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(loc.getLatitude(),loc.getLongitude()))
					.zoom(18)
					.bearing(0)           
					.tilt(0)             
					.build();
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
					MapManager.drawPrimaryLinePath(
							new LatLng(location.getLatitude(),location.getLongitude()) ,
							new LatLng(loc.getLatitude(),loc.getLongitude()),
							map);
						
					location = loc;
				} 
	     }
	          
	     @Override  
	     public void onProviderDisabled(String provider) {  
	            // TODO Auto-generated method stub           
	     }  
	  
	     @Override  
	     public void onProviderEnabled(String provider) {  
	            // TODO Auto-generated method stub           
	     }  
	  
	     @Override  
	     public void onStatusChanged(String provider,   
	     		int status, Bundle extras) {  
	            // TODO Auto-generated method stub           
	     }  
	 }
}
