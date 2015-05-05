package com.andecy.gtalk.aty;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class LineTypeAty extends Activity implements OnCheckedChangeListener {
	// 用户配置SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;

	private RadioGroup rg_line;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_linetype);
		initView();
	}

	private void initView() {
		// 初始化用户配置信息
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		// 初始化各个组件
		rg_line = (RadioGroup) findViewById(id.rg_linetype);

		// 赋初值
		rg_line.check(settingsSpf.getInt("rg_line", id.rb_lineType_1));
		rg_line.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case id.rb_lineType_1:
			break;
		case id.rb_lineType_2:
			break;
		}
		mEditor.putInt("rg_line", checkedId);
		mEditor.commit();
	}
}
