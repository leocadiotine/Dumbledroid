package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.R;
import io.leocad.dumbledoreexample.models.Jedi;
import io.leocad.dumbledroid.net.NoConnectionException;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class JediActivity extends BaseActivity {
	
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

}
