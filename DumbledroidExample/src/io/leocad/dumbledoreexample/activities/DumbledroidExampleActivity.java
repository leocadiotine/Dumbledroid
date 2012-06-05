package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.models.Statuses;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class DumbledroidExampleActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//		//Test "Jedi" object
		//		new AsyncTask<Void, Void, Jedi>() {
		//
		//			@Override
		//			protected Jedi doInBackground(Void... params) {
		//
		//				try {
		//					Jedi jedi = new Jedi();
		//					jedi.load(DumbledroidExampleActivity.this);
		//					return jedi;
		//
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//					return null;
		//				}
		//			}
		//
		//			@Override
		//			protected void onPostExecute(Jedi jedi) {
		//
		//				Log.v("Dumbledroid", jedi.toString());
		//			}
		//		}.execute();
		//		
		//Test "Search" object
//		new AsyncTask<Void, Void, Search>() {
//
//			@Override
//			protected Search doInBackground(Void... params) {
//
//				try {
//					Search search = new Search();
//					search.load(DumbledroidExampleActivity.this, "android", 10, "en");
//					return search;
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					return null;
//				}
//			}
//
//			@Override
//			protected void onPostExecute(Search search) {
//
//				Log.v("Dumbledroid", search.toString());
//			}
//		}.execute();

		//Test "Statuses" object
		new AsyncTask<Void, Void, Statuses>() {

			@Override
			protected Statuses doInBackground(Void... params) {

				try {
					Statuses statuses = new Statuses();
					statuses.load(DumbledroidExampleActivity.this);
					return statuses;

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Statuses statuses) {

				Log.v("Dumbledroid", statuses.toString());
			}
		}.execute();
	}
}