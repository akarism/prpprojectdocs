package com.forever.bike;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private BikeApplication app;
	private Button loginBtn;
	private EditText nameEditText, pwdEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		app = (BikeApplication) getApplicationContext();

		loginBtn = (Button) findViewById(R.id.loginbtn);

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		pwdEditText = (EditText) findViewById(R.id.pwdEditText);

		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String username = nameEditText.getText().toString();
				String password = pwdEditText.getText().toString();
				app.setSessionId("");
				// String login_answer = "success";
				boolean login_answer = login(username, password);
				if (login_answer == true) {
					nameEditText.setText("");
					pwdEditText.setText("");
					Intent intent = new Intent(LoginActivity.this,
							MainMenuActivity.class);
					startActivity(intent);
					// 当我从下一个页面返回的时候，直接退出
					finish();
				} else {

					showErrorDialog();
				}
			}
		});
	}

	private boolean login(String username, String password) {
		String urlStr = "http://59.78.58.190:8080/forever/loginaction.action?userName="
				+ username + "&password=" + password;
		// String
		// urlStr="http://10.0.2.2:8080/forever/loginaction.action?userName="+username+"&password="+password;
		HttpGet request = new HttpGet(urlStr);
		String answer = "success";
		if (app.getSessionId() != null)
			request.setHeader("Cookie", "JSESSIONID=" + app.getSessionId());

		try {
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				answer = EntityUtils.toString(response.getEntity());
				JSONObject json = new JSONObject(answer);
				Object succ = json.get("result");
				if (succ.equals("success")) {
					Object sessId = json.get("session");
					app.setSessionId((String) sessId);
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	private void showErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("用户名或密码错误").setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

}