package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.R;
import io.leocad.dumbledoreexample.models.Sith;
import io.leocad.dumbledoreexample.models.Suit;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

public class SithActivity extends Activity {
	
	private Dialog mDialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sith);
		
		if (Build.VERSION.SDK_INT >= 11) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		loadContent();
	}
	
	private void loadContent() {
		new AsyncTask<Void, Void, Sith>() {

			@Override
			protected void onPreExecute() {
				mDialog = ProgressDialog.show(SithActivity.this, null, "Loading…");
			};
			
			@Override
			protected Sith doInBackground(Void... params) {

				try {
					Sith sith = new Sith();
					sith.load(SithActivity.this);
					return sith;

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Sith sith) {

				printContent(sith);
				mDialog.dismiss();
			}
		}.execute();
	}
	
	private void printContent(Sith sith) {
		((TextView) findViewById(R.id.tv_side)).setText( sith.getSide() );
		((TextView) findViewById(R.id.tv_names)).setText( sith.getNames() );
		Suit suit = sith.getSuit();
		((TextView) findViewById(R.id.tv_suit_color)).setText( suit.getColor() );
		((TextView) findViewById(R.id.tv_suit_cloak)).setText( String.valueOf(suit.hasCloak()) );
		((TextView) findViewById(R.id.tv_kills)).setText( String.valueOf(sith.getKills()) );
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
