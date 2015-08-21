package com.followme.manager;

import java.util.Iterator;
import java.util.List;

import android.graphics.Color;
import android.location.Location;

import com.followme.object.Position;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapManager {
	
	
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

}
