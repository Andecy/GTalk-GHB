package com.andecy.gtalk.aty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andecy.gtalk.R.anim;
import com.andecy.gtalk.R.id;
import com.andecy.gtalk.R.layout;
import com.andecy.gtalk.service.MsgService;
import com.andecy.gtalk.service.UpdaterService2;

public class WelcomeAty extends Activity implements OnClickListener {

	private Button btn_start;
	private TextView tv_appname;
	private TextView tv_copyright;
	private ImageView iv_ver;
	private ImageView iv_verno;
	private ImageView iv_logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.aty_welcome);
		initView();
	}

	private void initView() {
		btn_start = (Button) findViewById(id.btn_welcome_ok);
		tv_appname = (TextView) findViewById(id.tv_welcome_appname);
		tv_copyright = (TextView) findViewById(id.tv_welcome_copyright);
		iv_ver = (ImageView) findViewById(id.iv_welcome_ver);
		iv_logo = (ImageView) findViewById(id.iv_welcome_logo);
		iv_verno = (ImageView) findViewById(id.iv_welcome_verno);
		btn_start.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case id.btn_welcome_ok:

			intoLogin();
			break;
		}
	}

	private void intoLogin() {

		btn_start.startAnimation(AnimationUtils.loadAnimation(this,
				anim.anim_dismiss));
		tv_appname.startAnimation(AnimationUtils.loadAnimation(this,
				anim.anim_dismiss));
		tv_copyright.startAnimation(AnimationUtils.loadAnimation(this,
				anim.anim_dismiss));
		iv_ver.startAnimation(AnimationUtils.loadAnimation(this,
				anim.anim_dismiss));
		iv_verno.startAnimation(AnimationUtils.loadAnimation(this,
				anim.anim_dismiss));
		iv_logo.startAnimation(AnimationUtils.loadAnimation(this,
				anim.anim_fall));
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(900);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(WelcomeAty.this, MsgService.class);
				intent.putExtra("name", "1100310118");
				startService(intent);
				startActivity(new Intent(WelcomeAty.this, LoginAty.class));
				finish();
			}
		}).start();
	}
}
