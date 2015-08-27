package com.followme.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.followme.object.Contact;
import com.followme.object.CustomMarker;
import com.followme.object.PhotoMarker;
import com.followme.object.VideoMarker;

public class Utils {
	
	/**
	 * This method create the list of contacts that are in both lists
	 * allContacts and allUsers. 
	 * @param allContacts : all the contacts of the user
	 * @param allUsers : all the phone numbers that are present in parse db
	 * @return
	 */
	public static List<Contact> phoneContactsOnParse(List<Contact> allContacts, List<Contact> allUsers)
	{
		List<Contact> output = new ArrayList<Contact>();
		Iterator<Contact> i = allContacts.iterator();
		Iterator<Contact> j = allUsers.iterator();
		Contact c;
		Contact number;
		
		while(i.hasNext())
		{
			c = i.next();
			j = allUsers.iterator();
			
			while(j.hasNext())
			{
				number = j.next();
				
				if(c.getPhoneNumber().compareTo(number.getPhoneNumber()) == 0)
				{
					c.setId(number.getId());
					output.add(c);
				}
			}
		}
		
		return output;
	}
	
	/**
	 * This method create the list of contact that are checked.
	 * @param contacts : starting input array
	 * @return
	 */
	public static List<Contact> selectedContacts(Contact[] contacts)
	{
		List<Contact> output = new ArrayList<Contact>();
		for(int i=0; i<contacts.length; i++)
		{
			if(contacts[i].isChecked())
			{
				output.add(contacts[i]);
			}
		}
		
		return output;
	}
	
		/**
		 * Resize image to 480 width.
		 * @param filePath
		 * @return
		 */
	public static Bitmap getSmallBitmap(String filePath) 
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize based on a preset ratio
		options.inSampleSize = calculateInSampleSize(options, 480);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		Bitmap compressedImage = BitmapFactory.decodeFile(filePath, options);
		
		//Handle orientation
	    //rotateBitmap(filePath, compressedImage);
	  
		return compressedImage;
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
		// Raw width of image
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (width > reqWidth) {
		    final int halfWidth = width / 2;

		    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		    // height and width larger than the requested height and width.
		    while ((halfWidth / inSampleSize) > reqWidth) {
		        inSampleSize *= 2;
		    }
		}
		return inSampleSize;
	}
	
	/**
	 * Method to Check GPS is enable or disable
	 * @return
	 */
	 public static Boolean displayGpsStatus(Context context) {  
	  ContentResolver contentResolver = context.getContentResolver();  
	  boolean gpsStatus = Settings.Secure  
	  .isLocationProviderEnabled(contentResolver,   
	  LocationManager.GPS_PROVIDER);  
	  if (gpsStatus) {  
	   return true;  
	  
	  } else {  
	   return false;  
	  }  
	 }  
	
	/**
	 * Set an image file as imageView content.
	 * @param targetW
	 * @param targetH
	 * @param mImageView
	 * @param mCurrentPhotoPath
	 */
	public static void setPic(int targetW, int targetH, ImageView mImageView, String mCurrentPhotoPath) {
		// Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    
	    //Handle orientation
	    //rotateBitmap(mCurrentPhotoPath, bitmap);
	    
	    mImageView.setImageBitmap(bitmap);
	    rotateImageView(mImageView, mCurrentPhotoPath);
	}
	
	public static void rotateImageView(View view, String filePath)
	{
		int orientation = Utils.getOrientation(filePath);
        int angle = 0;
        if (orientation == 6) {
            angle = 90;
        }
        else if (orientation == 3) {
            angle = 180;
        }
        else if (orientation == 8) {
        	angle = 270;
        }
	    
        view.setRotation(angle);
	}
	
	/**
	 * Return orientation of a file.
	 * @param filePath
	 * @return
	 */
	public static int getOrientation(String filePath)
	{
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
	}
	
	
	/*/**
	 * Adjust the orientation of a photo file.
	 * @param filePath
	 * @param bitmap
	 */
	/*public static void rotateBitmap(String filePath, Bitmap bitmap)
	{
		//Handle orientation
	    int orientation = getOrientation(filePath);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        }
        else if (orientation == 3) {
            matrix.postRotate(180);
        }
        else if (orientation == 8) {
            matrix.postRotate(270);
        }		
	    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}*/
	
	public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
	
	/**
	 * Method useful for marker visualization.
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
	}
	
	/**
	 * Method that search in the markers array list first occurrence of given marker with that title.
	 * @param markers
	 * @param title
	 * @return
	 */
	public static CustomMarker getMarkerByTitle(ArrayList<CustomMarker> markers, String title)
	{
		for(CustomMarker pm : markers)
		{
			if(pm.getTitle().compareTo(title) == 0)
			{
				return pm;
			}
		}
		return null;
	}
	
	/**
	 * Copy a file in another.
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copyFile(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public static String getPath(Context context,Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
}
