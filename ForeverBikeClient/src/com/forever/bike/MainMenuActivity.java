package com.forever.bike;


import com.forever.weibo.WeiboLoginActivity;
import com.forever.weibo.WeiboMainActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainMenuActivity extends Activity{
	
	private Globle globleinfo; 
	private Bundle bundle;
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.mainmenu);
	     GridView gridview=(GridView) findViewById(R.id.gridView1);
	     gridview.setAdapter(new ImageAdapter(this));
		 
		 Intent intent=getIntent();
		 bundle=intent.getExtras();
		 
		 
		
		 
	 }
	 
	 public class ImageAdapter extends BaseAdapter{
	    	private Context mContext;
	    	
	    	public ImageAdapter(Context c){
	    		mContext=c;   		
	    	}
	    	
	    	public int getCount(){
	    		return mThumbIds.length;
	    	}
	    	
	    	public Object getItem(int position){
	    		return null;
	    	}
	    	
	    	public long getItemId(int position){
	    		return 0;
	    	}
	    	
	    	 public View getView(int position, View convertView, ViewGroup parent) {
	         	// 声明图片视图
	             ImageView imageView;
	             if (convertView == null) {
	             	// 实例化图片视图
	                 imageView = new ImageView(mContext);
	                 // 设置图片视图属性
	                 imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	                 imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	                 imageView.setPadding(8, 8, 8, 8);
	             } else {
	                 imageView = (ImageView) convertView;
	             }
	             // 设置图片视图图片资源
	             imageView.setImageResource(mThumbIds[position]);
	             // 为当前视图添加监听器
	             switch (position) {
	 			case 0:
	 				// 添加账户管理监听器
	 				imageView.setOnClickListener(zhanghuguanliLinstener);
	 				break;
	 			case 1:
	 				// 永久查询监听器
	 				imageView.setOnClickListener(yongjiuchaxunLinstener);
	 				break;
	 				
	 			case 2:
	 				// 积分好礼监听器
	 				imageView.setOnClickListener(jifenghaoliLinstener);
	 				
	 				break;
	 			case 3:
	 				// 有奖调研监听器
	 				imageView.setOnClickListener(youjiangdiaoyanLinstener);
	 				break;
	 				
	 			case 4:
	 				// 满意度监听器
	 				imageView.setOnClickListener(manyiduLinstener);
	 				break;
	 		
	 			case 5:
	 				imageView.setOnClickListener(weiboListener);
	 		
	 			default:
	 				break;
	 			}
	             
	             return imageView;
	         }
	         // 图片资源数组
	         private Integer[] mThumbIds = {
	                 R.drawable.user, R.drawable.search,
	                 R.drawable.gift, R.drawable.research,
	                 R.drawable.heart, R.drawable.weibo
	               
	         };
	    }
	    
	    OnClickListener zhanghuguanliLinstener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtras(bundle);
				
				intent.setClass(MainMenuActivity.this, AccountActivity.class);
				startActivity(intent);
			}
		};
		
		
		OnClickListener yongjiuchaxunLinstener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.setClass(MainMenuActivity.this, QueryMapActivity.class);
				startActivity(intent);
			}
		};
	    OnClickListener jifenghaoliLinstener = new OnClickListener() {
	    	 	
				@Override
				public void onClick(View v) {
					Toast.makeText(MainMenuActivity.this, "积分好礼模块正在开发中...", Toast.LENGTH_SHORT).show();
					
				}
		};
		
		
		OnClickListener youjiangdiaoyanLinstener = new OnClickListener() {
	    	 	
				@Override
				public void onClick(View v) {
					Toast.makeText(MainMenuActivity.this, "有奖调研模块正在开发中...", Toast.LENGTH_SHORT).show();
					
				}
		};
		
		OnClickListener manyiduLinstener = new OnClickListener() {
    	 	
			@Override
			public void onClick(View v) {
				Toast.makeText(MainMenuActivity.this, "满意度模块正在开发中...", Toast.LENGTH_SHORT).show();
				
			}
		};
		
		OnClickListener weiboListener = new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.setClass(MainMenuActivity.this, WeiboMainActivity.class);
				startActivity(intent);
			}
		};
		
		OnClickListener tuichuLinstener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
		};
	}
