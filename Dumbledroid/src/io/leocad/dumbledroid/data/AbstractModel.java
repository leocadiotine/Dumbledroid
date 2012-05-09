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
	
	protected abstract DataType getDataType();
	
	protected AbstractModel(String url) {
		this(url, DEFAULT_ENCODING);
	}

	protected AbstractModel(String _url, String _encoding) {
		url = _url;
		encoding = _encoding;
	}
	
	public void load(Context ctx) throws Exception {
		load(ctx, null);
	}
	
	protected void load(Context ctx, List<NameValuePair> params) throws Exception {
		load(ctx, params, null);
	}
	
	protected void load(Context ctx, List<NameValuePair> params, HttpMethod method) throws Exception {
		DataController.load(ctx, this, getDataType(), params, method);
	}
}
