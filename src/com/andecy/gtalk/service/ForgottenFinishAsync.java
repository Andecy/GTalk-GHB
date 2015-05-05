package com.andecy.gtalk.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.andecy.gtalk.bean.Constant;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ForgottenFinishAsync extends AsyncTask<String, Void, String> {

	private Context context;
	private HttpClient mHttpClient;
	private HttpGet mHttpGet;
	private HttpResponse mHttpResponse;
	private HttpEntity mHttpEntity;
	private ProgressDialog mProgressDialog;
	public ForgottenFinishAsync(Context context) {
		this.context = context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			mHttpClient = new DefaultHttpClient(Constant.httpConfig());
			mHttpGet = new HttpGet(params[0]);
			Log.i("TAG", "params[0]--->"+params[0]);
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
		// TODO Auto-generated method stub
		Log.i("TAG", "onPostExecute--->start：" + result);
		int i = -1;
		if (null != result) {
			i = Integer.parseInt(result);
		}
		Log.i("TAG", "onPostExecute--->parseInt");
		switch (i) {
		case Constant.TEST_OK:
			Toast.makeText(context, "改密完成，可以登录GTalk了！", Toast.LENGTH_LONG).show();
			
			break;
		case Constant.TEST_FAIL:
			Toast.makeText(context, "改密失败！", Toast.LENGTH_LONG).show();
			break;
		default:
			Toast.makeText(context, "服务器连接超时，请稍后重试！", Toast.LENGTH_LONG).show();
			break;
		}
		super.onPostExecute(result);
	}

}
