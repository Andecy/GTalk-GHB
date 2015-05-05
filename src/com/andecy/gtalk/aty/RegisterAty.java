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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.bean.Constant;
import com.andecy.gtalk.service.RegFinishAsync;
import com.andecy.gtalk.service.RegTestAsync;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class RegisterAty extends Activity implements OnClickListener {
	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	// 控件
	private Context context = RegisterAty.this;
	private ProgressDialog mProgressDialog;
	private TextView tv_code;
	private EditText et_name;
	private EditText et_pwd;
	private EditText et_cpwd;
	private EditText et_email;
	private EditText et_code;
	private CheckBox cb_agree;
	private Button btn_ok;
	private Button btn_code;
	private String get_url;
	private String hostName = null;

	private RegTestAsync mTAsync;
	private RegFinishAsync mFAsync;
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
		setContentView(layout.aty_register);
		initView();
		// 测试完毕请删除
		testMethod();
	}

	private void initView() {
		// 初始化用户配置信息
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		// 初始化各个组件
		et_name = (EditText) findViewById(id.et_register_name);
		et_pwd = (EditText) findViewById(id.et_register_pwd);
		et_cpwd = (EditText) findViewById(id.et_register_cpwd);
		et_email = (EditText) findViewById(id.et_register_email);
		et_code = (EditText) findViewById(id.et_register_code);
		cb_agree = (CheckBox) findViewById(id.cb_register_agree);
		btn_ok = (Button) findViewById(id.btn_register_ok);
		btn_code = (Button) findViewById(id.btn_register_code);
		tv_code = (TextView) findViewById(id.tv_register_code);

		btn_ok.setOnClickListener(this);
		btn_code.setOnClickListener(this);
		et_name.addTextChangedListener(new NameWatcher());
		et_code.addTextChangedListener(new CodeWatcher());
		hostName = settingsSpf.getString("HostAdd", null);
	}

	/**
	 * 用于自动填写注册数据 正常使用请务必删除！
	 */
	private void testMethod() {
		et_name.setText("1100310118");
		et_pwd.setText("123456");
		et_cpwd.setText("123456");
		et_email.setText("andecy@foxmail.com");
		cb_agree.setChecked(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case id.btn_register_code:
			if (registerCheck()) {
				registerAcount();
			}
			break;
		case id.btn_register_ok:
			if (tv_code.getText().toString().split(":")[4].equals(et_code
					.getText().toString())) {
				finishEmail();
				finish();
			} else {
				Toast.makeText(this, "验证码有误，请重新输入！", Toast.LENGTH_LONG).show();
				et_code.setText(null);
			}
			break;
		}
	}

	private boolean registerCheck() {
		if ((!TextUtils.isEmpty(et_name.getText()))
				&& (!TextUtils.isEmpty(et_pwd.getText()))
				&& (!TextUtils.isEmpty(et_cpwd.getText()))
				&& (!TextUtils.isEmpty(et_email.getText()))
				&& (cb_agree.isChecked())) {
			if (et_pwd.getText().toString()
					.equals(et_cpwd.getText().toString())) {
				return true;
			} else {
				Toast.makeText(this, "两次密码不一致，请重新输入！", Toast.LENGTH_LONG)
						.show();
				return false;
			}
		} else {
			Toast.makeText(this, "注册失败，请检查选项是否完整！", Toast.LENGTH_LONG).show();
			return false;
		}

	}

	private boolean registerAcount() {
		mTAsync = new RegTestAsync(this, et_pwd, et_cpwd, tv_code, et_code,
				btn_code);
		String params = "Name=" + et_name.getText().toString() + "&Pwd="
				+ et_pwd.getText().toString() + "&Email="
				+ et_email.getText().toString();
		get_url = "http://" + hostName + "/servlet/GetRegisterServlet?"
				+ params;
		connNet(get_url);
		// mTAsync.execute(get_url);
		return false;
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
						// if (null != mProgressDialog) {
						// mProgressDialog.dismiss();
						// }
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						if (null != mProgressDialog) {
							mProgressDialog.hide();
						}
						System.out.println(arg0.result);
						setResult(arg0.result);
						int i = -1;
						if (null != result) {
							i = Integer.parseInt(result.split(":")[0]);
						}
						switch (i) {
						case Constant.TEST_OK:
							Toast.makeText(context, "注册即将成功,请完成邮箱验证！",
									Toast.LENGTH_LONG).show();
							setAll(result);
							break;
						case Constant.TEST_ERROR_NAMES:
							Toast.makeText(context, "验证失败，用户名已被注册！",
									Toast.LENGTH_LONG).show();
							break;
						case Constant.TEST_ERROR_EMAILS:
							Toast.makeText(context, "验证失败，邮箱已被注册！",
									Toast.LENGTH_LONG).show();
							break;
						case Constant.TEST_NULL:
							Toast.makeText(context, "资料不完整！", Toast.LENGTH_LONG)
									.show();
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
		et_pwd.setText(null);
		et_cpwd.setText(null);
		btn_code.setEnabled(false);
		tv_code.setText(result);

	}

	private boolean finishEmail() {
		mFAsync = new RegFinishAsync(this);
		String params = "Name=" + tv_code.getText().toString().split(":")[1]
				+ "&Pwd=" + tv_code.getText().toString().split(":")[2]
				+ "&Email=" + tv_code.getText().toString().split(":")[3];
		get_url = "http://" + hostName + "/servlet/RegDatabase?" + params;
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
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(getCurrentFocus()
						.getApplicationWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
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
