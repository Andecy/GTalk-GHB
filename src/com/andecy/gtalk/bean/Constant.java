package com.andecy.gtalk.bean;

import java.util.Random;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Color;

public final class Constant {

	public static final int ACCOUNT_LENGTH = 10;
	public static final int CODE_LENGTH = 4;
	public static final int TEST_FAIL = 29;
	public static final int TEST_OK = 20;
	public static final int TEST_ERROR_NAMES = 21;
	public static final int TEST_ERROR_EMAILS = 22;
	public static final int TEST_ERROR_FALSE = 23;
	public static final int TEST_ERROR_TRUE = 24;
	public static final int TEST_ERROR_TIMEOUT = 25;
	public static final int TEST_NULL = 26;
	public static final int TEST_REPEAT = 27;

	public static HttpParams httpConfig() {
		HttpParams mhParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(mhParams, 3 * 1000);
		HttpConnectionParams.setSoTimeout(mhParams, 5 * 1000);
		HttpConnectionParams.setSocketBufferSize(mhParams, 8192);
		return mhParams;
	}

	public static int getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return Color.rgb(r, g, b);
	}
}
