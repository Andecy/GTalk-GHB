package com.andecy.gtalk.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.andecy.gtalk.bean.Constant;

public class SignAsync extends AsyncTask<String, Void, String> {
	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;

	private Context context;

	private HttpClient mHttpClient;
	private HttpGet mHttpGet;
	private HttpResponse mHttpResponse;
	private HttpEntity mHttpEntity;
	private ProgressDialog mProgressDialog;

	public SignAsync(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		if (null == mProgressDialog) {
			mProgressDialog = ProgressDialog.show(context, "", "正在修改...");
		} else {
			mProgressDialog.show();
		}
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			mHttpClient = new DefaultHttpClient(Constant.httpConfig());
			mHttpGet = new HttpGet(params[0]);
			mHttpResponse = mHttpClient.execute(mHttpGet);
			int bacckCode = mHttpResponse.getStatusLine().getStatusCode();
			if (bacckCode == 200) {
				mHttpEntity = mHttpResponse.getEntity();
				result = EntityUtils.toString(mHttpEntity);
			} else {
				result = String.valueOf(Constant.TEST_ERROR_TIMEOUT);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		Log.i("TAG", "onPostExecute--->start：" + result);
		int i = -1;
		if (null != result) {
			i = Integer.parseInt(result.split(":")[0]);
		}
		Log.i("TAG", "onPostExecute--->parseInt");
		switch (i) {
		case Constant.TEST_OK:
			Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();
			postSigned();
			break;
		case Constant.TEST_FAIL:
			Toast.makeText(context, "身份验证失败！", Toast.LENGTH_LONG).show();
			break;
		default:
			Toast.makeText(context, "服务器连接超时，请稍后重试！", Toast.LENGTH_LONG).show();
			break;
		}
		if (null != mProgressDialog) {
			mProgressDialog.dismiss();
		}
		super.onPostExecute(result);
	}

	private void postSigned() {
		
	}

}
