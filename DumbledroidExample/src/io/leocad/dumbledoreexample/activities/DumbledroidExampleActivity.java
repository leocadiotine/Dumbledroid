package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.models.LookupUsers;
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
		
		//Test "LookupUsers" object
		new AsyncTask<Void, Void, LookupUsers>() {

			@Override
			protected LookupUsers doInBackground(Void... params) {

				try {
					LookupUsers lu = new LookupUsers();
					lu.load(DumbledroidExampleActivity.this);
					return lu;

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(LookupUsers lu) {

				Log.v("Dumbledroid", lu.toString());
			}
		}.execute();

		//Test "Statuses" object
//		new AsyncTask<Void, Void, LookupUsers>() {
//
//			@Override
//			protected LookupUsers doInBackground(Void... params) {
//
//				try {
//					LookupUsers statuses = new LookupUsers();
//					statuses.load(DumbledroidExampleActivity.this);
//					return statuses;
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					return null;
//				}
//			}
//
//			@Override
//			protected void onPostExecute(LookupUsers statuses) {
//
//				Log.v("Dumbledroid", statuses.toString());
//			}
//		}.execute();
	}
}