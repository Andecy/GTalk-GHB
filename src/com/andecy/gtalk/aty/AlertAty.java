package com.andecy.gtalk.aty;

import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AlertAty extends Activity implements OnCheckedChangeListener {
	// �û�����SharePreference
	public static final String PREFS_SETTING_STRING = "SettingsInfo";
	private SharedPreferences settingsSpf;
	public Editor mEditor;

	private CheckBox cb_sound;
	private CheckBox cb_shake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_alert);
		initView();
	}

	private void initView() {
		// ��ʼ���û�������Ϣ
		settingsSpf = getSharedPreferences(PREFS_SETTING_STRING, MODE_PRIVATE);
		mEditor = settingsSpf.edit();
		// ��ʼ���������
		cb_sound = (CheckBox) findViewById(id.cb_alert_sound);
		cb_shake = (CheckBox) findViewById(id.cb_alert_shake);
		// ����ֵ
		cb_sound.setChecked(settingsSpf.getBoolean("cb_sound", false));
		cb_shake.setChecked(settingsSpf.getBoolean("cb_shake", false));
		cb_shake.setOnCheckedChangeListener(this);
		cb_sound.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (cb_sound.isChecked()&&cb_shake.isChecked()) {
			mEditor.putBoolean("alert_all", true);
		}
		if (cb_sound.isChecked()&&!cb_shake.isChecked()) {
			mEditor.putBoolean("alert_sound", true);
		}
		if (!cb_sound.isChecked()&&cb_shake.isChecked()) {
			mEditor.putBoolean("alert_shake", true);
		}
		if (!cb_sound.isChecked()&&!cb_shake.isChecked()) {
			mEditor.putBoolean("alert_none", true);
		}
		
		switch (buttonView.getId()) {
		case id.cb_alert_sound:
			mEditor.putBoolean("cb_sound", isChecked);
			break;
		case id.cb_alert_shake:
			mEditor.putBoolean("cb_shake", isChecked);
			break;
		}
		mEditor.commit();
	}

}
