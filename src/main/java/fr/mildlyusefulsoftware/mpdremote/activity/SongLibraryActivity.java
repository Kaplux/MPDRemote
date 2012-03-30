package fr.mildlyusefulsoftware.mpdremote.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import fr.mildlyusefulsoftware.mpdremote.R;
import fr.mildlyusefulsoftware.mpdremote.service.MPDService;

public class SongLibraryActivity extends Activity {


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_library_layout);
		final ListView songLibraryView = (ListView) findViewById(R.id.songLibraryView);
		songLibraryView.setAdapter(new PlaylistAdapter(this, MPDService.getInstance().getSongsInLibrary()));

	}

}
