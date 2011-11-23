package com.forever.bike;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PathOverlay extends Overlay {

	private List<GeoPoint> points;
	private Context context;
	private LocationOverlay location;

	public PathOverlay(Context context, LocationOverlay location) {
		this.context = context;
		this.location = location;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, shadow);
		if (points == null || points.size() == 0)
			return;
		// pen
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(4);
		// lines
		Projection projection = mapView.getProjection();
		Path path = new Path();
		Iterator<GeoPoint> iter = points.iterator();
		Point p = new Point();
		GeoPoint gp = iter.next();
		projection.toPixels(gp, p);
		path.moveTo(p.x, p.y);
		while (iter.hasNext()) {
			p = new Point();
			gp = iter.next();
			projection.toPixels(gp, p);
			path.lineTo(p.x, p.y);
		}
		// draw
		canvas.drawPath(path, paint);
		mapView.getController().animateTo(gp);
	}

	public void clearRoute() {
		this.points = null;
	}

	public void findRoute(GeoPoint start, GeoPoint end) {

		if (start == null || end == null)
			return;

		try {
			start = this.location.getMyLocation();
		} catch (Exception e) {
			return;
		}
		if(start == null)
			return;
		String url = "http://maps.google.com/maps/api/directions/xml?origin="
				+ (double) (start.getLatitudeE6()-1950) / 1E6 + ","
				+ (double) (start.getLongitudeE6()+4978) / 1E6 + "&destination="
				+ (double) end.getLatitudeE6() / 1E6 + ","
				+ (double) end.getLongitudeE6() / 1E6
				+ "&sensor=false&mode=walking";

		HttpGet get = new HttpGet(url);
		String strResult = "";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			HttpResponse httpResponse = null;
			httpResponse = httpClient.execute(get);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			return;
		}

		if (-1 == strResult.indexOf("<status>OK</status>")) {
			Toast.makeText(context, "获取导航路线失败!", Toast.LENGTH_SHORT).show();
			return;
		}

		int pos = strResult.indexOf("<overview_polyline>");
		pos = strResult.indexOf("<points>", pos + 1);
		int pos2 = strResult.indexOf("</points>", pos);
		strResult = strResult.substring(pos + 8, pos2);

		points = decodePoly(strResult);
	}

	private List<GeoPoint> decodePoly(String encoded) {

		List<GeoPoint> poly = new ArrayList<GeoPoint>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
					(int) (((double) lng / 1E5) * 1E6));
			poly.add(p);
		}

		return poly;
	}

}
