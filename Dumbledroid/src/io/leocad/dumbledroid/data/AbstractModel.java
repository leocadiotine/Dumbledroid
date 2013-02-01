package io.leocad.dumbledroid.data;

import io.leocad.dumbledroid.net.HttpMethod;

import java.io.Serializable;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;


public abstract class AbstractModel implements Serializable {
	
	private static final long serialVersionUID = -889584042617358518L;

	private static final String DEFAULT_ENCODING = HTTP.UTF_8;
	
	public String url;
	public String encoding;
	public String error;

	public long cacheDuration;
	
	protected abstract DataType getDataType();
	
	protected AbstractModel(String url) {
		this(url, DEFAULT_ENCODING);
	}
	
	protected AbstractModel(String _url, long _cacheDuration) {
		this(_url, DEFAULT_ENCODING, _cacheDuration);
	}

	protected AbstractModel(String _url, String _encoding) {
		this(_url, _encoding, -1L);
	}
	
	protected AbstractModel(String _url, String _encoding, long _cacheDuration) {
		url = _url;
		encoding = _encoding;
		cacheDuration = _cacheDuration;
	}
	
	public void load(Context ctx) throws Exception {
		load(ctx, null);
	}
	
	protected void load(Context ctx, List<NameValuePair> params) throws Exception {
		load(ctx, params, null);
	}
	
	protected void load(Context ctx, List<NameValuePair> params, HttpMethod method) throws Exception {
		checkConnection(ctx);
		DataController.load(ctx, this, getDataType(), params, method);
	}

	private void checkConnection(Context ctx) throws NoConnectionException {
		ConnectivityManager cm = (ConnectivityManager)
		ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo netInfo = cm.getActiveNetworkInfo();
	        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
	            throw new NoConnectionException();
	        }
	}
}
