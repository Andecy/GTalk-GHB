package com.andecy.gtalk.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.andecy.gtalk.aty.HomeAty;
import com.andecy.gtalk.bean.Constant;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class LoginTestAsync extends AsyncTask<String, Void, String> {

	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;

	private Context context;
	// 记住密码＆自动登录
	private CheckBox cb_rmb;
	private CheckBox cb_auto;

	private HttpClient mHttpClient;
	private HttpGet mHttpGet;
	private HttpResponse mHttpResponse;
	private HttpEntity mHttpEntity;
	private ProgressDialog mProgressDialog;

	public LoginTestAsync(Context context, CheckBox cb_rmb, CheckBox cb_auto) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.cb_auto = cb_auto;
		this.cb_rmb = cb_rmb;
		// 初始化用户配置信息
		settingsSpf = context.getSharedPreferences(PREFS_SETTING_STRING,
				Context.MODE_PRIVATE);
		mEditor = settingsSpf.edit();
	}

	@Override
	protected void onPreExecute() {
		if (null == mProgressDialog) {
			mProgressDialog = ProgressDialog.show(context, "", "正在登陆...");
		} else {
			mProgressDialog.show();
		}
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String result = null;
		new HttpUtils().send(HttpMethod.GET, params[0],
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						System.out.println(arg0.result);
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}

				});
		// try {
		//
		// mHttpClient = new DefaultHttpClient(Constant.httpConfig());
		// mHttpGet = new HttpGet(params[0]);
		// mHttpResponse = mHttpClient.execute(mHttpGet);
		// int bacckCode = mHttpResponse.getStatusLine().getStatusCode();
		// if (bacckCode == 200) {
		// mHttpEntity = mHttpResponse.getEntity();
		// result = EntityUtils.toString(mHttpEntity);
		// } else {
		// result = String.valueOf(Constant.TEST_ERROR_TIMEOUT);
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
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
			Toast.makeText(context, "登陆成功！", Toast.LENGTH_LONG).show();
			postLogined(result.split(":")[1], result.split(":")[2],
					result.split(":")[3], result.split(":")[4]);
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

	private void postLogined(String name, String pwd, String sign, String level) {
		if (cb_rmb.isChecked()) {
			mEditor.putString("name", name);
			mEditor.putString("pwd", pwd);

		} else {

		}
		if (sign.equals("null")) {
			mEditor.putString("sign", null);
		} else {
			mEditor.putString("sign", sign);
		}

		mEditor.commit();
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("name", name);
		intent.putExtras(bundle);
		intent.setClass(context, HomeAty.class);
		context.startActivity(intent);
	}
}
