package com.forever.weibo;

import java.util.List;

import com.forever.bike.R;
import com.utl.ConfigHelper;
import com.utl.DataHelper;
import com.utl.OAuth;
import com.utl.UserInfo;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class WeiboAuthorizeActivity extends Activity {

	private Dialog dialog;
	private OAuth auth;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weiboauthorizee);

		
		
		View diaView = View.inflate(this, R.layout.weibodialog, null);
		dialog = new Dialog(WeiboAuthorizeActivity.this, R.style.dialog);

		dialog.setContentView(diaView);
		dialog.show();
		ImageButton startBtn = (ImageButton) diaView
				.findViewById(R.id.btn_start);
		
		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(WeiboAuthorizeActivity.this, "正在与新浪微博连接，请稍后", Toast.LENGTH_LONG)
				.show();
				auth = new OAuth();
				String CallBackUrl = "myapp://WeiboAuthorizeActivity";

				auth.RequestAccessToken(WeiboAuthorizeActivity.this,
						CallBackUrl);
			}

		});

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		UserInfo user = auth.GetAccessToken(intent);
		if (user != null) {
			DataHelper helper = new DataHelper(this);
			String uid = user.getUserId();
			if (helper.HaveUserInfo(uid)) {
				helper.UpdateUserInfo(user);
				Log.e("UserInfo", "update");

			} else {
				helper.SaveUserInfo(user);
				Log.e("UserInfo", "add");

			}

			Log.e("will go to login aci", "gogogo");
			try {
				Intent intent_go = new Intent();
			
				intent_go.setClass(WeiboAuthorizeActivity.this,
						WeiboMainActivity.class);
				startActivity(intent_go);
			} catch (Exception e) {

			}
		}
	}
}