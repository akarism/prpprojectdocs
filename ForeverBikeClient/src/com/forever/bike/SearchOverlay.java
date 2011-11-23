package com.forever.bike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

public class SearchOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays;
	private Context context;

	public SearchOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public SearchOverlay(Drawable defaultMarker, Context context) {
		this(defaultMarker);
		this.context = context;
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
		OverlayItem item = mOverlays.get(index);

		// 用Toast显示
		Toast.makeText(context, item.getSnippet(), Toast.LENGTH_SHORT).show();
		return true;

	}
}
