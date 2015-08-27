package com.followme.adapter;

import java.util.ArrayList;

import com.followme.activity.R;
import com.followme.activity.VideoInsertActivity;
import com.followme.manager.Utils;
import com.followme.object.CustomMarker;
import com.followme.object.PhotoMarker;
import com.followme.object.VideoMarker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.view.MotionEvent;

public class GalleryLayout extends HorizontalScrollView {
	private static final int SWIPE_MIN_DISTANCE = 5;
	private static final int SWIPE_THRESHOLD_VELOCITY = 300;

	private ArrayList mItems = null;
	private GestureDetector mGestureDetector;
	private int mActiveFeature = 0;

	public GalleryLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GalleryLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GalleryLayout(Context context) {
		super(context);
	}

	public void setActiveFeature(int index)
	{
		mActiveFeature = index;
	}
	
	public void setFeatureItems(Context context,ArrayList<CustomMarker> items){
		LinearLayout internalWrapper = new LinearLayout(getContext());
		internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		internalWrapper.setOrientation(LinearLayout.HORIZONTAL);
		addView(internalWrapper);
		this.mItems = items;
		for(CustomMarker cm : items){
			LinearLayout featureLayout=null;
			if(cm instanceof PhotoMarker)
			{
				DisplayMetrics metrics = new DisplayMetrics();
				((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(metrics.widthPixels, LayoutParams.MATCH_PARENT);
				featureLayout = (LinearLayout) View.inflate(this.getContext(),R.layout.photo_gallery_page,null);
				featureLayout.setLayoutParams(params);
				TextView title = (TextView) featureLayout.getChildAt(1);
				FrameLayout fl = (FrameLayout) featureLayout.getChildAt(0);
				ImageView image = (ImageView) fl.getChildAt(0);
				
				PhotoMarker pm = (PhotoMarker) cm;
				Bitmap bitmap = Utils.getSmallBitmap(pm.getPath());
				title.setText(pm.getSnippet());
				image.setImageBitmap(bitmap);
				Utils.rotateImageView(image, pm.getPath());
			}
			else
			{
				DisplayMetrics metrics = new DisplayMetrics();
				((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(metrics.widthPixels, LayoutParams.MATCH_PARENT);
				featureLayout = (LinearLayout) View.inflate(this.getContext(),R.layout.video_gallery_page,null);
				featureLayout.setLayoutParams(params);
				TextView title = (TextView) featureLayout.findViewById(R.id.titleVideoPage);
				VideoView video = (VideoView) featureLayout.findViewById(R.id.videoViewVideoPage);
				
				VideoMarker vm = (VideoMarker) cm;
				
				title.setText(vm.getSnippet());
				video.setVideoURI(Uri.parse(vm.getVideoUriString()));
				MediaController mc = new MediaController(context);
				video.setMediaController(mc);
		        mc.setAnchorView(video);		          
		        video.setMinimumWidth(metrics.widthPixels);		        
			}
 			internalWrapper.addView(featureLayout);
 		}
 		setOnTouchListener(new View.OnTouchListener() {
 			@Override
 			public boolean onTouch(View v, MotionEvent event) {
 				//If the user swipes
 				if (mGestureDetector.onTouchEvent(event)) {
 					return true;
 				}
 				else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL ){
 					int scrollX = getScrollX();
 					int featureWidth = v.getMeasuredWidth();
 					mActiveFeature = ((scrollX + (featureWidth/2))/featureWidth);
 					int scrollTo = mActiveFeature*featureWidth;
 					smoothScrollTo(scrollTo, 0);
 					return true;
 				}
 				else{
 					return false;
 				}
 			}
 		});
 		mGestureDetector = new GestureDetector(new MyGestureDetector());
 	}
 	 	class MyGestureDetector extends SimpleOnGestureListener {
 		@Override
 		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
 				//right to left
  				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					int featureWidth = getMeasuredWidth();
					mActiveFeature = (mActiveFeature < (mItems.size() - 1))? mActiveFeature + 1:mItems.size() -1;
 					smoothScrollTo(mActiveFeature*featureWidth, 0);
 					return true;
 				}
   				//left to right
 				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					int featureWidth = getMeasuredWidth();
					mActiveFeature = (mActiveFeature > 0)? mActiveFeature - 1:0;
					smoothScrollTo(mActiveFeature*featureWidth, 0);
					return true;
				}
			return false;
		}
	}
}
