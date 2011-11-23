package com.forever.bike;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class QueryMapActivity extends MapActivity {

	private BikeApplication app;
	private QueryMapActivity context;

	private MapView mapView;
	private MapController controller;

	private List<Overlay> mapOverlays;
	private BikeItemOverlay bikeOverlay;
	private ShopItemOverlay shopOverlay;
	private LocationOverlay locationOverlay;
	private SearchOverlay searchOverlay;
	private PathOverlay pathOverlay;

	private Drawable drawable_bike;
	private Drawable drawable_search;
	private Drawable drawable_shop;

	private EditText MySearchText;
	private Button MyPos, MySearch, Lucky;

	private Station shekou_array[];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.querymap);

		app = (BikeApplication) getApplicationContext();
		context = this;

		mapView = (MapView) findViewById(R.id.mapview1);

		mapView.setBuiltInZoomControls(true);
		controller = mapView.getController();
		mapOverlays = mapView.getOverlays();

		// icon
		drawable_bike = this.getResources().getDrawable(R.drawable.grown);
		drawable_shop = this.getResources().getDrawable(R.drawable.shop);
		drawable_search = this.getResources().getDrawable(R.drawable.flag);

		// location
		locationOverlay = new LocationOverlay(this, mapView);
		locationOverlay.enableCompass();
		locationOverlay.enableMyLocation();
		locationOverlay.runOnFirstFix(new Runnable() {

			public void run() {
				myLocation();
			}
		});
		mapOverlays.add(locationOverlay);

		// path
		pathOverlay = new PathOverlay(context, locationOverlay);
		mapOverlays.add(pathOverlay);

		// bike

		bikeOverlay = new BikeItemOverlay(drawable_bike, this, app, pathOverlay);
		bikeOverlay.getPoint();
		mapOverlays.add(bikeOverlay);
		Thread thread = new Thread(bikeOverlay);
		thread.start();

		// shop
		shopOverlay = new ShopItemOverlay(drawable_shop, this, app, pathOverlay);
		mapOverlays.add(shopOverlay);

		MySearchText = (EditText) findViewById(R.id.editText1);
		MyPos = (Button) findViewById(R.id.button1);
		MySearch = (Button) findViewById(R.id.button2);
		Lucky = (Button) findViewById(R.id.button3);

		MyPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myLocation();
				// updateWithNewLocation(location);
			}
		});

		MySearch.setOnClickListener(new OnClickListener() {
			GeoPoint gp = null;

			@Override
			public void onClick(View v) {
				try {

					String strSearchAddress = MySearchText.getText().toString();
					if (!strSearchAddress.equals("")) {

						JSONObject jsonObject = getLocationInfo(strSearchAddress);
						Double lon1 = new Double(0);
						Double lat1 = new Double(0);

						try {

							lon1 = ((JSONArray) jsonObject.get("results"))
									.getJSONObject(0).getJSONObject("geometry")
									.getJSONObject("location").getDouble("lng");

							lat1 = ((JSONArray) jsonObject.get("results"))
									.getJSONObject(0).getJSONObject("geometry")
									.getJSONObject("location").getDouble("lat");
							gp = new GeoPoint((int) (lat1 * 1E6),
									(int) (lon1 * 1E6));
							controller.animateTo(gp);
							if (searchOverlay != null) {
								mapOverlays.remove(searchOverlay);
							}
							OverlayItem item = new OverlayItem(gp, "",
									strSearchAddress);
							searchOverlay = new SearchOverlay(drawable_search,
									context);
							searchOverlay.addOverlay(item);
							mapOverlays.add(searchOverlay);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} catch (Exception e) {
					Toast.makeText(context, "查询失败!", Toast.LENGTH_SHORT).show();
				}

			}
		});
		
		Lucky.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					shopOverlay.getPoint(locationOverlay.getMyLocation());
				} catch (Exception e) {

				}
			}
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private void myLocation() {
		try {
			GeoPoint current = locationOverlay.getMyLocation();
			controller.animateTo(new GeoPoint(current.getLatitudeE6() - 1590,
					current.getLongitudeE6() + 4978));
		} catch (Exception e) {
			Toast.makeText(context, "获取当前地点失败!", Toast.LENGTH_SHORT).show();
		}
	}

	// 这个类，是来此http://blog.csdn.net/dadoneo/archive/2011/03/17/6255486.aspx的介绍。
	// android2.2貌似有一个bug，不支持GeoCoder，这个通过http访问谷歌，再得到location
	public static JSONObject getLocationInfo(String address) {

		HttpGet httpGet = new HttpGet(
				"http://maps.google.com/maps/api/geocode/json?address="
						+ address + "&sensor=false");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}

}