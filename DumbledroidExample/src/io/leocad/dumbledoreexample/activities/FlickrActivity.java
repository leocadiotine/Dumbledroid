package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.R;
import io.leocad.dumbledoreexample.adapters.FlickrAdapter;
import io.leocad.dumbledoreexample.models.FlickrPhotos;
import io.leocad.dumbledroid.net.NoConnectionException;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FlickrActivity extends BaseActivity {

	private ViewFlipper mViewFlipper;
	private TextView mResultsTitle;
	private ListView mListView;
	private Dialog mDialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flickr);

		if (Build.VERSION.SDK_INT >= 11) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		mViewFlipper = (ViewFlipper) findViewById(R.id.vf);
		mResultsTitle = (TextView) findViewById(R.id.tv_results_title);
		mListView = (ListView) findViewById(R.id.lv);
	}

	public void onSearchClicked(View v) {

		//Hide the soft keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

		String query = ((EditText) findViewById(R.id.et_query)).getText().toString();
		loadContent(query);
	}

	private void loadContent(String query) {
		new AsyncTask<String, Void, FlickrPhotos>() {

			@Override
			protected void onPreExecute() {
				mDialog = ProgressDialog.show(FlickrActivity.this, null, "Loading…");
			};

			@Override
			protected FlickrPhotos doInBackground(String... params) {

				try {
					FlickrPhotos photos = new FlickrPhotos();
					photos.load(FlickrActivity.this, params[0]);
					return photos;

				} catch (NoConnectionException e) {
					onConnectionError();
					return null;

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(FlickrPhotos photos) {

				if (photos != null) {
					printContent(photos);
				}
				mDialog.dismiss();
			}
		}.execute(query);
	}

	private void printContent(FlickrPhotos photos) {

		mViewFlipper.setDisplayedChild(1);
		mResultsTitle.setText(photos.title);

		FlickrAdapter adapter = new FlickrAdapter(this, photos);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {

		if (mViewFlipper.getDisplayedChild() == 1) {
			mViewFlipper.setDisplayedChild(0);
		} else {
			super.onBackPressed();
		}
	}
}
