package com.followme.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.followme.adapter.MapWrapperLayout;
import com.followme.adapter.OnInfoWindowElemTouchListener;
import com.followme.fragment.RequestDialogFragment;
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

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadPathActivity extends ActionBarActivity {

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
	private ProgressDialog progressBar;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_path_layout);
		handler=new Handler();
	}
	
	@Override
	protected void onResume()
	{
		super.onStart();
		LoadPathThread loadPathThread=new LoadPathThread();
		loadPathThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private class LoadPathThread extends AsyncTask<Void, Integer, String>
	{

		@Override
		protected void onProgressUpdate (Integer...progress)
		{
			if(progress[0]==0)
			{
				progressBar = new ProgressDialog(LoadPathActivity.this);
				progressBar.setCancelable(true);
				progressBar.setMessage("Path loading ...");
				progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressBar.setMax(100);
				progressBar.show();

			}
			else if(progress[0]==2)	
			{
				progressBar.dismiss();
			}
			else
			{
				progressBar.setProgress(progress[1]);
			}
		}

		@Override
		protected void onPreExecute() {

			publishProgress(0,0);
		}

		@Override
		protected void onPostExecute(String result) {

			publishProgress(2,0);

		}

		@Override
		protected String doInBackground(Void... params) 
		{	


			FragmentManager fm = getSupportFragmentManager();
			map = ((SupportMapFragment) fm.findFragmentById(R.id.mapLoad)).getMap();
			mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.mapLoadLinearLayout);

			String idPath=getIntent().getStringExtra("pathLocalId");


			//recupero tutti i dati del percorso
			positionList=PersonalDataManager.getAllPositionsOfPath(idPath);
			photos=PersonalDataManager.getAllPhotosOfPath(idPath);
			videos=PersonalDataManager.getAllVideosOfPath(idPath);

			//progress bar al 20%
			publishProgress(1,20);

			handler.post(new Runnable() {
				@Override
				public void run() 
				{
					photoTouchListener = new OnInfoWindowElemTouchListener() 
					{
						@Override
						protected void onClickConfirmed(View v, Marker marker) 
						{              
							//intent per l'attività di gallery
							Intent intent = new Intent(LoadPathActivity.this,MediaGalleryActivity.class);
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
							Intent intent = new Intent(LoadPathActivity.this,MediaGalleryActivity.class);
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
								mapWrapperLayout.init(map, Utils.getPixelsFromDp(LoadPathActivity.this, 59)); 				
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
								mapWrapperLayout.init(map, Utils.getPixelsFromDp(LoadPathActivity.this, 59)); 				
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

								infoImageView.setImageBitmap(thumb);

								// We must call this to set the current marker and infoWindow references
								// to the MapWrapperLayout
								mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

								return infoWindow;
							}
						}
					});	
				}
			});
			publishProgress(1,40);

			//disegno il percorso con tutte le posizioni
			handler.post(new Runnable() {
				@Override
						public void run() 
						{
							map.getUiSettings().setMapToolbarEnabled(false);
							MapManager.drawPolygonPath(positionList, map);

							//zoommo
							CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(positionList.get(0).getLatitude(),positionList.get(0).getLongitude()))
							.zoom(18)
							.bearing(0)           
							.tilt(0)             
							.build();
							map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						}
					});

					//inserisco i marker per le foto
					publishProgress(1,50);

					for (final Media photo : photos)
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
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
										.title("photo"+String.valueOf(markerCounter)));


								Date dt = new Date();
								File newFile = new File(Environment.getExternalStorageDirectory(),
										dt.toString()+".jpg");
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

					//inserisco i marker per i video
					publishProgress(1,75);

					for (final Media video : videos)
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
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
										.title("video"+String.valueOf(markerCounter)));

								Date dt = new Date();
								File newFile = new File(Environment.getExternalStorageDirectory(),
										dt.toString()+".mp4");
								//copy bytes to file
								try 
								{
									FileOutputStream outputStream = new FileOutputStream(newFile.getAbsolutePath());

									outputStream.write(video.getMedia());
									Thread.sleep(1000);
									outputStream.flush();
									outputStream.close();							            
								}
								catch (Exception e) 
								{
									e.printStackTrace();
								}

								markers.add(new VideoMarker(m.getTitle(), m.getSnippet(),newFile.getAbsolutePath()));  
								markerCounter++;
							}
						});
					}

					publishProgress(1,100);

			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load_path, menu);
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
}
