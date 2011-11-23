package com.forever.bike;

import android.content.Context;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationOverlay extends MyLocationOverlay {

	public LocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void drawMyLocation(android.graphics.Canvas canvas,
			MapView mapView, android.location.Location lastFix,
			GeoPoint myLocation, long when) {
		lastFix.setLatitude(lastFix.getLatitude() - 0.001950);
		lastFix.setLongitude(lastFix.getLongitude() + 0.004978);
		GeoPoint fixLocation = new GeoPoint(myLocation.getLatitudeE6() - 1950,
				myLocation.getLongitudeE6() + 4978);
		super.drawMyLocation(canvas, mapView, lastFix, fixLocation, when);
	}

}
