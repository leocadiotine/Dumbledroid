package io.leocad.dumbledoreexample.adapters;

import io.leocad.dumbledoreexample.R;
import io.leocad.dumbledoreexample.models.FlickrPhotos;
import io.leocad.dumbledoreexample.models.PhotoItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FlickrAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private FlickrPhotos mPhotos;

	public FlickrAdapter(Context ctx, FlickrPhotos photos) {
		mInflater = LayoutInflater.from(ctx);
		mPhotos = photos;
	}
	
	@Override
	public int getCount() {

		return mPhotos.items.size();
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.flickr_result_row, parent, false);
			holder = new ViewHolder();
			
			holder.image = (ImageView) convertView.findViewById(R.id.iv);
			holder.title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.author = (TextView) convertView.findViewById(R.id.tv_author);
			
			convertView.setTag(holder);
		
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PhotoItem item = mPhotos.items.get(position);
		holder.title.setText(item.title);
		holder.author.setText(item.author);
		
		return convertView;
	}
	
	static class ViewHolder {
		ImageView image;
		TextView title;
		TextView author;
	}

}
