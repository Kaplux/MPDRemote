package fr.mildlyusefulsoftware.mpdremote.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bff.javampd.MPD;
import org.bff.javampd.events.PlayerChangeEvent;
import org.bff.javampd.events.PlayerChangeListener;
import org.bff.javampd.events.PlaylistBasicChangeEvent;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDDatabaseException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDPlaylistException;
import org.bff.javampd.monitor.MPDStandAloneMonitor;
import org.bff.javampd.objects.MPDSong;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import fr.mildlyusefulsoftware.mpdremote.bo.CurrentlyPlayingSong;
import fr.mildlyusefulsoftware.mpdremote.bo.Song;
import fr.mildlyusefulsoftware.mpdremote.util.MPDRemoteUtils;

public class MPDService implements
		org.bff.javampd.events.PlaylistBasicChangeListener,
		PlayerChangeListener {

	private static MPDService instance;
	private MPD mpd;
	private List<MPDListener> MPDListeners = new ArrayList<MPDListener>();

	private MPDService(Context context) {
		
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String portString = prefs.getString("mpd_port_preference", "6600");
			if (!NumberUtils.isNumber(portString)) {
				portString = "6600";
			}
			String hostname=prefs.getString("mpd_host_preference", "localhost");
			connect(portString, hostname);
	}

	public void connect(String portString, String hostname) {
		try {
		mpd = connect(hostname,
				Integer.valueOf(portString));
		MPDStandAloneMonitor monitor = new MPDStandAloneMonitor(mpd, 1000);
		monitor.addPlaylistChangeListener(this);
		Thread th = new Thread(monitor);
		th.start();
} catch (UnknownHostException e) {
		Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
} catch (MPDConnectionException e) {
		handleMPDConnectionException(e);
}
	}

	public static MPDService getInstance(Context context) {
		if (instance == null) {
			instance = new MPDService(context);
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
			handleMPDConnectionException(e);
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
				handleMPDConnectionException(e);
			}
		}
	}

	public void addSongToPlayList(Song s) {
		if (s != null) {
			MPDSong mpdSong = new MPDSong();
			mpdSong.setId(s.getId());
			mpdSong.setFile(s.getFilename());
			try {
				mpd.getMPDPlaylist().addSong(mpdSong);
			} catch (MPDPlaylistException e) {
				Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
			} catch (MPDConnectionException e) {
				handleMPDConnectionException(e);
			}
		}

	}

	public void removeSongFromPlaylist(Song s) {
		if (s != null) {
			MPDSong mpdSong = new MPDSong();
			mpdSong.setId(s.getId());
			try {
				mpd.getMPDPlaylist().removeSong(mpdSong);
			} catch (MPDPlaylistException e) {
				Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
			} catch (MPDConnectionException e) {
				handleMPDConnectionException(e);
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
			handleMPDConnectionException(e);
		}
		return songs;
	}

	private void handleMPDConnectionException(MPDConnectionException e) {
		Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
	}

	@Override
	public void playerChanged(PlayerChangeEvent event) {
		try {
			CurrentlyPlayingSong currentlyPlayingSong = new CurrentlyPlayingSong();
			currentlyPlayingSong.setElapsedTime(mpd.getMPDPlayer()
					.getElapsedTime());
			currentlyPlayingSong.setSong((Song) new MPDSongToSongTransformer()
					.transform(mpd.getMPDPlayer().getCurrentSong()));

			for (MPDListener p : MPDListeners) {
				p.currentlyPlayingSongChanged(currentlyPlayingSong);
			}
		} catch (MPDPlayerException e) {
			Log.e(MPDRemoteUtils.TAG, e.getMessage(), e);
		} catch (MPDConnectionException e) {
			handleMPDConnectionException(e);
		}
	}

}

class MPDSongToSongTransformer implements Transformer {

	@Override
	public Object transform(Object mpdSong) {
		Song s = new Song();
		s.setTitle(((MPDSong) mpdSong).getTitle());
		if (StringUtils.isBlank(s.getTitle())) {
			s.setTitle(((MPDSong) mpdSong).getFile());
		}
		s.setId(((MPDSong) mpdSong).getId());
		s.setFilename(((MPDSong) mpdSong).getFile());
		return s;
	}

}
