package com.followme.manager;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;

import com.followme.object.Position;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapManager {
	
	
	public static Circle drawCircle(LatLng center, int radius, GoogleMap map)
	{
	    return map.addCircle(new CircleOptions()
        .center(center)
        .radius(radius)
        .strokeColor(Color.RED)
        .fillColor(Color.BLUE));

	}
	
	
	
	/**
	 * Draw the line between two locations
	 * @param loc1
	 * @param loc2
	 * @param map
	 */
	public static void drawPrimaryLinePath(LatLng loc1, LatLng loc2, GoogleMap map)
	{
	    PolylineOptions options = new PolylineOptions();

	    options.color( Color.parseColor( "#CC0000FF" ) );
	    options.width( 5 );
	    options.visible( true );
	    options.geodesic( true );
	    options.add(loc1);
	    options.add(loc2);
	    map.addPolyline( options );

	}
	
	
	/**
	 * Draw broken polygon from list
	 * @param positionList
	 * @param map
	 */
	public static void drawPolygonPath(List<Position> positionList, GoogleMap map)
	{
	    PolylineOptions options = new PolylineOptions();
	    
	    Iterator<Position> i = positionList.iterator();
		Position posItem=null;
		
		while(i.hasNext())
		{
			posItem = i.next();
			
			options.add(new LatLng(posItem.getLatitude(),posItem.getLongitude()));
			
		}
	    options.color( Color.parseColor( "#CC0000FF" ) );
	    options.width( 5 );
	    options.visible( true );
	    options.geodesic( true );
	    map.addPolyline( options );

	}
	
	/**
	 * Method that return the best location found from location manager.
	 * @param context
	 * @param mLocationManager
	 * @return
	 */
	public static Location getLastKnownLocation(Context context,LocationManager mLocationManager) {
	    List<String> providers = mLocationManager.getProviders(true);
	    Location bestLocation = null;
	    for (String provider : providers) {
	        Location l = mLocationManager.getLastKnownLocation(provider);
	        if (l == null) {
	            continue;
	        }
	        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
	            // Found best last known location: %s", l);
	            bestLocation = l;
	        }
	    }
	    return bestLocation;
	}
}
