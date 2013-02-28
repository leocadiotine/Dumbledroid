package io.leocad.dumbledoreexample.activities;

import io.leocad.dumbledoreexample.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void onJediClicked(View v) {
		startActivity( new Intent(this, JediActivity.class) );
	}
	
	public void onSithClicked(View v) {
		startActivity( new Intent(this, SithActivity.class) );
	}
	
	public void onTwitterClicked(View v) {
		startActivity( new Intent(this, FlickrActivity.class) );
	}
}