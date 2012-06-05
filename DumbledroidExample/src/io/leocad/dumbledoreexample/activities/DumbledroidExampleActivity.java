package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.models.Search;
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
		new AsyncTask<Void, Void, Search>() {
			
			@Override
			protected Search doInBackground(Void... params) {
				
				try {
					Search search = new Search();
					search.load(DumbledroidExampleActivity.this, "android", 10, "en");
					return search;
					
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(Search search) {
				
				Log.v("Dumbledroid", search.toString());
			}
		}.execute();
		
		//Test "Sith" object
//				new AsyncTask<Void, Void, Sith>() {
//
//					@Override
//					protected Sith doInBackground(Void... params) {
//
//						try {
//							Sith sith = new Sith();
//							sith.load(DumbledroidExampleActivity.this);
//							return sith;
//
//						} catch (Exception e) {
//							e.printStackTrace();
//							return null;
//						}
//					}
//
//					@Override
//					protected void onPostExecute(Sith sith) {
//
//						Log.v("Dumbledroid", sith.toString());
//					}
//				}.execute();
	}
}