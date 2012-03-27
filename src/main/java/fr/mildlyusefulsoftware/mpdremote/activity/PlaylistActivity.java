package fr.mildlyusefulsoftware.mpdremote.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;

public class PlaylistActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_layout);
		ListView playlistView = (ListView) findViewById(R.id.playlistView);
		final List<Song> songsInPlaylist = MPDService.getInstance().getCurrentPlayList();
		playlistView.setAdapter(new PlaylistAdapter(this,songsInPlaylist));
	}

}
