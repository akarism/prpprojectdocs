package com.forever.bike;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ShopItemOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays;
	private Map<String, Shop> shops;
	private BikeApplication app;
	private PathOverlay pathOverlay;
	private Context context;
	private OverlayItem item;
	private GeoPoint gp;

	public ShopItemOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public ShopItemOverlay(Drawable defaultMarker, Context context,
			BikeApplication app, PathOverlay pathOverlay) {
		this(defaultMarker);
		this.context = context;
		this.app = app;
		this.pathOverlay = pathOverlay;
		this.shops = new HashMap<String, Shop>();
		this.mOverlays = new ArrayList<OverlayItem>();
		populate();
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		item = mOverlays.get(index);

		gp = new GeoPoint(22489283, 113924961);

		Shop shop = shops.get(item.getSnippet());
		// 用AlertDialog显示
		Builder builder = new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.btn_star)
				.setTitle(shop.getName())
				.setMessage(
						"地点： " + shop.getLocation() + "\n简介： "
								+ shop.getDescribe())
				.setPositiveButton("告诉我怎么去", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						pathOverlay.findRoute(gp, item.getPoint());
						sendSelect(item);
					}
				}).setNegativeButton("返回", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				});
		Dialog dialog = builder.create();
		dialog.show();

		// 用Toast显示
		// Toast.makeText(context, item.getTitle() + "\n" + item.getSnippet(),
		// Toast.LENGTH_SHORT).show();
		return true;

	}
	
	private void sendSelect(OverlayItem item){
		Date date = new Date();
		String time = date.getHours() + ":" + date.getMinutes() + ":"
				+ date.getSeconds();
		String urlStr = "http://59.78.58.190:8080/forever/selectshop.action?time="
				+ time+ "&shop="+item.getSnippet();
		HttpGet request = new HttpGet(urlStr);
		if (app.getSessionId() != null)
			request.setHeader("Cookie", "JSESSIONID=" + app.getSessionId());

		String answer;

		try {
			HttpResponse response = new DefaultHttpClient().execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				this.shops = new HashMap<String, Shop>();
				answer = EntityUtils.toString(response.getEntity());

				JSONObject json = new JSONObject(answer);
				if(!json.getString("result").equals("success"))
					return;

			} else {
				Toast.makeText(context, "无法链接到服务器", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {

		}
	}

	public void getPoint(GeoPoint p) {

		Date date = new Date();
		String time = date.getHours() + ":" + date.getMinutes() + ":"
				+ date.getSeconds();
		String urlStr = "http://59.78.58.190:8080/forever/fetchdata.action?time="
				+ time+ "&lat="+ (double) p.getLatitudeE6()/1E6
				+ "&lng="+ (double) p.getLongitudeE6()/1E6;

		HttpGet request = new HttpGet(urlStr);
		if (app.getSessionId() != null)
			request.setHeader("Cookie", "JSESSIONID=" + app.getSessionId());

		String answer;

		try {
			HttpResponse response = new DefaultHttpClient().execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				this.shops = new HashMap<String, Shop>();
				answer = EntityUtils.toString(response.getEntity());

				JSONObject json = new JSONObject(answer);
				JSONArray station_array = json.getJSONArray("content");

				for (int iter = 0; iter < station_array.length(); ++iter) {
					JSONObject shop = station_array.getJSONObject(iter);
					Shop temp = new Shop();
					temp.setName(shop.getString("name"));
					temp.setLocation(shop.getString("location"));
					temp.setDescribe(shop.getString("describe"));
					temp.setLat(shop.getDouble("lat"));
					temp.setLng(shop.getDouble("lng"));
					temp.setNid(shop.getString("nid"));
					GeoPoint point = new GeoPoint((int) (temp.getLat() * 1E6),
							(int) (temp.getLng() * 1E6));
					shops.put(temp.getNid(), temp);
					OverlayItem item = new OverlayItem(point, temp.getName(),
							temp.getNid());
					addOverlay(item);
				}

			} else {
				Toast.makeText(context, "无法链接到服务器", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {

		}
	}
}
