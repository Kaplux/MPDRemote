package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.CurrentlyPlayingSong;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDListener;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;
import fr.mildlyusefulsoftware.mpdremote.util.MPDRemoteUtils;

public class SongLibraryActivity extends AbstractMPDActivity implements
		MPDListener {

	private MPDService mpd;
	private String searchFilter = "";
	private final List<Song> songLibrary = new ArrayList<Song>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mpd = MPDService.getInstance(this);
		mpd.addMPDListener(this);
		setContentView(R.layout.song_library_layout);
		final ListView songLibraryView = (ListView) findViewById(R.id.songLibraryView);
		registerForContextMenu(songLibraryView);
		final PlaylistAdapter songLibraryAdapter = new PlaylistAdapter(this,
				songLibrary, R.layout.song_library_item_layout);
		songLibraryView.setAdapter(songLibraryAdapter);

	
		
		final ImageButton setFilterButton = (ImageButton) findViewById(R.id.song_library_search);
		setFilterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final TextView filterTextView = (TextView) findViewById(R.id.song_library_search_filter_text);
				searchFilter = filterTextView.getText().toString();
				executeSongSearch();
			}
		});
		
		

		final ImageButton addToPlaylistButton = (ImageButton) findViewById(R.id.song_library_add);
		addToPlaylistButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Song> songsToAdd = new ArrayList<Song>();
				Log.d(MPDRemoteUtils.TAG,"nb song in lib : "+songLibraryView.getChildCount());
				for (int i = 0; i < songLibraryView.getCount(); i++) {
					if (songLibraryView.getCheckedItemPositions().get(i)) {
						songsToAdd.add(songLibraryAdapter.getItem(i));
					}
				}
				mpd.addSongToPlayList(songsToAdd);
				resetCheckbox();
			}
		});

		if (mpd.isConnected()) {
			connectionChanged(true);
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
		executeSongSearch();
		setEnabled(connected);
	}
	
	private void resetCheckbox(){
		final ListView songLibraryView = (ListView) findViewById(R.id.songLibraryView);
		for (int i = 0; i < songLibraryView.getCount(); i++) {
			songLibraryView.getCheckedItemPositions().delete(i);
		}
		((PlaylistAdapter) songLibraryView.getAdapter())
		.notifyDataSetChanged();
	}

	private void executeSongSearch() {
		final Activity currentActivity = this;
		final ProgressDialog progressDialog = ProgressDialog.show(this,
				"Searching", "please wait", true);
		new AsyncTask<Void, Void, List<Song>>() {

			@Override
			protected List<Song> doInBackground(Void... params) {
				return mpd.getSongsInLibrary(searchFilter);
			}

			@Override
			protected void onPostExecute(List<Song> result) {
				songLibrary.clear();
				songLibrary.addAll(result);
				final ListView songLibraryView = (ListView) findViewById(R.id.songLibraryView);
				resetCheckbox();
				progressDialog.cancel();
			}
		}.execute();
	}

}
