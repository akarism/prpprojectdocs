package com.forever.bike;

import android.app.Application;

public class BikeApplication extends Application{

	private String sessionId;
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
