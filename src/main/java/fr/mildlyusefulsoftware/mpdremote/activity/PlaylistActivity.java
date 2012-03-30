package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;
import fr.mildlyusefulsoftware.mpdremote.service.MPDListener;

public class PlaylistActivity extends Activity implements
		MPDListener {

	private Song selectedSong = null;
	final List<Song> songsInPlaylist = new ArrayList<Song>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_layout);
		MPDService.getInstance().addPlaylistChangeListener(this);
		final ListView playlistView = (ListView) findViewById(R.id.playlistView);

		playlistView.setAdapter(new PlaylistAdapter(this, songsInPlaylist));
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
					MPDService.getInstance().playSong(selectedSong);
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
				((PlaylistAdapter) playlistView.getAdapter()).notifyDataSetChanged();
	
			}
		});
	}

}
