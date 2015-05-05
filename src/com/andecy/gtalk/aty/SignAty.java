package com.andecy.gtalk.aty;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
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
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class SignAty extends Activity implements OnClickListener, TextWatcher {
	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	// 控件声明
	private TextView tv_hint;
	private EditText et_sign;
	private Button btn_ok;
	private Button btn_cancel;
	//联网
	private Context context = SignAty.this;
	private String hostName = null;
	private String get_url;
	private String result = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_sign);
		initView();
	}

	private void initView() {
		// 初始化用户配置信息
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		// 初始化各个组件
		tv_hint = (TextView) findViewById(id.tv_sign_hint);
		et_sign = (EditText) findViewById(id.et_sign);
		btn_ok = (Button) findViewById(id.btn_sign_ok);
		btn_cancel = (Button) findViewById(id.btn_sign_cancel);

		btn_cancel.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		et_sign.addTextChangedListener(this);
		// 赋初值
		hostName = settingsSpf.getString("HostAdd", null);
		et_sign.setText(settingsSpf.getString("sign", null));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case id.btn_sign_ok:
			changeSign();
			break;
		}
		finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

			tv_hint.setText(s.length()+"/24");


	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}
	
	private void changeSign() {
		String params = "Name=" + getIntent().getExtras().getString("name")+ "&Sign="
				+ et_sign.getText().toString();
		get_url = "http://" + hostName + "/servlet/SignDatabase?" + params;
		connNet4Sign(get_url);
		mEditor.putString("sign", et_sign.getText().toString());
		mEditor.commit();
	}
	
	private void connNet4Sign(String get_url) {
		System.out.println(get_url);
		new HttpUtils().send(HttpMethod.GET, get_url,
				new RequestCallBack<String>() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(context, "onFailure-->服务器连接超时，请稍后重试！",
								Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
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
							break;
						case Constant.TEST_FAIL:
							Toast.makeText(context, "身份验证失败！",
									Toast.LENGTH_LONG).show();
							break;
						default:
							Toast.makeText(context, "onSucess-->服务器连接超时，请稍后重试！",
									Toast.LENGTH_LONG).show();
							break;
						}
					}
				});

	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
}
