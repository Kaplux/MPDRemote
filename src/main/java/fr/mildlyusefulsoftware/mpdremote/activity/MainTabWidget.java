package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.List;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.widget.TabHost;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.CurrentlyPlayingSong;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDListener;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;

public class MainTabWidget extends TabActivity implements MPDListener {

	private MPDService mpd;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mpd=MPDService.getInstance(this);
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

		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 35;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 35;
		tabHost.setCurrentTab(0);
		mpd.addPlaylistChangeListener(this);

	}

	@Override
	public void currentlyPlayingSongChanged(
			final CurrentlyPlayingSong currentlyPlayingSong) {
		final Activity currentActivity = this;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				currentActivity.setTitle(new SpannableString(
						currentlyPlayingSong.getSong().getTitle()));

			}
		});

	}

	@Override
	public void playListChanged(List<Song> playList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionChanged(boolean connected) {
		// TODO Auto-generated method stub
		
	}
}
