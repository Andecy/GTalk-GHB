package com.andecy.gtalk.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.bean.Constant;

public class SearchAsync extends AsyncTask<String, Void, String> {

	private RelativeLayout rl_result;
	private TextView tv_name;
	private TextView tv_email;
	private TextView tv_sign;
	private TextView tv_state;
	
	private Context context;
	private HttpClient mHttpClient;
	private HttpGet mHttpGet;
	private HttpResponse mHttpResponse;
	private HttpEntity mHttpEntity;
	private ProgressDialog mProgressDialog;

	public SearchAsync(Context context, RelativeLayout rl_result) {
		this.context = context;
		this.rl_result = rl_result;
	}

	@Override
	protected void onPreExecute() {
		if (null == mProgressDialog) {
			mProgressDialog = ProgressDialog.show(context, "", "正在验证身份...");
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
			Toast.makeText(context, "找到-->" + result.split(":")[3],
					Toast.LENGTH_LONG).show();
			hasFriend(result.split(":")[1], result.split(":")[2],
					result.split(":")[3], result.split(":")[4]);
			if (result.split(":")[2].equals("null")) {
				Toast.makeText(context, "当前离线！", Toast.LENGTH_LONG).show();
			}
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

	private void hasFriend(String name, String state, String email, String sign) {
		tv_name = (TextView) rl_result.findViewById(id.tv_search_name);
		tv_email = (TextView) rl_result.findViewById(id.tv_search_email);
		tv_sign = (TextView) rl_result.findViewById(id.tv_search_sign);
		tv_state = (TextView) rl_result.findViewById(id.tv_search_state);
		rl_result.setVisibility(View.VISIBLE);
		tv_name.setText("学号: " + name);
		tv_email.setText("邮箱: " + email);
		tv_sign.setText("个性签名: " + sign);
		if (state.equals("null")) {
			tv_state.setText("当前状态: 离线");
		}else {
			tv_state.setText("当前状态: 在线");
		}
	}

}
