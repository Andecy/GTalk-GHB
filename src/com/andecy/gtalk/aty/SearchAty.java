package com.andecy.gtalk.aty;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.bean.Constant;
import com.andecy.gtalk.service.SearchAsync;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class SearchAty extends Activity implements TextWatcher, OnClickListener {
	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	// 控件
	private Context context = SearchAty.this;
	private ProgressDialog mProgressDialog;

	private String result = null;
	private EditText et_name;
	private Button btn_ok;
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	private RelativeLayout rl_result;
	// 隐藏控件
	private Button btn_add;

	private SearchAsync mSAsync;
	private String get_url;
	private String hostName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_search);
		initView();
		testMethod();
	}

	private void initView() {
		// 初始化用户配置信息
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		// 初始化各个组件
		et_name = (EditText) findViewById(id.et_search);
		btn_ok = (Button) findViewById(id.btn_search_ok);
		btn_add = (Button) findViewById(id.btn_search_add);
		rl_result = (RelativeLayout) findViewById(id.rl_search_result);
		rl_result.getBackground().setAlpha(150);
		btn_ok.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		et_name.addTextChangedListener(this);
		hostName = settingsSpf.getString("HostAdd", null);
	}

	/**
	 * 用于自动填写注册数据 正常使用请务必删除！
	 */
	private void testMethod() {
		et_name.setText("1100310118");
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

		if (s.length() == Constant.ACCOUNT_LENGTH
				&& !getIntent().getStringExtra("name").equals(
						et_name.getText().toString())) {
			btn_ok.setEnabled(true);
		} else {
			btn_ok.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case id.btn_search_ok:
			startSearch();
			break;
		case id.btn_search_add:
			startAdd();
			break;
		}
	}

	private void startSearch() {
		mSAsync = new SearchAsync(this, rl_result);
		String params = "Name=" + et_name.getText().toString();
		get_url = "http://" + hostName + "/servlet/SearchServlet?" + params;
		mSAsync.execute(get_url);
	}

	private void startAdd() {
		String params = "Name=" + getIntent().getStringExtra("name")
				+ "&Cname=" + et_name.getText().toString();
		get_url = "http://" + hostName + "/servlet/AddServlet?" + params;
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
						Toast.makeText(context, "onFailure--->服务器连接超时，请稍后重试！",
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
							Toast.makeText(context, "添加成功！", Toast.LENGTH_LONG)
									.show();
							break;
						case Constant.TEST_FAIL:
							Toast.makeText(context, "添加失败！",
									Toast.LENGTH_LONG).show();
							break;
						case Constant.TEST_REPEAT:
							Toast.makeText(context, "好友已存在！",
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
}
