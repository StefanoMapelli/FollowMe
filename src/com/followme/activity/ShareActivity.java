package com.followme.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.android.gms.maps.model.Marker;
import com.followme.activity.R;
import com.followme.adapter.MapWrapperLayout;
import com.followme.adapter.OnInfoWindowElemTouchListener;
import com.followme.fragment.SavePathDialogFragment;
import com.followme.fragment.SavePathDialogFragment.SavePathDialogListener;
import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.manager.Utils;
import com.followme.object.Contact;
import com.followme.object.CustomMarker;
import com.followme.object.Media;
import com.followme.object.Path;
import com.followme.object.PhotoMarker;
import com.followme.object.Position;
import com.followme.object.VideoMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem; 
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends ActionBarActivity implements SavePathDialogListener {

	private Contact[] contacts = null;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location location = null;
	private Position lastPosition = null;
	private ParseObject pathParseObject;
	private Path path;
	private int positionCounter;
	private int counterPhoto;
	private int counterVideo;
	private int markerCounter;
	private GoogleMap map;
	private MapWrapperLayout mapWrapperLayout;
	private ViewGroup infoWindow;
	private TextView infoSnippet;
	private ImageView infoImageView;
	private OnInfoWindowElemTouchListener photoTouchListener;	
	private OnInfoWindowElemTouchListener videoTouchListener;
	private String photoFileName;
	private String videoFileName;
	private Uri videoUri;
	private ArrayList<CustomMarker> markers = new ArrayList<CustomMarker>();
	private ArrayList<Media> photos = new ArrayList<Media>();
	private ArrayList<Media> videos = new ArrayList<Media>();
	private ArrayList<Position> positionList=new ArrayList<Position>();
	private int finishMode=1;  
	private ArrayList<String> requestIdList=new ArrayList<String>();
	private CheckRequestShare checkRequestThread;
	private Handler handler;
	private boolean pausedForGPS=false;
	private boolean dialogShow=false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_layout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.shareMap)).getMap();
		mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_linear_layout);

		handler=new Handler();	     

		if (savedInstanceState != null)
			photoFileName = savedInstanceState.getString("fileName");

		positionCounter = 0;
		counterPhoto = 0;
		counterVideo = 0;
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
		if(pathId == null)
		{
			Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
		}
		else
		{
			pathParseObject = ParseManager.getPathbyId(this, pathId);
			if(pathParseObject == null)
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
			else
			{
				path = new Path(pathParseObject.getObjectId(), "", "");
				for(int i=0; i<contacts.length; i++)
				{
					String outcomeId = ParseManager.insertRequest(this, "condivisione", userId, contacts[i].getId(), pathId, null, null);
					if(outcomeId == null)
					{
						Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
						requestIdList.add(outcomeId);
					}
				}

				checkRequestThread=new CheckRequestShare();
				
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
						String id = ParseManager.insertPosition(ShareActivity.this, pathParseObject, location.getLatitude(), location.getLongitude(), positionCounter);
						
						if(id == null)
						{
							Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
						}
						else
						{
							lastPosition = new Position(location.getLatitude(),location.getLongitude(), positionCounter);
							lastPosition.setId(id);

							positionList.add(lastPosition);

							positionCounter++;

							CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(location.getLatitude(),location.getLongitude()))
							.zoom(18)
							.bearing(0)           
							.tilt(0)             
							.build();
							map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						}
					}
					else
					{
						Toast.makeText(ShareActivity.this, "Searching for a valid location...",Toast.LENGTH_LONG).show();
					}

					map.setMyLocationEnabled(true);
					
					checkRequestThread=new CheckRequestShare();
					checkRequestThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} 
				else
				{
					new AlertDialog.Builder(this)
					.setTitle("Attention")
					.setMessage("Your GPS is not enabled. Please enable it now!")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{ 
							pausedForGPS=true;
							ShareActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));				    
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
					dialogShow=true;
				}

				photoTouchListener = new OnInfoWindowElemTouchListener() 
				{
					@Override
					protected void onClickConfirmed(View v, Marker marker) 
					{              
						//intent per l'attività di gallery
						Intent intent = new Intent(ShareActivity.this,MediaGalleryActivity.class);
						//passaggio parametri all'intent
						intent.putExtra("media", markers.toArray());

						int index = markers.indexOf(Utils.getMarkerByTitle(markers, marker.getTitle()));

						intent.putExtra("index", index);
						startActivity(intent);
					}
				};  

				videoTouchListener = new OnInfoWindowElemTouchListener() 
				{
					@Override
					protected void onClickConfirmed(View v, Marker marker) 
					{             
						//intent per l'attività di gallery
						Intent intent = new Intent(ShareActivity.this,MediaGalleryActivity.class);
						//passaggio parametri all'intent
						intent.putExtra("media", markers.toArray());

						int index = markers.indexOf(Utils.getMarkerByTitle(markers, marker.getTitle()));

						intent.putExtra("index", index);
						startActivity(intent);
					}
				}; 

				//info window setting
				map.setInfoWindowAdapter(new InfoWindowAdapter() {
					@Override
					public View getInfoWindow(Marker marker) {
						return null;
					}

					@Override
					public View getInfoContents(Marker marker) {
						if(marker.getTitle().startsWith("photo"))
						{
							// MapWrapperLayout initialization
							mapWrapperLayout.init(map, Utils.getPixelsFromDp(ShareActivity.this, 59)); 				
							infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.photo_info_window_layout, null);
							infoSnippet = (TextView)infoWindow.findViewById(R.id.PhotoSnippet);
							infoImageView = (ImageView)infoWindow.findViewById(R.id.infoImageView);
							photoTouchListener.setView(infoImageView);

							// Setting up the infoWindow with current's marker info
							infoSnippet.setText(marker.getSnippet());

							photoTouchListener.setMarker(marker);					        
							infoImageView.setOnTouchListener(photoTouchListener);		

							//image setting
							PhotoMarker pm = (PhotoMarker)Utils.getMarkerByTitle(markers, marker.getTitle());
							Bitmap bitmap = Utils.getSmallBitmap(pm.getPath());
							infoImageView.setImageBitmap(Bitmap.createScaledBitmap(
									bitmap, 
									bitmap.getWidth()/2, 
									bitmap.getHeight()/2, false));
							Utils.rotateImageView(infoImageView, pm.getPath());

							// We must call this to set the current marker and infoWindow references
							// to the MapWrapperLayout
							mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

							return infoWindow;
						}
						else
						{
							// MapWrapperLayout initialization
							mapWrapperLayout.init(map, Utils.getPixelsFromDp(ShareActivity.this, 59)); 				
							infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.photo_info_window_layout, null);
							infoSnippet = (TextView)infoWindow.findViewById(R.id.PhotoSnippet);
							infoImageView = (ImageView)infoWindow.findViewById(R.id.infoImageView);
							videoTouchListener.setView(infoImageView);

							// Setting up the infoWindow with current's marker info
							infoSnippet.setText(marker.getSnippet());

							videoTouchListener.setMarker(marker);					        
							infoImageView.setOnTouchListener(videoTouchListener);		

							//image setting
							VideoMarker vm = (VideoMarker)Utils.getMarkerByTitle(markers, marker.getTitle());	

							Bitmap thumb = ThumbnailUtils.createVideoThumbnail(
									vm.getVideoUriString(),
									MediaStore.Images.Thumbnails.MINI_KIND);

							Bitmap play = BitmapFactory.decodeResource(getResources(), R.drawable.play);
							Bitmap playIcon = Bitmap.createScaledBitmap(play,
									play.getWidth()/3, play.getHeight()/3, false);
							;
							Bitmap bmOverlay = Bitmap.createBitmap(thumb.getWidth(), thumb.getHeight(), thumb.getConfig());
							Canvas canvas = new Canvas(bmOverlay);
							canvas.drawBitmap(thumb, new Matrix(), null);
							canvas.drawBitmap(playIcon,(thumb.getWidth()/2) - (playIcon.getWidth()/2),
									(thumb.getHeight()/2) - (playIcon.getHeight()/2), null);

							infoImageView.setImageBitmap(bmOverlay);

							// We must call this to set the current marker and infoWindow references
							// to the MapWrapperLayout
							mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

							return infoWindow;
						}
					}
				});	
			}
		}		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(pausedForGPS)
		{
			if(Utils.displayGpsStatus(this))
			{
				Toast.makeText(ShareActivity.this, "GPS enabled",Toast.LENGTH_LONG).show();
				locationListener = new MyLocationListener(); 
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				locationManager.requestLocationUpdates(LocationManager  
						.GPS_PROVIDER, 5000, 10,locationListener); 

				location = MapManager.getLastKnownLocation(ShareActivity.this, locationManager);

				if(location != null)
				{
					Log.i("GPS", "FIRST LOCATION");
					String id = ParseManager.insertPosition(ShareActivity.this, pathParseObject, location.getLatitude(), location.getLongitude(), positionCounter);

					if(id == null)
					{
						Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
						lastPosition = new Position(location.getLatitude(),location.getLongitude(), positionCounter);
						lastPosition.setId(id);

						positionList.add(lastPosition);

						positionCounter++;

						CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(),location.getLongitude()))
						.zoom(18)
						.bearing(0)           
						.tilt(0)             
						.build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
						checkRequestThread=new CheckRequestShare();
						checkRequestThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}	
				}
				else
				{
					Toast.makeText(ShareActivity.this, "Searching for a valid location...",Toast.LENGTH_LONG).show();
				}

				map.setMyLocationEnabled(true);
				pausedForGPS=false;
			}
			else
			{
				Toast.makeText(ShareActivity.this, "GPS not enabled",Toast.LENGTH_LONG).show();
				
				checkRequestThread.cancel(true);
				finishMode=2;
				finish();
			}
		}
	}
	public void photoButtonOnClickHandler(View v)
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

	public void videoButtonOnClickHandler(View v)
	{
		if(location != null)
		{
			Date dt = new Date();
			File tempFile = new File(Environment.getExternalStorageDirectory(),
					dt.toString()+".mp4");
			videoFileName = tempFile.getAbsolutePath();
			Uri uri = Uri.fromFile(tempFile);

			//intent per l'attività di video
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10485760L);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent,1);
		}
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
				File file = new File(videoFileName);
				videoUri = Uri.fromFile(file);	        
				Intent intent = new Intent(ShareActivity.this, VideoInsertActivity.class);
				intent.putExtra("videoUri", videoUri.toString());	
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
				File oldPhoto=new File(photoFileName);
				oldPhoto.delete();

				//inserisco il file piccolo in memoria
				Date dt = new Date();
				File newFile = new File(Environment.getExternalStorageDirectory(),
						dt.toString()+".jpg");
				//copy bytes to file
				try {
					FileOutputStream outputStream = new FileOutputStream(newFile.getAbsolutePath()); 

					outputStream.write(image);
					Thread.sleep(1000);
					outputStream.flush();
					outputStream.close();							            							            

				} catch (Exception e) {
					e.printStackTrace();
				}


				//create media and insert in parse and in photos
				Media photo = new Media(image, data.getStringExtra("title"), lastPosition, path, counterPhoto);
				photo.setFilePath(newFile.getAbsolutePath());
				ParseManager.insertPhoto(this, photo);
				counterPhoto++;
				photos.add(photo);

				//add marker
				Marker m = map.addMarker(new MarkerOptions()
				.position(new LatLng(photo.getPosition().getLatitude(),
						photo.getPosition().getLongitude()))
						.snippet(photo.getTitle())
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.camera_marker))
						.title("photo"+String.valueOf(markerCounter)));
				map.getUiSettings().setMapToolbarEnabled(false);

				//add photo marker object
				markers.add(new PhotoMarker(m.getTitle(), m.getSnippet(),newFile.getAbsolutePath()));  
				markerCounter++;		        		        	       	 
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

				Media video = new Media(inputData, data.getStringExtra("title"), lastPosition, path, counterVideo);
				ParseManager.insertVideo(this, video);
				video.setFilePath(videoFileName);
				counterVideo++;
				videos.add(video);				

				//add marker
				Marker m = map.addMarker(new MarkerOptions()
				.position(new LatLng(video.getPosition().getLatitude(),
						video.getPosition().getLongitude()))
						.snippet(video.getTitle())
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.movie_marker))
						.title("video"+String.valueOf(markerCounter)));
				map.getUiSettings().setMapToolbarEnabled(false);

				//add video marker object
				markers.add(new VideoMarker(m.getTitle(), m.getSnippet(), videoFileName));  
				markerCounter++;;		        		        
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
		if (id == R.id.savePathShareItem) 
		{
			/*Date d=new Date();
			savePathOnLocalDB(d.toString());*/
			DialogFragment savePathDialogFragment = new SavePathDialogFragment();
			savePathDialogFragment.show(getFragmentManager(), "savePath");
			return true;
		}
		else if (id == R.id.galleryShareItem && !markers.isEmpty())
		{
			//intent per l'attività di gallery
			Intent intent = new Intent(ShareActivity.this,MediaGalleryActivity.class);
			//passaggio parametri all'intent
			intent.putExtra("media", markers.toArray());					
			intent.putExtra("index", 0);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDialogPositiveClick(SavePathDialogFragment dialog) 
	{
		String title = dialog.getTitleText().getText().toString();
		savePathOnLocalDB(title);
	}

	private void savePathOnLocalDB(String title) 
	{

		int idPath=PersonalDataManager.insertPath(title, "Me");
		PersonalDataManager.insertPositionList(positionList, idPath);
		PersonalDataManager.insertPhotoList(photos, idPath+"");
		PersonalDataManager.insertVideoList(videos, idPath+"");
		Toast.makeText(ShareActivity.this, "Path saved successfully!", Toast.LENGTH_LONG).show();

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
				location = loc;
				String id = ParseManager.insertPosition(ShareActivity.this, pathParseObject, loc.getLatitude(), loc.getLongitude(), positionCounter);

				if(id==null)
				{
					Toast.makeText(ShareActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
				else
				{
					
					lastPosition = new Position(loc.getLatitude(),loc.getLongitude(), positionCounter);
					lastPosition.setId(id);

					positionList.add(lastPosition);

					positionCounter++;

					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(loc.getLatitude(),loc.getLongitude()))
					.zoom(18)
					.bearing(0)           
					.tilt(0)             
					.build();
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			}
			else
				if((Math.abs(location.getLongitude() - loc.getLongitude()) > 0.00001 ||
						Math.abs(location.getLatitude() - loc.getLatitude()) > 0.00001))
				{
					Toast.makeText(ShareActivity.this, "speed: "+loc.getSpeed()+" accuracy: "+loc.getAccuracy(), Toast.LENGTH_LONG).show();
					Log.i("GPS", "LOCATION FOUND");

					String id = ParseManager.insertPosition(ShareActivity.this, pathParseObject, loc.getLatitude(), loc.getLongitude(), positionCounter);

					if(id==null)
					{
						Toast.makeText(ShareActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{

						lastPosition = new Position(loc.getLatitude(),loc.getLongitude(), positionCounter);
						lastPosition.setId(id);

						positionList.add(lastPosition);

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
		}

		@Override  
		public void onProviderDisabled(String provider) {    
		}  

		@Override  
		public void onProviderEnabled(String provider) {            
		}  

		@Override  
		public void onStatusChanged(String provider,   
				int status, Bundle extras) {     
		}  
	}	 


	private class CheckRequestShare extends AsyncTask<Void, Integer, String>
	{
		@Override
		protected String doInBackground(Void... params) 
		{							
			while(true)
			{		
				if(isCancelled())
					return null;
				
				if(!Utils.displayGpsStatus(ShareActivity.this) && !dialogShow)
				{
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							dialogShow = true;
							new AlertDialog.Builder(ShareActivity.this)
							.setTitle("Attention")
							.setMessage("Your GPS is not enabled. Please enable it now!")
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which)
								{ 
									pausedForGPS=true;
									dialogShow = false;
									ShareActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));				    
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
						}
					});
					
				}
				
				Iterator<String> iterator = requestIdList.iterator();

				while(iterator.hasNext())
				{
					String idReq=iterator.next();
					Boolean isActive=ParseManager.isRequestActive(ShareActivity.this, idReq);

					if(isActive==null)
					{
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(ShareActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
							}
						});
					}
					else
					{					
						if(!isActive)
						{
							//notificare all'utente che l'activity è stata chiusa dallo user
							handler.post(new Runnable() {
								@Override
								public void run() 
								{
									Toast.makeText(ShareActivity.this, "A user declines your sharing request",Toast.LENGTH_LONG).show();
								}
							});

							//cancello dalle liste ogni volta che una richiesta viene chiusa
							if(!ParseManager.deleteRequest(ShareActivity.this, idReq))
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										Toast.makeText(ShareActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
									}
								});
							}
							else
							{
								iterator.remove();

								//se non ci sono più fence chiudo l'activity
								if(requestIdList.isEmpty())
								{
									finishMode=1;
									finish();
								}
							}
						}
					}
				}

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}



	@Override
	public void onBackPressed()
	{
		checkRequestThread.cancel(true);
		new AlertDialog.Builder(this)
		.setTitle("Attention")
		.setMessage("Are you sure you want to stop sharing?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{ 
				Toast.makeText(ShareActivity.this, "Sharing finished",Toast.LENGTH_LONG).show();
				finishMode=2;
				finish();
			}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) 
			{ 
				checkRequestThread=new CheckRequestShare();
				checkRequestThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(finishMode==1)
		{
			for(int i=0; i<requestIdList.size();i++)
			{
				if(!ParseManager.deleteRequestAndFollowPath(this, requestIdList.get(i), pathParseObject))
				{
					Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
			}
		}
		else
		{
			if(finishMode==2)
			{
				for(int i=0; i<requestIdList.size();i++)
				{
					ParseManager.updateRequestStatusById(this, requestIdList.get(i), "chiusa");
				}
			}
		}
		
		 locationManager.removeUpdates(locationListener);

		Intent intent = new Intent(ShareActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
