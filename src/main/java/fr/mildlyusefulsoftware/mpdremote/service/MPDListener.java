package fr.mildlyusefulsoftware.mpdremote.service;

import java.util.List;

import fr.mildlyusefulsoftware.mpdremote.bo.CurrentlyPlayingSong;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;

public interface MPDListener {
	
	void playListChanged(List<Song> playList);
	void currentlyPlayingSongChanged(CurrentlyPlayingSong currentlyPlayingSong);
	void connectionChanged(boolean connected);
}
