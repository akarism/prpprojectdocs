package com.forever.bike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class BikeItemOverlay extends ItemizedOverlay implements Runnable {

	private ArrayList<OverlayItem> mOverlays;
	private Map<String, Station> stations;
	private BikeApplication app;
	private PathOverlay pathOverlay;
	private Context context;
	private OverlayItem item;
	private GeoPoint gp;

	public BikeItemOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public BikeItemOverlay(Drawable defaultMarker, Context context,
			BikeApplication app, PathOverlay pathOverlay) {
		this(defaultMarker);
		this.context = context;
		this.app = app;
		this.pathOverlay = pathOverlay;
		this.stations = new HashMap<String, Station>();
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
		
		gp = new GeoPoint(22489283,113924961);

		Station station = stations.get(item.getSnippet());
		// 用AlertDialog显示
		Builder builder = new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.btn_star)
				.setTitle(station.getName())
				.setMessage(
						"空闲车辆： " + station.getBike_num() + "\n空闲锁柱： "
								+ station.getPillar_num())
				.setPositiveButton("告诉我怎么去", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						pathOverlay.findRoute(gp, item.getPoint());
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

	public void getPoint() {
		String urlStr = "http://59.78.58.190:8080/forever/getallnetpoint.action";

		HttpGet request = new HttpGet(urlStr);
		if (app.getSessionId() != null)
			request.setHeader("Cookie", "JSESSIONID=" + app.getSessionId());

		String answer;

		try {
			HttpResponse response = new DefaultHttpClient().execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				answer = EntityUtils.toString(response.getEntity());

				JSONObject json = new JSONObject(answer);
				JSONArray station_array = json.getJSONArray("content");

				for (int iter = 0; iter < station_array.length(); ++iter) {
					JSONObject station = station_array.getJSONObject(iter);
					Station temp = new Station();
					temp.setName(station.getString("name"));
					temp.setlat((float) station.getDouble("lat"));
					temp.setlng((float) station.getDouble("lng"));
					GeoPoint point = new GeoPoint((int) (temp.getlat() * 1E6),
							(int) (temp.getlng() * 1E6));
					stations.put(station.getString("nid"), temp);
					OverlayItem item = new OverlayItem(point,
							station.getString("name"), station.getString("nid"));
					addOverlay(item);
				}

			} else {
				Toast.makeText(context, "无法链接到服务器", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {

		}
	}

	private void getInfo() {
		String urlStr = "http://59.78.58.190:8080/forever/getallnetinfo.action";

		HttpGet request = new HttpGet(urlStr);
		if (app.getSessionId() != null)
			request.setHeader("Cookie", "JSESSIONID=" + app.getSessionId());

		String answer;

		try {
			HttpResponse response = new DefaultHttpClient().execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				answer = EntityUtils.toString(response.getEntity());

				JSONObject json = new JSONObject(answer);
				JSONArray station_array = json.getJSONArray("content");

				for (int iter = 0; iter < station_array.length(); ++iter) {
					JSONObject station = station_array.getJSONObject(iter);
					Station temp = stations.get(station.getString("nid"));
					temp.setBike_num(station.getString("bike"));
					temp.setPillar_num(station.getString("lock"));
				}

			}
		} catch (Exception e) {

		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(1000);
				getInfo();
				Thread.sleep(99000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
