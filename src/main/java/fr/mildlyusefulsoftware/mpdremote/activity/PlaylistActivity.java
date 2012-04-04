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

	private Song selectedSong = null;
	private final List<Song> songsInPlaylist = new ArrayList<Song>();
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.playlist_layout);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
		mpd.addPlaylistChangeListener(this);
		final ListView playlistView = (ListView) findViewById(R.id.playlistView);
		registerForContextMenu(playlistView);
		playlistView.setAdapter(new PlaylistAdapter(this, songsInPlaylist));
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
				if (selectedSong != null) {
					mpd.playOrPauseSong(selectedSong);
				}
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
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// mpd.seek(progress);

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
		final SeekBar sb = (SeekBar) findViewById(R.id.playlistSeekbar);
		sb.setMax(currentlyPlayingSong.getSong().getLength());
		sb.setProgress((int) currentlyPlayingSong.getElapsedTime());

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

}
