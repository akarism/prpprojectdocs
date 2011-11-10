package com.forever.bike;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyPositionItemizedOverlay extends ItemizedOverlay{
	
	private ArrayList<OverlayItem> mOverlays=new ArrayList<OverlayItem>();
	
	private Context context;
	
	public MyPositionItemizedOverlay(Drawable defaultMarker){
		super(boundCenterBottom(defaultMarker));
	}
	
	public MyPositionItemizedOverlay(Drawable defaultMarker,Context context){
		this(defaultMarker);
		this.context=context;
	}
	
	public void addOverlay(OverlayItem overlay){
		mOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i){
		return mOverlays.get(i);
	}
	
	
	@Override
	public int size(){
		return mOverlays.size();
	}
	
	
	
	@Override
	protected boolean onTap(int index){
		OverlayItem item=mOverlays.get(index);
		
		/*
		//用AlertDialog显示
		AlertDialog.Builder dialog=new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());  得到num
		dialog.show();
		*/
		//用Toast显示
		Toast.makeText(context, item.getTitle()+"\n"+item.getSnippet(), Toast.LENGTH_SHORT).show();
		return true;
	
	
		
	}
	

	

}
