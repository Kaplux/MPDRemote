package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.CurrentlyPlayingSong;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDListener;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;
import fr.mildlyusefulsoftware.mpdremote.util.MPDRemoteUtils;

public class SongLibraryActivity extends AbstractMPDActivity implements
		MPDListener {

	private MPDService mpd;
	private final List<Song> songLibrary = new ArrayList<Song>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mpd = MPDService.getInstance(this);
		mpd.addMPDListener(this);
		setContentView(R.layout.song_library_layout);
		final ListView songLibraryView = (ListView) findViewById(R.id.songLibraryView);
		registerForContextMenu(songLibraryView);
		songLibraryView.setAdapter(new PlaylistAdapter(this, songLibrary));
		if (mpd.isConnected()){
			connectionChanged(true);
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.song_library_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.addSongToPlaylist:
			final ListView songLibraryView = (ListView) findViewById(R.id.songLibraryView);
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Song selectedSong = (Song) (songLibraryView
					.getItemAtPosition(info.position));
			mpd.addSongToPlayList(selectedSong);
			return true;
		default:
			return super.onContextItemSelected(item);

		}
	}

	@Override
	public void playListChanged(List<Song> playList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void currentlyPlayingSongChanged(
			CurrentlyPlayingSong currentlyPlayingSong) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void connectionChanged(final boolean connected) {
		Log.d(MPDRemoteUtils.TAG, "songlib -> cnx changed");
		songLibrary.clear();
		songLibrary.addAll(mpd.getSongsInLibrary());
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				final ListView songLibraryView = (ListView) findViewById(R.id.songLibraryView);
				((PlaylistAdapter) songLibraryView.getAdapter())
						.notifyDataSetChanged();
				setEnabled(connected);
			}
		});

	}

}
