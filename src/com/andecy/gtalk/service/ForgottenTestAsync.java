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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andecy.gtalk.bean.Constant;

public class ForgottenTestAsync extends AsyncTask<String, Void, String> {

	private Context context;
	private HttpClient mHttpClient;
	private HttpGet mHttpGet;
	private HttpResponse mHttpResponse;
	private HttpEntity mHttpEntity;
	private ProgressDialog mProgressDialog;

	private EditText et_pwd;
	private EditText et_cpwd;
	private EditText et_code;
	private Button btn_code;
	private TextView tv_code;

	public ForgottenTestAsync(Context context, EditText et_pwd,
			EditText et_cpwd, Button btn_code, EditText et_code,
			TextView tv_code) {
		this.context = context;
		this.et_pwd = et_pwd;
		this.et_cpwd = et_cpwd;
		this.btn_code = btn_code;
		this.et_code = et_code;
		this.tv_code = tv_code;
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
			Toast.makeText(context, "身份验证成功,请完成邮箱验证！", Toast.LENGTH_LONG)
					.show();
			setAll(result);
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

	private void setAll(String result) {
		et_code.setEnabled(true);
		et_pwd.setEnabled(true);
		et_cpwd.setEnabled(true);
		btn_code.setEnabled(false);
		tv_code.setText(result);

	}
}
