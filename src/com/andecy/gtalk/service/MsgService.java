package com.andecy.gtalk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.andecy.gtalk.R;
import com.andecy.gtalk.bean.Constant;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MsgService extends Service {

	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	private String hostName = null;
	private String get_url;
	private Context context = MsgService.this;
	private MediaPlayer mediaPlayer;
	private String result = null;
	static final int DELAY = 60000;
	private static final String TAG = "MsgService";
	private boolean runFlag = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mediaPlayer = new MediaPlayer(); // 媒体播放器对象
		// 初始化用户配置信息

		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		hostName = settingsSpf.getString("HostAdd", null);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		 super.onStartCommand(intent, flags, startId);
		mediaPlayer = MediaPlayer.create(this, R.raw.you_re_beautiful);
		 mediaPlayer.start();
		// receive(intent);

		Toast.makeText(context, intent.getStringExtra("name"),
				Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}

	private void receive(Intent intent) {
		while (true) {
			Toast.makeText(context, intent.getStringExtra("name"),
					Toast.LENGTH_SHORT).show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private void connNet(String get_url) {
		System.out.println(get_url);
		new HttpUtils().send(HttpMethod.GET, get_url,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						setResult(arg0.result);
						int i = -1;
						if (null != result) {
							i = Integer.parseInt(result);
						}
						switch (i) {
						case Constant.TEST_OK:
							Toast.makeText(context, "新消息来了！", Toast.LENGTH_LONG)
									.show();
							break;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});

	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mediaPlayer.stop();
	}

	private class Updater extends Thread {
		public Updater() {
			super("MsgService-Msg");
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			MsgService mService = MsgService.this;
			while (mService.runFlag) {
				Log.d(TAG, "Msg running");
				try {
					Log.d(TAG, "Msg ran");
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// TODO: handle exception
				}
			}
			super.run();
		}

	}
}
