package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.List;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.CurrentlyPlayingSong;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDListener;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;

public class MainTabWidget extends TabActivity implements MPDListener {

	private MPDService mpd;

	private AdView adView;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mpd=MPDService.getInstance(this);
		setContentView(R.layout.tabs_layout);
		initAdBannerView();
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		intent = new Intent().setClass(this, PlaylistActivity.class);
		spec = tabHost
				.newTabSpec("playlist")
				.setIndicator("",
						res.getDrawable(android.R.drawable.ic_btn_speak_now))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, SongLibraryActivity.class);
		spec = tabHost
				.newTabSpec("library")
				.setIndicator("",
						res.getDrawable(android.R.drawable.ic_menu_search))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 45;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 45;
		connectionChanged(false);
		tabHost.setCurrentTab(0);
		mpd.addMPDListener(this);
		if (mpd.isConnected()){
			connectionChanged(true);
		}

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
	public void connectionChanged(final boolean connected) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				TabHost tabHost = getTabHost(); // The activity TabHost
				if(connected){
					tabHost.getTabWidget().getChildAt(0).setEnabled(true);
					tabHost.getTabWidget().getChildAt(1).setEnabled(true);
				}else{
					tabHost.getTabWidget().getChildAt(0).setEnabled(false);
					tabHost.getTabWidget().getChildAt(1).setEnabled(false);
				}
				}
			});
	}
	protected void initAdBannerView() {
		final ViewGroup layout = (ViewGroup) findViewById(R.id.tabHostRootView);
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, "a14f9f866683db7");
	
		// Add the adView to it
		layout.addView(adView);
		AdRequest ar = new AdRequest();
	//	ar.addTestDevice(AdRequest.TEST_EMULATOR);
		// Initiate a generic request to load it with an ad
		adView.loadAd(ar);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (adView != null) {
			adView.destroy();
		}
	}
}
