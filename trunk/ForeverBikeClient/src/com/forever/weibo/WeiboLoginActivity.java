package com.forever.weibo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.ImageItem;

import com.forever.bike.MainMenuActivity;
import com.forever.bike.R;
import com.utl.ConfigHelper;
import com.utl.DataHelper;

import com.utl.MyReceiver;
import com.utl.OAuth;
import com.utl.UserInfo;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WeiboLoginActivity extends Activity {

	private Dialog dialog;

	private DataHelper dbHelper;

	private List<UserInfo> userList;

	private ImageView icon;

	private EditText iconSelect;

	private static final String Select_Name = "select_name";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibologin);

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		icon = (ImageView) findViewById(R.id.icon);
		iconSelect = (EditText) findViewById(R.id.iconSelect);

		initUser();

		ImageButton iconSelectBtn = (ImageButton) findViewById(R.id.iconSelectBtn);
		iconSelectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View diaView = View.inflate(WeiboLoginActivity.this,
						R.layout.weibodialog2, null);
				dialog = new Dialog(WeiboLoginActivity.this, R.style.dialog2);
				dialog.setContentView(diaView);
				dialog.show();

				UserAdapater adapater = new UserAdapater();
				ListView listview = (ListView) diaView.findViewById(R.id.list);
				listview.setVerticalScrollBarEnabled(false);
				listview.setAdapter(adapater);

				listview.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						TextView tv = (TextView) view
								.findViewById(R.id.showName);
						iconSelect.setText(tv.getText());
						ImageView iv = (ImageView) view
								.findViewById(R.id.iconImg);
						icon.setImageDrawable(iv.getDrawable());
						dialog.dismiss();
					}

				});
			}

		});

		ImageButton login = (ImageButton) findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GoHome();
			}

		});
	}

	private void initUser() {
		dbHelper = new DataHelper(this);
		userList = dbHelper.GetUserList(false);
		if (userList.isEmpty()) {
			Intent intent = new Intent();
			intent.setClass(WeiboLoginActivity.this,
					WeiboAuthorizeActivity.class);
			startActivity(intent);
		} else {

			SharedPreferences preferences = getSharedPreferences(Select_Name,
					Activity.MODE_PRIVATE);

			String str = preferences.getString("name", "");
			UserInfo user = null;
			if (str != "") {
				user = GetUserByName(str);
			}
			if (user == null) {
				user = userList.get(0);
			}

			icon.setImageDrawable(user.getUserIcon());
			iconSelect.setText(user.getUserName());
		}
	}

	public class UserAdapater extends BaseAdapter {

		@Override
		public int getCount() {
			return userList.size();
		}

		@Override
		public Object getItem(int position) {
			return userList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.weiboitem_user, null);

			ImageView iv = (ImageView) convertView.findViewById(R.id.iconImg);
			TextView tv = (TextView) convertView.findViewById(R.id.showName);
			UserInfo user = userList.get(position);
			try {
				// ����ͼƬ��ʾ
				iv.setImageDrawable(user.getUserIcon());
				// ������Ϣ
				tv.setText(user.getUserName());

			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

	}

	private void GoHome() {
		if (userList != null) {
			String name = iconSelect.getText().toString();
			UserInfo u = GetUserByName(name);
			if (u != null) {
				ConfigHelper.nowUser = u;
			}
		}
		if (ConfigHelper.nowUser != null) {
			Toast.makeText(WeiboLoginActivity.this,
					"正在发送信息至新浪微博，请稍后" + "\n" + "中国的网速，你懂的", Toast.LENGTH_LONG)
					.show();
			if (MyReceiver.haspic == false) {
				SendToWeibo();
			} else {
				SendToWeibo_pic();
			}

			MyReceiver.haspic = false;
			MyReceiver.dat = null;
			MyReceiver.msg = null;
			Intent intent = new Intent();
			intent.setClass(WeiboLoginActivity.this, MainMenuActivity.class);
			startActivity(intent);

		}
	}

	public void SendToWeibo_pic() {
		UserInfo user = ConfigHelper.nowUser;

		// Weibo weibo = new Weibo("zhulicong89@gmail.com", "ZHULICONG89@SH");

		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);

		Weibo weibo = new Weibo();
		weibo.setToken(user.getToken(), user.getTokenSecret());

		String msg = MyReceiver.msg;
		ImageItem it = null;
		try {
			it = new ImageItem("pic", MyReceiver.dat);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Status status = weibo.uploadStatus(msg, it);
			Toast.makeText(WeiboLoginActivity.this, "发送图片微博成功...",
					Toast.LENGTH_SHORT).show();
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(WeiboLoginActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		// try {
		// Status status=weibo.updateStatus("dajiahao!");
		// } catch (WeiboException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Toast.makeText(WeiboLoginActivity.this, e.getMessage(),
		// Toast.LENGTH_LONG)
		// .show();
		// }
		//

	}

	public void SendToWeibo() {
		OAuth auth = new OAuth();
		// 上传一条文字微博
		String url = "http://api.t.sina.com.cn/statuses/update.json";

		UserInfo user = ConfigHelper.nowUser;

		List params = new ArrayList();

		params.add(new BasicNameValuePair("source", auth.consumerKey));
		params.add(new BasicNameValuePair("status", MyReceiver.msg));

		HttpResponse response = null;

		response = auth.SignRequest(user.getToken(), user.getTokenSecret(),
				url, params);

		Toast.makeText(WeiboLoginActivity.this, "发送微博成功...", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected void onStop() {

		SharedPreferences MyPreferences = getSharedPreferences(Select_Name,
				Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = MyPreferences.edit();
		
		editor.putString("name", iconSelect.getText().toString());
		editor.commit();
		super.onStop();
	}

	public UserInfo GetUserByName(String name) {
		for (UserInfo u : userList) {
			if (u.getUserName().equals(name)) {
				return u;
			}
		}
		return null;
	}
}
