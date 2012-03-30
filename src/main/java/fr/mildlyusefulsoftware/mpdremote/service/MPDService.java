package fr.mildlyusefulsoftware.mpdremote.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.bff.javampd.MPD;
import org.bff.javampd.events.PlaylistBasicChangeEvent;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDDatabaseException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDPlaylistException;
import org.bff.javampd.monitor.MPDStandAloneMonitor;
import org.bff.javampd.objects.MPDSong;

import android.util.Log;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.util.MPDRemoteUtils;

public class MPDService implements
		org.bff.javampd.events.PlaylistBasicChangeListener {

	private static MPDService instance;
	private MPD mpd;
	private List<MPDListener> MPDListeners = new ArrayList<MPDListener>();

	private MPDService() {
		try {
			mpd = connect("192.168.1.18", 6600);
			MPDStandAloneMonitor monitor = new MPDStandAloneMonitor(mpd, 1000);
			monitor.addPlaylistChangeListener(this);
			Thread th = new Thread(monitor);
			th.start();
		} catch (UnknownHostException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		} catch (MPDConnectionException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		}
	}

	public static MPDService getInstance() {
		if (instance == null) {
			instance = new MPDService();
		}
		return instance;
	}

	private List<Song> getCurrentPlayList() {
		List<Song> songs = new ArrayList<Song>();
		try {
			List<MPDSong> mpdSongList;
			mpdSongList = mpd.getMPDPlaylist().getSongList();

			CollectionUtils.collect(mpdSongList,
					new MPDSongToSongTransformer(), songs);
		} catch (MPDPlaylistException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		} catch (MPDConnectionException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		}

		return songs;
	}

	public void playSong(Song s) {
		if (s != null) {
			MPDSong mpdSong = new MPDSong();
			mpdSong.setId(s.getId());
			try {
				mpd.getMPDPlayer().playId(mpdSong);
			} catch (MPDPlayerException e) {
				Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
			} catch (MPDConnectionException e) {
				Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
			}
		}
	}

	private MPD connect(String ip, int port) throws UnknownHostException,
			MPDConnectionException {
		return new MPD(ip, port);
	}

	public void addPlaylistChangeListener(MPDListener p) {
		MPDListeners.add(p);
	}

	@Override
	public void playlistBasicChange(PlaylistBasicChangeEvent event) {
		List<Song> currentPlaylist = getCurrentPlayList();
		for (MPDListener p : MPDListeners) {
			p.playListChanged(currentPlaylist);
		}

	}

	public List<Song> getSongsInLibrary() {
		List<Song> songs = new ArrayList<Song>();
		try {
			Collection<MPDSong> mpdSongs = mpd.getMPDDatabase().listAllSongs();

			CollectionUtils.collect(mpdSongs, new MPDSongToSongTransformer(),
					songs);
		} catch (MPDDatabaseException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		} catch (MPDConnectionException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		}
		return songs;
	}

}

class MPDSongToSongTransformer implements Transformer {

	@Override
	public Object transform(Object mpdSong) {
		Song s = new Song();
		s.setTitle(((MPDSong) mpdSong).getTitle());
		s.setId(((MPDSong) mpdSong).getId());
		return s;
	}

}
