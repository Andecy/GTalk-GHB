package com.andecy.gtalk.aty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.bean.Constant;
import com.andecy.gtalk.service.LoginTestAsync;
import com.andecy.gtalk.service.UpdaterService2;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class LoginAty extends Activity implements OnClickListener,
		OnCheckedChangeListener, OnItemSelectedListener {

	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	// 注册＆找回密码
	private TextView tv_new;
	private TextView tv_forgotten;
	// 登陆
	private Context context = LoginAty.this;
	private ProgressDialog mProgressDialog;
	private Button btn_login;
	private EditText et_name;
	private EditText et_pwd;
	// 记住密码＆自动登录
	private CheckBox cb_rmb;
	private CheckBox cb_auto;

	// 測試用btn
	private Button btn_test;

	// 测试用选择器
	private Spinner sp_host;
	private static final String[] STRINGS_SP = { "hehelab.jd-app.com",
			"125.222.103.73", "10.0.0.107:8080", "t_gtalk.jd-app.com",
			"192.168.199.159:8080/MyServletTest", "192.168.1.107" };
	private ArrayAdapter<String> adapter_sp;
	private String hostName = null;
	private String get_url;
	private LoginTestAsync mLAsync;
	private String result = null;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_login);
		initView();
	}

	private void initView() {
		// 初始化用户配置信息
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		ViewUtils.inject(this);
		// 初始化各个控件
		tv_new = (TextView) findViewById(id.tv_login_new);
		tv_forgotten = (TextView) findViewById(id.tv_login_forgotten);
		btn_login = (Button) findViewById(id.btn_login);
		et_name = (EditText) findViewById(id.et_login_account);
		et_pwd = (EditText) findViewById(id.et_login_pwd);
		cb_rmb = (CheckBox) findViewById(id.cb_login_rmb);
		cb_auto = (CheckBox) findViewById(id.cb_login_auto);
		btn_test = (Button) findViewById(id.btn_login_test);
		// 测试用sp
		sp_host = (Spinner) findViewById(id.sp_login_getHostAdd);
		adapter_sp = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, STRINGS_SP);
		adapter_sp
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_host.setAdapter(adapter_sp);
		sp_host.setOnItemSelectedListener(this);
		hostName = sp_host.getSelectedItem().toString();
		// 赋初值
		et_name.setText(settingsSpf.getString("name", null));
		et_pwd.setText(settingsSpf.getString("pwd", null));
		cb_rmb.setChecked(settingsSpf.getBoolean("cb_rmb", false));
		cb_auto.setChecked(settingsSpf.getBoolean("cb_auto", false));
		sp_host.setSelection(settingsSpf.getInt("HostAddPos", 0));
		cb_auto.setOnCheckedChangeListener(this);
		cb_rmb.setOnCheckedChangeListener(this);
		tv_new.setOnClickListener(this);
		tv_forgotten.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_test.setOnClickListener(this);
		btn_login.addTextChangedListener(new NameWatcher());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		mEditor.putString("HostAdd", sp_host.getSelectedItem().toString());
		mEditor.putInt("HostAddPos", sp_host.getSelectedItemPosition());
		mEditor.commit();
		switch (v.getId()) {
		case id.tv_login_new:

			intent.setClass(this, RegisterAty.class);
			startActivity(intent);
			break;
		case id.tv_login_forgotten:
			intent.setClass(this, ForgottenAty.class);
			startActivity(intent);
			break;
		case id.btn_login:
			LoginCheck();
			break;

		case id.btn_login_test:
			intent.setClass(this, UpdaterService2.class);
			startService(intent);
			break;

		}

	}

	private void LoginCheck() {
		mLAsync = new LoginTestAsync(this, cb_rmb, cb_auto);
		String params = "Name=" + et_name.getText().toString() + "&Pwd="
				+ et_pwd.getText().toString();
		get_url = "http://" + hostName + "/servlet/LoginServlet?" + params;

		connNet(get_url);
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
									"正在登陆...");
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
							Toast.makeText(context, "登陆成功！", Toast.LENGTH_LONG)
									.show();
							postLogined(result.split(":")[1],
									result.split(":")[2], result.split(":")[3],
									result.split(":")[4]);
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

	private void postLogined(String name, String pwd, String sign, String level) {
		if (cb_rmb.isChecked()) {
			mEditor.putString("name", name);
			mEditor.putString("pwd", pwd);

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
		finish();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case id.cb_login_auto:
			mEditor.putBoolean("cb_auto", isChecked);
			break;
		case id.cb_login_rmb:
			mEditor.putBoolean("cb_rmb", isChecked);
			if (!isChecked) {
				mEditor.putString("name", null);
				mEditor.putString("pwd", null);
				mEditor.commit();
			}
			break;
		}

		mEditor.commit();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		hostName = sp_host.getSelectedItem().toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
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
				btn_login.setEnabled(true);
			} else {
				btn_login.setEnabled(false);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	}

}
