package com.forever.bike;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;

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


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class QueryMapActivity extends MapActivity{
	private LocationManager locationManager;
	private MapView mapView;
	private MapController mc;
	private List<Overlay> mapOverlays;
	private MyPositionItemizedOverlay itemizedOverlay;
	private Drawable drawable_bike,drawable_me;
	private double lat,lng;
    private EditText MySearchText;
    private Button MyPos,MySearch;
    private String provider;
    private Location location;
	private station shekou_array[];
	private Globle globleinfo;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.querymap);
    	
    	globleinfo=new Globle();
    	Intent intent=getIntent();
    	Bundle bundle=intent.getExtras();
    	globleinfo.setUserId(bundle.getString("userId"));
    	globleinfo.setSessionId(bundle.getString("sessionId"));
    	
    	
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	mapView=(MapView) findViewById(R.id.mapview1);
    	LinearLayout zoomLayout=(LinearLayout)findViewById(R.id.zoom1);
    	View zoomView=mapView.getZoomControls();
    	
    	zoomLayout.addView(zoomView,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	mapView.displayZoomControls(true);
    	MySearchText=(EditText) findViewById(R.id.editText1);
    	//http://localhost:8080/forever/getallnetinfo.action
    	MyPos=(Button) findViewById(R.id.button1);
    	MySearch=(Button) findViewById(R.id.button2);
    	
    	
    	mc=mapView.getController();
    	new_stations();
    	checkin_info();
    	
    	//criteria 封装了获得locationprovider的条件
    	Criteria criteria=new Criteria();
    	//较高精度
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	//设置是否需要高度信息
    	criteria.setAltitudeRequired(false);  
    	//设置是否需要方位信息
        criteria.setBearingRequired(false);  
        //设置是否需要产生费用
        criteria.setCostAllowed(false);  
        
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        
    	provider=locationManager.getBestProvider(criteria, true);
    	location=locationManager.getLastKnownLocation(provider);
    	
    	updateWithNewLocation(location);
    	//设置6000ms，监听一次
    	locationManager.requestLocationUpdates(provider, 6000, 0, locationListener);
    	
    	String ttt="hello";
    	for (int i=0;i<=15;i++){
    		GeoPoint station_=new GeoPoint(
            		(int) (shekou_array[i].getlat()*1E6),
            		(int) (shekou_array[i].getlng()*1E6));
    		OverlayItem overlayitemi=new OverlayItem(station_,shekou_array[i].getName(),"锁住数量:"+shekou_array[i].getPillar_num()+"\n自行车数量:"+shekou_array[i].getBike_num());
    		ttt+=shekou_array[i].getPillar_num();
    		itemizedOverlay.addOverlay(overlayitemi);
    	}
    //	MySearchText.setText(ttt);
    	
    	/*
    	
    	GeoPoint station_=new GeoPoint(
        		(int) (31.042345*1E6),
        		(int) (121.434631*1E6));
		OverlayItem overlayitemi=new OverlayItem(station_,"e","now num");
		itemizedOverlay.addOverlay(overlayitemi);
    	*/
    	
    	mapOverlays.add(itemizedOverlay);
    	
    	MyPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			
				updateWithNewLocation(location);
			}
		});
    	
    	
    
    	MySearch.setOnClickListener(new OnClickListener() {
    		GeoPoint gp=null;
			@Override
			public void onClick(View v) {
				try{
					
					String strSearchAddress=MySearchText.getText().toString();
					if (!strSearchAddress.equals("")){
						
							JSONObject jsonObject=getLocationInfo(strSearchAddress);
							Double lon1 = new Double(0);    
					        Double lat1 = new Double(0);    
					    
					        try {    
					        	
					        	 lon1 = ((JSONArray)jsonObject.get("results")).getJSONObject(0)    
					                .getJSONObject("geometry").getJSONObject("location")    
					                .getDouble("lng");    
					    
					            lat1 = ((JSONArray)jsonObject.get("results")).getJSONObject(0)    
					                .getJSONObject("geometry").getJSONObject("location")    
					                .getDouble("lat"); 
					            gp=new GeoPoint(
					            		(int) (lat1*1E6),
					            		(int) (lon1*1E6));
					        	mc.animateTo(gp);
					    
					        } catch (JSONException e) {    
					            // TODO Auto-generated catch block    
					            e.printStackTrace();    
					        }    
					    				
					}
				}catch (Exception e){
					MySearchText.setText("error");
				}
			
				
			}
		});
    }
    
    @Override
    protected boolean isRouteDisplayed(){
    	//TODO Auto-generated method stub
    	return false;
    }
    
  
    
    private void updateWithNewLocation(Location location){
    	lat=22.489283;
    	lng=113.924961;
    	if (location!=null){
    		 lat = location.getLatitude()-0.001950;
       		 lng = location.getLongitude()+0.004978;
       		
    	}
    	GeoPoint p=new GeoPoint(
        		(int) (lat*1E6),
        		(int) (lng*1E6));
    	mc.animateTo(p);
    	
    	mc.setZoom(17);
    	mapView.invalidate();
    	mapView.displayZoomControls(true);
    	
    	drawable_bike=this.getResources().getDrawable(R.drawable.grown);
    	drawable_me=this.getResources().getDrawable(R.drawable.grown0);
    	mapOverlays=mapView.getOverlays();
    	itemizedOverlay=new MyPositionItemizedOverlay(drawable_bike,this);
    	OverlayItem overlayitem=new OverlayItem(p,"我的位置","呵呵");
    	itemizedOverlay.addOverlay(overlayitem);
    
    	mapOverlays.add(itemizedOverlay);
    }
    
    private final LocationListener locationListener=new LocationListener(){
    	@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}
		@Override
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
		}
    };
    
    
    //这个类，是来此http://blog.csdn.net/dadoneo/archive/2011/03/17/6255486.aspx的介绍。
    //android2.2貌似有一个bug，不支持GeoCoder，这个通过http访问谷歌，再得到location
    public static JSONObject getLocationInfo(String address) {    
        
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" 
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
    
    
    public void checkin_info(){
    	
//    	
//    	Timer timer=new Timer();
//    	timer.schedule(
//				 new java.util.TimerTask() { 
//					 public void run() {
//						 
//						 String urlStr="http://59.78.58.190:8080/forever/getallnetinfo.action";
//						 HttpGet request=new HttpGet(urlStr);
//						 if (globleinfo.getSessionId() != null)
//								request.setHeader("Cookie", "JSESSIONID="+globleinfo.getSessionId());
//							
//						 String answer;	
//							
//						  try{
//							  HttpResponse response=new DefaultHttpClient().execute(request);
//								
//							  if (response.getStatusLine().getStatusCode()==200){
//								  answer=EntityUtils.toString(response.getEntity());
//								  
//								  JSONObject json = new JSONObject(answer);
//								  JSONArray station_array=json.getJSONArray("content");
//								
//								  for(int iter=0 ; iter<station_array.length() ; ++ iter){
//									  JSONObject stations=station_array.getJSONObject(iter);
//									  shekou_array[iter].setBike_num(stations.getString("bike"));
//									  shekou_array[iter].setPillar_num(stations.getString("lock"));
//									 
//								  }
//								  
//							  }
//							} catch (Exception e){
//								
//							}
//					 }
//				 } , 0, 2*60*1000);        //1分钟更新一次        
    	
    	
//    	 String urlStr="http://59.78.58.190:8080/forever/getallnetinfo.action";
//    	 String urlStr="http://192.168.1.192:8080/forever/getallnetinfo.action";
    	 String urlStr="http://10.0.2.2:8080/forever/getallnetinfo.action";
    	 
    	 HttpGet request=new HttpGet(urlStr);
		 if (globleinfo.getSessionId() != null)
				request.setHeader("Cookie", "JSESSIONID="+globleinfo.getSessionId());
			
		 String answer;	
			
		  try{
			  HttpResponse response=new DefaultHttpClient().execute(request);
				
			  if (response.getStatusLine().getStatusCode()==200){
				  answer=EntityUtils.toString(response.getEntity());
				  
				  JSONObject json = new JSONObject(answer);
				  JSONArray station_array=json.getJSONArray("content");
				
				  for(int iter=0 ; iter<station_array.length() ; ++ iter){
					  JSONObject stations=station_array.getJSONObject(iter);
					  shekou_array[iter].setBike_num(stations.getString("bike"));
					  shekou_array[iter].setPillar_num(stations.getString("lock"));
				  }
				  
			  }
			} catch (Exception e){
				
			}
    }
    
    
    public void new_stations(){
    
    	shekou_array=new station[20];
    	for (int i=0;i<20;i++)
    		shekou_array[i]=new station();
    	shekou_array[0].setName("鲸山别墅");
    	shekou_array[0].setAddress("");
    	shekou_array[0].setlat((float) 22.481303);
    	shekou_array[0].setlng((float) 113.911496);
    	

    	shekou_array[1].setName("新时代广场");
    	shekou_array[1].setAddress("");
    	shekou_array[1].setlat((float) 22.482892);
    	shekou_array[1].setlng((float) 113.912722);
    	

    	shekou_array[2].setName("泰阁公寓");
    	shekou_array[2].setAddress("");
    	shekou_array[2].setlat((float) 22.48452);
    	shekou_array[2].setlng((float) 113.912671);
    	

    	shekou_array[3].setName("半山社区中心");
    	shekou_array[3].setAddress("");
    	shekou_array[3].setlat((float) 22.488867);
    	shekou_array[3].setlng((float) 113.912795);
    	

    	shekou_array[4].setName("金融中心");
    	shekou_array[4].setAddress("");
    	shekou_array[4].setlat((float) 22.484684);
    	shekou_array[4].setlng((float) 113.915461);
    	

    	shekou_array[5].setName("南海意库");
    	shekou_array[5].setAddress("");
    	shekou_array[5].setlat((float) 22.484148);
    	shekou_array[5].setlng((float) 113.919017);
    	

    	shekou_array[6].setName("招商大厦");
    	shekou_array[6].setAddress("");
    	shekou_array[6].setlat((float) 22.49023);
    	shekou_array[6].setlng((float) 113.921919);
    	

    	shekou_array[7].setName("科技大厦二期");
    	shekou_array[7].setAddress("");
    	shekou_array[7].setlat((float) 22.49537);
    	shekou_array[7].setlng((float) 113.916995);
    	

    	shekou_array[8].setName("南山公园登山口");
    	shekou_array[8].setAddress("");
    	shekou_array[8].setlat((float) 22.497667);
    	shekou_array[8].setlng((float) 113.917446);
    	

    	shekou_array[9].setName("联合大厦");
    	shekou_array[9].setAddress("");
    	shekou_array[9].setlat((float) 22.496683);
    	shekou_array[9].setlng((float) 113.920085);
    	

    	shekou_array[10].setName("桃花园三期");
    	shekou_array[10].setAddress("");
    	shekou_array[10].setlat((float) 22.503171);
    	shekou_array[10].setlng((float) 113.916695);
    	
    	
    	shekou_array[11].setName("风华大剧院");
    	shekou_array[11].setAddress("");
    	shekou_array[11].setlat((float) 22.496956);
    	shekou_array[11].setlng((float) 113.924382);
    	
    	shekou_array[12].setName("花园城中心");
    	shekou_array[12].setAddress("");
    	shekou_array[12].setlat((float) 22.502754);
    	shekou_array[12].setlng((float) 113.923084);
    	
    	shekou_array[13].setName("雍华府");
    	shekou_array[13].setAddress("");
    	shekou_array[13].setlat((float) 22.501634);
    	shekou_array[13].setlng((float) 113.927118);
    	
    	shekou_array[14].setName("爱榕园");
    	shekou_array[14].setAddress("");
    	shekou_array[14].setlat((float) 22.499796);
    	shekou_array[14].setlng((float) 113.929328);
    	
    	shekou_array[15].setName("海月花园");
    	shekou_array[15].setAddress("");
    	shekou_array[15].setlat((float) 22.495974);
    	shekou_array[15].setlng((float) 113.936505);
    	
    }
  
}