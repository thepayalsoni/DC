package com.fl.ps.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;


public class FetchCategoryRequest extends StringRequest {
	private Listener<String> listener;

	private Priority mPriority = Priority.HIGH;

	public FetchCategoryRequest(Context context, Listener<String> listener, ErrorListener errorListener) {

		super(Method.GET, "http://52.6.188.224:8080/DCProject/service/category", listener,
				errorListener);
		this.listener = listener;
		setRetryPolicy(new DefaultRetryPolicy(1000, 3, 2));
	}

	@Override
	public Priority getPriority() {
		return mPriority;
	}

	public void setPriority(Priority priority) {
		mPriority = priority;
	}

	@Override
	protected void deliverResponse(String arg0) {
		final String result = (String) arg0;
		if(listener != null)
			listener.onResponse(result);
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			
			return (Response<String>) Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.error(new ParseError(e));
		}
	}
}
