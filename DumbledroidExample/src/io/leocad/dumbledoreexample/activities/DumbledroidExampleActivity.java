package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.models.Jedi;
import io.leocad.dumbledoreexample.models.Search;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class DumbledroidExampleActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Test "Jedi" object
		new AsyncTask<Void, Void, Jedi>() {

			@Override
			protected Jedi doInBackground(Void... params) {

				try {
					Jedi jedi = new Jedi();
					jedi.load(DumbledroidExampleActivity.this);
					return jedi;

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Jedi jedi) {

				Log.v("Dumbledroid", jedi.toString());
			}
		}.execute();
		
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
	}
}