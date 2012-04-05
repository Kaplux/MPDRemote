package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.CurrentlyPlayingSong;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDListener;

public class PlaylistActivity extends AbstractMPDActivity implements
		MPDListener {

	private static final String SELECTED_SONG = "SELECTED_SONG";
	private static final String PLAYLIST = "PLAYLIST";
	private Song selectedSong = null;
	boolean currentlySeeking;
	PlaylistAdapter playlistAdapter;
	private final ArrayList<Song> songsInPlaylist = new ArrayList<Song>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.playlist_layout);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
		setEnabled(false);
		final ListView playlistView = (ListView) findViewById(R.id.playlistView);
		registerForContextMenu(playlistView);
		playlistAdapter = new PlaylistAdapter(this, songsInPlaylist,
				R.layout.playlist_item_layout);
		playlistView.setAdapter(playlistAdapter);
		if (savedInstanceState != null) {
			List<Song> playListContent = savedInstanceState
					.getParcelableArrayList(PLAYLIST);
			songsInPlaylist.clear();
			songsInPlaylist.addAll(playListContent);
			Song savedSelectedSong = savedInstanceState
					.getParcelable(SELECTED_SONG);
			if (savedSelectedSong != null) {
				selectSong(savedSelectedSong);
			}
		}

		mpd.addMPDListener(this);
		setEnabled(mpd.isConnected());

		playlistView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				selectedSong = (Song) (playlistView
						.getItemAtPosition(myItemInt));
			}
		});

		Button playButton = (Button) findViewById(R.id.playButton);
		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mpd.playOrPauseSong(selectedSong);
				selectedSong = null;
			}
		});

		Button previousButton = (Button) findViewById(R.id.previousSongButton);
		previousButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mpd.playPreviousSong();
			}
		});

		Button nextButton = (Button) findViewById(R.id.nextSongButton);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mpd.playNextSong();
			}
		});

		final SeekBar sb = (SeekBar) findViewById(R.id.playlistSeekbar);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				currentlySeeking = false;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				currentlySeeking = true;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (currentlySeeking) {
					mpd.seek(progress);
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
			final CurrentlyPlayingSong currentlyPlayingSong) {
		if (!currentlySeeking) {
			final SeekBar sb = (SeekBar) findViewById(R.id.playlistSeekbar);
			sb.setMax(currentlyPlayingSong.getSong().getLength());
			sb.setProgress((int) currentlyPlayingSong.getElapsedTime());
		}
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
			mpd.removeSongFromPlaylist(selectedSong);
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
	public void connectionChanged(boolean connected) {
		setEnabled(connected);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelableArrayList(PLAYLIST, songsInPlaylist);
		savedInstanceState.putParcelable(SELECTED_SONG, selectedSong);
		super.onSaveInstanceState(savedInstanceState);
	}

	private void selectSong(Song s) {
		selectedSong = s;
		final ListView playlistView = (ListView) findViewById(R.id.playlistView);
		playlistView.setItemChecked(playlistAdapter.getPosition(selectedSong),
				true);
	}

}
