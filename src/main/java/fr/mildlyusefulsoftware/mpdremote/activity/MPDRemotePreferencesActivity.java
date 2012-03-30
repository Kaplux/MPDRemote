package fr.mildlyusefulsoftware.mpdremote.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import fr.mildlyusefulsoftware.mpdremote.R;

public class MPDRemotePreferencesActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
