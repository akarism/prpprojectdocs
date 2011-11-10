package com.forever.weibo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.forever.bike.R;
import com.utl.DataHelper;
import com.utl.OAuth;
import com.utl.UserInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class WeiboMainActivity extends Activity implements Runnable {
	private DataHelper dbHelper;

	private List<UserInfo> userList;

	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibomain);
		

		
		
		dbHelper = new DataHelper(this);
		userList = dbHelper.GetUserList(true);

		Thread thread = new Thread(this);
		thread.start();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			

			if (userList.isEmpty()) {
				Thread.sleep(500);
				Intent intent = new Intent();
			
				intent.setClass(WeiboMainActivity.this,
						WeiboAuthorizeActivity.class);
				startActivity(intent);
			} else {
				try {
					UpdateUserInfo(this, userList);
					
					Intent intent = new Intent();
				
					intent.setClass(WeiboMainActivity.this,
							WeiboLoginActivity.class);
					startActivity(intent);
				} catch (Exception e) {

				}

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	public void UpdateUserInfo(Context context, List<UserInfo> userList) {
		DataHelper dbHelper = new DataHelper(context);
		OAuth auth = new OAuth();
		String url = "http://api.t.sina.com.cn/users/show.json";
		Log.e("userCount", userList.size() + "");
		for (UserInfo user : userList) {
			if (user != null) {
				List params = new ArrayList();
				params.add(new BasicNameValuePair("source", auth.consumerKey));
				params.add(new BasicNameValuePair("user_id", user.getUserId()));
				HttpResponse response = null;
				Log.e("userMessage", user.getUserId() + " " + user.getToken()
						+ " " + user.getTokenSecret());

				response = auth.SignRequest(user.getToken(),
						user.getTokenSecret(), url, params);

				Log.e("middle", "hahahahaha");
				if (200 == response.getStatusLine().getStatusCode()) {
					try {
						InputStream is = response.getEntity().getContent();
						Reader reader = new BufferedReader(
								new InputStreamReader(is), 4000);
						StringBuilder buffer = new StringBuilder((int) response
								.getEntity().getContentLength());
						try {
							char[] tmp = new char[1024];
							int l;
							while ((l = reader.read(tmp)) != -1) {
								buffer.append(tmp, 0, l);
							}
						} finally {
							reader.close();
						}
						String string = buffer.toString();
						response.getEntity().consumeContent();
						JSONObject data = new JSONObject(string);
						String ImgPath = data.getString("profile_image_url");
						Bitmap userIcon = DownloadImg(ImgPath);

						String userName = data.getString("screen_name");
						dbHelper.UpdateUserInfo(userName, userIcon,
								user.getUserId());
						Log.e("ImgPath", ImgPath);

					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		dbHelper.Close();
	}

	public Bitmap DownloadImg(String url) {
		URL uri;
		Bitmap bm = null;
		try {
			uri = new URL(url);
			// ��ȡͼƬ�����
			InputStream is = uri.openStream();
			// ���ͼƬ
			bm = BitmapFactory.decodeStream(is);
		
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}
}