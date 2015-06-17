package com.fl.ps.requests;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.os.Build;

public class DCHttpClient {

	public HttpClient getHttpClient() {
		if (Build.VERSION.RELEASE.trim().compareTo("2.2") == 0)
			return new MyHttpsClient();
		else
			return new DefaultHttpClient();
	}

	private class MyHttpsClient extends DefaultHttpClient {

		@Override
		protected ClientConnectionManager createClientConnectionManager() {
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

			return new SingleClientConnManager(getParams(), registry);
		}

	}

}