package com.andecy.gtalk.aty;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.adp.MsgAdp;
import com.andecy.gtalk.bean.ChatMsg;
import com.andecy.gtalk.bean.Constant;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ChatAty extends Activity implements OnClickListener, TextWatcher {

	// 用户配置SharePreference
	private static final String TAG = "ChatAty";
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	private String get_url;
	private String init_url;
	private String hostName = null;
	// 常驻
	private Context context = ChatAty.this;
	private ProgressDialog mProgressDialog;
	private String result = null;
	private String newMsg;
	// 控件
	private TextView tv_label;
	private Button btn_send;
	private ListView lv_msg;
	private EditText et_msg;
	// 适配器
	private MsgAdp mAdp;
	private List<ChatMsg> list;
	private Boolean isNew = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_chat);
		initView();
	}

	private void initView() {
		// 初始化用户配置信息
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		// 常项
		list = new ArrayList<ChatMsg>();
		// 空间find
		hostName = settingsSpf.getString("HostAdd", null);
		tv_label = (TextView) findViewById(id.tv_chat_label);
		btn_send = (Button) findViewById(id.btn_chat_ok);
		lv_msg = (ListView) findViewById(id.lv_chat);
		et_msg = (EditText) findViewById(id.et_chat);

		btn_send.setOnClickListener(this);
		et_msg.addTextChangedListener(this);
		tv_label.setText(getIntent().getStringExtra("cname"));
		mAdp = new MsgAdp(context, list);
		lv_msg.setAdapter(mAdp);
		initGet();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case id.btn_chat_ok:
			startSend();
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (s.length() != 0) {
			btn_send.setEnabled(true);
		} else {
			btn_send.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	private void startSend() {
		String params = "Name=" + getIntent().getStringExtra("name")
				+ "&Cname=" + tv_label.getText().toString() + "&Content="
				+ et_msg.getText().toString();
		get_url = "http://" + hostName + "/servlet/SendChatMsg?" + params;
		Log.d(TAG, "get_url--->" + get_url);
		connNet(get_url);
	}

	private void initGet() {
		String param = "MyName=" + getIntent().getStringExtra("name")
				+ "&TaName=" + tv_label.getText().toString();
		init_url = "http://" + hostName + "/servlet/GetChatMsg?" + param;
		Log.d(TAG, "init_url--->" + init_url);
		connChat(init_url);
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
									"正在发送...");
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
							Toast.makeText(context, "发送成功！", Toast.LENGTH_LONG)
									.show();
							updateList();
							break;
						case Constant.TEST_FAIL:
							Toast.makeText(context, "发送失败！", Toast.LENGTH_LONG)
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

	private void connChat(String get_url) {
		System.out.println(get_url);
		new HttpUtils().send(HttpMethod.GET, get_url,
				new RequestCallBack<String>() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						if (null == mProgressDialog) {
							mProgressDialog = ProgressDialog.show(context, "",
									"正在接收...");
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
						
						setNewMsg(arg0.result);
						Log.d(TAG, "newMsg--->" + newMsg);
						int i = -1;
						if (null != newMsg) {
							i = Integer.parseInt(newMsg.split(":")[0]);
						}
						switch (i) {
						case Constant.TEST_OK:
							Toast.makeText(context, "获取成功！", Toast.LENGTH_LONG)
									.show();
							
							setMsg(newMsg);
							break;
						case Constant.TEST_FAIL:
							Toast.makeText(context, "获取失败！", Toast.LENGTH_LONG)
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

	public void setMsg(String msg) {
		Log.d(TAG, "setMsg--->" + msg);
		mAdp.addMsg(null, msg);
		mAdp.notifyDataSetInvalidated();
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getNewMsg() {
		return newMsg;
	}

	public void setNewMsg(String newMsg) {
		this.newMsg = newMsg;
	}

	private void updateList() {
		mAdp.addMsg(et_msg.getText().toString(), null);
		//mAdp.addMsg(null, et_msg.getText().toString());
		mAdp.notifyDataSetInvalidated();
		et_msg.setText(null);
	}

}
