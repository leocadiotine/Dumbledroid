package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.R;
import io.leocad.dumbledoreexample.models.Jedi;
import io.leocad.dumbledroid.net.NoConnectionException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

public class JediActivity extends Activity {
	
	private Dialog mDialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jedi);
		
		if (Build.VERSION.SDK_INT >= 11) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		loadContent();
	}
	
	private void loadContent() {
		new AsyncTask<Void, Void, Jedi>() {

			@Override
			protected void onPreExecute() {
				mDialog = ProgressDialog.show(JediActivity.this, null, "Loading…");
			};
			
			@Override
			protected Jedi doInBackground(Void... params) {

				try {
					Jedi jedi = new Jedi();
					jedi.load(JediActivity.this);
					return jedi;

				} catch (NoConnectionException e) {
					onConnectionError();
					return null;
					
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Jedi jedi) {

				if (jedi != null) {
					printContent(jedi);
				}
				mDialog.dismiss();
			}
		}.execute();
	}
	
	private void printContent(Jedi jedi) {
		((TextView) findViewById(R.id.tv_name)).setText( jedi.name );
		((TextView) findViewById(R.id.tv_surname)).setText( jedi.surname );
		((TextView) findViewById(R.id.tv_ability)).setText( jedi.ability );
		((TextView) findViewById(R.id.tv_master)).setText( jedi.master );
		((TextView) findViewById(R.id.tv_father)).setText( jedi.father );
	}
	
	private void onConnectionError() {
		
		runOnUiThread( new Runnable() {
			
			@Override
			public void run() {
				
				new AlertDialog.Builder(JediActivity.this)
				.setTitle("Error")
				.setMessage("Data connection unavailable!")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.create()
				.show();
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
