package com.andecy.gtalk.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class UpdaterService2 extends Service {

	// �û�����SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	private String hostName = null;
	private String get_url;

	private static final String TAG = "UpdaterService";
	static final int DELAY = 12000;
	private boolean runFlag = false;
	private Updater updater;

	// NotificationManager nm = (NotificationManager)
	// getSystemService(Context.NOTIFICATION_SERVICE);

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		hostName = settingsSpf.getString("HostAdd", null);
		this.updater = new Updater();
		Log.d(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		this.runFlag = true;
		this.updater.start();
		Log.d(TAG, "onStarted");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		Log.d(TAG, "onDestroyed");
	}

	// public void simpleNotice(View view) {
	// // ��BuilderΪandroid.support.v4.app.NotificationCompat.Builder�еģ���ͬ��
	// Builder mBuilder = new Builder(this);
	// // ϵͳ�յ�֪ͨʱ��֪ͨ��������ʾ�����֡�
	// mBuilder.setTicker("����磬2��15�ȣ�΢��");
	//
	// // ֪ͨ����
	// mBuilder.setContentTitle("����Ԥ��");
	// // ֪ͨ����
	// mBuilder.setContentText("����磬2��15�ȣ�΢��");
	// mBuilder.setNumber(6);
	//
	// // ����Ϊ�������ģʽ
	// mBuilder.setOngoing(true);
	//
	// // ��ʾ֪ͨ��id���벻�ظ��������µ�֪ͨ�Ḳ�Ǿɵ�֪ͨ��������һ���ԣ����Զ�֪ͨ���и��£�
	// nm.notify(1, mBuilder.build());
	// }

	private class Updater extends Thread {
		int tint = 0;

		public Updater() {
			super("UpdaterService-Updater");
		}

		private void connNet(String get_url) {
			Log.d(TAG, "connNet+" + get_url);
			new HttpUtils().send(HttpMethod.GET, get_url,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.d(TAG, "connNet--->onFailure");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							Log.d(TAG, "connNet--->onSuccess");
						}
					});
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			UpdaterService2 mService = UpdaterService2.this;

			while (mService.runFlag) {
				String params = "Name=" + "110031013" + "&Pwd="
						+ "12345" + "&Email=" + "123" ;
				get_url = "http://" + hostName + "/servlet/RegDatabase?"
						+ params;
				connNet(get_url);
				Log.d(TAG, "Updater running+" + (tint++));
				try {

					Log.d(TAG, "Updater ran");

					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// TODO: handle exception
					mService.runFlag = false;
				}
			}
		}
	}

}
