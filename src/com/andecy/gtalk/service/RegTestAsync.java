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

public class RegTestAsync extends AsyncTask<String, Void, String> {

	private Context context;
	private HttpClient mHttpClient;
	private HttpGet mHttpGet;
	private HttpResponse mHttpResponse;
	private HttpEntity mHttpEntity;
	private ProgressDialog mProgressDialog;

	private EditText et_pwd;
	private EditText et_cpwd;
	private EditText et_code;
	private TextView tv_code;
	private Button btn_code;

	public RegTestAsync(Context context, EditText et_pwd,
			EditText et_cpwd, TextView tv_code,EditText et_code,Button btn_code) {
		this.context = context;
		this.et_pwd = et_pwd;
		this.et_cpwd = et_cpwd;
		this.tv_code = tv_code;
		this.et_code = et_code;
		this.btn_code = btn_code;
	}

	@Override
	protected void onPreExecute() {
		if (null == mProgressDialog) {
			mProgressDialog = ProgressDialog.show(context, "", "正在验证...");
		} else {
			mProgressDialog.show();
		}
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		String result = null;
		try {

			mHttpClient = new DefaultHttpClient(Constant.httpConfig());
			//String s=new String("欲转换字符串".getBytes(),"utf-8");
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
			Toast.makeText(context, "注册即将成功,请完成邮箱验证！", Toast.LENGTH_LONG).show();
			setAll(result);

			break;
		case Constant.TEST_ERROR_NAMES:
			Toast.makeText(context, "验证失败，用户名已被注册！", Toast.LENGTH_LONG).show();
			break;
		case Constant.TEST_ERROR_EMAILS:
			Toast.makeText(context, "验证失败，邮箱已被注册！", Toast.LENGTH_LONG).show();
			break;
		case Constant.TEST_NULL:
			Toast.makeText(context, "资料不完整！", Toast.LENGTH_LONG).show();
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
		et_pwd.setText(null);
		et_cpwd.setText(null);
		btn_code.setEnabled(false);
		tv_code.setText(result);

	}

}
