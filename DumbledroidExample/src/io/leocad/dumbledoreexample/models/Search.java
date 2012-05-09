package io.leocad.dumbledoreexample.models;

import io.leocad.dumbledroid.data.AbstractModel;
import io.leocad.dumbledroid.data.DataType;

import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class Search extends AbstractModel {

	public double completed_in;
	public int max_id;
	public String query;
	public List<Result> results;
	
	public Search() {
		super("http://search.twitter.com/search.json");
	}
	
	public void load(Context ctx, String query, int resultsPerPage, String lang) throws Exception {
		
		List<NameValuePair> params = new Vector<NameValuePair>();
		
		params.add( new BasicNameValuePair("q", query) );
		params.add( new BasicNameValuePair("rpp", String.valueOf(resultsPerPage)) );
		params.add( new BasicNameValuePair("lang", lang) );
		
		super.load(ctx, params);
	}

	private static final long serialVersionUID = 1L;
	
	@Override
	protected DataType getDataType() {
		return DataType.JSON;
	}

}
