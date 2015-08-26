package com.followme.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.Marker;
import com.followme.activity.R;
import com.followme.adapter.MapWrapperLayout;
import com.followme.adapter.OnInfoWindowElemTouchListener;
import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.Utils;
import com.followme.object.Contact;
import com.followme.object.Media;
import com.followme.object.PhotoMarker;
import com.followme.object.Position;
import com.followme.object.VideoMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem; 
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class ShareActivity extends ActionBarActivity {

	private Contact[] contacts = null;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location location = null;
	private Position lastPosition = null;
	private ParseObject path;
	private int positionCounter;
	private int markerCounter;
	private GoogleMap map;
    private MapWrapperLayout mapWrapperLayout;
	private ViewGroup infoWindow;
    private TextView infoSnippet;
    private ImageButton infoImageButton;
    private VideoView infoVideoView;
    private OnInfoWindowElemTouchListener infoButtonListener;	
	private Button photoButton;
	private Button videoButton;
	private String photoFileName;
	private Uri videoUri;
	private ArrayList<PhotoMarker> photoMarkers = new ArrayList<PhotoMarker>();
	private ArrayList<VideoMarker> videoMarkers = new ArrayList<VideoMarker>();
	private ArrayList<Media> photos = new ArrayList<Media>();
	private ArrayList<Media> videos = new ArrayList<Media>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("CREATE", "FATTA CREATE");
		setContentView(R.layout.share_layout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.shareMap)).getMap();
		photoButton = (Button) findViewById(R.id.photoButton);
		videoButton = (Button) findViewById(R.id.videoButton);
		mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_linear_layout);
	     
		
		if (savedInstanceState != null)
			  photoFileName = savedInstanceState.getString("fileName");
		
		positionCounter = 0;
		markerCounter = 0;
		
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
			
			location = MapManager.getLastKnownLocation(this, locationManager);
			
			if(location != null)
			{
				Log.i("GPS", "FIRST LOCATION");
				String id = ParseManager.insertPosition(ShareActivity.this, path, location.getLatitude(), location.getLongitude(), positionCounter);
				 
				lastPosition = new Position(location.getLatitude(),location.getLongitude(), positionCounter);
				lastPosition.setId(id);
				 
				positionCounter++;
				 
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
				if(location != null)
				{
					Date dt = new Date();
					File tempFile = new File(Environment.getExternalStorageDirectory(),
		                      dt.toString()+".jpg");
					photoFileName = tempFile.getAbsolutePath();
					Uri uri = Uri.fromFile(tempFile);
					
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent,0);
				}
			}
			
		});
		
		videoButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(location != null)
				{
					//intent per l'attività di video
					Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
					intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10485760L);
					startActivityForResult(intent,1);
				}
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
				intent.putExtra("fileName", photoFileName);	        	
				startActivityForResult(intent,2);
	        }
	    }
		//ACTIVITY VIDEO
	    if (requestCode == 1) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	        	videoUri = data.getData();				
				Intent intent = new Intent(ShareActivity.this, VideoInsertActivity.class);
				intent.putExtra("imageUri", videoUri.toString());	
				startActivityForResult(intent,3);
	        }
	    }
	    //ACTIVITY PHOTOINSERT
	    if (requestCode == 2) {
	        if (resultCode == RESULT_OK)
	        {  	
	        	//obtain byte array from scaled compressed bitmap 
	    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    		final Bitmap imageBitMap = Utils.getSmallBitmap(photoFileName);
	    		imageBitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] image = stream.toByteArray();
				
				//create media and insert in parse and in photos
	    		Media photo = new Media(image, data.getStringExtra("title"), lastPosition);
	        	ParseManager.insertPhoto(this, photo);
	        	photos.add(photo);
	    		        
	        	//add marker
	        	Marker m = map.addMarker(new MarkerOptions()
	            .position(new LatLng(photo.getPosition().getLatitude(),
	            					 photo.getPosition().getLongitude()))
	            .snippet(photo.getTitle())
	            .title(String.valueOf(markerCounter)));
		        map.getUiSettings().setMapToolbarEnabled(false);
		        photoMarkers.add(new PhotoMarker(imageBitMap, m));  
	        	markerCounter++;
		        		        
		        //info window setting
		        map.setInfoWindowAdapter(new InfoWindowAdapter() {
		            @Override
		            public View getInfoWindow(Marker marker) {
		                return null;
		            }

		            @Override
		            public View getInfoContents(Marker marker) {
						// MapWrapperLayout initialization
				        mapWrapperLayout.init(map, Utils.getPixelsFromDp(ShareActivity.this, 59)); 				
				        infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.photo_info_window_layout, null);
				        infoSnippet = (TextView)infoWindow.findViewById(R.id.PhotoSnippet);
				        infoImageButton = (ImageButton)infoWindow.findViewById(R.id.photoInfoButton);
		            				        
				        //image setting
				        infoImageButton.setImageBitmap(Bitmap.createScaledBitmap(
				        		Utils.getBitmapOfPhotoMarker(photoMarkers, marker.getTitle()), 
								imageBitMap.getWidth()/2, 
								imageBitMap.getHeight()/2, false));
				        Utils.rotateImageView(infoImageButton, photoFileName);
				        
				        //info button setting
				        infoButtonListener = new OnInfoWindowElemTouchListener(infoImageButton) 
				        {
				            @Override
				            protected void onClickConfirmed(View v, Marker marker) {
				            	//APRI GALLERIA FOTO
				                // Here we can perform some action triggered after clicking the button
				                Toast.makeText(ShareActivity.this, "button clicked!", Toast.LENGTH_SHORT).show();
				            }
				        }; 
				        infoImageButton.setOnTouchListener(infoButtonListener);
		                
		                // Setting up the infoWindow with current's marker info
		                infoSnippet.setText(marker.getSnippet());
		                infoButtonListener.setMarker(marker);
				        
		                // We must call this to set the current marker and infoWindow references
		                // to the MapWrapperLayout
		                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
		                return infoWindow;
		            }
		        });
		       	        	
	        	//delete file from memory
				File file = new File(photoFileName);
				file.delete();
	        }
	    }
	    //ACTIVITY VIDEOINSERT
	    if (requestCode == 3) {
	        if (resultCode == RESULT_OK)
	        {  	
	        	InputStream iStream=null;
				try {
					iStream = getContentResolver().openInputStream(videoUri);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	        	byte[] inputData=null;
				try {
					inputData = Utils.getBytes(iStream); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Media video = new Media(inputData, data.getStringExtra("title"), lastPosition);
				ParseManager.insertVideo(this, video);
				videos.add(video);
				
				//add marker
	        	Marker m = map.addMarker(new MarkerOptions()
	            .position(new LatLng(video.getPosition().getLatitude(),
	            					 video.getPosition().getLongitude()))
	            .snippet(video.getTitle())
	            .title(String.valueOf(markerCounter)));
		        map.getUiSettings().setMapToolbarEnabled(false);
		        videoMarkers.add(new VideoMarker(videoUri, m));  
	        	markerCounter++;;
		        map.getUiSettings().setMapToolbarEnabled(false);
		        
		        //info window setting
		        map.setInfoWindowAdapter(new InfoWindowAdapter() {
		            @Override
		            public View getInfoWindow(Marker marker) {
		                return null;
		            }

		            @Override
		            public View getInfoContents(Marker marker) {				        		                
		            	// MapWrapperLayout initialization
				        mapWrapperLayout.init(map, Utils.getPixelsFromDp(ShareActivity.this, 39 + 20)); 				
				        infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.video_info_window_layout, null);
				        infoSnippet = (TextView)infoWindow.findViewById(R.id.VideoSnippet);
				        infoVideoView = (VideoView)infoWindow.findViewById(R.id.infoVideoView);
				        
				        //setting videoView
				        infoVideoView.setVideoURI(Utils.getUriOfVideoMarker(videoMarkers, marker.getTitle()));
				        
				        //setting media controller
						MediaController mc = new MediaController(ShareActivity.this);
						infoVideoView.setMediaController(mc);
				        mc.setAnchorView(infoVideoView);
				        
				         //setting dimensions  
				        DisplayMetrics dm=new DisplayMetrics();            
				        getWindowManager().getDefaultDisplay().getMetrics(dm);
				        int width=dm.widthPixels;
				        infoVideoView.setMinimumWidth(width);
				        
				        //start video
				        infoVideoView.start();

				      //info button setting
				       /* infoButtonListener = new OnInfoWindowElemTouchListener(infoImageButton) 
				        {
				            @Override
				            protected void onClickConfirmed(View v, Marker marker) {
				            	//APRI GALLERIA VIDEO
				                // Here we can perform some action triggered after clicking the button
				                Toast.makeText(ShareActivity.this, "button clicked!", Toast.LENGTH_SHORT).show();
				            }
				        }; 
				        infoImageButton.setOnTouchListener(infoButtonListener);*/				        
				        
		                // Setting up the infoWindow with current's marker info
		                infoSnippet.setText(marker.getSnippet());
		                //infoButtonListener.setMarker(marker);
				        
		                // We must call this to set the current marker and infoWindow references
		                // to the MapWrapperLayout
		                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
		                return infoWindow;
		            }
		        });
	        }
	    }
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle)
	{
	 super.onSaveInstanceState(bundle);
	 bundle.putString("fileName", photoFileName);
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
			 if(location == null)
				{
					Log.i("GPS", "FIRST LOCATION");
					String id = ParseManager.insertPosition(ShareActivity.this, path, loc.getLatitude(), loc.getLongitude(), positionCounter);
					 
					lastPosition = new Position(loc.getLatitude(),loc.getLongitude(), positionCounter);
					lastPosition.setId(id);
					 
					positionCounter++;
					 
					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(loc.getLatitude(),loc.getLongitude()))
					.zoom(18)
					.bearing(0)           
					.tilt(0)             
					.build();
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			 else
			 if((Math.abs(location.getLongitude() - loc.getLongitude()) > 0.00001 ||
				  Math.abs(location.getLatitude() - loc.getLatitude()) > 0.00001) &&
				  loc.getSpeed() > 1 && loc.getAccuracy() < 50)
				 {
					Toast.makeText(ShareActivity.this, "speed: "+loc.getSpeed()+" accuracy: "+loc.getAccuracy(), Toast.LENGTH_LONG).show();
					Log.i("GPS", "LOCATION FOUND");
					    
					String id = ParseManager.insertPosition(ShareActivity.this, path, loc.getLatitude(), loc.getLongitude(), positionCounter);
					    
					lastPosition = new Position(location.getLatitude(),location.getLongitude(), positionCounter);
					lastPosition.setId(id);
					    
					positionCounter++;
					    
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
