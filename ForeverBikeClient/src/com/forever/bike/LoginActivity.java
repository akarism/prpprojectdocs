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
	
	private Button cancelBtn,loginBtn;
	private EditText nameEditText,pwdEditText;

	private Globle globleinfo;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
       
        
        cancelBtn = (Button)findViewById(R.id.cancelbtn);
		loginBtn = (Button)findViewById(R.id.loginbtn);
		
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		pwdEditText = (EditText)findViewById(R.id.pwdEditText);
		
		globleinfo=new Globle();
		globleinfo.setUserId(nameEditText.getText().toString());
		globleinfo.setSessionId(null);
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				
				String username=nameEditText.getText().toString();
				String password=pwdEditText.getText().toString();
//				String login_answer = "success";
				String login_answer=login(username,password);
				if (login_answer.equals("success")){
					nameEditText.setText("");
					pwdEditText.setText("");
					Intent intent=new Intent(LoginActivity.this,MainMenuActivity.class);     
					Bundle globledata=new Bundle();
					globledata.putString("userId", globleinfo.getUserId());
					globledata.putString("sessionId", globleinfo.getSessionId());
					intent.putExtras(globledata);
					startActivity(intent);
					//当我从下一个页面返回的时候，直接退出
					
					finish();
				}
				else {
					
					showErrorDialog();
				}
			}
		});	
    }
    
    private String login(String username,String password){
		String urlStr="http://59.78.58.190:8080/forever/loginaction.action?userName="+username+"&password="+password;
//		String urlStr="http://192.168.1.192:8080/forever/loginaction.action?userName="+username+"&password="+password;
//    	String urlStr="http://10.0.2.2:8080/forever/loginaction.action?userName="+username+"&password="+password;
		HttpGet request=new HttpGet(urlStr);
		String answer="success";
		if (globleinfo.getSessionId() != null)
			request.setHeader("Cookie", "JSESSIONID="+globleinfo.getSessionId());
		
		try{
			HttpResponse response=new DefaultHttpClient().execute(request);
			
			if (response.getStatusLine().getStatusCode()==200){
				answer=EntityUtils.toString(response.getEntity());
				JSONObject json = new JSONObject(answer);
				Object succ=json.get("result");
				if (succ.equals("success")){
					Object sessId=json.get("session");
					globleinfo.setSessionId((String)sessId);
					return "success";
				}
				else return "fail";				
			}
		} catch (Exception e){			
		}
		return "fail";
	}
    
    private void showErrorDialog(){
    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
    	builder.setMessage("用户名或密码错误")
    		.setCancelable(false)
    		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
    	AlertDialog alert=builder.create();
    	alert.show();
    }
    
}