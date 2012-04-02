package fr.mildlyusefulsoftware.mpdremote.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;

public class SongLibraryActivity extends Activity {

	private MPDService mpd;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_library_layout);
		final ListView songLibraryView = (ListView) findViewById(R.id.songLibraryView);
		registerForContextMenu(songLibraryView);
		songLibraryView.setAdapter(new PlaylistAdapter(this,mpd.getSongsInLibrary()));

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

}
