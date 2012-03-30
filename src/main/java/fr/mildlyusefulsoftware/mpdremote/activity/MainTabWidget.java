package fr.mildlyusefulsoftware.mpdremote.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import fr.mildlyusefulsoftware.mpdremote.R;

public class MainTabWidget extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs_layout);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		intent = new Intent().setClass(this, PlaylistActivity.class);
		spec = tabHost
				.newTabSpec("playlist")
				.setIndicator("PlayList",
						res.getDrawable(android.R.drawable.ic_btn_speak_now))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, SongLibraryActivity.class);
		spec = tabHost
				.newTabSpec("library")
				.setIndicator("Library",
						res.getDrawable(android.R.drawable.ic_menu_search))
				.setContent(intent);
		tabHost.addTab(spec);


		tabHost.setCurrentTab(0);
	}
}
