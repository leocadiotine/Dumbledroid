package io.leocad.dumbledoreexample.models;

import io.leocad.dumbledroid.data.AbstractModel;
import io.leocad.dumbledroid.data.DataType;

import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class FlickrPhotos extends AbstractModel {

	private static final long serialVersionUID = 1L;
	
	public String title;
	public List<PhotoItem> items;

	public FlickrPhotos() {
		super("https://secure.flickr.com/services/feeds/photos_public.gne");
	}
	
	public void load(Context ctx, String query) throws Exception {
		
		List<NameValuePair> params = new Vector<NameValuePair>();
		params.add( new BasicNameValuePair("format", "json") );
		params.add( new BasicNameValuePair("nojsoncallback", "1") );
		params.add( new BasicNameValuePair("tags", query) );
		
		super.load(ctx, params);
	}
	
	@Override
	protected DataType getDataType() {
		return DataType.JSON;
	}
}
