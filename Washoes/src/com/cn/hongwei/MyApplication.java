package com.cn.hongwei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.cn.hongwei.BaiduLoction.LocationCallback;
import com.cn.washoes.util.Cst;
import com.cn.washoes.util.NetworkAction;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;



public class MyApplication extends Application {

	public static MyHttpClient client;// 网络请求的终端
	public static ArrayList<BaseActivity> list;// 记录所有存在的activity
	public static SharedPreferences sp; // 本地存储SharedPreferences
	public static Editor ed; // 本地存储编辑器Editor

	public static String identity;
	public static boolean loginStat = false;
	public static boolean refresh = false; // 是否需要刷新
	public static NotificationManager mNotificationManager;
	public static String lng = "0";
	public static String lat = "0";
	public static String address = "";
	public static String detail = "";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		/*
		 * 初始化Volley框架的Http工具类
		 */
		ShareSDK.initSDK(this);
		client = MyHttpClient.getInstance(MyApplication.this
				.getApplicationContext());
		BaiduLoction.getInstance().init(this);
		// AppInfo.init(this);
		// RelayoutTool.initScreenScale(this);
		initImageLoader(this);
		// initTaeSDK();
		list = new ArrayList<BaseActivity>();
		// UpgradeManager.getInstence().init(this);
		// 初始化SharedPreferences
		sp = getSharedPreferences("CarService", MODE_PRIVATE);
		ed = sp.edit();

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 初始化JPUSH
		JPushInterface.init(getApplicationContext());
		getLocation();
	}

	private void getLocation() {
		BaiduLoction.getInstance().startLocation();

		BaiduLoction.getInstance().setLocationCallback(new LocationCallback() {

			@Override
			public void locationResult(BDLocation location) {
				if (loginStat) {
					address = location.getProvince() + location.getCity()
							+ location.getDistrict();
					detail = location.getStreet() + location.getStreetNumber();
					lng = String.valueOf(location.getLongitude());
					lat = String.valueOf(location.getLatitude());
					HashMap<String, String> pramer = new HashMap<String, String>();
					pramer.put("identity", identity);
					pramer.put("lng", lng);
					pramer.put("lat", lat);
					client.postWithURL(Cst.HOST, pramer,
							NetworkAction.centerF_location,
							new Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject arg0) {
									// TODO Auto-generated method stub

								}
							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError arg0) {
									// TODO Auto-generated method stub

								}
							});
				}

			}

		});

	}

	// 获取拼接出来的请求字符串
	public static String getUrl(HashMap<String, String> paramter, String url) {
		Iterator iter = paramter.entrySet().iterator();
		int count = 0;
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (count == 0)
				url = url + "?" + key + "=" + val;
			else
				url = url + "&" + key + "=" + val;
			count++;
		}
		return url;
	}

	/**
	 * 初始化图片缓存模块，根据实际需要设置必要的选项。
	 * 
	 * @param ctx
	 */
	private void initImageLoader(Context ctx) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				ctx).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheSize(32 * 1024 * 1024)
				.memoryCacheSize(4 * 1024 * 1024).enableLogging().build();
		ImageLoader.getInstance().init(config);
	}



	
	private String guide;

	public String getGuide() {
		return guide;
	}

	public void setGuide(String guide) {
		this.guide = guide;
		ed.putString("guide", guide);
		ed.commit();
	}



	public NotificationManager getmNotificationManager() {
		return mNotificationManager;
	}

	public void setmNotificationManager(NotificationManager mNotificationManager) {
		this.mNotificationManager = mNotificationManager;
	}

	
	 public static boolean isPhoneNumberValid(String phoneNumber) {
		  boolean isValid = false;
		  /*
		   * 可接受的电话格式有：
		   */
		  String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		  /*
		   * 可接受的电话格式有：
		   */
		  String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		  CharSequence inputStr = phoneNumber;
		  Pattern pattern = Pattern.compile(expression);
		  Matcher matcher = pattern.matcher(inputStr);
		  
		  Pattern pattern2 = Pattern.compile(expression2);
		  Matcher matcher2 = pattern2.matcher(inputStr);
		  if(matcher.matches() || matcher2.matches()) {
			  isValid = true;
		  }
		  return isValid;
	   }



}