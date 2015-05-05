 package com.andecy.gtalk.aty;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andecy.gtalk.R;
import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.adp.FriendAdp;
import com.andecy.gtalk.bean.Constant;
import com.andecy.gtalk.bean.User;
import com.andecy.gtalk.service.UpdaterService2;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class HomeAty extends Activity implements OnItemClickListener,
		OnItemLongClickListener {
	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;
	// 侧边栏btn
	private TextView tv_name;
	private TextView tv_sign;
	private TextView tv_others;
	private TextView tv_lineType;
	private TextView tv_alert;
	private TextView tv_logout;
	private TextView tv_search;
	private TextView tv_exit;
	// 获取列表
	private ListView lv_friend;
	private List<User> list_friend;
	private Context context = HomeAty.this;
	private ProgressDialog mProgressDialog;
	private String hostName = null;
	private String get_url;
	private String result = null;
	private FriendAdp mFriendAdp;

	// 监听器
	private SlidingListener mListener_Sliding;

	// 侧边栏及动画
	@SuppressWarnings("unused")
	private CanvasTransformer mTransformer;
	private SlidingMenu mSlidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_home);
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		stateChanged();
		alertChanged();
		signChanged();
	}

	private void initView() {
		// 初始化用户配置信息
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		ViewUtils.inject(this);
		hostName = settingsSpf.getString("HostAdd", null);
		// 初始化各个组件
		initAnimation();
		initSlidingView();
		initSlidingMenu();

		initListener();
		// 赋初值
		Bundle bundle = getIntent().getExtras();
		tv_name.setText(bundle.getString("name"));
		initList();
		initServ();
	}

	private static Interpolator interp = new Interpolator() {
		@Override
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}
	};

	private void initAnimation() {
		mTransformer = new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				canvas.translate(
						0,
						canvas.getHeight()
								* (1 - interp.getInterpolation(percentOpen)));
			}

		};
	}

	private void initServ() {
		Intent intent4serv = new Intent(this,UpdaterService2.class);
		startService(intent4serv);
	}

	private void initList() {
		list_friend = new ArrayList<User>();
		lv_friend = (ListView) findViewById(id.lv_home_friend);
		String params = "Name=" + getIntent().getExtras().getString("name");
		get_url = "http://" + hostName + "/servlet/ListFriendServlet?" + params;
		Log.i("TAG5", "get_url--->" + get_url);
		connNet4List(get_url);
		Log.i("TAG5", "getResult--->" + getResult());
	}

	private void initSlidingView() {
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mSlidingMenu.setMenu(R.layout.menu_slide);
		mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width);
	}

	private void initSlidingMenu() {
		tv_name = (TextView) findViewById(id.tv_slide_name);
		tv_sign = (TextView) findViewById(id.tv_slide_sign);
		tv_others = (TextView) findViewById(id.tv_slide_others);
		tv_lineType = (TextView) findViewById(id.tv_slide_lineType);
		tv_alert = (TextView) findViewById(id.tv_slide_alert);
		tv_logout = (TextView) findViewById(id.tv_slide_logout);
		tv_search = (TextView) findViewById(id.tv_slide_search);
		tv_exit = (TextView) findViewById(id.tv_slide_exit);
	}

	private void initListener() {
		mListener_Sliding = new SlidingListener();
		tv_sign.setOnClickListener(mListener_Sliding);
		tv_others.setOnClickListener(mListener_Sliding);
		tv_lineType.setOnClickListener(mListener_Sliding);
		tv_alert.setOnClickListener(mListener_Sliding);
		tv_logout.setOnClickListener(mListener_Sliding);
		tv_search.setOnClickListener(mListener_Sliding);
		tv_exit.setOnClickListener(mListener_Sliding);

	}

	public class SlidingListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			switch (v.getId()) {
			case id.tv_slide_alert:
				intent.setClass(HomeAty.this, AlertAty.class);
				HomeAty.this.startActivity(intent);
				break;
			case id.tv_slide_lineType:
				intent.setClass(HomeAty.this, LineTypeAty.class);
				HomeAty.this.startActivity(intent);
				break;
			case id.tv_slide_exit:
				exitChat();
				break;
			case id.tv_slide_others:
				intent.setClass(HomeAty.this, OthersAty.class);
				HomeAty.this.startActivity(intent);
				break;
			case id.tv_slide_search:
				intent.putExtra("name", tv_name.getText().toString());
				intent.setClass(HomeAty.this, SearchAty.class);
				HomeAty.this.startActivity(intent);
				break;
			case id.tv_slide_logout:
				offChat();
				break;
			case id.tv_slide_sign:
				setSign();
				break;
			}
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<User> getList_friend() {
		return list_friend;
	}

	public void setList_friend(List<User> list_friend) {
		this.list_friend = list_friend;
	}

	private void signChanged() {
		tv_sign.setText(settingsSpf.getString("sign", null));
	}

	private void stateChanged() {
		Drawable drawable = null;
		switch (settingsSpf.getInt("rg_line", id.rb_lineType_1)) {
		case id.rb_lineType_1:
			tv_lineType.setText("状态:在线");
			drawable = getResources().getDrawable(R.drawable.linetype_online);

			break;
		case id.rb_lineType_2:
			tv_lineType.setText("状态:离线");
			drawable = getResources().getDrawable(R.drawable.linetype_offline);
			break;
		}
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		tv_lineType.setCompoundDrawables(drawable, null, null, null);
	}

	private void alertChanged() {
		Drawable drawable = null;
		if (settingsSpf.getBoolean("alert_all", true)) {
			drawable = getResources().getDrawable(R.drawable.alert_all);
		}
		if (settingsSpf.getBoolean("alert_none", false)) {
			drawable = getResources().getDrawable(R.drawable.alert_none);
		}
		if (settingsSpf.getBoolean("alert_shake", false)) {
			drawable = getResources().getDrawable(R.drawable.alert_shake);
		}
		if (settingsSpf.getBoolean("alert_sound", false)) {
			drawable = getResources().getDrawable(R.drawable.alert_sound);
		}

		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		tv_alert.setCompoundDrawables(drawable, null, null, null);
	}

	private void setSign() {
		Intent intent = new Intent();
		intent.setClass(HomeAty.this, SignAty.class);
		intent.putExtra("name", tv_name.getText().toString());
		HomeAty.this.startActivity(intent);
	}

	private void exitChat() {
		AlertDialog.Builder builder = new Builder(HomeAty.this);
		builder.setMessage("确认退出吗？");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	private void offChat() {
		AlertDialog.Builder builder = new Builder(HomeAty.this);
		builder.setMessage("确认注销吗？");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(context, LoginAty.class);
				startActivity(intent);
				finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	private void connNet4List(String get_url) {
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
							addList(result.split(":"));
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

	private void addList(String[] st_array) {
		User mUser = null;
		for (int i = 1; i < st_array.length; i++) {
			Log.i("TAG5", "ARRAY--->" + st_array[i]);
			mUser = new User();
			mUser.setName(st_array[i]);
			list_friend.add(mUser);
			mFriendAdp = new FriendAdp(context, list_friend);
			lv_friend.setAdapter(mFriendAdp);
			lv_friend.setOnItemClickListener(this);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent2List = new Intent();
		intent2List.setClass(HomeAty.this, ChatAty.class);
		intent2List.putExtra("cname", list_friend.get(arg2).getName());
		intent2List.putExtra("name", tv_name.getText().toString());
		startActivity(intent2List);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
}
