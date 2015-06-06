package com.fl.ps.requests;

import android.content.Context;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

public class MyVolley {

	private static RequestQueue mRequestQueue;

	private MyVolley() {
		// no instances
	}

	static void init(Context context) {

		mRequestQueue = createVolleyRequestQue(context);

	}

	public static RequestQueue createVolleyRequestQue(Context context) {

		if (Build.VERSION.SDK_INT < 9)
			return Volley.newRequestQueue(context, new HttpClientStack(new DCHttpClient().getHttpClient()));
		else
			return Volley.newRequestQueue(context);

	}

	public static RequestQueue getRequestQueue(Context mContext) {

		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext);
		}
		return mRequestQueue;
	}

}