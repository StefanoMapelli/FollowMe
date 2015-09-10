package com.followme.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.followme.adapter.MapWrapperLayout;
import com.followme.adapter.OnInfoWindowElemTouchListener;
import com.followme.fragment.SavePathDialogFragment;
import com.followme.fragment.SavePathDialogFragment.SavePathDialogListener;
import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.manager.Utils;
import com.followme.object.CustomMarker;
import com.followme.object.Media;
import com.followme.object.PhotoMarker;
import com.followme.object.Position;
import com.followme.object.Request;
import com.followme.object.VideoMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SharingReceiverActivity extends ActionBarActivity implements SavePathDialogListener {
	
	private Request request;
	private ParseObject path;
	private int counterPhoto;
	private int counterVideo;
	private int counterPosition;
	private GoogleMap map;
	private MapWrapperLayout mapWrapperLayout;
	private ViewGroup infoWindow;
    private TextView infoSnippet;
    private ImageView infoImageView;
    private OnInfoWindowElemTouchListener photoTouchListener;	
    private OnInfoWindowElemTouchListener videoTouchListener;
	private List<Position> positionList = new ArrayList<Position>();
	private List<Media> photos = new ArrayList<Media>();
	private List<Media> videos = new ArrayList<Media>();
	private ArrayList<CustomMarker> markers = new ArrayList<CustomMarker>();
	private int markerCounter = 0;
	private FindNewPositions networkThread;
	private Marker marker;
	private String senderName;
	private Handler handler;
	private int finishMode=1; //1 destroyed by follower, 2 destroyed by receiver

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharing_receiver_layout);
		handler=new Handler();
		FragmentManager fm = getSupportFragmentManager();
		map = ((SupportMapFragment) fm.findFragmentById(R.id.mapSharingReceiver)).getMap();
		mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.mapRecieverLinearLayout);
		map.getUiSettings().setMapToolbarEnabled(false);
		request = (Request) getIntent().getSerializableExtra("acceptedRequest");
		senderName = PersonalDataManager.getNameOfContact(request.getSender().getPhoneNumber());
		counterPosition=-1;
		counterPhoto = -1;
		counterVideo = -1;
		
		//recupero l'id del percorso relativo alla richiesta
		path=ParseManager.getPathOfRequest(this, request);
		
		if(path == null)
		{
			Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
		}
		else
		{
		
		//verifico che ci siano posizioni relative alla mia request su parse
		if(path!=null)
		{
			//parte il thread per il controllo delle nuove posizioni nel db
			networkThread = new FindNewPositions();
			networkThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			finish();
		}
		
		photoTouchListener = new OnInfoWindowElemTouchListener() 
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) 
            {              
				//intent per l'attività di gallery
				Intent intent = new Intent(SharingReceiverActivity.this,MediaGalleryActivity.class);
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
        		Intent intent = new Intent(SharingReceiverActivity.this,MediaGalleryActivity.class);
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
        			mapWrapperLayout.init(map, Utils.getPixelsFromDp(SharingReceiverActivity.this, 59)); 				
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
        		else if(marker.getTitle().startsWith("video"))
        		{
        			// MapWrapperLayout initialization
        			mapWrapperLayout.init(map, Utils.getPixelsFromDp(SharingReceiverActivity.this, 59)); 				
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
        		else
        		{
        			return null;
        		}
        	}
        });	
		}
	}
	
	
	@Override
	public void onBackPressed()
	{
		networkThread.cancel(true);
		new AlertDialog.Builder(this)
	    .setTitle("Attention")
	    .setMessage("Are you sure you want to stop sharing activity?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which)
	        { 
	        	Toast.makeText(SharingReceiverActivity.this, "Sharing finished",Toast.LENGTH_LONG).show();
	            finishMode=2;
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        { 
	        	networkThread = new FindNewPositions();
	        	networkThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
			if(!ParseManager.deleteRequestAndFollowPath(this, request.getId(), path))
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			if(!ParseManager.updateRequestStatusById(this, request.getId(), "chiusa"))
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
		}
		
		Intent intent = new Intent(SharingReceiverActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private class FindNewPositions extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{				
			while(true)
			{		
				if(isCancelled())
					return null;
				//ricerco le nuove posizioni
				List<Position> newPositions = ParseManager.getNewPosition(SharingReceiverActivity.this, path, counterPosition);
				
				if(newPositions == null)
				{
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							Toast.makeText(SharingReceiverActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
						}
					});
				}
				else
				{
					positionList.addAll(newPositions);
					if(newPositions.size()>0)
					{	
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(new LatLng(positionList.get(positionList.size()-1).getLatitude(),
										positionList.get(positionList.size()-1).getLongitude()))
										.zoom(18)
										.bearing(0)           
										.tilt(0)             
										.build();
								map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

								if(marker != null)
								{
									marker.remove();
								}
								//add position marker
								Marker m = map.addMarker(new MarkerOptions()
								.position(new LatLng(positionList.get(positionList.size()-1).getLatitude(),
										positionList.get(positionList.size()-1).getLongitude()))
										.snippet(request.getSender().getPhoneNumber())
										.title(senderName));
								marker=m;

								MapManager.drawPolygonPath(positionList, map);
							}
						});


						counterPosition=positionList.get(positionList.size()-1).getCounter();
					}

					List<Media> newPhotos = ParseManager.getNewPhotos(SharingReceiverActivity.this, path, counterPhoto);

					if(newPhotos == null)
					{
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(SharingReceiverActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
							}
						});
					}
					else
					{
						for (final Media photo : newPhotos)
						{

							handler.post(new Runnable() {
								@Override
								public void run() 
								{
									//add marker
									Marker m = map.addMarker(new MarkerOptions()
									.position(new LatLng(photo.getPosition().getLatitude(),
											photo.getPosition().getLongitude()))
											.snippet(photo.getTitle())
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.camera_marker))
											.title("photo"+String.valueOf(markerCounter)));


									Date dt = new Date();
									File newFile = new File(Environment.getExternalStorageDirectory(),
											dt.toString()+".jpg");
									photo.setFilePath(newFile.getAbsolutePath());
									//copy bytes to file
									try {
										FileOutputStream outputStream = new FileOutputStream(newFile.getAbsolutePath()); 

										outputStream.write(photo.getMedia());
										Thread.sleep(1000);
										outputStream.flush();
										outputStream.close();							            							            

									} catch (Exception e) {
										e.printStackTrace();
									}

									markers.add(new PhotoMarker(m.getTitle(), m.getSnippet(),newFile.getAbsolutePath()));  
									markerCounter++;
								}
							});	
						}
						if(!newPhotos.isEmpty())
							counterPhoto=newPhotos.get(newPhotos.size()-1).getCounter();
						photos.addAll(newPhotos);


						List<Media> newVideos = 
								ParseManager.getNewVideos(SharingReceiverActivity.this, path, counterVideo);

						if(newVideos == null)
						{
							handler.post(new Runnable() {
								@Override
								public void run() 
								{
									Toast.makeText(SharingReceiverActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
								}
							});
						}
						else
						{

							for (final Media video : newVideos)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										//add marker
										Marker m = map.addMarker(new MarkerOptions()
										.position(new LatLng(video.getPosition().getLatitude(),
												video.getPosition().getLongitude()))
												.snippet(video.getTitle())
												.icon(BitmapDescriptorFactory.fromResource(R.drawable.movie_marker))
												.title("video"+String.valueOf(markerCounter)));
										map.getUiSettings().setMapToolbarEnabled(false);

										Date dt = new Date();
										File newFile = new File(Environment.getExternalStorageDirectory(),
												dt.toString()+".mp4");
										video.setFilePath(newFile.getAbsolutePath());
										//copy bytes to file
										try {
											FileOutputStream outputStream = new FileOutputStream(newFile.getAbsolutePath());

											outputStream.write(video.getMedia());
											Thread.sleep(1000);
											outputStream.flush();
											outputStream.close();							            
										} catch (Exception e) {
											e.printStackTrace();
										}

										markers.add(new VideoMarker(m.getTitle(), m.getSnippet(),newFile.getAbsolutePath()));  
										markerCounter++;
									}
								});															
							}
							if(!newVideos.isEmpty())
								counterVideo=newVideos.get(newVideos.size()-1).getCounter();
							videos.addAll(newVideos);


							Boolean isActive=ParseManager.isRequestActive(SharingReceiverActivity.this, request.getId());

							if(isActive == null)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										Toast.makeText(SharingReceiverActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
									}
								});
							}
							else
							{
								if(!isActive)
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											//notificare all'utente che l'activity è stata chiusa dal follower
											Toast.makeText(SharingReceiverActivity.this, "Sharing terminated by the user",Toast.LENGTH_LONG).show();
										}
									});
									finishMode=1;
									finish();
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
			}
		}
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sharing_receiver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.savePathShareReceiving) {

			DialogFragment savePathDialogFragment = new SavePathDialogFragment();
			savePathDialogFragment.show(getFragmentManager(), "savePath");
			return true;

		}
		else if (id == R.id.galleryItemReceiving && !markers.isEmpty())
		{
			//intent per l'attività di gallery
			Intent intent = new Intent(SharingReceiverActivity.this,MediaGalleryActivity.class);
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

	//salvataggio di path, posizioni, photos e videos	
	private void savePathOnLocalDB(String title) 
	{
		int idPath=PersonalDataManager.insertPath(title, PersonalDataManager.getNameOfContact(request.getSender().getPhoneNumber()));
		PersonalDataManager.insertPositionList(positionList, idPath);
		PersonalDataManager.insertPhotoList(photos, idPath+"");
		PersonalDataManager.insertVideoList(videos, idPath+"");
		Toast.makeText(this, "Path saved successfully", Toast.LENGTH_LONG).show();
	}
}
