package com.andecy.gtalk.aty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.bean.Constant;
import com.andecy.gtalk.service.ForgottenFinishAsync;
import com.andecy.gtalk.service.ForgottenTestAsync;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ForgottenAty extends Activity implements OnClickListener {
	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	// 控件
	private Context context = ForgottenAty.this;
	private ProgressDialog mProgressDialog;
	private TextView tv_code;
	private EditText et_name;
	private EditText et_pwd;
	private EditText et_cpwd;
	private EditText et_email;
	private EditText et_code;
	private Button btn_ok;
	private Button btn_code;
	private String get_url;
	private String hostName = null;
	private String result = null;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	private ForgottenTestAsync mTAsync;
	private ForgottenFinishAsync mFAsync;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_forgotten);
		initView();
		testMethod();
	}

	private void initView() {
		// 初始化用户配置信息
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		// 初始化各个组件
		et_name = (EditText) findViewById(id.et_forgotten_name);
		et_pwd = (EditText) findViewById(id.et_forgotten_pwd);
		et_cpwd = (EditText) findViewById(id.et_forgotten_cpwd);
		et_email = (EditText) findViewById(id.et_forgotten_email);
		et_code = (EditText) findViewById(id.et_forgotten_code);
		btn_ok = (Button) findViewById(id.btn_forgotten_ok);
		btn_code = (Button) findViewById(id.btn_forgotten_code);
		tv_code = (TextView) findViewById(id.tv_forgotten_code);

		btn_ok.setOnClickListener(this);
		btn_code.setOnClickListener(this);
		et_name.addTextChangedListener(new NameWatcher());
		et_code.addTextChangedListener(new CodeWatcher());
		hostName = settingsSpf.getString("HostAdd", null);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case id.btn_forgotten_code:
			if (forgottenCheck()) {
				forgottenPwd();
			}
			break;
		case id.btn_forgotten_ok:
			if (alterCheck()) {
				finishAlter();
			}
			break;
		}
	}

	/**
	 * 用于自动填写注册数据 正常使用请务必删除！
	 */
	private void testMethod() {
		et_name.setText("1100310118");
		et_email.setText("andecy@foxmail.com");
	}

	private boolean forgottenCheck() {
		if ((!TextUtils.isEmpty(et_name.getText()))
				&& (!TextUtils.isEmpty(et_email.getText()))) {
			return true;
		} else {
			Toast.makeText(this, "提交失败，请检查选项是否完整！", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private boolean alterCheck() {
		if ((!TextUtils.isEmpty(et_pwd.getText()))
				&& (!TextUtils.isEmpty(et_cpwd.getText()))) {
			if (et_pwd.getText().toString()
					.equals(et_cpwd.getText().toString())) {
				if (tv_code.getText().toString().split(":")[3].equals(et_code
						.getText().toString())) {
					finish();
					return true;

				} else {
					Toast.makeText(this, "验证码有误，请重新输入！", Toast.LENGTH_LONG)
							.show();
					et_code.setText(null);
					return false;
				}

			} else {
				Toast.makeText(this, "两次密码不一致，请重新输入！", Toast.LENGTH_LONG)
						.show();
				return false;
			}
		} else {
			Toast.makeText(this, "改密失败，请检查选项是否完整！", Toast.LENGTH_LONG).show();
			return false;
		}

	}

	private void forgottenPwd() {
		// Toast.makeText(this, hostName, Toast.LENGTH_LONG).show();
		mTAsync = new ForgottenTestAsync(this, et_pwd, et_cpwd, btn_code,
				et_code, tv_code);
		String params = "Name=" + et_name.getText().toString() + "&Email="
				+ et_email.getText().toString();
		get_url = "http://" + hostName + "/servlet/GetForgottenServlet?"
				+ params;
		connNet(get_url);
		// mTAsync.execute(get_url);
	}

	private void connNet(String get_url) {
		System.out.println(get_url);
		new HttpUtils().send(HttpMethod.GET, get_url,
				new RequestCallBack<String>() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						if (null == mProgressDialog) {
							mProgressDialog = ProgressDialog.show(context, "",
									"正在验证身份...");
						} else {
							mProgressDialog.show();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(context, "服务器连接超时，请稍后重试！",
								Toast.LENGTH_LONG).show();
						if (null != mProgressDialog) {
							mProgressDialog.dismiss();
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						if (null != mProgressDialog) {
							mProgressDialog.dismiss();
						}
						System.out.println(arg0.result);
						setResult(arg0.result);
						int i = -1;
						if (null != result) {
							i = Integer.parseInt(result.split(":")[0]);
						}
						switch (i) {
						case Constant.TEST_OK:
							Toast.makeText(context, "身份验证成功,请完成邮箱验证！",
									Toast.LENGTH_LONG).show();
							setAll(result);
							break;
						case Constant.TEST_FAIL:
							Toast.makeText(context, "身份验证失败！",
									Toast.LENGTH_LONG).show();
							break;
						default:
							Toast.makeText(context, "服务器连接超时，请稍后重试！",
									Toast.LENGTH_LONG).show();
							break;
						}
					}
				});

	}

	private void setAll(String result) {

		et_code.setEnabled(true);
		et_pwd.setEnabled(true);
		et_cpwd.setEnabled(true);
		btn_code.setEnabled(false);
		tv_code.setText(result);

	}

	private boolean finishAlter() {
		mFAsync = new ForgottenFinishAsync(this);
		String params = "Name=" + tv_code.getText().toString().split(":")[1]
				+ "&Pwd=" + et_pwd.getText().toString();
		get_url = "http://" + hostName + "/servlet/ForgottenDatabase?" + params;
		mFAsync.execute(get_url);
		return false;
	}

	public class NameWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			if (s.length() == Constant.ACCOUNT_LENGTH) {
				btn_code.setEnabled(true);
			} else {
				btn_code.setEnabled(false);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	}

	public class CodeWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			if (s.length() == Constant.CODE_LENGTH) {
				btn_ok.setEnabled(true);

			} else {
				btn_ok.setEnabled(false);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	}
}
