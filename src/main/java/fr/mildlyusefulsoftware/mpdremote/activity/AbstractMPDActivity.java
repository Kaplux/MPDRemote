package fr.mildlyusefulsoftware.mpdremote.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;

public abstract class AbstractMPDActivity extends Activity implements
		OnSharedPreferenceChangeListener {

	protected MPDService mpd=MPDService.getInstance(this);;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferencesOptionMenu:
			Intent intent = new Intent(getApplicationContext(),
					MPDRemotePreferencesActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		setEnabled(false);
		mpd.launchConnectThread();
	}

	protected void setEnabled(boolean enabled) {
		setEnabled(enabled, findViewById(android.R.id.content).getRootView());
	}

	private void setEnabled(final boolean enabled, final View component) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				component.setEnabled(enabled);
			}
		});
		if (component instanceof ViewGroup) {
			final ViewGroup vg = (ViewGroup) component;
			for (int i = 0; i < vg.getChildCount(); i++) {
				final View v = vg.getChildAt(i);
				setEnabled(enabled, v);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		PreferenceManager.getDefaultSharedPreferences(this)
		.registerOnSharedPreferenceChangeListener(this);
		super.onCreate(savedInstanceState);
	}
	

}
