package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.CurrentlyPlayingSong;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDListener;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;

public class PlaylistActivity extends Activity implements MPDListener,
		OnSharedPreferenceChangeListener {

	private Song selectedSong = null;
	final List<Song> songsInPlaylist = new ArrayList<Song>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_layout);
		MPDService.getInstance(this).addPlaylistChangeListener(this);
		final ListView playlistView = (ListView) findViewById(R.id.playlistView);
		registerForContextMenu(playlistView);
		playlistView.setAdapter(new PlaylistAdapter(this, songsInPlaylist));
		playlistView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				selectedSong = (Song) (playlistView
						.getItemAtPosition(myItemInt));
			}
		});

		Button playButton = (Button) findViewById(R.id.playButton);
		final MPDService mpd = MPDService.getInstance(this);
		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedSong != null) {
					mpd.playSong(selectedSong);
				}
			}
		});
	}

	@Override
	public void playListChanged(List<Song> playList) {
		songsInPlaylist.clear();
		songsInPlaylist.addAll(playList);
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				final ListView playlistView = (ListView) findViewById(R.id.playlistView);
				((PlaylistAdapter) playlistView.getAdapter())
						.notifyDataSetChanged();

			}
		});
	}

	@Override
	public void currentlyPlayingSongChanged(
			CurrentlyPlayingSong currentlyPlayingSong) {
		this.setTitle(new SpannableString(currentlyPlayingSong.getSong()
				.getTitle()));

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.playlist_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.removeSongFromPlaylist:
			final ListView songLibraryView = (ListView) findViewById(R.id.playlistView);
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Song selectedSong = (Song) (songLibraryView
					.getItemAtPosition(info.position));
			MPDService.getInstance(this).removeSongFromPlaylist(selectedSong);
			return true;
		default:
			return super.onContextItemSelected(item);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.playlist_option_menu, menu);
		return true;
	}

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
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String portString = prefs.getString("mpd_port_preference", "6600");
		if (!NumberUtils.isNumber(portString)) {
			portString = "6600";
		}
		String hostname = prefs.getString("mpd_host_preference", "localhost");
		MPDService.getInstance(this).connect(portString, hostname);
	}

}
